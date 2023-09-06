import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import VerticalItem from "./VerticalItem";
import axios from "axios";

const globalTokens = tokens.global;

const HomeBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${globalTokens.White.value};
`
const HomeTitle = styled.h2`
    height: 20px;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px;
`
const ItemContainer = styled.ul`
    width: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    flex-wrap: wrap;
    gap: ${globalTokens.Spacing28.value}px;
`

export default function ChannelHome({ channelInfor }) {
    const [lectures, setLectures] = useState({
        free: [],
        poular: [],
    });
    useEffect(()=>{
    axios.get('https://api.itprometheus.net/channels/4/videos?page=1&free=true&size=4&sort=created-date')
    .then(res=>setLectures((prev)=>({...prev,free:res.data.data})))
    .catch(err=>console.log(err))
    axios.get('https://api.itprometheus.net/channels/4/videos?page=1&size=4&sort=view')
    .then(res=>setLectures((prev)=>({...prev,poular:res.data.data})))
    .catch(err=>console.log(err))
    }, [])
    
    return (
        <HomeBody>
            <HomeTitle>무료강의</HomeTitle>
            <ItemContainer>
                {lectures.free.map((el)=><VerticalItem key={el.videoId} lecture={el} channel={channelInfor}/>)}
            </ItemContainer>
            <HomeTitle>채널 내 인기 강의</HomeTitle>
            <ItemContainer>
                {lectures.poular.map((el)=><VerticalItem key={el.videoId} lecture={el} channel={channelInfor}/>)}
            </ItemContainer>
        </HomeBody>
    )
}