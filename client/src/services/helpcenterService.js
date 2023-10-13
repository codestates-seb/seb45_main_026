import axios from "axios";
import { ROOT_URL } from ".";

// 채팅방 대화 조회
export const getChatComments = async (authorization, page) => {
  const response = await axios.get(
    `${ROOT_URL}/user/chats/my-rooms?page=${page}`,
    {
      headers: {
        Authorization: authorization,
      },
    }
  );
  return response.data;
};

// 상담방 나가기 (채팅방 삭제)
export const deleteChatEnd = async (authorization) => {
  const response = await axios.delete(`${ROOT_URL}/user/chats`, {
    headers: {
      Authorization: authorization,
    },
  });
  return response;
};
