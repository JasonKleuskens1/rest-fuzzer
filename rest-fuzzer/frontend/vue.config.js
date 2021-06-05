const path = require("path");

module.exports = {
  devServer: {
    proxy: {
      '/rest/': {
        target: 'http://localhost:8888/',
        ws: true,
        changeOrigin: true
      }
    }
  },
};