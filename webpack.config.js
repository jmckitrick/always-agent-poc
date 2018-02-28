/* const webpack = require('webpack');
 * const path = require('path');
 *
 * const BUILD_DIR = path.resolve(__dirname, 'public', 'js');
 * const APP_DIR = path.resolve(__dirname, 'src', 'js');
 *
 * const config = {
 *   entry: `${APP_DIR}/library.js`,
 *   output: {
 *     path: BUILD_DIR,
 *     filename: 'npm-bundle.js'
 *   },
 * };
 *
 * module.exports = config;
 * */
module.exports = {
  entry: './library.js',
  output: {
    filename: 'resources/public/js/npm-bundle.js'
  }
};