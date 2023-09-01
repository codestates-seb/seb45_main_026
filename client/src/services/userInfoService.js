import axios from 'axios';
import { ROOT_URL } from '.';

//프로필 조회
export const getUserInfoService = async (authorization) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/members`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );

        return {
            status: 'success',
            data: response.data.data
        }
    } catch (err) {
        return {
            status: 'error',
            ...err,
        }
    } 
}