import { createSlice } from "@reduxjs/toolkit";

const ProblemSlice = createSlice({
  name: "ProblemSlice",
  initialState: {
    data: [],
    setting: {
      isPage: 1,
    },
  },
  reducers: {
    setProblems: (state, action) => {
      state.data = action.payload;
    },
    setPage: (state, action) => {
      state.setting.isPage = action.payload;
    },
  },
});

export default ProblemSlice;
export const { setProblems, setPage } = ProblemSlice.actions;
