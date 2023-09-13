import { keyframes, styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { TextButton } from '../../atoms/buttons/Buttons'

const globalTokens = tokens.global;

export const ReceiptDropdownContainer = styled.section`
    width: 100%;
    display: flex;
    flex-direction: row;
`
export const DropdownWrapper = styled.div`
    margin: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
`
export const DropdownButton = styled(TextButton)`
    position: relative;
    top: 0;
    left: 0;
    width: 150px;
    border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    &:hover {
        background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':'rgba(0,0,0,0.15)'};
    }
`
export const DropdownButtonIcon = styled.img`
    margin-left: ${globalTokens.Spacing8.value}px;
    width: 15px;
`
export const DropdownMenuWrapper = styled.div`
    margin-top: ${globalTokens.Spacing4.value}px;
    padding: ${globalTokens.Spacing8.value}px;
    position: absolute;
    top: 30px;
    left: 0;
    width: 150px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: ${props=>props.isDark?globalTokens.Black.value : globalTokens.White.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
    z-index: 1;
`
export const DropdownMenuButton = styled(TextButton)`
    width: 150px;
    padding: ${globalTokens.Spacing4.value};
`
