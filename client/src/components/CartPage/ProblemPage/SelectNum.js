import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { setPage } from "../../../redux/createSlice/ProblemSlice";
import { ReactComponent as plus_circle } from "../../../assets/images/icons/plus_circle.svg";
import { useNavigate, useParams } from "react-router-dom";
import { useState } from "react";
import axios from "axios";

const SelectNum = () => {
  const { videoId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [isDisplay, setDisplay] = useState(null);
  const myId = useSelector((state) => state.loginInfo.myid);
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const setting = useSelector((state) => state.problemSlice.setting);
  const problemsData = useSelector((state) => state.problemSlice.data);

  const handleDeleteProblem = (questionId) => {
    if (window.confirm("강의 문제를 삭제하시겠습니까?")) {
      return axios
        .delete(`https://api.itprometheus.net/questions/${questionId}`, {
          headers: { Authorization: token.authorization },
        })
        .then((res) => {
          if (res.status === 204) {
            alert("강의 문제가 삭제 되었습니다.");
            window.location.reload();
          }
        })
        .catch((err) => console.log(err));
    }
  };

  return (
    <UploadType>
      {problemsData.map((el) => (
        <TypeBtnBox
          onMouseOver={() => {
            setDisplay(el.questionId);
          }}
          onMouseOut={() => {
            setDisplay(null);
          }}
        >
          {myId === videoDatas.channel.memberId &&
            isDisplay === el.questionId && (
              <DeleteProblemBtn
                onClick={() => handleDeleteProblem(el.questionId)}
              >
                &times;
              </DeleteProblemBtn>
            )}
          <UploadTypeBtn
            key={el.questionId}
            isActive={setting.isPage === el.position}
            onClick={() => dispatch(setPage(el.position))}
          >
            {el.position}
          </UploadTypeBtn>
        </TypeBtnBox>
      ))}
      {myId === videoDatas.channel.memberId && (
        <AddProblemBtn
          onClick={() => navigate(`/videos/${videoId}/problems/upload`)}
        />
      )}
    </UploadType>
  );
};

export default SelectNum;

export const UploadType = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
`;

export const TypeBtnBox = styled.div`
  display: flex;
  flex-direction: column;
`;
export const DeleteProblemBtn = styled.button`
  font-weight: bold;
`;

export const UploadTypeBtn = styled.button`
  width: 40px;
  height: 40px;
  margin: 10px 10px;
  border: 2px solid rgb(255, 100, 100);
  border-radius: 50%;
  background-color: ${(props) =>
    props.isActive ? "rgb(255, 100, 100)" : "white"};
  color: ${(props) => (props.isActive ? "white" : "rgb(255, 100, 100)")};
  font-weight: bold;
  font-size: 16px;
`;

export const AddProblemBtn = styled(plus_circle)`
  margin: 0px 5px;
  path {
    fill: rgb(255, 220, 220);
    transition: 200ms;
  }
  &:hover {
    path {
      fill: rgb(255, 100, 100);
      transition: 200ms;
    }
  }
`;
