import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { frameInBottomToTopAnimation } from "../mainPageItems/frameAnimation";
import { Heading5Typo } from "../../atoms/typographys/Typographys";
import { useMutation } from "@tanstack/react-query";
import { deleteChatEnd } from "../../services/helpcenterService";
import { queryClient } from "../..";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { ReactComponent as SendArrow } from "../../assets/images/icons/Send.svg";
import { RegularButton } from "../../atoms/buttons/Buttons";
import HelpChatLists from "./HelpChatLists";

const globalTokens = tokens.global;

const HelpCenter = ({ isHelpClick, setIsHelpClick }) => {
  const stompClient = useRef(null);
  const scrollRef = useRef(null);
  const messageEndRef = useRef(null);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const roomId = useSelector((state) => state.loginInfo.loginInfo.email);
  const [isMsg, setMsg] = useState("");
  const [isArrive, setArrive] = useState({});
  const [isChatStart, setChatStart] = useState(false);

  // token
  const headers = {
    Authorization: accessToken.authorization,
  };

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
            setArrive(newMessage);
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
      stompClient.current.disconnect(() => {});
      console.log("WebSocket 연결이 끊겼습니다. (stomp)");
      mutate();
    } catch (err) {
      console.log("disconnect", err);
    }
  };

  // message 보내기
  const handleSendMessage = async () => {
    if (isMsg === "") return;
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
        setIsHelpClick(false);
      };
    }
  }, []);

  return (
    <HelpCenterContainer
      className={isHelpClick ? "frame-in" : "frame-out"}
      isDark={isDark}
      isHelpClick={isHelpClick}
    >
      <HelpCenterCloseContainer isHelpClick={isHelpClick}>
        <HelpCenterEnd
          isDark={isDark}
          onClick={() => {
            deleteChatEnd(accessToken.authorization);
            stompClient.current.unsubscribe(`/sub/chat/room/${roomId}`);
            setIsHelpClick(false);
          }}
        >
          상담 종료
        </HelpCenterEnd>
        <HelpCenterTitle isDark={isDark}>고객센터</HelpCenterTitle>
        <HelpCenterClose
          isDark={isDark}
          isHelpClick={isHelpClick}
          onClick={() => {
            setIsHelpClick(false);
          }}
        >
          &times;
        </HelpCenterClose>
      </HelpCenterCloseContainer>

      <MsgLists isDark={isDark} ref={scrollRef}>
        {!isChatStart ? (
          <ChatEmpty>
            <ChatStart isDark={isDark} onClick={() => setChatStart(true)}>
              문의 시작하기
            </ChatStart>
          </ChatEmpty>
        ) : (
          <HelpChatLists isArrive={isArrive} scrollRef={scrollRef} />
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
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  opacity: ${(props) => (props.isHelpClick ? "1" : "0")};
  visibility: ${(props) => (props.isHelpClick ? "visible" : "hidden")};
`;

export const HelpCenterEnd = styled(RegularButton)`
  position: absolute;
  top: 5px;
  left: 15px;
`;
export const HelpCenterTitle = styled(Heading5Typo)`
  padding: 10px 0px;
`;
export const HelpCenterClose = styled(Heading5Typo)`
  position: absolute;
  top: 5px;
  right: 15px;
  cursor: pointer;
`;

export const MsgLists = styled.ul`
  /* border-radius: ${globalTokens.Spacing16.value}px; */
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  width: 95%;
  height: 65vh;
  margin: ${globalTokens.Spacing4.value}px 0 ${globalTokens.Spacing12.value}px 0;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
  overflow-y: scroll;
  display: flex;
  flex-direction: column;
  padding: 5px;

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

export const ChatEmpty = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const ChatStart = styled(RegularButton)`
  width: 50%;
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
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
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
