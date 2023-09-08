import React, { useRef, useState } from 'react';
import { ProfileImg, ImgContainer } from '../../pages/contents/ChannelPage';
import profileGray from '../../assets/images/icons/profile/profileGray.svg';
import { styled } from 'styled-components';
import { BodyTextTypo, SmallTextTypo } from '../typographys/Typographys';
import { useSelector } from 'react-redux';
import { NegativeTextButton, PositiveTextButton } from '../buttons/Buttons';
import { deleteProfileImage, getUploadProfileImgUrlService, getUserInfoService, uploadProfileImage } from '../../services/userInfoService';
import { useDispatch } from 'react-redux';
import { setLoginInfo } from '../../redux/createSlice/LoginInfoSlice';
import tokens from '../../styles/tokens.json';
import { AlertModal } from '../modal/Modal';

const globalTokens = tokens.global;

const ImageInputContainer = styled.div`
    margin-top: ${props=>props.marginTop?props.marginTop:0}px;
    display: flex;
    flex-direction: column;
`
const ImageInputButtonContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: start;
    padding-left: 12px;
`
const CautionTextTypo = styled(SmallTextTypo)`
    color: ${props=>props.isDark?globalTokens.LightRed.value:globalTokens.Negative.value}
`

export const ImageInput = ({
    marginTop, label}) => {
    const dispatch = useDispatch();
    const fileInput = useRef(null);
    const tokens = useSelector(state=>state.loginInfo.accessToken);
    const userInfo = useSelector(state=>state.loginInfo.loginInfo);
    const myid = useSelector(state=>state.loginInfo.myid)
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [ is이미지등록실패팝업, setIs이미지등록실패팝업 ] = useState(false);
    //새로운 이미지를 선택하면 실행됨
    const onChangeProfileImg = async (e) => {
        const file = e.target.files[0];
        if(!file) return;
        const fileArr = file.name.split('.');
        const 확장자 = fileArr[fileArr.length-1].toLowerCase();
       
        if (확장자!=='jpg' && 확장자!=='jpeg' && 확장자!=='png') {
            console.log('지원하지 않는 확장자입니다.');
            return;
        }
        const response = await getUploadProfileImgUrlService(
           tokens.authorization, `${myid}`, 확장자 );

        if(response.status==='success') {
            const presignedUrl = response.data;
            const res = await uploadProfileImage(presignedUrl, file);
            if(res.status==='success') {
                const response = await getUserInfoService(tokens.authorization);
                if(response.status==='success') {
                    dispatch(setLoginInfo({
                        ...userInfo,
                        imgUrl: `${response.data.imageUrl}?${new Date().getTime()}`
                    }));
                }
            } else {
                setIs이미지등록실패팝업(true);
            }
        } else {
            console.log(response.data);
        }
    }
    //수정 버튼을 누르면 실행됨
    const handleProfileUpdateButtonClick = () => {
        fileInput.current.click();
    }
    //삭제 버튼을 누르면 실행됨
    const handleProfileDeleteButtonClick = async () => {
        const response = await deleteProfileImage(tokens.authorization);
        if(response.status==='success') {
            dispatch(setLoginInfo({
                ...userInfo,
                imgUrl: '프로필 이미지 미등록'
            }))
        }
    }

    return (
        <>
        {/* 이미지 등록 실패 팝업 */}
        <AlertModal 
            isModalOpen={is이미지등록실패팝업}
            setIsModalOpen={setIs이미지등록실패팝업}
            isBackdropClickClose={true}
            content='이미지 등록 실패했습니다.'
            buttonTitle='확인'
            handleButtonClick={()=>{setIs이미지등록실패팝업(false)}}/>
        <ImageInputContainer marginTop={marginTop}>
            { label &&
                <BodyTextTypo isDark={isDark}>{label}</BodyTextTypo>
            }
            <ImgContainer>
                <ProfileImg src={userInfo.imgUrl!=='프로필 이미지 미등록'?userInfo.imgUrl:profileGray}/>
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
            <CautionTextTypo isDark={isDark}>파일명에 특수문자, 공백을 포함하지 않는 png, jpg, jpeg 파일만 등록 가능합니다. </CautionTextTypo>
        </ImageInputContainer>
        </>
    );
};

export default ImageInput;