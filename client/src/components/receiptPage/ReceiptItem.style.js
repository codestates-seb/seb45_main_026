import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;

export const ReceiptItemContainer = styled.section`
    padding: ${globalTokens.Spacing16.value}px 0px;
    width: 90%;
    display: flex;
    flex-direction: row;
    align-items: center;
    border-bottom: ${props=>props.isAcordianOpen?0:1}px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
`
export const ReceiptGrayTypo = styled(BodyTextTypo)`
    width: 150px;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
export const ReceiptTitleTypo = styled(BodyTextTypo)`
    width: 350px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing20.value}px;
`
export const ReceiptAmountTypo = styled(BodyTextTypo)`
    width: 100px;
    text-align: center;
`
export const ReceiptStatusTypo = styled(BodyTextTypo)`
    width: 150px;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
    text-align: center;
`