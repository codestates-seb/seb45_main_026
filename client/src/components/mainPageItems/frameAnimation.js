import { keyframes, styled } from "styled-components";

export const frameInTopToBottomAnimation = keyframes`
  0% {
    opacity: 0;
    transform: translateY(-20%);
  }
  100%{
    opacity: 1;
    transform: translateY(0%);
  }
`;

export const frameInBottomToTopAnimation = keyframes`
  0% {
    opacity: 0;
    transform: translateY(20%);
  }
  100%{
    opacity: 1;
    transform: translateY(0%);
  }
`;

export const frameInLeftToRightAnimation = keyframes`
  0% {
    opacity: 0;
    transform: translateX(-20%);
  }
  100%{
    opacity: 1;
    transform: translateX(0%);
  }
`

export const frameInRightToLeftAnimation = keyframes`
  0% {
    opacity: 0;
    transform: translateX(20%);
  }
  100% {
    opacity: 1;
    transform: translateX(0%);
  }
`
