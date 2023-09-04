import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

const ItemBody = styled.div`
    width: 100%;
    min-height: 300px;
    padding: ${globalTokens.Spacing28.value}px;
    gap: ${globalTokens.Spacing28.value}px;
    border: 1px black solid;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
`
const ProfileContainer = styled.div`
    height: 50px;
    display: flex;
    flex-direction: row;
    align-items: center;   
    gap: ${globalTokens.Spacing8.value}px;
`
const ProfileImg = styled.img`
    max-height: 50px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.span`
    width: 50px;
    height: 50px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid lightgray;
    overflow: hidden;
`
const TextInfor = styled.div`
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
`
const AuthorName = styled.div`
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
`
const CreatedAt = styled.div`
    font-size: ${globalTokens.BodyText.value}px;
`
const NoticeContent = styled.p`
    font-size: ${globalTokens.Heading5.value}px;
`

export default function NoticeItem() {
    const text = "이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 떠나야 합니다. 이 편지를 포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹 미신이라 하실지 모르지만 사실입니다.영국에서 HGXWCH이라는 사람은 1930년에 이 편지를 받았습니다. 그는 비서에게 복사해서 보내라고 했습니다. 며칠 뒤에 복권이 당첨되어 20억을 받았습니다. 어떤 이는 이 편지를 받았으나 96시간 이내 자신의 손에서 떠나야 한다는 사실을 잊었습니다. 그는 곧 사직되었습니다. 나중에야 이 사실을 알고 7통의 편지를 보냈는데 다시 좋은 직장을 얻었습니다. 미국의 케네디 대통령은 이 편지를 받았지만 그냥 버렸습니다. 결국 9일 후 그는 암살당했습니다. 기억해 주세요. 이 편지를 보내면 7년의 행운이 있을 것이고 그렇지 않으면 3년의 불행이 있을 것입니다. 그리고 이 편지를 버리거나 낙서를 해서는 절대로 안됩니다. 7통입니다. 이 편지를 받은 사람은 행운이 깃들것입니다. 힘들겠지만 좋은게 좋다고 생각하세요. 7년의 행운을 빌면서..."
    return (
      <ItemBody>
        <ProfileContainer>
          <ImgContainer>
            <ProfileImg src="https://avatars.githubusercontent.com/u/50258232?v=4" />
          </ImgContainer>
          <TextInfor>
            <AuthorName>HyerimKimm</AuthorName>
            <CreatedAt>9월 1일 작성됨</CreatedAt>
          </TextInfor>
        </ProfileContainer>
        <NoticeContent>{text}</NoticeContent>
      </ItemBody>
    );
}