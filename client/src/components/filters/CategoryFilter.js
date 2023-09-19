import React,{useEffect,useState} from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import CategoryButton from "./CategoryButton";
import axios from "axios";

const globalTokens = tokens.global;

const FilterContainer = styled.div`
    height: 50px;
    display: flex;
    flex-direction: row;
    gap: ${globalTokens.Spacing8.value}px;
`

export default function CategoryFilter({ filterNum }) {
  const [categories,setCategories]=useState([])
  useEffect(() => {
    axios.get("https://api.itprometheus.net/categories")
      .then(res => {
        const categoryData = res.data.data.map((el) => ({ text: el.categoryName, value: el.categoryName }));
        setCategories([{ text: "전체", value: "" }, ...categoryData]);
      })
  },[])
    const filters1 = [
      {
        name: "category",
        initialText: "카테고리",
        initialValue: "",
        actionName: "setCategory",
        options: categories,
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
        options: categories,
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
          { text: "별점순", value: "star" },
          { text: "이름순", value: "name" },
        ],
      },
    ];
  const filters4 = [
      {
        name: "watchedDate",
        initialText: "7일간",
        initialValue: "7",
        actionName:"setWatchedDate",
        options: [
          {text:"7일간", value:"7"},
          {text:"14일간", value:"14"},
          {text:"30일간", value:"30"},
        ]
      }
  ]
  const filters5 = [
    {
      name: "category",
      initialText: "카테고리",
      initialValue: "",
      actionName: "setCategory",
      options: categories,
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
      ],
    },
    {
      name: "sortBy",
      initialText: "정확도순",
      initialValue: "",
      actionName: "setSort",
      options: [
        { text: "정확도순", value: ""},
        { text: "최신순", value: "created-date" },
        { text: "조회순", value: "view" },
        { text: "별점순", value: "star" },
      ],
    },
  ];
    const filters = {filters1,filters2,filters3,filters4,filters5}
    return (
        <FilterContainer>
            {filters[filterNum].map((el,idx)=><CategoryButton key={idx} filter={el} />)}
        </FilterContainer>
    )
}

