/** @type { import('@storybook/react').Preview } */
import './index.css';
import tokens from '../src/styles/tokens.json'

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
        { name: 'dark', value: `${globalTokens.DarkGray.value}` },
        { name: 'light', value: `${globalTokens.White.value}` }
      ]
    }
  },
};

export default preview;
