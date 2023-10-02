import React, { useState } from "react";
import { useSelector } from "react-redux";
import {
  IconButtonContainer,
  IconButtonImg,
} from "../../atoms/buttons/IconButtons";
import arrowDown from "../../assets/images/icons/arrow/subscribe_arrow_down.svg";
import arrowUp from "../../assets/images/icons/arrow/subscribe_arrow_up.svg";
import {
  ReceiptAmountTypo,
  ReceiptCancelButton,
  ReceiptGrayTypo,
  ReceiptItemContainer,
  ReceiptStatusTypo,
  ReceiptTitleTypo,
} from "./ReceiptItem.style";
import ReceiptArcodian from "./ReceiptArcodian";
import { cancelWholePurchaseService } from "../../services/receiptServices";
import { AlertModal } from "../../atoms/modal/Modal";
import { useToken } from "../../hooks/useToken";

const ReceiptItem = ({ item }) => {
  const refreshToken = useToken();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const accessTokens = useSelector((state) => state.loginInfo.accessToken);
  let createDate = item.createdDate;
  let createDay = createDate.split("T")[0];
  let createTime = createDate.split("T")[1];
  const titleName = `${item.orderVideos[0].videoName} 외 ${
    item.orderCount - 1
  }개`;
  const amount = item.amount;
  const [isAcordianOpen, setIsAcordianOpen] = useState(false);
  const [is결제취소성공팝업, setIs결제취소성공팝업] = useState(false);
  const [is결제취소실패팝업, setIs결제취소실패팝업] = useState(false);
  const [결제취소실패content, set결제취소실패content] = useState("");

  const handleArcodianButtonClick = () => {
    setIsAcordianOpen(!isAcordianOpen);
  };
  //결제 취소 버튼 선택시 동작
  const handleCancelButtonClick = async () => {
    const response = await cancelWholePurchaseService(
      accessTokens.authorization,
      item.orderId
    );
    if (response.status === "success") {
      setIs결제취소성공팝업(true);
    } else if (response.data === "만료된 토큰입니다.") {
      refreshToken(handleCancelButtonClick);
    } else {
      set결제취소실패content(response.data);
      setIs결제취소실패팝업(true);
    }
  };

  return (
    <>
      <AlertModal
        isModalOpen={is결제취소실패팝업}
        setIsModalOpen={setIs결제취소실패팝업}
        isBackdropClickClose={true}
        content={결제취소실패content}
        buttonTitle="확인"
        handleButtonClick={() => {
          setIs결제취소실패팝업(false);
        }}
      />
      <AlertModal
        isModalOpen={is결제취소성공팝업}
        setIsModalOpen={(setIsModalOpen) => {
          setIsModalOpen(false);
          window.location.reload();
        }}
        isBackdropClickClose={true}
        content="결제 취소되었습니다."
        buttonTitle="확인"
        handleButtonClick={() => {
          setIs결제취소성공팝업(false);
          window.location.reload();
        }}
      />
      <ReceiptItemContainer isDark={isDark} isAcordianOpen={isAcordianOpen}>
        <ReceiptGrayTypo isDark={isDark}>
          {`${createDay} ${createTime}`}
        </ReceiptGrayTypo>
        <ReceiptTitleTypo isDark={isDark}>{titleName}</ReceiptTitleTypo>
        <ReceiptAmountTypo isDark={isDark}>{`${amount}원`}</ReceiptAmountTypo>
        <ReceiptStatusTypo isDark={isDark}>
          {item.orderStatus === "COMPLETED"
            ? "결제 완료"
            : item.orderStatus === "CANCELED"
            ? "결제 취소"
            : item.orderStatus === "ORDERED"
            ? "결제 대기"
            : null}
        </ReceiptStatusTypo>
        {
          <IconButtonContainer
            isDark={isDark}
            onClick={handleArcodianButtonClick}
          >
            <IconButtonImg src={isAcordianOpen ? arrowUp : arrowDown} />
          </IconButtonContainer>
        }
        {item.orderStatus === "COMPLETED" && (
          <ReceiptCancelButton
            isDark={isDark}
            onClick={handleCancelButtonClick}
          >
            결제취소
          </ReceiptCancelButton>
        )}
      </ReceiptItemContainer>
      {isAcordianOpen && (
        <ReceiptArcodian orderId={item.orderId} videos={item.orderVideos} />
      )}
    </>
  );
};

export default ReceiptItem;
