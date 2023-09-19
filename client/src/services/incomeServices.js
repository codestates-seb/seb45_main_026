import axios from "axios";
import { ROOT_URL } from ".";

//정산 내역 조회
export const getIncomeService = async (
    { authorization, page, size, month, year, sort }
) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/orders/adjustment?page=${page}&size=${size}&month=${month}&year=${year}&sort=${sort}`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data,
        };
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        };
    }
}