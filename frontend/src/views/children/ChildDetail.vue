<template>
  <div class="child-detail">
    <div class="page-header">
      <h2>{{ child?.name }}的档案</h2>
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
            <el-avatar :size="80" :style="{ backgroundColor: getAvatarColor(child?.gender) }">
              {{ child?.name?.charAt(0) || '儿' }}
            </el-avatar>
          </div>
          <div class="basic-info">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ child?.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ child?.gender === 'MALE' ? '男' : '女' }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{ calculateAge(child?.birthDate) }}岁</span>
            </div>
            <div class="info-item">
              <span class="label">出生日期：</span>
              <span class="value">{{ child?.birthDate | formatDate }}</span>
            </div>
            <div class="info-item" v-if="child?.height">
              <span class="label">身高：</span>
              <span class="value">{{ child?.height }}cm</span>
            </div>
            <div class="info-item" v-if="child?.weight">
              <span class="label">体重：</span>
              <span class="value">{{ child?.weight }}kg</span>
            </div>
            <div class="info-item" v-if="child?.bloodType">
              <span class="label">血型：</span>
              <span class="value">{{ child?.bloodType }}型</span>
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
            <div class="info-item">
              <span class="label">ASD等级：</span>
              <el-tag :type="getASDLevelType(asdProfile.asdLevel)" size="small">
                {{ getASDLevelText(asdProfile.asdLevel) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="asdProfile.diagnosisDate">
              <span class="label">诊断日期：</span>
              <span class="value">{{ asdProfile.diagnosisDate | formatDate }}</span>
            </div>
            <div class="info-item" v-if="asdProfile.diagnosisInstitution">
              <span class="label">诊断机构：</span>
              <span class="value">{{ asdProfile.diagnosisInstitution }}</span>
            </div>
            <div class="info-item" v-if="asdProfile.symptoms?.length">
              <span class="label">主要症状：</span>
              <div class="symptoms-list">
                <el-tag
                  v-for="(symptom, index) in asdProfile.symptoms"
                  :key="index"
                  size="small"
                  type="info"
                  style="margin: 2px;"
                >
                  {{ symptom }}
                </el-tag>
              </div>
            </div>
            <div class="info-item" v-if="asdProfile.specialNeeds">
              <span class="label">特殊需求：</span>
              <span class="value">{{ asdProfile.specialNeeds }}</span>
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
          <div class="dietary-info" v-if="child?.dietaryRestrictions?.length">
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
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import moment from 'moment'

export default {
  name: 'ChildDetail',
  data() {
    return {
      child: null,
      asdProfile: null,
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
    ...mapActions('child', ['fetchChildById', 'fetchASDProfiles']),

    async loadChildData() {
      try {
        // 获取儿童基本信息
        const childData = await this.fetchChildById(this.childId)
        this.child = childData

        // 获取ASD特质信息
        const asdProfiles = await this.fetchASDProfiles(this.childId)
        this.asdProfile = asdProfiles.length > 0 ? asdProfiles[0] : null
      } catch (error) {
        this.$message.error('获取儿童信息失败')
      }
    },

    calculateAge(birthDate) {
      if (!birthDate) return 0
      return moment().diff(moment(birthDate), 'years')
    },

    getAvatarColor(gender) {
      return gender === 'MALE' ? '#409EFF' : '#F56C6C'
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
      this.$message.info('营养师留言功能开发中...')
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
}

.value {
  flex: 1;
  color: #303133;
  font-size: 14px;
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

