import { RegularInput } from "./Inputs"

const Inputs = ({isDark, width}) => {
    return (
        <div style={{display: "flex", flexDirection:"column", }}>
            <RegularInput isDark={isDark} width={width} type='text' placeholder='placeholder'/>
        </div>
    )
}

export default {
    title: 'Atoms/Inputs',
    component: Inputs,
    argTypes: {
        isDark: { control: 'boolean' },
        width: {  control: 'text' },
    }
}

export const InputsTemplate = (args) => <Inputs {...args}/>
InputsTemplate.args = {
    isDark: false,
    width: '300px'
}