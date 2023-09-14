import { styled } from "styled-components";
import {
  UploadTitle,
  UploadSubtitle,
  SubDescribe,
  RowBox,
  ColBox,
} from "../../pages/contents/CourseUploadPage";
import { useMemo, useRef, useState } from "react";
import axios from "axios";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { RegularInput } from "../../atoms/inputs/Inputs";
import { RegularTextArea } from "../../atoms/inputs/TextAreas";
import { useToken } from "../../hooks/useToken";
import Loading from "../../atoms/loading/Loading";
import tokens from '../../styles/tokens.json';
import { BigButton } from "../../atoms/buttons/Buttons";

const globalToken = tokens.global;

const CourseUpload = ({ isTags }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const imgRef = useRef();
  const videoRef = useRef();
  const navigate = useNavigate();
  const refreshToken = useToken();
  const [imgFile, setImgFile] = useState("");
  const [videoFile, setVideoFile] = useState("");
  const [uploadVideo, setUploadVideo] = useState({
    imageType: "",
    fileName: "",
  });
  const [uploadDetail, setUploadDetail] = useState({
    videoName: "",
    price: null,
    description: "",
    categories: [],
  });
  const [presignedUrl, setPresignedUrl] = useState({
    thumbnailUrl: "",
    videoUrl: "",
  });
  const [isLoading, setLoading] = useState(false);
  const [isComplete, setComplete] = useState(false);
  const [tagList, setTagList] = useState([]); // 현재 추가한 카테고리
  const tagListLower = tagList.map((el) => el.toLowerCase()); // tagList 대소문자 판별
  const tagsData = isTags.map((el) => el.categoryName); // 실제 tag data 리스트
  const tagsDataLower = tagsData.map((el) => el.toLowerCase()); // tagsData 대소문자 판별
  const [tagOpen, setTagOpen] = useState(false); // tag 드롭다운 열고 닫기

  const handleSaveFile = (e) => {
    const file = e.target.files[0];
    if (file) {
      const type = file.type;
      const [fileName, fileType] = file.name.split(".");
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onloadend = () => {
        if (type.includes("image")) {
          setImgFile(reader.result);
          setUploadVideo({ ...uploadVideo, imageType: fileType });
        } else if (type.includes("video")) {
          setVideoFile(reader.result);
          setUploadVideo({ ...uploadVideo, fileName });
          setUploadDetail({ ...uploadDetail, videoName: fileName });
        }
      };
    }
  };

  const handleBlurPrice = (price) => {
    const regExp = /[a-z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/g;
    if (regExp.test(price)) {
      setUploadDetail({ ...uploadDetail, price: 0 });
      alert("숫자만 입력해주세요.");
    }
  };

  const handleVideoPost = () => {
    if (!uploadDetail.price) {
      return alert("가격을 설정해 주세요.");
    }
    if (!uploadDetail.categories.length) {
      return alert("1개 이상의 카테고리를 설정해 주세요.");
    }

    if (!imgFile) {
      return alert("썸네일 이미지 파일을 올려주세요");
    }
    if (!videoFile) {
      return alert("동영상 파일을 올려주세요");
    }

    if (imgRef.current.files[0].name.split(".")[0] !== uploadDetail.videoName) {
      return alert("동일한 이름의 이미지와 동영상 파일을 업로드 해주세요.");
    }

    if (uploadVideo.fileName && uploadVideo.imageType) {
      return axios
        .post(
          "https://api.itprometheus.net/videos/presigned-url",
          uploadVideo,
          {
            headers: { Authorization: token.authorization },
          }
        )
        .then((res) => {
          setLoading(true);
          setPresignedUrl(res.data.data);
        })
        .catch((err) => {
          console.log(err);
          if (err.response.data.code === 409) {
            alert(`${err.response.data.message}`);
          } else if (err.response.data.code === 401) {
            refreshToken(() => handleVideoPost());
          } else {
            console.log(err);
          }
        });
    }
  };

  const handleImgUpload = () => {
    if (presignedUrl.thumbnailUrl && presignedUrl.videoUrl) {
      const file = imgRef.current.files[0];
      const ex = file.name.split(".")[1].toLowerCase();
      return axios
        .put(`${presignedUrl.thumbnailUrl}`, file, {
          headers: {
            "Content-type": `image/${ex}`,
          },
        })
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            handleVideoUpload();
          }
        })
        .catch((err) => {
          if (err.response.status === 503) {
            console.log(err);
            // handleImgUpload();
          }
        });
    }
  };

  const handleVideoUpload = () => {
    if (presignedUrl.videoUrl) {
      return axios
        .put(`${presignedUrl.videoUrl}`, videoRef.current.files[0], {
          headers: {
            "Content-type": "video/mp4",
          },
        })
        .then((res) => {
          if (res.status === 200) {
            handleDetailPost();
          }
        })
        .catch((err) => {
          if (err.response.status === 503) {
            console.log(err);
            // handleVideoUpload();
          }
        });
    }
  };

  const handleDetailPost = () => {
    if (!isComplete) {
      return axios
        .post("https://api.itprometheus.net/videos", uploadDetail, {
          headers: { Authorization: token.authorization },
        })
        .then((res) => {
          setLoading(false);
          setComplete(true);
          alert("성공적으로 강의가 등록 되었습니다.");
          if (window.confirm("강의 문제를 업로드 하시겠습니까?")) {
            navigate(`${res.headers.location}/problems/upload`);
          } else {
            navigate("/lecture");
          }
        })
        .catch((err) => {
          console.log(err);
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken(() => handleDetailPost());
          }
        });
    }
  };

  useMemo(() => handleImgUpload(), [presignedUrl]);

  const addTagList = (e) => {
    if (e.key === "Enter") {
      if (
        tagList.includes(e.target.value) ||
        tagListLower.includes(e.target.value.toLowerCase())
      ) {
        alert("이미 존재하는 카테고리입니다.");
        return;
      }
      if (
        !tagsData.includes(e.target.value) &&
        !tagsDataLower.includes(e.target.value.toLowerCase())
      ) {
        alert("존재하지 않는 카테고리 입니다.");
        return;
      }
      if (e.target.value && !tagList.includes(e.target.value)) {
        setTagList([...tagList, e.target.value]);
        e.target.value = "";
        return;
      }
    } else if (e.key === "Backspace" && !e.target.value) {
      if (tagList.length) {
        setTagList(tagList.filter((el, idx) => idx !== tagList.length - 1));
        return;
      }
    }
  };

  const handleAddTags = (el) => {
    if (tagList.includes(el) || tagListLower.includes(el.toLowerCase())) {
      alert("이미 존재하는 카테고리입니다.");
      return;
    }
    if (!tagList.includes(el)) {
      setTagList([...tagList, el]);
    }
  };

  const removeTagList = (el) => {
    setTagList(tagList.filter((tag) => tag !== el));
  };

  return (
    <CourseBox>
      <UploadTitle isDark={isDark}>강의 등록하기</UploadTitle>
      <UploadSubtitle isDark={isDark}>강의 정보를 입력합니다.</UploadSubtitle>
      <ColBox>
        <RowBox>
          <CourseName isDark={isDark}>강의명</CourseName>
          <ChooseName
            isDark={isDark}
            type="text"
            placeholder="강의명은 영상 파일의 제목으로 업로드 됩니다."
            value={uploadDetail.videoName}
            disabled
          />
        </RowBox>
        <RowBox>
          <CourseIntro isDark={isDark}>강의 소개</CourseIntro>
          <ChooseIntro
            isDark={isDark}
            type="text"
            placeholder="강의 소개를 입력해 주세요."
            value={uploadDetail.description}
            onChange={(e) => {
              setUploadDetail({ ...uploadDetail, description: e.target.value });
            }}
          />
        </RowBox>
        <RowBox>
          <CourseCategory isDark={isDark}>가격</CourseCategory>
          <ChoosePrice
            isDark={isDark}
            type="text"
            placeholder="가격을 설정해 주세요."
            value={uploadDetail.price}
            onChange={(e) => {
              setUploadDetail({ ...uploadDetail, price: e.target.value });
            }}
            onBlur={(e) => handleBlurPrice(e.target.value)}
          />
        </RowBox>
        <RowBox>
          <CourseCategory isDark={isDark}>카테고리</CourseCategory>
          <CategoryBox isDark={isDark}>
            <CategoryLists isDark={isDark}>
              {tagList.map((el, idx) => (
                <CategoryList isDark={isDark} key={idx}>
                  {el}
                  <CategoryListBtn isDark={isDark} onClick={() => removeTagList(el)}>
                    &times;
                  </CategoryListBtn>
                </CategoryList>
              ))}
            </CategoryLists>
            <ChooseCategory
              isDark={isDark}
              type="text"
              placeholder={!tagList.length ? "카테고리를 설정해 주세요." : ""}
              onKeyDown={(e) => addTagList(e)}
              onFocus={() => setTagOpen(true)}
              onBlur={() => {
                setTimeout(() => {
                  setUploadDetail({ ...uploadDetail, categories: tagList });
                  setTagOpen(false);
                }, 100);
              }}
            />
            {tagOpen && (
              <TagDropDown isDark={isDark}>
                {tagsData.map((el, idx) => (
                  <TagDropDownList
                    isDark={isDark}
                    key={idx}
                    onClick={() => {
                      handleAddTags(el);
                      setTagOpen(false);
                    }}
                  >
                    {el}
                  </TagDropDownList>
                ))}
              </TagDropDown>
            )}
          </CategoryBox>
        </RowBox>

        <RowBox>
          <CourseImage isDark={isDark}>썸네일 이미지</CourseImage>
          <ColBox>
            <ChooseImageInupt
              id="imageUpload"
              type="file"
              accept="image/png image/jpg image/jpeg"
              onChange={handleSaveFile}
              ref={imgRef}
            />
            <ChooseImageBtn isDark={isDark} htmlFor="imageUpload">
              {imgFile ? (
                <ChooseImage src={imgFile} alt="프로필 이미지" />
              ) : (
                <ChooseSpan isDark={isDark}>썸네일을 등록해 주세요.</ChooseSpan>
              )}
            </ChooseImageBtn>
            <SubDescribe isDark={isDark}>
              썸네일 이미지는 png, jpg, jpeg 확장자만 등록이 가능합니다.
            </SubDescribe>
            <SubDescribe isDark={isDark}>
              권장 이미지 크기 : 291px &times; 212px
            </SubDescribe>
          </ColBox>
        </RowBox>

        <RowBox>
          <CourseVideo isDark={isDark}>강의 영상</CourseVideo>
          <ColBox>
            <ChooseVideo
              isDark={isDark}
              type="file"
              accept="video/mp4"
              onChange={handleSaveFile}
              ref={videoRef}
            />
            <SubDescribe isDark={isDark}>
              강의 영상은 mp4만 등록이 가능합니다.
            </SubDescribe>
            <SubDescribe isDark={isDark}>
              권장 화면 비율 : 1920 &times; 1080
            </SubDescribe>
            <SubDescribe isDark={isDark}>최대 영상 크기 : 1GB</SubDescribe>
          </ColBox>
        </RowBox>
        <SubmitCourse isDark={isDark} onClick={handleVideoPost}>강의 등록 완료</SubmitCourse>
      </ColBox>
      <Loading isLoading={isLoading} />
    </CourseBox>
  );
};

