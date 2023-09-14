import axios from 'axios';
import { ROOT_URL } from '.';

export const getRewardListService = async (authorization, page, size) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/members/rewards?page=${page}&size=${size}`,
            {
                headers: {
                    Authorization: authorization
                }
            });
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