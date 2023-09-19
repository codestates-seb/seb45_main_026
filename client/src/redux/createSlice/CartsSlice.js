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
        videoCategories:[]
      },
    ],
    myCartInfo: {
      memberId: 0,
      nickname: "",
      email: "",
      grade: "",
      reward: 0,
    },
    checkedItem: [],
    paymentInfo: {
      amount: 0,
      orderId: "",
      orderName: "",
      customerName: "",
      successUrl: "http://localhost:3000/carts",
      failUrl: "http://localhost:3000/carts",
    },
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
    setPayment: (state, action) => {
      state.paymentInfo = { ...state.paymentInfo, ...action.payload };
    },
  },
});

export default CartsSlice;
export const { setCarts, setChecked, setMyInfo, setPayment } =
  CartsSlice.actions;
