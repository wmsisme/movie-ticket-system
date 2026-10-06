<template>
  <div class="session-list">
    <div class="page-header">
      <h2 class="page-title">选择场次</h2>
    </div>
    <el-empty v-if="sessions.length === 0" description="暂无场次" />
    <div v-for="group in groupedSessions" :key="group.date" class="date-group">
      <div class="date-header">
        <span class="date-text">{{ group.date }}</span>
        <span class="date-count">{{ group.sessions.length }} 场</span>
      </div>
      <div class="session-cards">
        <div v-for="s in group.sessions" :key="s.screeningId" class="session-card" @click="goSeats(s.screeningId)">
          <div class="s-time-block">
            <div class="s-start">{{ s.startTime }}</div>
            <div class="s-end">{{ s.endTime }} 散场</div>
          </div>
          <div class="s-meta">
            <el-tag size="small" effect="plain">{{ s.version }}</el-tag>
            <el-tag size="small" effect="plain" type="info">{{ s.language }}</el-tag>
            <span class="s-hall">影厅 #{{ s.hallId }}</span>
          </div>
          <div class="s-price-action">
            <div class="s-price">¥{{ s.price }}</div>
            <el-button type="danger" round size="default" @click.stop="goSeats(s.screeningId)">选座购票</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    return { sessions: [] }
  },
  computed: {
    groupedSessions() {
      const groups = {}
      this.sessions.forEach(s => {
        const date = s.screeningDate
        if (!groups[date]) groups[date] = []
        groups[date].push(s)
      })
      return Object.keys(groups).sort().map(date => ({
        date,
        sessions: groups[date]
      }))
    }
  },
  created() {
    this.fetchSessions()
  },
  methods: {
    async fetchSessions() {
      try {
        const movieId = this.$route.params.movieId
        this.sessions = await request.get('/screening/byMovie/' + movieId)
      } catch (e) {
        this.$message.error('加载场次失败')
      }
    },
    goSeats(screeningId) {
      this.$router.push('/seats/' + screeningId)
    }
  }
}
</script>

<style scoped>
.session-list { max-width: 900px; margin: 0 auto; }

.page-header { text-align: center; margin-bottom: 28px; }
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0; border: none; padding: 0;
}

.date-group { margin-bottom: 28px; }
.date-header {
  display: flex; align-items: center; gap: 12px;
  margin-bottom: 14px; padding-bottom: 10px;
  border-bottom: 2px solid var(--accent);
}
.date-text { font-size: 18px; font-weight: 600; color: var(--text-primary); }
.date-count { font-size: 13px; color: var(--text-muted); }

.session-cards { display: flex; flex-direction: column; gap: 10px; }

.session-card {
  display: flex; align-items: center; gap: 20px;
  padding: 16px 20px;
  border-radius: 12px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow);
}
.session-card:hover {
  border-color: var(--accent);
  box-shadow: var(--shadow-hover);
  transform: translateX(4px);
}

.s-time-block { min-width: 100px; text-align: center; }
.s-start { font-size: 22px; font-weight: 700; color: var(--text-primary); }
.s-end { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

.s-meta { display: flex; align-items: center; gap: 8px; flex: 1; }
.s-hall { font-size: 13px; color: var(--text-muted); }

.s-price-action { display: flex; align-items: center; gap: 16px; }
.s-price { font-size: 26px; font-weight: 700; color: var(--accent); }
</style>
