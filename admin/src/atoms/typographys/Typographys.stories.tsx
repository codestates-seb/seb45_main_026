import { Meta, Story } from "@storybook/react";
import { BodyTextTypo, Heading1Typo, Heading2Typo, Heading3Typo, Heading4Typo, Heading5Typo, SmallTextTypo } from "./Typographys";

type typographysPropsType = {
    isDark: boolean;
}

const Typographys = ({isDark}:typographysPropsType) => {
    return (
        <div style={{ display:'flex', flexDirection:'column'}} >
            <Heading1Typo isDark={isDark}>This is Heading1 Typo.</Heading1Typo>
            <Heading2Typo isDark={isDark}>This is Heading2 Typo.</Heading2Typo>
            <Heading3Typo isDark={isDark}>This is Heading3 Typo.</Heading3Typo>
            <Heading4Typo isDark={isDark}>This is Heading4 Typo.</Heading4Typo>
            <Heading5Typo isDark={isDark}>This is Heading5 Typo.</Heading5Typo>
            <BodyTextTypo isDark={isDark}>This is Body Text Typo.</BodyTextTypo>
            <SmallTextTypo isDark={isDark}>This is Small Text Typo.</SmallTextTypo>
        </div>
    );
}

export default {
    title: 'atoms/Typography',
    component: Typographys,
    argTypes: {
        isDark: { control: { type: 'boolean' } }
    }
} as Meta

export const TypographysTemplate = (args:typographysPropsType) => <Typographys {...args}/>
TypographysTemplate.args = {
    isDark: true,
}