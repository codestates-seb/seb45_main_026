import { useState } from "react";
import { styled } from "styled-components";
import starYellow from "../../assets/images/icons/star/starYellow.svg";
import starWhite from "../../assets/images/icons/star/starWhite.svg";

export const StarImg = styled.img.attrs((props) => ({
  src: `${props.isStar ? starYellow : starWhite}`,
}))`
  width: 30px;
  height: 30px;
`;

const ReviewStar = () => {
  const [isStar, setStar] = useState({
    1: false,
    2: false,
    3: false,
    4: false,
    5: false,
  });

  return (
    <div>
      <StarImg
        isStar={isStar[1]}
        onClick={() => setStar({ ...isStar, 1: !isStar[1] })}
      />
      <StarImg
        isStar={isStar[2]}
        onClick={() => setStar({ ...isStar, 2: !isStar[2] })}
      />
      <StarImg
        isStar={isStar[3]}
        onClick={() => setStar({ ...isStar, 3: !isStar[3] })}
      />
      <StarImg
        isStar={isStar[4]}
        onClick={() => setStar({ ...isStar, 4: !isStar[4] })}
      />
      <StarImg
        isStar={isStar[5]}
        onClick={() => setStar({ ...isStar, 5: !isStar[5] })}
      />
    </div>
  );
};

export default ReviewStar;
