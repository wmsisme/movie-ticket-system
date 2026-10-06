<template>
  <div class="seat-page">
    <div class="page-header">
      <h2 class="page-title">选择座位</h2>
    </div>

    <div class="auto-bar">
      <span class="auto-label">自动选座：</span>
      <el-input-number v-model="autoCount" :min="1" :max="5" size="small" style="width:120px" />
      <el-button type="primary" size="small" @click="autoSelectSeats" :disabled="autoCount < 1">帮我选座</el-button>
      <span class="limit-tip" :class="{ 'limit-warn': selectedSeatIds.length >= 5 }">
        已选 <strong>{{ selectedSeatIds.length }}</strong> / 5
      </span>
    </div>

    <div class="screen-indicator">
      <span class="screen-text">银 幕</span>
      <div class="screen-line"></div>
    </div>

    <div class="seat-grid" v-if="seatMatrix.length > 0">
      <div v-for="(row, ri) in seatMatrix" :key="ri" class="seat-row">
        <span class="row-label">{{ ri + 1 }}排</span>
        <div
          v-for="(cell, ci) in row"
          :key="ci"
          class="seat-item"
          :class="cell"
          @click="cell === 'available' || cell === 'selected' || cell === 'preferred' ? toggleSeat(ri, ci) : null"
        >
          <span v-if="cell !== 'empty'">{{ ci + 1 }}</span>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无座位数据" />

    <div class="seat-legend">
      <span class="legend-item"><span class="dot preferred-dot"></span>优选座位</span>
      <span class="legend-item"><span class="dot available-dot"></span>可选</span>
      <span class="legend-item"><span class="dot selected-dot"></span>已选</span>
      <span class="legend-item"><span class="dot occupied-dot"></span>已售</span>
    </div>

    <div class="bottom-bar" v-if="selectedSeatIds.length > 0">
      <div class="bottom-info">
        <span>已选 <strong>{{ selectedSeatIds.length }}</strong> / 5 个座位</span>
        <span class="bottom-seats">
          <el-tag v-for="id in selectedSeatIds" :key="id" size="small" type="danger" effect="plain">
            #{{ id }}
          </el-tag>
        </span>
        <span class="bottom-divider">|</span>
        <span>合计 <strong class="total-price">¥{{ totalPrice }}</strong></span>
      </div>
      <el-button type="danger" size="large" round @click="confirmOrder">确认订单</el-button>
    </div>
  </div>
</template>

<script>
const MAX_SEATS = 5

