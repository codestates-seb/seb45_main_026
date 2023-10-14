import { useQuery } from "@tanstack/react-query";
import { getChatComments } from "../../services/helpcenterService";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { useToken } from "../../hooks/useToken";
import axios from "axios";
import Loading from "../../atoms/loading/Loading";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const HelpChatLists = ({ isArrive, scrollRef }) => {
  const refreshToken = useToken();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const [isPage, setPage] = useState(1);

  // 채팅방 대화 내용 조회
  const {
    data: ChatContents,
    isLoading,
    isFetching,
    isError,
    isPreviousData,
  } = useQuery({
    queryKey: ["ChatComments", isArrive],
    queryFn: async () => {
      try {
        const response = await getChatComments(
          accessToken.authorization,
          isPage
        );
        console.log("response", response);
        return response;
      } catch (err) {
        if (axios.isAxiosError(err)) {
          if (err.response?.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        } else {
          console.log(err);
        }
      }
    },
    keepPreviousData: false,
    staleTime: 1000 * 60 * 5,
    cacheTime: 1000 * 60 * 30,
    retry: 3,
    retryDelay: 1000,
  });

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [ChatContents]);

  return (
    <>
      {isLoading || isError ? (
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
                <RightMsgList key={idx}>
                  <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                  <MsgContent isDark={isDark}>{el.message}</MsgContent>
                </RightMsgList>
              );
            } else {
              return (
                <LeftMsgList key={idx}>
                  <MsgContent isDark={isDark}>{el.message}</MsgContent>
                  <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                </LeftMsgList>
              );
            }
          })}
        </>
      )}
    </>
  );
};

export default HelpChatLists;

export const MsgList = styled.li`
  width: 100%;
  padding: 10px;
  border-radius: 8px;
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
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
