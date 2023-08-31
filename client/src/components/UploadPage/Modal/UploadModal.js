import { styled } from "styled-components";

const UploadModal = () => {
  return (
    <div>
      <div>
        <label>문제 이미지</label>
        <div>
          <div>문제 이미지를 등록해 주세요.</div>
          <span>썸네일 이미지는 png, jpg만 등록이 가능합니다.</span>
          <span>권장 이미지 크기 : 550px &times; 250px</span>
        </div>
      </div>
      <div>
        <div>
          <span>문항</span>
          <span>정답 여부</span>
        </div>
        <div>
          <label>1번 문항</label>
          <input type="text" placeholder="1번 문항을 입력해주세요." />
          <input type="checkbox" />
        </div>
        <div>
          <label>2번 문항</label>
          <input type="text" placeholder="2번 문항을 입력해주세요." />
          <input type="checkbox" />
        </div>
        <div>
          <label>3번 문항</label>
          <input type="text" placeholder="3번 문항을 입력해주세요." />
          <input type="checkbox" />
        </div>
        <div>
          <label>4번 문항</label>
          <input type="text" placeholder="4번 문항을 입력해주세요." />
          <input type="checkbox" />
        </div>
        <div>
          <label>해설</label>
          <input type="text" placeholder="해설을 입력해 주세요." />
        </div>
      </div>
    </div>
  );
};

export default UploadModal;
