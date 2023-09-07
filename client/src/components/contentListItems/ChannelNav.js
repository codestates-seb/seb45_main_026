import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";

const globalTokens = tokens.global;

const NavyContainer = styled.div`
  width: 100%;
  height: 45px;
  display: flex;
  flex-direction: row;
  border-radius: ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px 0 0;
  background-color: ${ props=>props.isDark ? 'rgba(255,255,255,0.15)' : globalTokens.White.value };
  border-bottom: 3px solid ${globalTokens.LightGray.value};
`;
//선택한 Nav Item
const NavyItem = styled.div`
  width: 85px;
  height: 45px;
  color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
  font-size: ${globalTokens.BodyText.value}px;
  font-weight: ${globalTokens.Bold.value};
  text-align: center;
  padding-top: ${globalTokens.Spacing8.value}px;
  border-bottom: 3px solid ${globalTokens.Gray.value};
  &:hover{
    cursor: pointer;
  }
`;
//선택하지 않은 Nav Item
const NavyItem2 = styled.div`
  width: 85px;
  height: 45px;
  color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
  font-size: ${globalTokens.BodyText.value}px;
  /* font-weight: ${globalTokens.Bold.value}; */
  text-align: center;
  padding-top: ${globalTokens.Spacing8.value}px;
  border-bottom: 3px solid ${globalTokens.LightGray.value};
  &:hover {
    cursor: pointer;
  }
`;

export default function ChannelNav({ navigate, setNavigate }) {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const myid = useSelector(state=>state.loginInfo.myid);
  const { userId } = useParams();
  
  const navs = ["홈", "동영상", "커뮤니티"];
  if( myid === Number(userId) ) navs.push('설정');

  return (
    <NavyContainer isDark={isDark}>
          { navs.map((el, idx) => (
              navigate === idx ? <NavyItem key={idx} onClick={()=>setNavigate(idx)} isDark={isDark}>{el}</NavyItem>
                 : <NavyItem2 key={idx} onClick={()=>setNavigate(idx)} isDark={isDark}>{el}</NavyItem2>)) }
    </NavyContainer>
  );
}