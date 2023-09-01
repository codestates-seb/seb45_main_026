import { createSlice } from "@reduxjs/toolkit";

const VideoInfoSlice = createSlice({
  name: "VideoInfoSlice",
  initialState: { data: {} },
  reducers: {
    setVideoInfo: (state, action) => {
      state.data = { ...state.data, ...action.payload };
    },
  },
});

export default VideoInfoSlice;
export const { setVideoInfo } = VideoInfoSlice.actions;
