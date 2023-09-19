import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import VerticalItem from "./VerticalItem";
import axios from "axios";
import { Heading5Typo } from '../../atoms/typographys/Typographys'
import { useSelector } from "react-redux";
import { useToken } from "../../hooks/useToken";

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
    border-radius: 0 0 ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px;
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
const LectureBlank = styled(Heading5Typo)`
  width: 100%;
  margin-top: 200px;
  text-align: center;
`;

export default function ChannelHome({ channelInfor, accessToken, userId }) {
  const [lectures, setLectures] = useState({
    free: [],
    popular: [],
  });
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const refreshToken = useToken();
  const tokens = useSelector(state=>state.loginInfo.accessToken);
  
  useEffect(() => {
    axios
      .get(
        `https://api.itprometheus.net/channels/${userId}/videos?page=1&free=true&size=4&sort=created-date`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) => setLectures((prev) => ({ ...prev, free: res.data.data })))
      .catch((err) => {
        if(err.response.data.message==='만료된 토큰입니다.') {
          refreshToken();
        } else {
          console.log(err);
        }
      });
    axios
      .get(
        `https://api.itprometheus.net/channels/${userId}/videos?page=1&size=4&sort=view`,
        {
          headers: { Authorization: accessToken.authorization },
        }
      )
      .then((res) =>
        setLectures((prev) => ({ ...prev, popular: res.data.data }))
      )
      .catch((err) => {
        if(err.response.data.message==='만료된 토큰입니다.') {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  }, [tokens,userId]);

  return (
    <HomeBody isDark={isDark}>
      <HomeTitle isDark={isDark}>무료강의</HomeTitle>
      <ItemContainer>
        {lectures.free.map((el) => (
          <VerticalItem key={el.videoId} lecture={el} channel={channelInfor} />
        ))}
        {lectures.free.length===0?<LectureBlank isDark={isDark}>채널 내 무료강의가 없습니다.</LectureBlank>:<></>}
      </ItemContainer>
      <HomeTitle isDark={isDark}>채널 내 인기 강의</HomeTitle>
      <ItemContainer>
        {lectures.popular.map((el) => (
          <VerticalItem key={el.videoId} lecture={el} channel={channelInfor} />
        ))}
        {lectures.popular.length===0?<LectureBlank isDark={isDark}>채널 내 강의가 없습니다.</LectureBlank>:<></>}
      </ItemContainer>
    </HomeBody>
  );
}