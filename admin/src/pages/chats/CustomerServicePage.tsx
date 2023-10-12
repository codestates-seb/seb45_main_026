import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import { PageTitle } from "../../styles/PageTitle";
import MyChatLists from "../../components/CustomerService/MyChatLists";
import AllChatLists from "../../components/CustomerService/AllChatLists";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { DarkMode } from "../../types/reportDataType";

const CustomerServicePage: React.FC = () => {
  const navigate = useNavigate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const isLogin = useSelector((state: RootState) => state.loginInfo.isLogin);

  useEffect(() => {
    if (!isLogin) {
      navigate("/login");
      return;
    }
  }, []);

  return (
    <PageContainer isDark={isDark}>
      <MainContainer isDark={isDark}>
        <PageTitle isDark={isDark}>고객센터</PageTitle>
        <ChatTableBox>
          <ChatType isDark={isDark}>나의 채팅방</ChatType>
          <MyChatLists />
        </ChatTableBox>
        <ChatTableBox>
          <ChatType isDark={isDark}>대기중인 채팅방</ChatType>
          <AllChatLists />
        </ChatTableBox>
      </MainContainer>
    </PageContainer>
  );
};

export default CustomerServicePage;

const globalTokens = tokens.global;

export const ChatTableBox = styled.section`
  width: 90%;
  padding: 20px 0px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const ChatType = styled(BodyTextTypo)`
  width: 100%;
  max-width: 900px;
  font-weight: ${globalTokens.Bold.value};
`;

export const Customertable = styled.table`
  margin: 30px 0px 30px 0px;
`;

export const CustomerTr = styled.tr<DarkMode>`
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`;
export const Customerth = styled.th<DarkMode>`
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  padding: 15px 0px;
  text-align: center;
`;
export const Customertd = styled.td<DarkMode>`
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  padding: 15px 0px;
  text-align: center;
`;

export const CustomerHead = styled.thead``;
export const CustomerBody = styled.tbody``;

export const CustomerIdth = styled(Customerth)`
  width: 80px;
`;
export const CustomerNameth = styled(Customerth)`
  width: 220px;
`;
export const CustomerEmailth = styled(Customerth)`
  width: 220px;
`;
export const InquireDateth = styled(Customerth)`
  width: 250px;
`;
export const ChatBlockth = styled(Customerth)`
  width: 120px;
`;

export const CustomerIdtd = styled(Customertd)`
  width: 80px;
`;
export const CustomerNametd = styled(Customertd)`
  width: 220px;
`;
export const CustomerEmailtd = styled(Customertd)`
  width: 220px;
`;
export const InquireDatetd = styled(Customertd)`
  width: 250px;
`;
export const ChatBlocktd = styled(Customertd)`
  width: 120px;
`;
