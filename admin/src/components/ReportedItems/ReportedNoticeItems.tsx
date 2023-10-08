import styled from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { RootState } from "../../redux/Store";
import { useLocate } from "../../hooks/useLocation";
import {
  DarkMode,
  UseLocateType,
  ReportNoticeDataType,
} from "../../types/reportDataType";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { useState } from "react";
import NoticeReportList from "../reportPage/NoticeReportList";
import { ReactComponent as arrowPrev } from "../../assets/images/icons/arrowPrev.svg";
import { ReactComponent as arrowNext } from "../../assets/images/icons/arrowNext.svg";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";
import { queryClient } from "../..";
import { ROOT_URL } from "../../services";

interface OwnProps {
  item: ReportNoticeDataType;
}

type MutationType = {
  authorization: string;
  announcementId: number;
};

const ReportedNoticeItems: React.FC<OwnProps> = ({ item }) => {
  const { locateNotice }: UseLocateType = useLocate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [isOpened, setOpened] = useState<number>(0);

  const { mutate, isLoading, isError, error, isSuccess } = useMutation({
    mutationFn: ({ authorization, announcementId }: MutationType) => {
      return axios.delete(`${ROOT_URL}/announcements/${announcementId}`, {
        headers: {
          Authorization: authorization,
        },
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ReportNotice"] });
    },
  });

  // 강의 활성화, 비활성화 처리(권리자 권한)
  const deleteNotice = () => {
    mutate({
      authorization: accessToken.authorization,
      announcementId: item.announcementId,
    });
  };

  return (
    <>
      <TableTr isDark={isDark} key={item.announcementId}>
        <TypotdId isDark={isDark}>{item.announcementId}</TypotdId>
        <TypotdNoticeName
          isDark={isDark}
          isOpened={isOpened === item.announcementId}
          onClick={() => locateNotice(item.memberId, item.announcementId)}
        >
          {item.content}
        </TypotdNoticeName>
        <TypotdReportCount isDark={isDark}>
          {item.reportCount}회
        </TypotdReportCount>
        <TypotdLastDate isDark={isDark}>
          {item.lastReportedDate.split("T")[0]}
        </TypotdLastDate>
        <TypotdReportBlock isDark={isDark}>
          <RegularButton isDark={isDark} onClick={deleteNotice}>
            삭제
          </RegularButton>
        </TypotdReportBlock>
        <TypotdReportDetail isDark={isDark}>
          {isOpened === item.announcementId ? (
            <HideListArrow
              onClick={() => {
                if (isOpened !== item.announcementId) {
                  setOpened(item.announcementId);
                } else {
                  setOpened(0);
                }
              }}
            />
          ) : (
            <ShowListArrow
              onClick={() => {
                if (isOpened !== item.announcementId) {
                  setOpened(item.announcementId);
                } else {
                  setOpened(0);
                }
              }}
            />
          )}
        </TypotdReportDetail>
      </TableTr>

      {isOpened === item.announcementId && (
        <NoticeReportList announcementId={item.announcementId} />
      )}
    </>
  );
};

export default ReportedNoticeItems;

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
  overflow: hidden;
  text-overflow: ellipsis;
`;

export const TypotdId = styled(Typotd)`
  width: 100px;
`;
type StyledProps = {
  isOpened: boolean;
};
export const TypotdNoticeName = styled(Typotd)<StyledProps>`
  width: 455px;
  white-space: ${(props) => (props.isOpened ? "wrap" : "nowrap")};
  padding-left: 20px;
  padding-right: 20px;
  cursor: pointer;
`;
export const TypotdReportCount = styled(Typotd)`
  width: 85px;
`;
export const TypotdLastDate = styled(Typotd)`
  width: 140px;
`;
export const TypotdReportDetail = styled(Typotd)`
  width: 80px;
`;
export const TypotdReportBlock = styled(Typotd)`
  width: 90px;
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
