import { styled } from "styled-components";
import { useEffect } from "react";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import ProblemUpload from "../../components/UploadPage/ProblemUpload";
import { useSelector } from "react-redux";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

const ProblemUploadPage = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  useEffect(() => {
    window.scrollTo({
      top: 0,
    });
  }, []);
  return (
    <PageContainer isDark={isDark}>
      <UploadContainer isDark={isDark}>
        <ProblemUpload />
      </UploadContainer>
    </PageContainer>
  );
};

export default ProblemUploadPage;

export const UploadContainer = styled.section`
  width: 100%;
  max-width: 1000px;
  margin: ${globalTokens.Spacing40.value}px 0;
  padding: 50px 0px 100px 0px;
  background-color: ${
    props=>props.isDark?'rgba(255,255,255,0.15)'
    :globalTokens.White.value
  };
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

export const UploadTitle = styled.h2``;

export const UploadSubtitle = styled.span`
  margin: 5px 0px 50px 5px;
  color: gray;
  font-size: 14px;
`;

export const SubDescribe = styled.span`
  color: red;
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

export const NextBtn = styled.button`
  position: absolute;
  bottom: 4%;
  right: 6%;
  width: 100px;
  height: 40px;
  color: white;
  font-weight: 600;
  border-radius: 8px;
  background-color: rgb(255, 100, 100);
  &:hover {
    background-color: rgb(255, 150, 150);
  }
`;
