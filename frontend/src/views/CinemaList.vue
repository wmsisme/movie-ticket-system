<template>
  <div class="cinema-page">
    <div class="page-header">
      <h2 class="page-title">影院列表</h2>
      <p class="page-subtitle">选择您附近的影院，立即购票观影</p>
    </div>

    <div v-if="cinemas.length === 0">
      <el-empty description="暂无影院" />
    </div>

    <el-card v-for="cinema in cinemas" :key="cinema.cinemaId" class="cinema-card" shadow="hover">
      <div class="cinema-header" @click="toggleCinema(cinema.cinemaId)">
        <div class="cinema-left">
          <div class="cinema-icon">🏢</div>
          <div class="cinema-info">
            <h3 class="cinema-name">{{ cinema.name }}</h3>
            <p class="cinema-meta">{{ cinema.city }} · {{ cinema.address }}</p>
            <p class="cinema-phone" v-if="cinema.phone">📞 {{ cinema.phone }}</p>
          </div>
        </div>
        <div class="cinema-arrow">
          <span class="arrow-text" v-if="expandedCinemaId === cinema.cinemaId">
            收起 ▲
          </span>
          <span class="arrow-text" v-else>展开查看影厅 ▼</span>
        </div>
      </div>

      <div v-if="expandedCinemaId === cinema.cinemaId" class="cinema-detail">
        <div v-if="getCinemaHalls(cinema.cinemaId).length === 0" class="no-data">
          该影院暂无影厅
        </div>

        <div class="hall-tabs">
          <div
            v-for="hall in getCinemaHalls(cinema.cinemaId)"
            :key="hall.hallId"
            class="hall-tab"
            :class="{ active: selectedHallId === hall.hallId }"
            @click="selectHall(hall.hallId)"
          >
            {{ hall.name }}
            <span class="hall-size">({{ hall.seatRows }}×{{ hall.seatCols }})</span>
          </div>
        </div>

        <div v-if="selectedHallId && getHallScreenings(selectedHallId).length === 0" class="no-data">
          该影厅暂无场次
        </div>

        <div v-if="selectedHallId" class="hall-movies">
          <h4 class="hall-movie-title">{{ getHallName(selectedHallId) }} — 放映安排</h4>

          <div v-for="group in getHallMovieGroups(selectedHallId)" :key="group.movieId" class="movie-group">
            <div class="movie-group-header">
              <span class="movie-group-name">{{ group.movieTitle }}</span>
              <el-tag size="small" type="info">{{ group.movieGenre }}</el-tag>
              <span class="movie-group-rating">⭐ {{ group.movieRating || '-' }}</span>
            </div>
            <div class="screening-times">
              <div
                v-for="scr in group.screenings"
                :key="scr.screeningId"
                class="screening-item"
              >
                <div class="scr-date">{{ scr.screeningDate }}</div>
                <div class="scr-time">{{ scr.startTime }} - {{ scr.endTime }}</div>
                <div class="scr-meta">
                  <el-tag size="small">{{ scr.version }}</el-tag>
                  <el-tag size="small" type="info">{{ scr.language }}</el-tag>
                </div>
                <div class="scr-price">¥{{ scr.price }}</div>
                <el-button type="danger" size="small" round @click.stop="goSeats(scr.screeningId)">选座购票</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    return {
      cinemas: [],
      halls: [],
      movies: [],
      screenings: [],
      expandedCinemaId: null,
      selectedHallId: null
    }
  },
  created() {
    this.loadAll()
  },
  methods: {
    async loadAll() {
      try {
        const [cinemasData, hallsData, moviesData, screeningsData] = await Promise.all([
          request.get('/cinema/list'),
          fetch('/api/admin/hall/list').then(r => r.json()),
          request.get('/movie/list'),
          request.get('/screening/list')
        ])
        this.cinemas = cinemasData || []
        this.halls = (hallsData && hallsData.data) ? hallsData.data : []
        this.movies = moviesData || []
        this.screenings = screeningsData || []
      } catch (e) {
        this.$message.error('加载数据失败')
      }
    },
    getCinemaHalls(cinemaId) {
      return this.halls.filter(h => h.cinemaId === cinemaId)
    },
    toggleCinema(cinemaId) {
      if (this.expandedCinemaId === cinemaId) {
        this.expandedCinemaId = null
        this.selectedHallId = null
      } else {
        this.expandedCinemaId = cinemaId
        const halls = this.getCinemaHalls(cinemaId)
        this.selectedHallId = halls.length > 0 ? halls[0].hallId : null
      }
    },
    selectHall(hallId) {
      this.selectedHallId = hallId
    },
    getHallName(hallId) {
      const h = this.halls.find(h => h.hallId === hallId)
      return h ? h.name : ''
    },
    getHallScreenings(hallId) {
      return this.screenings.filter(s => s.hallId === hallId)
    },
    getHallMovieGroups(hallId) {
      const hallScreenings = this.getHallScreenings(hallId)
      const movieMap = {}
      for (const scr of hallScreenings) {
        if (!movieMap[scr.movieId]) {
          const movie = this.movies.find(m => m.movieId === scr.movieId)
          movieMap[scr.movieId] = {
            movieId: scr.movieId,
            movieTitle: movie ? movie.title : '未知电影',
            movieGenre: movie ? movie.genre : '',
            movieRating: movie ? movie.rating : null,
            screenings: []
          }
        }
        movieMap[scr.movieId].screenings.push(scr)
      }
      for (const key in movieMap) {
        movieMap[key].screenings.sort((a, b) => {
          if (a.screeningDate !== b.screeningDate) return a.screeningDate.localeCompare(b.screeningDate)
          return a.startTime.localeCompare(b.startTime)
        })
      }
      return Object.values(movieMap)
    },
    goSeats(screeningId) {
      this.$router.push('/seats/' + screeningId)
    }
  }
}
</script>

