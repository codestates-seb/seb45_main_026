import axios from "axios";
import { ROOT_URL } from ".";

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
  } catch {
    return console.log("err");
  }
};

export const getVideoReportList = async (
  authorization: string,
  videoId: number,
  page: number,
  size: number,
  sort: string
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
    return console.log(err);
  }
};
