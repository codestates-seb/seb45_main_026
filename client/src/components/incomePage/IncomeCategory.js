import React, { useState } from "react";
import {
  DropdownButton,
  DropdownButtonIcon,
  DropdownContainer,
  DropdownMenuButton,
  DropdownMenuWrapper,
  DropdownTextTypo,
  DropdownWrapper,
  ReceiptDropdownContainer,
} from "../receiptPage/ReceiptDropdown.style";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import arrowUp from "../../assets/images/icons/arrow/subscribe_arrow_up.svg";
import arrowDown from "../../assets/images/icons/arrow/subscribe_arrow_down.svg";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

const IncomeDropdownWrapper = styled(DropdownWrapper)`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
`;
const IncomeDropdownButton = styled(DropdownButton)`
  width: 100px;
`;
const IncomeDropdownUnitTypo = styled(BodyTextTypo)`
  margin: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px
    ${globalTokens.Spacing4.value}px ${globalTokens.Spacing4.value}px;
`;
const IncomeDropdownMenuWrapper = styled(DropdownMenuWrapper)`
  width: 100px;
`;

const IncomeCategory = ({ year, setYear, month, setMonth }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  const date = new Date();
  const currentYear = date.getFullYear();

  const [isYearDropdownOpen, setIsYearDropdownOpen] = useState(false);
  const [isMonthDropdownOpen, setIsMonthDropdownOpen] = useState(false);

  const handleYearClick = (e) => {
    switch (e.target.textContent) {
      case "전체":
        setYear(null);
        setMonth(null);
        return;
      case `${currentYear}`:
        setYear(currentYear);
        setMonth(null);
        return;
      case `${currentYear - 1}`:
        setYear(`${currentYear - 1}`);
        setMonth(null);
        return;
      case `${currentYear - 2}`:
        setYear(`${currentYear - 2}`);
        setMonth(null);
        return;
      case `${currentYear - 3}`:
        setYear(`${currentYear - 3}`);
        setMonth(null);
        return;
      case `${currentYear - 4}`:
        setYear(`${currentYear - 4}`);
        setMonth(null);
        return;
      default:
        return;
    }
  };

  const handleMonthClick = (e) => {
    switch (e.target.textContent) {
      case "전체":
        setMonth(null);
        return;
      case "1":
        setMonth(1);
        return;
      case "2":
        setMonth(2);
        return;
      case "3":
        setMonth(3);
        return;
      case "4":
        setMonth(4);
        return;
      case "5":
        setMonth(5);
        return;
      case "6":
        setMonth(6);
        return;
      case "7":
        setMonth(7);
        return;
      case "8":
        setMonth(8);
        return;
      case "9":
        setMonth(9);
        return;
      case "10":
        setMonth(10);
        return;
      case "11":
        setMonth(11);
        return;
      case "12":
        setMonth(12);
        return;
      default:
        return;
    }
  };

  return (
    <ReceiptDropdownContainer>
      <IncomeDropdownWrapper>
        <DropdownContainer>
          <IncomeDropdownButton
            isDark={isDark}
            onClick={() => {
              setIsYearDropdownOpen(!isYearDropdownOpen);
            }}
            onBlur={() => {
              setIsYearDropdownOpen(false);
            }}
          >
            <DropdownTextTypo isDark={isDark}>
              {year === null ? "전체" : year}
            </DropdownTextTypo>
            <DropdownButtonIcon
              src={isYearDropdownOpen ? arrowUp : arrowDown}
            />
          </IncomeDropdownButton>
          <IncomeDropdownMenuWrapper
            isDark={isDark}
            isDropdownOpen={isYearDropdownOpen}
          >
            <DropdownMenuButton
              isDropdownOpen={isYearDropdownOpen}
              onClick={handleYearClick}
              isDark={isDark}
            >
              전체
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isYearDropdownOpen}
              onClick={handleYearClick}
              isDark={isDark}
            >
              {currentYear}
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isYearDropdownOpen}
              onClick={handleYearClick}
              isDark={isDark}
            >
              {currentYear - 1}
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isYearDropdownOpen}
              onClick={handleYearClick}
              isDark={isDark}
            >
              {currentYear - 2}
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isYearDropdownOpen}
              onClick={handleYearClick}
              isDark={isDark}
            >
              {currentYear - 3}
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isYearDropdownOpen}
              onClick={handleYearClick}
              isDark={isDark}
            >
              {currentYear - 4}
            </DropdownMenuButton>
          </IncomeDropdownMenuWrapper>
        </DropdownContainer>
        <IncomeDropdownUnitTypo isDark={isDark}>년</IncomeDropdownUnitTypo>
        <DropdownContainer>
          <IncomeDropdownButton
            isDark={isDark}
            onClick={() => {
              year && setIsMonthDropdownOpen(!isMonthDropdownOpen);
            }}
            onBlur={() => {
              setIsMonthDropdownOpen(false);
            }}
          >
            <DropdownTextTypo isDark={isDark}>
              {month === null ? "전체" : month}
            </DropdownTextTypo>
            <DropdownButtonIcon
              src={isMonthDropdownOpen ? arrowUp : arrowDown}
            />
          </IncomeDropdownButton>
          <IncomeDropdownMenuWrapper
            isDark={isDark}
            isDropdownOpen={isMonthDropdownOpen}
          >
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              전체
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              1
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              2
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              3
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              4
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              5
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              6
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              7
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              8
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              9
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              10
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              11
            </DropdownMenuButton>
            <DropdownMenuButton
              isDropdownOpen={isMonthDropdownOpen}
              onClick={handleMonthClick}
              isDark={isDark}
            >
              12
            </DropdownMenuButton>
          </IncomeDropdownMenuWrapper>
        </DropdownContainer>
        <IncomeDropdownUnitTypo isDark={isDark}>월</IncomeDropdownUnitTypo>
      </IncomeDropdownWrapper>
    </ReceiptDropdownContainer>
  );
};

export default IncomeCategory;
