import { Meta, Story } from "@storybook/react";
import { LoadingContainer, LoadingImg } from "./Loading";
import loadingLight from '../../assets/images/loadings/loadingLight.gif';
import loadingDark from '../../assets/images/loadings/loadingDark.gif';

type loadingsPropsType = {
    isDark : boolean;
}

const Loadings = ({ isDark }:loadingsPropsType) => {
    return (
        <LoadingContainer>
            <LoadingImg src={isDark ? loadingDark : loadingLight}/>
        </LoadingContainer>
    )
}

export default {
    title : 'Components/Loading',
    component : Loadings,
    argTypes : {
        isDark: { controls : 'boolean' },
    }
}

export const LoadingTemplate = (args:loadingsPropsType) => <Loadings {...args}/>
LoadingTemplate.args = {
    isDark : false,
}