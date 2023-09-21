import axios from "axios"
import { ROOT_URL } from "."

//신고 목록 내역 조회
export const getReportService = async (authorization,page,category) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/videos/reports?page=${page}&size=10&sort=${category}`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data,
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        }
    }
}

//신고된 비디오의 신고 기록 목록 조회
export const getReportContentService = async (authorization,page,videoId) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/videos/${videoId}/reports?page=${page}&size=10`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        }
    }
}

//비디오 폐쇄 / 해제
export const patchVideoStatus = async (authorization, videoId) => {
    try {
        const response = await axios.patch(
            `${ROOT_URL}/videos/${videoId}/status`,
            {},
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        }
    }
}

//비디오 상세 정보 조회
export const getVideoInfo = async (authorization, videoId) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/videos/${videoId}`,
            {
                headers: {
                    Authorization : authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data,
        }
     } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        }
     }
}