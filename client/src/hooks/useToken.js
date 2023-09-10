import { useDispatch, useSelector } from "react-redux"
import { getNewAuthorizationService } from "../services/authServices";
import { setToken } from "../redux/createSlice/LoginInfoSlice";
import { useLogout } from "./useLogout";

export const useToken = (callback=null) => {
    const dispatch = useDispatch();
    const tokens = useSelector(state=>state.loginInfo.accessToken);
    const logout = useLogout();

    const reIssueToken = () => {
        getNewAuthorizationService(tokens.refresh).then((res)=>{
            if(res.status==='success') {
                dispatch(setToken({
                    ...tokens,
                    authorization: res.data
                }));
                callback && callback(); //token dispatch후 실행 할 메소드
            } else {
                logout();
            }
        })
    }

    return reIssueToken;
}