import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { setPage } from "../../redux/createSlice/ProblemSlice";
import axios from "axios";
import { useState } from "react";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

const ProblemBox = ({ el }) => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const dispatch = useDispatch();
  const token = useSelector((state) => state.loginInfo.accessToken);
  const problemsData = useSelector((state) => state.problemSlice.data);
  const setting = useSelector((state) => state.problemSlice.setting);

  const [isConfirm, setConfirm] = useState(false);
  const [isDisable, setDisable] = useState(false);
  const [isAnswer, setAnswer] = useState("");
  const [isTrue, setTrue] = useState(null);

  const handleSubmit = (questionId) => {
    return axios
      .post(
        `https://api.itprometheus.net/questions/${questionId}/answers`,
        { myAnswer: isAnswer },
        { headers: { Authorization: token.authorization } }
      )
      .then((res) => {
        if (res.data.code === 200) {
          setConfirm(!isConfirm);
          setTrue(res.data.data);
          setDisable(true);
        }
      })
      .catch((err) => console.log(err));
  };

  return (
    <>
      <ProblemTitle>
        <ProblemContent isDark={isDark}>{el.content}</ProblemContent>
      </ProblemTitle>
      <ProblemLists>
        {el.choice ? (
          el.selections.map((li, idx) => (
            <ProblemList
              key={idx}
              isTrue={
                (isDisable && isTrue && isAnswer === idx + 1) ||
                (isDisable && el.questionAnswer === "answer" + (idx + 1))
              }
              isFalse={isDisable && !isTrue && isAnswer === idx + 1}
            >
              <ContentNum
                isDark={isDark}
                type="checkbox"
                checked={isAnswer === idx + 1}
                onChange={() => {
                  setAnswer(idx + 1);
                }}
                disabled={isDisable}
              />
              <ListContent isDark={isDark}>
                {idx + 1}. {li}
              </ListContent>
            </ProblemList>
          ))
        ) : (
          <ProblemInputBox isDark={isDark}>
            정답
            <ProblemInput
              isTrue={isDisable && isTrue && isAnswer}
              isFalse={isDisable && !isTrue && isAnswer}
              value={!isDisable ? isAnswer : el.questionAnswer}
              onChange={(e) => setAnswer(e.target.value)}
              placeholder="단답형으로 입력해주세요."
              disabled={isDisable}
              isDark={isDark}
            />
          </ProblemInputBox>
        )}
      </ProblemLists>

      <BtnBox>
        {setting.isPage !== 1 && (
          <PrevBtn
            isDark={isDark}
            onClick={() => {
              dispatch(setPage(setting.isPage - 1));
            }}
          >
            이전
          </PrevBtn>
        )}
        <ConfirmBtn
           isDark={isDark}
           isOpened={isConfirm}
           onClick={() => {
            if (!isAnswer) {
              alert("정답을 입력해주세요.");
            } else {
              handleSubmit(el.questionId);
            }
          }}
        >
          {!isConfirm ? "정답 확인" : "해설 닫기"}
        </ConfirmBtn>
        {setting.isPage !== problemsData.length && (
          <NextBtn
            isDark={isDark}
            onClick={() => {
              dispatch(setPage(setting.isPage + 1));
            }}
          >
            다음
          </NextBtn>
        )}
      </BtnBox>

      {isConfirm && (
        <DiscBox>
          <DiscName isDark={isDark}>해설</DiscName>
          <DiscContent isDark={isDark}>{el.description}</DiscContent>
        </DiscBox>
      )}
    </>
  );
};

export default ProblemBox;

export const ProblemTitle = styled.div`
  width: 100%;
  margin: 20px 0px;
  padding: 60px 20px;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
`;

export const ProblemContent = styled.span``;

export const ProblemLists = styled.ul`
  width: 100%;
  margin: 30px 0px;
`;

export const ProblemList = styled.li`
  width: 100%;
  margin: 15px 0px;
  padding: 10px 20px;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  background-color: ${(props) =>
    props.isTrue
      ? "rgb(255, 100, 100)"
      : props.isFalse
      ? "rgb(100, 100, 255)"
      : "white"};
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
`;

export const ContentNum = styled.input`
  width: 20px;
  height: 20px;
`;
export const ListContent = styled.label`
  width: 100%;
  height: 30px;
  margin-left: 30px;
  flex-wrap: wrap;
`;

export const BtnBox = styled.div`
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
`;

export const RegularBtn = styled.button`
  padding: 0px 20px;
  height: 40px;
  border: 1px solid ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
  border-radius: ${globalTokens.BigRadius.value}px;
`;

export const PrevBtn = styled(RegularBtn)`
  position: absolute;
  top: 0;
  left: 3%;
`;
export const ConfirmBtn = styled(RegularBtn)`
  background-color: ${(props) =>
    props.isOpened ? "rgb(255, 100, 100)" : "white"};
  color: ${(props) => (props.isOpened ? "white" : globalTokens.Black.value)};
`;

export const NextBtn = styled(RegularBtn)`
  position: absolute;
  top: 0;
  right: 3%;
`;

export const SubmitBtn = styled(RegularBtn)`
  position: absolute;
  top: 0;
  right: 3%;
  background-color: rgb(255, 100, 100);
  color: white;
`;

export const DiscBox = styled.div`
  width: 100%;
  margin: 30px 10px 10px 0px;
  display: flex;
  flex-direction: column;
`;

export const DiscName = styled(BodyTextTypo)`
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
  padding-left: 10px;
`;

export const DiscContent = styled.div`
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-top: 10px;
  padding: 20px;
  flex-wrap: wrap;
`;

export const ProblemInputBox = styled.div`
  width: 100%;
  display: flex;
  justify-content: end;
  align-items: center;
  font-weight: 600;
`;

export const ProblemInput = styled.input`
  width: 90%;
  height: 50px;
  background-color: ${(props) =>
    props.isTrue
      ? "rgb(255, 100, 100)"
      : props.isFalse
      ? "rgb(100, 100, 255)"
      : "white"};
  color: ${(props) =>
    props.isTrue ? "white" : props.isFalse ? "white" : globalTokens.Black.value};
  font-size: 16px;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-left: 30px;
  padding: 20px;
`;
