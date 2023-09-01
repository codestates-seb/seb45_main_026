import { styled } from "styled-components";

export const ModalBackground = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(200, 200, 200, 40%);
  width: 100vw;
  height: 100vh;
`;

export const ProblemModal = styled.div`
  position: relative;
  background-color: white;
  border: 2px solid rgb(200, 200, 200);
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  padding: 10px 30px 30px 30px;
`;

export const ProblemTitle = styled.h2`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  width: 100%;
  margin: 20px 0px;
`;

export const ProblemContent = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  margin-bottom: 40px;
`;

export const ProblemLists = styled.ul`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

export const ProblemList = styled.li`
  display: flex;
  flex-direction: row;
  justify-content: end;
  align-items: start;
  margin: 10px 0px;
`;

export const RegularLabel = styled.label`
  width: 100%;
  max-width: 80px;
  text-align: end;
  margin-top: 10px;
`;

export const TitleLabel = styled(RegularLabel)``;
export const ListLabel = styled(RegularLabel)``;
export const CommentLabel = styled(RegularLabel)`
  max-width: 50px;
  margin-top: 5px;
`;

export const GrayInput = styled.input`
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-left: 15px;
  padding-left: 10px;
`;

export const CheckNumber = styled.input`
  width: 20px;
  height: 20px;
  margin-top: 12px;
`;

export const TitleInput = styled.textarea`
  width: 100%;
  height: 100px;
  /* max-width: 500px; */
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-top: 20px;
  padding: 10px 0px 0px 10px;
`;

export const ListInput = styled(GrayInput)`
  width: 100%;
  height: 50px;
  max-width: 500px;
`;

export const CommentInput = styled.textarea`
  width: 100%;
  height: 120px;
  /* max-width: 620px; */
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-left: 15px;
  padding: 10px 0px 0px 10px;
`;

export const SubmitBtn = styled.button`
  position: absolute;
  bottom: 3%;
  right: 4%;
  width: 100px;
  height: 40px;
  color: white;
  font-weight: 600;
  border-radius: 8px;
  background-color: rgb(255, 100, 100);
  &:hover {
    background-color: rgb(255, 150, 150);
  }
`;

const UploadModal = ({ setModal }) => {
  return (
    <ModalBackground onClick={() => setModal(false)}>
      <ProblemModal
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        <ProblemTitle>
          문제 등록하기
          <TitleInput type="text" placeholder="문제의 지문을 입력해주세요." />
        </ProblemTitle>
        <ProblemContent>
          <ProblemLists>
            {[1, 2, 3, 4].map((el) => (
              <ProblemList>
                <CheckNumber type="checkbox" />
                <ListLabel>{el}번 문항</ListLabel>
                <ListInput
                  type="text"
                  placeholder={`${el}번 문항을 입력해주세요.`}
                />
              </ProblemList>
            ))}
            <ProblemList>
              <CommentLabel>해설</CommentLabel>
              <CommentInput type="text" placeholder="해설을 입력해 주세요." />
            </ProblemList>
          </ProblemLists>
        </ProblemContent>
        <SubmitBtn>문제 추가</SubmitBtn>
      </ProblemModal>
    </ModalBackground>
  );
};

export default UploadModal;
