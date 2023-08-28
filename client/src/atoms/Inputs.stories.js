import { RegularInput } from "./Inputs"

const Inputs = ({isDark}) => {
    return (
        <div style={{display: "flex", flexDirection:"column", }}>
            <RegularInput isDark={isDark} type='text' placeholder='place holder'/>
        </div>
    )
}

export default {
    title: 'Atoms/Inputs',
    component: Inputs,
    argTypes: {
        isDark: { control: 'boolean' }
    }
}

export const InputsTemplate = (args) => <Inputs {...args}/>
InputsTemplate.args = {
    isDark: false
}