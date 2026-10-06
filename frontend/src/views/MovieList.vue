<template>
  <div class="movie-list">
    <div class="page-header">
      <h2 class="page-title">正在热映</h2>
      <p class="page-subtitle">精彩大片，不容错过</p>
    </div>

    <el-row :gutter="24" v-if="hotMovies.length > 0">
      <el-col :span="6" v-for="movie in hotMovies" :key="movie.movieId" class="movie-col">
        <div class="movie-card" @click="goDetail(movie.movieId)">
          <div class="card-poster">
            <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" />
            <div v-else class="poster-placeholder">{{ movie.title?.charAt(0) }}</div>
            <div class="poster-overlay">
              <div class="overlay-btn">查看详情</div>
            </div>
          </div>
          <div class="card-body">
            <div class="card-title-row">
              <h3 class="card-title">{{ movie.title }}</h3>
              <el-tag :type="movie.status === 2 ? 'success' : 'warning'" size="small">
                {{ movie.status === 2 ? '热映' : movie.status === 1 ? '即将上映' : '已下映' }}
              </el-tag>
            </div>
            <p class="card-genre">{{ movie.genre }}</p>
            <div class="card-bottom">
              <span class="card-rating">
                <span class="star">⭐</span> {{ movie.rating || '暂无评分' }}
              </span>
              <span class="card-duration">{{ movie.duration }}分钟</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-empty v-else description="暂无上映电影" />

    <div class="section-divider">
      <span>即将上映</span>
    </div>

    <el-row :gutter="24" v-if="upcomingMovies.length > 0">
      <el-col :span="6" v-for="movie in upcomingMovies" :key="movie.movieId" class="movie-col">
        <div class="movie-card upcoming" @click="goDetail(movie.movieId)">
          <div class="card-poster">
            <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" />
            <div v-else class="poster-placeholder">{{ movie.title?.charAt(0) }}</div>
            <div class="poster-overlay">
              <div class="overlay-btn">查看详情</div>
            </div>
          </div>
          <div class="card-body">
            <div class="card-title-row">
              <h3 class="card-title">{{ movie.title }}</h3>
              <el-tag type="warning" size="small">即将上映</el-tag>
            </div>
            <p class="card-genre">{{ movie.genre }}</p>
            <div class="card-bottom">
              <span class="card-rating"><span class="star">⭐</span> {{ movie.rating || '暂无评分' }}</span>
              <span class="card-date">{{ movie.releaseDate }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    return { movies: [] }
  },
  computed: {
    hotMovies() {
      return this.movies.filter(m => Number(m.status) === 2)
    },
    upcomingMovies() {
      return this.movies.filter(m => Number(m.status) === 1)
    }
  },
  created() {
    this.fetchMovies()
  },
  methods: {
    async fetchMovies() {
      try {
        this.movies = await request.get('/movie/list')
      } catch (e) {
        this.$message.error('加载电影列表失败')
      }
    },
    goDetail(movieId) {
      this.$router.push('/movie/' + movieId)
    }
  }
}
</script>

<style scoped>
.movie-list { max-width: 1280px; margin: 0 auto; padding: 10px 0; }

.page-header { text-align: center; margin-bottom: 32px; }
.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
  border: none;
  padding: 0;
}
.page-subtitle { color: var(--text-muted); font-size: 15px; margin: 0; }

.movie-col { margin-bottom: 24px; }

.movie-card {
  border-radius: 12px;
  overflow: hidden;
  background: var(--bg-card);
  box-shadow: var(--shadow);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}
.movie-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-hover);
}

.card-poster {
  position: relative;
  width: 100%;
  height: 340px;
  overflow: hidden;
  background: var(--placeholder-gradient);
}
.card-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}
.movie-card:hover .card-poster img {
  transform: scale(1.05);
}
.poster-placeholder {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  font-size: 70px; color: #fff; font-weight: 700;
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.movie-card:hover .poster-overlay { opacity: 1; }
.overlay-btn {
  padding: 10px 28px;
  border: 2px solid #fff;
  border-radius: 24px;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 1px;
}

.card-body { padding: 14px 16px; }
.card-title-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px; }
.card-title { font-size: 16px; font-weight: 600; color: var(--text-primary); margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 70%; }
.card-genre { color: var(--text-muted); font-size: 13px; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-bottom { display: flex; align-items: center; justify-content: space-between; }
.card-rating { color: var(--accent); font-size: 14px; font-weight: 600; }
.star { font-size: 12px; }
.card-duration { color: var(--text-muted); font-size: 13px; }
.card-date { color: var(--accent-secondary); font-size: 13px; }

.section-divider {
  text-align: center;
  margin: 40px 0 24px;
  position: relative;
}
.section-divider::before {
  content: '';
  position: absolute;
  left: 0; right: 0; top: 50%;
  height: 1px;
  background: var(--border-color);
}
.section-divider span {
  position: relative;
  background: var(--bg-primary);
  padding: 0 24px;
  color: var(--text-muted);
  font-size: 16px;
  font-weight: 500;
}

.upcoming .card-poster { height: 340px; }
</style>
