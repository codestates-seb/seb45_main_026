import { styled } from "styled-components";

export const CartInfo = styled.div`
  border: 1px solid blue;
  width: 100%;
  max-width: 350px;

  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
`;

const CartRight = () => {
  return (
    <CartInfo>
      <form>
        <div>
          <span>구매자 정보</span>
          <button>저장</button>
        </div>
        <label>이름</label>
        <input type="text" />
        <label>이메일</label>
        <input type="text" />
        <button>인증 요청</button>
        <label>인증코드</label>
        <input type="text" />
        <button>인증</button>
      </form>
      <form>
        <label>포인트</label>
        <input type="text" placeholder="1,000원 이상 사용가능" />
        <button>전액 사용</button>
        <div>
          <div>
            <span>선택 상품 금액</span>
            <span>19,000원</span>
          </div>
          <div>
            <span>할인 금액</span>
            <span>1,900</span>
          </div>
          <div>
            <span>총 결제금액</span>
            <span>17,100원</span>
          </div>
        </div>
        <button onClick={(e) => e.preventDefault()}>결제하기</button>
        <div>
          회원 본인은 주문내용을 확인했으며, 구매조건 및 개인정보처리방침과
          결제에 동의합니다.
        </div>
      </form>
    </CartInfo>
  );
};

export default CartRight;
