<template>
  <div class="admin-panel">
    <div class="page-header">
      <h2>管理员面板</h2>
      <el-button type="primary" @click="refreshData">
        <i class="el-icon-refresh"></i> 刷新数据
      </el-button>
    </div>

    <!-- 系统统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon users">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon active">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon children">
              <i class="el-icon-s-custom"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalChildren }}</div>
              <div class="stat-label">儿童总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon records">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalRecords }}</div>
              <div class="stat-label">饮食记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户分布 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>用户角色分布</span>
          </div>
          <div id="roleChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>用户增长趋势（近7天）</span>
          </div>
          <div id="growthChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近期活动 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>系统活动日志</span>
          </div>
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="timestamp" label="时间" width="180"></el-table-column>
            <el-table-column prop="type" label="类型" width="120">
              <template slot-scope="scope">
                <el-tag :type="getActivityType(scope.row.type)">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述"></el-table-column>
            <el-table-column prop="user" label="操作用户" width="150"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-row :gutter="20" class="actions-row">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>快速操作</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-button type="primary" icon="el-icon-user" class="action-button" @click="goToUsers">
                用户管理
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="success" icon="el-icon-s-tools" class="action-button" @click="goToRoles">
                角色权限
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="warning" icon="el-icon-setting" class="action-button" @click="goToSettings">
                系统设置
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="info" icon="el-icon-document" class="action-button" @click="goToReports">
                系统报告
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import moment from 'moment'

export default {
  name: 'AdminPanel',
  data() {
    return {
      stats: {
        totalUsers: 0,
        activeUsers: 0,
        totalChildren: 0,
        totalRecords: 0
      },
      recentActivities: [
        {
          timestamp: '2026-01-11 18:30',
          type: '用户注册',
          description: '新用户注册',
          user: '张三'
        },
        {
          timestamp: '2026-01-11 17:45',
          type: '数据导入',
          description: '批量导入食物营养数据',
          user: '系统'
        },
        {
          timestamp: '2026-01-11 16:20',
          type: '系统更新',
          description: '更新系统版本至 v1.2.0',
          user: '管理员'
        },
        {
          timestamp: '2026-01-11 15:00',
          type: '角色分配',
          description: '将用户李四分配为营养师角色',
          user: '管理员'
        }
      ],
      charts: {}
    }
  },
  mounted() {
    this.loadStats()
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose()
      }
    })
  },
  methods: {
    loadStats() {
      // 这里应该调用API获取统计数据
      // 暂时使用模拟数据
      this.stats = {
        totalUsers: 156,
        activeUsers: 120,
        totalChildren: 89,
        totalRecords: 1245
      }
    },

    refreshData() {
      this.loadStats()
      this.initCharts()
      this.$message.success('数据已刷新')
    },

    initCharts() {
      this.initRoleChart()
      this.initGrowthChart()
    },

    initRoleChart() {
      const chart = echarts.init(document.getElementById('roleChart'))
      const data = [
        { value: 89, name: '家长' },
        { value: 67, name: '营养师' },
        { value: 3, name: '管理员' }
      ]

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '用户角色',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      }

      chart.setOption(option)
      this.charts.roleChart = chart
    },

    initGrowthChart() {
      const chart = echarts.init(document.getElementById('growthChart'))
      const dates = []
      const growthData = []

      // 生成最近7天的日期
      for (let i = 6; i >= 0; i--) {
        const date = moment().subtract(i, 'days').format('MM-DD')
        dates.push(date)
        growthData.push(Math.floor(Math.random() * 10) + 1)
      }

      const option = {
        title: {
          text: '用户增长',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '新增用户',
            type: 'line',
            data: growthData,
            smooth: true,
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                ]
              }
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.growthChart = chart
    },

    getActivityType(type) {
      const types = {
        '用户注册': 'success',
        '数据导入': 'info',
        '系统更新': 'warning',
        '角色分配': 'primary'
      }
      return types[type] || 'info'
    },

    goToUsers() {
      this.$router.push('/admin/users')
    },

    goToRoles() {
      this.$router.push('/admin/roles')
    },

    goToSettings() {
      this.$message.info('系统设置功能开发中...')
    },

    goToReports() {
      this.$message.info('系统报告功能开发中...')
    }
  }
}
</script>

<style scoped>
.admin-panel {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.stat-item {
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

.stat-icon.users {
  background: linear-gradient(135deg, #409EFF, #36a3f7);
}

.stat-icon.active {
  background: linear-gradient(135deg, #67C23A, #5daf34);
}

.stat-icon.children {
  background: linear-gradient(135deg, #E6A23C, #d4a733);
}

.stat-icon.records {
  background: linear-gradient(135deg, #F56C6C, #e56363);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.activity-row {
  margin-bottom: 20px;
}

.actions-row {
  margin-bottom: 20px;
}

.action-button {
  width: 100%;
  height: 60px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .stat-item {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin: 0 0 10px 0;
  }

  .charts-row .el-col {
    margin-bottom: 20px;
  }

  .action-button {
    margin-bottom: 10px;
  }
}
</style>

