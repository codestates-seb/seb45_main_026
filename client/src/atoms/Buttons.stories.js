import { RegularNavyButton, RegularRedButton, RegularRedTextButton, TextButton } from "./Buttons";

const Buttons = ({isDark}) => {
    return (
        <div>
            <RegularRedButton isDark={isDark}>Regular Red Button</RegularRedButton>
            <RegularNavyButton isDark={isDark}>Regular Navy Button</RegularNavyButton>
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