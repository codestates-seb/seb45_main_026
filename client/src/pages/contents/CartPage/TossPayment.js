import axios from "axios";
import { useState } from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { loadTossPayments } from "@tosspayments/payment-sdk";
import { BigButton } from "../../../atoms/buttons/Buttons";
import Loading from "../../../atoms/loading/Loading";
import { AlertModal } from "../../../atoms/modal/Modal";

const PaymentBtn = ({ isDiscount }) => {
  const [isLoading, setLoading] = useState(false);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const cartsItems = useSelector((state) => state.cartSlice.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const myCartInfo = useSelector((state) => state.cartSlice.myCartInfo);
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);
  const orderList = cartsItems.find((el) => el.videoId === checkedItems[0]);
  const orderName = orderList && orderList.videoName;
  const orderDetail =
    checkedItems.length > 1 ? ` 외 ${checkedItems.length - 1}건` : "";
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handlePostPayment = () => {
    if (!checkedItems.length) {
      return setIsModalOpen(true);
    }
    return axios
      .post(
        `https://api.itprometheus.net/orders`,
        { reward: isDiscount, videoIds: checkedItems },
        { headers: { Authorization: token.authorization } }
      )
      .then((res) => {
        if (res.data.code === 200) {
          const paymentInfo = {
            amount: res.data.data.totalAmount,
            orderId: res.data.data.orderId,
            orderName: orderName + orderDetail,
            customerName: myCartInfo.nickname,
            successUrl: "https://www.itprometheus.net/carts",
            failUrl: "https://www.itprometheus.net/carts",
          };
          setLoading(true);
          setTimeout(() => {
            setLoading(false);
            tossActive(paymentInfo);
          }, 1000);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const clientKey = "test_ck_AQ92ymxN3491bNE6pQjVajRKXvdk";
  const tossActive = (paymentInfo) => {
    return loadTossPayments(clientKey).then((tossPayments) => {
      tossPayments.requestPayment("카드", paymentInfo).catch((err) => {
        if (err.code === "USER_CANCEL") {
          // 결제 고객이 결제창을 닫았을 때 에러 처리
        } else if (err.code === "INVALID_CARD_COMPANY") {
          // 유효하지 않은 카드 코드에 대한 에러 처리
        } else {
          console.log(err);
        }
      });
    });
  };

  return (
    <>
      <PayBtn
        isDark={isDark}
        onClick={(e) => {
          e.preventDefault();
          handlePostPayment();
        }}
      >
        결제하기
      </PayBtn>
      <Loading isLoading={isLoading} />
      <AlertModal
        isModalOpen={isModalOpen}
        setIsModalOpen={setIsModalOpen}
        isBackdropClickClose={true}
        content="선택된 강의가 없습니다."
        buttonTitle="확인"
        handleButtonClick={() => setIsModalOpen(false)}
      />
    </>
  );
};

export default PaymentBtn;

export const PayBtn = styled(BigButton)`
  @media screen and (min-width: 1170px) {
    max-width: 310px;
  }
  @media screen and (min-width: 0px) {
    /* max-width: 310px; */
  }
  width: 100%;
  height: 45px;
  border-radius: 8px;
  margin-top: 10px;
  font-weight: 600;
  font-size: 16px;
`;
