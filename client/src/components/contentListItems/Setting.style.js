import { styled } from "styled-components";
import tokens from '../../styles/tokens.json';
import { Heading5Typo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

export const SettingContainer = styled.div`
    padding-bottom: ${globalTokens.Spacing40.value}px;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: ${ props=>props.isDark ? 'rgba(255,255,255,0.15)' : globalTokens.White.value };
`
export const SettingTitle = styled(Heading5Typo)`
    width: 100%;
    margin-top: ${globalTokens.Spacing40.value}px;
    text-align: start;
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