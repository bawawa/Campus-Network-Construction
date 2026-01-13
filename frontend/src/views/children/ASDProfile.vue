<template>
  <div class="asd-profile">
    <div class="page-header">
      <h2>ASD特质档案编辑</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card>
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item label="过敏食物" prop="allergicFoods">
          <el-input
            v-model="form.allergicFoods"
            type="textarea"
            :rows="3"
            placeholder="请输入过敏食物，多个食物用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="不耐受食物" prop="intolerantFoods">
          <el-input
            v-model="form.intolerantFoods"
            type="textarea"
            :rows="3"
            placeholder="请输入不耐受食物，多个食物用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="感官敏感度" prop="sensorySensitivity">
          <el-select v-model="form.sensorySensitivity" placeholder="请选择感官敏感度">
            <el-option label="不敏感" value="LOW" />
            <el-option label="轻度敏感" value="MILD" />
            <el-option label="中度敏感" value="MODERATE" />
            <el-option label="高度敏感" value="HIGH" />
            <el-option label="极度敏感" value="SEVERE" />
          </el-select>
        </el-form-item>

        <el-form-item label="行为模式" prop="behaviorPatterns">
          <el-input
            v-model="form.behaviorPatterns"
            type="textarea"
            :rows="4"
            placeholder="请描述儿童的行为模式特征"
          />
        </el-form-item>

        <el-form-item label="颜色偏好" prop="colorPreference">
          <el-select v-model="form.colorPreference" placeholder="请选择颜色偏好">
            <el-option label="喜欢" value="LIKE" />
            <el-option label="不喜欢" value="DISLIKE" />
            <el-option label="中性" value="NEUTRAL" />
            <el-option label="敏感" value="SENSITIVE" />
            <el-option label="厌恶" value="AVERSIVE" />
          </el-select>
        </el-form-item>

        <el-form-item label="质地偏好" prop="texturePreference">
          <el-select v-model="form.texturePreference" placeholder="请选择质地偏好">
            <el-option label="喜欢" value="LIKE" />
            <el-option label="不喜欢" value="DISLIKE" />
            <el-option label="中性" value="NEUTRAL" />
            <el-option label="敏感" value="SENSITIVE" />
            <el-option label="厌恶" value="AVERSIVE" />
          </el-select>
        </el-form-item>

        <el-form-item label="气味偏好" prop="smellPreference">
          <el-select v-model="form.smellPreference" placeholder="请选择气味偏好">
            <el-option label="喜欢" value="LIKE" />
            <el-option label="不喜欢" value="DISLIKE" />
            <el-option label="中性" value="NEUTRAL" />
            <el-option label="敏感" value="SENSITIVE" />
            <el-option label="厌恶" value="AVERSIVE" />
          </el-select>
        </el-form-item>

        <el-form-item label="饮食行为备注" prop="eatingBehaviorNotes">
          <el-input
            v-model="form.eatingBehaviorNotes"
            type="textarea"
            :rows="4"
            placeholder="请描述儿童的饮食行为特征"
          />
        </el-form-item>

        <el-form-item label="特殊备注" prop="specialNotes">
          <el-input
            v-model="form.specialNotes"
            type="textarea"
            :rows="3"
            placeholder="请输入其他需要特别注意的事项"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import {mapActions} from 'vuex'

export default {
  name: 'ASDProfile',
  data() {
    return {
      loading: false,
      form: {
        allergicFoods: '',
        intolerantFoods: '',
        sensorySensitivity: '',
        behaviorPatterns: '',
        colorPreference: '',
        texturePreference: '',
        smellPreference: '',
        eatingBehaviorNotes: '',
        specialNotes: ''
      },
      rules: {
        sensorySensitivity: [
          { required: true, message: '请选择感官敏感度', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    childId() {
      return this.$route.params.id
    }
  },
  created() {
    this.loadExistingProfile()
  },
  methods: {
    ...mapActions('child', ['fetchASDProfiles', 'updateASDProfile', 'createASDProfile']),

    async loadExistingProfile() {
      try {
        const profiles = await this.fetchASDProfiles(this.childId)
        if (profiles && profiles.length > 0) {
          // 使用最新的档案
          const profile = profiles[0]
          this.form = {
            allergicFoods: profile.allergicFoods || '',
            intolerantFoods: profile.intolerantFoods || '',
            sensorySensitivity: profile.sensorySensitivity || '',
            behaviorPatterns: profile.behaviorPatterns || '',
            colorPreference: profile.colorPreference || '',
            texturePreference: profile.texturePreference || '',
            smellPreference: profile.smellPreference || '',
            eatingBehaviorNotes: profile.eatingBehaviorNotes || '',
            specialNotes: profile.specialNotes || ''
          }
        }
      } catch (error) {
        console.error('加载ASD档案失败:', error)
      }
    },

    async submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            this.loading = true

            // 过滤空字符串，转换为 null
            const submitData = {}
            Object.keys(this.form).forEach(key => {
              if (this.form[key] !== '' && this.form[key] !== null) {
                submitData[key] = this.form[key]
              }
            })

            // 检查是否已有档案
            const profiles = await this.fetchASDProfiles(this.childId)

            if (profiles && profiles.length > 0) {
              // 更新现有档案
              await this.updateASDProfile({
                childId: this.childId,
                profileId: profiles[0].id,
                data: submitData
              })
              this.$message.success('更新成功')
            } else {
              // 创建新档案
              await this.createASDProfile({
                childId: this.childId,
                data: submitData
              })
              this.$message.success('创建成功')
            }

            this.goBack()
          } catch (error) {
            this.$message.error('保存失败: ' + error.message)
          } finally {
            this.loading = false
          }
        }
      })
    },

    resetForm() {
      this.$refs.form.resetFields()
    },

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
</style>

