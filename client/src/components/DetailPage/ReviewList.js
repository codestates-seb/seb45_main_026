import { styled } from "styled-components";
import Stars from "../contentListItems/Stars";

const ReviewList = () => {
  return (
    <ReList>
      <ReviewPatch>수정</ReviewPatch>
      <ReviewDelete>삭제</ReviewDelete>
      <StarBox>
        <Stars score={1.5} />
      </StarBox>
      <ReviewContent>
        중간에 루즈한 부분도 있었지만 재밌게 잘봤습니다.
      </ReviewContent>
      <ReviewInfo>
        <ReviewName>김둥구</ReviewName>
        <ReviewDate>2023.08.26</ReviewDate>
      </ReviewInfo>
    </ReList>
  );
};

export default ReviewList;

export const StarBox = styled.div`
  height: 30px;
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
