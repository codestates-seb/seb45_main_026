import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { useState } from "react";
import { setPage } from "../../../redux/createSlice/ProblemSlice";

export const UploadType = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
`;

export const UploadTypeBtn = styled.button`
  width: 40px;
  height: 40px;
  margin: 10px;
  border: 2px solid rgb(255, 100, 100);
  border-radius: 50%;
  background-color: ${(props) =>
    props.active ? "rgb(255, 100, 100);" : "white"};
  color: ${(props) => (props.active ? "white" : "rgb(255, 100, 100)")};
  font-weight: bold;
  font-size: 16px;
`;

const SelectNum = () => {
  const dispatch = useDispatch();
  const problemsData = useSelector((state) => state.problemSlice.data);
  const setting = useSelector((state) => state.problemSlice.setting);

  return (
    <UploadType>
      {problemsData.map((el) => (
        <UploadTypeBtn
          key={el.questionId}
          active={setting.isPage === el.position}
          onClick={() => dispatch(setPage(el.position))}
        >
          {el.position}
        </UploadTypeBtn>
      ))}
    </UploadType>
  );
};

export default SelectNum;
