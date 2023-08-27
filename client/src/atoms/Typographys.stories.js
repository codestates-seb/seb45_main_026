import { BodyTextTypo, Heading1Typo, Heading2Typo, Heading3Typo, Heading4Typo, Heading5Typo, SmallTextTypo } from './Typographys';

const Typographys = () => {
    return (
        <>
            <Heading1Typo>This is Heading 1.</Heading1Typo>
            <Heading2Typo>This is Heading 2.</Heading2Typo>
            <Heading3Typo>This is Heading 3.</Heading3Typo>
            <Heading4Typo>This is Heading 4.</Heading4Typo>
            <Heading5Typo>This is Heading 5.</Heading5Typo>
            <BodyTextTypo>This is BodyText.</BodyTextTypo>
            <SmallTextTypo>This is SmallText.</SmallTextTypo>
        </>
    )
}

export default {
    title: 'Atoms/Typography',
    component: Typographys,
}

export const TypographysTemplate = () => <Typographys/>
