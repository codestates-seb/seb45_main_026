import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  dropdown: false,
  isHorizon: true,
  isList: false,
  page: 1,
  maxPage : null,
  filter: {
    sortBy: { text: "최신순", value: "created-date" },
    category: { text: "카테고리", value: "" },
    isPurchased: { text: "구매여부", value: "true" },
    isFree: { text: "유료/무료", value: "" },
    isSubscribed: { text: "구독여부", value: "" },
  },
};
const FilterSlice = createSlice({
  name: "FilterSlice",
  initialState,
  reducers: {
    setDropdown: (state, action) => {
      state.dropdown = action.payload;
    },
    setSort: (state, action) => {
      state.filter.sortBy = action.payload;
    },
    setIsPurchased: (state, action) => {
      state.filter.isPurchased = action.payload;
    },
    setCategory: (state, action) => {
      state.filter.category = action.payload;
    },
    setIsFree: (state, action) => {
      state.filter.isFree = action.payload;
    },
    setIsSubscribed: (state, action) => {
      state.filter.isSubscribed = action.payload;
    },
    setIsHorizon: (state, action) => {
      state.isHorizon = action.payload;
    },
    setIsList: (state, action) => {
      state.isList = action.payload;
    },
    setPage: (state, action) => {
      state.page = action.payload;
    },
    setMaxPage: (state, action) => {
      state.maxPage = action.payload;
    },
    resetToInitialState: (state) => {
      return initialState;
    },
  },
});

export default FilterSlice;
export const {
  setSort,
  setDropdown,
  setIsPurchased,
  setCategory,
  resetToInitialState,
  setIsFree,
  setIsHorizon,
  setIsSubscribed,
  setIsList,
  setPage,
  setMaxPage,
} = FilterSlice.actions;