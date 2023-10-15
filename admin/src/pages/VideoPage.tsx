import { useSelector } from "react-redux";
import { RootState } from "../redux/Store";
import {
  MainContainer,
  NoResult,
  PageContainer,
} from "../atoms/layouts/PageContainer";
import { useNavigate } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { getVideoList } from "../services/videoService";
import { PageTitle } from "../styles/PageTitle";
import Loading from "../components/loading/Loading";
import { videoDataType } from "../types/videoDataType";
import VideoListItem from "../components/videoListPage/VideoListItem";
import Pagination from "../atoms/pagination/Pagination";
import { queryClient } from "..";
import { TableContainer } from "../atoms/table/Tabel";
import axios from "axios";
import { errorResponseDataType } from "../types/axiosErrorType";
import { useToken } from "../hooks/useToken";
import { BodyTextTypo } from "../atoms/typographys/Typographys";
import VideoListHeader from "../components/videoListPage/VideoListHeader";
import styled from "styled-components";
import { RegularButton } from "../atoms/buttons/Buttons";
import tokens from "../styles/tokens.json";
import { DarkMode } from "../types/reportDataType";

type SearchParams = {
  email: string;
  keyword: string;
};

const VideoPage = () => {
  const navigate = useNavigate();
  const refreshToken = useToken();
  const isLogin = useSelector((state: RootState) => state.loginInfo.isLogin);
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [maxPage, setMaxPage] = useState<number>(10);
  const [isEmail, setEmail] = useState<string>("");
  const [isKeyword, setKeyword] = useState<string>("");
  const [searchParams, setSearchParams] = useState<SearchParams>({
    email: "",
    keyword: "",
  });
  const searchList = ["강의명", "이메일"];
  const [searchFilter, setSearchFilter] = useState("강의명");

  const { isLoading, error, data, isPreviousData } = useQuery({
    queryKey: ["videos", currentPage, searchParams, accessToken],
    queryFn: async () => {
      try {
        const response = await getVideoList(
          accessToken.authorization,
          searchParams.email,
          searchParams.keyword,
          currentPage,
          10
        );
        return response;
      } catch (err) {
        if (axios.isAxiosError<errorResponseDataType, any>(err)) {
          if (err.response?.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        }
      }
    },
    keepPreviousData: false,
    staleTime: 1000 * 60 * 5,
    cacheTime: 1000 * 60 * 30,
    retry: 3, //error를 표시하기 전에 실패한 요청을 다시 시도하는 횟수
    retryDelay: 1000,
  });

  useEffect(() => {
    if (!isLogin) {
      navigate("/login");
      return;
    }
    if (data) {
      setMaxPage(data.pageInfo.totalPage);
    }
    if (!isPreviousData && data?.hasMore) {
      queryClient.prefetchQuery({
        queryKey: ["videos", currentPage + 1, searchParams, accessToken],
        queryFn: async () => {
          try {
            const response = await getVideoList(
              accessToken.authorization,
              "",
              "",
              currentPage + 1,
              10
            );
            return response;
          } catch (err) {
            if (axios.isAxiosError<errorResponseDataType, any>(err)) {
              if (err.response?.data.message === "만료된 토큰입니다.") {
                refreshToken();
              }
            } else {
              console.log(err);
            }
          }
        },
      });
    }
  }, [isLogin, data, isPreviousData, currentPage, searchParams, queryClient]);

  const handleChangeSearch = (e: React.ChangeEvent<HTMLInputElement>): void => {
    if (searchFilter === "강의명") {
      setEmail("");
      setKeyword(e.target.value);
    } else if (searchFilter === "이메일") {
      setEmail(e.target.value);
      setKeyword("");
    }
  };

  const handleKeyEnter = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const handleSearch = () => {
    if (searchFilter === "강의명") {
      setSearchParams({ ...searchParams, keyword: isKeyword });
    } else if (searchFilter === "이메일") {
      setSearchParams({ ...searchParams, email: isEmail });
    }
    setCurrentPage(1);
  };

  const [isDropdown, setDropdown] = useState<boolean>(false);

  return (
    <PageContainer isDark={isDark}>
      <MainContainer isDark={isDark}>
        <PageTitle isDark={isDark}>강의 관리</PageTitle>
        <SearchBox>
          <SearchFiiterBox>
            <SearchFiiter
              isDark={isDark}
              onClick={() => setDropdown(!isDropdown)}
            >
              <DropdownArrow>{isDropdown ? "▲" : "▼"}</DropdownArrow>
              {searchFilter}
            </SearchFiiter>
            {isDropdown && (
              <SearchDropdown isDark={isDark}>
                {searchList.map((el, idx) => (
                  <SearchDropdownItem
                    isDark={isDark}
                    key={idx}
                    onClick={() => {
                      setSearchFilter(el);
                      setDropdown(!isDropdown);
                    }}
                  >
                    {el}
                  </SearchDropdownItem>
                ))}
              </SearchDropdown>
            )}
          </SearchFiiterBox>
          <SearchInput
            isDark={isDark}
            onChange={(e) => handleChangeSearch(e)}
            onKeyUp={(e) => handleKeyEnter(e)}
          />
          <SearchBtn isDark={isDark} onClick={() => handleSearch()}>
            검색
          </SearchBtn>
        </SearchBox>
        {isLoading ? (
          <Loading />
        ) : error ? (
          <>error</>
        ) : (
          <TableContainer>
            <VideoListHeader />
            {data.data.length > 0 ? (
              data.data.map((e: videoDataType) => (
                <VideoListItem key={e.videoId} item={e} />
              ))
            ) : (
              <NoResult>
                <BodyTextTypo isDark={isDark}>
                  강의 검색 결과가 없습니다.
                </BodyTextTypo>
              </NoResult>
            )}
          </TableContainer>
        )}
        {!isLoading && !error && data.data.length > 0 && (
          <Pagination
            isDark={isDark}
            maxPage={maxPage}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
          />
        )}
      </MainContainer>
    </PageContainer>
  );
};

export default VideoPage;

const globalTokens = tokens.global;

export const SearchBox = styled.div`
  width: 100%;
  display: flex;
  justify-content: end;
  align-items: center;
`;
export const SearchFiiterBox = styled.div`
  position: relative;
`;
export const SearchFiiter = styled(RegularButton)<DarkMode>`
  width: 85px;
  height: 30px;
  padding: 2px 10px;
  font-size: 14px;
  border: 1px solid black;
  border-radius: 8px;
  display: flex;
  align-items: center;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  &:hover {
    background-color: ${(props) =>
      props.isDark ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)"};
    color: ${(props) =>
      props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  }
`;
export const DropdownArrow = styled.span`
  font-size: 10px;
  margin-right: 10px;
`;
export const SearchDropdown = styled.div<DarkMode>`
  position: absolute;
  width: 85px;
  margin-top: 2px;
  border: 1px solid black;
  border-radius: 8px;
  z-index: 1;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  display: flex;
  flex-direction: column;
`;
export const SearchDropdownItem = styled.div<DarkMode>`
  width: 85px;
  padding: 5px 0px;
  text-align: center;
  font-size: 14px;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  transition: 300ms;
  cursor: pointer;
  &:hover {
    color: ${globalTokens.Gray.value};
  }
`;
export const SearchInput = styled.input<DarkMode>`
  width: 200px;
  height: 30px;
  margin: 0px 10px;
  padding: 0px 8px;
  font-size: 14px;
  border: 1px solid black;
  border-radius: 8px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`;

export const SearchBtn = styled.button<DarkMode>`
  width: 60px;
  height: 30px;
  border-radius: 8px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  &:hover {
    background-color: ${(props) =>
      props.isDark ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)"};
    color: ${(props) =>
      props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  }
`;
