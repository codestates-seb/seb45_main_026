import axios from "axios"
import { ROOT_URL } from "."

export const getVideoList = async (
    authorization : string, 
    email : string, 
    keyword : string, 
    page : number, 
    size : number ) => {
    const response = await axios.get(
        `${ROOT_URL}/admin/videos?email=${email}&keyword=${keyword}&page=${page}&size=${size}`,
        {
            headers: {
                Authorization: authorization
            }
        }
    );
    return response.data;
}