import styled from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { RootState } from "../../redux/Store";
import { useLocate } from "../../hooks/useLocation";
import {
  DarkMode,
  UseLocateType,
  ReportVideoDataType,
} from "../../types/reportDataType";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { useState } from "react";
import VideoReportList from "../reportPage/VideoReportList";
import { ReactComponent as arrowPrev } from "../../assets/images/icons/arrowPrev.svg";
import { ReactComponent as arrowNext } from "../../assets/images/icons/arrowNext.svg";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";
import { queryClient } from "../..";
import { ROOT_URL } from "../../services";

interface OwnProps {
  item: ReportVideoDataType;
}

type MutationType = {
  authorization: string;
  videoId: number;
};

const ReportedVideoItems: React.FC<OwnProps> = ({ item }) => {
  const { locateVideo }: UseLocateType = useLocate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [isOpened, setOpened] = useState<number>(0);

  const { mutate, isLoading, isError, error, isSuccess } = useMutation({
    mutationFn: ({ authorization, videoId }: MutationType) => {
      return axios.patch(
        `${ROOT_URL}/videos/${videoId}/status`,
        {},
        {
          headers: {
            Authorization: authorization,
          },
        }
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ReportVideo"] });
    },
  });

  // 강의 활성화, 비활성화 처리(권리자 권한)
  const patchVideoStatus = () => {
    mutate({
      authorization: accessToken.authorization,
      videoId: item.videoId,
    });
  };

  return (
    <>
      <TableTr isDark={isDark} key={item.videoId}>
        <TypotdId isDark={isDark}>{item.videoId}</TypotdId>
        <TypotdVideoName
          isDark={isDark}
          onClick={() => locateVideo(item.videoId)}
        >
          {item.videoName}
        </TypotdVideoName>
        <TypotdVideoStatus isDark={isDark}>
          {item.videoStatus === "CREATED"
            ? "활동중"
            : item.videoStatus === "CLOSED"
            ? "폐쇄됨"
            : item.videoStatus === "ADMIN_CLOSED"
            ? "관리자에 의해 폐쇄됨"
            : null}
        </TypotdVideoStatus>
        <TypotdReportCount isDark={isDark}>
          {item.reportCount} 회
        </TypotdReportCount>
        <TypotdLastDate isDark={isDark}>
          {item.lastReportedDate.split("T")[0]}
        </TypotdLastDate>
        <TypotdReportBlock isDark={isDark}>
          {(item.videoStatus === "CLOSED" ||
            item.videoStatus === "ADMIN_CLOSED") && (
            <RegularButton isDark={isDark} onClick={patchVideoStatus}>
              활성화
            </RegularButton>
          )}
          {item.videoStatus === "CREATED" && (
            <RegularButton isDark={isDark} onClick={patchVideoStatus}>
              비활성화
            </RegularButton>
          )}
        </TypotdReportBlock>
        <TypotdReportDetail isDark={isDark}>
          {isOpened === item.videoId ? (
            <HideListArrow
              onClick={() => {
                if (isOpened !== item.videoId) {
                  setOpened(item.videoId);
                } else {
                  setOpened(0);
                }
              }}
            />
          ) : (
            <ShowListArrow
              onClick={() => {
                if (isOpened !== item.videoId) {
                  setOpened(item.videoId);
                } else {
                  setOpened(0);
                }
              }}
            />
          )}
        </TypotdReportDetail>
      </TableTr>
      {isOpened === item.videoId && <VideoReportList videoId={item.videoId} />}
    </>
  );
};

export default ReportedVideoItems;

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
export const TypotdVideoName = styled(Typotd)`
  width: 330px;
  cursor: pointer;
`;
export const TypotdVideoStatus = styled(Typotd)`
  width: 145px;
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
