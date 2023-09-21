import React, { useState } from 'react';
import { styled } from 'styled-components';
import { DropdownButton, DropdownButtonIcon, DropdownContainer, DropdownMenuButton, DropdownMenuWrapper, DropdownTextTypo, DropdownWrapper, ReceiptDropdownContainer } from '../receiptPage/ReceiptDropdown.style';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';
import arrowUp from '../../assets/images/icons/arrow/subscribe_arrow_up.svg';
import arrowDown from '../../assets/images/icons/arrow/subscribe_arrow_down.svg';

const globalTokens = tokens.global;

const ReportDropdownWrapper = styled(DropdownWrapper)`
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
`
const ReportDropdownButton = styled(DropdownButton)`
    width: 150px;
`
const ReportDropdownUnitTypo = styled(BodyTextTypo)`
    margin: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px ${globalTokens.Spacing4.value}px ${globalTokens.Spacing4.value}px;
`
const ReportDropdownMenuWrapper = styled(DropdownMenuWrapper)`
    width: 150px;
`

const ReportCategory = ({category, setCategory}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [ isDropdownOpen, setIsDropdownOpen ] = useState(false);

    const handleCategoryClick = (e) => {
        switch(e.target.textContent) {
            case '최근 신고 순': 
                setCategory('last-reported-date');
                return;
            case '신고 많은 순':
                setCategory('report-count');
                return;
            case '비디오 생성 순':
                setCategory('created-date');
                return;
            default: return;
        }
    }

    return (
        <ReceiptDropdownContainer>
            <ReportDropdownWrapper>
                <DropdownContainer>
                    <ReportDropdownButton
                        isDark={isDark}
                        onClick={()=>{ setIsDropdownOpen(!isDropdownOpen) }}
                        onBlur={()=>{ setIsDropdownOpen(false) }}>
                        <DropdownTextTypo isDark={isDark}>
                        {
                            category==='last-reported-date'? '최근 신고 순'
                            : category==='report-count'? '신고 많은 순'
                            : category==='created-date'? '비디오 생성 순'
                            : ''
                        }
                        </DropdownTextTypo>
                        <DropdownButtonIcon src={isDropdownOpen?arrowUp:arrowDown}/>
                    </ReportDropdownButton>
                    <ReportDropdownMenuWrapper isDark={isDark} isDropdownOpen={isDropdownOpen}>
                        <DropdownMenuButton isDark={isDark} isDropdownOpen={isDropdownOpen} onClick={handleCategoryClick}>최근 신고 순</DropdownMenuButton>
                        <DropdownMenuButton isDark={isDark} isDropdownOpen={isDropdownOpen} onClick={handleCategoryClick}>신고 많은 순</DropdownMenuButton>
                        <DropdownMenuButton isDark={isDark} isDropdownOpen={isDropdownOpen} onClick={handleCategoryClick}>비디오 생성 순</DropdownMenuButton>
                    </ReportDropdownMenuWrapper>
                </DropdownContainer>
            </ReportDropdownWrapper>
        </ReceiptDropdownContainer>
    );
};

export default ReportCategory;