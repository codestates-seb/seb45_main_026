import React, { useState } from 'react'
import { ToggleCircle, ToggleContainer, ToggleWrapper, togglePropsType } from './Toggle';

type toggleStoryPropsType = {
    isDark: boolean;
}

const ToggleStory = ({isDark}:toggleStoryPropsType) => {
    const [ isOn, setIsOn ] = useState(false);

    return (
        <ToggleWrapper onClick={()=>{ setIsOn(!isOn) }}>
            <ToggleContainer isDark={isDark}>
                <ToggleCircle isOn={isOn}/>
            </ToggleContainer>
        </ToggleWrapper>
    )
}

export default {
    title: 'Atoms/Button',
    component: ToggleStory,
}

export const ToggleTemplate = (args: toggleStoryPropsType) => <ToggleStory {...args}/>
ToggleTemplate.args = {
    isDark: false,
}