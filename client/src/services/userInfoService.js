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
//채널정보 조회
export const getUserChannelInfoService = async (authorization, memberId) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/channels/${memberId}`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        )
        return {
            status: 'success',
            data: response.data.data
        }
    } catch (err) {
        return {
            status: 'error',
        }
    }
}
//프로필 변경 step1 : 프로필 등록 presignedUrl 받기
export const getUploadProfileImgUrlService = async (
    authorization, 
    imageName, 
    imageType
) => {
    try {
        const response = await axios.patch(
            `${ROOT_URL}/members/image`,
            {
                imageName: imageName,
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
            data: response.headers.location,
        }
    } catch(err) {
        return {
            status: 'error',
            data: err
        }
    }
}
//프로필 변경 step2 : presignedUrl로 post 요청하기
export const uploadProfileImage = async (presignedUrl, file) => {
    try {
        let ex = file.name.split(".")[1];
        ex = ex.toLowerCase();
        const response = await axios.put(
            `${presignedUrl}`, 
            file,
            { headers: { "Content-type": `image/${ex}`, } }
        );
        return {
            status: 'success',
            data: response
        }
    } catch (err) {
        return {
            status: 'error',
            data: err
        }
    }
}
//프로필 삭제
export const deleteProfileImage = async (authorization) => {
    try {
        await axios.delete(
            `${ROOT_URL}/members/image`,{
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success',
            data: null
        }
    } catch (err) {
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
//채널 정보 수정
export const updateChannelInfoService = async (authorization, memberId, channelName, channelDescription) => {
    try {
        await axios.patch(
            `${ROOT_URL}/channels/${memberId}`,
            {
                channelName: channelName,
                description: channelDescription,
            },
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        return {
            status: 'success'
        }
    } catch(err) {
        return {
            status: 'error'
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