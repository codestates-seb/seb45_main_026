import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";

const globalTokens = tokens.global;

const NavyContainer = styled.div`
  width: 100%;
  height: 50px;
  display: flex;
  flex-direction: row;
  border-radius: ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px 0 0;
  background-color: ${globalTokens.White.value};
  border-bottom: 5px solid lightgray;
`;
const NavyItem = styled.div`
  width: 100px;
  height: 50px;
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  text-align: center;
  padding-top: ${globalTokens.Spacing8.value}px;
  border-bottom: 5px solid gray;
  &:hover{
    cursor: pointer;
  }
`;
const NavyItem2 = styled.div`
  width: 100px;
  height: 50px;
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  text-align: center;
  padding-top: ${globalTokens.Spacing8.value}px;
  border-bottom: 5px solid lightgray;
  &:hover {
    cursor: pointer;
  }
`;

export default function ChannelNav({ navigate, setNavigate }) {
  const userId = useSelector(state=>state.loginInfo.myid);
  const url = new URL(window.location.href);
  const pathnameArr = url.pathname.split('/');
  const channelMemberId = Number(pathnameArr[2]);
  
  const navs = ["홈", "동영상", "커뮤니티"];
  if( channelMemberId === userId ) navs.push('설정');

  return (
    <NavyContainer>
          { navs.map((el, idx) => (
              navigate === idx ? <NavyItem key={idx} onClick={()=>setNavigate(idx)}>{el}</NavyItem>
                 : <NavyItem2 key={idx} onClick={()=>setNavigate(idx)}>{el}</NavyItem2>)) }
    </NavyContainer>
  );
}