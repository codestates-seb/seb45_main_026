import axios from 'axios';

const ROOT_URL = 'https://api.itprometheus.net';

//회원가입 API
export const signupService = async ({data}) => {
    try {
        console.log(data)
        const response = await axios.post(
            `${ROOT_URL}/auth/signup`,
            {
                email: data.email,
                password: data.password,
                nickname: data.nickname
            }
        );
        return response.status;
    } catch (err) {
        console.log(err.message);
        return err.message;
    }
}

//email 인증코드 발송, 성공 시 204 return, 실패 시 err return
export const emailValidationService = async (email) => {
    console.log(`email : ${email}`)
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/signup/email`,
            {
                email: email
            }
        )
        return response.status;
    } catch (err) {
        console.log(err.response.data.message);
        return err.response.data.message;
    }
}

//email 인증코드 확인, 성공 시 204 return, 실패 시 err return 
export const emailValidationConfirmService = async (email, emailCode) => {
    try {
        console.log(`${email}, ${emailCode}`)
        const response = await axios.post(
            `${ROOT_URL}/auth/signup/confirm`,
            {
                email:email,
                code:emailCode,
            }
        );
        return response.status;
    } catch(err) {
        console.log(err.response.data.message);
        return err.response.data.message;
    }
}

//일반 로그인
export const loginService = async (data) => {
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/login`,
            {
                email: data.email,
                password: data.password
            }
        );
        return {
            isLogin: true,
            authorization: response.headers.authorization,
            refresh: response.headers.refresh,
        };
    } catch (err) {
        return {
            isLogin: false,
        };
    }
}

//OAuth 로그인
export const oauthLoginService = async (authorizationCode) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/auth/oauth?provider=GOOGLE&code=${authorizationCode}`,
        );
        console.log(response);
    } catch (err) {
        console.log(err.response.data.message);
        return err.response.data.message;
    }
}