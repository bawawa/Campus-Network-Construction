'use strict'

// 参见vue-loader官网：https://vue-loader.vuejs.org/zh-cn/options.html

// 判断是否为生产环境
const isProduction = process.env.NODE_ENV === 'production'

module.exports = {
  // 部署应用包时的基本 URL
  publicPath: '/',

  // 构建输出目录
  outputDir: 'dist',

  // 静态资源目录
  assetsDir: 'static',

  // 是否在开发环境下通过 eslint-loader 在每次保存时 lint 代码
  lintOnSave: !isProduction,

  // 生产环境是否生成 source map
  productionSourceMap: false,

  // webpack-dev-server 相关配置
  devServer: {
    open: true,
    host: 'localhost',
    port: 8080,
    https: false,
    hotOnly: false,
    // 设置代理
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    },
    before: app => {}
  },

  // 第三方插件配置
  pluginOptions: {},

  // 配置webpack
  configureWebpack: {
    name: 'ASD儿童饮食与营养均衡系统',
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, '../src')
      }
    }
  },

  // 链式操作webpack
  chainWebpack: config => {
    // 设置svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  }
}

function resolve(dir) {
  return require('path').resolve(__dirname, dir)
}

