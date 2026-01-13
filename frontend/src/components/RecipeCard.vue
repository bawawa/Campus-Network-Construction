<template>
  <el-card class="recipe-card">
    <div class="recipe-header">
      <h3 class="recipe-title">{{ recipeItem.recipe.name }}</h3>
      <el-rate v-model="recipeItem.recipe.difficultyLevel" disabled show-score text-color="#ff9900">
      </el-rate>
    </div>

    <div class="recipe-info">
      <el-tag size="mini" :type="getMealTypeColor(recipeItem.recipe.suitableMealType)">
        {{ getMealTypeName(recipeItem.recipe.suitableMealType) }}
      </el-tag>
      <span class="cooking-time">
        <i class="el-icon-time"></i> {{ recipeItem.recipe.cookingTime || 0 }}分钟
      </span>
    </div>

    <div class="recipe-description">
      {{ recipeItem.recipe.description || '暂无描述' }}
    </div>

    <div class="recommendation-reasons">
      <h4>推荐理由:</h4>
      <ul>
        <li v-for="(reason, index) in recipeItem.reasons" :key="index">{{ reason }}</li>
      </ul>
    </div>

    <div class="recipe-footer">
      <el-progress
        :percentage="recipeItem.suitabilityScore"
        :color="getScoreColor(recipeItem.suitabilityScore)"
        :format="score => `适合度: ${score}分`"
      ></el-progress>
      <el-button type="primary" size="small" @click="viewDetail">
        查看详情
      </el-button>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'RecipeCard',
  props: {
    recipe: {
      type: Object,
      required: true
    }
  },
  computed: {
    recipeItem() {
      return this.recipe || { recipe: {}, reasons: [], suitabilityScore: 0 }
    }
  },
  methods: {
    viewDetail() {
      this.$emit('view-detail', this.recipeItem.recipe)
    },

    getMealTypeName(mealType) {
      const names = {
        'BREAKFAST': '早餐',
        'LUNCH': '午餐',
        'DINNER': '晚餐',
        'SNACK': '加餐',
        'SUPPER': '夜宵',
        'ANY': '任意'
      }
      return names[mealType] || '未知'
    },

    getMealTypeColor(mealType) {
      const colors = {
        'BREAKFAST': 'success',
        'LUNCH': 'warning',
        'DINNER': 'primary',
        'SNACK': 'info',
        'SUPPER': 'danger',
        'ANY': 'info'
      }
      return colors[mealType] || 'info'
    },

    getScoreColor(score) {
      if (score >= 80) return '#67C23A'
      if (score >= 60) return '#E6A23C'
      return '#F56C6C'
    }
  }
}
</script>

<style scoped>
.recipe-card {
  transition: all 0.3s;
  cursor: pointer;
  margin-bottom: 15px;
}

.recipe-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.recipe-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.recipe-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
  flex: 1;
}

.recipe-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.cooking-time {
  font-size: 12px;
  color: #909399;
}

.recipe-description {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommendation-reasons {
  margin-bottom: 15px;
}

.recommendation-reasons h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #303133;
}

.recommendation-reasons ul {
  margin: 0;
  padding-left: 20px;
}

.recommendation-reasons li {
  font-size: 12px;
  color: #67C23A;
  margin-bottom: 4px;
}

.recipe-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

