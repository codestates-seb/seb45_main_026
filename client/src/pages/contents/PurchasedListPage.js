import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import { useSelector, useDispatch } from "react-redux";
import {
  PageContainer,
  MainContainer,
} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../../components/filters/CategoryFilter";
import PurchasedItem from "../../components/contentListItems/PurchasedItem";
import HorizonItem from "../../components/contentListItems/HorizonItem";
import { setIsList } from "../../redux/createSlice/FilterSlice";
import axios from "axios";
import { useToken } from "../../hooks/useToken";
import { Heading5Typo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const PurchasedListContainer = styled(MainContainer)`
  min-width: 600px;
  min-height: 700px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-radius: ${globalTokens.RegularRadius.value}px;
  border: none;
  gap: ${globalTokens.Spacing28.value}px;
  align-items: start;
  margin: ${globalTokens.Spacing40.value}px 0;
  padding: ${globalTokens.Spacing20.value}px;
`;
const ListTitle = styled(Heading5Typo)`
  height: 30px;
  width: 100%;
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  padding-left: ${globalTokens.Spacing8.value}px;
  margin-top: ${globalTokens.Spacing20.value}px;
`;
const SwitchButton = styled.button`
  width: 100px;
  height: 50px;
  border: 1px black solid;
  border-radius: ${globalTokens.RegularRadius.value}px;
`;

export default function PurchasedListPage() {
  const dispatch = useDispatch();
  const refreshToken = useToken();
  const [videolList, setVideoList] = useState([]);
  const [channelList, setChannelList] = useState([]);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const isList = useSelector((state) => state.filterSlice.isList);
  const filterState = useSelector((state) => state.filterSlice.filter);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);

  useEffect(() => {
    if (isList) {
      axios
        .get(
          `https://api.itprometheus.net/members/playlists?page=1&size=16&sort=${filterState.sortBy.value}`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        )
        .then((res) => setVideoList(res.data.data))
        .catch((err) => console.log(err));
    } else if (!isList) {
      axios
        .get(
          "https://api.itprometheus.net/members/playlists/channels?page=1&size=16",
          {
            headers: { Authorization: accessToken.authorization },
          }
        )
        .then((res) => setChannelList(res.data.data))
        .catch((err) => {
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        });
    }
  }, [isList, filterState, accessToken]);
  
  return (
    <PageContainer isDark={isDark}>
      <PurchasedListContainer isDark={isDark}>
        <ListTitle isDark={isDark}>구매한 강의 목록</ListTitle>
        <SwitchButton
          isDark={isDark}
          onClick={() => dispatch(setIsList(!isList))}
        >
          {isList ? "채널별 보기" : "목록으로 보기"}
        </SwitchButton>
        {isList ? <CategoryFilter filterNum="filters3" /> : <></>}
        {isList
          ? videolList.map((el) => (
              <HorizonItem lecture={el} channel={el.channel} />
            ))
          : channelList.map((el) => (
              <PurchasedItem
                key={el.memberId}
                channel={el}
                setChannelList={setChannelList}
              />
            ))}
      </PurchasedListContainer>
    </PageContainer>
  );
}
