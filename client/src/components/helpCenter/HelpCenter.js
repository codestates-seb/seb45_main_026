import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import {
  IconButtonContainer,
  IconButtonImg,
} from "../../atoms/buttons/IconButtons";
import closeWihte from "../../assets/images/icons/close/closeWhite.svg";
import closeBlack from "../../assets/images/icons/close/closeBlack.svg";
import { useSelector } from "react-redux";
import { frameInBottomToTopAnimation } from "../mainPageItems/frameAnimation";
import {
  BodyTextTypo,
  Heading5Typo,
} from "../../atoms/typographys/Typographys";
import { useMutation, useQuery } from "@tanstack/react-query";
import axios from "axios";
import { getChatComments } from "../../services/helpcenterService";
import { useToken } from "../../hooks/useToken";
import { queryClient } from "../..";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import Loading from "../../atoms/loading/Loading";
import { ReactComponent as SendArrow } from "../../assets/images/icons/Send.svg";

const globalTokens = tokens.global;

const HelpCenter = ({ isHelpClick, setIsHelpClick }) => {
  const refreshToken = useToken();
  const stompClient = useRef(null);
  const scrollRef = useRef(null);
  const messageEndRef = useRef(null);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const roomId = useSelector((state) => state.loginInfo.loginInfo.email);
  const [isPage, setPage] = useState(1);
  const [isMsg, setMsg] = useState("");

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
          isPage
        );
        console.log(response);
        return response;
      } catch (err) {
        if (axios.isAxiosError(err)) {
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
          (data) => {
            const newMessage = JSON.parse(data.body);
            // console.log(newMessage);
          },
          headers
        );
      },
      (err) => {
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
  const handleKeyEnter = (e) => {
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
    <HelpCenterContainer
      className={isHelpClick ? "frame-in" : "frame-out"}
      isDark={isDark}
      isHelpClick={isHelpClick}
    >
      <HelpCenterCloseContainer isHelpClick={isHelpClick}>
        <HelpCenterTitle isDark={isDark}>고객센터</HelpCenterTitle>
        <IconButtonContainer
          isHelpClick={isHelpClick}
          onClick={() => {
            setIsHelpClick(false);
          }}
        >
          <IconButtonImg
            isHelpClick={isHelpClick}
            src={isDark ? closeWihte : closeBlack}
          />
        </IconButtonContainer>
      </HelpCenterCloseContainer>

      <MsgLists isDark={isDark} ref={scrollRef}>
        {isLoading ? (
          <Loading />
        ) : (
          <>
            {ChatContents.data?.map((el, idx) => {
              const sendDate = el.sendDate.split(".")[0];
              const sendTimes = sendDate.split("T")[1].split(":");
              const sendTime =
                parseInt(sendTimes[0]) >= 12
                  ? `오후 ${parseInt(sendTimes[0]) - 12}:${parseInt(
                      sendTimes[1]
                    )}`
                  : `오전 ${parseInt(sendTimes[0])}:${parseInt(sendTimes[1])}`;
              if (el.sender === el.roomId) {
                return (
                  <RightMsgList>
                    <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                    <MsgContent isDark={isDark}>{el.message}</MsgContent>
                  </RightMsgList>
                );
              } else {
                return (
                  <LeftMsgList>
                    <MsgContent isDark={isDark}>{el.message}</MsgContent>
                    <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                  </LeftMsgList>
                );
              }
            })}
          </>
        )}
        <div ref={messageEndRef}></div>
      </MsgLists>
      <ChatFooter>
        <ChatInputBox isDark={isDark}>
          <ChatInput
            isDark={isDark}
            value={isMsg}
            onChange={(e) => setMsg(e.target.value)}
            onKeyUp={(e) => handleKeyEnter(e)}
          />
          <ChatSendBtn isDark={isDark} onClick={handleSendMessage} />
        </ChatInputBox>
      </ChatFooter>
    </HelpCenterContainer>
  );
};

export default HelpCenter;

export const HelpCenterContainer = styled.section`
  position: absolute;
  bottom: 0;
  right: 0;
  opacity: ${(props) => (props.isHelpClick ? 1 : 0)};
  visibility: ${(props) => (props.isHelpClick ? "visible" : "hidden")};
  z-index: 100;
  transition: 300ms;
  width: 500px;
  min-height: 100px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
  border-radius: ${globalTokens.BigRadius.value}px;
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  padding: ${globalTokens.Spacing8.value}px;
  &.frame-in {
    animation: ${frameInBottomToTopAnimation} 0.5s;
  }
  &.frame-out {
  }
`;
export const HelpCenterCloseContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: end;
  align-items: center;
  opacity: ${(props) => (props.isHelpClick ? "1" : "0")};
  visibility: ${(props) => (props.isHelpClick ? "visible" : "hidden")};
`;
export const HelpCenterTitle = styled(Heading5Typo)`
  flex-grow: 1;
  margin-left: ${globalTokens.Spacing16.value}px;
`;

export const MsgLists = styled.ul`
  border-radius: ${globalTokens.RegularRadius.value}px;
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  width: 95%;
  max-height: 65vh;
  min-height: 10vh;
  margin: ${globalTokens.Spacing4.value}px 0 ${globalTokens.Spacing12.value}px 0;

  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.LightRed.value};
  overflow-y: scroll;
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
        : globalTokens.Negative.value};
    border-radius: 10px;
  }

  &::-webkit-scrollbar-track {
    /*스크롤바 뒷 배경 색상*/
    background: ${(props) =>
      props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
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

export const MsgDate = styled.div`
  margin: 0px 10px;
  padding-bottom: 2px;
  font-size: 12px;
  font-weight: 600;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.White.value};
`;

export const ChatFooter = styled.div`
  width: 100%;
  padding: 0px 12px 10px 12px;
`;

export const ChatInputBox = styled.div`
  width: 100%;
  height: 45px;
  padding: 5px 15px;
  border-radius: 10px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  background-color: ${(props) =>
    props.isDark ? globalTokens.LightNavy.value : globalTokens.LightRed.value};
`;

export const ChatInput = styled.input`
  width: 100%;
  margin-right: 10px;
  padding: 0px 5px;
  background: none;
  border: none;
  outline: none;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  font-weight: 600;
`;

export const ChatSendBtn = styled(SendArrow)`
  width: 20px;
  height: 20px;
  cursor: pointer;
  path {
    fill: ${(props) =>
      props.isDark ? globalTokens.White.value : globalTokens.Gray.value};
  }
`;
