import React, { useEffect, useState } from "react";
import axios from "axios";
import { styled } from "styled-components";
import { useToken } from "../../hooks/useToken";
import { useInView } from "react-intersection-observer";
import { useParams } from "react-router";
import { useSelector, useDispatch } from "react-redux";
import {PageContainer,MainContainer,} from "../../atoms/layouts/PageContainer";
import { Heading5Typo } from "../../atoms/typographys/Typographys";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../../components/filters/CategoryFilter";
import ChannelItem from "../../components/contentListItems/ChannelItem";
import HorizonItem from "../../components/contentListItems/HorizonItem";
import arrowLeft from "../../assets/images/icons/arrow/carouselPrev.svg"
import arrowRight from "../../assets/images/icons/arrow/carouselNext.svg"
import { setPage,setMaxPage,resetToInitialState,setSort } from "../../redux/createSlice/FilterSlice";
import SearchSubmit from "../../components/contentListItems/Searchsubmit";
import { BottomDiv } from "./LectureListPage";

const globalTokens = tokens.global;

const ResultMainContainer = styled(MainContainer)`
    min-width: 1000px;
    min-height: 700px;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border: none;
    gap: ${globalTokens.Spacing12.value}px;
    margin-top: ${globalTokens.Spacing40.value}px;
    margin-bottom: ${globalTokens.Spacing40.value}px;
    padding: ${globalTokens.Spacing20.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
`;
const ChannelContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-around;
`
const ChannelArrowLeft = styled.button`
    width: 5%;
    height: 60px;
    min-width: 40px;
    background-image: ${props=>props.hasPrevious?`url(${arrowLeft})`:'none'};
    background-size: contain;
    background-repeat: no-repeat;
    margin-bottom: 50px;
    cursor: ${props=>props.hasPrevious?"pointer":"default"};
`
const ChannelArrowRight = styled.button`
  width: 5%;
  min-width: 40px;
  height: 60px;
  background-image: ${props=>props.hasNext?`url(${arrowRight})`:'none'};
  background-size: contain;
  background-repeat: no-repeat;
  margin-bottom: 50px;
  cursor: ${(props) => (props.hasNext ? "pointer" : "default")};
`;
const ChannelItems = styled.ul`
    width: 80%;
    min-width: 900px;
    height: 350px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
    gap: ${globalTokens.Spacing16.value}px;
`
const ChannelBlank = styled.div`
    font-size: ${globalTokens.Heading5.value}px;
    width: 100%;
    text-align: center;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`
const VideoBlank = styled.div`
    font-size: ${globalTokens.Heading5.value}px;
    width: 100%;
    text-align: center;
    margin-top: 200px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`
const ResultTitle = styled(Heading5Typo)`
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px;
    width: 100%;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing28.value}px;
    margin-top: ${globalTokens.Spacing20.value}px;
    margin: ${globalTokens.Spacing8.value}px;
`
const FilterContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: start;
    padding: 0 ${globalTokens.Spacing16.value}px;
`
const HorizonItemContainer = styled.ul`
  width: 100%;
  min-height: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${globalTokens.Spacing16.value}px;
  margin-bottom: ${globalTokens.Spacing28.value}px;
`;


