import { createSlice } from "@reduxjs/toolkit";

const FilterSlice = createSlice({
  name: "FilterSlice",
  initialState: {
    dropdown: false,
    isHorizon: true,
    filter: {
      sortBy: { text: "최신순", value: "created-date" },
      category: { text: "카테고리", value: "" },
      isPurchased: { text: "구매여부", value: "true" },
    }
  },
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
  },
});

export default FilterSlice;
export const { setSort, setDropdown, setIsPurchased, setCategory } = FilterSlice.actions;