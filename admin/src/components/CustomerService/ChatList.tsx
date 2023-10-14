import styled from "styled-components";
import { DarkMode } from "../../types/reportDataType";
import tokens from "../../styles/tokens.json";
import { getChatComments } from "../../services/CustomerService";
import { useToken } from "../../hooks/useToken";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { errorResponseDataType } from "../../types/axiosErrorType";
import axios from "axios";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import Loading from "../loading/Loading";

type ChatContents = {
  message: string;
  roomId: string;
  sendDate: string;
  sender: string;
};

interface OwnProps {
  isArrive: any;
  scrollRef: any;
}

const ChatList: React.FC<OwnProps> = ({ isArrive, scrollRef }) => {
  const refreshToken = useToken();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const roomId = useSelector((state: RootState) => state.customerInfo.roomId);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [isPage, setPage] = useState<number>(1);
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
          {ChatContents.data?.map((el: ChatContents, idx: number) => {
            const sendDate: string = el.sendDate.split(".")[0];
            const sendTimes: string[] = sendDate.split("T")[1].split(":");
            const sendTime =
              parseInt(sendTimes[0]) >= 12
                ? `오후 ${parseInt(sendTimes[0]) - 12}:${parseInt(
                    sendTimes[1]
                  )}`
                : `오전 ${parseInt(sendTimes[0])}:${parseInt(sendTimes[1])}`;
            if (el.sender === el.roomId) {
              return (
                <LeftMsgList key={idx}>
                  <MsgContent isDark={isDark}>{el.message}</MsgContent>
                  <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                </LeftMsgList>
              );
            } else {
              return (
                <RightMsgList key={idx}>
                  <MsgDate isDark={isDark}>{sendTime}</MsgDate>
                  <MsgContent isDark={isDark}>{el.message}</MsgContent>
                </RightMsgList>
              );
            }
          })}
        </>
      )}
    </>
  );
};

export default ChatList;

const globalTokens = tokens.global;

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
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
