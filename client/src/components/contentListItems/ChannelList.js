import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../filters/CategoryFilter";
import HorizonItem from "./HorizonItem";

const globalTokens = tokens.global;

const ListBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${globalTokens.White.value};
    gap: ${globalTokens.Spacing24.value}px;
`; 
const ListContainer = styled.ul`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: ${globalTokens.Spacing16.value}px;
    margin-bottom: ${globalTokens.Spacing24.value}px;
`
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
export default function ChannelList() {
    return (
        <ListBody>
            <CategoryFilter />
            <ListContainer>
                <HorizonItem lecture={a} channel={a.channel}/>
                <HorizonItem lecture={a} channel={a.channel}/>
                <HorizonItem lecture={a} channel={a.channel}/>
                <HorizonItem lecture={a} channel={a.channel}/>
                <HorizonItem lecture={a} channel={a.channel}/>
                <HorizonItem lecture={a} channel={a.channel}/>
            </ListContainer>
        </ListBody>
    )
}
