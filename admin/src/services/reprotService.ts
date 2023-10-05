import axios from "axios";
import { ROOT_URL } from ".";

// 신고 내역들(비디오) 조회
export const getReportVideoList = async (
  authorization: string,
  page: number,
  size: number,
  sort: string
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/videos?page=${page}&size=${size}&sort=${sort}`,
      {
        headers: {
          Authorization: authorization,
        },
      }
    );
    return response.data;
  } catch (err) {
    return err;
  }
};

// 특정 비디오 신고 내역 조회
export const getVideoReportList = async (
  authorization: string,
  videoId: number,
  page: number,
  size: number
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/videos/${videoId}?page=${page}&size=${size}`,
      {
        headers: {
          Authorization: authorization,
        },
      }
    );
    return response.data;
  } catch (err) {
    return err;
  }
};
// 신고 내역들(댓글) 조회
export const getReportReviewList = async (
  authorization: string,
  page: number,
  size: number,
  sort: string
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/replies?page=${page}&size=${size}&sort=${sort}`,
      {
        headers: {
          Authorization: authorization,
        },
      }
    );
    return response.data;
  } catch (err) {
    return err;
  }
};

// 특정 비디오 신고 내역 조회
export const getReviewReportList = async (
  authorization: string,
  videoId: number,
  page: number,
  size: number
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/replies/${videoId}?page=${page}&size=${size}`,
      {
        headers: {
          Authorization: authorization,
        },
      }
    );
    return response.data;
  } catch (err) {
    return err;
  }
};
