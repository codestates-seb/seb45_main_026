import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import {
  ChatBlocktd,
  CustomerEmailtd,
  CustomerIdtd,
  CustomerNametd,
  CustomerTr,
  InquireDatetd,
} from "../../pages/chats/CustomerServicePage";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { AllChatRoom } from "./AllChatLists";
import { useNavigate } from "react-router-dom";
import { setRoomId } from "../../redux/createSlice/customerInfoSlice";

interface OwnProps {
  item: AllChatRoom;
}

const AllChatItems: React.FC<OwnProps> = ({ item }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);

  const handleChatStart = (memberId: number): void => {
    navigate(`/chats/${memberId}`);
    dispatch(setRoomId(item.roomId));
  };

  return (
    <CustomerTr isDark={isDark}>
      <CustomerIdtd isDark={isDark}>{item.memberId}</CustomerIdtd>
      <CustomerNametd isDark={isDark}>{item.nickname}</CustomerNametd>
      <CustomerEmailtd isDark={isDark}>{item.roomId}</CustomerEmailtd>
      <InquireDatetd isDark={isDark}>
        {item.inquireDate?.replace("T", " ")}
      </InquireDatetd>
      <ChatBlocktd isDark={isDark}>
        <RegularButton
          isDark={isDark}
          onClick={() => handleChatStart(item.memberId)}
        >
          채팅하기
        </RegularButton>
      </ChatBlocktd>
    </CustomerTr>
  );
};

export default AllChatItems;
