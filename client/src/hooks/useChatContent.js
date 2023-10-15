import { useSelector } from "react-redux";
import { useToken } from "./useToken";
import { useState } from "react";
import { getChatComments } from "../services/helpcenterService";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";

export const useChatContent = (isArrive, isChatStart) => {
  const refreshToken = useToken();
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const [isPage, setPage] = useState(1);
  // 채팅방 대화 내용 조회
  const { data, isLoading, isFetching, isError, isPreviousData } = useQuery({
    queryKey: ["ChatComments", isArrive, isChatStart],
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
            return undefined;
          }
        } else {
          return undefined;
        }
      }
    },
    keepPreviousData: false,
    staleTime: 1000 * 60 * 5,
    cacheTime: 1000 * 60 * 30,
    retry: 3,
    retryDelay: 1000,
  });

  return {
    data,
    isLoading,
    isFetching,
    isError,
    isPreviousData,
  };
};
