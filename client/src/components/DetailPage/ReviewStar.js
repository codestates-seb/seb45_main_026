import { styled } from "styled-components";
import { ReactComponent as star_lefthalf } from "../../assets/images/icons/star/star_lefthalf.svg";
import { ReactComponent as star_righthalf } from "../../assets/images/icons/star/star_righthalf.svg";

const ReviewStar = ({ isStar, setStar }) => {
  const reviewStars = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  return (
    <StarBox>
      {reviewStars.map((el, idx) => {
        if (el % 2 !== 0 && el > isStar.star) {
          return (
            <StarLeftWhite
              key={idx}
              onClick={() => setStar({ ...isStar, star: el })}
            />
          );
        } else if (el % 2 !== 0 && el <= isStar.star) {
          return (
            <StarLeftYellow
              key={idx}
              onClick={() => setStar({ ...isStar, star: el })}
            />
          );
        } else if (el % 2 === 0 && el > isStar.star) {
          return (
            <StarRightWhite
              key={idx}
              onClick={() => setStar({ ...isStar, star: el })}
            />
          );
        } else if (el % 2 === 0 && el <= isStar.star) {
          return (
            <StarRightYellow
              key={idx}
              onClick={() => setStar({ ...isStar, star: el })}
            />
          );
        }
      })}
    </StarBox>
  );
};

export default ReviewStar;

export const StarBox = styled.div``;

export const StarLeftWhite = styled(star_lefthalf)`
  width: 15px;
  height: 30px;
  margin-left: 2px;
  &:hover {
    path {
      stroke: black;
    }
  }
  &:active {
    path {
      fill: #ffe072;
    }
  }
`;

export const StarRightWhite = styled(star_righthalf)`
  width: 15px;
  height: 30px;
  margin-right: 2px;
  &:hover {
    path {
      stroke: black;
    }
  }
  &:active {
    path {
      fill: #ffe072;
    }
  }
`;

export const StarLeftYellow = styled(star_lefthalf)`
  width: 15px;
  height: 30px;
  margin-left: 2px;
  path {
    fill: #ffc700;
  }
`;

export const StarRightYellow = styled(star_righthalf)`
  width: 15px;
  height: 30px;
  margin-right: 2px;
  path {
    fill: #ffc700;
  }
`;
