import { getAllByText, getByRole, render } from '@testing-library/react';
import { Heading1Typo } from '../atoms/typographys/Typographys';
import tokens from '../styles/tokens.json';

const globalStyles = tokens.global;

type TypographyTestType = {
    isDark: boolean;
}

const TypographyTest = ({isDark}:TypographyTestType) => {
    return (
        <>
            <Heading1Typo isDark={isDark}>This is Heading1 Typo.</Heading1Typo>
        </>
    );
}

describe('<TypographyTest/>',()=>{
    it('Typography의 Light Mode 색상은 Black이다.', ()=>{
        const isDark = true;
        const { container } = render(<TypographyTest isDark={isDark}/>);
    });
})