export default CourseUpload;

export const CourseBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;

  width: 100%;
  max-width: 800px;
  padding: 20px;
`;

export const RegularLabel = styled(BodyTextTypo)`
  width: 100%;
  max-width: 100px;
  text-align: end;
  margin-top: 10px;
`;

export const CourseName = styled(RegularLabel)``;
export const CourseIntro = styled(RegularLabel)``;
export const CourseCategory = styled(RegularLabel)``;
export const CourseImage = styled(RegularLabel)``;
export const CourseVideo = styled(RegularLabel)``;

export const GrayInput = styled(RegularInput)`
  margin-left: 15px;
  padding-left: 10px;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
`;

export const ChooseName = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  height: 50px;
`;

export const ChooseIntro = styled(RegularTextArea)`
  width: 100%;
  max-width: 500px;
  height: 100px;
  margin-left: 15px;
  padding: 10px 0px 0px 10px;
  resize: none;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
`;

export const CategoryBox = styled(RegularLabel)`
  position: relative;
  width: 100%;
  max-width: 500px;
  height: 50px;
  margin-left: 15px;
  display: flex;
  justify-content: start;
  border-radius: 8px;
  /* border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value}; */
`;

export const TagDropDown = styled.ul`
  position: absolute;
  top: 48px;
  left: 0px;
  width: 100%;
  max-width: 500px;
  padding: 0px 10px;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
  border-radius: 8px;
  background-color: ${props=>props.isDark?globalToken.Black.value:globalToken.Background.value};
  flex-wrap: wrap;
  display: flex;
`;
export const TagDropDownList = styled.li`
  height: 38px;
  margin: 10px 6px;
  padding: 5px 10px;
  background-color: ${props=>props.isDark?globalToken.MainNavy.value:globalToken.LightRed.value};
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
  border-radius: 8px;
  cursor: pointer;
`;

