import { styled } from "styled-components";
import axios from "axios";
import { useSelector } from "react-redux";
import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { RegularInput } from "../../../atoms/inputs/Inputs";
import { ReactComponent as StarYellow } from "../../../assets/images/icons/star/starYellow.svg";
import ReviewStar from "../../../components/DetailPage/ReviewStar";
import ReviewList from "../../../components/DetailPage/ReviewList";
import Pagination from "../../../components/DetailPage/Pagination";
import tokens from "../../../styles/tokens.json";
import {
  BodyTextTypo,
  Heading5Typo,
} from "../../../atoms/typographys/Typographys";
import { RegularButton, TextButton } from "../../../atoms/buttons/Buttons";
import { AlertModal } from "../../../atoms/modal/Modal";

const globalTokens = tokens.global;

const DetailReview = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const { videoId } = useParams();
  const [isParams, setParams] = useState({
    page: 1,
    size: 8,
    sort: "", // || star
    star: "", // 1 ~ 10
  });
  const [isReply, setReply] = useState({
    content: "",
    star: 0,
  });
  const [isActive, setActive] = useState(1);
  const [isReviews, setReviews] = useState([]);
  const [isPage, setPage] = useState({ page: 1, totalPage: 1 });
  const myId = useSelector((state) => state.loginInfo.myid);
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isModalOpen, setIsModalOpen] = useState({
    isModalOpen: false,
    content: "",
  });

  const getReview = () => {
    const queryString = new URLSearchParams(isParams).toString();
    return axios
      .get(
        `https://api.itprometheus.net/videos/${videoId}/replies?${queryString}`,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => {
        setPage({ ...isPage, ...res.data.pageInfo });
        setReviews(res.data.data);
      })
      .catch((err) => console.log(err));
  };

  const postReview = () => {
    if (myId === videoDatas.channel.memberId) {
      return setIsModalOpen({
        ...isModalOpen,
        isModalOpen: true,
        content: "내 강의에는 리뷰를 쓸 수 없습니다.",
      });
    }
    if (!isReply.content) {
      return setIsModalOpen({
        ...isModalOpen,
        isModalOpen: true,
        content: "감상평을 입력해주세요.",
      });
    }
    if (!isReply.star) {
      return setIsModalOpen({
        ...isModalOpen,
        isModalOpen: true,
        content: "별점을 선택해주세요.",
      });
    }
    return axios
      .post(`https://api.itprometheus.net/videos/${videoId}/replies`, isReply, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        if (res.status === 201) {
          setIsModalOpen({
            ...isModalOpen,
            isModalOpen: true,
            content: "성공적으로 댓글이 등록되었습니다.",
          });
        }
        setReply({ content: "", star: 0 });
        getReview();
      })
      .catch((err) => {
        if (err.response.status === 403) {
          return setIsModalOpen({
            ...isModalOpen,
            isModalOpen: true,
            content: "구매한 강의만 리뷰를 남길 수 있습니다.",
          });
        }
        if (err.response.status === 404) {
          return setIsModalOpen({
            ...isModalOpen,
            isModalOpen: true,
            content: "수강평은 한 번만 작성할 수 있습니다.",
          });
        }
      });
  };

  const handleChangeReply = (e) => {
    setReply({ ...isReply, content: e.target.value });
  };

  useEffect(() => {
    getReview();
  }, []);

  useMemo(() => {
    getReview();
  }, [isParams.page, isParams.sort, isParams.star]);

  return (
    <>
      <ReviewContainer isDark={isDark}>
        <ReviewTitle isDark={isDark}>수강평 {isReviews.length}</ReviewTitle>
        {videoDatas.isPurchased && (
          <ReviewForm isDark={isDark}>
            <ReviewLabel isDark={isDark}>리뷰</ReviewLabel>
            <WriteTitle isDark={isDark}>별점을 선택해주세요.</WriteTitle>
            <ReviewStar isDark={isDark} isStar={isReply} setStar={setReply} />
            <ReviewSubmit isDark={isDark}>
              <ReviewInput
                isDark={isDark}
                placeholder="한 줄 감상평을 등록해주세요."
                value={isReply.content}
                onChange={(e) => handleChangeReply(e)}
              />
              <ReviewBtn
                isDark={isDark}
                onClick={(e) => {
                  e.preventDefault();
                  postReview();
                }}
              >
                등록
              </ReviewBtn>
            </ReviewSubmit>
          </ReviewForm>
        )}
        <Reviews isDark={isDark}>
          <FilterBtns isDark={isDark}>
            <FilterBtn
              isDark={isDark}
              isActive={isActive === 1}
              onClick={() => {
                setActive(1);
                setParams({ ...isParams, sort: "created-date" });
              }}
            >
              최신순
            </FilterBtn>
            <FilterBtn
              isDark={isDark}
              isActive={isActive === 2}
              onClick={() => {
                setActive(2);
                setParams({ ...isParams, sort: "star" });
              }}
            >
              별점순
            </FilterBtn>
            <FilterBtn
              isDark={isDark}
              isActive={isActive === 3}
              onClick={() => {
                setActive(3);
              }}
            >
              별점별
              {isActive === 3 && (
                <>
                  <Star />
                  {isParams.star}
                  <FilterStar
                    type="range"
                    max={10}
                    min={1}
                    step={1}
                    value={isParams.star}
                    onChange={(e) => {
                      setParams({
                        ...isParams,
                        sort: "",
                        star: e.target.value,
                      });
                    }}
                  />
                </>
              )}
            </FilterBtn>
          </FilterBtns>
          {!isReviews.length ? (
            <ReviewEmpty>현재 리뷰가 없습니다.</ReviewEmpty>
          ) : (
            <ReviewLists isDark={isDark}>
              {isReviews.map((el, idx) => (
                <ReviewList key={idx} el={el} getReview={getReview} />
              ))}
            </ReviewLists>
          )}
        </Reviews>
        <Pagination
          isDark={isDark}
          isPage={isPage}
          setParams={setParams}
          isParams={isParams}
        />
      </ReviewContainer>
      <AlertModal
        isModalOpen={isModalOpen.isModalOpen}
        setIsModalOpen={setIsModalOpen}
        isBackdropClickClose={false}
        content={isModalOpen.content}
        buttonTitle="확인"
        handleButtonClick={() =>
          setIsModalOpen({
            ...isModalOpen,
            isModalOpen: false,
          })
        }
      />
    </>
  );
};

