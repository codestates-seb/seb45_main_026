import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import VerticalItem from "./VerticalItem";
import axios from "axios";
import { Heading5Typo } from '../../atoms/typographys/Typographys'
import { useSelector } from "react-redux";

const globalTokens = tokens.global;

const HomeBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding: ${globalTokens.Spacing20.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: ${ props=>props.isDark ? 'rgba(255,255,255,0.15)' : globalTokens.White.value };
`
export const HomeTitle = styled(Heading5Typo)`
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px;
    width: 100%;
`
const ItemContainer = styled.ul`
    width: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    flex-wrap: wrap;
    gap: ${globalTokens.Spacing12.value}px;
`

export default function ChannelHome({ channelInfor, accessToken, userId }) {
  const [lectures, setLectures] = useState({
    free: [],
    poular: [],
  });
  const isDark = useSelector(state=>state.uiSetting.isDark);
  
  useEffect(() => {
    axios
      .get(
        `https://api.itprometheus.net/channels/${userId}/videos?page=1&free=true&size=4&sort=created-date`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => setLectures((prev) => ({ ...prev, free: res.data.data })))
      .catch((err) => console.log(err));
    axios
      .get(
        `https://api.itprometheus.net/channels/${userId}/videos?page=1&size=4&sort=view`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) =>
        setLectures((prev) => ({ ...prev, poular: res.data.data }))
      )
      .catch((err) => console.log(err));
  }, []);

  return (
    <HomeBody isDark={isDark}>
      <HomeTitle isDark={isDark}>무료강의</HomeTitle>
      <ItemContainer>
        {lectures.free.map((el) => (
          <VerticalItem key={el.videoId} lecture={el} channel={channelInfor} />
        ))}
      </ItemContainer>
      <HomeTitle isDark={isDark}>채널 내 인기 강의</HomeTitle>
      <ItemContainer>
        {lectures.poular.map((el) => (
          <VerticalItem key={el.videoId} lecture={el} channel={channelInfor} />
        ))}
      </ItemContainer>
    </HomeBody>
  );
}