<template>
  <div class="user-profile">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>

    <el-card v-loading="loading">
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="userForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" disabled />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="关系类型" prop="relationshipType" v-if="userInfo.role === 'PARENT'">
              <el-select v-model="userForm.relationshipType" placeholder="请选择与儿童关系">
                <el-option label="父亲" value="FATHER" />
                <el-option label="母亲" value="MOTHER" />
                <el-option label="监护人" value="GUARDIAN" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="密码">
              <el-button type="primary" @click="showChangePasswordDialog">修改密码</el-button>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="center">
          <el-button type="primary" :loading="saving" @click="saveUserInfo">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-row>
      </el-form>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" :visible.sync="passwordDialogVisible" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {mapActions, mapGetters} from 'vuex'

export default {
  name: 'UserProfile',
  data() {
    return {
      loading: false,
      saving: false,
      changingPassword: false,
      passwordDialogVisible: false,
      userForm: {
        name: '',
        email: '',
        phone: '',
        relationshipType: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
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
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        relationshipType: [
          { required: true, message: '请选择关系类型', trigger: 'change' }
        ]
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo'])
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    ...mapActions('user', ['updateUserInfo', 'changePassword']),

    loadUserInfo() {
      if (this.userInfo) {
        this.userForm = {
          name: this.userInfo.name || '',
          email: this.userInfo.email || '',
          phone: this.userInfo.phone || '',
          relationshipType: this.userInfo.relationshipType || ''
        }
      }
    },

    resetForm() {
      this.userForm = {
        name: this.userInfo.name || '',
        email: this.userInfo.email || '',
        phone: this.userInfo.phone || '',
        relationshipType: this.userInfo.relationshipType || ''
      }
      this.$refs.userForm.resetFields()
    },

    async saveUserInfo() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.saving = true
          try {
            await this.updateUserInfo(this.userForm)
            this.$message.success('保存成功')
          } catch (error) {
            console.error('保存失败:', error)
            this.$message.error('保存失败，请重试')
          } finally {
            this.saving = false
          }
        }
      })
    },

    showChangePasswordDialog() {
      this.passwordDialogVisible = true
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    },

    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    },

    async changePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.changingPassword = true
          try {
            // TODO: 调用修改密码 API
            await new Promise(resolve => setTimeout(resolve, 1000))
            this.$message.success('密码修改成功')
            this.passwordDialogVisible = false
          } catch (error) {
            this.$message.error('密码修改失败')
          } finally {
            this.changingPassword = false
          }
        }
      })
    },

    validateConfirmPassword(rule, value, callback) {
      if (!value) {
        callback(new Error('请再次输入新密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },

    goToDashboard() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.user-profile {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #301303133;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button + .el-button {
  margin-left: 10px;
}
</style>