export default DetailReview;

export const ReviewContainer = styled.div`
  width: 100%;
  border-radius: ${globalTokens.BigRadius.value}px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 20px 0px;
  padding: 5px 20px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
`;

export const ReviewTitle = styled(Heading5Typo)`
  width: 100%;
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  font-weight: ${globalTokens.Bold.value};
  padding: 5px;
  margin: 10px 0px;
`;

export const ReviewForm = styled.form`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  width: 100%;
  padding: 10px;
  border-radius: ${globalTokens.RegularRadius.value}px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
`;

export const ReviewLabel = styled(BodyTextTypo)`
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  border-radius: ${globalTokens.BigRadius.value}px;
  width: 60px;
  height: 30px;
  text-align: center;
  padding: 0px;
  margin: ${globalTokens.Spacing8.value}px 0px;
`;

export const WriteTitle = styled(BodyTextTypo)`
  margin-bottom: 10px;
`;

export const ReviewSubmit = styled.div`
  margin: 10px 0px;
  width: 100%;
  max-width: 600px;
`;

export const ReviewInput = styled(RegularInput)`
  width: 100%;
  max-width: 500px;
  height: 45px;
  margin-right: ${globalTokens.Spacing8.value}px;
`;

export const ReviewBtn = styled(RegularButton)`
  width: 100%;
  max-width: 80px;
  height: 45px;
`;

export const Reviews = styled.div`
  width: 100%;
`;

export const FilterBtns = styled.div`
  width: 100%;
  display: flex;
  justify-content: start;
`;

export const FilterBtn = styled(TextButton)`
  background: none;
  border-radius: ${globalTokens.RegularRadius.value}px;
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  background-color: ${(props) =>
    props.isActive && props.isDark
      ? globalTokens.Black.value
      : props.isActive && !props.isDark
      ? globalTokens.Background.value
      : !props.isActive && props.isDark
      ? "rgba(0,0,0,0)"
      : globalTokens.White.value};
  padding: 5px 10px;
  margin-left: ${globalTokens.Spacing8.value}px;
  margin-top: ${globalTokens.Spacing20.value}px;
  display: flex;
  justify-content: start;
  align-items: center;
`;

export const Star = styled(StarYellow)`
  width: 15px;
  height: 15px;
  margin: 0px 3px 0px 7px;
`;

export const FilterStar = styled.input`
  margin-left: 10px;
  overflow: hidden;
  appearance: none;
  background: none;

  &::-webkit-slider-runnable-track {
    width: 100%;
    cursor: pointer;
    border-radius: 10px;
    border: 1px solid gray;
    overflow: hidden;
  }

  &::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 20px;
    height: 20px;
    background: yellow;
    border-radius: 10px;
    box-shadow: 1px 1px 7px yellow;
    box-shadow: -100vw 0 0 99.5vw yellow;
  }
`;

export const ReviewLists = styled.ul`
  width: 100%;
  display: grid;
  flex-wrap: wrap;
  place-items: center;
  padding: 20px;

  @media screen and (min-width: 850px) {
    grid-template-columns: repeat(2, 50%);
    grid-column-gap: 10px;
  }
`;

export const ReviewEmpty = styled.div`
  width: 100%;
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
