import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { RootState } from "../../redux/Store";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import { getChatComments, patchChatEnd } from "../../services/CustomerService";
import { setRoomId } from "../../redux/createSlice/customerInfoSlice";
import React, { useEffect, useRef, useState } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useToken } from "../../hooks/useToken";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import Loading from "../../components/loading/Loading";
import styled from "styled-components";
import { queryClient } from "../..";
import { PageTitle } from "../../styles/PageTitle";
import tokens from "../../styles/tokens.json";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import axios from "axios";
import { errorResponseDataType } from "../../types/axiosErrorType";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { ReactComponent as SendArrow } from "../../assets/images/icons/Send.svg";
import { DarkMode } from "../../types/reportDataType";

type ChatContents = {
  message: string;
  roomId: string;
  sendDate: string;
  sender: string;
};

const ChatRoomPage: React.FC = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const refreshToken = useToken();
  const stompClient = useRef<any>(null);
  const scrollRef = useRef<HTMLUListElement>(null);
  const messageEndRef = useRef<HTMLDivElement>(null);
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const roomId = useSelector((state: RootState) => state.customerInfo.roomId);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [isPage, setPage] = useState<number>(1);
  const [isMsg, setMsg] = useState<string>("");

  // token
  const headers = {
    Authorization: accessToken.authorization,
  };

  // 채팅방 대화 내용 조회
  const {
    data: ChatContents,
    isLoading,
    isFetching,
    error,
    isPreviousData,
  } = useQuery({
    queryKey: ["ChatComments"],
    queryFn: async () => {
      try {
        const response = await getChatComments(
          accessToken.authorization,
          roomId,
          isPage
        );
        console.log(response);
        return response;
      } catch (err) {
        if (axios.isAxiosError<errorResponseDataType, any>(err)) {
          if (err.response?.data.message === "만료된 토큰입니다.") {
            refreshToken();
          }
        } else {
          console.log(err);
        }
      }
    },
    keepPreviousData: true,
    staleTime: 1000 * 60 * 5,
    cacheTime: 1000 * 60 * 30,
    retry: 3,
    retryDelay: 1000,
  });

  // STOMP 연결 설정
  const stompConnect = () => {
    const sock = new SockJS(`https://api.itprometheus.net/ws`);
    stompClient.current = Stomp.over(sock);
    stompClient.current.connect(
      headers,
      () => {
        console.log("WebSocket 연결이 열렸습니다.");
        mutate();
        stompClient.current.subscribe(
          `/sub/chat/room/${roomId}`,
          (data: any) => {
            const newMessage = JSON.parse(data.body);
            // console.log(newMessage);
          },
          headers
        );
      },
      (err: any) => {
        console.log("WebSocket 연결에 실패했습니다.", err);
      }
    );
  };

  // STOMP 연결 해제
  const stompDisConnect = () => {
    try {
      stompClient.current.disconnect(() => {
        stompClient.current.unsubscribe(`/sub/chat/room/${roomId}`);
      });
      console.log("WebSocket 연결이 끊겼습니다. (stomp)");
      mutate();
    } catch (err) {
      console.log("disconnect", err);
    }
  };

  // message 보내기
  const handleSendMessage = async () => {
    if (isMsg === "") return;
    if (messageEndRef.current) {
      messageEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
    const newMessage = {
      roomId: roomId,
      message: isMsg,
    };
    stompClient.current.send(
      "/pub/message",
      headers,
      JSON.stringify(newMessage)
    );
    mutate();
    setMsg("");
  };

  // Enter 눌렀을 때, 메시지 입력
  const handleKeyEnter = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSendMessage();
    }
  };

  // message 보냈을 때 동기화
  const { mutate } = useMutation({
    mutationFn: async () => {},
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ChatComments"] });
    },
  });

  // 뒤로가기 버튼
  const handleBack = () => {
    navigate(`/chats`);
    dispatch(setRoomId(""));
  };

  useEffect(() => {
    if (roomId) {
      // 랜더링 됐을 때, 자동으로 연결하기
      stompConnect();

      return () => {
        // 컴포넌트 언마운트 시 실행될 클린업 로직
        stompDisConnect();
      };
    }
  }, []);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, []);

  return (
    <PageContainer isDark={isDark}>
      <MainContainer isDark={isDark}>
        <PageTitle isDark={isDark}>고객센터</PageTitle>

        {isLoading ? (
          <Loading />
        ) : (
          <ChatContainer isDark={isDark}>
            <ChatHeader isDark={isDark}>
              <QuestionEndBtn
                isDark={isDark}
                onClick={() => {
                  patchChatEnd(accessToken.authorization, roomId);
                }}
              >
                상담 종료하기
              </QuestionEndBtn>
              <RoomID isDark={isDark}>ID : {roomId}</RoomID>
              <BackBtn isDark={isDark} onClick={handleBack}>
                &times;
              </BackBtn>
            </ChatHeader>

            <MsgLists isDark={isDark} ref={scrollRef}>
              {ChatContents.data?.map((el: ChatContents) => {
                const sendDate: string = el.sendDate.split(".")[0];
                const sendTimes: string[] = sendDate.split("T")[1].split(":");
                const sendTime =
                  parseInt(sendTimes[0]) >= 12
                    ? `오후 ${parseInt(sendTimes[0]) - 12}:${parseInt(
                        sendTimes[1]
                      )}`
                    : `오전 ${parseInt(sendTimes[0])}:${parseInt(
                        sendTimes[1]
                      )}`;
                if (el.sender === el.roomId) {
                  return (
                    <LeftMsgList>
                      <MsgContent isDark={isDark}>{el.message}</MsgContent>
                      <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                    </LeftMsgList>
                  );
                } else {
                  return (
                    <RightMsgList>
                      <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                      <MsgContent isDark={isDark}>{el.message}</MsgContent>
                    </RightMsgList>
                  );
                }
              })}
              <div ref={messageEndRef}></div>
            </MsgLists>
            <ChatFooter>
              <ChatInputBox isDark={isDark}>
                <ChatInput
                  isDark={isDark}
                  value={isMsg}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    setMsg(e.target.value)
                  }
                  onKeyUp={(e: React.KeyboardEvent<HTMLInputElement>) =>
                    handleKeyEnter(e)
                  }
                />
                <ChatSendBtn isDark={isDark} onClick={handleSendMessage} />
              </ChatInputBox>
            </ChatFooter>
          </ChatContainer>
        )}
      </MainContainer>
    </PageContainer>
  );
};

