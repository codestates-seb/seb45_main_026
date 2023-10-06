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

// 특정 댓글 신고 내역 조회
export const getReviewReportList = async (
  authorization: string,
  replyId: number,
  page: number,
  size: number
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/replies/${replyId}?page=${page}&size=${size}`,
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

// 신고 내역들(채널) 조회
export const getReportChannelList = async (
  authorization: string,
  page: number,
  size: number,
  sort: string
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/channels?page=${page}&size=${size}&sort=${sort}`,
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

// 특정 채널 신고 내역 조회
export const getChannelReportList = async (
  authorization: string,
  memberId: number,
  page: number,
  size: number
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/channels/${memberId}?page=${page}&size=${size}`,
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

// 신고 내역들(공지사항) 조회
export const getReportNoticeList = async (
  authorization: string,
  page: number,
  size: number,
  sort: string
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/announcements?page=${page}&size=${size}&sort=${sort}`,
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

// 특정 공지사항 신고 내역 조회
export const getNoticeReportList = async (
  authorization: string,
  announcementId: number,
  page: number,
  size: number
) => {
  try {
    const response = await axios.get(
      `${ROOT_URL}/reports/announcements/${announcementId}?page=${page}&size=${size}`,
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
