import Loading from "./Loading";

export default {
    title: 'Components/Loading',
    component: Loading,
    argTypes: {
        width: { control : 'text' },
        height: { control : 'text' }
    }
}

export const LoadingTemplate = (args) => <Loading {...args}/>
LoadingTemplate.args = {
    width: '100vw',
    height: '100px'
}