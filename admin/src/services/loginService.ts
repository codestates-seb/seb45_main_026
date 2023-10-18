import axios from "axios";
import { ROOT_URL } from ".";

//로그인 요청
export const loginService = async (email: string, password: string) => {
  try {
    const response = await axios.post(`${ROOT_URL}/auth/login`, {
      email: email,
      password: password,
    });
    return {
      status: "success",
      authorization: response.headers.authorization,
      refresh: response.headers.refresh,
    };
  } catch (err) {
    return {
      status: "error",
      data: err,
    };
  }
};

//유저 정보 조회
export const getUserInfoService = async (authorization: string) => {
  try {
    const response = await axios.get(`${ROOT_URL}/members`, {
      headers: {
        Authorization: authorization,
      },
    });
    return {
      status: "success",
      data: response.data,
    };
  } catch (err) {
    return {
      status: "error",
      data: err,
    };
  }
};

//토큰 재발급
export const getNewAuthorizationService = async (refresh: string) => {
  try {
    const response = await axios.post(
      `${ROOT_URL}/auth/refresh`,
      {},
      {
        headers: {
          Refresh: refresh,
        },
      }
    );
    return {
      status: "success",
      data: response.headers.authorization,
    };
  } catch (err) {
    return {
      status: "error",
      data: err,
    };
  }
};
