import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import { useSelector, useDispatch } from "react-redux";
import {PageContainer,MainContainer,} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import ChannelItem from "../../components/contentListItems/ChannelItem";
import { useInView } from "react-intersection-observer";
import { setPage,setMaxPage  } from "../../redux/createSlice/FilterSlice";
import { HomeTitle } from "../../components/contentListItems/ChannelHome";
import { useToken } from "../../hooks/useToken";
import { BottomDiv } from "./LectureListPage";
import { Heading5Typo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const ChannelListContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 600px;
    border: none;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    margin-top: ${globalTokens.Spacing40.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    padding: ${globalTokens.Spacing20.value}px;
    gap: ${globalTokens.Spacing28.value}px;
`
export const ListTitle = styled(HomeTitle)`
  width: 100%;
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  padding-left: ${globalTokens.Spacing28.value}px;
  margin-top: ${globalTokens.Spacing20.value}px;
  margin: ${globalTokens.Spacing8.value}px;
`;
const ItemContainer = styled.ul`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: start;
    flex-wrap: wrap;
    gap: ${globalTokens.Spacing16.value}px;
`
const ChannelBlank = styled(Heading5Typo)`
  width: 100%;
  margin-top: 160px;
  text-align: center;
`

export default function ChannelListPage() {
    const dispatch=useDispatch()
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const page = useSelector((state) => state.filterSlice.page);
    const maxPage = useSelector((state) => state.filterSlice.maxPage);
    const accessToken = useSelector((state) => state.loginInfo.accessToken);
    const refreshToken = useToken();
    const [bottomRef, bottomInView] = useInView();
    const [channels, setChannels] = useState([])
    const [loading,setLoading] = useState(true)
  const channelGetHandler = () => {
      return null
    }
    const getChannels = () => {
        dispatch(setPage(1))
        axios.get("https://api.itprometheus.net/members/subscribes?page=1&size=12", {
          headers: { Authorization: accessToken.authorization },
        }).then(res => {
            dispatch(setMaxPage(res.data.pageInfo.totalPage));
            setChannels(res.data.data)
            setLoading(false)
        }).catch((err) => {
        if(err.response.data.message==='만료된 토큰입니다.') {
          refreshToken();
        } else {
          console.log(err.response.data.message);
        }
      })
    }
    useEffect(() => {
        if (page !== 1) {
            axios
              .get(
                `https://api.itprometheus.net/members/subscribes?page=${page}&size=12`,
                {
                  headers: { Authorization: accessToken.authorization },
                }
              )
              .then((res) => {
                setChannels((prev) => [...prev, ...res.data.data]);
                setLoading(false);
              })
              .catch((err) => {
                if (err.response.data.message === "만료된 토큰입니다.") {
                  refreshToken();
                } else {
                  console.log(err.response.data.message);
                }
              });
        }
    },[page])
    useEffect(() => {
        getChannels()
    }, [])
    useEffect(() => {
      if (bottomInView && maxPage && page < maxPage) {
        setLoading(true);
        dispatch(setPage(page + 1));
      }
    }, [bottomInView]);
    
    return (
      <PageContainer isDark={isDark}>
        <ChannelListContainer isDark={isDark}>
          <ListTitle isDark={isDark}>구독한 채널 목록</ListTitle>
          <ItemContainer>
            {channels.map((el) => (
              <ChannelItem
                key={el.memberId}
                accessToken={accessToken}
                refreshToken={refreshToken}
                channel={el}
                getChannels={getChannels}
                channelGetHandler={channelGetHandler}
              />
            ))}
            {channels.length===0?<ChannelBlank isDark={isDark}>구독한 채널이 없습니다.</ChannelBlank>:<></>}
          </ItemContainer>
          {page < maxPage && !loading ? <BottomDiv ref={bottomRef} /> : <></>}
        </ChannelListContainer>
      </PageContainer>
    );
}