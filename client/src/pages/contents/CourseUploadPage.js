import { styled } from "styled-components";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import CourseUpload from "../../components/UploadPage/CourseUpload";
import tokens from '../../styles/tokens.json';
import { useSelector } from "react-redux";
import { Heading5Typo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const CourseUploadPage = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);

  return (
    <PageContainer isDark={isDark}>
      <UploadContainer isDark={isDark}>
        <CourseUpload />
      </UploadContainer>
    </PageContainer>
  );
};

export default CourseUploadPage;

export const UploadContainer = styled.section`
  width: 100%;
  max-width: 1000px;
  margin: ${globalTokens.Spacing40.value}px 0px;
  padding: 50px 0px 100px 0px;
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
  border-radius: ${globalTokens.RegularRadius.value}px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  position: relative;
`;

export const UploadType = styled.div`
  display: flex;
  align-items: center;
`;

export const UploadTypeBtn = styled.button`
  width: 40px;
  height: 40px;
  margin: 10px;
  border: 2px solid rgb(255, 100, 100);
  border-radius: 50%;
  background-color: ${(props) =>
    props.isFocus ? "rgb(255, 100, 100);" : "white"};
  color: ${(props) => (props.isFocus ? "white" : "rgb(255, 100, 100)")};
  font-weight: bold;
  font-size: 16px;
`;

export const UploadTitle = styled(Heading5Typo)``;

export const UploadSubtitle = styled.span`
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
  font-size: 14px;
`;

export const SubDescribe = styled.span`
  color: ${props=>props.isDark?globalTokens.LightRed.value:globalTokens.Negative.value};
  font-size: 12px;
  margin-left: 15px;
`;

export const RowBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
  width: 100%;
  margin: 10px 0px;
`;

export const ColBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  width: 100%;
`;