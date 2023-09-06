import Loading from "./Loading";

export default {
    title: 'Components/Loading',
    component: Loading,
    argTypes: {
        isLoading: { control: 'boolean' }
    }
}

export const LoadingTemplate = (args) => <Loading {...args}/>
LoadingTemplate.args = {
    isLoading: true
}