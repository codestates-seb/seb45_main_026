import { useToken } from "../../hooks/useToken";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useQuery } from "@tanstack/react-query";
import { getChatRoomList } from "../../services/CustomerService";
import Loading from "../loading/Loading";
import AllChatItems from "./AllChatItems";
import {
  ChatBlockth,
  CustomerBody,
  CustomerEmailth,
  CustomerHead,
  CustomerIdth,
  CustomerNameth,
  CustomerTr,
  Customertable,
  InquireDateth,
  ChatListEmpty,
} from "../../pages/chats/CustomerServicePage";
import axios from "axios";
import { errorResponseDataType } from "../../types/axiosErrorType";

export type AllChatRoom = {
  roomId: string;
  memberId: number;
  nickname: string;
  inquireDate: string | null;
};

const AllChatLists: React.FC = () => {
  const refreshToken = useToken();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );

  const {
    data: allChatRoomList,
    isLoading,
    isFetching,
    isPreviousData,
  } = useQuery({
    queryKey: ["AllChatRoomList"],
    queryFn: async () => {
      try {
        const response = await getChatRoomList(accessToken.authorization);
        return response;
      } catch (err) {
        if (axios.isAxiosError<errorResponseDataType, any>(err)) {
          if (err.response?.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        }
      }
    },
    keepPreviousData: true,
    staleTime: 1000 * 60 * 5,
    cacheTime: 1000 * 60 * 30,
    retry: 3,
    retryDelay: 1000,
  });

  return (
    <>
      {isLoading ? (
        <Loading />
      ) : (
        <>
          {allChatRoomList.data?.length > 0 ? (
            <Customertable>
              <CustomerHead>
                <CustomerTr isDark={isDark}>
                  <CustomerIdth isDark={isDark}>ID</CustomerIdth>
                  <CustomerNameth isDark={isDark}>문의자</CustomerNameth>
                  <CustomerEmailth isDark={isDark}>이메일</CustomerEmailth>
                  <InquireDateth isDark={isDark}>문의 날짜</InquireDateth>
                  <ChatBlockth isDark={isDark}>비고</ChatBlockth>
                </CustomerTr>
              </CustomerHead>
              <CustomerBody>
                {allChatRoomList.data?.map((el: AllChatRoom) => (
                  <AllChatItems key={el.memberId} item={el} />
                ))}
              </CustomerBody>
            </Customertable>
          ) : (
            <ChatListEmpty isDark={isDark}>
              현재 대기중인 채팅방이 없습니다.
            </ChatListEmpty>
          )}
        </>
      )}
    </>
  );
};

export default AllChatLists;
