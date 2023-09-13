import React, { useState } from 'react';
import arrowUp from '../../assets/images/icons/arrow/subscribe_arrow_up.svg';
import arrowDown from '../../assets/images/icons/arrow/subscribe_arrow_down.svg';
import { useSelector } from 'react-redux';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { 
    ReceiptDropdownContainer,
    DropdownWrapper,
    DropdownButton,
    DropdownButtonIcon,
    DropdownMenuWrapper,
    DropdownMenuButton
} from '../rewardPage/RewardDropdown.style';

const ReceiptDropdown = ({category, setCategory}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const handleDropdownButtonClick = (e) => {
        setCategory(e.target.textContent);
    }

    return (
        <ReceiptDropdownContainer>
            <DropdownWrapper>
                <DropdownButton isDark={isDark}>
                    <BodyTextTypo isDark={isDark}>{category}</BodyTextTypo>
                    <DropdownButtonIcon src={isDropdownOpen?arrowUp:arrowDown}/>
                    <DropdownMenuWrapper 
                        isDark={isDark}
                        className={isDropdownOpen?'growDown':'growUp'}>
                        <DropdownMenuButton isDark={isDark} onClick={handleDropdownButtonClick}>최근 1달</DropdownMenuButton>
                        <DropdownMenuButton isDark={isDark} onClick={handleDropdownButtonClick}>최근 3달</DropdownMenuButton>
                        <DropdownMenuButton isDark={isDark} onClick={handleDropdownButtonClick}>최근 6달</DropdownMenuButton>
                        <DropdownMenuButton isDark={isDark} onClick={handleDropdownButtonClick}>최근 1년</DropdownMenuButton>
                    </DropdownMenuWrapper>
                </DropdownButton>
            </DropdownWrapper>
        </ReceiptDropdownContainer>
    );
};

export default ReceiptDropdown;