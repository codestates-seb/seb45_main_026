import { Meta, Story } from "@storybook/react";
import { BigButton, NegativeTextButton, PositiveTextButton, RegularButton, TextButton } from "./Buttons";

type buttonsPropsType = {
    isDark: boolean;
}

const Buttons = ({isDark}:buttonsPropsType) => {
    return (
        <div style={{display: "grid", gridTemplateColumns: '1fr 1fr 1fr 1fr', gridAutoRows: '1fr', placeItems: 'center', backgroundColor:isDark?'#182333':'white' }}>
            <BigButton isDark={isDark}>Big Button</BigButton>
            <RegularButton isDark={isDark}>Regular Button</RegularButton>
            <TextButton isDark={isDark}>Text Button</TextButton>
            <NegativeTextButton isDark={isDark}>Negative Text Button</NegativeTextButton>
            <PositiveTextButton isDark={isDark}>Positive Text Button</PositiveTextButton>
        </div>
    );
}

export default {
    title: 'Atoms/Button',
    component: Buttons,
    argTypes: {
        isDark: { controls: 'boolean' }
    }
}

export const ButtonsTemplate = (args:buttonsPropsType) => <Buttons {...args}/>
ButtonsTemplate.args = {
    isDark: false,
}