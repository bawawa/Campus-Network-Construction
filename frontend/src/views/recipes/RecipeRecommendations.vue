<template>
  <div class="recipe-recommendations">
    <div class="page-header">
      <h2>智慧食谱推荐</h2>
      <div class="header-actions">
        <el-select v-model="selectedChildId" placeholder="选择儿童" style="width: 200px; margin-right: 10px;">
          <el-option
            v-for="child in children"
            :key="child.id"
            :label="child.name"
            :value="child.id"
          ></el-option>
        </el-select>

        <el-select v-model="selectedMealType" placeholder="选择餐次" style="width: 120px; margin-right: 10px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="早餐" value="BREAKFAST"></el-option>
          <el-option label="午餐" value="LUNCH"></el-option>
          <el-option label="晚餐" value="DINNER"></el-option>
          <el-option label="加餐" value="SNACK"></el-option>
        </el-select>

        <el-button type="primary" @click="loadSmartRecommendations" :loading="loading">
          智能推荐
        </el-button>
        <el-button type="success" @click="loadAIRecommendations" :loading="loading" icon="el-icon-magic-stick">
          AI 智能推荐
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索食谱名称..."
          style="width: 300px; margin-right: 10px;"
          @keyup.enter.native="searchRecipes"
        >
          <i slot="prefix" class="el-icon-search"></i>
        </el-input>

        <el-select v-model="filters.mealType" placeholder="餐次" style="width: 120px; margin-right: 10px;">
          <el-option label="全部餐次" value=""></el-option>
          <el-option label="早餐" value="BREAKFAST"></el-option>
          <el-option label="午餐" value="LUNCH"></el-option>
          <el-option label="晚餐" value="DINNER"></el-option>
          <el-option label="加餐" value="SNACK"></el-option>
        </el-select>

        <el-select v-model="filters.maxDifficulty" placeholder="难度" style="width: 120px; margin-right: 10px;">
          <el-option label="不限难度" value=""></el-option>
          <el-option label="简单 (1-2级)" value="2"></el-option>
          <el-option label="中等 (3级)" value="3"></el-option>
          <el-option label="困难 (4-5级)" value="5"></el-option>
        </el-select>

        <el-select v-model="filters.maxCookingTime" placeholder="时间" style="width: 120px; margin-right: 10px;">
          <el-option label="不限时间" value=""></el-option>
          <el-option label="15分钟内" value="15"></el-option>
          <el-option label="30分钟内" value="30"></el-option>
          <el-option label="1小时内" value="60"></el-option>
        </el-select>

        <el-button type="primary" @click="searchRecipes" :loading="loading">搜索</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>
    </el-card>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="activeTab === 'smart' && smartRecommendations.length === 0" class="empty-state">
      <el-empty description="暂无智能推荐结果">
        <el-button type="primary" @click="loadSmartRecommendations">生成智能推荐</el-button>
      </el-empty>
    </div>

    <div v-else-if="activeTab === 'search' && searchResults.length === 0 && searchKeyword" class="empty-state">
      <el-empty :description="`未找到包含 \"${searchKeyword}\" 的食谱`">
        <el-button @click="resetSearch">重新搜索</el-button>
      </el-empty>
    </div>

    <div v-else class="recommendations-content">
      <!-- 标签页切换 -->
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="智能推荐" name="smart">
          <div class="recommendations-grid">
            <el-card v-for="item in smartRecommendations" :key="item.recipe.id" class="recipe-card">
              <div class="recipe-header">
                <h3 class="recipe-title">{{ item.recipe.name }}</h3>
                <el-rate v-model="item.recipe.difficultyLevel" disabled show-score text-color="#ff9900">
                </el-rate>
              </div>

              <div class="recipe-info">
                <el-tag size="mini" :type="getMealTypeColor(item.recipe.suitableMealType)">
                  {{ getMealTypeName(item.recipe.suitableMealType) }}
                </el-tag>
                <span class="cooking-time">
                  <i class="el-icon-time"></i> {{ item.recipe.cookingTime || 0 }}分钟
                </span>
              </div>

              <div class="recipe-description">
                {{ item.recipe.description || '暂无描述' }}
              </div>

              <div class="recommendation-reasons">
                <h4>推荐理由:</h4>
                <ul>
                  <li v-for="(reason, index) in item.reasons" :key="index">{{ reason }}</li>
                </ul>
              </div>

              <div class="recipe-footer">
                <el-progress
                  :percentage="item.suitabilityScore"
                  :color="getScoreColor(item.suitabilityScore)"
                  :format="score => `适合度: ${score}分`"
                ></el-progress>
                <el-button type="primary" size="small" @click="viewRecipeDetail(item.recipe)">
                  查看详情
                </el-button>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="搜索结果" name="search">
          <div class="search-results">
            <div class="search-info">
              找到 {{ searchResults.length }} 个相关食谱
              <span v-if="searchKeyword">，关键词: "{{ searchKeyword }}"</span>
            </div>
            <div class="recommendations-grid">
              <el-card v-for="recipe in searchResults" :key="recipe.id" class="recipe-card">
                <div class="recipe-header">
                  <h3 class="recipe-title">{{ recipe.name }}</h3>
                  <el-rate v-model="recipe.difficultyLevel" disabled show-score text-color="#ff9900">
                  </el-rate>
                </div>

                <div class="recipe-info">
                  <el-tag size="mini" :type="getMealTypeColor(recipe.suitableMealType)">
                    {{ getMealTypeName(recipe.suitableMealType) }}
                  </el-tag>
                  <span class="cooking-time">
                    <i class="el-icon-time"></i> {{ recipe.cookingTime || 0 }}分钟
                  </span>
                </div>

                <div class="recipe-description">
                  {{ recipe.description || '暂无描述' }}
                </div>

                <div class="recipe-footer">
                  <el-button type="primary" size="small" @click="viewRecipeDetail(recipe)">
                    查看详情
                  </el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 食谱详情对话框 -->
    <el-dialog :title="selectedRecipe && selectedRecipe.name" :visible.sync="recipeDialogVisible" width="700px">
      <div v-if="selectedRecipe" class="recipe-detail">
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="中文名称">{{ selectedRecipe.chineseName || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="适合餐次">{{ getMealTypeName(selectedRecipe.suitableMealType) }}</el-descriptions-item>
            <el-descriptions-item label="制作难度">{{ selectedRecipe.difficultyLevel || 1 }}级</el-descriptions-item>
            <el-descriptions-item label="制作时间">{{ selectedRecipe.cookingTime || 0 }}分钟</el-descriptions-item>
            <el-descriptions-item label="适合年龄">{{ selectedRecipe.ageGroup || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="预设食谱">{{ selectedRecipe.isPreset ? '是' : '否' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>食谱描述</h4>
          <p>{{ selectedRecipe.description || '暂无描述' }}</p>
        </div>

        <div class="detail-section">
          <h4>食材清单</h4>
          <div v-if="selectedRecipe.ingredients">
            <pre>{{ formatIngredients(selectedRecipe.ingredients) }}</pre>
          </div>
          <p v-else>暂无食材信息</p>
        </div>

        <div class="detail-section">
          <h4>制作步骤</h4>
          <div v-if="selectedRecipe.cookingSteps">
            <pre>{{ formatCookingSteps(selectedRecipe.cookingSteps) }}</pre>
          </div>
          <p v-else>暂无制作步骤</p>
        </div>

        <div v-if="selectedRecipe.textureNotes" class="detail-section">
          <h4>质地说明</h4>
          <p>{{ selectedRecipe.textureNotes }}</p>
        </div>

        <div v-if="selectedRecipe.allergenWarnings" class="detail-section">
          <h4>过敏原警告</h4>
          <el-alert :title="selectedRecipe.allergenWarnings" type="warning" show-icon></el-alert>
        </div>

        <div v-if="selectedRecipe.feedingTips" class="detail-section">
          <h4>喂食小贴士</h4>
          <p>{{ selectedRecipe.feedingTips }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- AI 推荐对话框 -->
    <el-dialog
      title="AI 智能食谱推荐"
      :visible.sync="aiRecommendationDialogVisible"
      width="80%"
      :close-on-click-modal="false"
      class="ai-recommendation-dialog"
    >
      <div v-loading="loading" class="recommendation-content-wrapper">
        <div v-if="aiRecommendation" class="markdown-content" v-html="formatAIRecommendation(aiRecommendation)"></div>
        <div v-else class="loading-text">
          <el-empty description="AI 正在分析儿童饮食需求，生成个性化食谱推荐..." />
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="aiRecommendationDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyAIRecommendation" :disabled="!aiRecommendation">
          <i class="el-icon-document-copy"></i> 复制推荐
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {mapActions, mapGetters, mapState} from 'vuex'
import RecipeCard from '@/components/RecipeCard.vue'

export default {
  name: 'RecipeRecommendations',
  components: {
    RecipeCard
  },
  data() {
    return {
      children: [],
      selectedChildId: null,
      selectedMealType: '',
      searchKeyword: '',
      activeTab: 'smart',
      filters: {
        mealType: '',
        maxDifficulty: '',
        maxCookingTime: ''
      },
      selectedRecipe: null,
      recipeDialogVisible: false,
      aiRecommendationDialogVisible: false
    }
  },
  computed: {
    ...mapState('recipe', ['loading', 'smartRecommendations', 'searchResults', 'aiRecommendation']),
    ...mapGetters('recipe', [])
  },
  created() {
    this.loadChildren()
  },
  methods: {
    ...mapActions('recipe', [
      'getSmartRecommendations',
      'searchRecipes',
      'generateAIRecommendations',
      'setSelectedChild'
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

    async loadSmartRecommendations() {
      if (!this.selectedChildId) {
        this.$message.warning('请选择儿童')
        return
      }

      try {
        await this.getSmartRecommendations({
          childId: this.selectedChildId,
          mealType: this.selectedMealType || null
        })
        this.activeTab = 'smart'
        this.$message.success('智能推荐生成成功')
      } catch (error) {
        this.$message.error('智能推荐生成失败: ' + error.message)
      }
    },

    async searchRecipes() {
      if (!this.searchKeyword.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }

      try {
        const filters = {
          mealType: this.filters.mealType || null,
          maxDifficulty: this.filters.maxDifficulty ? parseInt(this.filters.maxDifficulty) : null,
          maxCookingTime: this.filters.maxCookingTime ? parseInt(this.filters.maxCookingTime) : null
        }

        await this.searchRecipes({
          keyword: this.searchKeyword,
          ...filters
        })
        this.activeTab = 'search'
        this.$message.success('搜索完成')
      } catch (error) {
        this.$message.error('搜索失败: ' + error.message)
      }
    },

    resetFilters() {
      this.filters = {
        mealType: '',
        maxDifficulty: '',
        maxCookingTime: ''
      }
      this.searchKeyword = ''
    },

    resetSearch() {
      this.searchKeyword = ''
      this.resetFilters()
    },

    handleTabClick(tab) {
      this.activeTab = tab.name
    },

    viewRecipeDetail(recipe) {
      this.selectedRecipe = recipe
      this.recipeDialogVisible = true
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
    },

    formatIngredients(ingredients) {
      try {
        const parsed = JSON.parse(ingredients)
        return Array.isArray(parsed) ? parsed.join('\n') : ingredients
      } catch (e) {
        return ingredients
      }
    },

    formatCookingSteps(steps) {
      try {
        const parsed = JSON.parse(steps)
        return Array.isArray(parsed) ? parsed.join('\n') : steps
      } catch (e) {
        return steps
      }
    },

    goToDashboard() {
      this.$router.push('/dashboard')
    },

    async loadAIRecommendations() {
      if (!this.selectedChildId) {
        this.$message.warning('请选择儿童')
        return
      }

      try {
        const response = await this.generateAIRecommendations({
          childId: this.selectedChildId,
          mealType: this.selectedMealType || null
        })
        this.aiRecommendationDialogVisible = true
        this.$message.success('AI推荐生成成功')
      } catch (error) {
        this.$message.error('AI推荐生成失败: ' + error.message)
      }
    },

    copyAIRecommendation() {
      if (!this.aiRecommendation) return
      navigator.clipboard.writeText(this.aiRecommendation).then(() => {
        this.$message.success('推荐已复制到剪贴板')
      }).catch(() => {
        this.$message.error('复制失败')
      })
    },

    formatAIRecommendation(content) {
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
    }
  }
}
</script>

<style scoped>
.recipe-recommendations {
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

.search-card {
  margin-bottom: 20px;
}

.search-section {
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

.recommendations-content {
  margin-top: 20px;
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.recipe-card {
  transition: all 0.3s;
  cursor: pointer;
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

.search-results {
  margin-top: 20px;
}

.search-info {
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.recipe-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  border-left: 3px solid #409EFF;
  padding-left: 10px;
}

.detail-section pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .header-actions {
    margin-top: 15px;
    width: 100%;
  }

  .header-actions .el-select,
  .header-actions .el-button {
    margin-bottom: 10px;
    width: 100%;
  }

  .search-section {
    flex-direction: column;
    align-items: stretch;
  }

  .search-section .el-input,
  .search-section .el-select,
  .search-section .el-button {
    margin-bottom: 10px;
    width: 100%;
  }

  .recommendations-grid {
    grid-template-columns: 1fr;
  }
}

.ai-recommendation-dialog .recommendation-content-wrapper {
  min-height: 400px;
  max-height: 600px;
  overflow-y: auto;
  padding: 10px;
}

.ai-recommendation-dialog .markdown-content {
  line-height: 1.8;
  color: #303133;
}

.ai-recommendation-dialog .markdown-content h1 {
  font-size: 24px;
  margin: 20px 0 10px 0;
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
}

.ai-recommendation-dialog .markdown-content h2 {
  font-size: 20px;
  margin: 18px 0 8px 0;
  color: #409EFF;
}

.ai-recommendation-dialog .markdown-content h3 {
  font-size: 18px;
  margin: 15px 0 8px 0;
  color: #606266;
}

.ai-recommendation-dialog .markdown-content strong {
  font-weight: bold;
  color: #409EFF;
}

.ai-recommendation-dialog .markdown-content em {
  font-style: italic;
  color: #909399;
}

.ai-recommendation-dialog .markdown-content li {
  margin: 8px 0 8px 20px;
  list-style-type: disc;
}

.ai-recommendation-dialog .loading-text {
  text-align: center;
  padding: 40px 0;
}
</style>

