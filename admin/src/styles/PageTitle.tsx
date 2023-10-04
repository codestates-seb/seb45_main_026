import styled from "styled-components";
import { Heading5Typo } from "../atoms/typographys/Typographys";
import tokens from '../styles/tokens.json';

const globalTokens = tokens.global;

export const PageTitle = styled(Heading5Typo)`
    width: 100%;
    text-align: start;
    font-weight: ${globalTokens.Bold.value};
`