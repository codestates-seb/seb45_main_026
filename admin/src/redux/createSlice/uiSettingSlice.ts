import { createSlice } from "@reduxjs/toolkit";

const uiSettingSlice = createSlice({
    name: 'uiSetting',
    initialState: {
        isDark: false,
    },
    reducers: {
        setIsDark: (state, action) => {
            state.isDark = action.payload;
        }
    }
});

export default uiSettingSlice;
export const { setIsDark } = uiSettingSlice.actions;