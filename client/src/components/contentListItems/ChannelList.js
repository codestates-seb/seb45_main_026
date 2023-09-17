import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../filters/CategoryFilter";
import HorizonItem from "./HorizonItem";
import axios from "axios";
import { useSelector,useDispatch } from "react-redux";
import { resetToInitialState,setPage,setMaxPage  } from "../../redux/createSlice/FilterSlice";
import { useToken } from '../../hooks/useToken';
import { useInView } from "react-intersection-observer";
import { Heading5Typo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const ListBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 700px;
    padding: ${globalTokens.Spacing20.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border-radius: 0 0 ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px;
    gap: ${globalTokens.Spacing24.value}px;
`; 
const ListContainer = styled.ul`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: ${globalTokens.Spacing16.value}px;
    margin-bottom: ${globalTokens.Spacing24.value}px;
`
const BottomDiv = styled.div`
  height: 10px;
  width: 10px;
`
const LectureBlank = styled(Heading5Typo)`
  width: 100%;
  margin-top: 200px;
  text-align: center;
`

export default function ChannelList({ channelInfor, accessToken, userId }) {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const filterState = useSelector((state) => state.filterSlice.filter);
  const page = useSelector((state) => state.filterSlice.page);
  const maxPage = useSelector((state) => state.filterSlice.maxPage);
  const refreshToken = useToken();
  const dispatch = useDispatch();
  const [lectures, setLectures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bottomRef, bottomInView] = useInView();

  useEffect(() => {
    return () => {
      dispatch(resetToInitialState());
    };
  }, []);
  useEffect(() => {
    dispatch(setPage(1));
    axios
      .get(
        `https://api.itprometheus.net/channels/${userId}/videos?sort=${
          filterState.sortBy.value
        }&is-purchased=${filterState.isPurchased.value}${
          filterState.category.value
            ? `&category=${filterState.category.value}`
            : ""
        }${
          filterState.isFree.value ? `&free=${filterState.isFree.value}` : ""
        }&page=1`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => {
        dispatch(setMaxPage(res.data.pageInfo.totalPage));
        setLectures(res.data.data);
        setLoading(false);
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  }, [filterState, accessToken,userId]);
  
  useEffect(() => {
    if (lectures !== [] && page !== 1) {
      axios.get(
        `https://api.itprometheus.net/channels/${userId}/videos?sort=${
          filterState.sortBy.value
        }&is-purchased=${filterState.isPurchased.value}${
          filterState.category.value
            ? `&category=${filterState.category.value}`
            : ""
        }${
          filterState.isFree.value ? `&free=${filterState.isFree.value}` : ""
        }&page=${page}`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => {
        setLectures((prev) => [...prev, ...res.data.data]);
        setLoading(false);
      })
      .catch((err) => {
        if(err.response.data.message==='만료된 토큰입니다.') {
          refreshToken();
        } else {
          console.log(err);
        }
      });
    }
  }, [page])
  
  useEffect(() => {
    if (bottomInView && maxPage && page < maxPage) {
      setLoading(true);
      dispatch(setPage(page + 1));
    }
  }, [bottomInView]);

  return (
    <ListBody isDark={isDark}>
      <CategoryFilter filterNum="filters2" />
      <ListContainer>
        {lectures.map((el) => (
          <HorizonItem lecture={el} channel={channelInfor} />
        ))}
        {lectures.length===0?<LectureBlank isDark={isDark}>등록된 영상이 없습니다.</LectureBlank>:<></>}
        {page < maxPage && !loading ? <BottomDiv ref={bottomRef} /> : <></>}
      </ListContainer>
    </ListBody>
  );
}