export default ChatRoomPage;

const globalTokens = tokens.global;

export const ChatContainer = styled.div<DarkMode>`
  width: 100%;
  max-width: 800px;
  margin: 20px 0px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.LightRed.value};
  border-radius: 8px;
`;

export const ChatHeader = styled.div<DarkMode>`
  position: relative;
  width: 100%;
  padding: 10px 10px;
  border-radius: 8px 8px 0px 0px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.LightRed.value};
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.White.value};
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const RoomID = styled(BodyTextTypo)`
  padding: 5px 10px;
  font-weight: ${globalTokens.Bold.value};
`;

export const BackBtn = styled(BodyTextTypo)`
  position: absolute;
  right: 2%;
  padding: 5px 10px;
  font-weight: ${globalTokens.Bold.value};
  cursor: pointer;
`;

export const QuestionEndBtn = styled(RegularButton)`
  position: absolute;
  left: 2%;
  border: ${globalTokens.ThinHeight.value}px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.Background.value};
  background-color: ${(props) =>
    props.isDark ? globalTokens.MainNavy.value : globalTokens.Background.value};
  font-weight: 600;
  font-size: 14px;
`;

export const MsgLists = styled.ul<DarkMode>`
  width: 100%;
  height: 60vh;
  padding: 15px 10px;
  overflow-y: scroll;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.LightRed.value};
  display: flex;
  flex-direction: column;

  &::-webkit-scrollbar {
    width: 8px; /* 스크롤바의 너비 */
  }

  &::-webkit-scrollbar-thumb {
    /* 스크롤바의 길이 */
    height: 30%;
    /* 스크롤바의 색상 */
    background: ${(props) =>
      props.isDark
        ? globalTokens.LightNavy.value
        : globalTokens.Background.value};
    border-radius: 10px;
  }

  &::-webkit-scrollbar-track {
    /*스크롤바 뒷 배경 색상*/
    background: ${(props) =>
      props.isDark ? globalTokens.Black.value : globalTokens.LightRed.value};
  }
`;

export const MsgList = styled.li`
  width: 100%;
  padding: 10px;
  display: flex;
  align-items: end;
`;

export const LeftMsgList = styled(MsgList)`
  justify-content: start;
`;

export const RightMsgList = styled(MsgList)`
  justify-content: end;
`;

export const MsgContent = styled(BodyTextTypo)`
  padding: 8px 18px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-radius: 8px;
  font-weight: ${globalTokens.Bold.value};
`;

export const MsgDate = styled.div<DarkMode>`
  margin: 0px 10px;
  padding-bottom: 2px;
  font-size: 12px;
  font-weight: 600;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.White.value};
`;

export const ChatFooter = styled.div`
  width: 100%;
  padding: 10px 20px;
`;

export const ChatInputBox = styled.div<DarkMode>`
  width: 100%;
  height: 45px;
  margin: 5px 0px 10px 0px;
  padding: 5px 15px;
  border-radius: 18px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  background-color: ${(props) =>
    props.isDark
      ? globalTokens.LightNavy.value
      : globalTokens.Background.value};
`;

export const ChatInput = styled.input<DarkMode>`
  width: 100%;
  margin-right: 10px;
  padding: 0px 5px;
  background: none;
  border: none;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`;

export const ChatSendBtn = styled(SendArrow)<DarkMode>`
  width: 20px;
  height: 20px;
  cursor: pointer;
  path {
    fill: ${(props) =>
      props.isDark ? globalTokens.White.value : globalTokens.Gray.value};
  }
`;
