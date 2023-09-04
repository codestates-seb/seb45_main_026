import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import {
  setAnswer,
  setConfirm,
  setDetail,
  setPage,
} from "../../../redux/createSlice/ProblemSlice";
import axios from "axios";

const ProblemBox = ({ el }) => {
  const dispatch = useDispatch();
  const token = useSelector((state) => state.loginInfo.accessToken);
  const problemsData = useSelector((state) => state.problemSlice.data);
  const setting = useSelector((state) => state.problemSlice.setting);

  const handleSubmit = (questionId) => {
    if (setting.isDetail) {
      return;
    }
    return axios
      .post(
        `https://api.itprometheus.net/questions/${questionId}/answers`,
        { myAnswer: setting.answers.answer },
        { headers: { Authorization: token.authorization } }
      )
      .then((res) => {
        console.log(res);
        dispatch(setDetail(true));
      })
      .catch((err) => console.log(err));
  };

  return (
    <>
      <ProblemTitle>
        <ProblemContent>{el.content}</ProblemContent>
      </ProblemTitle>

      <ProblemLists>
        {el.selections.map((li, idx) => (
          <ProblemList
            key={idx}
            isTrue={
              setting.answers.answer === idx + 1 ||
              parseInt(el.myAnswer) === idx + 1
            }
            isFalse={
              parseInt(el.myAnswer) === idx + 1 &&
              parseInt(el.myAnswer) !== parseInt(el.questionAnswer)
            }
          >
            <ContentNum
              type="checkbox"
              checked={
                idx + 1 === setting.answers.answer ||
                idx + 1 === parseInt(el.myAnswer)
              }
              onClick={() => {
                if (el.myAnswer === "") {
                  dispatch(
                    setAnswer({ questionId: el.questionId, answer: idx + 1 })
                  );
                }
              }}
            />
            <ListContent>
              {idx + 1}. {li}
            </ListContent>
          </ProblemList>
        ))}
      </ProblemLists>

      <BtnBox>
        {setting.isPage !== 1 && (
          <PrevBtn
            onClick={() => {
              dispatch(setPage(setting.isPage - 1));
            }}
          >
            이전
          </PrevBtn>
        )}

        <ConfirmBtn
          opened={el.myAnswer !== "" || setting.isDetail}
          onClick={() => {
            if (el.myAnswer === "") {
              handleSubmit(el.questionId);
            }
          }}
        >
          정답 확인
        </ConfirmBtn>

        {setting.isPage !== problemsData.length && (
          <NextBtn
            onClick={() => {
              dispatch(setPage(setting.isPage + 1));
            }}
          >
            다음
          </NextBtn>
        )}
      </BtnBox>

      {(el.myAnswer !== "" || setting.isDetail) && (
        <DiscBox>
          <DiscName>해설</DiscName>
          <DiscContent>{el.description}</DiscContent>
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
  border: 1px solid rgb(220, 220, 220);
  border-radius: 20px;
`;

export const PrevBtn = styled(RegularBtn)`
  position: absolute;
  top: 0;
  left: 3%;
`;
export const ConfirmBtn = styled(RegularBtn)`
  background-color: ${(props) =>
    props.opened ? "rgb(255, 100, 100)" : "white"};
  color: ${(props) => (props.opened ? "white" : "black")};
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

export const DiscName = styled.span`
  color: gray;
  padding-left: 10px;
`;

export const DiscContent = styled.div`
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-top: 10px;
  padding: 20px;
  flex-wrap: wrap;
`;
