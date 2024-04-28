/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {},
  },
  daisyui: {
    themes: [
      {
        mytheme: {
          "primary": "#89259a",
          "secondary": "#88d530",
          "accent": "#eeaac3",
          "neutral": "#f4f3e3",
          "base-100": "#f2f4f1",
          "info": "#e8dbdd",
          "success": "#5fc158",
          "warning": "#f1ba0e",
          "error": "#d80d0d",
        },
      },
    ],
  },

  plugins: [require("daisyui")],
}
