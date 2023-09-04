import { Input } from "./Inputs"

export default {
    title: 'Atoms/Inputs',
    component: Input,
    argTypes: {
        isDark: { control: 'boolean' },
        width: {  control: 'text' },
    }
}

export const InputsTemplate = (args) => <Input {...args}/>
InputsTemplate.args = {
    width: '300px'
}