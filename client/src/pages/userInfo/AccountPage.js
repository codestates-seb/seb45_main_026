import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import axios from "axios";
import styled from "styled-components";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import {
  RewardContentContainer,
  RewardMainContainer,
  RewardTitle,
} from "./RewardPage";
import RewardCategory from "../../components/rewardPage/RewardCategory";
import tokens from "../../styles/tokens.json";
import { BigButton } from "../../atoms/buttons/Buttons";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { RegularInput } from "../../atoms/inputs/Inputs";

const AccountPage = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [editMode, setEditMode] = useState(false);
  const [accountData, setAccountData] = useState({
    name: "",
    account: "",
    bank: "",
  });

  const handleInsertData = (e, type) => {
    switch (type) {
      case "name":
        setAccountData({ ...accountData, name: e.target.value });
        return;

      case "account":
        setAccountData({ ...accountData, account: e.target.value });
        return;

      case "bank":
        setAccountData({ ...accountData, bank: e.target.value });
        return;

      default:
        return;
    }
  };

  const getAccountData = () => {
    return axios
      .get(`https://api.itprometheus.net/adjustments/account`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res.data.data);
        if (
          res.data.data.name !== "계좌 정보가 없습니다." &&
          res.data.data.account !== "계좌 정보가 없습니다." &&
          res.data.data.bank !== "계좌 정보가 없습니다."
        ) {
          setAccountData({ ...accountData, ...res.data.data });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const putAccountData = () => {
    return axios
      .put(`https://api.itprometheus.net/adjustments/account`, accountData, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res.data);
        setEditMode(false);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getAccountData();
  }, []);

  return (
    <PageContainer isDark={isDark}>
      <RewardMainContainer isDark={isDark}>
        <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
        <RewardCategory category="account" />
        <RewardContentContainer>
          <AccountContainer>
            <AccountInfoTitle>
              <AccountTitle>세부 정보</AccountTitle>
            </AccountInfoTitle>
            <AccountInfoBox>
              <AccountInfoSubtitle>예금주</AccountInfoSubtitle>
              {!editMode ? (
                <>{accountData.name || "등록된 예금주가 없습니다."}</>
              ) : (
                <AccountInput
                  placeholder="예금주를 입력해주세요."
                  value={accountData.name}
                  onChange={(e) => handleInsertData(e, "name")}
                />
              )}
            </AccountInfoBox>
            <AccountInfoBox>
              <AccountInfoSubtitle>계좌번호</AccountInfoSubtitle>
              {!editMode ? (
                <>{accountData.account || "등록된 계좌 정보가 없습니다."}</>
              ) : (
                <AccountInput
                  placeholder="계좌를 입력해주세요."
                  value={accountData.account}
                  onChange={(e) => handleInsertData(e, "account")}
                />
              )}
            </AccountInfoBox>
            <AccountInfoBox>
              <AccountInfoSubtitle>은행</AccountInfoSubtitle>
              {!editMode ? (
                <>{accountData.bank || "등록된 은행이 없습니다."}</>
              ) : (
                <AccountInput
                  placeholder="은행을 선택해주세요."
                  value={accountData.bank}
                  onChange={(e) => handleInsertData(e, "bank")}
                />
              )}
            </AccountInfoBox>
            {editMode ? (
              <AccountBtn
                onClick={(e) => {
                  e.preventDefault();
                  putAccountData();
                }}
              >
                등록하기
              </AccountBtn>
            ) : (
              <AccountBtn
                onClick={(e) => {
                  e.preventDefault();
                  setEditMode(true);
                }}
              >
                수정하기
              </AccountBtn>
            )}
          </AccountContainer>
          <AccountInfo>
            해당 정보와 본인의 정보가 다를 경우 발생하는 문제는 책임지지
            않습니다.
          </AccountInfo>
        </RewardContentContainer>
      </RewardMainContainer>
    </PageContainer>
  );
};

export default AccountPage;

const globalTokens = tokens.global;

export const AccountContainer = styled.form`
  width: 100%;
  max-width: 500px;
  padding: 20px 30px;
  margin: 15px 0px;
  border-radius: ${globalTokens.Spacing8.value}px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};

  display: flex;
  flex-direction: column;
  justify-content: start;
`;

export const AccountInfoTitle = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin: 10px 0px;
  padding-bottom: 10px;
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  font-weight: bold;
`;

export const AccountTitle = styled(BodyTextTypo)``;

export const AccountInfoBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  margin: 10px 0px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const AccountInfoSubtitle = styled(BodyTextTypo)`
  width: 80px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const AccountInfoContnent = styled(BodyTextTypo)`
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const AccountInput = styled(RegularInput)`
  width: 100%;
  height: 45px;
  margin-left: 10px;
  padding-left: 10px;
  text-align: start;
`;

export const AccountInfo = styled.div`
  width: 100%;
  max-width: 500px;
  padding: 10px 20px;
  margin: 10px 0px 25px 0px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-radius: ${globalTokens.RegularRadius.value}px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  font-size: ${globalTokens.SmallText.value}px;
`;

export const AccountBtn = styled(BigButton)`
  width: 100%;
  height: 45px;
  border-radius: 8px;
  margin-top: 10px;
  font-weight: 600;
  font-size: 16px;
`;
