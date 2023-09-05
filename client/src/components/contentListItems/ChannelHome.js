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

export default function ChannelHome() {
    const [lectures, setLectures] = useState([]);
    useEffect(()=>{
    axios.get('https://api.itprometheus.net/videos?page=1&sort=view')
    .then(res=>console.log(res.data.data))
    .catch(err=>console.log(err))
    }, [])
    const a = {
            "videoId": 1,
            "videoName": "촛불로 공부하기",
            "thumbnailUrl": "https://d2ouhv9pc4idoe.cloudfront.net/4/videos/1/video1.png",
            "views": 1266,
            "price": 0,
            "star": 0.0,
            "isPurchased": false,
            "description": "test 영상입니다.",
            "categories": [
                {
                    "categoryId": 1,
                    "categoryName": "React"
                },
                {
                    "categoryId": 2,
                    "categoryName": "Redux"
                }
            ],
            "channel": {
                "memberId": 4,
                "channelName": "andygugu",
                "subscribes": 3,
                "isSubscribed": false,
                "imageUrl": "https://d2ouhv9pc4idoe.cloudfront.net/4/profile/test22.png"
            },
            "createdDate": "2023-09-04T00:00:00"
        }
    return (
        <HomeBody>
            <HomeTitle>무료강의</HomeTitle>
            <ItemContainer>
                <VerticalItem lecture={a} channel={a.channel}/>
                <VerticalItem lecture={a} channel={a.channel}/>
                <VerticalItem lecture={a} channel={a.channel}/>
                <VerticalItem lecture={a} channel={a.channel}/>
            </ItemContainer>
            <HomeTitle>채널 내 인기 강의</HomeTitle>
            <ItemContainer>
                <VerticalItem lecture={a} channel={a.channel}/>
                <VerticalItem lecture={a} channel={a.channel}/>
                <VerticalItem lecture={a} channel={a.channel}/>
                <VerticalItem lecture={a} channel={a.channel}/>
            </ItemContainer>
        </HomeBody>
    )
}