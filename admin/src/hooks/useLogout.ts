import { useDispatch } from "react-redux"
import { setAccessToken, setIsLogin, setLoginInfo, setMyId } from "../redux/createSlice/loginInfoSlice";
import { useNavigate } from "react-router-dom";

export const useLogout = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const logout = () => {
        dispatch(setMyId(''));
        dispatch(setLoginInfo({
            email: "",
            nickname: "", 
            grade: "", 
            imgUrl: "", 
            reward: "",
            authority: "",
        }));
        dispatch(setAccessToken({
            authorization: '',
            refresh: '',
        }));
        dispatch(setIsLogin(false));
        navigate('/login')
    }

    return logout;
}