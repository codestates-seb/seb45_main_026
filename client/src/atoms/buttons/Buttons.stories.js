import { BigNavyButton, BigRedButton, PositiveTextButton, NegativeTextButton, RegularNavyButton, RegularRedButton, RoundNavyButton, RoundRedButton, TextButton, BigButton, RegularButton, RoundButton } from "./Buttons";
import { CloseIconButton } from "./IconButtons";

const Buttons = ({isDark}) => {
    return (
        <div style={{display: "grid", gridTemplateColumns: '1fr 1fr 1fr 1fr', gridAutoRows: '1fr', placeItems: 'center' }}>
            <BigButton isDark={isDark}>BigButton</BigButton>
            <BigRedButton isDark={isDark}>BigRedButton</BigRedButton>
            <BigNavyButton isDark={isDark}>BigNavyButton</BigNavyButton>
            <RegularButton isDark={isDark}>RegularButton</RegularButton>
            <RegularRedButton isDark={isDark}>Regular Red Button</RegularRedButton>
            <RegularNavyButton isDark={isDark}>Regular Navy Button</RegularNavyButton>
            <RoundButton isDark={isDark}>RoundButton</RoundButton>
            <RoundRedButton isDark={isDark}>Round Red Button</RoundRedButton>
            <RoundNavyButton isDark={isDark}>Round Navy Button</RoundNavyButton>
            <TextButton isDark={isDark}>Text Button</TextButton>
            <PositiveTextButton isDark={isDark}>Positive Text Button</PositiveTextButton>
            <NegativeTextButton isDark={isDark}>Negative Text Button</NegativeTextButton>
            <CloseIconButton  isDark={isDark}/>
        </div>
    );
}

export default {
    title: 'Atoms/Button',
    component: Buttons,
    argTypes: {
        isDark: { control: 'boolean' },
    }
}

export const ButtonsTemplate = (args) => <Buttons {...args}/>
ButtonsTemplate.args = {
    isDark: false,
}