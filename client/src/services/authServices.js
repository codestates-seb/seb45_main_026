import axios from 'axios';
import { ROOT_URL } from '.';

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
        return {
            status: 'success',
            data: response.data,
        };
    } catch (err) {
        return {
            status: 'error',
            data: err.message,
        };
    }
}

//email 인증코드 발송
export const emailValidationService = async (email) => {
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/signup/email`,
            {
                email: email
            }
        )
        return {
            status : 'success',
            data: response.data,
        };
    } catch (err) {
        return {
            status: 'error',
            data : err.response.data.message,
        };
    }
}

//email 인증코드 확인
export const emailValidationConfirmService = async (email, emailCode) => {
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/signup/confirm`,
            {
                email:email,
                code:emailCode,
            }
        );
        return {
            status: 'success',
            data: response.data,
        };
    } catch(err) {
        return {
            status: 'error',
            data: err.response.data.message,
        };
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
            status: 'success',
            authorization: response.headers.authorization,
            refresh: response.headers.refresh,
        };
    } catch (err) {
        return {
            status: 'error',
            data: err.data
        };
    }
}

//OAuth 로그인
export const oauthLoginService = async (provider,authorizationCode) => {
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/oauth`,
            {
                provider: provider,
                code:authorizationCode
            }
        );
        return {
            status: 'sccess',
            authorization: response.headers.authorization,
            refresh: response.headers.refresh,
        };
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message
        };
    }
}