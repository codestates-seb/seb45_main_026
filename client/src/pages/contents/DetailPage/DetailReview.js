import { styled } from "styled-components";
import axios from "axios";
import { useSelector } from "react-redux";
import { RegularInput } from "../../../atoms/inputs/Inputs";
import ReviewStar from "../../../components/DetailPage/ReviewStar";
import ReviewList from "../../../components/DetailPage/ReviewList";

const DetailReview = () => {
  const token = useSelector((state) => state.loginInfo.accessToken);
  const dummyData = [0, 1, 2, 3, 4, 5, 6];

  const getReviewData = () => {
    return axios
      .get(
        `https://api.itprometheus.net/videos/1/replies?page=1&size=5&sort=created-date&star=4`,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => console.log(res.data))
      .catch((err) => console.log(err));
  };

  return (
    <ReviewContainer>
      <ReviewTitle>수강평 {dummyData.length}</ReviewTitle>

      <ReviewForm>
        <ReviewLabel>리뷰</ReviewLabel>
        <WriteTitle>별점을 선택해주세요.</WriteTitle>
        <ReviewStar />
        <ReviewSubmit>
          <ReviewInput placeholder="한 줄 감상평을 등록해주세요." />
          <ReviewBtn onClick={(e) => e.preventDefault()}>등록</ReviewBtn>
        </ReviewSubmit>
      </ReviewForm>

      <Reviews>
        <FilterBtns>
          <FilterBtn>최신순 ↑</FilterBtn>
          <FilterBtn>별점순 ↑</FilterBtn>
          <FilterBtn>
            별점별
            <img src="" alt="" />
          </FilterBtn>
        </FilterBtns>

        <ReviewLists>
          {dummyData.map((el, idx) => (
            <ReviewList key={idx} el={el} />
          ))}
        </ReviewLists>
      </Reviews>
    </ReviewContainer>
  );
};

export default DetailReview;

export const ReviewContainer = styled.div`
  width: 100%;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 20px 0px;
  padding: 5px 20px;
  background-color: white;
`;

export const ReviewTitle = styled.div`
  width: 100%;
  border-bottom: 2px solid rgb(236, 236, 236);
  font-weight: bold;
  font-size: 18px;
  background-color: white;
  padding: 5px;
  margin: 10px 0px;
`;

export const ReviewForm = styled.form`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  width: 100%;
  padding: 10px;
  background-color: rgb(220, 220, 250);
`;

export const ReviewLabel = styled.span`
  border: 2px solid rgb(250, 250, 220);
  border-radius: 20px;
  width: 60px;
  height: 30px;
  text-align: center;
  padding: 1px 0px;
  margin: 10px 0px;
`;

export const WriteTitle = styled.span`
  margin-bottom: 10px;
`;

export const ReviewSubmit = styled.div`
  margin: 10px 0px;
  width: 100%;
  max-width: 600px;
`;

export const ReviewInput = styled(RegularInput)`
  width: 100%;
  max-width: 500px;
  height: 50px;
  font-size: 16px;
  margin-right: 20px;
`;

export const ReviewBtn = styled.button`
  width: 100%;
  max-width: 80px;
  height: 50px;
  font-size: 16px;
  border-radius: 10px;
`;

export const Reviews = styled.div`
  width: 100%;
`;

export const FilterBtns = styled.div``;
export const FilterBtn = styled.button`
  background: none;
  font-size: 16px;
  border-radius: 8px;
  border: 1px solid black;
  padding: 5px 10px;
  margin-left: 10px;
  margin-top: 30px;
`;

export const ReviewLists = styled.ul`
  width: 100%;
  display: grid;
  flex-wrap: wrap;
  place-items: center;
  padding: 20px;

  @media screen and (min-width: 850px) {
    grid-template-columns: repeat(2, 50%);
    grid-column-gap: 10px;
  }
`;
