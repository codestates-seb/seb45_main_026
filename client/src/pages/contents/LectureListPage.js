import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import {PageContainer,MainContainer} from "../../atoms/layouts/PageContainer";
import { useDispatch, useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../../components/filters/CategoryFilter";
import HorizonItem from "../../components/contentListItems/HorizonItem";
import VerticalItem from "../../components/contentListItems/VerticalItem";
import { setLocation } from "../../redux/createSlice/UISettingSlice";
import axios from "axios";
import { resetToInitialState,setIsHorizon,setPage,setMaxPage } from "../../redux/createSlice/FilterSlice";
import list from '../../assets/images/icons/listItem/list.svg'
import grid from '../../assets/images/icons/listItem/grid.svg'
import { HomeTitle } from '../../components/contentListItems/ChannelHome';
import { useToken } from '../../hooks/useToken';
import { useInView } from "react-intersection-observer";
import SearchSubmit from "../../components/contentListItems/Searchsubmit";

const globalTokens = tokens.global;

const LectureMainContainer = styled(MainContainer)`
  min-width: 600px;
  min-height: 700px;
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
  border: none;
  gap: ${globalTokens.Spacing12.value}px;
  margin-top: ${globalTokens.Spacing40.value}px;
  margin-bottom: ${globalTokens.Spacing40.value}px;
  padding: ${globalTokens.Spacing20.value}px;
  border-radius: ${globalTokens.RegularRadius.value}px;
`;
const ListTitle = styled(HomeTitle)`
  width: 100%;
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  padding-left: ${globalTokens.Spacing28.value}px;
  margin-top: ${globalTokens.Spacing20.value}px;
  margin: ${globalTokens.Spacing8.value}px;
`;
const FilterContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: start;
  padding: 0 ${globalTokens.Spacing16.value}px;
`;
const StructureButton = styled.button`
  width: 35px;
  height: 35px;
  background-image: ${(props) => (props.isHorizon ? `url(${list})` : `url(${grid})` )};
  background-size: contain;
  background-repeat: no-repeat;
  border-radius: ${globalTokens.RegularRadius.value}px;
`;
const HorizonItemContainer = styled.ul`
  width: 100%;
  min-height: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${globalTokens.Spacing16.value}px;
  margin-bottom: ${globalTokens.Spacing28.value}px;
`;
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
export const BottomDiv = styled.div`
  height: 10px;
  width: 10px;
`

const LectureListPage = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const filterState = useSelector((state) => state.filterSlice.filter);
  const isHorizon = useSelector((state) => state.filterSlice.isHorizon);
  const accessToken = useSelector(state=>state.loginInfo.accessToken);
  const page = useSelector((state) => state.filterSlice.page);
  const maxPage = useSelector((state) => state.filterSlice.maxPage);
  const refreshToken = useToken();
  const dispatch = useDispatch();
  const [bottomRef, bottomInView] = useInView()
  const [lectures, setLectures] = useState([]);
  const [loading,setLoading]=useState(true)
  
  useEffect(() => {
    return () => {
      dispatch(resetToInitialState());
    };
  }, []);
  
  //초기에 값을 불러옴
  useEffect(() => {
    dispatch(setPage(1));
    axios
      .get(
        `https://api.itprometheus.net/videos?sort=${
          filterState.sortBy.value
        }&is-purchased=${filterState.isPurchased.value}${
          filterState.category.value
            ? `&category=${filterState.category.value}`
            : ""
        }${
          filterState.isSubscribed.value
            ? `&subscribe=${filterState.isSubscribed.value}`
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
  }, [filterState, accessToken]);

  //page state가 변경되면 데이터를 추가로 불러옴
  useEffect(() => {
    if (page !== 1) {
      axios.get(
        `https://api.itprometheus.net/videos?sort=${
          filterState.sortBy.value
        }&is-purchased=${filterState.isPurchased.value}${
          filterState.category.value
            ? `&category=${filterState.category.value}`
            : ""
        }${
          filterState.isSubscribed.value
            ? `&subscribe=${filterState.isSubscribed.value}`
            : ""
        }${
          filterState.isFree.value ? `&free=${filterState.isFree.value}` : ""
        }&page=${page}`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => {
        setLectures((prev) => [...prev, ...res.data.data])
        setLoading(false)
      })
      .catch((err) => 
        {
          if(err.response.data.message==='만료된 토큰입니다.') {
            refreshToken();
          } else {
            console.log(err);
          }
        }
      );}
  }, [page])

  //
  useEffect(() => {
    if (bottomInView&&maxPage && (page < maxPage)) {
      setLoading(true)
      dispatch(setPage(page+1))
    }
  },[bottomInView])
  
  return (
    <PageContainer isDark={isDark}>
      <LectureMainContainer isDark={isDark}>
        <ListTitle isDark={isDark} onClick={()=>dispatch(setPage(page+1))}>강의 목록</ListTitle>
        <SearchSubmit/>
        <FilterContainer>
          <CategoryFilter filterNum="filters1"/>
          <StructureButton
            isHorizon={isHorizon}
            onClick={()=>dispatch(setIsHorizon(!isHorizon))}
          />
        </FilterContainer >
        {isHorizon ? (
          <HorizonItemContainer>
            {lectures.map((el) => (
              <HorizonItem key={el.videoId} lecture={el} channel={el.channel} isDark={isDark}/>
            ))}
          </HorizonItemContainer>
        ) : (
          <VerticalItemContainer>
            {lectures.map((el) => (
              <VerticalItem key={el.videoId} lecture={el} channel={el.channel} isDark={isDark}/>
            ))}
          </VerticalItemContainer>
        )}
      {page < maxPage&&!loading?<BottomDiv ref={bottomRef}/>:<></>}
      </LectureMainContainer>
    </PageContainer>
  );
};

export default LectureListPage;
