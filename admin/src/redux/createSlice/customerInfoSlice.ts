import { createSlice } from "@reduxjs/toolkit";

const customerInfoSlice = createSlice({
  name: "customerInfo",
  initialState: {
    roomId: "",
  },
  reducers: {
    setRoomId: (state, action) => {
      state.roomId = action.payload;
    },
  },
});

export default customerInfoSlice;
export const { setRoomId } = customerInfoSlice.actions;
