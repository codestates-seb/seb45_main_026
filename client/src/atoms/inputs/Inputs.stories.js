import { Input } from "./Inputs"
import { FormProvider, useForm } from 'react-hook-form';

const StorybookFormProvider = ({ children }) => {
    const methods = useForm();
    return (
      <FormProvider {...methods}>
        <form
          onSubmit={()=>{}}>
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

