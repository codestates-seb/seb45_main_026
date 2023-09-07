import { styled } from "styled-components";
import tokens from '../../styles/tokens.json';
import { HomeTitle } from "./ChannelHome";

const globalTokens = tokens.global;

export const SettingContainer = styled.div`
    padding-top: ${globalTokens.Spacing24.value}px;
    padding-bottom: ${globalTokens.Spacing40.value}px;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: ${ props=>props.isDark ? 'rgba(255,255,255,0.15)' : globalTokens.White.value };
`
export const SettingTitle2 = styled(HomeTitle)`
    padding-left: 0;
    margin-left: 0;
`
export const SettingTitle = styled(HomeTitle)`
    padding-top: ${globalTokens.Spacing24.value}px;
    padding-left: 0;
    margin-left: 0;
`
export const UserInfoContainer = styled.form`
    padding: 0 ${globalTokens.Spacing40.value}px;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: start;
`
export const ExtraButtonContainer = styled.div`
    margin-top: ${globalTokens.Spacing40.value}px;
    display: flex;
    flex-direction: column;
    align-items: start;
    justify-content: center;
`