import { createSlice } from "@reduxjs/toolkit";

const VideoInfoSlice = createSlice({
  name: "VideoInfoSlice",
  initialState: {
    data: {
      videoId: "",
      videoName: "",
      videoUrl: "",
      isPurchased: false,
      views: "",
      description: "",
      createdDate: "",
      price: 0,
      star: 0,
      channel: {
        memberId: "",
        channelName: "",
        imageUrl: "",
        subscribes: "",
        isSubscribed: false,
      },
      categories: [{ categoryId: 0, categoryName: "" }],
      isReplied: [],
    },
    mode: { contentOpend: false },
  },
  reducers: {
    setVideoInfo: (state, action) => {
      state.data = { ...state.data, ...action.payload };
    },
    setPrev: (state, action) => {
      state.data.isPurchased = action.payload;
    },
    setContnentOpen: (state, action) => {
      state.mode.contentOpend = action.payload;
    },
  },
});

export default VideoInfoSlice;
export const { setVideoInfo, setPrev, setContnentOpen } =
  VideoInfoSlice.actions;
