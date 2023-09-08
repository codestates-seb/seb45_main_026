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
import { resetToInitialState,setIsHorizon } from "../../redux/createSlice/FilterSlice";
import { Heading5Typo } from '../../atoms/typographys/Typographys';
import list from '../../assets/images/icons/listItem/list.svg'
import grid from '../../assets/images/icons/listItem/grid.svg'
import { HomeTitle } from '../../components/contentListItems/ChannelHome';

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
  border-radius: ${globalTokens.BigRadius.value}px;
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
  border-radius: ${globalTokens.RegularRadius.value}px;
`;
const HorizonItemContainer = styled.ul`
  width: 100%;
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

const LectureListPage = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const [lectures, setLectures] = useState([]);
  const filterState = useSelector((state) => state.filterSlice.filter);
  const isHorizon = useSelector((state) => state.filterSlice.isHorizon);
  const accessToken = useSelector(state=>state.loginInfo.accessToken);
  const dispatch = useDispatch();
  useEffect(() => {
    return () => {
      dispatch(resetToInitialState());
    };
  }, []);
  useEffect(()=>{
    axios
      .get(
        `https://api.itprometheus.net/videos?sort=${
          filterState.sortBy.value
        }&is-purchased=${filterState.isPurchased.value}${
          filterState.category.value
            ? `&category=${filterState.category.value}`
            : ""
        }${
          filterState.isFree.value ? `&free=${filterState.isFree.value}` : ""
        }`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => setLectures(res.data.data))
      .catch((err) => console.log(err));
  }, [filterState])
  
  return (
    <PageContainer isDark={isDark}>
      <LectureMainContainer isDark={isDark}>
        <ListTitle isDark={isDark}>강의 목록</ListTitle>
        <FilterContainer>
          <CategoryFilter />
          <StructureButton
            isHorizon={isHorizon}
            onClick={()=>dispatch(setIsHorizon(!isHorizon))}
          />
        </FilterContainer>
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
      </LectureMainContainer>
    </PageContainer>
  );
};

export default LectureListPage;
