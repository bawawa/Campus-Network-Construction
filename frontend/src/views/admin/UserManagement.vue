<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="showAddUserDialog">
        添加用户
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名或邮箱"
            prefix-icon="el-icon-search"
            @keyup.enter.native="loadUsers"
          />
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="searchForm.role"
            placeholder="选择角色"
            clearable
            @change="loadUsers"
          >
            <el-option label="全部角色" value="" />
            <el-option label="家长" value="PARENT" />
            <el-option label="营养师" value="NUTRITIONIST" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="searchForm.status"
            placeholder="用户状态"
            clearable
            @change="loadUsers"
          >
            <el-option label="全部状态" value="" />
            <el-option label="活跃" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </el-col>
        <el-col :span="10">
          <el-button type="primary" @click="loadUsers">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button @click="resetSearch">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
          <el-button type="success" @click="exportUsers">
            <i class="el-icon-download"></i> 导出
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column label="角色" width="100">
          <template slot-scope="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isActive"
              @change="toggleUserStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="editUser(scope.row)">
              编辑
            </el-button>
            <el-button size="mini" type="warning" @click="resetPassword(scope.row)">
              重置密码
            </el-button>
            <el-button size="mini" type="danger" @click="deleteUser(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          :current-page="pagination.page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :title="userDialog.title"
      :visible.sync="userDialog.visible"
      width="600px"
      @close="resetUserForm"
    >
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" type="email" placeholder="请输入邮箱" :disabled="!!userForm.id" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword" v-if="!userForm.id">
          <el-input v-model="userForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="家长" value="PARENT" />
            <el-option label="营养师" value="NUTRITIONIST" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>

        <el-form-item label="关系" prop="relationshipType" v-if="userForm.role === 'PARENT'">
          <el-select v-model="userForm.relationshipType" placeholder="请选择关系" style="width: 100%">
            <el-option label="母亲" value="MOTHER" />
            <el-option label="父亲" value="FATHER" />
            <el-option label="监护人" value="GUARDIAN" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-switch v-model="userForm.isActive" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="userDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="userDialog.loading">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import moment from 'moment'

export default {
  name: 'UserManagement',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value && value !== this.userForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      searchForm: {
        keyword: '',
        role: '',
        status: ''
      },
      users: [],
      pagination: {
        page: 1,
        size: 20,
        total: 0
      },
      userDialog: {
        visible: false,
        title: '添加用户',
        loading: false
      },
      userForm: {
        id: null,
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        role: '',
        relationshipType: '',
        isActive: true
      },
      userRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        // 这里应该调用API获取用户列表
        // 暂时使用模拟数据
        const mockUsers = [
          {
            id: 1,
            name: '张三',
            email: 'zhangsan@example.com',
            phone: '13800138000',
            role: 'PARENT',
            relationshipType: 'FATHER',
            isActive: true,
            createdAt: '2026-01-01T10:00:00'
          },
          {
            id: 2,
            name: '李四',
            email: 'lisi@example.com',
            phone: '13800138001',
            role: 'NUTRITIONIST',
            isActive: true,
            createdAt: '2026-01-02T10:00:00'
          },
          {
            id: 3,
            name: '王五',
            email: 'wangwu@example.com',
            phone: '13800138002',
            role: 'ADMIN',
            isActive: true,
            createdAt: '2026-01-03T10:00:00'
          },
          {
            id: 4,
            name: '赵六',
            email: 'zhaoliu@example.com',
            phone: '13800138003',
            role: 'PARENT',
            relationshipType: 'MOTHER',
            isActive: false,
            createdAt: '2026-01-04T10:00:00'
          }
        ]
        this.users = mockUsers
        this.pagination.total = mockUsers.length
      } catch (error) {
        this.$message.error('加载用户列表失败')
      } finally {
        this.loading = false
      }
    },

    resetSearch() {
      this.searchForm = {
        keyword: '',
        role: '',
        status: ''
      }
      this.loadUsers()
    },

    showAddUserDialog() {
      this.userDialog.title = '添加用户'
      this.userDialog.visible = true
    },

    editUser(user) {
      this.userDialog.title = '编辑用户'
      this.userForm = {
        id: user.id,
        name: user.name,
        email: user.email,
        phone: user.phone,
        role: user.role,
        relationshipType: user.relationshipType,
        isActive: user.isActive
      }
      this.userDialog.visible = true
    },

    async saveUser() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.userDialog.loading = true
          try {
            // 这里应该调用API保存用户
            await new Promise(resolve => setTimeout(resolve, 1000))
            this.$message.success(this.userForm.id ? '更新成功' : '添加成功')
            this.userDialog.visible = false
            this.loadUsers()
          } catch (error) {
            this.$message.error('操作失败')
          } finally {
            this.userDialog.loading = false
          }
        }
      })
    },

    resetUserForm() {
      this.$refs.userForm && this.$refs.userForm.resetFields()
      this.userForm = {
        id: null,
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        role: '',
        relationshipType: '',
        isActive: true
      }
    },

    async toggleUserStatus(user) {
      try {
        // 这里应该调用API更新用户状态
        await new Promise(resolve => setTimeout(resolve, 500))
        this.$message.success('用户状态已更新')
      } catch (error) {
        user.isActive = !user.isActive
        this.$message.error('更新状态失败')
      }
    },

    async resetPassword(user) {
      this.$prompt('请输入新密码', '重置密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{6,}$/,
        inputErrorMessage: '密码长度不能少于6位'
      }).then(async ({ value }) => {
        try {
          // 这里应该调用API重置密码
          await new Promise(resolve => setTimeout(resolve, 1000))
          this.$message.success('密码重置成功')
        } catch (error) {
          this.$message.error('重置密码失败')
        }
      })
    },

    async deleteUser(user) {
      this.$confirm(`确定要删除用户 "${user.name}" 吗？此操作不可恢复。`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用API删除用户
          await new Promise(resolve => setTimeout(resolve, 1000))
          this.$message.success('删除成功')
          this.loadUsers()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },

    exportUsers() {
      this.$message.info('导出功能开发中...')
    },

    handleSizeChange(val) {
      this.pagination.size = val
      this.loadUsers()
    },

    handlePageChange(val) {
      this.pagination.page = val
      this.loadUsers()
    },

    getRoleType(role) {
      const types = {
        'PARENT': 'primary',
        'NUTRITIONIST': 'success',
        'ADMIN': 'danger'
      }
      return types[role] || 'info'
    },

    getRoleText(role) {
      const texts = {
        'PARENT': '家长',
        'NUTRITIONIST': '营养师',
        'ADMIN': '管理员'
      }
      return texts[role] || role
    },

    formatDate(date) {
      return moment(date).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.user-management {
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

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-header .el-button {
    margin-top: 10px;
    width: 100%;
  }

  .search-card .el-col {
    margin-bottom: 10px;
  }

  .search-card .el-button {
    width: 100%;
    margin-bottom: 5px;
  }
}
</style>

