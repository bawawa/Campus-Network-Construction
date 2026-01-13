<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ userInfo && userInfo.name }}！</h2>
            <p>今天是 {{ currentDate }}，让我们关注孩子的营养健康</p>
          </div>
          <div class="welcome-icon">
            <i class="el-icon-sunny"></i>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon children">
                <i class="el-icon-s-custom"></i>
              </div>
              <div class="stat-info">
                <h3>{{ stats.childrenCount }}</h3>
                <p>管理儿童</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon records">
                <i class="el-icon-document"></i>
              </div>
              <div class="stat-info">
                <h3>{{ stats.recordsCount }}</h3>
                <p>饮食记录</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon reports">
                <i class="el-icon-s-data"></i>
              </div>
              <div class="stat-info">
                <h3>{{ stats.reportsCount }}</h3>
                <p>营养报告</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon recipes">
                <i class="el-icon-dish"></i>
              </div>
              <div class="stat-info">
                <h3>{{ stats.recipesCount }}</h3>
                <p>推荐食谱</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <el-card>
        <div class="section-header">
          <h3>快速操作</h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-plus" @click="goToAddChild" class="action-button">
              添加儿童
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="success" icon="el-icon-document" @click="goToDietaryRecords" class="action-button">
              记录饮食
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="warning" icon="el-icon-s-data" @click="goToReports" class="action-button">
              查看报告
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="info" icon="el-icon-dish" @click="goToRecipes" class="action-button">
              食谱推荐
            </el-button>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activities" v-if="recentActivities.length > 0">
      <el-card>
        <div class="section-header">
          <h3>最近活动</h3>
        </div>
        <el-timeline v-if="!loading">
          <el-timeline-item
            v-for="(activity, index) in recentActivities"
            :key="index"
            :timestamp="activity.timestamp"
            :color="activity.color"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
        <div v-else v-loading="true" style="padding: 20px;"></div>
      </el-card>
    </div>

    <!-- 营养师角色特殊面板 -->
    <div class="nutritionist-panel" v-if="userRole === 'NUTRITIONIST'">
      <el-card>
        <div class="section-header">
          <h3>营养师工作台</h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="待处理咨询" :value="pendingConsultations" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="本月服务儿童" :value="monthlyChildren" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="营养报告" :value="nutritionReports" />
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 管理员角色特殊面板 -->
    <div class="admin-panel" v-if="userRole === 'ADMIN'">
      <el-card>
        <div class="section-header">
          <h3>系统管理概览</h3>
        </div>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="总用户数" :value="totalUsers" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="活跃用户" :value="activeUsers" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="家长数量" :value="parentCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="营养师数量" :value="nutritionistCount" />
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import moment from 'moment'
import {getChildrenByParent} from '@/api/child'
import {getRecentDietaryRecords} from '@/api/dietary'
import {getActiveUsers, getUsersByRole} from '@/api/user'

