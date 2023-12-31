import { createSlice } from "@reduxjs/toolkit";

const UISettingSlice = createSlice({
  name: "UISetting",
  initialState: {
    browserWidth: window.innerWidth,
    isDark: false,
    isSideBar: false,
  },
  reducers: {
    setIsDark: (state, action) => {
      state.isDark = action.payload;
    },
    setBrowserWidth: (state, action) => {
      state.browserWidth = action.payload;
    },
    setIsSideBar: (state, action) => {
      state.isSideBar = action.payload;
    },
  },
});

export default UISettingSlice;
export const { setIsDark, setBrowserWidth, setIsSideBar } =
  UISettingSlice.actions;
