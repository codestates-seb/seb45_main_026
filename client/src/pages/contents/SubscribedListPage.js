import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import { useSelector, useDispatch } from "react-redux";
import {PageContainer,MainContainer,} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import { useInView } from "react-intersection-observer";
import { setPage,setMaxPage  } from "../../redux/createSlice/FilterSlice";

const globalTokens = tokens.global;

const SubscribedMainContainer = styled(MainContainer)`
  min-width: 600px;
  min-height: 700px;
  margin-bottom: ${globalTokens.Spacing40.value}px;
  border: none;
`;

export default function SubscribedListPage() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const page = useSelector((state) => state.filterSlice.page);
    const maxPage = useSelector((state) => state.filterSlice.maxPage);

    return (
        <PageContainer isDark={isDark}>
            <SubscribedMainContainer>
                
            </SubscribedMainContainer>
        </PageContainer>
    )
}