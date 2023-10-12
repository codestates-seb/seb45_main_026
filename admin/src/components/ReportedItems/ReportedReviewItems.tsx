import styled from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { RootState } from "../../redux/Store";
import { useLocate } from "../../hooks/useLocation";
import {
  DarkMode,
  UseLocateType,
  ReportReviewDataType,
} from "../../types/reportDataType";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { useState } from "react";
import ReviewReportList from "../reportPage/ReviewReportList";
import { ReactComponent as arrowPrev } from "../../assets/images/icons/arrowPrev.svg";
import { ReactComponent as arrowNext } from "../../assets/images/icons/arrowNext.svg";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";
import { queryClient } from "../..";
import { ROOT_URL } from "../../services";

interface OwnProps {
  item: ReportReviewDataType;
}

type MutationType = {
  authorization: string;
  replyId: number;
};

const ReportedReviewItems: React.FC<OwnProps> = ({ item }) => {
  const { locateReview }: UseLocateType = useLocate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [isOpened, setOpened] = useState<number>(0);

  const { mutate, isLoading, isError, error, isSuccess } = useMutation({
    mutationFn: ({ authorization, replyId }: MutationType) => {
      return axios.delete(`${ROOT_URL}/replies/${replyId}`, {
        headers: {
          Authorization: authorization,
        },
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ReportReview"] });
    },
  });

  // 댓글 삭제하기(권리자 권한)
  const deleteReview = () => {
    mutate({
      authorization: accessToken.authorization,
      replyId: item.replyId,
    });
  };

  return (
    <>
      <TableTr isDark={isDark} key={item.replyId}>
        <TypotdId isDark={isDark}>{item.replyId}</TypotdId>
        <TypotdReplyName
          isDark={isDark}
          onClick={() => locateReview(item.videoId, item.replyId)}
        >
          {item.content}
        </TypotdReplyName>
        <TypotdReportCount isDark={isDark}>
          {item.reportCount}회
        </TypotdReportCount>
        <TypotdLastDate isDark={isDark}>
          {item.lastReportedDate.split("T")[0]}
        </TypotdLastDate>
        <TypotdReportBlock isDark={isDark}>
          <RegularButton isDark={isDark} onClick={deleteReview}>
            삭제
          </RegularButton>
        </TypotdReportBlock>
        <TypotdReportDetail isDark={isDark}>
          {isOpened === item.replyId ? (
            <HideListArrow
              onClick={() => {
                if (isOpened !== item.replyId) {
                  setOpened(item.replyId);
                } else {
                  setOpened(0);
                }
              }}
            />
          ) : (
            <ShowListArrow
              onClick={() => {
                if (isOpened !== item.replyId) {
                  setOpened(item.replyId);
                } else {
                  setOpened(0);
                }
              }}
            />
          )}
        </TypotdReportDetail>
      </TableTr>

      {isOpened === item.replyId && <ReviewReportList replyId={item.replyId} />}
    </>
  );
};

export default ReportedReviewItems;

const globalTokens = tokens.global;

export const TableTr = styled.tr<DarkMode>`
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`;
export const Typotd = styled.td<DarkMode>`
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  padding: 15px 0px;
  text-align: center;
`;

export const TypotdId = styled(Typotd)`
  width: 80px;
`;
export const TypotdReplyName = styled(Typotd)`
  width: 400px;
`;
export const TypotdReportCount = styled(Typotd)`
  width: 100px;
`;
export const TypotdLastDate = styled(Typotd)`
  width: 150px;
`;
export const TypotdReportDetail = styled(Typotd)`
  width: 100px;
`;
export const TypotdReportBlock = styled(Typotd)`
  width: 80px;
`;

export const ShowListArrow = styled(arrowNext)`
  width: 18px;
  height: 18px;
  transform: rotate(90deg);
  cursor: pointer;
`;
export const HideListArrow = styled(arrowPrev)`
  width: 18px;
  height: 18px;
  transform: rotate(90deg);
  cursor: pointer;
`;
