import { createSlice } from "@reduxjs/toolkit";

const CartsSlice = createSlice({
  name: "CartsSlice",
  initialState: {
    data: [
      {
        videoId: 0,
        videoName: "",
        thumbnailUrl: "",
        views: 0,
        createdDate: "",
        price: 0,
        channel: {
          memberId: 0,
          channelName: "",
          subscribes: 0,
          imageUrl: "",
        },
      },
    ],
    myCartInfo: {
      memberId: 1,
      nickname: "test",
      email: "",
      grade: "BRONZE",
      reward: 40,
    },
    checkedItem: [],
  },
  reducers: {
    setCarts: (state, action) => {
      state.data = action.payload;
    },
    setChecked: (state, action) => {
      state.checkedItem = action.payload;
    },
    setMyInfo: (state, action) => {
      state.myCartInfo = action.payload;
    },
  },
});

export default CartsSlice;
export const { setCarts, setChecked, setMyInfo } = CartsSlice.actions;
