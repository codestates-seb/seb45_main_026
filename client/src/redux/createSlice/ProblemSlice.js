import { createSlice } from "@reduxjs/toolkit";

const ProblemSlice = createSlice({
  name: "ProblemSlice",
  initialState: {
    data: [],
    setting: {
      isPage: 1,
      isDetail: false,
      answers: { questionId: 0, answer: "" },
    },
  },
  reducers: {
    setProblems: (state, action) => {
      state.data = action.payload;
    },
    setPage: (state, action) => {
      state.setting.isPage = action.payload;
    },
    setDetail: (state, action) => {
      state.setting.isDetail = action.payload;
    },
    setAnswer: (state, action) => {
      state.setting.answers = {
        questionId: action.payload.questionId,
        answer: action.payload.answer,
      };
    },
  },
});

export default ProblemSlice;
export const { setProblems, setPage, setDetail, setConfirm, setAnswer } =
  ProblemSlice.actions;
