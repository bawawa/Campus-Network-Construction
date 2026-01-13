#!/usr/bin/env node

/**
 * 前端项目验证脚本
 * 检查项目结构和关键文件是否存在
 */

const fs = require('fs')
const path = require('path')

const projectRoot = __dirname

// 需要检查的关键文件
const requiredFiles = [
  'package.json',
  'src/main.js',
  'src/App.vue',
  'src/router/index.js',
  'src/store/index.js',
  'src/utils/axios.js',
  'index.html',
  '.babelrc',
  '.eslintrc.js',
  '.gitignore'
]

// 需要检查的目录
const requiredDirs = [
  'src/api',
  'src/components',
  'src/views',
  'src/store/modules',
  'src/assets/styles',
  'build',
  'config'
]

// 页面组件检查
const pageComponents = [
  'src/views/Login.vue',
  'src/views/Dashboard.vue',
  'src/views/layout/MainLayout.vue',
  'src/views/children/ChildrenList.vue',
  'src/views/children/AddChild.vue',
  'src/views/children/ChildDetail.vue'
]

function checkFile(filePath) {
  const fullPath = path.join(projectRoot, filePath)
  return fs.existsSync(fullPath)
}

function checkDir(dirPath) {
  const fullPath = path.join(projectRoot, dirPath)
  return fs.existsSync(fullPath) && fs.statSync(fullPath).isDirectory()
}

function verify() {
  console.log('🔍 开始验证前端项目结构...\n')

  let allPassed = true

  // 检查关键文件
  console.log('📄 检查关键文件:')
  requiredFiles.forEach(file => {
    const exists = checkFile(file)
    const status = exists ? '✅' : '❌'
    console.log(`  ${status} ${file}`)
    if (!exists) allPassed = false
  })

  console.log('')

  // 检查目录
  console.log('📁 检查必要目录:')
  requiredDirs.forEach(dir => {
    const exists = checkDir(dir)
    const status = exists ? '✅' : '❌'
    console.log(`  ${status} ${dir}`)
    if (!exists) allPassed = false
  })

  console.log('')

  // 检查页面组件
  console.log('🏗️  检查页面组件:')
  pageComponents.forEach(component => {
    const exists = checkFile(component)
    const status = exists ? '✅' : '❌'
    console.log(`  ${status} ${component}`)
    if (!exists) allPassed = false
  })

  console.log('')

  // 检查package.json依赖
  console.log('📦 检查依赖配置:')
  try {
    const packageJson = require('./package.json')
    const requiredDeps = ['vue', 'vue-router', 'vuex', 'element-ui', 'axios']

    requiredDeps.forEach(dep => {
      const hasDep = packageJson.dependencies && packageJson.dependencies[dep]
      const status = hasDep ? '✅' : '❌'
      console.log(`  ${status} ${dep}`)
      if (!hasDep) allPassed = false
    })
  } catch (error) {
    console.log('  ❌ package.json 读取失败')
    allPassed = false
  }

  console.log('')

  // 总结
  if (allPassed) {
    console.log('🎉 项目验证通过！前端项目结构完整。')
    console.log('\n📋 项目概览:')
    console.log('  • Vue 2 + Element UI 前端框架')
    console.log('  • 完整的用户认证系统')
    console.log('  • 儿童档案管理功能')
    console.log('  • 响应式布局设计')
    console.log('  • 模块化状态管理')
    console.log('  • 完整的构建配置')
    console.log('\n🚀 可以开始开发或运行项目了！')
  } else {
    console.log('❌ 项目验证失败，请检查缺失的文件或目录。')
  }

  return allPassed
}

// 运行验证
if (require.main === module) {
  verify()
}

module.exports = verify

