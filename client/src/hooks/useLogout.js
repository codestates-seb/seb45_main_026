import { useDispatch } from "react-redux"
import { setChannelInfo, setIsLogin, setLoginInfo, setMyid, setProvider, setToken } from "../redux/createSlice/LoginInfoSlice";

export const useLogout = () => {
    const dispatch = useDispatch();

    const logout = () => {
        dispatch(setMyid(''));
        dispatch(setLoginInfo({
            email: "",
            nickname: "",
            grade: "",
            imgUrl: "",
            reward: "" 
        }));
        dispatch(setChannelInfo({
            channelName: "",
            description: "",
          }));
        dispatch(setProvider(''));
        dispatch(setToken({
            authorization: "",
            refresh: "",
          }));
        dispatch(setIsLogin(false));
    }
    return logout;
}