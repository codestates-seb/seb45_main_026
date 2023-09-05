import { createSlice } from "@reduxjs/toolkit";

const UISettingSlice = createSlice({
  name: "UISetting",
  initialState: {
    browserWidth: window.innerWidth,
    isDark: false,
    isSideBar: false,
    modal: {
      isModalOpen: false,
      isBackdropClose: false,
      content: '',
      negativeButtonTitle: '',
      positiveButtonTitle: '',
    },
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
    setModal: (state, action) => {
      state.modal = action.payload;
    },
  },
});

export default UISettingSlice;
export const { setIsDark, setBrowserWidth, setIsSideBar, setModal } =
  UISettingSlice.actions;
