import axios from 'axios';
import { ROOT_URL } from '.';

//프로필 조회
export const getUserInfoService = async (authorization) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/members`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data.data
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message
        }
    }
}
//프로필 변경 step1 : 프로필 등록 URL 받기
export const getUploadProfileImgUrlService = async (
    authorization, 
    email, 
    imageType
) => {
    try {
        const response = await axios.patch(
            `${ROOT_URL}/members/image`,
            {
                imageName: email,
                imageType: imageType
            },
            {
                headers: {
                    Authorization: authorization
                }
            }
        )
        return {
            status : 'success',
            data: response.data,
        }
    } catch(err) {
        return {
            status: 'error',
            data: err
        }
    }
}

//닉네임 변경
export const updateNicknameService = async (authorization, nickname) => {
    try {
        const response = await axios.patch(
            `${ROOT_URL}/members`,
            { nickname: nickname, },
            { headers: { Authorization: authorization, } }
        );
        return {
            status: 'success',
            data: response.data
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.message
        }
    }
}

//비밀번호 변경
export const updatePasswordService = async (authorization, prevPassword, newPassword) => {
    try {
        const response = await axios.patch(
            `${ROOT_URL}/members/password`,
            {
                prevPassword: prevPassword,
                newPassword: newPassword
            },
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: response.data
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message
        }
    }
}

//회원 탈퇴
export const deleteUserInfoService = async (authorization) => {
    try {
        const response = await axios.delete(
            `${ROOT_URL}/members`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        )
        return {
            status: 'success',
            data: response.data,
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response
        }
    }
}