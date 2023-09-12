import { styled } from "styled-components";
import starWhite from "../../assets/images/icons/star/starWhite.svg";
import starYellow from "../../assets/images/icons/star/starYellow.svg";

const StarsBody = styled.div`
    display: flex;
    flex-direction: row;
    position: absolute;
    height: 100%;
`
const StarImage = styled.img`
    height: 100%;
`;
const YellowStars = styled.div`
    height: 100%;
    width: ${(props) => props.score * 20}%;
    display: flex;
    flex-direction: row;
    position: absolute;
    overflow: hidden;
    z-index: 1;
`

export default function Stars({score}) {
    return (
      <>
        <YellowStars score={score}>
          <StarImage src={starYellow} />
          <StarImage src={starYellow} />
          <StarImage src={starYellow} />
          <StarImage src={starYellow} />
          <StarImage src={starYellow} />
        </YellowStars>
        <StarsBody>
          <StarImage src={starWhite} />
          <StarImage src={starWhite} />
          <StarImage src={starWhite} />
          <StarImage src={starWhite} />
          <StarImage src={starWhite} />
        </StarsBody>
      </>
    );
}