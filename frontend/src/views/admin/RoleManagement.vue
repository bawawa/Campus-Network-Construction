<template>
  <div class="role-management">
    <div class="page-header">
      <h2>角色权限管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="showAddRoleDialog">
        添加角色
      </el-button>
    </div>

    <!-- 角色列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="role in roles" :key="role.id">
        <el-card class="role-card">
          <div class="role-header">
            <div class="role-icon">
              <i :class="getRoleIcon(role.name)"></i>
            </div>
            <div class="role-info">
              <h3>{{ role.displayName }}</h3>
              <el-tag :type="getRoleType(role.name)" size="small">
                {{ role.name }}
              </el-tag>
            </div>
          </div>

          <div class="role-description">
            {{ role.description || '暂无描述' }}
          </div>

          <div class="role-stats">
            <span>用户数: {{ role.userCount || 0 }}</span>
            <span>权限数: {{ (role.permissions && role.permissions.length) || 0 }}</span>
          </div>

          <div class="role-permissions">
            <h4>权限列表</h4>
            <div class="permissions-tags">
              <el-tag
                v-for="permission in (role.permissions || [])"
                :key="permission.id"
                size="mini"
                type="info"
                style="margin: 2px;"
              >
                {{ permission.displayName }}
              </el-tag>
            </div>
          </div>

          <div class="role-actions">
            <el-button size="small" type="primary" @click="editRole(role)">
              编辑
            </el-button>
            <el-button size="small" type="warning" @click="managePermissions(role)">
              权限管理
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="deleteRole(role)"
              :disabled="isSystemRole(role.name)"
            >
              删除
            </el-button>
          </div>

          <div class="role-status">
            <el-switch
              v-model="role.isActive"
              @change="toggleRoleStatus(role)"
              active-text="启用"
              inactive-text="禁用"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      :title="roleDialog.title"
      :visible.sync="roleDialog.visible"
      width="600px"
      @close="resetRoleForm"
    >
      <el-form :model="roleForm" :rules="roleRules" ref="roleForm" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" :disabled="!!roleForm.id" />
        </el-form-item>

        <el-form-item label="显示名称" prop="displayName">
          <el-input v-model="roleForm.displayName" placeholder="请输入显示名称" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>

        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="roleForm.sortOrder" :min="0" :max="999" />
        </el-form-item>

        <el-form-item label="状态">
          <el-switch
            v-model="roleForm.isActive"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>

        <el-form-item label="权限">
          <el-select
            v-model="roleForm.permissionIds"
            multiple
            placeholder="请选择权限"
            style="width: 100%"
          >
            <el-option
              v-for="permission in allPermissions"
              :key="permission.id"
              :label="permission.displayName"
              :value="permission.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveRole" :loading="roleDialog.loading">
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 权限管理对话框 -->
    <el-dialog
      :title="`管理 ${selectedRole && selectedRole.displayName} 的权限`"
      :visible.sync="permissionDialog.visible"
      width="800px"
    >
      <el-transfer
        v-model="selectedPermissions"
        :data="availablePermissions"
        :props="{
          key: 'id',
          label: 'displayName'
        }"
        :titles="['可选权限', '已选权限']"
        filterable
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'RoleManagement',
  data() {
    return {
      roles: [],
      allPermissions: [],
      selectedRole: null,
      selectedPermissions: [],
      availablePermissions: [],
      roleDialog: {
        visible: false,
        title: '添加角色',
        loading: false
      },
      permissionDialog: {
        visible: false
      },
      roleForm: {
        id: null,
        name: '',
        displayName: '',
        description: '',
        sortOrder: 0,
        isActive: true,
        permissionIds: []
      },
      roleRules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ],
        displayName: [
          { required: true, message: '请输入显示名称', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadRoles()
    this.loadPermissions()
  },
  methods: {
    async loadRoles() {
      try {
        // 这里应该调用API获取角色列表
        // 暂时使用模拟数据
        const mockRoles = [
          {
            id: 1,
            name: 'PARENT',
            displayName: '家长',
            description: '管理自己孩子的饮食和营养信息',
            isActive: true,
            sortOrder: 1,
            userCount: 89,
            permissions: [
              { id: 1, displayName: '查看儿童信息' },
              { id: 2, displayName: '添加饮食记录' },
              { id: 3, displayName: '查看营养报告' },
              { id: 4, displayName: '查看食谱推荐' }
            ]
          },
          {
            id: 2,
            name: 'NUTRITIONIST',
            displayName: '营养师',
            description: '为多个孩子提供营养建议和分析',
            isActive: true,
            sortOrder: 2,
            userCount: 67,
            permissions: [
              { id: 1, displayName: '查看儿童信息' },
              { id: 3, displayName: '查看营养报告' },
              { id: 4, displayName: '查看食谱推荐' },
              { id: 5, displayName: '创建营养建议' },
              { id: 6, displayName: '查看营养师面板' }
            ]
          },
          {
            id: 3,
            name: 'ADMIN',
            displayName: '管理员',
            description: '系统管理员，拥有所有权限',
            isActive: true,
            sortOrder: 3,
            userCount: 3,
            permissions: [
              { id: 1, displayName: '查看儿童信息' },
              { id: 2, displayName: '添加饮食记录' },
              { id: 3, displayName: '查看营养报告' },
              { id: 4, displayName: '查看食谱推荐' },
              { id: 5, displayName: '创建营养建议' },
              { id: 6, displayName: '查看营养师面板' },
              { id: 7, displayName: '用户管理' },
              { id: 8, displayName: '角色管理' },
              { id: 9, displayName: '系统设置' }
            ]
          }
        ]
        this.roles = mockRoles
      } catch (error) {
        this.$message.error('加载角色列表失败')
      }
    },

    async loadPermissions() {
      try {
        // 这里应该调用API获取权限列表
        const mockPermissions = [
          { id: 1, displayName: '查看儿童信息', name: 'VIEW_CHILDREN' },
          { id: 2, displayName: '添加饮食记录', name: 'ADD_DIETARY_RECORDS' },
          { id: 3, displayName: '查看营养报告', name: 'VIEW_REPORTS' },
          { id: 4, displayName: '查看食谱推荐', name: 'VIEW_RECIPES' },
          { id: 5, displayName: '创建营养建议', name: 'CREATE_ADVICE' },
          { id: 6, displayName: '查看营养师面板', name: 'VIEW_NUTRITIONIST_PANEL' },
          { id: 7, displayName: '用户管理', name: 'MANAGE_USERS' },
          { id: 8, displayName: '角色管理', name: 'MANAGE_ROLES' },
          { id: 9, displayName: '系统设置', name: 'SYSTEM_SETTINGS' }
        ]
        this.allPermissions = mockPermissions
        this.availablePermissions = mockPermissions
      } catch (error) {
        this.$message.error('加载权限列表失败')
      }
    },

    showAddRoleDialog() {
      this.roleDialog.title = '添加角色'
      this.roleDialog.visible = true
    },

    editRole(role) {
      this.roleDialog.title = '编辑角色'
      this.roleForm = {
        id: role.id,
        name: role.name,
        displayName: role.displayName,
        description: role.description,
        sortOrder: role.sortOrder,
        isActive: role.isActive,
        permissionIds: role.permissions ? role.permissions.map(p => p.id) : []
      }
      this.roleDialog.visible = true
    },

    async saveRole() {
      this.$refs.roleForm.validate(async (valid) => {
        if (valid) {
          this.roleDialog.loading = true
          try {
            // 这里应该调用API保存角色
            await new Promise(resolve => setTimeout(resolve, 1000))
            this.$message.success(this.roleForm.id ? '更新成功' : '添加成功')
            this.roleDialog.visible = false
            this.loadRoles()
          } catch (error) {
            this.$message.error('操作失败')
          } finally {
            this.roleDialog.loading = false
          }
        }
      })
    },

    resetRoleForm() {
      this.$refs.roleForm && this.$refs.roleForm.resetFields()
      this.roleForm = {
        id: null,
        name: '',
        displayName: '',
        description: '',
        sortOrder: 0,
        isActive: true,
        permissionIds: []
      }
    },

    async toggleRoleStatus(role) {
      try {
        // 这里应该调用API更新角色状态
        await new Promise(resolve => setTimeout(resolve, 500))
        this.$message.success('角色状态已更新')
      } catch (error) {
        role.isActive = !role.isActive
        this.$message.error('更新状态失败')
      }
    },

    managePermissions(role) {
      this.selectedRole = role
      this.selectedPermissions = role.permissions ? role.permissions.map(p => p.id) : []
      this.permissionDialog.visible = true
    },

    async savePermissions() {
      try {
        // 这里应该调用API保存角色权限
        await new Promise(resolve => setTimeout(resolve, 1000))
        this.$message.success('权限设置成功')
        this.permissionDialog.visible = false
        this.loadRoles()
      } catch (error) {
        this.$message.error('权限设置失败')
      }
    },

    async deleteRole(role) {
      const systemRoles = ['PARENT', 'NUTRITIONIST', 'ADMIN']
      if (systemRoles.includes(role.name)) {
        this.$message.warning('系统默认角色不能删除')
        return
      }

      this.$confirm(`确定要删除角色 "${role.displayName}" 吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用API删除角色
          await new Promise(resolve => setTimeout(resolve, 1000))
          this.$message.success('删除成功')
          this.loadRoles()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },

    isSystemRole(roleName) {
      const systemRoles = ['PARENT', 'NUTRITIONIST', 'ADMIN']
      return systemRoles.includes(roleName)
    },

    getRoleType(roleName) {
      const types = {
        'PARENT': 'primary',
        'NUTRITIONIST': 'success',
        'ADMIN': 'danger'
      }
      return types[roleName] || 'info'
    },

    getRoleIcon(roleName) {
      const icons = {
        'PARENT': 'el-icon-s-custom',
        'NUTRITIONIST': 'el-icon-user-solid',
        'ADMIN': 'el-icon-s-tools'
      }
      return icons[roleName] || 'el-icon-user'
    }
  }
}
</script>

<style scoped>
.role-management {
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

.role-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.role-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.role-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.role-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #36a3f7);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.role-info h3 {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 16px;
}

.role-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
  min-height: 40px;
}

.role-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 13px;
  color: #909399;
}

.role-permissions {
  margin-bottom: 15px;
}

.role-permissions h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}

.permissions-tags {
  min-height: 60px;
}

.role-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.role-actions .el-button {
  flex: 1;
}

.role-status {
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
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

  .el-col {
    margin-bottom: 20px;
  }

  .role-actions {
    flex-direction: column;
  }

  .role-actions .el-button {
    width: 100%;
  }
}
</style>