export default {
  data() {
    return {
      allHallSeats: [],
      availableSeats: [],
      selectedSeatIds: [],
      screeningId: null,
      price: 0,
      rows: 0,
      cols: 0,
      autoCount: 1
    }
  },
  computed: {
    seatMatrix() {
      const matrix = []
      for (let r = 1; r <= this.rows; r++) {
        const row = []
        for (let c = 1; c <= this.cols; c++) {
          const seat = this.allHallSeats.find(s => s.rowNum === r && s.colNum === c)
          if (!seat) {
            row.push('empty')
          } else if (this.selectedSeatIds.includes(seat.seatId)) {
            row.push('selected')
          } else if (this.availableSeats.find(s => s.seatId === seat.seatId)) {
            if (this.isPreferredSeat(r, c)) {
              row.push('preferred')
            } else {
              row.push('available')
            }
          } else {
            row.push('occupied')
          }
        }
        matrix.push(row)
      }
      return matrix
    },
    totalPrice() {
      return (this.price * this.selectedSeatIds.length).toFixed(2)
    }
  },
  created() {
    this.screeningId = this.$route.params.screeningId
    this.loadData()
  },
  methods: {
    isPreferredSeat(row, col) {
      const r = this.rows
      const c = this.cols
      const rowStart = Math.floor(r * 0.2) + 1
      const rowEnd = Math.ceil(r * 0.8)
      const colStart = Math.floor(c * 0.2) + 1
      const colEnd = Math.ceil(c * 0.8)
      return row >= rowStart && row <= rowEnd && col >= colStart && col <= colEnd
    },

    getAvailableSeat(r, c) {
      const seat = this.allHallSeats.find(s => s.rowNum === r && s.colNum === c)
      if (!seat) return null
      if (!this.availableSeats.find(s => s.seatId === seat.seatId)) return null
      return seat
    },

    async loadData() {
      try {
        const screeningId = this.screeningId
        const [availableRes, screeningRes] = await Promise.all([
          fetch('/api/seat/available/' + screeningId),
          fetch('/api/screening/' + screeningId)
        ])
        const availableData = await availableRes.json()
        const screeningData = await screeningRes.json()

        this.availableSeats = availableData.data || []

        if (screeningData.code === 200 && screeningData.data) {
          this.price = screeningData.data.price || 0
        }

        if (this.availableSeats.length > 0) {
          const hallId = this.availableSeats[0].hallId
          const hallRes = await fetch('/api/seat/hall/' + hallId)
          const hallData = await hallRes.json()
          this.allHallSeats = hallData.data || []

          this.rows = Math.max(...this.allHallSeats.map(s => s.rowNum), 0)
          this.cols = Math.max(...this.allHallSeats.map(s => s.colNum), 0)
        }
      } catch (e) {
        this.$message.error('加载座位数据失败')
      }
    },

    toggleSeat(ri, ci) {
      const seat = this.allHallSeats.find(s => s.rowNum === ri + 1 && s.colNum === ci + 1)
      if (!seat) return
      const idx = this.selectedSeatIds.indexOf(seat.seatId)
      if (idx >= 0) {
        this.selectedSeatIds.splice(idx, 1)
      } else {
        if (this.selectedSeatIds.length >= MAX_SEATS) {
          this.$message.warning('单次最多只能选择 ' + MAX_SEATS + ' 个座位')
          return
        }
        this.selectedSeatIds.push(seat.seatId)
      }
    },

    autoSelectSeats() {
      const count = this.autoCount
      if (count < 1 || count > MAX_SEATS) return

      this.selectedSeatIds = []

      const grid = {}
      for (let r = 1; r <= this.rows; r++) {
        grid[r] = {}
        for (let c = 1; c <= this.cols; c++) {
          grid[r][c] = this.getAvailableSeat(r, c)
        }
      }

      const centerRow = (this.rows + 1) / 2
      const centerCol = (this.cols + 1) / 2

      const rowsByCenter = Array.from({ length: this.rows }, (_, i) => i + 1)
        .sort((a, b) => Math.abs(a - centerRow) - Math.abs(b - centerRow))

      const blocks = []
      for (const r of rowsByCenter) {
        let blockStart = -1
        for (let c = 1; c <= this.cols + 1; c++) {
          if (c <= this.cols && grid[r][c]) {
            if (blockStart === -1) blockStart = c
          } else {
            if (blockStart !== -1) {
              const blockEnd = c - 1
              if (blockEnd - blockStart + 1 >= count) {
                const blockMid = (blockStart + blockEnd) / 2
                const colScore = Math.abs(blockMid - centerCol)
                const rowScore = Math.abs(r - centerRow)
                blocks.push({ row: r, start: blockStart, end: blockEnd, colScore, rowScore })
              }
              blockStart = -1
            }
          }
        }
      }

      blocks.sort((a, b) => {
        if (Math.abs(a.rowScore - b.rowScore) > 0.5) return a.rowScore - b.rowScore
        return a.colScore - b.colScore
      })

      if (blocks.length > 0) {
        const best = blocks[0]
        let bestStart = best.start
        let bestScore = Infinity
        for (let s = best.start; s <= best.end - count + 1; s++) {
          const segMid = s + (count - 1) / 2
          const score = Math.abs(segMid - centerCol)
          if (score < bestScore) {
            bestScore = score
            bestStart = s
          }
        }
        for (let col = bestStart; col < bestStart + count; col++) {
          this.selectedSeatIds.push(grid[best.row][col].seatId)
        }
        this.$message.success('已自动选择 ' + count + ' 个相邻座位')
        return
      }

      const clusters = []
      const centers = []
      for (const r of rowsByCenter) {
        for (let c = 1; c <= this.cols; c++) {
          if (!grid[r][c]) continue
          const found = centers.find(ci => ci.row === r - 1 && Math.abs(ci.col - c) <= 1)
          if (found) {
            found.seats.push(grid[r][c])
            if (found.seats.length >= count) {
              found.rowScore = Math.abs(found.row - centerRow)
              found.colScore = Math.abs((found.col + (c - (found.seats.length - 1))) / 2 - centerCol)
              clusters.push(found)
            }
          } else if (r > 1 && grid[r - 1] && grid[r - 1][c]) {
            centers.push({ row: r, col: c, seats: [grid[r - 1][c], grid[r][c]], row: r })
          }
        }
      }

      clusters.sort((a, b) => {
        if (Math.abs(a.rowScore - b.rowScore) > 0.5) return a.rowScore - b.rowScore
        return a.colScore - b.colScore
      })

      if (clusters.length > 0) {
        const best = clusters[0]
        const selection = best.seats.slice(0, count)
        this.selectedSeatIds = selection.map(s => s.seatId)
        this.$message.success('已自动选择 ' + count + ' 个座位（就近排布）')
        return
      }

      const allAvailable = []
      for (const r of rowsByCenter) {
        for (let c = 1; c <= this.cols; c++) {
          if (grid[r][c]) {
            allAvailable.push(grid[r][c])
          }
        }
      }
      if (allAvailable.length >= count) {
        this.selectedSeatIds = allAvailable.slice(0, count).map(s => s.seatId)
        this.$message.warning('无法找到相邻座位，已选择最优位置')
      } else {
        this.$message.warning('可选座位不足 ' + count + ' 个')
      }
    },

    confirmOrder() {
      if (this.selectedSeatIds.length === 0) {
        this.$message.warning('请至少选择一个座位')
        return
      }
      this.$router.push({
        path: '/order/confirm',
        query: {
          screeningId: this.screeningId,
          seatIds: this.selectedSeatIds.join(','),
          price: this.price,
          count: this.selectedSeatIds.length
        }
      })
    }
  }
}
</script>

