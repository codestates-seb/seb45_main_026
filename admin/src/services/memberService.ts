import axios from "axios"
import { ROOT_URL } from "."

//회원 리스트 조회
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
    return response.data;
}

//회원 차단, 차단 해제
type updateMemberStatusServicePropsType = {
    authorization : string;
    memberId : number;
    days : number;
    blockReason : string;
}

export const updateMemberStatusService = async ({ 
    authorization,
    memberId,
    days,
    blockReason,
} : updateMemberStatusServicePropsType) => {
    return axios.patch(
        `${ROOT_URL}/reports/members/${memberId}`,
        {
            days: days,
            blockReason: blockReason,
        }, 
        {
            headers: {
                Authorization: authorization,
            }
        }
    );
}