<style scoped>
.cinema-page { max-width: 960px; margin: 0 auto; }

.page-header { text-align: center; margin-bottom: 28px; }
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px;
  border: none;
  padding: 0;
}
.page-subtitle { color: var(--text-muted); font-size: 14px; margin: 0; }

.cinema-card { margin-bottom: 16px; border-radius: 12px; }
.cinema-header { display: flex; align-items: center; justify-content: space-between; cursor: pointer; padding: 4px 0; }
.cinema-left { display: flex; align-items: center; gap: 16px; }
.cinema-icon { font-size: 36px; }
.cinema-name { font-size: 18px; font-weight: 600; color: var(--text-primary); margin: 0 0 4px; }
.cinema-meta { color: var(--text-secondary); font-size: 14px; margin: 0; }
.cinema-phone { color: var(--text-muted); font-size: 13px; margin: 4px 0 0; }
.cinema-arrow { flex-shrink: 0; }
.arrow-text { color: var(--accent); font-size: 14px; font-weight: 500; }

.cinema-detail { margin-top: 20px; padding-top: 20px; border-top: 1px solid var(--border-color); }

.hall-tabs { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 20px; }
.hall-tab {
  padding: 8px 18px; border-radius: 8px; cursor: pointer;
  background: var(--bg-card); border: 1px solid var(--border-color);
  font-size: 14px; color: var(--text-secondary); transition: all 0.2s;
  font-weight: 500;
}
.hall-tab:hover { border-color: var(--accent); color: var(--accent); }
.hall-tab.active { background: var(--accent); color: #fff; border-color: var(--accent); }
.hall-size { font-size: 12px; opacity: 0.7; }

.hall-movie-title { color: var(--text-primary); font-size: 16px; margin-bottom: 14px; }
.movie-group { margin-bottom: 18px; padding: 16px; background: var(--bg-primary); border-radius: 10px; }
.movie-group-header { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.movie-group-name { font-size: 16px; font-weight: 600; color: var(--text-primary); }
.movie-group-rating { color: var(--accent); font-size: 13px; }

.screening-times { display: flex; flex-direction: column; gap: 8px; }
.screening-item {
  display: flex; align-items: center; gap: 16px;
  padding: 10px 18px; border-radius: 10px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  transition: border-color 0.2s;
}
.screening-item:hover { border-color: var(--accent); }
.scr-date { font-weight: 500; color: var(--text-primary); min-width: 90px; font-size: 14px; }
.scr-time { color: var(--accent); font-weight: 600; min-width: 100px; }
.scr-meta { display: flex; gap: 4px; }
.scr-price { color: var(--accent); font-weight: 700; font-size: 16px; margin-left: auto; }

.no-data { text-align: center; color: var(--text-muted); padding: 24px 0; }
</style>