<style scoped>
.seat-page { max-width: 960px; margin: 0 auto; }

.page-header { text-align: center; margin-bottom: 16px; }
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0; border: none; padding: 0;
}

.auto-bar {
  display: flex; align-items: center; justify-content: center;
  gap: 12px; margin-bottom: 20px; flex-wrap: wrap;
}
.auto-label { font-size: 14px; color: var(--text-secondary); }
.limit-tip {
  font-size: 15px; color: var(--text-secondary); font-weight: 500;
  background: var(--bg-card);
  padding: 4px 14px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
}
.limit-tip.limit-warn { color: var(--accent); border-color: var(--accent); }

.screen-indicator { text-align: center; margin: 0 40px 24px; }
.screen-text {
  color: var(--text-muted);
  font-size: 13px;
  letter-spacing: 16px;
  display: inline-block;
}
.screen-line {
  margin-top: 6px;
  height: 3px;
  background: linear-gradient(90deg, transparent, var(--border-color), transparent);
  border-radius: 2px;
}

.seat-grid { text-align: center; max-width: 900px; margin: 0 auto; }
.seat-row { display: flex; align-items: center; justify-content: center; margin-bottom: 8px; }
.row-label { width: 44px; font-size: 12px; color: var(--text-muted); text-align: right; margin-right: 6px; font-weight: 500; }
.seat-item {
  width: 34px; height: 30px; margin: 2px; border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; cursor: default; user-select: none;
  transition: transform 0.15s, opacity 0.15s;
  font-weight: 500;
}

.seat-item.preferred { background: #f0ad4e; color: #fff; cursor: pointer; }
.seat-item.preferred:hover { opacity: 0.85; transform: scale(1.12); }
.seat-item.available { background: var(--seat-available); color: #fff; cursor: pointer; }
.seat-item.available:hover { opacity: 0.85; transform: scale(1.12); }
.seat-item.selected { background: var(--accent); color: #fff; cursor: pointer; transform: scale(1.08); }
.seat-item.occupied { background: var(--seat-occupied); color: var(--seat-occupied-text); cursor: not-allowed; }
.seat-item.empty { background: transparent; }

.seat-legend {
  display: flex; justify-content: center; gap: 24px;
  margin: 24px 0; font-size: 13px; color: var(--text-secondary);
  flex-wrap: wrap;
}
.legend-item { display: flex; align-items: center; gap: 6px; }
.dot { display: inline-block; width: 16px; height: 16px; border-radius: 4px; }
.preferred-dot { background: #f0ad4e; }
.available-dot { background: var(--seat-available); }
.selected-dot { background: var(--accent); }
.occupied-dot { background: var(--seat-occupied); border: 1px solid var(--border-color); }

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: var(--bg-bottom-bar);
  border-top: 1px solid var(--border-light);
  padding: 14px 40px;
  display: flex; align-items: center; justify-content: space-between;
  z-index: 100;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.08);
}
.bottom-info { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; font-size: 15px; color: var(--text-secondary); }
.bottom-seats { display: flex; gap: 4px; }
.bottom-divider { color: var(--border-color); }
.total-price { color: var(--accent); font-size: 22px; }
</style>
