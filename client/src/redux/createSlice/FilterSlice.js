import { createSlice } from "@reduxjs/toolkit";

const FilterSlice = createSlice({
  name: "FilterSlice",
  initialState: {
    sortBy: { text: "최신순", value: "created-date" },
    dropdown: false,
    category: { text: "카테고리", value: "" },
    isPurchased: { text: "구매여부", value: "" },
  },
  reducers: {
    setSort: (state, action) => {
      state.sortBy = action.payload;
    },
    setDropdown: (state, action) => {
      state.dropdown = action.payload;
    },
    setIsPurchased: (state, action) => {
      state.isPurchased = action.payload;
    },
    setCategory: (state, action) => {
      state.category = action.payload;
    },
  },
});

export default FilterSlice;
export const { setSort, setDropdown, setIsPurchased, setCategory } = FilterSlice.actions;