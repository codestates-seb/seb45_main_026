import { useState } from "react";
import { styled } from "styled-components";
import { ReactComponent as starYellow } from "../../assets/images/icons/star/starYellow.svg";
import { ReactComponent as starWhite } from "../../assets/images/icons/star/starWhite.svg";

const ReviewStar = () => {
  const [isStar, setStar] = useState({ star: 0 });
  const reviewStars = [1, 2, 3, 4, 5];

  return (
    <StarBox>
      {reviewStars.map((el) => {
        if (el <= isStar.star) {
          return <StarYellow onClick={() => setStar({ ...isStar, star: 0 })} />;
        } else {
          return <StarWhite onClick={() => setStar({ ...isStar, star: el })} />;
        }
      })}
    </StarBox>
  );
};

export default ReviewStar;

export const StarBox = styled.div``;

export const StarYellow = styled(starYellow)`
  width: 30px;
  height: 30px;

  /* path {
    stroke-width: 2px;
    stroke: gray;
  } */
`;

export const StarWhite = styled(starWhite)`
  width: 30px;
  height: 30px;

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
