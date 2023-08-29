import React, { useMemo } from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useDispatch, useSelector } from 'react-redux';
import { setLocation } from '../../redux/createSlice/UISettingSlice';

const MyProfilePage = () => {
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    
    useMemo(()=>{
        dispatch(setLocation('/myprofile'));
    },[]);
    return (
        <PageContainer isDark={isDark}>
            This is My Profile Page.
        </PageContainer>
    );
};

export default MyProfilePage;