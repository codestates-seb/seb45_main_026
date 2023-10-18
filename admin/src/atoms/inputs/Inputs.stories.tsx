import { Meta, Story } from "@storybook/react";
import { RegularInput, RegularTextArea } from "./Input.style";

type inputsPropsType = {
    isDark: boolean;
    width: string;
}

const Inputs = ({ isDark, width } : inputsPropsType) => {
    return (
        <div style={{ display:'flex', flexDirection:'column', gap:'10px'}}>
            <RegularInput isDark={isDark} width={width}/>
            <RegularTextArea isDark={isDark} width={width}/>
        </div>
    );
}

export default {
    title: 'Atoms/Input',
    component: Inputs,
    argTypes: {
        isDark: { controls : 'boolean' },
        width: { controls : 'string' },
    }
}

export const InputTemplate = (args:inputsPropsType) => <Inputs {...args}/>
InputTemplate.args = {
    isDark: false,
    width: '250px',
}