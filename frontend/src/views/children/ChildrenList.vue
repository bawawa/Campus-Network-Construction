<template>
  <div class="children-list">
    <div class="page-header">
      <h2>儿童管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="goToAddChild">
        添加儿童
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="姓名">
          <el-input v-model="filterForm.name" placeholder="请输入儿童姓名" clearable />
        </el-form-item>
        <el-form-item label="年龄">
          <el-select v-model="filterForm.ageRange" placeholder="请选择年龄段" clearable>
            <el-option label="0-3岁" value="0-3" />
            <el-option label="4-6岁" value="4-6" />
            <el-option label="7-12岁" value="7-12" />
            <el-option label="13-18岁" value="13-18" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="filterForm.gender" placeholder="请选择性别" clearable>
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 儿童列表 -->
    <el-card>
      <el-table
        :data="childrenList"
        style="width: 100%"
        v-loading="loading"
        @row-click="handleRowClick"
      >
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="name" label="姓名" width="120">
          <template slot-scope="scope">
            <div class="child-name">
              <el-avatar size="small" :style="{ backgroundColor: getAvatarColor(scope.row.gender) }">
                {{ scope.row.name.charAt(0) }}
              </el-avatar>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.gender === 'MALE' ? 'primary' : 'danger'" size="small">
              {{ scope.row.gender === 'MALE' ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="age" label="年龄" width="80">
          <template slot-scope="scope">
            {{ calculateAge(scope.row.birthDate) }}岁
          </template>
        </el-table-column>

        <el-table-column prop="birthDate" label="出生日期" width="120">
          <template slot-scope="scope">
            {{ scope.row.birthDate | formatDate }}
          </template>
        </el-table-column>

        <el-table-column prop="asdLevel" label="ASD等级" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="getASDLevelType(scope.row.asdLevel)"
              size="small"
              v-if="scope.row.asdLevel"
            >
              {{ getASDLevelText(scope.row.asdLevel) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="dietaryRestrictions" label="饮食限制" min-width="150">
          <template slot-scope="scope">
            <div v-if="scope.row.dietaryRestrictions && scope.row.dietaryRestrictions.length > 0">
              <el-tag
                v-for="(restriction, index) in scope.row.dietaryRestrictions.slice(0, 2)"
                :key="index"
                size="small"
                type="warning"
                style="margin-right: 5px; margin-bottom: 2px;"
              >
                {{ restriction }}
              </el-tag>
              <el-tag
                v-if="scope.row.dietaryRestrictions.length > 2"
                size="small"
                type="info"
              >
                +{{ scope.row.dietaryRestrictions.length - 2 }}
              </el-tag>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="lastRecordDate" label="最后记录" width="120">
          <template slot-scope="scope">
            {{ scope.row.lastRecordDate | formatDate }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click.stop="viewChildDetail(scope.row)"
            >
              查看详情
            </el-button>
            <el-button
              size="mini"
              type="success"
              @click.stop="viewDietaryRecords(scope.row)"
            >
              饮食记录
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import moment from 'moment'
import {getChildrenByParent} from '@/api/child'

export default {
  name: 'ChildrenList',
  data() {
    return {
      filterForm: {
        name: '',
        ageRange: '',
        gender: ''
      },
      childrenList: [],
      loading: false,
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  computed: {
    ...mapGetters('user', ['userId'])
  },
  created() {
    console.log('=== ChildrenList created ===')
    console.log('当前 userId:', this.userId)
    this.loadChildrenList()
  },
  methods: {
    async loadChildrenList() {
      console.log('=== loadChildrenList 开始 ===')
      console.log('userId:', this.userId)
      console.log('localStorage userInfo:', localStorage.getItem('userInfo'))

      if (!this.userId) {
        console.warn('userId 为空，提示登录')
        this.$message.warning('请先登录')
        return
      }

      this.loading = true
      try {
        const url = `/api/children/parent/${this.userId}`
        console.log('请求 URL:', url)
        const response = await getChildrenByParent(this.userId)
        console.log('完整响应:', response)
        console.log('Response 类型:', typeof response)
        console.log('Response 是否为数组:', Array.isArray(response))
        // axios拦截器已经返回了 response.data，所以 response 直接就是数据
        const data = Array.isArray(response) ? response : (response.data || [])
        console.log('解析后的数据:', data)
        this.childrenList = data
        this.pagination.total = data.length
        console.log('childrenList 长度:', this.childrenList.length)
      } catch (error) {
        console.error('获取儿童列表失败:', error)
        console.error('错误详情:', error.response)
        this.$message.error('获取儿童列表失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.pagination.currentPage = 1
      this.loadChildrenList()
    },

    resetFilter() {
      this.filterForm = {
        name: '',
        ageRange: '',
        gender: ''
      }
      this.handleSearch()
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadChildrenList()
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadChildrenList()
    },

    handleRowClick(row) {
      this.viewChildDetail(row)
    },

    viewChildDetail(child) {
      this.$router.push(`/children/${child.id}`)
    },

    viewDietaryRecords(child) {
      this.$router.push(`/children/${child.id}/dietary-records`)
    },

    goToAddChild() {
      this.$router.push('/children/add')
    },

    calculateAge(birthDate) {
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
    }
  }
}
</script>

<style scoped>
.children-list {
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

.child-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

@media (max-width: 768px) {
  .filter-form {
    flex-direction: column;
  }

  .filter-form .el-form-item {
    margin-bottom: 10px;
  }
}
</style>

