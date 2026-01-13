<template>
  <div class="nutritionist-panel">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>营养师工作台</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshData">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-number">{{ (stats && stats.totalChildren) || 0 }}</div>
              <div class="stat-label">管理儿童</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-number">{{ (stats && stats.totalNotes) || 0 }}</div>
              <div class="stat-label">总留言数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-number">{{ (stats && stats.pendingResponses) || 0 }}</div>
              <div class="stat-label">待回复</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>儿童列表</span>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索儿童姓名"
              style="width: 200px; float: right;"
              @input="filterChildren"
            >
              <i slot="prefix" class="el-icon-search"></i>
            </el-input>
          </div>

          <el-table :data="filteredChildren" style="width: 100%" @row-click="selectChild">
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="age" label="年龄" width="80"></el-table-column>
            <el-table-column prop="gender" label="性别" width="80"></el-table-column>
            <el-table-column label="ASD特质" min-width="150">
              <template slot-scope="scope">
                <el-tag size="mini" v-if="scope.row.asdProfile">
                  {{ scope.row.asdProfile.traitLevel }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="未读留言" width="100">
              <template slot-scope="scope">
                <el-badge :value="getUnreadCount(scope.row.id)" :max="99">
                  <el-button size="mini" type="info">留言</el-button>
                </el-badge>
              </template>
            </el-table-column>
            <el-table-column label="最后活动时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.lastActivity) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card v-if="selectedChild">
          <div slot="header">
            <span>{{ selectedChild.name }} - 详细信息</span>
          </div>

          <div class="child-detail">
            <el-descriptions :title="selectedChild.name" :column="1" size="small">
              <el-descriptions-item label="年龄">{{ selectedChild.age }}岁</el-descriptions-item>
              <el-descriptions-item label="性别">{{ selectedChild.gender }}</el-descriptions-item>
              <el-descriptions-item label="生日">{{ formatDate(selectedChild.birthDate) }}</el-descriptions-item>
              <el-descriptions-item label="ASD特质等级">
                <el-tag size="mini">{{ (selectedChild.asdProfile && selectedChild.asdProfile.traitLevel) || '未评估' }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <div style="margin-top: 20px;">
              <el-button type="primary" size="small" @click="showCreateNoteDialog">
                创建留言
              </el-button>
              <el-button size="small" @click="viewChildNotes">
                查看留言历史
              </el-button>
            </div>

            <div v-if="selectedChild.asdProfile" style="margin-top: 15px;">
              <h4>ASD特质详情</h4>
              <el-descriptions :column="1" size="mini" border>
                <el-descriptions-item label="社交互动">{{ selectedChild.asdProfile.socialInteraction }}</el-descriptions-item>
                <el-descriptions-item label="沟通能力">{{ selectedChild.asdProfile.communication }}</el-descriptions-item>
                <el-descriptions-item label="行为模式">{{ selectedChild.asdProfile.behaviorPatterns }}</el-descriptions-item>
                <el-descriptions-item label="感官敏感度">{{ selectedChild.asdProfile.sensorySensitivity }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-card>

        <el-card v-else>
          <div class="placeholder">
            <i class="el-icon-user" style="font-size: 48px; color: #ccc;"></i>
            <p>请选择一个儿童查看详细信息</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 创建留言对话框 -->
    <el-dialog :title="`为 ${selectedChild && selectedChild.name} 创建留言`" :visible.sync="createNoteDialogVisible" width="600px">
      <el-form :model="noteForm" :rules="noteRules" ref="noteForm" label-width="80px">
        <el-form-item label="留言类型" prop="noteType">
          <el-select v-model="noteForm.noteType" placeholder="请选择留言类型" style="width: 100%">
            <el-option label="营养建议" value="nutrition_advice"></el-option>
            <el-option label="饮食警告" value="diet_warning"></el-option>
            <el-option label="健康提醒" value="health_reminder"></el-option>
            <el-option label="进展反馈" value="progress_feedback"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="noteForm.title" placeholder="请输入留言标题"></el-input>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            v-model="noteForm.content"
            :rows="4"
            placeholder="请输入留言内容"
          ></el-input>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-rate v-model="noteForm.priority" :max="5" show-text></el-rate>
        </el-form-item>

        <el-form-item label="参考链接">
          <el-input v-model="noteForm.referenceLinks" placeholder="请输入相关参考链接"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="createNoteDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitNote" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 留言历史对话框 -->
    <el-dialog title="留言历史" :visible.sync="notesDialogVisible" width="800px">
      <div v-if="childNotes.length > 0">
        <el-timeline>
          <el-timeline-item
            v-for="note in childNotes"
            :key="note.id"
            :timestamp="formatDate(note.createdAt)"
            :type="getNoteType(note.noteType)"
          >
            <div class="note-item">
              <div class="note-header">
                <span class="note-title">{{ note.title }}</span>
                <el-tag size="mini" :type="getPriorityType(note.priority)">
                  优先级: {{ note.priority }}
                </el-tag>
              </div>
              <div class="note-content">{{ note.content }}</div>
              <div v-if="note.parentResponse" class="note-response">
                <strong>家长回复:</strong> {{ note.parentResponse }}
                <small style="margin-left: 10px;">{{ formatDate(note.responseTime) }}</small>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
      <div v-else class="no-notes">
        <p>暂无留言记录</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {mapActions, mapState} from 'vuex'
import moment from 'moment'

export default {
  name: 'NutritionistPanel',
  data() {
    return {
      children: [],
      filteredChildren: [],
      selectedChild: null,
      searchKeyword: '',
      createNoteDialogVisible: false,
      notesDialogVisible: false,
      childNotes: [],
      submitting: false,
      noteForm: {
        noteType: '',
        title: '',
        content: '',
        priority: 3,
        referenceLinks: ''
      },
      noteRules: {
        noteType: [{ required: true, message: '请选择留言类型', trigger: 'change' }],
        title: [{ required: true, message: '请输入留言标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入留言内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapState('nutritionist', ['noteStats', 'unreadNotes']),
    ...mapState('user', ['userInfo']),
    stats() {
      return this.noteStats || { totalChildren: 0, totalNotes: 0, pendingResponses: 0, recentNotes: 0 }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    ...mapActions('nutritionist', ['getNotesByChildId', 'createNote', 'getUnreadNotes', 'getNutritionistStats']),
    ...mapActions('child', ['fetchAllChildren']),

    async loadData() {
      try {
        // 加载儿童列表
        await this.loadChildren()
        // 加载统计数据
        this.loadStats()
      } catch (error) {
        this.$message.error('加载数据失败: ' + error.message)
      }
    },

    async loadChildren() {
      try {
        const response = await this.fetchChildren()
        if (response && response.data) {
          this.children = response.data || []
          this.filteredChildren = this.children
        }
      } catch (error) {
        console.error('加载儿童列表失败:', error)
      }
    },

    async loadStats() {
      if (!this.userInfo || !this.userInfo.id) {
        console.warn('用户未登录，无法加载统计数据')
        return
      }
      try {
        await this.getNutritionistStats(this.userInfo.id)
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },

    filterChildren() {
      if (!this.searchKeyword) {
        this.filteredChildren = this.children
      } else {
        this.filteredChildren = this.children.filter(child =>
          child.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
        )
      }
    },

    selectChild(child) {
      this.selectedChild = child
      this.loadChildUnreadNotes(child.id)
    },

    async loadChildUnreadNotes(childId) {
      try {
        await this.getUnreadNotes(childId)
      } catch (error) {
        console.error('加载未读留言失败:', error)
      }
    },

    getUnreadCount(childId) {
      const unread = this.unreadNotes.filter(note => note.child.id === childId)
      return unread.length
    },

    showCreateNoteDialog() {
      this.createNoteDialogVisible = true
      this.resetNoteForm()
    },

    resetNoteForm() {
      this.noteForm = {
        noteType: '',
        title: '',
        content: '',
        priority: 3,
        referenceLinks: ''
      }
    },

    async submitNote() {
      this.$refs.noteForm.validate(async (valid) => {
        if (valid) {
          try {
            this.submitting = true
            const noteData = {
              childId: this.selectedChild.id,
              nutritionistId: this.userInfo.id,
              ...this.noteForm
            }

            await this.createNote(noteData)
            this.$message.success('留言创建成功')
            this.createNoteDialogVisible = false

            // 重新加载未读留言
            await this.loadChildUnreadNotes(this.selectedChild.id)
          } catch (error) {
            this.$message.error('创建留言失败: ' + error.message)
          } finally {
            this.submitting = false
          }
        }
      })
    },

    async viewChildNotes() {
      if (!this.selectedChild) return

      try {
        const response = await this.getNotesByChildId({
          childId: this.selectedChild.id,
          page: 0,
          size: 50
        })
        this.childNotes = response.data || []
        this.notesDialogVisible = true
      } catch (error) {
        this.$message.error('加载留言历史失败: ' + error.message)
      }
    },

    getNoteType(type) {
      const types = {
        'nutrition_advice': 'success',
        'diet_warning': 'warning',
        'health_reminder': 'info',
        'progress_feedback': 'primary',
        'other': 'info'
      }
      return types[type] || 'info'
    },

    getPriorityType(priority) {
      if (priority >= 4) return 'danger'
      if (priority >= 3) return 'warning'
      return 'info'
    },

    formatDate(date) {
      return moment(date).format('YYYY-MM-DD HH:mm')
    },

    refreshData() {
      this.loadData()
      this.$message.success('数据已刷新')
    }
  }
}
</script>

<style scoped>
.nutritionist-panel {
  padding: 20px;
}

.stat-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.stat-item {
  padding: 20px 0;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.child-detail {
  max-height: 500px;
  overflow-y: auto;
}

.placeholder {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.note-item {
  padding: 10px;
  border-left: 3px solid #409EFF;
  margin-bottom: 10px;
  background-color: #f8f9fa;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.note-title {
  font-weight: bold;
  color: #333;
}

.note-content {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.5;
}

.note-response {
  padding: 8px;
  background-color: #e8f4fd;
  border-radius: 4px;
  font-size: 13px;
  color: #666;
}

.no-notes {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.el-table .el-badge {
  vertical-align: middle;
}
</style>

