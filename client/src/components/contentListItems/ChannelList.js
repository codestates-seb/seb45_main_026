import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../filters/CategoryFilter";
import HorizonItem from "./HorizonItem";
import axios from "axios";
import { useSelector,useDispatch } from "react-redux";
import { resetToInitialState } from "../../redux/createSlice/FilterSlice";

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
export default function ChannelList({ channelInfor, accessToken, userId }) {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const filterState = useSelector((state) => state.filterSlice.filter);
  const dispatch = useDispatch();
  const [lectures, setLectures] = useState([]);
  useEffect(() => {
    return () => {
      dispatch(resetToInitialState());
    };
  }, []);
  useEffect(() => {
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
        }`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => setLectures(res.data.data))
      .catch((err) => console.log(err));
  }, [filterState]);
  return (
    <ListBody isDark={isDark}>
      <CategoryFilter />
      <ListContainer>
        {lectures.map((el) => (
          <HorizonItem lecture={el} channel={channelInfor} />
        ))}
      </ListContainer>
    </ListBody>
  );
}
