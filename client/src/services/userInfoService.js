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
        console.log(response);
    } catch (err) {
        console.log(err);
    } 
}