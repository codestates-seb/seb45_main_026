import axios from "axios";
import { ROOT_URL } from ".";

export const getVideoList = async (
  authorization: string,
  email: string,
  keyword: string,
  page: number,
  size: number
) => {
  const response = await axios.get(
    `${ROOT_URL}/admin/videos?email=${email}&keyword=${keyword}&page=${page}&size=${size}`,
    {
      headers: {
        Authorization: authorization,
      },
    }
  );
  return response.data;
};

// 미할당 채팅방 조회
export const getChatRoomList = async (authorization: string) => {
  const response = await axios.get(`${ROOT_URL}/admin/chats`, {
    headers: {
      Authorization: authorization,
    },
  });
  return response.data;
};

// 내가 속해있는 채팅방 조회
export const getMyRoomList = async (authorization: string) => {
  const response = await axios.get(`${ROOT_URL}/admin/chats/my-rooms`, {
    headers: {
      Authorization: authorization,
    },
  });
  return response.data;
};

// 채팅방 대화 조회
export const getChatComments = async (
  authorization: string,
  roomId: string,
  page: number
) => {
  const response = await axios.get(
    `${ROOT_URL}/admin/chats/${roomId}?page=${page}`,
    {
      headers: {
        Authorization: authorization,
      },
    }
  );
  return response.data;
};

// 상담 처리 완료 (채팅방 닫기)
export const patchChatEnd = async (
  authorization: string,
  roomId: string
) => {
  const response = await axios.patch(
    `${ROOT_URL}/admin/chats/${roomId}`,
    null,
    {
      headers: {
        Authorization: authorization,
      },
    }
  );
  return response;
};
