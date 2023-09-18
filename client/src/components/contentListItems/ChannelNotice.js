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
import { Heading5Typo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const NoticeBody = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing20.value}px;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border-radius: 0 0 ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px;

`
const NoticeTitle = styled(HomeTitle)`
    margin-left: ${globalTokens.Spacing40.value}px;
    margin-bottom: 0;
`
const ItemContainer = styled.div`
    width: 100%;
    min-height: 600px;
    padding: ${globalTokens.Spacing20.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: ${globalTokens.Spacing28.value}px;
`
const Nothing = styled(Heading5Typo)`
    width: 100%;
    margin-top: 200px;
    text-align: center;
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
            <Nothing isDark={isDark}>등록된 공지사항이 없습니다.</Nothing>
          ) : (
            <></>
          )}
          {page < maxPage && !loading ? <BottomDiv ref={bottomRef} /> : <></>}
        </ItemContainer>
      </NoticeBody>
    );
}