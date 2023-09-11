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
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { RegularInput } from "../../atoms/inputs/Inputs";
import { RegularTextArea } from "../../atoms/inputs/TextAreas";
import { useToken } from "../../hooks/useToken";
import { setIsLoading } from "../../redux/createSlice/UISettingSlice";

const CourseUpload = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const imgRef = useRef();
  const videoRef = useRef();
  const navigate = useNavigate();
  const dispatch = useDispatch();
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
  const [isComplete, setComplete] = useState(false);

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
          dispatch(setIsLoading(true));
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
          dispatch(setIsLoading(false));
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
          <ChooseCategory
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
          <ChooseCategory
            isDark={isDark}
            type="text"
            placeholder="카테고리를 선택해 주세요."
            value={uploadDetail.categories}
            onChange={(e) => {
              setUploadDetail({
                ...uploadDetail,
                categories: [e.target.value],
              });
            }}
          />
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
            <ChooseImageBtn htmlFor="imageUpload">
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
        <SubmitCourse onClick={handleVideoPost}>강의 등록 완료</SubmitCourse>
      </ColBox>
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
`;

export const ChooseCategory = styled(GrayInput)`
  width: 100%;
  max-width: 240px;
  height: 50px;
`;

export const ChooseImageInupt = styled.input`
  display: none;
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
  border: 2px solid rgb(236, 236, 236);
  background-color: rgb(230, 230, 230);
  margin: 0px 0px 10px 15px;
  cursor: pointer;

  display: flex;
  justify-content: center;
  align-items: center;

  &:hover {
    background-color: rgb(220, 220, 220);
  }
`;

export const ChooseSpan = styled.span`
  color: rgb(120, 120, 120);
  text-align: center;
  font-size: 14px;
`;

export const ChooseVideo = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  margin-bottom: 10px;
  padding-top: 10px;
  height: 50px;
`;

export const SubmitCourse = styled.button`
  position: absolute;
  bottom: 4%;
  left: 40%;
  width: 200px;
  height: 40px;
  color: white;
  font-weight: 600;
  border-radius: 20px;
  background-color: rgb(255, 100, 100);
  &:hover {
    background-color: rgb(255, 150, 150);
  }
`;
