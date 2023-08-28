import { BodyTextTypo, Heading1Typo, Heading2Typo, Heading3Typo, Heading4Typo, Heading5Typo, SmallTextTypo } from './Typographys';

const Typographys = ({isDark}) => {
    return (
        <>
            <Heading1Typo isDark={isDark}>This is Heading 1.</Heading1Typo>
            <Heading2Typo isDark={isDark}>This is Heading 2.</Heading2Typo>
            <Heading3Typo isDark={isDark}>This is Heading 3.</Heading3Typo>
            <Heading4Typo isDark={isDark}>This is Heading 4.</Heading4Typo>
            <Heading5Typo isDark={isDark}>This is Heading 5.</Heading5Typo>
            <BodyTextTypo isDark={isDark}>This is BodyText.</BodyTextTypo>
            <SmallTextTypo isDark={isDark}>This is SmallText.</SmallTextTypo>
        </>
    )
}

export default {
    title: 'Atoms/Typography',
    component: Typographys,
    argTypes: {
        isDark: { control: 'boolean' }
    }
}

export const TypographysTemplate = (args) => <Typographys {...args}/>
TypographysTemplate.args = {
    isDark: false
}