export default function ResultPage() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const page = useSelector((state) => state.filterSlice.page);
    const maxPage = useSelector((state) => state.filterSlice.maxPage);
    const filterState = useSelector((state)=>state.filterSlice.filter)
    const { keyword } = useParams();
    const dispatch = useDispatch()
    const refreshToken = useToken();
    const [loading,setLoading] = useState(true)
    const [hasNext, setHasNext] = useState(false)
    const [hasPrevious,setHasPrevious] = useState(false)
    const [channelPage, setChannelPage] = useState(1)
    const [channelList, setChannelList] = useState([])
    const [videoList, setVideoList] = useState([])
    const [bottomRef, bottomInView] = useInView();
    
    const previousHandler = () => {
        if (hasPrevious) {
            setChannelPage(channelPage-1)
        }
    }
    const nextHandler = () => {
        if (hasNext) {
            setChannelPage(channelPage+1)
        }
    }
    const channelGetHandler = () => {
        axios.get(
          `https://api.itprometheus.net/search/channels?keyword=${keyword}&page=${channelPage}&size=5`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        ).then(res => {
            setChannelList(res.data.data)
            setHasNext(res.data.pageInfo.hasNext)
            setHasPrevious(res.data.pageInfo.hasPrevious)
        })
        .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      })
    }
    const videoGetHandler = () => {
        dispatch(setPage(1))
        axios.get(
          `https://api.itprometheus.net/search/videos?keyword=${keyword}&page=1&size=16&is-purchased=${
            filterState.isPurchased.value
          }${
            filterState.category.value
              ? `&category=${filterState.category.value}`
              : ""
          }${
            filterState.isSubscribed.value
              ? `&subscribe=${filterState.isSubscribed.value}`
              : ""
          }${
            filterState.isFree.value ? `&free=${filterState.isFree.value}` : ""
          }${
            filterState.sortBy.value ? `&sort=${filterState.sortBy.value}` : ""
          }`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        ).then(res => {
            dispatch(setMaxPage(res.data.pageInfo.totalPage))
            setVideoList(res.data.data)
            setLoading(false)
        }).catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      })      
    }
    useEffect(() => {
        dispatch(setSort({ text: "정확도순", value: "" }));
    return () => {
      dispatch(resetToInitialState());
    };
  }, []);
    useEffect(() => {
        setChannelPage(1)
        channelGetHandler()
    }, [keyword])
    useEffect(() => {
        channelGetHandler()
    },[channelPage])
    useEffect(() => {
        videoGetHandler()
    },[keyword,filterState])
    useEffect(() => {
        if (page !== 1) {
            axios.get(
          `https://api.itprometheus.net/search/videos?keyword=${keyword}&page=${page}&size=16&is-purchased=${
            filterState.isPurchased.value
          }${
            filterState.category.value
              ? `&category=${filterState.category.value}`
              : ""
          }${
            filterState.isSubscribed.value
              ? `&subscribe=${filterState.isSubscribed.value}`
              : ""
          }${
            filterState.isFree.value ? `&free=${filterState.isFree.value}` : ""
          }${
            filterState.sortBy.value ? `&sort=${filterState.sortBy.value}` : ""
          }`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        ).then(res => {
            setVideoList((prev) => [...prev, ...res.data.data]);
            setLoading(false)
        }).catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      }) 
        }
    },[page])
    useEffect(() => {
      if (bottomInView && maxPage && page < maxPage) {
        setLoading(true);
        dispatch(setPage(page + 1));
      }
    }, [bottomInView]);

    return (
      <PageContainer isDark={isDark}>
        <ResultMainContainer isDark={isDark}>
          <ResultTitle isDark={isDark}>검색 결과</ResultTitle>
          <SearchSubmit />
          <ResultTitle isDark={isDark}>채널</ResultTitle>
          <ChannelContainer>
            <ChannelArrowLeft
              hasPrevious={hasPrevious}
              onClick={previousHandler}
            />
            <ChannelItems>
              {channelList.map((el) => (
                <ChannelItem
                  channel={el}
                  isSubscribed={el.isSubscribed}
                  channelGetHandler={channelGetHandler}
                  accessToken={accessToken}
                />
              ))}
              {channelList.length === 0 ? (
                <ChannelBlank isDark={isDark}>
                  조건에 맞는 채널이 없습니다
                </ChannelBlank>
              ) : (
                <></>
              )}
            </ChannelItems>
            <ChannelArrowRight hasNext={hasNext} onClick={nextHandler} />
          </ChannelContainer>
          <ResultTitle isDark={isDark}>강의</ResultTitle>
          <FilterContainer>
            <CategoryFilter filterNum="filters5" />
          </FilterContainer>
          <HorizonItemContainer>
            {videoList.map((el) => (
              <HorizonItem
                key={el.videoId}
                lecture={el}
                channel={el.channel}
                isDark={isDark}
              />
            ))}
            {videoList.length === 0 ? (
              <VideoBlank isDark={isDark}>조건에 맞는 강의가 없습니다</VideoBlank>
            ) : (
              <></>
            )}
            {page < maxPage && !loading ? <BottomDiv ref={bottomRef} /> : <></>}
          </HorizonItemContainer>
        </ResultMainContainer>
      </PageContainer>
    );
}