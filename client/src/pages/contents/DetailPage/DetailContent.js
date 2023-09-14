import axios from "axios";
import { useState } from "react";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setContnentOpen } from "../../../redux/createSlice/VideoInfoSlice";
import tokens from '../../../styles/tokens.json';
import { BodyTextTypo, Heading5Typo, SmallTextTypo } from '../../../atoms/typographys/Typographys'
import { PositiveTextButton, TextButton } from '../../../atoms/buttons/Buttons'

const globalTokens = tokens.global;

const DetailContent = ({ getVideoInfo }) => {
  const { videoId } = useParams();
  const myId = useSelector((state) => state.loginInfo.myid);
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const dispatch = useDispatch();
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isEdit, setEdit] = useState(false);
  const [isIntro, setIntro] = useState({ description: "" });

  const patchIntro = () => {
    return axios
      .patch(`https://api.itprometheus.net/videos/${videoId}`, isIntro, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res);
        getVideoInfo();
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const handlePatchIntro = (e) => {
    setIntro({ ...isIntro, description: e.target.value });
  };

  return (
    <ContentInfo isDark={isDark}>
      <ContentTitle isDark={isDark}>
        강의 소개
        {myId === videoDatas.channel.memberId &&
          (!isEdit ? (
            <ContentPatch
              onClick={() => {
                setEdit(!isEdit);
                setIntro({ ...isIntro, description: videoDatas.description });
              }}
            >
              수정하기
            </ContentPatch>
          ) : (
            <ContentPatch
              onClick={() => {
                setEdit(!isEdit);
                patchIntro();
              }}
            >
              저장하기
            </ContentPatch>
          ))}
      </ContentTitle>
      <SubTitle isDark={isDark}>
        <Views isDark={isDark}>조회수 {videoDatas.views}회</Views>
        <Createdate isDark={isDark}>{videoDatas.createdDate.split("T")[0]}</Createdate>
      </SubTitle>

      <Content isDark={isDark}>
        {!isEdit ? (
          videoDatas.description || "(강의 소개가 없습니다.)"
        ) : (
          <ContentEdit
            placeholder="강의 소개가 없습니다."
            value={isIntro.description}
            onChange={(e) => handlePatchIntro(e)}
          />
        )}
        <Category>
          {videoDatas.categories.map((el) => (
            <CategoryLists key={el.categoryId}>
              #{el.categoryName}
            </CategoryLists>
          ))}
        </Category>
      </Content>
    </ContentInfo>
  );
};

export default DetailContent;

export const ContentInfo = styled.div`
  width: 100%;
  border-radius: ${globalTokens.BigRadius.value}px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing16.value}px;
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
`;

export const ContentTitle = styled(Heading5Typo)`
  position: relative;
  width: 100%;
  border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
  font-weight: ${globalTokens.Bold.value};
  padding: ${globalTokens.Spacing8.value}px;
`;

export const ContentEdit = styled.textarea`
  width: 100%;
  flex-wrap: wrap;
  margin: 5px 0px;
  padding: 5px 5px;
  border: none;
  background-color: rgb(240, 240, 240);

  &:focus {
    outline: none;
  }
`;

export const ContentPatch = styled.button`
  position: absolute;
  top: 20px;
  right: 2%;
  color: rgb(260, 100, 120);
  text-decoration: underline;
`;

export const SubTitle = styled(SmallTextTypo)`
  margin: ${globalTokens.Spacing4.value}px 0px;
  padding-left: ${globalTokens.Spacing8.value}px;
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`;

export const Views = styled.div`
  padding-right: ${globalTokens.Spacing12.value}px;
`;

export const Createdate = styled(Views)``;

export const Content = styled(BodyTextTypo)`
  width: 100%;
  margin-bottom: ${globalTokens.Spacing12.value}px;
  padding: ${globalTokens.Spacing8.value}px 0px 0px ${globalTokens.Spacing8.value}px;
  flex-wrap: wrap;
  overflow: hidden;
`;

export const Category = styled.ul`
  display: flex;
  justify-content: start;
  align-items: center;
  margin-top: 10px;
  padding-left: 5px;
`;

export const CategoryLists = styled.li`
  margin-right: ${globalTokens.Spacing8.value}px;
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
  font-size: ${globalTokens.BodyText.value}px;
`;

export const ContentBtn = styled(PositiveTextButton)`
  padding-left: ${globalTokens.Spacing8.value}px;
  margin-bottom: ${globalTokens.Spacing12.value}px;
`;
