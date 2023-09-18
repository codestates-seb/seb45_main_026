import { styled } from "styled-components";
import { useSelector } from 'react-redux';
import tokens from '../../../styles/tokens.json';
import { BodyTextTypo, Heading5Typo } from '../../../atoms/typographys/Typographys';
import { RegularInput } from '../../../atoms/inputs/Inputs';
import { RegularTextArea } from '../../../atoms/inputs/TextAreas';
import { RegularButton } from "../../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const UploadModal = ({
  setModal,
  isProblem,
  handleChangeContent,
  handleCreateProblem,
  initProblem,
}) => {
  const isDark = useSelector(state=>state.uiSetting.isDark);

  return (
    <ModalBackground
    isDark={isDark}
      onClick={() => {
        setModal(false);
        initProblem();
      }}
    >
      <ProblemModal
        isDark={isDark}
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        <ProblemTitle isDark={isDark}>
          문제 등록하기
          <TitleInput
            isDark={isDark}
            id="ProblemTitle"
            type="text"
            placeholder="문제의 지문을 입력해주세요."
            onChange={(e) => handleChangeContent(e)}
            value={isProblem.content}
          />
        </ProblemTitle>
        <ProblemContent isDark={isDark}>
          <ProblemLists isDark={isDark}>
            {[1, 2, 3, 4].map((el) => (
              <ProblemList isDark={isDark}>
                <CheckNumber
                  id="questionAnswer"
                  type="checkbox"
                  onChange={(e) => {
                    handleChangeContent(e, el);
                  }}
                  checked={isProblem.questionAnswer === el}
                />
                <ListLabel isDark={isDark}>{el}번 문항</ListLabel>
                <ListInput
                  isDark={isDark}
                  id="selections"
                  type="text"
                  placeholder={`${el}번 문항을 입력해주세요.`}
                  onChange={(e) => handleChangeContent(e, el)}
                  value={isProblem.selections[el - 1]}
                />
              </ProblemList>
            ))}
            <ProblemList isDark={isDark}>
              <CommentLabel isDark={isDark}>해설</CommentLabel>
              <CommentInput
                isDark={isDark}
                id="ProblemDiscribe"
                type="text"
                placeholder="해설을 입력해 주세요."
                onChange={(e) => handleChangeContent(e)}
                value={isProblem.description}
              />
            </ProblemList>
          </ProblemLists>
        </ProblemContent>
        <SubmitBtn
          isDark={isDark}
          onClick={() => {
            if (!isProblem.content) {
              return alert("지문을 입력해 주세요.");
            }
            if (!isProblem.questionAnswer) {
              return alert("정답을 체크해 주세요.");
            }
            if (!isProblem.selections.length) {
              return alert("선택지들을 입력해 주세요.");
            }
            handleCreateProblem();
            setModal(false);
            initProblem();
          }}
        >
          문제 추가
        </SubmitBtn>
      </ProblemModal>
    </ModalBackground>
  );
};

export default UploadModal;

export const ModalBackground = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(255,255,255,0.15);
  width: 100vw;
  height: 100vh;
`;

export const ProblemModal = styled.div`
  position: relative;
  background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.White.value};
  border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  padding: 10px 30px 30px 30px;
`;

export const ProblemTitle = styled(Heading5Typo)`
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

export const RegularLabel = styled(BodyTextTypo)`
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

export const GrayInput = styled(RegularInput)`
  margin-left: 15px;
  padding-left: 10px;
`;

export const CheckNumber = styled.input`
  width: 20px;
  height: 20px;
  margin-top: 12px;
`;

export const TitleInput = styled(RegularTextArea)`
  width: 100%;
  height: 100px;
  /* max-width: 500px; */
  margin-top: 20px;
  padding: 10px 0px 0px 10px;
  resize: none;
`;

export const ListInput = styled(GrayInput)`
  width: 100%;
  height: 50px;
  max-width: 500px;
`;

export const CommentInput = styled(RegularTextArea)`
  width: 100%;
  height: 120px;
  /* max-width: 620px; */
  margin-left: 15px;
  padding: 10px 0px 0px 10px;
  resize: none;
`;

export const SubmitBtn = styled(RegularButton)`
  position: absolute;
  bottom: 3%;
  right: 4%;
  width: 100px;
  height: 40px;
  font-weight: ${globalTokens.Bold.value};
`;
