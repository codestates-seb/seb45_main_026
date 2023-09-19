/** @type { import('@storybook/react').Preview } */
import './index.css';
import tokens from '../src/styles/tokens.json'
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";
import { persistStore } from "redux-persist";
import store from "../src/redux/Store"
import { MemoryRouter } from 'react-router-dom';

const globalTokens = tokens.global;

const preview = {
  parameters: {
    actions: { argTypesRegex: "^on[A-Z].*" },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/,
      },
    },
    backgrounds: {
      default: 'light',
      values: [
        { name: 'dark', value: `${globalTokens.Black.value}` },
        { name: 'light', value: `${globalTokens.Background.value}` }
      ]
    }
  },
};

export const decorators = [
  (Story) => (
    <Provider store={store}>
      <MemoryRouter initialEntries={['/']}>
        <Story/>
      </MemoryRouter>
    </Provider>
  )
]


export default preview;
