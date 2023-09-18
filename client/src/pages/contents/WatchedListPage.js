import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import { useSelector, useDispatch } from "react-redux";
import {PageContainer,MainContainer,} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import { useInView } from "react-intersection-observer";
import { setPage, setMaxPage } from "../../redux/createSlice/FilterSlice";
import { HomeTitle } from "../../components/contentListItems/ChannelHome";
import { useToken } from "../../hooks/useToken";
import { BottomDiv } from "./LectureListPage";
import VerticalItem from "../../components/contentListItems/VerticalItem";
import CategoryFilter from "../../components/filters/CategoryFilter";

const globalTokens = tokens.global;

const WatchedContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 600px;
    padding: ${globalTokens.Spacing20.value}px;
    border: none;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    margin-top: ${globalTokens.Spacing40.value}px;
    margin-bottom: ${globalTokens.Spacing40.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    padding: ${globalTokens.Spacing20.value}px;
    gap: ${globalTokens.Spacing12.value}px;
`
const WatchedTitle = styled(HomeTitle)`
    width: 100%;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing4.value}px;
    margin-top: ${globalTokens.Spacing20.value}px;
    margin: ${globalTokens.Spacing8.value}px;
`
const VerticalItemContainer = styled.ul`
    width: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
    gap: ${globalTokens.Spacing12.value}px;
    margin-bottom: ${globalTokens.Spacing28.value}px;
`
const FilterContainer = styled.div`
    width: 100%;
    display: flex;
    justify-content: start;
`

export default function WatchedListPage() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const accessToken = useSelector((state) => state.loginInfo.accessToken);
    const page = useSelector((state) => state.filterSlice.page);
    const maxPage = useSelector((state) => state.filterSlice.maxPage);
    const filterDate = useSelector((state)=>state.filterSlice.filter.watchedDate)
    const refreshToken = useToken();
    const dispatch = useDispatch();
    const [bottomRef, bottomInView] = useInView();
    const [loading, setLoading] = useState(true);
    const [Lectures,setLectures] = useState([])
    
    const getWatchedList = () => {
        dispatch(setPage(1))
        axios.get(`https://api.itprometheus.net/members/watchs?page=1&size=16&day=${filterDate.value}`, {
          headers: { Authorization: accessToken.authorization },
        }).then(res => {
            dispatch(setMaxPage(res.data.pageInfo.totalPage))
            setLectures(res.data.data);
            setLoading(false)
        }).catch((err) => 
        {
          if(err.response.data.message==='만료된 토큰입니다.') {
            refreshToken();
          } else {
            console.log(err);
          }
        })
    }
    useEffect(() => {
      getWatchedList();
    }, [filterDate]);
    useEffect(() => {
        if (page !== 1) {
            axios.get(
              `https://api.itprometheus.net/members/watchs?page=${page}&size=16&day=${filterDate.value}`,
              {
                headers: { Authorization: accessToken.authorization },
              }
            ).then(res => {
                setLectures((prev) => [...prev, ...res.data.data])
                setLoading(false)
            }).catch((err) => 
        {
          if(err.response.data.message==='만료된 토큰입니다.') {
            refreshToken();
          } else {
            console.log(err);
          }
        })
        }
    }, [page])
    useEffect(() => {
      if (bottomInView && maxPage && page < maxPage) {
        setLoading(true);
        dispatch(setPage(page + 1));
      }
    }, [bottomInView]);

    return (
        <PageContainer isDark={isDark} >
            <WatchedContainer isDark={isDark}>
                <WatchedTitle isDark={isDark}>시청한 강의 목록</WatchedTitle>
                <FilterContainer>
                    <CategoryFilter filterNum="filters4"/>
                </FilterContainer>
                <VerticalItemContainer>
                    {Lectures.map(el => <VerticalItem lecture={el} channel={el.channel} />)}
                    {page < maxPage&&!loading?<BottomDiv ref={bottomRef}/>:<></>}
                </VerticalItemContainer>
            </WatchedContainer>
        </PageContainer>
    )
}