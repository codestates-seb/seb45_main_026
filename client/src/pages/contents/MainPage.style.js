import { styled } from 'styled-components';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const MainPageContainer = styled(PageContainer)`
    display: block;
    overflow-x: hidden;
    overflow-y: auto;
    width: 100vw;
    height: 100vh;
`
export const LightContainer = styled.div`
    background-color: ${(props)=>props.isDark ? globalTokens.BackgroundDark.value : globalTokens.Background.value};
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    transition: 300ms;
`
export const DarkContainer = styled.div`
    background-color: ${globalTokens.Gray.value};
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    transition: 300ms;
`
