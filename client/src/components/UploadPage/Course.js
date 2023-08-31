import { styled } from "styled-components";
import { UploadTitle, UploadSubtitle } from "../../pages/contents/UploadPage";

export const CourseBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;

  width: 100%;
  max-width: 800px;
  padding: 20px;

  border: 1px solid red;
`;

export const RowBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
`;

export const ColBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
`;

const CourseSection = () => {
  return (
    <CourseBox>
      <UploadTitle>강의 등록하기</UploadTitle>
      <UploadSubtitle>강의 정보를 입력합니다.</UploadSubtitle>
      <ColBox>
        <RowBox>
          <label>강의명</label>
          <input type="text" placeholder="강의명을 입력해 주세요." />
        </RowBox>
        <RowBox>
          <label>강의 소개</label>
          <input type="text" placeholder="강의 소개를 입력해 주세요." />
        </RowBox>
        <RowBox>
          <label>카테고리</label>
          <input type="text" placeholder="카테고리를 선택해 주세요." />
        </RowBox>
        <RowBox>
          <label>썸네일 이미지</label>
          <ColBox>
            <div>썸네일을 등록해 주세요</div>
            <span>썸네일 이미지는 png, jpg만 등록이 가능합니다.</span>
            <span>권장 이미지 크기 : 291px &times; 212px</span>
          </ColBox>
        </RowBox>
        <RowBox>
          <label>강의 영상</label>
          <ColBox>
            <input type="text" placeholder="강의 영상을 선택해 주세요." />
            <span>강의 영상은 mp4만 등록이 가능합니다.</span>
            <span>권장 화면 비율 : 1920 &times; 1080</span>
            <span>최대 영상 크기 : 1GB</span>
          </ColBox>
        </RowBox>
        <button>다음</button>
      </ColBox>
    </CourseBox>
  );
};

export default CourseSection;
