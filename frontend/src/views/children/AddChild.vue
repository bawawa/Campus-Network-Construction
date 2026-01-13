<template>
  <div class="add-child">
    <div class="page-header">
      <h2>添加儿童档案</h2>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <el-card>
      <el-steps :active="currentStep" finish-status="success" class="steps">
        <el-step title="基本信息" />
        <el-step title="ASD特质" />
        <el-step title="饮食限制" />
        <el-step title="完成" />
      </el-steps>

      <!-- 步骤1：基本信息 -->
      <div v-show="currentStep === 0" class="step-content">
        <h3>基本信息</h3>
        <el-form :model="childForm" :rules="basicRules" ref="basicForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="childForm.name" placeholder="请输入儿童姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="childForm.gender">
                  <el-radio label="MALE">男</el-radio>
                  <el-radio label="FEMALE">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="出生日期" prop="birthDate">
                <el-date-picker
                  v-model="childForm.birthDate"
                  type="date"
                  placeholder="选择出生日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身高(cm)" prop="height">
                <el-input-number
                  v-model="childForm.height"
                  :min="0"
                  :max="200"
                  style="width: 100%"
                  placeholder="请输入身高"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="体重(kg)" prop="weight">
                <el-input-number
                  v-model="childForm.weight"
                  :min="0"
                  :max="100"
                  style="width: 100%"
                  placeholder="请输入体重"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="血型" prop="bloodType">
                <el-select v-model="childForm.bloodType" placeholder="请选择血型" style="width: 100%">
                  <el-option label="A型" value="A" />
                  <el-option label="B型" value="B" />
                  <el-option label="AB型" value="AB" />
                  <el-option label="O型" value="O" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="备注" prop="notes">
            <el-input
              v-model="childForm.notes"
              type="textarea"
              rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤2：ASD特质 -->
      <div v-show="currentStep === 1" class="step-content">
        <h3>ASD特质档案</h3>
        <el-form :model="asdForm" :rules="asdRules" ref="asdForm" label-width="120px">
          <el-form-item label="ASD等级" prop="asdLevel">
            <el-select v-model="asdForm.asdLevel" placeholder="请选择ASD等级" style="width: 100%">
              <el-option label="轻度 (Level 1)" value="LEVEL_1" />
              <el-option label="中度 (Level 2)" value="LEVEL_2" />
              <el-option label="重度 (Level 3)" value="LEVEL_3" />
            </el-select>
          </el-form-item>

          <el-form-item label="诊断日期" prop="diagnosisDate">
            <el-date-picker
              v-model="asdForm.diagnosisDate"
              type="date"
              placeholder="选择诊断日期"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="诊断机构" prop="diagnosisInstitution">
            <el-input v-model="asdForm.diagnosisInstitution" placeholder="请输入诊断机构" />
          </el-form-item>

          <el-form-item label="主要症状">
            <el-checkbox-group v-model="asdForm.symptoms">
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-checkbox label="社交障碍">社交障碍</el-checkbox>
                </el-col>
                <el-col :span="8">
                  <el-checkbox label="语言发育迟缓">语言发育迟缓</el-checkbox>
                </el-col>
                <el-col :span="8">
                  <el-checkbox label="重复行为">重复行为</el-checkbox>
                </el-col>
                <el-col :span="8">
                  <el-checkbox label="感官敏感">感官敏感</el-checkbox>
                </el-col>
                <el-col :span="8">
                  <el-checkbox label="注意力缺陷">注意力缺陷</el-checkbox>
                </el-col>
                <el-col :span="8">
                  <el-checkbox label="情绪调节困难">情绪调节困难</el-checkbox>
                </el-col>
              </el-row>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="特殊需求" prop="specialNeeds">
            <el-input
              v-model="asdForm.specialNeeds"
              type="textarea"
              rows="3"
              placeholder="请描述特殊需求或注意事项"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤3：饮食限制 -->
      <div v-show="currentStep === 2" class="step-content">
        <h3>饮食限制与偏好</h3>
        <el-form :model="dietaryForm" ref="dietaryForm" label-width="120px">
          <el-form-item label="过敏原">
            <el-checkbox-group v-model="dietaryForm.allergens">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-checkbox label="牛奶">牛奶</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="鸡蛋">鸡蛋</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="花生">花生</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="坚果">坚果</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="大豆">大豆</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="小麦">小麦</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="鱼类">鱼类</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="贝类">贝类</el-checkbox>
                </el-col>
              </el-row>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="不耐受">
            <el-checkbox-group v-model="dietaryForm.intolerances">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-checkbox label="乳糖">乳糖</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="麸质">麸质</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="果糖">果糖</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="组胺">组胺</el-checkbox>
                </el-col>
              </el-row>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="饮食偏好">
            <el-checkbox-group v-model="dietaryForm.preferences">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-checkbox label="素食">素食</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="纯素食">纯素食</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="无麸质">无麸质</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox label="低碳水">低碳水</el-checkbox>
                </el-col>
              </el-row>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="其他限制">
            <el-input
              v-model="dietaryForm.otherRestrictions"
              type="textarea"
              rows="3"
              placeholder="请描述其他饮食限制或特殊要求"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤4：完成 -->
      <div v-show="currentStep === 3" class="step-content">
        <div class="completion-content">
          <el-result
            icon="success"
            title="添加成功！"
            sub-title="儿童档案已成功创建，您可以开始记录饮食信息了。"
          >
            <template slot="extra">
              <el-button type="primary" @click="goToChildDetail">查看详情</el-button>
              <el-button @click="goToChildrenList">返回列表</el-button>
            </template>
          </el-result>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="step-actions" v-if="currentStep < 3">
        <el-button
          v-if="currentStep > 0"
          @click="prevStep"
        >
          上一步
        </el-button>
        <el-button
          type="primary"
          @click="nextStep"
          :loading="loading"
        >
          {{ currentStep === 2 ? '完成' : '下一步' }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import moment from 'moment'
import { createChild } from '@/api/child'

export default {
  name: 'AddChild',
  data() {
    return {
      currentStep: 0,
      loading: false,
      childForm: {
        name: '',
        gender: 'MALE',
        birthDate: '',
        height: null,
        weight: null,
        bloodType: '',
        notes: ''
      },
      asdForm: {
        asdLevel: '',
        diagnosisDate: '',
        diagnosisInstitution: '',
        symptoms: [],
        specialNeeds: ''
      },
      dietaryForm: {
        allergens: [],
        intolerances: [],
        preferences: [],
        otherRestrictions: ''
      },
      basicRules: {
        name: [
          { required: true, message: '请输入儿童姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birthDate: [
          { required: true, message: '请选择出生日期', trigger: 'change' }
        ]
      },
      asdRules: {
        asdLevel: [
          { required: true, message: '请选择ASD等级', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters('user', ['userId'])
  },
  methods: {
    ...mapActions('child', ['createChild']),
    nextStep() {
      if (this.currentStep === 0) {
        this.$refs.basicForm.validate((valid) => {
          if (valid) {
            this.currentStep++
          }
        })
      } else if (this.currentStep === 1) {
        this.$refs.asdForm.validate((valid) => {
          if (valid) {
            this.currentStep++
          }
        })
      } else if (this.currentStep === 2) {
        this.submitChildData()
      }
    },

    prevStep() {
      if (this.currentStep > 0) {
        this.currentStep--
      }
    },

    async submitChildData() {
      this.loading = true
      try {
        // 合并所有表单数据，只发送后端支持的字段
        const childData = {
          name: this.childForm.name,
          gender: this.childForm.gender,
          birthDate: this.childForm.birthDate ? moment(this.childForm.birthDate).format('YYYY-MM-DD') : null,
          diagnosisInfo: this.asdForm.symptoms.join(', '),
          allergyHistory: [...this.dietaryForm.allergens, ...this.dietaryForm.intolerances].join(', '),
          preferredFoods: [...this.dietaryForm.preferences, ...(this.dietaryForm.otherRestrictions ? [this.dietaryForm.otherRestrictions] : [])].join(', ')
        }

        // 调用API创建儿童档案（后端会根据当前登录用户自动设置parent）
        const response = await this.createChild(childData)

        this.currentStep = 3
        this.$message.success('儿童档案创建成功')
      } catch (error) {
        console.error('创建儿童失败:', error)
        this.$message.error('创建失败，请重试')
      } finally {
        this.loading = false
      }
    },

    goBack() {
      this.$router.push('/children')
    },

    goToChildDetail() {
      // 这里应该跳转到新创建的儿童详情页
      this.$router.push('/children')
    },

    goToChildrenList() {
      this.$router.push('/children')
    }
  }
}
</script>

<style scoped>
.add-child {
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

.steps {
  margin-bottom: 30px;
}

.step-content {
  margin: 30px 0;
}

.step-content h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
}

.step-actions {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.completion-content {
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }

  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
}
</style>

