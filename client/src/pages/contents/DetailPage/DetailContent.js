import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { setContnentOpen } from "../../../redux/createSlice/VideoInfoSlice";

const DetailContent = () => {
  const dispatch = useDispatch();
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const contentOpend = useSelector(
    (state) => state.videoInfo.mode.contentOpend
  );

  return (
    <ContentInfo>
      <ContentTitle>강의 소개</ContentTitle>

      <SubTitle>
        <Views>조회수 {videoDatas.views}회</Views>
        <Createdate>{videoDatas.createdDate.split("T")[0]}</Createdate>
      </SubTitle>

      <Content isOpened={contentOpend}>
        {videoDatas.description}
        <Category>
          {videoDatas.categories.map((el) => (
            <CategoryLists key={el.categoryId}>
              #{el.categoryName}
            </CategoryLists>
          ))}
        </Category>
      </Content>

      <ContentBtn onClick={() => dispatch(setContnentOpen(!contentOpend))}>
        {!contentOpend ? "...더보기" : "간략히"}
      </ContentBtn>
    </ContentInfo>
  );
};

export default DetailContent;

export const ContentInfo = styled.div`
  width: 100%;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  padding: 5px 20px;
  background-color: white;
`;

export const ContentTitle = styled.div`
  width: 100%;
  border-bottom: 2px solid rgb(236, 236, 236);
  background-color: white;
  font-weight: bold;
  font-size: 18px;
  padding: 10px;
`;

export const SubTitle = styled.div`
  margin: 5px 0px;
  padding-left: 5px;
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  font-size: 14px;
  color: gray;
`;

export const Views = styled.div`
  padding-right: 10px;
`;

export const Createdate = styled(Views)``;

export const Content = styled.div`
  width: 100%;
  height: ${(props) => props.isOpened || "30px"};
  margin-bottom: 10px;
  padding: 10px 0px 0px 10px;
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
  margin-right: 10px;
  color: gray;
  font-size: 14px;
`;

export const ContentBtn = styled.button`
  padding-left: 5px;
  font-size: small;
  color: gray;
  font-size: 16px;
  margin-bottom: 10px;
`;
