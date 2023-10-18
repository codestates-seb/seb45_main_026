import { useDispatch, useSelector } from "react-redux";
import {
  HeaderProfileContainer,
  HeaderProfileImg,
  HeaderProfileImgContainer,
  HeaderProfileInfo,
  HeaderProfileNegativeButton,
  HeaderProfileWrapper,
} from "./HeaderProfile.style";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import ProfileGray from "../../assets/images/icons/profile/profileGray.svg";
import { setIsSideBar } from "../../redux/createSlice/UISettingSlice";
import SideBar from "../sideBar/SideBar";
import { useNavigate } from "react-router-dom";

const HeaderProfile = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const isSideBar = useSelector((state) => state.uiSetting.isSideBar);
  const userInfo = useSelector((state) => state.loginInfo.loginInfo);

  const handleHeaderProfileClick = () => {
    dispatch(setIsSideBar(!isSideBar));
  };

  const handleHeaderProfileBlur = () => {
    dispatch(setIsSideBar(false));
  };
  const handleRewardClick = (e) => {
    e.stopPropagation();
    navigate("/activity/receipt");
  };

  return (
    <HeaderProfileWrapper>
      {userInfo.authority === "ROLE_ADMIN" && (
        <HeaderProfileNegativeButton
          isDark={isDark}
          onClick={() => {
            navigate("/admin");
          }}
        >
          신고 내역 관리
        </HeaderProfileNegativeButton>
      )}
      <HeaderProfileInfo isDark={isDark} onClick={handleRewardClick}>
        보유 포인트 : {userInfo.reward}p
      </HeaderProfileInfo>
      <HeaderProfileContainer
        onClick={handleHeaderProfileClick}
        onBlur={handleHeaderProfileBlur}
      >
        <BodyTextTypo isDark={isDark}>{userInfo.nickname}</BodyTextTypo>
        <HeaderProfileImgContainer>
          <HeaderProfileImg
            src={
              userInfo.imgUrl !== "프로필 이미지 미등록"
                ? userInfo.imgUrl
                : ProfileGray
            }
          />
        </HeaderProfileImgContainer>
        <SideBar />
      </HeaderProfileContainer>
    </HeaderProfileWrapper>
  );
};

export default HeaderProfile;
