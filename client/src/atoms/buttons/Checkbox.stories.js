import { useState } from "react";
import { Checkbox } from "./Checkbox"

export default {
    title: 'Atoms/Button',
    component: Checkbox,
    argTypes: {
        isDark: 'boolean',
        label: 'text',
    }
}

export const CheckboxTemplate = (args) => {
    const [ isChecked, setIsChecked ] = useState(false);

    return (
        <Checkbox 
            isChecked={isChecked} 
            setIsChecked={()=>{setIsChecked(!isChecked)}} 
            {...args}/>
    );
}
CheckboxTemplate.args = {
    label: '레이블',
    isDark : false,
}