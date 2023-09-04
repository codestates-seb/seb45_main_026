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
  const [isStar, setStar] = useState({ star: 0 });

  return (
    <div>
      <StarImg
        isStar={isStar.star >= 1}
        onClick={() => setStar({ ...isStar, star: 1 })}
      />
      <StarImg
        isStar={isStar.star >= 2}
        onClick={() => setStar({ ...isStar, star: 2 })}
      />
      <StarImg
        isStar={isStar.star >= 3}
        onClick={() => setStar({ ...isStar, star: 3 })}
      />
      <StarImg
        isStar={isStar.star >= 4}
        onClick={() => setStar({ ...isStar, star: 4 })}
      />
      <StarImg
        isStar={isStar.star >= 5}
        onClick={() => setStar({ ...isStar, star: 5 })}
      />
    </div>
  );
};

export default ReviewStar;
