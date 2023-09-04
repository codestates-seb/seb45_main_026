import React,{useState} from 'react';
import { styled } from 'styled-components';
import { PageContainer,MainContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import tokens from "../../styles/tokens.json";
import CategoryFilter from '../../components/filters/CategoryFilter';
import HorizonItem from '../../components/contentListItems/HorizonItem';
import VerticalItem from '../../components/contentListItems/VerticalItem';

const globalTokens = tokens.global;

const LectureMainContainer = styled(MainContainer)`
    min-width: 600px;
    background-color: ${globalTokens.White.value};
    border: none;
    gap: ${globalTokens.Spacing28.value}px;
`;
const ListTitle = styled.h2`
    height: 30px;
    width: 100%;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing28.value}px;
    margin-top: ${globalTokens.Spacing20.value}px;
`
const FilterContainer = styled.div`
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 0 ${globalTokens.Spacing16.value}px;
`
const StructureButton = styled.button`
    width: 48px;
    height: 48px;
    background-color: ${(props) => (props.isHorizon ? "white":"black")};
    border: 1px solid ${(props) => (props.isHorizon ? "black" : "white")};
    border-radius: ${globalTokens.RegularRadius.value}px;
`
const HorizonItemContainer = styled.ul`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: ${globalTokens.Spacing16.value}px;
    margin-bottom: ${globalTokens.Spacing28.value}px;
`
const VerticalItemContainer = styled.ul`
    width: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
    gap: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing28.value}px;
`

const LectureListPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [isHorizon, setIsHorizon] = useState(true);
    const a = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
    return (
        <PageContainer isDark={isDark}>
            <LectureMainContainer>
                <ListTitle>강의 목록</ListTitle>
                <FilterContainer>
                    <CategoryFilter />
                    <StructureButton isHorizon={isHorizon} onClick={()=>setIsHorizon(!isHorizon)} />
                </FilterContainer>
                {isHorizon?<HorizonItemContainer>{a.map((el,idx)=><HorizonItem key={idx}/>)}</HorizonItemContainer>:<VerticalItemContainer>{a.map((el,idx)=><VerticalItem key={idx}/>)}</VerticalItemContainer>}
            </LectureMainContainer>
        </PageContainer>
    );
};

export default LectureListPage;