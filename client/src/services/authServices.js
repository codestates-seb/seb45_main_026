import axios from 'axios';
import { ROOT_URL } from '.';

//회원가입 API
export const signupService = async ({data}) => {
    try {
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

//회원가입 email 인증코드 발송
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

//회원가입 email 인증코드 확인
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
            },
        );
        return {
            status: 'success',
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

//패스워드 찾기 email 인증번호 발송
export const findPasswordEmailValidService = async (email) => {
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/password/email`,
            {
                email: email
            }
        );
        return {
            status: 'success',
            data: response.data,
        }
    } catch (err) {
        return {
            status: 'error',
        }
    }
}

//패스워드 찾기 email 인증번호 확인
export const findPasswordEmailValidConfirmService = async (email, emailCode) => {
    try {
        const response = axios.post(
            `${ROOT_URL}/auth/password/confirm`,
            {
                email: email,
                code: emailCode
            }
        );
        return {
            status: 'success',
            data: response.data,
        };
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        };
    }
}

//비밀번호 초기화
export const updatePasswordService = async (email, password) => {
    try {
        console.log(email)
        console.log(password)
        const response = await axios.patch(
            `${ROOT_URL}/auth/password`,
            {
                email: email,
                password: password
            },
        );
        return {
            status: 'success',
            data: response.data
        }
    } catch(err) {
        return {
            status: 'error',
        }
    }
}

//refresh 토큰으로 다시 authorization 토큰 받기
export const getNewAuthorizationService = async (refresh) => {
    try {
        const response = await axios.post(
            `${ROOT_URL}/auth/refresh`,{}, 
            {
                headers: {
                    Refresh:refresh,
                }
            }
        );
        return {
            status: 'success',
            data: response.headers.authorization
        }
    } catch (err) {
        return {
            status: 'err',
            data: err
        }
    }
}