export default {
  name: 'Dashboard',
  data() {
    return {
      currentDate: moment().format('YYYY年MM月DD日 dddd'),
      loading: false,
      stats: {
        childrenCount: 0,
        recordsCount: 0,
        reportsCount: 0,
        recipesCount: 0
      },
      recentActivities: [],
      pendingConsultations: 5,
      monthlyChildren: 12,
      nutritionReports: 8,
      // 管理员统计数据
      totalUsers: 0,
      activeUsers: 0,
      parentCount: 0,
      nutritionistCount: 0
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo', 'userRole', 'userId'])
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      if (!this.userId) {
        this.$message.warning('请先登录')
        return
      }

      this.loading = true
      try {
        // 根据角色加载不同的数据
        if (this.userRole === 'PARENT') {
          await this.loadParentDashboard()
        } else if (this.userRole === 'NUTRITIONIST') {
          await this.loadNutritionistDashboard()
        } else if (this.userRole === 'ADMIN') {
          await this.loadAdminDashboard()
        }
      } catch (error) {
        console.error('加载仪表盘数据失败:', error)
      } finally {
        this.loading = false
      }
    },

    async loadParentDashboard() {
      try {
        // 获取儿童数量
        const childrenResponse = await getChildrenByParent(this.userId)
        const children = Array.isArray(childrenResponse) ? childrenResponse : []
        this.stats.childrenCount = children.length

        // 获取第一个儿童的饮食记录数量
        if (children.length > 0) {
          const firstChildId = children[0].id
          const recordsResponse = await getRecentDietaryRecords(firstChildId, 30)
          const records = Array.isArray(recordsResponse) ? recordsResponse : []
          this.stats.recordsCount = records.length

          // 生成最近活动
          this.recentActivities = this.generateRecentActivities(records, children)
        }

        // 暂时使用模拟数据
        this.stats.reportsCount = 0
        this.stats.recipesCount = 0
      } catch (error) {
        console.error('加载家长仪表盘数据失败:', error)
      }
    },

    async loadNutritionistDashboard() {
      try {
        // 获取所有家长用户（作为服务对象）
        const parentsResponse = await getUsersByRole('PARENT')
        const parents = Array.isArray(parentsResponse) ? parentsResponse : []
        this.stats.childrenCount = parents.length

        // 暂时使用模拟数据
        this.stats.recordsCount = 0
        this.stats.reportsCount = 0
        this.stats.recipesCount = 0
      } catch (error) {
        console.error('加载营养师仪表盘数据失败:', error)
      }
    },

    async loadAdminDashboard() {
      try {
        // 获取活跃用户
        const activeUsersResponse = await getActiveUsers()
        const activeUsers = Array.isArray(activeUsersResponse) ? activeUsersResponse : []
        this.activeUsers = activeUsers.length

        // 获取家长数量
        const parentsResponse = await getUsersByRole('PARENT')
        const parents = Array.isArray(parentsResponse) ? parentsResponse : []
        this.parentCount = parents.length

        // 获取营养师数量
        const nutritionistsResponse = await getUsersByRole('NUTRITIONIST')
        const nutritionists = Array.isArray(nutritionistsResponse) ? nutritionistsResponse : []
        this.nutritionistCount = nutritionists.length

        // 总用户数
        this.totalUsers = this.activeUsers

        this.stats.childrenCount = this.parentCount
        this.stats.recordsCount = 0
        this.stats.reportsCount = 0
        this.stats.recipesCount = 0
      } catch (error) {
        console.error('加载管理员仪表盘数据失败:', error)
      }
    },

    generateRecentActivities(records, children) {
      const activities = []

      // 从最近的饮食记录生成活动
      if (records.length > 0) {
        records.slice(0, 3).forEach(record => {
          const child = children.find(c => c.id === record.child?.id)
          const childName = child ? child.name : '未知儿童'
          const mealType = this.getMealTypeText(record.mealType)
          activities.push({
            content: `记录了${childName}的${mealType} - ${record.foodItem?.name || '食物'}`,
            timestamp: this.formatRelativeTime(record.createdAt),
            color: '#409EFF'
          })
        })
      }

      // 如果活动不足3个，添加一些默认活动
      while (activities.length < 3 && children.length > 0) {
        activities.push({
          content: `查看 ${children[0].name} 的儿童档案`,
          timestamp: '最近',
          color: '#67C23A'
        })
        break
      }

      return activities
    },

    formatRelativeTime(dateString) {
      if (!dateString) return '刚刚'
      const date = moment(dateString)
      const now = moment()
      const diffMinutes = now.diff(date, 'minutes')
      const diffHours = now.diff(date, 'hours')
      const diffDays = now.diff(date, 'days')

      if (diffMinutes < 60) {
        return `${diffMinutes}分钟前`
      } else if (diffHours < 24) {
        return `${diffHours}小时前`
      } else if (diffDays === 1) {
        return '昨天'
      } else if (diffDays < 7) {
        return `${diffDays}天前`
      } else {
        return date.format('MM-DD')
      }
    },

    getMealTypeText(type) {
      const map = {
        'BREAKFAST': '早餐',
        'LUNCH': '午餐',
        'DINNER': '晚餐',
        'SNACK': '加餐',
        'SUPPER': '夜宵'
      }
      return map[type] || type
    },

    goToAddChild() {
      this.$router.push('/children/add')
    },

    goToDietaryRecords() {
      this.$router.push('/children')
    },

    goToReports() {
      this.$router.push('/reports')
    },

    goToRecipes() {
      this.$router.push('/recipes')
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-card {
  background: linear-gradient(135deg, #409EFF 0%, #36a3f7 100%);
  border: none;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.welcome-icon {
  font-size: 48px;
  opacity: 0.8;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.children {
  background: linear-gradient(135deg, #409EFF, #36a3f7);
}

.stat-icon.records {
  background: linear-gradient(135deg, #67C23A, #5daf34);
}

.stat-icon.reports {
  background: linear-gradient(135deg, #E6A23C, #d4a733);
}

.stat-icon.recipes {
  background: linear-gradient(135deg, #F56C6C, #e56363);
}

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.quick-actions {
  margin-bottom: 30px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.action-button {
  width: 100%;
  height: 60px;
  font-size: 16px;
}

.recent-activities {
  margin-bottom: 30px;
}

.nutritionist-panel {
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
  }

  .welcome-icon {
    margin-top: 15px;
  }

  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin: 0 0 15px 0;
  }
}
</style>

