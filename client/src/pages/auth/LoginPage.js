import React, { useMemo } from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useDispatch, useSelector } from 'react-redux';
import { setLocation } from '../../redux/createSlice/UISettingSlice';

const LoginPage = () => {
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);

    useMemo(()=>{
        dispatch(setLocation('/login'));

    },[]);

    return (
        <PageContainer isDark={isDark}>
            This is Login Page.
        </PageContainer>
    );
};

export default LoginPage;