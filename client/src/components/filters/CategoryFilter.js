import React from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import CategoryButton from "./CategoryButton";

const globalTokens = tokens.global;

const FilterContainer = styled.div`
    height: 50px;
    display: flex;
    flex-direction: row;
    gap: ${globalTokens.Spacing8.value}px;
`

export default function CategoryFilter({filterNum}) {
    const filters = {filters1,filters2,filters3}
    return (
        <FilterContainer>
            {filters[filterNum].map((el,idx)=><CategoryButton key={idx} filter={el} />)}
        </FilterContainer>
    )
}

const filters1 = [
  {
    name: "category",
    initialText: "카테고리",
    initialValue: "",
    actionName: "setCategory",
    options: [
      { text: "전체", value: "" },
      { text: "React", value: "react" },
      { text: "Redux", value: "redux" },
      { text: "phython", value: "phython" },
      { text: "JS", value: "js" },
      { text: "AWS", value: "aws" },
    ],
  },
  {
    name: "isPurchased",
    initialText: "구매여부",
    initialValue: "true",
    actionName: "setIsPurchased",
    options: [
      { text: "전체", value: "true" },
      { text: "구매전", value: "false" },
    ],
  },
  {
    name: "isFree",
    initialText: "유료/무료",
    initialValue: "",
    actionName: "setIsFree",
    options: [
      { text: "전체", value: "" },
      { text: "유료", value: "false" },
      { text: "무료", value: "true" },
    ],
  },
  {
    name: "isSubscribed",
    initialText: "구독여부",
    initialValue: "",
    actionName: "setIsSubscribed",
    options: [
      { text: "전체", value: "" },
      { text: "구독됨", value: "true" },
      { text: "미구독", value: "false" },
    ],
  },
  {
    name: "sortBy",
    initialText: "최신순",
    initialValue: "created-date",
    actionName: "setSort",
    options: [
      { text: "최신순", value: "created-date" },
      { text: "조회순", value: "view" },
      { text: "별점순", value: "star" },
    ],
  },
];

const filters2 = [
  {
    name: "category",
    initialText: "카테고리",
    initialValue: "",
    actionName: "setCategory",
    options: [
      { text: "전체", value: "" },
      { text: "React", value: "react" },
      { text: "Redux", value: "redux" },
      { text: "phython", value: "phython" },
      { text: "JS", value: "js" },
      { text: "AWS", value: "aws" },
    ],
  },
  {
    name: "isPurchased",
    initialText: "구매여부",
    initialValue: "true",
    actionName: "setIsPurchased",
    options: [
      { text: "전체", value: "true" },
      { text: "구매전", value: "false" },
    ],
  },
  {
    name: "isFree",
    initialText: "유료/무료",
    initialValue: "",
    actionName: "setIsFree",
    options: [
      { text: "전체", value: "" },
      { text: "유료", value: "false" },
      { text: "무료", value: "true" },
    ],
  },
  {
    name: "sortBy",
    initialText: "최신순",
    initialValue: "created-date",
    actionName: "setSort",
    options: [
      { text: "최신순", value: "created-date" },
      { text: "조회순", value: "view" },
      { text: "별점순", value: "star" },
    ],
  },
];
const filters3 = [
  {
    name: "sortBy",
    initialText: "최신순",
    initialValue: "created-date",
    actionName: "setSort",
    options: [
      { text: "최신순", value: "created-date" },
      { text: "조회순", value: "view" },
      { text: "별점순", value: "star" },
      { text: "이름순", value: "name" },
    ],
  },
];