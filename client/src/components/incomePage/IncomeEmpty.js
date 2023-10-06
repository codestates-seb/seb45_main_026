import { Link } from "react-router-dom";
import { styled } from "styled-components";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { BigButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const IncomeEmpty = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const myid = useSelector((state) => state.loginInfo.myid);

  return (
    <IncomeEmptyBox>
      <IncomeEmptyGuide isDark={isDark}>
        등록된 계좌 정보가 없습니다.
      </IncomeEmptyGuide>
      <IncomeEmptyGuide isDark={isDark}>
        {"'내 채널 > 설정 > 내 계좌 정보' 에서 설정해주세요."}
      </IncomeEmptyGuide>
      <Link to={`/channels/${myid}`}>
        <SettingNavBtn isDark={isDark}>'내 채널'로 이동하기</SettingNavBtn>
      </Link>
    </IncomeEmptyBox>
  );
};

export default IncomeEmpty;

export const IncomeEmptyBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  padding: 200px 0px;
`;

export const IncomeEmptyGuide = styled(BodyTextTypo)`
  width: 100%;
  max-width: 500px;
  text-align: center;
  margin: 5px 0px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const SettingNavBtn = styled(BigButton)`
  width: 100%;
  max-width: 400px;
  height: 45px;
  margin: 10px 0px;
  border-radius: 8px;
`;
