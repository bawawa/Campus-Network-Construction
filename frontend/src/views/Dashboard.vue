<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ userInfo?.name }}！</h2>
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
    <div class="recent-activities">
      <el-card>
        <div class="section-header">
          <h3>最近活动</h3>
        </div>
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in recentActivities"
            :key="index"
            :timestamp="activity.timestamp"
            :color="activity.color"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
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
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import moment from 'moment'

export default {
  name: 'Dashboard',
  data() {
    return {
      currentDate: moment().format('YYYY年MM月DD日 dddd'),
      stats: {
        childrenCount: 0,
        recordsCount: 0,
        reportsCount: 0,
        recipesCount: 0
      },
      recentActivities: [
        {
          content: '添加了新的饮食记录',
          timestamp: '2小时前',
          color: '#409EFF'
        },
        {
          content: '查看了营养分析报告',
          timestamp: '昨天',
          color: '#67C23A'
        },
        {
          content: '更新了儿童ASD特质档案',
          timestamp: '3天前',
          color: '#E6A23C'
        }
      ],
      pendingConsultations: 5,
      monthlyChildren: 12,
      nutritionReports: 8
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo', 'userRole'])
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    loadDashboardData() {
      // 这里应该调用API获取仪表盘数据
      // 暂时使用模拟数据
      if (this.userRole === 'PARENT') {
        this.stats = {
          childrenCount: 2,
          recordsCount: 15,
          reportsCount: 3,
          recipesCount: 8
        }
      } else if (this.userRole === 'NUTRITIONIST') {
        this.stats = {
          childrenCount: 25,
          recordsCount: 150,
          reportsCount: 30,
          recipesCount: 50
        }
      }
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

