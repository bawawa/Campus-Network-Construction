<template>
  <div class="dietary-records">
    <div class="page-header">
      <h2>饮食记录管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="showAddDialog">
        添加记录
      </el-button>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="日期">
          <el-date-picker
            v-model="filterForm.date"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="餐别">
          <el-select v-model="filterForm.mealType" placeholder="全部" clearable @change="loadDietaryRecords">
            <el-option label="早餐" value="BREAKFAST" />
            <el-option label="午餐" value="LUNCH" />
            <el-option label="晚餐" value="DINNER" />
            <el-option label="加餐" value="SNACK" />
            <el-option label="夜宵" value="SUPPER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadDietaryRecords">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 饮食记录列表 -->
    <el-card>
      <div v-if="dietaryRecords.length === 0 && !loading" class="empty-container">
        <el-empty description="暂无饮食记录">
          <el-button type="primary" @click="showAddDialog">添加第一条记录</el-button>
        </el-empty>
      </div>

      <div v-else>
        <div v-for="record in dietaryRecords" :key="record.id" class="record-item">
          <el-card class="record-card" shadow="hover">
            <div class="record-header">
              <div class="record-info">
                <el-tag :type="getMealTypeColor(record.mealType)" size="small">
                  {{ getMealTypeText(record.mealType) }}
                </el-tag>
                <span class="record-date">{{ formatDate(record.recordDate) }}</span>
                <span class="record-time">{{ formatTime(record.recordTime) }}</span>
              </div>
              <div class="record-actions">
                <el-button
                  type="text"
                  icon="el-icon-edit"
                  size="small"
                  @click="editRecord(record)"
                >
                  编辑
                </el-button>
                <el-button
                  type="text"
                  icon="el-icon-delete"
                  size="small"
                  class="delete-btn"
                  @click="deleteRecord(record)"
                >
                  删除
                </el-button>
              </div>
            </div>

            <div class="record-content">
              <div class="food-info">
                <div class="food-name">{{ (record.foodItem && record.foodItem.name) || '未知食物' }}</div>
                <div class="food-details">
                  <span class="quantity">数量: {{ record.quantity }} {{ record.unit || '克' }}</span>
                  <span class="cooking-method" v-if="record.cookingMethod">
                    | 烹饪方式: {{ getCookingMethodText(record.cookingMethod) }}
                  </span>
                </div>
              </div>

              <div class="eating-info" v-if="record.eatingMood || record.behaviorNotes">
                <el-tag v-if="record.eatingMood" size="mini" :type="getEatingMoodType(record.eatingMood)">
                  进餐心情: {{ getEatingMoodText(record.eatingMood) }}
                </el-tag>
                <div v-if="record.behaviorNotes" class="behavior-notes">
                  <i class="el-icon-notebook-2"></i>
                  {{ record.behaviorNotes }}
                </div>
              </div>

              <div v-if="record.notes" class="notes">
                <i class="el-icon-edit-outline"></i>
                {{ record.notes }}
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑记录对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="recordForm" :rules="formRules" ref="recordForm" label-width="100px">
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="recordForm.recordDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="记录时间" prop="recordTime">
          <el-time-picker
            v-model="recordForm.recordTime"
            placeholder="选择时间"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="餐别" prop="mealType">
          <el-select v-model="recordForm.mealType" placeholder="选择餐别" style="width: 100%">
            <el-option label="早餐" value="BREAKFAST" />
            <el-option label="午餐" value="LUNCH" />
            <el-option label="晚餐" value="DINNER" />
            <el-option label="加餐" value="SNACK" />
            <el-option label="夜宵" value="SUPPER" />
          </el-select>
        </el-form-item>

        <el-form-item label="食物" prop="foodItemId">
          <el-select
            v-model="recordForm.foodItemId"
            filterable
            placeholder="搜索或选择食物"
            style="width: 100%"
            :loading="foodLoading"
            :remote-method="searchFoodItems"
            remote
          >
            <el-option
              v-for="food in foodItems"
              :key="food.id"
              :label="food.name"
              :value="food.id"
            >
              <div class="food-option">
                <span>{{ food.name }}</span>
                <span class="food-category">{{ getCategoryText(food.category) }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="数量" prop="quantity">
          <el-col :span="12">
            <el-input-number
              v-model="recordForm.quantity"
              :min="0"
              :max="1000"
              :precision="2"
              :step="10"
              style="width: 100%"
            />
          </el-col>
          <el-col :span="12" style="padding-left: 10px">
            <el-input v-model="recordForm.unit" placeholder="单位（克/毫升/份）" />
          </el-col>
        </el-form-item>

        <el-form-item label="烹饪方式" prop="cookingMethod">
          <el-select v-model="recordForm.cookingMethod" placeholder="选择烹饪方式" clearable style="width: 100%">
            <el-option label="生食" value="RAW" />
            <el-option label="煮" value="BOILED" />
            <el-option label="蒸" value="STEAMED" />
            <el-option label="炸" value="FRIED" />
            <el-option label="烤" value="GRILLED" />
            <el-option label="烘" value="BAKED" />
            <el-option label="炒" value="STIR_FRIED" />
            <el-option label="汤" value="SOUP" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>

        <el-form-item label="进餐心情" prop="eatingMood">
          <el-select v-model="recordForm.eatingMood" placeholder="选择进餐心情" clearable style="width: 100%">
            <el-option label="愿意" value="WILLING" />
            <el-option label="勉强" value="RELUCTANT" />
            <el-option label="抗拒" value="RESISTANT" />
            <el-option label="兴奋" value="EXCITED" />
            <el-option label="平静" value="NEUTRAL" />
            <el-option label="挑剔" value="PICKY" />
          </el-select>
        </el-form-item>

        <el-form-item label="行为观察" prop="behaviorNotes">
          <el-input
            v-model="recordForm.behaviorNotes"
            type="textarea"
            :rows="3"
            placeholder="记录进餐过程中的特殊行为或反应"
          />
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="recordForm.notes"
            type="textarea"
            :rows="2"
            placeholder="其他备注信息"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord" :loading="submitLoading">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import moment from 'moment'
import {
  deleteDietaryRecord as deleteDietaryRecordApi,
  getDietaryRecordsByDate,
  getDietaryRecordsByMealType,
  getFoodsByCategory,
  getRecentDietaryRecords,
  searchFoodItems as searchFoodItemsApi,
  updateDietaryRecord as updateDietaryRecordApi
} from '@/api/dietary'

export default {
  name: 'DietaryRecords',
  data() {
    return {
      childId: null,
      childName: '',
      dietaryRecords: [],
      foodItems: [],
      loading: false,
      foodLoading: false,
      submitLoading: false,
      dialogVisible: false,
      isEdit: false,
      currentRecordId: null,
      filterForm: {
        date: '',
        mealType: ''
      },
      recordForm: {
        recordDate: '',
        recordTime: '',
        mealType: '',
        foodItemId: null,
        quantity: 100,
        unit: '克',
        cookingMethod: '',
        eatingMood: '',
        behaviorNotes: '',
        notes: ''
      },
      formRules: {
        recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }],
        mealType: [{ required: true, message: '请选择餐别', trigger: 'change' }],
        foodItemId: [{ required: true, message: '请选择食物', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入数量', trigger: 'change' }]
      }
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    dialogTitle() {
      return this.isEdit ? '编辑饮食记录' : '添加饮食记录'
    }
  },
  created() {
    this.childId = parseInt(this.$route.params.id)
    console.log('DietaryRecords childId:', this.childId)
    this.loadDietaryRecords()
    this.loadFoodItems()
  },
  methods: {
    async loadDietaryRecords() {
      this.loading = true
      try {
        let response

        if (this.filterForm.date) {
          // 使用特定日期的 API
          if (this.filterForm.mealType) {
            // 按日期和餐别查询
            response = await getDietaryRecordsByMealType(
              this.childId,
              this.filterForm.date,
              this.filterForm.mealType
            )
          } else {
            // 按日期查询
            response = await getDietaryRecordsByDate(this.childId, this.filterForm.date)
          }
        } else {
          // 获取最近记录
          response = await getRecentDietaryRecords(this.childId, 30)
        }

        // 饮食记录 API 返回的是数组
        const data = Array.isArray(response) ? response : []
        this.dietaryRecords = data
        console.log('饮食记录数据:', data)
      } catch (error) {
        console.error('获取饮食记录失败:', error)
        this.$message.error('获取饮食记录失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },

    async loadFoodItems() {
      this.foodLoading = true
      try {
        // 获取一个类别的食物作为默认列表
        const response = await getFoodsByCategory('GRAIN')
        // 食物 API 返回的是包装对象 { success, data, ... }
        this.foodItems = (response && response.data) || []
        console.log('食物列表:', this.foodItems)
      } catch (error) {
        console.error('获取食物列表失败:', error)
      } finally {
        this.foodLoading = false
      }
    },

    async searchFoodItems(keyword) {
      if (!keyword) {
        this.loadFoodItems()
        return
      }
      this.foodLoading = true
      try {
        const response = await searchFoodItemsApi(keyword)
        // 食物 API 返回的是包装对象 { success, data, ... }
        this.foodItems = (response && response.data) || []
      } catch (error) {
        console.error('搜索食物失败:', error)
      } finally {
        this.foodLoading = false
      }
    },

    showAddDialog() {
      this.isEdit = false
      this.currentRecordId = null
      this.recordForm = {
        recordDate: moment().format('YYYY-MM-DD'),
        recordTime: moment().format('HH:mm'),
        mealType: '',
        foodItemId: null,
        quantity: 100,
        unit: '克',
        cookingMethod: '',
        eatingMood: '',
        behaviorNotes: '',
        notes: ''
      }
      this.dialogVisible = true
    },

    editRecord(record) {
      this.isEdit = true
      this.currentRecordId = record.id
      this.recordForm = {
        recordDate: record.recordDate,
        recordTime: record.recordTime ? record.recordTime.substring(0, 5) : '',
        mealType: record.mealType,
        foodItemId: (record.foodItem && record.foodItem.id) || null,
        quantity: record.quantity,
        unit: record.unit || '克',
        cookingMethod: record.cookingMethod || '',
        eatingMood: record.eatingMood || '',
        behaviorNotes: record.behaviorNotes || '',
        notes: record.notes || ''
      }
      this.dialogVisible = true
    },

    async saveRecord() {
      this.$refs.recordForm.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const data = {
            recordDate: this.recordForm.recordDate,
            recordTime: this.recordForm.recordTime ? `${this.recordForm.recordTime}:00` : null,
            mealType: this.recordForm.mealType,
            foodItemId: this.recordForm.foodItemId,
            quantity: this.recordForm.quantity,
            unit: this.recordForm.unit,
            cookingMethod: this.recordForm.cookingMethod || null,
            eatingMood: this.recordForm.eatingMood || null,
            behaviorNotes: this.recordForm.behaviorNotes,
            notes: this.recordForm.notes
          }

          if (this.isEdit) {
            await updateDietaryRecordApi(this.childId, this.currentRecordId, data)
            this.$message.success('更新成功')
          } else {
            // 创建记录需要调用不同的 API
            await this.createDietaryRecord(data)
            this.$message.success('添加成功')
          }

          this.dialogVisible = false
          this.loadDietaryRecords()
        } catch (error) {
          console.error('保存饮食记录失败:', error)
          this.$message.error('保存失败: ' + (error.message || '未知错误'))
        } finally {
          this.submitLoading = false
        }
      })
    },

    async createDietaryRecord(data) {
      // 创建饮食记录 - 直接使用 axios
      const axios = require('@/utils/axios').default
      const response = await axios({
        url: `/children/${this.childId}/dietary-records`,
        method: 'post',
        data
      })
      return response
    },

    async deleteRecord(record) {
      this.$confirm('确认删除这条饮食记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteDietaryRecordApi(this.childId, record.id)
          this.$message.success('删除成功')
          this.loadDietaryRecords()
        } catch (error) {
          console.error('删除饮食记录失败:', error)
          this.$message.error('删除失败: ' + (error.message || '未知错误'))
        }
      }).catch(() => {})
    },

    resetFilter() {
      this.filterForm = {
        date: '',
        mealType: ''
      }
      this.loadDietaryRecords()
    },

    handleDateChange() {
      this.loadDietaryRecords()
    },

    formatDate(date) {
      if (!date) return ''
      return moment(date).format('YYYY年MM月DD日')
    },

    formatTime(time) {
      if (!time) return ''
      return time.substring(0, 5)
    },

    getMealTypeText(type) {
      const map = {
        'BREAKFAST': '早餐',
        'LUNCH': '午餐',
        'DINNER': '晚餐',
        'SNACK': '加餐',
        'SUPPER': '夜宵'
      }
      return map[type] || type
    },

    getMealTypeColor(type) {
      const map = {
        'BREAKFAST': 'warning',
        'LUNCH': 'primary',
        'DINNER': 'danger',
        'SNACK': 'info',
        'SUPPER': ''
      }
      return map[type] || ''
    },

    getCookingMethodText(method) {
      const map = {
        'RAW': '生食',
        'BOILED': '煮',
        'STEAMED': '蒸',
        'FRIED': '炸',
        'GRILLED': '烤',
        'BAKED': '烘',
        'STIR_FRIED': '炒',
        'SOUP': '汤',
        'OTHER': '其他'
      }
      return map[method] || method
    },

    getEatingMoodText(mood) {
      const map = {
        'WILLING': '愿意',
        'RELUCTANT': '勉强',
        'RESISTANT': '抗拒',
        'EXCITED': '兴奋',
        'NEUTRAL': '平静',
        'PICKY': '挑剔'
      }
      return map[mood] || mood
    },

    getEatingMoodType(mood) {
      const map = {
        'WILLING': 'success',
        'RELUCTANT': 'warning',
        'RESISTANT': 'danger',
        'EXCITED': 'primary',
        'NEUTRAL': 'info',
        'PICKY': ''
      }
      return map[mood] || ''
    },

    getCategoryText(category) {
      const map = {
        'GRAIN': '谷物',
        'MEAT': '肉类',
        'DAIRY': '乳制品',
        'VEGETABLE': '蔬菜',
        'FRUIT': '水果',
        'SNACK': '零食',
        'BEVERAGE': '饮品',
        'SEAFOOD': '海鲜',
        'LEGUME': '豆类',
        'NUT': '坚果',
        'OTHER': '其他'
      }
      return map[category] || category
    },

    goBack() {
      this.$router.go(-1)
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

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.record-item {
  margin-bottom: 15px;
}

.record-card {
  transition: all 0.3s;
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.record-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.record-date {
  color: #606266;
  font-size: 14px;
}

.record-time {
  color: #909399;
  font-size: 13px;
}

.record-actions {
  display: flex;
  gap: 8px;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  background-color: #fef0f0;
}

.record-content {
  padding: 10px 0;
}

.food-info {
  margin-bottom: 12px;
}

.food-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
}

.food-details {
  font-size: 14px;
  color: #606266;
}

.quantity {
  margin-right: 15px;
}

.cooking-method {
  color: #909399;
}

.eating-info {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.behavior-notes {
  font-size: 14px;
  color: #606266;
  background-color: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  flex: 1;
}

.notes {
  font-size: 14px;
  color: #606266;
  background-color: #f5f7fa;
  padding: 8px 12px;
  border-radius: 4px;
}

.notes i {
  margin-right: 5px;
  color: #909399;
}

.empty-container {
  padding: 60px 0;
}

.food-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.food-category {
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .filter-form {
    flex-direction: column;
  }

  .filter-form .el-form-item {
    margin-bottom: 10px;
    width: 100%;
  }

  .record-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>

