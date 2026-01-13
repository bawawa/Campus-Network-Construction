<template>
  <div class="nutrition-reports">
    <div class="page-header">
      <h2>营养分析报告</h2>
    </div>

    <el-card>
      <div class="coming-soon">
        <el-empty description="营养分析报告功能开发中...">
          <el-button type="primary" @click="goToDashboard">返回仪表盘</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'NutritionReports',
  methods: {
    goToDashboard() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.nutrition-reports {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.coming-soon {
  padding: 60px 0;
}
</style>

