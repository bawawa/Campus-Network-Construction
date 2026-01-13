<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>ASD儿童饮食与营养均衡系统</h1>
        <p>专业的ASD儿童营养管理平台</p>
      </div>

      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" class="login-form" @keyup.enter.native="handleLogin">
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            type="email"
            placeholder="请输入邮箱地址"
            prefix-icon="el-icon-message"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            class="login-button"
            size="large"
            :loading="loading"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <el-link type="primary" @click="showRegister = true">还没有账号？立即注册</el-link>
        </div>
      </el-form>
    </div>

    <!-- 注册对话框 -->
    <el-dialog title="用户注册" :visible.sync="showRegister" width="500px" @close="resetRegisterForm">
      <el-form :model="registerForm" :rules="registerRules" ref="registerForm" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" type="email" placeholder="请输入邮箱地址" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="registerForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="家长" value="PARENT" />
            <el-option label="营养师" value="NUTRITIONIST" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>

        <el-form-item label="关系" prop="relationshipType" v-if="registerForm.role === 'PARENT'">
          <el-select v-model="registerForm.relationshipType" placeholder="请选择与儿童关系" style="width: 100%">
            <el-option label="母亲" value="MOTHER" />
            <el-option label="父亲" value="FATHER" />
            <el-option label="监护人" value="GUARDIAN" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="registerLoading">注册</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loginForm: {
        email: '',
        password: ''
      },
      loginRules: {
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ]
      },
      registerForm: {
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        role: 'PARENT',
        relationshipType: 'MOTHER'
      },
      registerRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        relationshipType: [
          { required: true, message: '请选择与儿童关系', trigger: 'change' }
        ]
      },
      loading: false,
      registerLoading: false,
      showRegister: false
    }
  },
  methods: {
    ...mapActions('user', ['login', 'register']),

    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            await this.login(this.loginForm)
            this.$message.success('登录成功')
            const redirect = this.$route.query.redirect || '/dashboard'
            this.$router.push(redirect)
          } catch (error) {
            this.$message.error(error.message || '登录失败，请检查邮箱和密码')
          } finally {
            this.loading = false
          }
        }
      })
    },

    handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          this.registerLoading = true
          try {
            const { confirmPassword, ...registerData } = this.registerForm
            await this.register(registerData)
            this.$message.success('注册成功，请登录')
            this.showRegister = false
            this.loginForm.email = registerData.email
          } catch (error) {
            this.$message.error(error.message || '注册失败，请重试')
          } finally {
            this.registerLoading = false
          }
        }
      })
    },

    resetRegisterForm() {
      this.$refs.registerForm && this.$refs.registerForm.resetFields()
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 420px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #303133;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .login-box {
    margin: 20px;
    padding: 30px 20px;
  }

  .login-header h1 {
    font-size: 20px;
  }
}
</style>

