import { styled } from "styled-components";
import { RegularInput } from "../../../atoms/inputs/Inputs";
import ReviewStar from "../../../components/DetailPage/ReviewStar";

export const ReviewInput = styled(RegularInput)`
  width: 500px;
`;

const DetailReview = () => {
  return (
    <>
      <div>수강평 27</div>
      <div>
        <div>
          <div>리뷰</div>
          <div>별점을 선택해주세요.</div>
          <ReviewStar />
          <div>
            <ReviewInput placeholder="감상평을 등록해주세요." />
            <button>등록</button>
          </div>
        </div>
        <div>
          <div>
            <button>최신순 ↑↓</button>
            <button>별점순 ↑↓</button>
            <button>
              <span>별점별</span>
              <img src="" alt="" />
            </button>
          </div>
          <ul>
            <li>
              <buttton>수정</buttton>
              <buttton>삭제</buttton>
              <div>☆☆☆☆☆</div>
              <div>중간에 루즈한 부분도 있었지만 재밌게 잘봤습니다.</div>
              <div>
                <span>김둥구</span>
                <span>2023.08.26</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </>
  );
};

export default DetailReview;
