import { useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import axios from "axios";
import { useToken } from "../../hooks/useToken";
import { AlertModal, ReportModal } from "../../atoms/modal/Modal";
import { ReactComponent as Chat } from "../../assets/images/icons/Chat.svg";
import { ReactComponent as Dot } from "../../assets/images/icons/Dot.svg";

export default function ChannelNav({ navigate, setNavigate }) {
  const { userId } = useParams();
  const refreshToken = useToken();
  const myid = useSelector((state) => state.loginInfo.myid);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isReportOpen, setReportOpen] = useState(false);
  const [reportModal, setReportModal] = useState(false);
  const [reportContent, setReportContent] = useState("");
  const [reportedModal, setReportedModal] = useState(false);
  const [alreadyReportedModal, setAlreadyReportedModal] = useState(false);

  const navs = ["홈", "강의", "커뮤니티"];
  if (myid === Number(userId)) navs.push("설정");

  const handleReportChannel = () => {
    return axios
      .post(
        `https://api.itprometheus.net/channels/${userId}/reports`,
        { reportContent: reportContent },
        { headers: { Authorization: token.authorization } }
      )
      .then((res) => {
        if (res.data.data) {
          setReportModal(false);
          setReportedModal(true);
          setReportContent("");
        } else {
          setReportModal(false);
          setAlreadyReportedModal(true);
        }
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  };

  const handleReportContent = (e) => {
    setReportContent(e.target.value);
  };

  return (
    <>
      <NavyContainer isDark={isDark}>
        {navs.map((el, idx) =>
          navigate === idx ? (
            <NavyItem
              key={idx}
              onClick={() => setNavigate(idx)}
              isDark={isDark}
            >
              {el}
            </NavyItem>
          ) : (
            <NavyItem2
              key={idx}
              onClick={() => setNavigate(idx)}
              isDark={isDark}
            >
              {el}
            </NavyItem2>
          )
        )}
        {myid !== Number(userId) && (
          <ReportBox>
            <ReportBtn
              isDark={isDark}
              onClick={() => setReportOpen(!isReportOpen)}
              onBlur={() => setReportOpen(false)}
            >
              <ReportDot isDark={isDark} />
              {isReportOpen && (
                <>
                  <ReportMsg isDark={isDark} />
                  <ReportComment
                    isDark={isDark}
                    onClick={() => setReportModal(true)}
                  >
                    채널 신고
                  </ReportComment>
                </>
              )}
            </ReportBtn>
          </ReportBox>
        )}
      </NavyContainer>
      <ReportModal
        reportContent={reportContent}
        setReportContent={handleReportContent}
        isModalOpen={reportModal}
        setIsModalOpen={setReportModal}
        isBackdropClickClose={false}
        negativeButtonTitle="신고"
        positiveButtonTitle="취소"
        handleNegativeButtonClick={() => handleReportChannel()}
        handlePositiveButtonClick={() => setReportModal(false)}
      />
      <AlertModal
        isModalOpen={reportedModal}
        setIsModalOpen={setReportedModal}
        isBackdropClickClose={true}
        content="비디오가 신고 되었습니다."
        buttonTitle="확인"
        handleButtonClick={() => setReportedModal(false)}
      />
      <AlertModal
        isModalOpen={alreadyReportedModal}
        setIsModalOpen={setAlreadyReportedModal}
        isBackdropClickClose={true}
        content="이미 신고한 비디오입니다."
        buttonTitle="확인"
        handleButtonClick={() => setAlreadyReportedModal(false)}
      />
    </>
  );
}

const globalTokens = tokens.global;

const NavyContainer = styled.div`
  position: relative;
  width: 100%;
  height: 45px;
  display: flex;
  flex-direction: row;
  border-radius: ${globalTokens.RegularRadius.value}px
    ${globalTokens.RegularRadius.value}px 0 0;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-bottom: 3px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`;
//선택한 Nav Item
export const NavyItem = styled.div`
  width: 85px;
  height: 45px;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  font-size: ${globalTokens.BodyText.value}px;
  font-weight: ${globalTokens.Bold.value};
  text-align: center;
  padding-top: ${globalTokens.Spacing8.value}px;
  border-bottom: 3px solid
    ${(props) =>
      props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  &:hover {
    cursor: pointer;
  }
`;
//선택하지 않은 Nav Item
export const NavyItem2 = styled.div`
  width: 85px;
  height: 45px;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  font-size: ${globalTokens.BodyText.value}px;
  /* font-weight: ${globalTokens.Bold.value}; */
  text-align: center;
  padding-top: ${globalTokens.Spacing8.value}px;
  border-bottom: 3px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  &:hover {
    cursor: pointer;
  }
`;

export const ReportBox = styled.div`
  position: absolute;
  right: 1%;
  width: 100%;
  max-width: 45px;
  height: 45px;
  padding-top: 15px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const ReportBtn = styled.button`
  position: relative;
  width: 100%;
  max-width: 45px;
  height: 45px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const ReportDot = styled(Dot)`
  width: 100%;
  max-width: 42px;
  height: 15px;
  path {
    fill: ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  }
  &:hover {
    path {
      fill: ${globalTokens.LightRed.value};
    }
  }
`;

export const ReportMsg = styled(Chat)`
  position: absolute;
  top: 28px;
  width: 100px;
  height: 50px;
  transform: scaleY(-1);
  path {
    fill: ${(props) =>
      props.isDark
        ? globalTokens.LightNavy.value
        : globalTokens.LightRed.value};
  }
`;

export const ReportComment = styled.span`
  position: absolute;
  top: 49px;
  width: 100px;
  height: 50px;
  text-align: center;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  font-weight: 600;
`;
