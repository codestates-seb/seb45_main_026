import React from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import { TextButton } from "../buttons/Buttons";

const globalTokens = tokens.global;

export type paginationPropsType = {
  isDark: boolean;
  maxPage: number;
  currentPage: number;
  setCurrentPage: React.Dispatch<React.SetStateAction<number>>;
};

const Pagination = ({
  isDark,
  maxPage,
  currentPage,
  setCurrentPage,
}: paginationPropsType) => {
  if (currentPage > maxPage) currentPage = maxPage;

  let start = 1 + 5 * (Math.ceil(currentPage / 5) - 1);
  let end = start + 4 > maxPage ? maxPage : start + 4;
  let numberArr = [];

  for (let i = start; i <= end; i++) numberArr.push(i);

  return (
    <PaginationContainer>
      <TextButton
        isDark={isDark}
        onClick={() => {
          if (start > 1) {
            start = start - 5;
            end = start + 4 > maxPage ? maxPage : start + 4;
            setCurrentPage(start);
          }
        }}
      >{`<`}</TextButton>
      {numberArr.map((e) => (
        <PaginationNumber
          isDark={isDark}
          isCurrentNumber={currentPage === e}
          onClick={() => {
            setCurrentPage(e);
          }}
        >
          {e}
        </PaginationNumber>
      ))}
      <TextButton
        isDark={isDark}
        onClick={() => {
          if (end < maxPage) {
            start = start + 5;
            end = start + 4 > maxPage ? maxPage : start + 4;
            setCurrentPage(start);
          }
        }}
      >{`>`}</TextButton>
    </PaginationContainer>
  );
};

export const PaginationContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
`;

export const PaginationNumber = styled(TextButton)<{
  isCurrentNumber: boolean;
}>`
  font-weight: ${(props) =>
    props.isCurrentNumber ? globalTokens.Bold.value : 400};
  font-size: ${globalTokens.BodyText.value}px;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`;

export default Pagination;
