import type { Preview } from '@storybook/react'
import 'tailwindcss/tailwind.css'
import '../../../apps/frontend/app/globals.css'

const parameters = {
  actions: { argTypesRegex: '^on[A-Z].*' },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/
    }
  },
  backgrounds: {
    default: 'dark'
  },
  viewport: {
    viewports: {
      mobile: {
        name: 'Mobile',
        styles: {
          width: '375px',
          height: '667px'
        }
      },
      tablet: {
        name: 'Tablet',
        styles: {
          width: '768px',
          height: '1024px'
        }
      },
      laptop: {
        name: 'Laptop',
        styles: {
          width: '1440px',
          height: '900px'
        }
      },
      desktop: {
        name: 'Desktop',
        styles: {
          width: '1920px',
          height: '1080px'
        }
      },
      iPhone15Pro: {
        name: 'iPhone 15 Pro',
        styles: {
          width: '390px', // CSS pixels
          height: '844px' // CSS pixels
        }
      },
      iPhone15ProMax: {
        name: 'iPhone 15 Pro Max',
        styles: {
          width: '428px', // CSS pixels
          height: '926px' // CSS pixels
        }
      },
      iPadMini: {
        name: 'iPad Mini',
        styles: {
          width: '744px', // CSS pixels
          height: '1133px' // CSS pixels
        }
      },
      MacBookAir13: {
        name: 'MacBook Air 13"',
        styles: {
          width: '1440px', // CSS pixels
          height: '900px' // CSS pixels
        }
      }
    },
    defaultViewport: 'responsive'
  }
}

const preview: Preview = {
  parameters: parameters
}

export default preview
