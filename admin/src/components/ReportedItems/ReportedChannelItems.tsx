import styled from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { RootState } from "../../redux/Store";
import { useLocate } from "../../hooks/useLocation";
import {
  DarkMode,
  UseLocateType,
  ReportChannelDataType,
} from "../../types/reportDataType";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { useState } from "react";
import ChannelReportList from "../reportPage/ChannelReportList";
import { ReactComponent as arrowPrev } from "../../assets/images/icons/arrowPrev.svg";
import { ReactComponent as arrowNext } from "../../assets/images/icons/arrowNext.svg";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";
import { queryClient } from "../..";
import { ROOT_URL } from "../../services";

interface OwnProps {
  item: ReportChannelDataType;
}

type MutationType = {
  authorization: string;
  memberId: number;
};

const ReportedChannelItems: React.FC<OwnProps> = ({ item }) => {
  const { locateChannel }: UseLocateType = useLocate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [isOpened, setOpened] = useState<number>(0);

  const { mutate, isLoading, isError, error, isSuccess } = useMutation({
    mutationFn: ({ authorization, memberId }: MutationType) => {
      return axios.patch(
        `${ROOT_URL}/reports/members/${memberId}`,
        {
          days: 7,
          blockReason: "채널 신고 누적으로 인한 계정 일시정지",
        },
        {
          headers: {
            Authorization: authorization,
          },
        }
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ReportChannel"] });
    },
  });

  // 채널 활성화, 비활성화 처리(권리자 권한)
  const patchChannelStatus = () => {
    mutate({
      authorization: accessToken.authorization,
      memberId: item.memberId,
    });
  };

  return (
    <>
      <TableTr isDark={isDark} key={item.memberId}>
        <TypotdId isDark={isDark}>{item.memberId}</TypotdId>
        <TypotdVideoName
          isDark={isDark}
          onClick={() => locateChannel(item.memberId)}
        >
          {item.channelName}
        </TypotdVideoName>
        <TypotdVideoStatus isDark={isDark}>
          {item.memberStatus === "ACTIVE"
            ? "활동중"
            : item.memberStatus === "BLOCKED"
            ? "차단됨"
            : null}
        </TypotdVideoStatus>
        <TypotdReportCount isDark={isDark}>
          {item.reportCount}회
        </TypotdReportCount>
        <TypotdLastDate isDark={isDark}>
          {item.lastReportedDate.split("T")[0]}
        </TypotdLastDate>
        <TypotdReportBlock isDark={isDark}>
          {item.memberStatus === "ACTIVE" && (
            <RegularButton isDark={isDark} onClick={patchChannelStatus}>
              차단하기
            </RegularButton>
          )}
          {item.memberStatus === "BLOCKED" && (
            <RegularButton isDark={isDark} onClick={patchChannelStatus}>
              차단해제
            </RegularButton>
          )}
        </TypotdReportBlock>
        <TypotdReportDetail isDark={isDark}>
          {isOpened === item.memberId ? (
            <HideListArrow
              onClick={() => {
                if (isOpened !== item.memberId) {
                  setOpened(item.memberId);
                } else {
                  setOpened(0);
                }
              }}
            />
          ) : (
            <ShowListArrow
              onClick={() => {
                if (isOpened !== item.memberId) {
                  setOpened(item.memberId);
                } else {
                  setOpened(0);
                }
              }}
            />
          )}
        </TypotdReportDetail>
      </TableTr>

      {isOpened === item.memberId && (
        <ChannelReportList memberId={item.memberId} />
      )}
    </>
  );
};

export default ReportedChannelItems;

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
