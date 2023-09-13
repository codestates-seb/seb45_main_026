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

const DetailReview = () => {
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
      return alert("내 강의에는 리뷰를 쓸 수 없습니다.");
    }
    if (!isReply.content) {
      return alert("감상평을 입력해주세요.");
    }
    if (!isReply.star) {
      return alert("별점을 선택해주세요.");
    }
    return axios
      .post(`https://api.itprometheus.net/videos/${videoId}/replies`, isReply, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        if (res.status === 201) {
          alert("성공적으로 댓글이 등록되었습니다.");
        }
        setReply({ content: "", star: 0 });
        // window.location.reload();
        getReview();
      })
      .catch((err) => {
        if (err.response.status === 403) {
          alert("구매한 강의만 리뷰를 남길 수 있습니다.");
        }
        if (err.response.status === 404) {
          alert("수강평은 한 번만 작성할 수 있습니다.");
        }
      });
  };

  const handleChangeReply = (e) => {
    setReply({ ...isReply, content: e.target.value });
  };

  useEffect(() => {
    getReview();
  }, []); // isParams.page, isParams.sort, isParams.star

  useMemo(() => {
    getReview();
  }, [isParams.page, isParams.sort, isParams.star]);

  return (
    <ReviewContainer>
      <ReviewTitle>수강평 {isReviews.length}</ReviewTitle>

      <ReviewForm>
        <ReviewLabel>리뷰</ReviewLabel>
        <WriteTitle>별점을 선택해주세요.</WriteTitle>
        <ReviewStar isStar={isReply} setStar={setReply} />
        <ReviewSubmit>
          <ReviewInput
            placeholder="한 줄 감상평을 등록해주세요."
            value={isReply.content}
            onChange={(e) => handleChangeReply(e)}
          />
          <ReviewBtn
            onClick={(e) => {
              e.preventDefault();
              postReview();
            }}
          >
            등록
          </ReviewBtn>
        </ReviewSubmit>
      </ReviewForm>

      <Reviews>
        <FilterBtns>
          <FilterBtn
            isActive={isActive === 1}
            onClick={() => {
              setActive(1);
              setParams({ ...isParams, sort: "created-date" });
            }}
          >
            최신순
          </FilterBtn>
          <FilterBtn
            isActive={isActive === 2}
            onClick={() => {
              setActive(2);
              setParams({ ...isParams, sort: "star" });
            }}
          >
            별점순
          </FilterBtn>
          <FilterBtn
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
                    setParams({ ...isParams, sort: "", star: e.target.value });
                  }}
                />
              </>
            )}
          </FilterBtn>
        </FilterBtns>

        <ReviewLists>
          {isReviews.map((el, idx) => (
            <ReviewList key={idx} el={el} getReview={getReview} />
          ))}
        </ReviewLists>
      </Reviews>
      <Pagination isPage={isPage} setParams={setParams} isParams={isParams} />
    </ReviewContainer>
  );
};

export default DetailReview;

export const ReviewContainer = styled.div`
  width: 100%;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 20px 0px;
  padding: 5px 20px;
  background-color: white;
`;

export const ReviewTitle = styled.div`
  width: 100%;
  border-bottom: 2px solid rgb(236, 236, 236);
  font-weight: bold;
  font-size: 18px;
  background-color: white;
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
  background-color: rgb(220, 220, 250);
`;

export const ReviewLabel = styled.span`
  border: 2px solid rgb(250, 250, 220);
  border-radius: 20px;
  width: 60px;
  height: 30px;
  text-align: center;
  padding: 1px 0px;
  margin: 10px 0px;
`;

export const WriteTitle = styled.span`
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
  height: 50px;
  font-size: 16px;
  margin-right: 20px;
`;

export const ReviewBtn = styled.button`
  width: 100%;
  max-width: 80px;
  height: 50px;
  font-size: 16px;
  border-radius: 10px;
`;

export const Reviews = styled.div`
  width: 100%;
`;

export const FilterBtns = styled.div`
  width: 100%;
  display: flex;
  justify-content: start;
`;

export const FilterBtn = styled.button`
  background: none;
  font-size: 16px;
  border-radius: 8px;
  border: 1px solid black;
  background-color: ${(props) => (props.isActive ? "black" : "white")};
  color: ${(props) => (props.isActive ? "white" : "black")};
  padding: 5px 10px;
  margin-left: 10px;
  margin-top: 30px;
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
    box-shadow: -100vw 0 0 99vw yellow;
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
