import axios from 'axios';

const ROOT_URL = 'https://api.itprometheus.net';

//회원가입 API
export const signupService = async () => {

}

//일반 로그인
export const loginService = async () => {

}

//OAuth 로그인
export const oauthLoginService = async (authorizationCode) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/auth/oauth?provider=GOOGLE&code=${authorizationCode}`,
        );
        console.log(response);
    } catch (err) {
        console.log(err);
    }
}