import { styled } from "styled-components";
import axios from "axios";
import { useSelector } from "react-redux";
import Stars from "../contentListItems/Stars";
import { useState } from "react";

const ReviewList = ({ el }) => {
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isEditMode, setEditMode] = useState(false);
  const [editReply, setEditReply] = useState({
    content: el.content,
    star: 0,
  });

  const patchReview = (replyId) => {
    return axios
      .patch(`https://api.itprometheus.net/replies/${replyId}`, editReply, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res.data);
      })
      .catch((err) => console.log(err));
  };

  const deleteReview = (replyId) => {
    return axios
      .delete(`https://api.itprometheus.net/replies/${replyId}`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res.data);
      })
      .catch((err) => console.log(err));
  };

  return (
    <ReList>
      {isEditMode ? (
        <ReviewPatch onClick={() => setEditMode(false)}>저장</ReviewPatch>
      ) : (
        <ReviewPatch onClick={() => setEditMode(true)}>수정</ReviewPatch>
      )}
      <ReviewDelete onClick={() => {}}>삭제</ReviewDelete>
      <StarBox>
        <Stars score={el.star} />
      </StarBox>
      {isEditMode ? (
        <ReviewEdit
          value={editReply.content}
          onChange={(e) =>
            setEditReply({ ...editReply, content: e.target.value })
          }
        />
      ) : (
        <ReviewContent>{el.content}</ReviewContent>
      )}

      <ReviewInfo>
        <ReviewName>{el.member.nickname}</ReviewName>
        <ReviewDate>{el.createdDate}</ReviewDate>
      </ReviewInfo>
    </ReList>
  );
};

export default ReviewList;

export const StarBox = styled.div`
  position: relative;
  width: 120px;
  height: 24px;
`;

export const ReList = styled.li`
  position: relative;
  width: 100%;
  max-width: 500px;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: start;
  border: 1px solid gray;
  border-radius: 8px;
  padding: 15px 20px;
  margin: 15px;
`;

export const ReviewPatch = styled.button`
  position: absolute;
  top: 5px;
  right: 10%;
  color: rgb(260, 100, 120);
  text-decoration: underline;
`;

export const ReviewDelete = styled.button`
  position: absolute;
  top: 5px;
  right: 3%;
  color: rgb(260, 100, 120);
  text-decoration: underline;
`;

export const ReviewEdit = styled.input`
  flex-wrap: wrap;
  margin: 10px 0px;
`;
export const ReviewContent = styled.div`
  flex-wrap: wrap;
  margin: 10px 0px;
`;

export const ReviewInfo = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  margin-top: 5px;
  font-size: 14px;
`;

export const ReviewName = styled.div`
  margin-right: 10px;
`;

export const ReviewDate = styled.div`
  color: gray;
`;
