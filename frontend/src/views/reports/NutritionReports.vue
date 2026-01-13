<template>
  <div class="nutrition-reports">
    <div class="page-header">
      <h2>营养分析报告</h2>
      <div class="header-actions">
        <el-select v-model="selectedChildId" placeholder="选择儿童" style="width: 200px; margin-right: 10px;">
          <el-option
            v-for="child in children"
            :key="child.id"
            :label="child.name"
            :value="child.id"
          ></el-option>
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 300px; margin-right: 10px;"
          @change="onDateRangeChange"
        ></el-date-picker>

        <el-button type="primary" @click="loadAnalysis" :loading="loading">
          生成报告
        </el-button>
        <el-button type="success" @click="generateAIReport" :loading="loading" icon="el-icon-magic-stick">
          AI 智能报告
        </el-button>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="!currentAnalysis" class="empty-state">
      <el-empty description="请选择儿童和时间范围生成营养分析报告">
      </el-empty>
    </div>

    <div v-else class="report-content">
      <!-- 总体评分卡片 -->
      <el-row :gutter="20" class="score-cards">
        <el-col :span="6">
          <el-card class="score-card">
            <div class="score-item">
              <div class="score-number">{{ overallScore }}</div>
              <div class="score-label">营养均衡评分</div>
              <el-progress
                :percentage="overallScore"
                :color="getScoreColor(overallScore)"
                type="circle"
                :width="60"
              ></el-progress>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="score-card">
            <div class="score-item">
              <div class="score-number">{{ totalRecords }}</div>
              <div class="score-label">记录总数</div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="score-card">
            <div class="score-item">
              <div class="score-number">{{ totalCategories }}</div>
              <div class="score-label">食物种类</div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="score-card">
            <div class="score-item">
              <div class="score-number">{{ totalMeals }}</div>
              <div class="score-label">用餐次数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 营养摄入概览 -->
      <el-card class="chart-card">
        <div slot="header">
          <span>营养摄入概览</span>
        </div>
        <div class="nutrition-overview">
          <el-row :gutter="20">
            <el-col :span="12">
              <div id="nutritionChart" class="chart-container"></div>
            </el-col>
            <el-col :span="12">
              <div class="nutrition-list">
                <div
                  v-for="(value, key) in nutritionSummary"
                  :key="key"
                  class="nutrition-item"
                >
                  <div class="nutrition-name">{{ getNutritionName(key) }}</div>
                  <div class="nutrition-value">{{ value }}{{ getNutritionUnit(key) }}</div>
                  <div class="nutrition-status">
                    <el-tag
                      :type="getNutritionStatus(key)"
                      size="mini"
                    >
                      {{ getNutritionStatusText(key) }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- 每日营养趋势 -->
      <el-card class="chart-card">
        <div slot="header">
          <span>每日营养摄入趋势</span>
        </div>
        <div id="trendChart" class="chart-container"></div>
      </el-card>

      <!-- 餐次分布分析 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <div slot="header">
              <span>餐次分布</span>
            </div>
            <div id="mealChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="chart-card">
            <div slot="header">
              <span>食物类别分析</span>
            </div>
            <div id="categoryChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 营养均衡评估 -->
      <el-card class="chart-card">
        <div slot="header">
          <span>营养均衡评估</span>
        </div>
        <div class="balance-assessment">
          <el-row :gutter="20">
            <el-col :span="12">
              <div id="balanceChart" class="chart-container"></div>
            </el-col>
            <el-col :span="12">
              <div class="recommendations">
                <h4>营养建议</h4>
                <ul>
                  <li v-for="(recommendation, index) in recommendations" :key="index">
                    {{ recommendation }}
                  </li>
                </ul>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </div>

    <!-- AI 报告对话框 -->
    <el-dialog
      title="AI 智能营养分析报告"
      :visible.sync="showAIReportDialog"
      width="80%"
      :close-on-click-modal="false"
      class="ai-report-dialog"
    >
      <div v-loading="generatingAIReport" class="report-content-wrapper">
        <div v-if="aiReportContent" class="markdown-report" v-html="formatAIReport(aiReportContent)"></div>
        <div v-else-if="generatingAIReport" class="loading-text">
          <el-empty description="AI 正在分析数据，生成报告中，请稍候..." />
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="closeAIReportDialog">关闭</el-button>
        <el-button type="primary" @click="copyAIReport" :disabled="!aiReportContent">
          <i class="el-icon-document-copy"></i> 复制报告
        </el-button>
        <el-button type="success" @click="downloadAIReport" :disabled="!aiReportContent">
          <i class="el-icon-download"></i> 下载报告
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {mapActions, mapGetters, mapState} from 'vuex'
import * as echarts from 'echarts'
import moment from 'moment'

export default {
  name: 'NutritionReports',
  data() {
    return {
      children: [],
      selectedChildId: null,
      dateRange: [],
      charts: {},
      showAIReportDialog: false,
      aiReportContent: '',
      generatingAIReport: false
    }
  },
  computed: {
    ...mapState('nutritionAnalysis', ['loading', 'currentAnalysis', 'aiReport']),
    ...mapGetters('nutritionAnalysis', [
      'nutritionSummary',
      'balanceAssessment',
      'recommendations',
      'dailyTrends',
      'mealDistribution',
      'foodCategoryAnalysis',
      'overallScore'
    ]),

    totalRecords() {
      return (this.currentAnalysis && this.currentAnalysis.totalRecords) || 0
    },

    totalCategories() {
      return (this.foodCategoryAnalysis && this.foodCategoryAnalysis.totalCategories) || 0
    },

    totalMeals() {
      return (this.mealDistribution && this.mealDistribution.totalMeals) || 0
    }
  },
  created() {
    this.loadChildren()
  },
  methods: {
    ...mapActions('nutritionAnalysis', [
      'analyzeNutritionIntake',
      'getNutritionTrends'
    ]),
    ...mapActions('child', ['fetchChildren']),

    async loadChildren() {
      try {
        const response = await this.fetchChildren()
        // axios拦截器已返回 response.data，所以 response 就是数据
        const data = Array.isArray(response) ? response : []
        this.children = data
        if (this.children.length > 0 && !this.selectedChildId) {
          this.selectedChildId = this.children[0].id
        }
      } catch (error) {
        this.$message.error('加载儿童列表失败: ' + error.message)
      }
    },

    async loadAnalysis() {
      if (!this.selectedChildId) {
        this.$message.warning('请选择儿童')
        return
      }

      if (!this.dateRange || this.dateRange.length !== 2) {
        this.$message.warning('请选择时间范围')
        return
      }

      try {
        const startDate = moment(this.dateRange[0]).format('YYYY-MM-DD')
        const endDate = moment(this.dateRange[1]).format('YYYY-MM-DD')

        await this.analyzeNutritionIntake({
          childId: this.selectedChildId,
          startDate,
          endDate
        })

        this.$nextTick(() => {
          this.initCharts()
        })
      } catch (error) {
        this.$message.error('生成报告失败: ' + error.message)
      }
  },

  async generateAIReport() {
      if (!this.selectedChildId) {
        this.$message.warning('请选择儿童')
        return
      }

      this.generatingAIReport = true

      try {
        let startDate = null
        let endDate = null

        if (this.dateRange && this.dateRange.length === 2) {
          startDate = moment(this.dateRange[0]).format('YYYY-MM-DD')
          endDate = moment(this.dateRange[1]).format('YYYY-MM-DD')
        }

        const response = await this.$store.dispatch('nutritionAnalysis/generateAIReport', {
          childId: this.selectedChildId,
          startDate,
          endDate
        })

        this.aiReportContent = response.data
        this.showAIReportDialog = true

        // 显示保存成功的提示
        if (response.reportId) {
          this.$message.success('AI 报告生成并保存成功')
        }
      } catch (error) {
        this.$message.error('AI报告生成失败: ' + error.message)
      } finally {
        this.generatingAIReport = false
      }
    },

    closeAIReportDialog() {
      this.showAIReportDialog = false
    },

    copyAIReport() {
      navigator.clipboard.writeText(this.aiReportContent).then(() => {
        this.$message.success('报告已复制到剪贴板')
      }).catch(() => {
        this.$message.error('复制失败')
      })
    },

    onDateRangeChange() {
      // 日期范围变化时的处理
    },

    initCharts() {
      this.initNutritionChart()
      this.initTrendChart()
      this.initMealChart()
      this.initCategoryChart()
      this.initBalanceChart()
    },

    initNutritionChart() {
      const chart = echarts.init(document.getElementById('nutritionChart'))
      const data = Object.entries(this.nutritionSummary).map(([key, value]) => ({
        name: this.getNutritionName(key),
        value: value
      }))

      const option = {
        title: {
          text: '营养摄入分布',
          left: 'center'
        },
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
            name: '营养摄入',
            type: 'pie',
            radius: '50%',
            data: data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.nutritionChart = chart
    },

    initTrendChart() {
      const chart = echarts.init(document.getElementById('trendChart'))
      const trends = this.dailyTrends

      if (!trends || trends.length === 0) return

      const dates = trends.map(item => item.date)
      const series = []
      const nutrients = ['calories', 'protein', 'carbohydrates', 'fat']
      const colors = ['#5470C6', '#91CC75', '#FAC858', '#EE6666']

      nutrients.forEach((nutrient, index) => {
        const data = trends.map(item => {
          const nutrition = item.nutrition || {}
          return nutrition[nutrient] || 0
        })

        series.push({
          name: this.getNutritionName(nutrient),
          type: 'line',
          data: data,
          color: colors[index]
        })
      })

      const option = {
        title: {
          text: '营养摄入趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: nutrients.map(n => this.getNutritionName(n)),
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: series
      }

      chart.setOption(option)
      this.charts.trendChart = chart
    },

    initMealChart() {
      const chart = echarts.init(document.getElementById('mealChart'))
      const mealData = (this.mealDistribution && this.mealDistribution.mealCount) || {}

      const data = Object.entries(mealData).map(([name, value]) => ({
        name,
        value
      }))

      const option = {
        title: {
          text: '餐次分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        series: [
          {
            name: '餐次分布',
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
      this.charts.mealChart = chart
    },

    initCategoryChart() {
      const chart = echarts.init(document.getElementById('categoryChart'))
      const categoryData = (this.foodCategoryAnalysis && this.foodCategoryAnalysis.categoryCount) || {}

      const data = Object.entries(categoryData).map(([name, value]) => ({
        name,
        value
      }))

      const option = {
        title: {
          text: '食物类别分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        series: [
          {
            name: '食物类别',
            type: 'pie',
            radius: '50%',
            data: data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.categoryChart = chart
    },

    initBalanceChart() {
      const chart = echarts.init(document.getElementById('balanceChart'))
      const percentages = (this.balanceAssessment && this.balanceAssessment.percentages) || {}

      const data = Object.entries(percentages).map(([key, value]) => ({
        name: this.getNutritionName(key),
        value: value
      }))

      const option = {
        title: {
          text: '营养摄入达标率',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}: {c}%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          max: 150,
          axisLabel: {
            formatter: '{value}%'
          }
        },
        yAxis: {
          type: 'category',
          data: data.map(item => item.name)
        },
        series: [
          {
            name: '达标率',
            type: 'bar',
            data: data.map(item => item.value),
            itemStyle: {
              color: function(params) {
                if (params.value < 80) return '#F56C6C'
                if (params.value > 120) return '#E6A23C'
                return '#67C23A'
              }
            }
          }
        ]
      }

      chart.setOption(option)
      this.charts.balanceChart = chart
    },

    getNutritionName(key) {
      const names = {
        calories: '热量',
        protein: '蛋白质',
        carbohydrates: '碳水化合物',
        fat: '脂肪',
        fiber: '膳食纤维',
        calcium: '钙',
        iron: '铁',
        vitaminC: '维生素C',
        vitaminD: '维生素D'
      }
      return names[key] || key
    },

    getNutritionUnit(key) {
      const units = {
        calories: ' kcal',
        protein: ' g',
        carbohydrates: ' g',
        fat: ' g',
        fiber: ' g',
        calcium: ' mg',
        iron: ' mg',
        vitaminC: ' mg',
        vitaminD: ' μg'
      }
      return units[key] || ''
    },

    getNutritionStatus(key) {
      const status = (this.balanceAssessment && this.balanceAssessment.status) || {}
      const statusValue = status[key]

      switch (statusValue) {
        case '不足': return 'danger'
        case '过量': return 'warning'
        case '正常': return 'success'
        default: return 'info'
      }
    },

    getNutritionStatusText(key) {
      const status = (this.balanceAssessment && this.balanceAssessment.status) || {}
      return status[key] || '未知'
    },

    getScoreColor(score) {
      if (score >= 80) return '#67C23A'
      if (score >= 60) return '#E6A23C'
      return '#F56C6C'
    },

    goToDashboard() {
      this.$router.push('/dashboard')
    },

    formatAIReport(content) {
      // 简单的 Markdown 格式化
      if (!content) return ''
      return content
        .replace(/### (.*)/g, '<h3>$1</h3>')
        .replace(/## (.*)/g, '<h2>$1</h2>')
        .replace(/# (.*)/g, '<h1>$1</h1>')
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*([^*]+)\*/g, '<em>$1</em>')
        .replace(/\n/g, '<br>')
        .replace(/^- (.*)/g, '<li>$1</li>')
        .replace(/(\d+)\. (.*)/g, '<li>$2</li>')
    },

    downloadAIReport() {
      if (!this.aiReportContent) return

      const blob = new Blob([this.aiReportContent], { type: 'text/plain;charset=utf-8' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `营养分析报告_${this.selectedChildId}_${moment().format('YYYY-MM-DD')}.txt`
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
    }
  },
  beforeDestroy() {
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose()
      }
    })
  }
}
</script>

<style scoped>
.nutrition-reports {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.loading-container {
  padding: 20px;
}

.empty-state {
  padding: 60px 0;
}

.report-content {
  margin-top: 20px;
}

.score-cards {
  margin-bottom: 20px;
}

.score-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.score-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.score-item {
  padding: 20px 0;
}

.score-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.score-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.nutrition-overview {
  margin-top: 20px;
}

.nutrition-list {
  padding: 20px;
}

.nutrition-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.nutrition-item:last-child {
  border-bottom: none;
}

.nutrition-name {
  font-weight: 500;
  color: #303133;
  flex: 1;
}

.nutrition-value {
  font-size: 16px;
  color: #409EFF;
  margin: 0 15px;
  min-width: 80px;
  text-align: right;
}

.nutrition-status {
  min-width: 60px;
}

.balance-assessment {
  margin-top: 20px;
}

.recommendations {
  padding: 20px;
}

.recommendations h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.recommendations ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recommendations li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  color: #666;
  line-height: 1.5;
}

.recommendations li:last-child {
  border-bottom: none;
}

@media (max-width: 768px) {
  .header-actions {
    margin-top: 15px;
    width: 100%;
  }

  .header-actions .el-select,
  .header-actions .el-date-picker,
  .header-actions .el-button {
    margin-bottom: 10px;
    width: 100%;
  }

  .chart-container {
    height: 300px;
  }
}

.ai-report-dialog .report-content-wrapper {
  min-height: 400px;
  max-height: 600px;
  overflow-y: auto;
  padding: 10px;
}

.ai-report-dialog .markdown-report {
  line-height: 1.8;
  color: #303133;
}

.ai-report-dialog .markdown-report h1 {
  font-size: 24px;
  margin: 20px 0 10px 0;
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
}

.ai-report-dialog .markdown-report h2 {
  font-size: 20px;
  margin: 18px 0 8px 0;
  color: #409EFF;
}

.ai-report-dialog .markdown-report h3 {
  font-size: 18px;
  margin: 15px 0 8px 0;
  color: #606266;
}

.ai-report-dialog .markdown-report strong {
  font-weight: bold;
  color: #409EFF;
}

.ai-report-dialog .markdown-report em {
  font-style: italic;
  color: #909399;
}

.ai-report-dialog .markdown-report li {
  margin: 8px 0 8px 20px;
  list-style-type: disc;
}

.ai-report-dialog .loading-text {
  text-align: center;
  padding: 40px 0;
}
</style>

