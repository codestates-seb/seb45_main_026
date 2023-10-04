import { useDispatch, useSelector } from "react-redux"
import { RootState } from "../redux/Store";
import { useLogout } from "./useLogout";
import { getNewAuthorizationService } from "../services/loginService";
import { setAccessToken } from "../redux/createSlice/loginInfoSlice";

export const useToken = ( callback? : Function ) => {
    const dispatch = useDispatch();
    const tokens = useSelector((state:RootState)=>state.loginInfo.accessToken);
    const logout = useLogout();

    const reIssueToken = () => {
        getNewAuthorizationService(tokens.refresh).then((res)=>{
            if(res.status==='success') {
                dispatch(setAccessToken({
                    ...tokens,
                    authorization: res.data,
                }));
                callback && callback();
            } else { 
                logout(); 
            }
        })
    }

    return reIssueToken;
}