export const CategoryLists = styled.ul`
  height: 46px;
  display: flex;
  justify-content: start;
  align-items: center;
  border-radius: 8px;
`;
export const CategoryList = styled.li`
  height: 35px;
  margin-left: 5px;
  padding: 4px 8px 4px 10px;
  display: flex;
  justify-content: start;
  border-radius: 8px;
  background-color: ${props=>props.isDark?globalToken.MainNavy.value:globalToken.LightRed.value};
`;

export const CategoryListBtn = styled.button`
  margin-left: 5px;
`;

export const ChooseCategory = styled(RegularInput)`
  width: 100%;
  height: 46px;
  border: none;
  padding-left: 10px;
  border-radius: 8px;
  font-size: 16px;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
  &:focus {
    outline: none;
  }
`;

export const ChoosePrice = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  height: 50px;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
  &:focus {
    outline: none;
  }
`;

export const ChooseImageInupt = styled.input`
  display: none;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
`;

export const ChooseImage = styled.img`
  width: 100%;
  height: 196px;
  max-width: 250px;
  border-radius: 8px;
  background-color: white;
  object-fit: scale-down;
`;

export const ChooseImageBtn = styled.label`
  width: 100%;
  height: 200px;
  max-width: 250px;
  border-radius: 8px;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':'rgba(0,0,0,0.15)'};
  margin: 0px 0px 10px 15px;
  cursor: pointer;

  display: flex;
  justify-content: center;
  align-items: center;

  transition: 300ms;

  &:hover {
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.25)':'rgba(0,0,0,0.25)'};
  }
`;

export const ChooseSpan = styled.span`
  color: ${props=>props.isDark?globalToken.LightGray.value:globalToken.Gray.value};
  text-align: center;
  font-size: 14px;
`;

export const ChooseVideo = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  margin-bottom: 10px;
  padding-top: 10px;
  height: 50px;
  border: 1px solid ${props=>props.isDark?globalToken.Gray.value:globalToken.LightGray.value};
`;

export const SubmitCourse = styled(BigButton)`
  position: absolute;
  bottom: 4%;
  left: 40%;
  width: 200px;
  height: 40px;
`;
