import React, { useRef, useState } from 'react';
import { ProfileImg, ImgContainer } from '../../pages/contents/ChannelPage';
import profileGray from '../../assets/images/icons/profile/profileGray.svg';
import { styled } from 'styled-components';
import { BodyTextTypo } from '../typographys/Typographys';
import { useSelector } from 'react-redux';
import { NegativeTextButton, PositiveTextButton } from '../buttons/Buttons';
import { getUploadProfileImgUrlService } from '../../services/userInfoService';

const ImageInputContainer = styled.div`
    margin-top: ${props=>props.marginTop?props.marginTop:0}px;
    display: flex;
    flex-direction: column;
`
const ImageInputButtonContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: center;
`

export const ImageInput = ({
    marginTop, label}) => {
    const fileInput = useRef(null);
    const tokens = useSelector(state=>state.loginInfo.accessToken);
    const userInfo = useSelector(state=>state.loginInfo.loginInfo);
    const isDark = useSelector(state=>state.uiSetting.isDark);
    
    //새로운 이미지를 선택하면 실행됨
    const onChangeProfileImg = async (e) => {
        const file = e.target.files[0];
        const fileArr = file.name.split('.');
        const 확장자 = fileArr[fileArr.length-1].toLowerCase();

        if (확장자!=='jpg' && 확장자!=='jpeg' && 확장자!=='png') {
            console.log('지원하지 않는 확장자입니다.');
            return;
        } else {
            const response = await getUploadProfileImgUrlService(
                tokens.authorization,
                userInfo.email,
                확장자
            );
            if(response.status==='success') {
                console.log(response.data);
            } else {
                console.log(response.data);
            }
        }
    }
    //수정 버튼을 누르면 실행됨
    const handleProfileUpdateButtonClick = () => {
        fileInput.current.click();
    }
    //삭제 버튼을 누르면 실행됨
    const handleProfileDeleteButtonClick = () => {

    }

    return (
        <ImageInputContainer marginTop={marginTop}>
            { label &&
                <BodyTextTypo isDark={isDark}>{label}</BodyTextTypo>
            }
            <ImgContainer>
                <ProfileImg src={profileGray}/>
            </ImgContainer>
            <input
                type='file'
                style={{display:'none'}}
                accept='image/jpg, image/png/ image/jpeg'
                name='profile_img'
                onChange={onChangeProfileImg}
                ref={fileInput}/>
            <ImageInputButtonContainer>
                <PositiveTextButton 
                    type='button' 
                    isDark={isDark}
                    onClick={handleProfileUpdateButtonClick}>
                        수정
                </PositiveTextButton>
                <NegativeTextButton 
                    type='button' 
                    isDark={isDark}
                    onClick={handleProfileDeleteButtonClick}>
                        삭제
                </NegativeTextButton>
            </ImageInputButtonContainer>
        </ImageInputContainer>
    );
};

export default ImageInput;