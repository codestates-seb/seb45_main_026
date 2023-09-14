import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import NoticeItem from "./NoticeItem";
import NoticeSubmit from "./NoticeSubmit";
import axios from "axios";
import { useSelector, useDispatch } from "react-redux";
import { HomeTitle } from './ChannelHome';
import { useInView } from "react-intersection-observer";
import { resetToInitialState,setPage,setMaxPage } from "../../redux/createSlice/FilterSlice";

const globalTokens = tokens.global;

const NoticeBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border-radius: 0 0 ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px;

`
const NoticeTitle = styled(HomeTitle)`
    /* height: 20px;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px; */
`
const ItemContainer = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing20.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: ${globalTokens.Spacing28.value}px;
`
const Nothing = styled.div`
    width: 100%;
    height: 500px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: ${globalTokens.Heading4.value}px;
`
const BottomDiv = styled.div`
  height: 10px;
  width: 10px;
`;


export default function ChannelNotice({channelInfor,userId}) {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const myId = useSelector((state) => state.loginInfo.myid);
    const page = useSelector((state) => state.filterSlice.page);
    const maxPage = useSelector((state) => state.filterSlice.maxPage);
    const dispatch=useDispatch()
    const [notices, setNotices] = useState([])
    const [bottomRef, bottomInView] = useInView();
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
      return () => {
        dispatch(resetToInitialState());
      };
    }, []);
    const getHandler = (userId) => {
        dispatch(setPage(1))
       axios.get(
          `https://api.itprometheus.net/channels/${userId}/announcements?page=1&size=10`
        )
        .then((res) => {
          dispatch(setMaxPage(res.data.pageInfo.totalPage));
          setNotices(res.data.data);
          setLoading(false);
        })
        .catch((err) => console.log(err));
    };
    useEffect(() => {
        getHandler(userId)
    }, [])
    useEffect(() => {
        if (page !== 1) {
            axios.get(`https://api.itprometheus.net/channels/${userId}/announcements?page=${page}&size=10`)
                .then(res => {
                    setNotices((prev) => [...prev, ...res.data.data])
                    setLoading(false)
                })
            .catch(err=>console.log(err))
        }
    }, [page])

    useEffect(() => {
      if (bottomInView && maxPage && page < maxPage) {
        setLoading(true);
        dispatch(setPage(page + 1));
      }
    }, [bottomInView]);

    return (
      <NoticeBody isDark={isDark}>
        <NoticeTitle isDark={isDark}>커뮤니티</NoticeTitle>
        <ItemContainer>
          {myId === Number(userId) ? (
            <NoticeSubmit
              userId={userId}
              accessToken={accessToken}
              getHandler={getHandler}
              todo="post"
            />
          ) : (
            <></>
          )}
          {notices.map((el) => (
            <NoticeItem
              key={el.announcementId}
              channelInfor={channelInfor}
              accessToken={accessToken}
              notice={el}
              getHandler={getHandler}
              userId={userId}
            />
          ))}
          {notices.length === 0 ? (
            <Nothing>등록된 공지사항이 없습니다.</Nothing>
          ) : (
            <></>
          )}
          {page < maxPage && !loading ? <BottomDiv ref={bottomRef} /> : <></>}
        </ItemContainer>
      </NoticeBody>
    );
}