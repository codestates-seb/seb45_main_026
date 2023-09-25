import { Meta, Story } from "@storybook/react";
import { BodyTextTypo, Heading1Typo, Heading2Typo, Heading3Typo, Heading4Typo, Heading5Typo, SmallTextTypo } from "./Typographys";

const Typographys = () => {
    return (
        <div style={{ display:'flex', flexDirection:'column' }}>
            <Heading1Typo>This is Heading1 Typo.</Heading1Typo>
            <Heading2Typo>This is Heading2 Typo.</Heading2Typo>
            <Heading3Typo>This is Heading3 Typo.</Heading3Typo>
            <Heading4Typo>This is Heading4 Typo.</Heading4Typo>
            <Heading5Typo>This is Heading5 Typo.</Heading5Typo>
            <BodyTextTypo>This is Body Text Typo.</BodyTextTypo>
            <SmallTextTypo>This is Small Text Typo.</SmallTextTypo>
        </div>
    );
}

export default {
    title: 'atoms/Typography',
    component: Typographys,
} as Meta

const Template: Story = () => <Typographys/>

export const TypographysTemplate = Template.bind({});