import axios from "axios";
import { useState } from "react";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import Stars from "../contentListItems/Stars";

const ReviewList = ({ el, getReview }) => {
  const myId = useSelector((state) => state.loginInfo.myid);
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
        console.log(res);
        if (res.status === 204) {
          // window.location.reload();
          getReview();
        }
      })
      .catch((err) => console.log(err));
  };

  const deleteReview = (replyId) => {
    return axios
      .delete(`https://api.itprometheus.net/replies/${replyId}`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res);
        if (res.status === 204) {
          // window.location.reload();
          getReview();
        }
      })
      .catch((err) => console.log(err));
  };

  return (
    <ReList>
      {myId === el.member.memberId && (
        <>
          {isEditMode ? (
            <ReviewPatch
              onClick={() => {
                if (window.confirm("댓글을 저장 하시겠습니까?")) {
                  setEditMode(false);
                  patchReview(el.replyId);
                }
              }}
            >
              저장
            </ReviewPatch>
          ) : (
            <ReviewPatch
              onClick={() => {
                setEditMode(true);
                setEditReply({ ...editReply, star: el.star });
              }}
            >
              수정
            </ReviewPatch>
          )}
          <ReviewDelete
            onClick={() => {
              if (window.confirm("댓글을 삭제 하시겠습니까?")) {
                deleteReview(el.replyId);
              }
            }}
          >
            삭제
          </ReviewDelete>
        </>
      )}

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
        <ReviewDate>{el.createdDate.split("T")[0]}</ReviewDate>
      </ReviewInfo>
    </ReList>
  );
};

export default ReviewList;

export const StarBox = styled.div`
  position: relative;
  width: 120px;
  height: 24px;
  margin-bottom: 5px;
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
export const ReviewContent = styled.div`
  width: 100%;
  flex-wrap: wrap;
  margin: 5px 0px;
  padding: 5px 5px;
`;

export const ReviewInfo = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  font-size: 14px;
`;

export const ReviewName = styled.div`
  margin-right: 10px;
  color: gray;
`;

export const ReviewDate = styled.div`
  color: gray;
`;
