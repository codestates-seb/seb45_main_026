import { BigNavyButton, BigRedButton, PositiveTextButton, NegativeTextButton, RegularNavyButton, RegularRedButton, RegularRedTextButton, RoundNavyButton, RoundRedButton, TextButton } from "./Buttons";

const Buttons = ({isDark}) => {
    return (
        <div style={{display: "grid", gridTemplateColumns: '1fr 1fr 1fr 1fr', gridAutoRows: '1fr', placeItems: 'center' }}>
            <BigRedButton isDark={isDark}>BigRedButton</BigRedButton>
            <BigNavyButton isDark={isDark}>BigNavyButton</BigNavyButton>
            <RegularRedButton isDark={isDark}>Regular Red Button</RegularRedButton>
            <RegularNavyButton isDark={isDark}>Regular Navy Button</RegularNavyButton>
            <RoundRedButton isDark={isDark}>Round Red Button</RoundRedButton>
            <RoundNavyButton isDark={isDark}>Round Navy Button</RoundNavyButton>
            <TextButton isDark={isDark}>Text Button</TextButton>
            <PositiveTextButton isDark={isDark}>Positive Text Button</PositiveTextButton>
            <NegativeTextButton isDark={isDark}>Negative Text Button</NegativeTextButton>
        </div>
    );
}

export default {
    title: 'Atoms/Button',
    component: Buttons,
    argTypes: {
        isDark: { control: 'boolean' }
    }
}

export const ButtonsTemplate = (args) => <Buttons {...args}/>
ButtonsTemplate.args = {
    isDark: false
}