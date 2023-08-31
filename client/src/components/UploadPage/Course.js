import { styled } from "styled-components";
import {
  UploadTitle,
  UploadSubtitle,
  SubDescribe,
  RowBox,
  ColBox,
} from "../../pages/contents/UploadPage";

export const CourseBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;

  width: 100%;
  max-width: 800px;
  padding: 20px;
`;

export const RegularLabel = styled.label`
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

export const GrayInput = styled.input`
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-left: 15px;
  padding-left: 6px;
`;

export const ChooseName = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  height: 50px;
`;

export const ChooseIntro = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  height: 100px;
`;

export const ChooseCategory = styled(GrayInput)`
  width: 100%;
  max-width: 240px;
  height: 50px;
`;

export const ChooseImage = styled.div`
  width: 100%;
  height: 200px;
  max-width: 250px;
  border-radius: 8px;
  background-color: rgb(236, 236, 236);
  margin: 0px 0px 10px 15px;
  padding-top: 90px;
  text-align: center;
  font-size: 14px;
`;

export const ChooseVideo = styled(GrayInput)`
  width: 100%;
  max-width: 500px;
  margin-bottom: 10px;
  height: 50px;
`;

const CourseSection = () => {
  return (
    <CourseBox>
      <UploadTitle>강의 등록하기</UploadTitle>
      <UploadSubtitle>강의 정보를 입력합니다.</UploadSubtitle>
      <ColBox>
        <RowBox>
          <CourseName>강의명</CourseName>
          <ChooseName type="text" placeholder="강의명을 입력해 주세요." />
        </RowBox>
        <RowBox>
          <CourseIntro>강의 소개</CourseIntro>
          <ChooseIntro type="text" placeholder="강의 소개를 입력해 주세요." />
        </RowBox>
        <RowBox>
          <CourseCategory>카테고리</CourseCategory>
          <ChooseCategory type="text" placeholder="카테고리를 선택해 주세요." />
        </RowBox>
        <RowBox>
          <CourseImage>썸네일 이미지</CourseImage>
          <ColBox>
            <ChooseImage>썸네일을 등록해 주세요</ChooseImage>
            <SubDescribe>
              썸네일 이미지는 png, jpg만 등록이 가능합니다.
            </SubDescribe>
            <SubDescribe>권장 이미지 크기 : 291px &times; 212px</SubDescribe>
          </ColBox>
        </RowBox>
        <RowBox>
          <CourseVideo>강의 영상</CourseVideo>
          <ColBox>
            <ChooseVideo type="text" placeholder="강의 영상을 선택해 주세요." />
            <SubDescribe>강의 영상은 mp4만 등록이 가능합니다.</SubDescribe>
            <SubDescribe>권장 화면 비율 : 1920 &times; 1080</SubDescribe>
            <SubDescribe>최대 영상 크기 : 1GB</SubDescribe>
          </ColBox>
        </RowBox>
      </ColBox>
    </CourseBox>
  );
};

export default CourseSection;
