import { CheckboxContainer } from "./Checkbox"

const Checkbox = ({isChecked, isDark}) => {
    return (<CheckboxContainer isDark={isDark} isChecked={isChecked}/>);
}

export default {
    title: 'Atoms/Button',
    component: Checkbox,
    argTypes: {
        isDark: { control: 'boolean' },
        isChecked: { control: 'boolean' },
    }
}

export const CheckboxTemplate = (args) => <Checkbox {...args}/>
CheckboxTemplate.args = {
    isChecked: false,
    isDark: false,
}