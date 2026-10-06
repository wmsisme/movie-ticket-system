<template>
  <div class="movie-detail" v-if="movie">
    <div class="detail-hero" :style="{ backgroundImage: movie.posterUrl ? `url(${movie.posterUrl})` : undefined }">
      <div class="hero-overlay">
        <div class="hero-content">
          <div class="poster-box">
            <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" class="poster-img" />
            <div v-else class="poster-placeholder">{{ movie.title?.charAt(0) }}</div>
          </div>
          <div class="info-box">
            <h1 class="movie-title">{{ movie.title }}</h1>
            <div class="movie-tags">
              <el-tag v-if="movie.status === 2" type="success" size="large">热映中</el-tag>
              <el-tag v-else-if="movie.status === 1" type="warning" size="large">即将上映</el-tag>
              <el-tag v-else type="info" size="large">已下映</el-tag>
              <el-tag size="large" type="info">{{ movie.genre }}</el-tag>
              <el-tag size="large" type="info">{{ movie.duration }}分钟</el-tag>
            </div>
            <div class="rating-box">
              <span class="rating-star">⭐</span>
              <span class="rating-value">{{ movie.rating || '暂无评分' }}</span>
              <span class="rating-unit" v-if="movie.rating">/ 10</span>
            </div>
            <div class="meta-list">
              <div class="meta-item">
                <span class="meta-label">导演</span>
                <span class="meta-value">{{ movie.director || '未知' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">主演</span>
                <span class="meta-value">{{ movie.cast || '未知' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">上映日期</span>
                <span class="meta-value">{{ movie.releaseDate || '待定' }}</span>
              </div>
            </div>
            <div class="action-btns">
              <el-button class="btn-want" @click="toggleWant" :type="wanted ? 'default' : 'warning'" size="large" round>
                <span v-if="wanted">❤️ 已想看</span>
                <span v-else>🤍 想看</span>
              </el-button>
              <el-button class="btn-watched" @click="toggleWatched" :type="watched ? 'default' : 'success'" size="large" round>
                <span v-if="watched">✅ 已看过</span>
                <span v-else>👁️ 看过</span>
              </el-button>
              <el-button class="btn-buy" type="danger" size="large" round @click="goSessions">🎫 选座购票</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="detail-body">
      <div class="section">
        <h2 class="section-title">剧情简介</h2>
        <p class="description">{{ movie.description || '暂无简介' }}</p>
      </div>

      <div class="section">
        <h2 class="section-title">场次信息</h2>
        <div v-if="upcomingSessions.length > 0" class="sessions-list">
          <div v-for="group in groupedSessions" :key="group.date" class="session-group">
            <div class="session-date">{{ group.date }}</div>
            <div class="session-items">
              <div v-for="s in group.sessions" :key="s.screeningId" class="session-item" @click="goSeats(s.screeningId)">
                <span class="s-time">{{ s.startTime }} - {{ s.endTime }}</span>
                <el-tag size="small">{{ s.version }}</el-tag>
                <el-tag size="small" type="info">{{ s.language }}</el-tag>
                <span class="s-price">¥{{ s.price }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无近期场次" />
      </div>
    </div>
  </div>

  <div v-else-if="loading" class="loading-box">
    <el-skeleton :rows="8" animated />
  </div>

  <div v-else class="error-box">
    <el-empty description="电影不存在" />
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    return {
      movie: null,
      sessions: [],
      loading: true,
      wanted: false,
      watched: false
    }
  },
  computed: {
    upcomingSessions() {
      const today = new Date().toISOString().split('T')[0]
      return this.sessions.filter(s => s.screeningDate >= today)
    },
    groupedSessions() {
      const groups = {}
      this.upcomingSessions.forEach(s => {
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
    this.loadMovie()
  },
  methods: {
    async loadMovie() {
      this.loading = true
      try {
        const movieId = this.$route.params.movieId
        const [movieData, sessionsData] = await Promise.all([
          request.get('/movie/' + movieId),
          request.get('/screening/byMovie/' + movieId)
        ])
        this.movie = movieData
        this.sessions = sessionsData || []
        this.loadLocalStatus()
      } catch (e) {
        this.$message.error('加载电影详情失败')
      } finally {
        this.loading = false
      }
    },
    loadLocalStatus() {
      const key = 'movie_status_' + this.movie.movieId
      const saved = localStorage.getItem(key)
      if (saved) {
        try {
          const s = JSON.parse(saved)
          this.wanted = !!s.wanted
          this.watched = !!s.watched
        } catch (e) { /* ignore */ }
      }
    },
    saveLocalStatus() {
      const key = 'movie_status_' + this.movie.movieId
      localStorage.setItem(key, JSON.stringify({ wanted: this.wanted, watched: this.watched }))
    },
    toggleWant() {
      this.wanted = !this.wanted
      this.saveLocalStatus()
      this.$message.success(this.wanted ? '已标记为想看' : '已取消想看')
    },
    toggleWatched() {
      this.watched = !this.watched
      this.saveLocalStatus()
      this.$message.success(this.watched ? '已标记为看过' : '已取消看过')
    },
    goSessions() {
      this.$router.push('/sessions/' + this.movie.movieId)
    },
    goSeats(screeningId) {
      this.$router.push('/seats/' + screeningId)
    }
  }
}
</script>

<style scoped>
.movie-detail { min-height: 100vh; }

.detail-hero {
  position: relative;
  min-height: 420px;
  background-size: cover;
  background-position: center top;
}
.detail-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--placeholder-gradient);
  z-index: 0;
}

.hero-overlay {
  position: relative;
  z-index: 1;
  background: var(--overlay-bg);
  min-height: 420px;
  display: flex;
  align-items: center;
  padding: 40px;
}

.hero-content {
  display: flex;
  gap: 40px;
  max-width: 1000px;
  width: 100%;
  margin: 0 auto;
  align-items: flex-start;
}

.poster-box {
  flex-shrink: 0;
  width: 240px;
  height: 340px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0,0,0,0.4);
}
.poster-img { width: 100%; height: 100%; object-fit: cover; }
.poster-placeholder {
  width: 100%; height: 100%;
  background: var(--placeholder-gradient);
  display: flex; align-items: center; justify-content: center;
  font-size: 80px; color: #fff;
}

.info-box { flex: 1; color: #fff; }

.movie-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 16px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.movie-tags { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 16px; }

.rating-box {
  display: flex; align-items: baseline; gap: 6px; margin-bottom: 20px;
}
.rating-star { font-size: 24px; }
.rating-value { font-size: 32px; font-weight: 700; color: #ffd700; }
.rating-unit { font-size: 16px; color: rgba(255,255,255,0.7); }

.meta-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 24px; }
.meta-item { display: flex; gap: 8px; }
.meta-label { color: rgba(255,255,255,0.6); min-width: 70px; font-size: 14px; }
.meta-value { color: rgba(255,255,255,0.9); font-size: 14px; }

.action-btns { display: flex; gap: 12px; flex-wrap: wrap; }
.btn-buy { padding: 12px 36px !important; font-size: 16px !important; font-weight: 600 !important; }

.detail-body {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
}

.section { margin-bottom: 30px; }
.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
  padding-left: 12px;
  border-left: 4px solid var(--accent);
}

.description {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 15px;
}

.session-group { margin-bottom: 16px; }
.session-date {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
  padding-left: 4px;
}
.session-items { display: flex; flex-wrap: wrap; gap: 10px; }
.session-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  cursor: pointer;
  transition: all 0.2s;
}
.session-item:hover {
  border-color: var(--accent);
  box-shadow: var(--shadow-hover);
  transform: translateY(-1px);
}
.s-time { color: var(--text-primary); font-weight: 600; }
.s-price { color: var(--accent); font-weight: 700; font-size: 16px; margin-left: auto; }

.loading-box, .error-box {
  display: flex; justify-content: center; align-items: center;
  min-height: 300px; padding: 40px;
}
</style>
