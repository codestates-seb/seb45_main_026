import axios from "axios";
import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { loadTossPayments } from "@tosspayments/payment-sdk";
import { BigButton } from "../../../atoms/buttons/Buttons";
import { setPayment } from "../../../redux/createSlice/CartsSlice";

const PaymentBtn = ({ isDiscount }) => {
  const dispatch = useDispatch();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const cartsItems = useSelector((state) => state.cartSlice.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const myCartInfo = useSelector((state) => state.cartSlice.myCartInfo);
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);

  const paymentInfo = useSelector((state) => state.cartSlice.paymentInfo);
  //   console.log(paymentInfo);

  const clientKey = "test_ck_AQ92ymxN3491bNE6pQjVajRKXvdk";

  loadTossPayments(clientKey).then((tossPayments) => {
    tossPayments.requestPayment("카드", paymentInfo).catch(function (error) {
      if (error.code === "USER_CANCEL") {
        // 결제 고객이 결제창을 닫았을 때 에러 처리
      } else if (error.code === "INVALID_CARD_COMPANY") {
        // 유효하지 않은 카드 코드에 대한 에러 처리
      }
    });
  });

  const orderList = cartsItems.find((el) => {
    return el.videoId === checkedItems[0];
  });
  const orderName = orderList && orderList.videoName;
  const orderDetail =
    checkedItems.length > 1 ? ` 외 ${checkedItems.length - 1}건` : "";

  const handlePostPayment = () => {
    if (!checkedItems.length) {
      return alert("선택된 강의가 없습니다.");
    }
    return axios
      .post(
        `https://api.itprometheus.net/orders`,
        { reward: isDiscount, videoIds: checkedItems },
        { headers: { Authorization: token.authorization } }
      )
      .then((res) => {
        dispatch(
          setPayment({
            amount: res.data.data.totalAmount,
            orderId: res.data.data.orderId,
            orderName: orderName + orderDetail,
            customerName: myCartInfo.nickname,
          })
        );
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const handelCancelPayment = () => {
    return axios
      .delete(
        `https://api.itprometheus.net/orders/d7992672-5413-4622-9a7e-e2e049305ac1`,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => console.log(res.data))
      .catch((err) => console.log(err));
  };

  const handelgetPayment = () => {
    return axios
      .get(
        `https://api.itprometheus.net/members/orders?page=1&size=6&month=1`,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => console.log(res.data))
      .catch((err) => console.log(err));
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
      <PayBtn
        onClick={(e) => {
          e.preventDefault();
          handelCancelPayment();
        }}
      >
        결제 취소
      </PayBtn>
      <PayBtn
        onClick={(e) => {
          e.preventDefault();
          handelgetPayment();
        }}
      >
        결제 내역
      </PayBtn>
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
