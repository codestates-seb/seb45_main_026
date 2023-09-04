import { createSlice } from "@reduxjs/toolkit";

const CartsSlice = createSlice({
  name: "CartsSlice",
  initialState: {
    data: [
      {
        videoId: 151,
        videoName: "리눅스 만드는 법",
        thumbnailUrl: "https://d2ouhv9pc4idoe.cloudfront.net/9999/test",
        views: 333,
        createdDate: "2023-09-04T01:12:04.519581",
        price: 100000,
        channel: {
          memberId: 3,
          channelName: "Linus Torvalds",
          subscribes: 8391,
          imageUrl: "https://d2ouhv9pc4idoe.cloudfront.net/images/test",
        },
      },
      {
        videoId: 9514,
        videoName: "컴활 강의",
        thumbnailUrl: "https://d2ouhv9pc4idoe.cloudfront.net/9999/test",
        views: 777,
        createdDate: "2023-09-04T01:12:04.52034",
        price: 70000,
        channel: {
          memberId: 361,
          channelName: "Bill Gates",
          subscribes: 9999,
          imageUrl: "https://d2ouhv9pc4idoe.cloudfront.net/images/test",
        },
      },
    ],
  },
  reducers: {
    setCarts: (state, action) => {
      state.data = action.payload;
    },
  },
});

export default CartsSlice;
export const { setCarts } = CartsSlice.actions;
