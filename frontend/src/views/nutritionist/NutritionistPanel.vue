<template>
  <div class="nutritionist-panel">
    <div class="page-header">
      <h2>营养师工作台</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <h3>12</h3>
              <p>本月服务儿童</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon">
              <i class="el-icon-message"></i>
            </div>
            <div class="stat-info">
              <h3>5</h3>
              <p>待处理咨询</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <h3>30</h3>
              <p>营养报告</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20">
      <div class="coming-soon">
        <el-empty description="营养师工作台功能开发中...">
          <el-button type="primary" @click="goToChildren">查看儿童列表</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'NutritionistPanel',
  methods: {
    goToChildren() {
      this.$router.push('/children')
    }
  }
}
</script>

<style scoped>
.nutritionist-panel {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
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
  background: linear-gradient(135deg, #409EFF, #36a3f7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 15px;
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

.mt-20 {
  margin-top: 20px;
}

.coming-soon {
  padding: 60px 0;
}
</style>

