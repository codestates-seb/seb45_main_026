import { Input } from "./Inputs"
import { FormProvider, useForm } from 'react-hook-form';
import { action } from '@storybook/addon-actions';

const StorybookFormProvider = ({ children }) => {
    const methods = useForm({
      defaultValues : {
        email:'',
        password:'',
      }
    });
    return (
      <FormProvider {...methods}>
        <form
          onSubmit={methods.handleSubmit(action('[React Hooks Form] Submit'))}>
          {children}
        </form>
      </FormProvider>
    );
  };

const withRHF = (showSubmitButton) => (Story) => (
    <StorybookFormProvider>
      <Story />
      {showSubmitButton && <button type="submit">Submit</button>}
    </StorybookFormProvider>
);

export default {
    title: 'Atoms/Inputs',
    component: Input,
    decorators: [withRHF(false)],
    argTypes: {
        width: {  control: 'text' },
    }
}

export const InputsTemplate = (args) => <Input {...args}/>
InputsTemplate.args = {
    width: '300px'
}

