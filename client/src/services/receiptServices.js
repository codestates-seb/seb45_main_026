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