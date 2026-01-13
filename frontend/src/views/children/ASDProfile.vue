<template>
  <div class="asd-profile">
    <div class="page-header">
      <h2>ASD特质档案管理</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card>
      <div class="coming-soon">
        <el-empty description="ASD特质档案管理功能开发中...">
          <el-button type="primary" @click="goBack">返回儿童详情</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ASDProfile',
  methods: {
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.asd-profile {
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

