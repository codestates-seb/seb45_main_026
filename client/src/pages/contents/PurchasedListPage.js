import React,{useEffect, useState} from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../../components/filters/CategoryFilter";
import PurchasedItem from "../../components/contentListItems/PurchasedItem";
import axios from "axios";

const globalTokens = tokens.global;

const PurchasedListContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 700px;
    background-color: ${globalTokens.White.value};
    border: none;
    gap: ${globalTokens.Spacing28.value}px;
    align-items: start;
    padding: ${globalTokens.Spacing20.value}px;
`
const ListTitle = styled.h2`
    height: 30px;
    width: 100%;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing8.value}px;
    margin-top: ${globalTokens.Spacing20.value}px;
`

export default function PurchasedListPage() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const accessToken = useSelector((state) => state.loginInfo.accessToken);
    const [chnnelList,setChannelList]=useState([])
    useEffect(() => {
        axios
          .get(
            "https://api.itprometheus.net/members/playlists/channels?page=1&size=16",
            {
              headers: { Authorization: accessToken.authorization },
            }
          )
            .then((res) => setChannelList(res.data.data.map(el => {
            return {...el, videos: []}
        })))
            .catch((err) => console.log(err));
    },[])

    return (
      <PageContainer isDark={isDark}>
        <PurchasedListContainer>
          <ListTitle>구매한 강의 목록</ListTitle>
          <CategoryFilter />
          {chnnelList.map((el) => (
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