import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { setContnentOpen } from "../../../redux/createSlice/VideoInfoSlice";
import tokens from '../../../styles/tokens.json';
import { BodyTextTypo, Heading5Typo, SmallTextTypo } from '../../../atoms/typographys/Typographys'
import { PositiveTextButton, TextButton } from '../../../atoms/buttons/Buttons'

const globalTokens = tokens.global;

const DetailContent = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const dispatch = useDispatch();
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const contentOpend = useSelector(
    (state) => state.videoInfo.mode.contentOpend
  );

  return (
    <ContentInfo isDark={isDark}>
      <ContentTitle isDark={isDark}>강의 소개</ContentTitle>

      <SubTitle isDark={isDark}>
        <Views isDark={isDark}>조회수 {videoDatas.views}회</Views>
        <Createdate isDark={isDark}>{videoDatas.createdDate.split("T")[0]}</Createdate>
      </SubTitle>

      <Content isDark={isDark} isOpened={contentOpend}>
        {videoDatas.description || "(강의 소개가 없습니다.)"}
        <Category>
          {videoDatas.categories.map((el) => (
            <CategoryLists key={el.categoryId}>
              #{el.categoryName}
            </CategoryLists>
          ))}
        </Category>
      </Content>

      <ContentBtn isDark={isDark} onClick={() => dispatch(setContnentOpen(!contentOpend))}>
        {!contentOpend ? "...더보기" : "간략히"}
      </ContentBtn>
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
  width: 100%;
  border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
  font-weight: ${globalTokens.Bold.value};
  padding: ${globalTokens.Spacing8.value}px;
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
  height: ${(props) => props.isOpened || "30px"};
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
