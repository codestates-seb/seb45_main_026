import axios from "axios";
import { useState } from "react";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import { useParams } from "react-router-dom";

const DetailContent = ({ getVideoInfo }) => {
  const { videoId } = useParams();
  const myId = useSelector((state) => state.loginInfo.myid);
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
    <ContentInfo>
      <ContentTitle>
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

      <SubTitle>
        <Views>조회수 {videoDatas.views}회</Views>
        <Createdate>{videoDatas.createdDate.split("T")[0]}</Createdate>
      </SubTitle>

      <Content>
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
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  padding: 5px 20px;
  background-color: white;
`;

export const ContentTitle = styled.div`
  position: relative;
  width: 100%;
  border-bottom: 2px solid rgb(236, 236, 236);
  background-color: white;
  font-weight: bold;
  font-size: 18px;
  padding: 10px;
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
  margin-bottom: 10px;
  padding: 10px 10px 0px 10px;
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
