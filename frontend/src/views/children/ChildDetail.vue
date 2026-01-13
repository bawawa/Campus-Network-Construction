<template>
  <div class="child-detail">
    <div class="page-header">
      <h2>{{ child && child.name }}的档案</h2>
      <div class="header-actions">
        <el-button @click="goBack">返回列表</el-button>
        <el-button type="primary" @click="editChild">编辑档案</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 基本信息卡片 -->
      <el-col :span="8">
        <el-card class="info-card">
          <div class="card-header">
            <h3>基本信息</h3>
          </div>
          <div class="child-avatar">
            <el-avatar :size="80" :style="{ backgroundColor: getAvatarColor(child && child.gender) }">
              {{ (child && child.name && child.name.charAt(0)) || '儿' }}
            </el-avatar>
          </div>
          <div class="basic-info">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ child && child.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ (child && child.gender) === 'MALE' ? '男' : '女' }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{ calculateAge(child && child.birthDate) }}岁</span>
            </div>
            <div class="info-item">
              <span class="label">出生日期：</span>
              <span class="value">{{ (child && child.birthDate) | formatDate }}</span>
            </div>
            <div class="info-item" v-if="child && child.height">
              <span class="label">身高：</span>
              <span class="value">{{ child && child.height }}cm</span>
            </div>
            <div class="info-item" v-if="child && child.weight">
              <span class="label">体重：</span>
              <span class="value">{{ child && child.weight }}kg</span>
            </div>
            <div class="info-item" v-if="child && child.bloodType">
              <span class="label">血型：</span>
              <span class="value">{{ child && child.bloodType }}型</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- ASD特质卡片 -->
      <el-col :span="8">
        <el-card class="info-card">
          <div class="card-header">
            <h3>ASD特质</h3>
            <el-button size="mini" @click="editASDProfile">编辑</el-button>
          </div>
          <div class="asd-info" v-if="asdProfile">
            <div class="info-item" v-if="asdProfile.sensorySensitivity">
              <span class="label">感官敏感度：</span>
              <el-tag :type="getSensitivityType(asdProfile.sensorySensitivity)" size="small">
                {{ getSensitivityText(asdProfile.sensorySensitivity) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="asdProfile.allergicFoods">
              <span class="label">过敏食物：</span>
              <span class="value">{{ asdProfile.allergicFoods }}</span>
            </div>
            <div class="info-item" v-if="asdProfile.intolerantFoods">
              <span class="label">不耐受食物：</span>
              <span class="value">{{ asdProfile.intolerantFoods }}</span>
            </div>
            <div class="info-item" v-if="asdProfile.colorPreference">
              <span class="label">颜色偏好：</span>
              <el-tag :type="getPreferenceType(asdProfile.colorPreference)" size="small">
                {{ getPreferenceText(asdProfile.colorPreference) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="asdProfile.texturePreference">
              <span class="label">质地偏好：</span>
              <el-tag :type="getPreferenceType(asdProfile.texturePreference)" size="small">
                {{ getPreferenceText(asdProfile.texturePreference) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="asdProfile.smellPreference">
              <span class="label">气味偏好：</span>
              <el-tag :type="getPreferenceType(asdProfile.smellPreference)" size="small">
                {{ getPreferenceText(asdProfile.smellPreference) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="asdProfile.behaviorPatterns">
              <span class="label">行为模式：</span>
              <span class="value">{{ asdProfile.behaviorPatterns }}</span>
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty description="暂无ASD特质信息" />
          </div>
        </el-card>
      </el-col>

      <!-- 饮食限制卡片 -->
      <el-col :span="8">
        <el-card class="info-card">
          <div class="card-header">
            <h3>饮食限制</h3>
            <el-button size="mini" @click="editDietaryRestrictions">编辑</el-button>
          </div>
          <div class="dietary-info" v-if="child && child.dietaryRestrictions && child.dietaryRestrictions.length">
            <div class="restrictions-list">
              <el-tag
                v-for="(restriction, index) in child.dietaryRestrictions"
                :key="index"
                size="small"
                type="warning"
                style="margin: 2px;"
              >
                {{ restriction }}
              </el-tag>
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty description="暂无饮食限制信息" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-card class="actions-card">
      <div class="card-header">
        <h3>快速操作</h3>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button type="primary" icon="el-icon-document" @click="goToDietaryRecords" class="action-button">
            饮食记录
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" icon="el-icon-s-data" @click="goToNutritionReports" class="action-button">
            营养报告
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" icon="el-icon-dish" @click="goToRecipeRecommendations" class="action-button">
            食谱推荐
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" icon="el-icon-message" @click="goToNutritionistNotes" class="action-button">
            营养师留言
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最近活动 -->
    <el-card class="activities-card">
      <div class="card-header">
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

    <!-- 饮食限制编辑对话框 -->
    <el-dialog
      title="编辑饮食限制"
      :visible.sync="dietaryDialogVisible"
      width="700px"
      @close="resetDietaryForm"
    >
      <el-form :model="dietaryForm" ref="dietaryForm" label-width="100px">
        <el-form-item label="限制类型" prop="restrictionType">
          <el-select v-model="dietaryForm.restrictionType" placeholder="请选择限制类型">
            <el-option label="过敏" value="ALLERGY" />
            <el-option label="不耐受" value="INTOLERANCE" />
            <el-option label="厌恶" value="AVERSION" />
            <el-option label="宗教" value="RELIGIOUS" />
            <el-option label="医疗" value="MEDICAL" />
            <el-option label="偏好" value="PREFERENCE" />
          </el-select>
        </el-form-item>

        <el-form-item label="食物名称" prop="foodName">
          <el-input v-model="dietaryForm.foodName" placeholder="请输入食物名称" />
        </el-form-item>

        <el-form-item label="严重程度" prop="severityLevel">
          <el-rate v-model="dietaryForm.severityLevel" :max="5" show-text />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="dietaryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dietaryDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addDietaryRestriction">添 加</el-button>
      </div>

      <!-- 已有的饮食限制列表 -->
      <el-divider content-position="left">已有限制</el-divider>
      <el-table :data="dietaryRestrictions" style="width: 100%">
        <el-table-column prop="restrictionType" label="类型" width="100">
          <template slot-scope="scope">
            {{ getRestrictionTypeName(scope.row.restrictionType) }}
          </template>
        </el-table-column>
        <el-table-column prop="foodName" label="食物名称" width="150" />
        <el-table-column prop="severityLevel" label="严重程度" width="120">
          <template slot-scope="scope">
            <el-rate v-model="scope.row.severityLevel" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="removeDietaryRestriction(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {mapActions, mapGetters} from 'vuex'
import moment from 'moment'

export default {
  name: 'ChildDetail',
  data() {
    return {
      child: null,
      asdProfile: null,
      dietaryDialogVisible: false,
      dietaryRestrictions: [],
      dietaryForm: {
        restrictionType: '',
        foodName: '',
        severityLevel: 3,
        description: ''
      },
      recentActivities: [
        {
          content: '添加了新的饮食记录',
          timestamp: '2小时前',
          color: '#409EFF'
        },
        {
          content: '营养师提供了新的建议',
          timestamp: '昨天',
          color: '#67C23A'
        },
        {
          content: '更新了ASD特质档案',
          timestamp: '3天前',
          color: '#E6A23C'
        }
      ]
    }
  },
  computed: {
    ...mapGetters('child', ['currentChild']),
    childId() {
      return this.$route.params.id
    }
  },
  created() {
    this.loadChildData()
  },
  methods: {
    ...mapActions('child', ['fetchChildById', 'fetchASDProfiles', 'fetchDietaryRestrictions', 'addDietaryRestriction', 'removeDietaryRestriction']),

    async loadChildData() {
      try {
        // 获取儿童基本信息
        const childData = await this.fetchChildById(this.childId)
        this.child = childData

        // 获取ASD特质信息
        const asdProfiles = await this.fetchASDProfiles(this.childId)
        this.asdProfile = asdProfiles.length > 0 ? asdProfiles[0] : null

        // 获取饮食限制
        await this.loadDietaryRestrictions()
      } catch (error) {
        this.$message.error('获取儿童信息失败')
      }
    },

    async loadDietaryRestrictions() {
      try {
        const restrictions = await this.fetchDietaryRestrictions(this.childId)
        this.dietaryRestrictions = restrictions
      } catch (error) {
        console.error('获取饮食限制失败:', error)
      }
    },

    calculateAge(birthDate) {
      if (!birthDate) return 0
      return moment().diff(moment(birthDate), 'years')
    },

    getAvatarColor(gender) {
      return gender === 'MALE' ? '#409EFF' : '#F56C6C'
    },

    getSensitivityType(level) {
      const types = {
        'LOW': 'success',
        'MILD': 'info',
        'MODERATE': 'warning',
        'HIGH': 'danger',
        'SEVERE': 'danger'
      }
      return types[level] || 'info'
    },

    getSensitivityText(level) {
      const texts = {
        'LOW': '不敏感',
        'MILD': '轻度敏感',
        'MODERATE': '中度敏感',
        'HIGH': '高度敏感',
        'SEVERE': '极度敏感'
      }
      return texts[level] || level
    },

    getPreferenceType(preference) {
      const types = {
        'LIKE': 'success',
        'DISLIKE': 'danger',
        'NEUTRAL': 'info',
        'SENSITIVE': 'warning',
        'AVERSIVE': 'danger'
      }
      return types[preference] || 'info'
    },

    getPreferenceText(preference) {
      const texts = {
        'LIKE': '喜欢',
        'DISLIKE': '不喜欢',
        'NEUTRAL': '中性',
        'SENSITIVE': '敏感',
        'AVERSIVE': '厌恶'
      }
      return texts[preference] || preference
    },

    getASDLevelType(level) {
      const types = {
        'LEVEL_1': 'success',
        'LEVEL_2': 'warning',
        'LEVEL_3': 'danger'
      }
      return types[level] || 'info'
    },

    getASDLevelText(level) {
      const texts = {
        'LEVEL_1': '轻度',
        'LEVEL_2': '中度',
        'LEVEL_3': '重度'
      }
      return texts[level] || level
    },

    goBack() {
      this.$router.push('/children')
    },

    editChild() {
      // 跳转到编辑页面
      this.$message.info('编辑功能开发中...')
    },

    editASDProfile() {
      this.$router.push(`/children/${this.childId}/asd-profile`)
    },

    editDietaryRestrictions() {
      this.$message.info('编辑功能开发中...')
    },

    goToDietaryRecords() {
      this.$router.push(`/children/${this.childId}/dietary-records`)
    },

    goToNutritionReports() {
      this.$router.push('/reports')
    },

    goToRecipeRecommendations() {
      this.$router.push('/recipes')
    },

    goToNutritionistNotes() {
      this.$router.push(`/children/${this.childId}/nutritionist-notes`)
    },

    editDietaryRestrictions() {
      this.dietaryDialogVisible = true
    },

    resetDietaryForm() {
      this.dietaryForm = {
        restrictionType: '',
        foodName: '',
        severityLevel: 3,
        description: ''
      }
    },

    async addDietaryRestriction() {
      if (!this.dietaryForm.restrictionType || !this.dietaryForm.foodName) {
        this.$message.warning('请填写限制类型和食物名称')
        return
      }

      try {
        await this.addDietaryRestriction({
          childId: this.childId,
          data: this.dietaryForm
        })
        this.$message.success('添加成功')

        // 清空表单
        this.resetDietaryForm()

        // 重新加载饮食限制
        await this.loadDietaryRestrictions()
      } catch (error) {
        this.$message.error('添加失败: ' + error.message)
      }
    },

    async removeDietaryRestriction(id) {
      this.$confirm('确定要删除这条饮食限制吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.removeDietaryRestriction({
            childId: this.childId,
            restrictionId: id
          })
          this.$message.success('删除成功')

          // 重新加载饮食限制
          await this.loadDietaryRestrictions()
        } catch (error) {
          this.$message.error('删除失败: ' + error.message)
        }
      }).catch(() => {})
    },

    getRestrictionTypeName(type) {
      const types = {
        'ALLERGY': '过敏',
        'INTOLERANCE': '不耐受',
        'AVERSION': '厌恶',
        'RELIGIOUS': '宗教',
        'MEDICAL': '医疗',
        'PREFERENCE': '偏好'
      }
      return types[type] || type
    }
  }
}
</script>

<style scoped>
.child-detail {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.child-avatar {
  text-align: center;
  margin-bottom: 20px;
}

.basic-info, .asd-info, .dietary-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-start;
}

.label {
  width: 80px;
  color: #909399;
  font-size: 14px;
  flex-shrink: 0;
}

.value {
  flex: 1;
  color: #303133;
  font-size: 14px;
  word-break: break-all;
}

.symptoms-list, .restrictions-list {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.no-data {
  text-align: center;
  padding: 40px 0;
}

.actions-card {
  margin-bottom: 20px;
}

.action-button {
  width: 100%;
  height: 50px;
  font-size: 14px;
}

.activities-card {
  margin-bottom: 20px;
}

/* ASD特质卡片优化 */
.asd-info {
  padding: 10px 0;
}

.asd-info .info-item {
  align-items: center;
}

.asd-info .el-tag {
  flex-shrink: 0;
}

/* 饮食限制卡片优化 */
.dietary-info .restrictions-list .el-tag {
  margin: 2px;
}

@media (max-width: 768px) {
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }

  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
    margin-bottom: 20px;
  }
}
</style>

