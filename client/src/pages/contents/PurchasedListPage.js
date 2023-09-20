import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import { useSelector, useDispatch } from "react-redux";
import {PageContainer,MainContainer,} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../../components/filters/CategoryFilter";
import PurchasedItem from "../../components/contentListItems/PurchasedItem";
import HorizonItem from "../../components/contentListItems/HorizonItem";
import { setIsList,setPage,setMaxPage } from "../../redux/createSlice/FilterSlice";
import axios from "axios";
import { useToken } from "../../hooks/useToken";
import { Heading5Typo } from "../../atoms/typographys/Typographys";
import { RoundButton } from "../../atoms/buttons/Buttons";
import { useInView } from "react-intersection-observer";

const globalTokens = tokens.global;

const PurchasedListContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 700px;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: none;
    gap: ${globalTokens.Spacing28.value}px;
    align-items: center;
    margin: ${globalTokens.Spacing40.value}px 0;
    padding: ${globalTokens.Spacing20.value}px;
`
const ListTitle = styled(Heading5Typo)`
    height: 30px;
    width: 100%;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing28.value}px;
    margin-top: ${globalTokens.Spacing20.value}px;
    margin: ${globalTokens.Spacing8.value}px;
`
const CategoryContainer = styled.div`
  width: 95%;
  display: flex;
  flex-direction: row;
  gap: ${globalTokens.Spacing8.value}px;
  justify-content: start;
`
const SwitchButton = styled(RoundButton)`
    height: 41px;
    border: 1px ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value} solid;
    background-color: rgba(0,0,0,0);
    &:hover {
      color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
      background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':'rgba(0,0,0,0.15)'};
    }
`
const BottomDiv = styled.div`
    height: 10px;
    width: 10px;
`;

export default function PurchasedListPage() {
  const page = useSelector((state) => state.filterSlice.page);
  const maxPage = useSelector((state) => state.filterSlice.maxPage);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const isList = useSelector((state) => state.filterSlice.isList);
  const filterState = useSelector((state) => state.filterSlice.filter);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const dispatch = useDispatch();
  const refreshToken = useToken();
  const [bottomRef, bottomInView] = useInView();
  const [videolList, setVideoList] = useState([]);
  const [channelList, setChannelList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dispatch(setPage(1))
    if (isList) {
      axios
        .get(
          `https://api.itprometheus.net/members/playlists?page=1&size=16&sort=${filterState.sortBy.value}`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        )
        .then((res) => {
          setVideoList(res.data.data);
          dispatch(setMaxPage(res.data.pageInfo.totalPage));
          setLoading(false);
        })
        .catch((err) => {
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        });
      } else if(!isList){
        axios
          .get(
            "https://api.itprometheus.net/members/playlists/channels?page=1&size=16",
            {
              headers: { Authorization: accessToken.authorization },
            }
          )
          .then((res) => {
            setChannelList(res.data.data)
            dispatch(setMaxPage(res.data.pageInfo.totalPage));
            setLoading(false)
          })
            .catch((err) => {
              if(err.response.data.message==='만료된 토큰입니다.') {
                refreshToken();
              } else {
                console.log(err);
              }
            });
      }
    },[isList,filterState,accessToken])
  
  useEffect(() => {
    if (isList&&page!==1&&videolList!==[]) {
      axios
        .get(
          `https://api.itprometheus.net/members/playlists?page=${page}&size=16&sort=${filterState.sortBy.value}`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        )
        .then((res) => {
          setVideoList((prev) => [...prev, ...res.data.data]);
          setLoading(false);
        })
        .catch((err) => {
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        });
      } else if(!isList&&page!==1&&channelList!==[]){
        axios
          .get(
            `https://api.itprometheus.net/members/playlists/channels?page=${page}&size=16`,
            {
              headers: { Authorization: accessToken.authorization },
            }
          )
          .then((res) => {
            setChannelList((prev) => [...prev, ...res.data.data]);
            setLoading(false)
          })
            .catch((err) => {
              if(err.response.data.message==='만료된 토큰입니다.') {
                refreshToken();
              } else {
                console.log(err);
              }
            });
      }
  },[page])
  useEffect(() => {
    if (bottomInView && maxPage && page < maxPage) {
      setLoading(true);
      dispatch(setPage(page + 1));
    }
  }, [bottomInView]);

  useEffect(() => {
    window.scrollTo({
      top: 0,
    });
  }, []);
  
    return (
      <PageContainer isDark={isDark}>
        <PurchasedListContainer isDark={isDark}>
          <ListTitle isDark={isDark}>구매한 강의 목록</ListTitle>
          <CategoryContainer>
            <SwitchButton
              isDark={isDark}
              onClick={() => dispatch(setIsList(!isList))}
            >
              {isList ? "채널별 보기" : "목록으로 보기"}
            </SwitchButton>

            {isList ? <CategoryFilter filterNum="filters3" /> : <></>}
          </CategoryContainer>
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
          {page < maxPage && !loading ? <BottomDiv ref={bottomRef} /> : <></>}
        </PurchasedListContainer>
      </PageContainer>
    );
}