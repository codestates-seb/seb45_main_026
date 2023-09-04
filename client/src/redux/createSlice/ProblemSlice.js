import { createSlice } from "@reduxjs/toolkit";

const ProblemSlice = createSlice({
  name: "ProblemSlice",
  initialState: {
    data: [
      {
        questionId: 1,
        position: 1,
        content: "content1",
        myAnswer: "1",
        questionAnswer: "2",
        answerStatus: "CORRECT",
        description: "description1",
        selections: ["selection1", "selection2", "selection3", "selection4"],
        solvedDate: "2023-09-04T07:20:51.920869",
      },
      {
        questionId: 2,
        position: 2,
        content: "content2",
        myAnswer: "2",
        questionAnswer: "2",
        answerStatus: "CORRECT",
        description: "description2",
        selections: ["selection1", "selection2", "selection3", "selection4"],
        solvedDate: "2023-09-04T07:20:51.920883",
      },
      {
        questionId: 3,
        position: 3,
        content: "content3",
        myAnswer: "3",
        questionAnswer: "2",
        answerStatus: "CORRECT",
        description: "description3",
        selections: ["selection1", "selection2", "selection3", "selection4"],
        solvedDate: "2023-09-04T07:20:51.920887",
      },
      {
        questionId: 4,
        position: 4,
        content: "content4",
        myAnswer: "4",
        questionAnswer: "2",
        answerStatus: "CORRECT",
        description: "description4",
        selections: ["selection1", "selection2", "selection3", "selection4"],
        solvedDate: "2023-09-04T07:20:51.920891",
      },
      {
        questionId: 5,
        position: 5,
        content: "content5",
        myAnswer: "5",
        questionAnswer: "2",
        answerStatus: "CORRECT",
        description: "description5",
        selections: ["selection1", "selection2", "selection3", "selection4"],
        solvedDate: "2023-09-04T07:20:51.920895",
      },
    ],
    setting: {
      isPage: 1,
      isDetail: false,
      confirm: false,
      answers: { questionId: 0, answer: 0 },
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
    setConfirm: (state, action) => {
      state.setting.confirm = action.payload;
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
