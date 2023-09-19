import React, { useState } from 'react';
import arrowUp from '../../assets/images/icons/arrow/subscribe_arrow_up.svg';
import arrowDown from '../../assets/images/icons/arrow/subscribe_arrow_down.svg';
import { useSelector } from 'react-redux';
import { 
    ReceiptDropdownContainer,
    DropdownWrapper,
    DropdownButton,
    DropdownButtonIcon,
    DropdownMenuWrapper,
    DropdownMenuButton,
    DropdownTextTypo,
    DropdownContainer
} from './ReceiptDropdown.style';

const ReceiptDropdown = ({category, setCategory}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const handleDropdownButtonClick = (e) => {
        switch (e.target.textContent) {
            case '최근 1달':
                setCategory(1);
                return;
            case '최근 3달':
                setCategory(3);
                return;
            case '최근 6달':
                setCategory(6);
                return;
            case '최근 1년':
                setCategory(12);
                return;
            default: 
                return;
        }
    }

    return (
        <ReceiptDropdownContainer>
            <DropdownWrapper>
                <DropdownContainer>
                <DropdownButton 
                    isDark={isDark} 
                    onClick={ ()=>{ setIsDropdownOpen(!isDropdownOpen) } } 
                    onBlur={()=>{ setIsDropdownOpen(false); }}>
                    <DropdownTextTypo isDark={isDark}>
                    {
                        category===12?'최근 1년'
                        :`최근 ${category}달`
                    }
                    </DropdownTextTypo>
                        <DropdownButtonIcon src={isDropdownOpen?arrowUp:arrowDown}/>
                    </DropdownButton>
                    <DropdownMenuWrapper 
                        isDark={isDark}
                        isDropdownOpen={isDropdownOpen}>
                        <DropdownMenuButton isDropdownOpen={isDropdownOpen} isDark={isDark} onClick={handleDropdownButtonClick}>최근 1달</DropdownMenuButton>
                        <DropdownMenuButton isDropdownOpen={isDropdownOpen} isDark={isDark} onClick={handleDropdownButtonClick}>최근 3달</DropdownMenuButton>
                        <DropdownMenuButton isDropdownOpen={isDropdownOpen} isDark={isDark} onClick={handleDropdownButtonClick}>최근 6달</DropdownMenuButton>
                        <DropdownMenuButton isDropdownOpen={isDropdownOpen} isDark={isDark} onClick={handleDropdownButtonClick}>최근 1년</DropdownMenuButton>
                    </DropdownMenuWrapper>
               
                </DropdownContainer>
            </DropdownWrapper>
        </ReceiptDropdownContainer>
    );
};

export default ReceiptDropdown;