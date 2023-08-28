import { createSlice } from "@reduxjs/toolkit";

const UISettingSlice = createSlice({
    name: "UISetting",
    initialState: {
        browserWidth: window.innerWidth,
        isDark: false,
    },
    reducers: {
        setIsDark: (state, action) => {
            state.isDark = action.payload;
        },
        setBrowserWidth: (state, action) => {
            state.browserWidth = action.payload;
        },
    }
});

export default UISettingSlice;
export const { setIsDark, setBrowserWidth } = UISettingSlice.actions;