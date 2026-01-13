<template>
  <div class="dietary-records">
    <div class="page-header">
      <h2>饮食记录管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="showAddRecord">
        添加记录
      </el-button>
    </div>

    <el-card>
      <div class="coming-soon">
        <el-empty description="饮食记录管理功能开发中...">
          <el-button type="primary" @click="goBack">返回儿童详情</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'DietaryRecords',
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    showAddRecord() {
      this.$message.info('添加记录功能开发中...')
    }
  }
}
</script>

<style scoped>
.dietary-records {
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

.coming-soon {
  padding: 60px 0;
}
</style>

