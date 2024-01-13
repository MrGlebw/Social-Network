/** @type {import('tailwindcss').Config} */

const config: Config = {
    content: [
        './components/**/*.{js,ts,jsx,tsx,mdx}',
        './components/stories/*.{js,ts,jsx,tsx}',
    ],
    theme: {
        extend: {
            backgroundImage: {
                'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
                'gradient-conic':
                    'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
            },
        },
    },
    plugins: [],
}
export default config
