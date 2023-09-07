import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const HeaderProfileContainer = styled.button`
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing32.value}px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    cursor: pointer;
`
export const HeaderProfileImgContainer = styled.div`
    margin-left: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    border: 1px solid ${globalTokens.LightGray.value};
    width: 40px;
    height: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    overflow: hidden;
`
export const HeaderProfileImg = styled.img`
    width: 40px;
    object-fit: cover;
`