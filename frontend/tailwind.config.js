/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#EEF0FF', 100: '#D9DDFF', 200: '#B3BBFF', 300: '#8A94FF',
          400: '#6470FF', 500: '#4353FF', 600: '#3442E0', 700: '#2533C2',
          800: '#1A259A', 900: '#101770',
        },
        accent: {
          50: '#E6FBF8', 100: '#CCFAF3', 200: '#99F4E7', 300: '#66EDDA',
          400: '#33E5CC', 500: '#00DCBE', 600: '#00B89E', 700: '#00957F',
          800: '#007160', 900: '#004E42',
        },
        success: { 50: '#ECFDF5', 100: '#D1FAE5', 500: '#10B981', 600: '#059669', 700: '#047857' },
        warning: { 50: '#FFFBEB', 100: '#FEF3C7', 500: '#F59E0B', 600: '#D97706', 700: '#B45309' },
        error: { 50: '#FEF2F2', 100: '#FEE2E2', 500: '#EF4444', 600: '#DC2626', 700: '#B91C1C' },
        info: { 50: '#EFF6FF', 100: '#DBEAFE', 500: '#3B82F6', 600: '#2563EB', 700: '#1D4ED8' },
        neutral: {
          50: '#F8FAFC', 100: '#F1F5F9', 200: '#E2E8F0', 300: '#CBD5E1',
          400: '#94A3B8', 500: '#64748B', 600: '#475569', 700: '#334155',
          800: '#1E293B', 900: '#0F172A',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'monospace'],
      },
      borderRadius: {
        'xs': '2px', 'sm': '4px', 'md': '6px', 'lg': '8px',
        'xl': '12px', '2xl': '16px', '3xl': '24px',
      },
      boxShadow: {
        'xs': '0 1px 2px 0 rgba(15,23,42,0.05)',
        'sm': '0 1px 3px 0 rgba(15,23,42,0.1), 0 1px 2px -1px rgba(15,23,42,0.06)',
        'md': '0 4px 6px -1px rgba(15,23,42,0.1), 0 2px 4px -2px rgba(15,23,42,0.06)',
        'lg': '0 10px 15px -3px rgba(15,23,42,0.1), 0 4px 6px -4px rgba(15,23,42,0.05)',
        'primary': '0 4px 14px 0 rgba(67,83,255,0.3)',
      },
    },
  },
  plugins: [require("@tailwindcss/forms")],
};
