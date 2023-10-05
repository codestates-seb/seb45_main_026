import styled from "styled-components";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const TableContainer = styled.table`
    width: 100%;
    margin: ${globalTokens.Spacing12.value}px 0;
`
export const TableTh = styled.th<{isDark : boolean}>`
    transition: 300ms;
    background-color: ${props=>props.isDark?globalTokens.Black.value : globalTokens.Background.value};
    width: 100%;
    padding: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
`
export const TableTr = styled.tr<{isDark : boolean}>`
    transition: 300ms;
    border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
    width: 100%;
    padding: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
`
export const TableTd = styled.td<{isDark:boolean}>`
    transition: 300ms;
    font-size: ${globalTokens.BodyText.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
