//결제 내역 조회
import axios from 'axios';
import { ROOT_URL } from '.';

export const getReceiptService = async (
    authorization, page, size, month
) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/members/orders?page=${page}&size=${size}&month=${month}`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status:'success',
            data: response.data
        }
    } catch(err) {
        return {
            status: 'error',
            data: err.response.data.message,
        }
    }
}

//결제 단건 취소
export const cancelOnePurchaseService = async (authorization, orderId, videoId) => {
    try {
        const response = await axios.delete(
            `${ROOT_URL}/orders/${orderId}/videos/${videoId}`,
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
            data: err.response.data.message
        }
    }
}
//결제 전체 취소
export const cancelWholePurchaseService = async (authorization, orderId) => {
    try {
        const response = await axios.delete(
            `${ROOT_URL}/orders/${orderId}`,
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