import { RegularInput } from "./Inputs"

const Inputs = ({isDark, width}) => {
    return (
        <div style={{display: "flex", flexDirection:"column", }}>
            <RegularInput isDark={isDark} width={width} type='text' placeholder='width:300px'/>
            <RegularInput isDark={isDark} width='10vw' type='password' placeholder='width:10vw'/>
        </div>
    )
}

export default {
    title: 'Atoms/Inputs',
    component: Inputs,
    argTypes: {
        isDark: { control: 'boolean' },
        width: {  control: 'string' },
    }
}

export const InputsTemplate = (args) => <Inputs {...args}/>
InputsTemplate.args = {
    isDark: false,
    width: '300px'
}