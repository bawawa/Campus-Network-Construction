<template>
  <div class="main-layout">
    <el-container style="height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>ASD营养系统</h2>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/dashboard">
            <i class="el-icon-s-home"></i>
            <span slot="title">仪表盘</span>
          </el-menu-item>

          <el-submenu index="children">
            <template slot="title">
              <i class="el-icon-s-custom"></i>
              <span>儿童管理</span>
            </template>
            <el-menu-item index="/children">儿童列表</el-menu-item>
            <el-menu-item index="/children/add">添加儿童</el-menu-item>
          </el-submenu>

          <el-menu-item index="/reports">
            <i class="el-icon-s-data"></i>
            <span slot="title">营养报告</span>
          </el-menu-item>

          <el-menu-item index="/recipes">
            <i class="el-icon-dish"></i>
            <span slot="title">食谱推荐</span>
          </el-menu-item>

          <el-menu-item
            index="/nutritionist"
            v-if="userRole === 'NUTRITIONIST'"
          >
            <i class="el-icon-user"></i>
            <span slot="title">营养师面板</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <h3 class="page-title">{{ currentPageTitle }}</h3>
          </div>

          <div class="header-right">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-info">
                <el-avatar size="small" icon="el-icon-user-solid"></el-avatar>
                <span class="username">{{ userInfo?.name }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>

              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>个人信息
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主要内容 -->
        <el-main class="main-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'MainLayout',
  computed: {
    ...mapGetters('user', ['userInfo', 'userRole']),

    activeMenu() {
      return this.$route.path
    },

    currentPageTitle() {
      const routeTitles = {
        '/dashboard': '仪表盘',
        '/children': '儿童列表',
        '/children/add': '添加儿童',
        '/reports': '营养报告',
        '/recipes': '食谱推荐',
        '/nutritionist': '营养师面板',
        '/profile': '个人信息'
      }
      return routeTitles[this.$route.path] || 'ASD营养系统'
    }
  },
  methods: {
    ...mapActions('user', ['logout']),

    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/profile')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },

    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.logout()
        this.$message.success('已退出登录')
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #1f2d3d;
}

.logo h2 {
  color: #fff;
  font-size: 16px;
  margin: 0;
  font-weight: 600;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left .page-title {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
  font-size: 14px;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    width: 64px !important;
  }

  .logo h2 {
    display: none;
  }

  .header {
    padding: 0 10px;
  }

  .page-title {
    font-size: 16px;
  }
}
</style>

