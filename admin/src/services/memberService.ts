import axios from "axios"
import { ROOT_URL } from "."

export const getMemberList = async ( 
    authorization : string,
    keyword : string, 
    page : number,
    size : number,
) => {
    const response = await axios.get(
        `${ROOT_URL}/admin/members?keyword=${keyword}&page=${page}&size=${size}`,
        {
            headers: {
                Authorization: authorization,
            }
        }
    );
    return response;
}