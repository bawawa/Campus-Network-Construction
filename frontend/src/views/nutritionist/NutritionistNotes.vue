<template>
  <div class="nutritionist-notes">
    <div class="page-header">
      <h2>营养师留言</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <!-- 留言列表 -->
    <el-card v-if="notes.length > 0">
      <el-timeline>
        <el-timeline-item
          v-for="note in notes"
          :key="note.id"
          :timestamp="formatDate(note.createdAt)"
          :type="getNoteType(note.noteType)"
          placement="top"
        >
          <div class="note-item">
            <div class="note-header">
              <span class="note-title">{{ note.title }}</span>
              <div class="note-tags">
                <el-tag size="mini" :type="getPriorityType(note.priority)">
                  优先级: {{ note.priority }}
                </el-tag>
                <el-tag size="mini" v-if="!note.isRead" type="danger">未读</el-tag>
              </div>
            </div>
            <div class="note-content">{{ note.content }}</div>
            <div v-if="note.referenceLinks" class="note-links">
              <strong>参考链接：</strong>
              <a :href="note.referenceLinks" target="_blank">{{ note.referenceLinks }}</a>
            </div>

            <!-- 家长回复区域 -->
            <div v-if="note.parentResponse" class="note-response">
              <div class="response-header">
                <strong>家长回复：</strong>
                <small>{{ formatDate(note.responseTime) }}</small>
              </div>
              <div class="response-content">{{ note.parentResponse }}</div>
            </div>

            <div v-else class="reply-area">
              <el-input
                v-model="replyForm[note.id]"
                type="textarea"
                :rows="3"
                placeholder="输入您的回复..."
              />
              <el-button
                type="primary"
                size="small"
                style="margin-top: 10px;"
                @click="submitReply(note.id)"
                :loading="replying[note.id]"
              >
                提交回复
              </el-button>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-card v-else>
      <el-empty description="暂无营养师留言" />
    </el-card>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import moment from 'moment'

export default {
  name: 'NutritionistNotes',
  data() {
    return {
      notes: [],
      childId: null,
      replying: {},
      replyForm: {}
    }
  },
  computed: {
    childId() {
      return this.$route.params.childId
    }
  },
  created() {
    this.loadNotes()
  },
  methods: {
    ...mapActions('nutritionist', ['getNotesByChildId', 'addParentResponse']),

    async loadNotes() {
      if (!this.childId) {
        this.$message.warning('未选择儿童')
        return
      }
      try {
        const response = await this.getNotesByChildId({
          childId: this.childId,
          page: 0,
          size: 100
        })
        this.notes = response.data || []
      } catch (error) {
        this.$message.error('加载留言失败: ' + error.message)
      }
    },

    async submitReply(noteId) {
      const replyText = this.replyForm[noteId]
      if (!replyText || !replyText.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }

      try {
        this.$set(this.replying, noteId, true)
        await this.addParentResponse({ noteId, response: replyText })
        this.$message.success('回复成功')

        // 清空回复框
        this.$set(this.replyForm, noteId, '')

        // 重新加载留言
        await this.loadNotes()
      } catch (error) {
        this.$message.error('回复失败: ' + error.message)
      } finally {
        this.$set(this.replying, noteId, false)
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

    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.nutritionist-notes {
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

.note-item {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.note-title {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.note-tags {
  display: flex;
  gap: 5px;
}

.note-content {
  margin-bottom: 10px;
  color: #606266;
  line-height: 1.6;
}

.note-links {
  margin-bottom: 10px;
  padding: 8px;
  background-color: #e8f4fd;
  border-radius: 4px;
  font-size: 13px;
}

.note-links a {
  color: #409EFF;
  text-decoration: none;
  word-break: break-all;
}

.note-response {
  margin-top: 15px;
  padding: 12px;
  background-color: #f0f9eb;
  border-left: 3px solid #67C23A;
  border-radius: 4px;
}

.response-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.response-header strong {
  color: #67C23A;
}

.response-content {
  color: #606266;
  line-height: 1.5;
}

.reply-area {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}
</style>

