<template>
  <div class="admin-page">
    <h2 class="page-title">管理后台</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="电影管理" name="movie">
        <el-button type="primary" @click="showMovieDialog(null)">添加电影</el-button>
        <el-table :data="movies" style="margin-top:15px" border>
          <el-table-column prop="movieId" label="ID" width="60" />
          <el-table-column prop="title" label="片名" min-width="120" />
          <el-table-column prop="director" label="导演" width="80" />
          <el-table-column prop="genre" label="类型" width="100" />
          <el-table-column prop="duration" label="时长(分)" width="80" />
          <el-table-column prop="releaseDate" label="上映日期" width="100" />
          <el-table-column prop="status" label="状态" width="70">
            <template #default="{ row }">
              <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
                {{ statusMap[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="60" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="showMovieDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteMovie(row.movieId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="影院管理" name="cinema">
        <el-button type="primary" @click="showCinemaDialog(null)">添加影院</el-button>
        <el-table :data="cinemas" style="margin-top:15px" border>
          <el-table-column prop="cinemaId" label="ID" width="60" />
          <el-table-column prop="name" label="名称" min-width="150" />
          <el-table-column prop="city" label="城市" width="80" />
          <el-table-column prop="address" label="地址" min-width="180" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="showCinemaDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteCinema(row.cinemaId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="影厅管理" name="hall">
        <div style="display:flex;align-items:center;gap:12px;">
          <el-select v-model="hallCinemaFilter" placeholder="按影院筛选" clearable style="width:220px" @change="onHallFilterChange">
            <el-option v-for="c in cinemas" :key="c.cinemaId" :value="c.cinemaId" :label="c.name" />
          </el-select>
          <el-button type="primary" @click="showHallDialog(null)" :disabled="!hallCinemaFilter">添加影厅</el-button>
        </div>
        <el-table :data="filteredHalls" style="margin-top:15px" border>
          <el-table-column prop="hallId" label="ID" width="60" />
          <el-table-column label="所属影院" width="170">
            <template #default="{ row }">{{ getCinemaName(row.cinemaId) }}</template>
          </el-table-column>
          <el-table-column prop="name" label="影厅名称" width="160" />
          <el-table-column prop="seatRows" label="排数" width="60" />
          <el-table-column prop="seatCols" label="列数" width="60" />
          <el-table-column label="座位数" width="70">
            <template #default="{ row }">{{ row.seatRows * row.seatCols }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="showHallDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteHall(row.hallId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <p v-if="hallCinemaFilter && filteredHalls.length === 0" style="color:var(--text-muted);margin-top:12px;">该影院暂无影厅</p>
      </el-tab-pane>

      <el-tab-pane label="场次管理" name="screening">
        <div style="display:flex;align-items:center;gap:12px;">
          <el-select v-model="screeningCinemaFilter" placeholder="按影院筛选" clearable style="width:220px" @change="onScreeningFilterChange">
            <el-option v-for="c in cinemas" :key="c.cinemaId" :value="c.cinemaId" :label="c.name" />
          </el-select>
          <el-button type="primary" @click="showScreeningDialog(null)">添加场次</el-button>
        </div>
        <el-table :data="filteredScreenings" style="margin-top:15px" border>
          <el-table-column prop="screeningId" label="ID" width="60" />
          <el-table-column label="所属影院" width="150">
            <template #default="{ row }">{{ getScreeningCinemaName(row) }}</template>
          </el-table-column>
          <el-table-column label="电影" min-width="130">
            <template #default="{ row }">{{ getMovieTitle(row.movieId) }}</template>
          </el-table-column>
          <el-table-column label="影厅" width="130">
            <template #default="{ row }">{{ getHallName(row.hallId) }}</template>
          </el-table-column>
          <el-table-column prop="screeningDate" label="日期" width="100" />
          <el-table-column prop="startTime" label="开始" width="80" />
          <el-table-column prop="endTime" label="结束" width="80" />
          <el-table-column prop="version" label="版本" width="70" />
          <el-table-column prop="language" label="语言" width="60" />
          <el-table-column prop="price" label="票价" width="70" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="showScreeningDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteScreening(row.screeningId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <p v-if="screeningCinemaFilter && filteredScreenings.length === 0" style="color:var(--text-muted);margin-top:12px;">该影院暂无场次</p>
      </el-tab-pane>

      <el-tab-pane label="优惠券管理" name="coupon">
        <div class="coupon-form">
          <el-input v-model="couponUserId" placeholder="目标用户ID" style="width:200px" />
          <el-input v-model="couponAmount" placeholder="优惠券金额" style="width:180px" type="number" />
          <el-input-number v-model="couponCount" :min="1" :max="100" placeholder="数量" style="width:140px" />
          <el-button type="primary" @click="addCoupons">发放优惠券</el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="订单管理" name="order">
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:16px;flex-wrap:wrap;">
          <span class="filter-label">选择用户：</span>
          <el-select v-model="orderUserId" placeholder="请选择用户ID" filterable clearable style="width:200px">
            <el-option v-for="u in userIdList" :key="u" :value="u" :label="'用户 ' + u" />
          </el-select>
          <el-button type="primary" @click="fetchAdminOrders" :disabled="!orderUserId">查询订单</el-button>
          <el-popconfirm title="确认删除该用户的所有订单？此操作不可恢复" @confirm="deleteUserOrders">
            <template #reference>
              <el-button type="danger" plain :disabled="!orderUserId">删除此用户</el-button>
            </template>
          </el-popconfirm>
        </div>
        <el-table :data="allOrders" style="margin-top:15px" border v-if="orderUserId">
          <el-table-column prop="orderId" label="ID" width="60" />
          <el-table-column prop="userId" label="用户ID" width="70" />
          <el-table-column prop="orderNumber" label="订单号" width="180" />
          <el-table-column label="场次" width="70">
            <template #default="{ row }">{{ row.screeningId }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="金额" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'info'">
                {{ row.status === 1 ? '已支付' : row.status === 0 ? '待支付' : '已取消' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-popconfirm title="确认删除此订单？" @confirm="deleteOrderById(row.orderId)">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else-if="orderUserId" description="暂无订单" />
        <p v-if="!orderUserId" style="color:var(--text-muted);">请先选择用户</p>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="movieVisible" :title="editingMovie.movieId ? '编辑电影' : '添加电影'" width="550px">
      <el-form :model="editingMovie" label-width="80px">
        <el-form-item label="片名"><el-input v-model="editingMovie.title" /></el-form-item>
        <el-form-item label="导演"><el-input v-model="editingMovie.director" /></el-form-item>
        <el-form-item label="主演"><el-input v-model="editingMovie.cast" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="editingMovie.genre" placeholder="多个类型用逗号分隔" /></el-form-item>
        <el-form-item label="时长(分)"><el-input-number v-model="editingMovie.duration" :min="1" :max="500" /></el-form-item>
        <el-form-item label="上映日期">
          <el-date-picker v-model="editingMovie.releaseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editingMovie.status" placeholder="选择状态">
            <el-option :value="1" label="即将上映" />
            <el-option :value="2" label="热映中" />
            <el-option :value="3" label="已下映" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-input-number v-model="editingMovie.rating" :min="0" :max="10" :precision="1" :step="0.1" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editingMovie.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="海报">
          <div style="display:flex;align-items:center;gap:12px;">
            <el-upload
              :auto-upload="false"
              :limit="1"
              :on-change="handlePosterChange"
              :file-list="posterFileList"
              list-type="picture"
              accept="image/*"
            >
              <el-button type="primary" size="small">选择图片</el-button>
            </el-upload>
            <img v-if="editingMovie.posterUrl" :src="editingMovie.posterUrl" style="height:60px;border-radius:4px;" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="movieVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMovie" :loading="savingMovie">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="cinemaVisible" :title="editingCinema.cinemaId ? '编辑影院' : '添加影院'">
      <el-form :model="editingCinema" label-width="80px">
        <el-form-item label="名称"><el-input v-model="editingCinema.name" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="editingCinema.city" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="editingCinema.address" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="editingCinema.phone" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cinemaVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCinema">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="hallVisible" :title="editingHall.hallId ? '编辑影厅' : '添加影厅'">
      <el-form :model="editingHall" label-width="80px">
        <el-form-item label="所属影院">
          <el-select v-model="editingHall.cinemaId" placeholder="选择影院">
            <el-option v-for="c in cinemas" :key="c.cinemaId" :value="c.cinemaId" :label="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称"><el-input v-model="editingHall.name" /></el-form-item>
        <el-form-item label="排数"><el-input-number v-model="editingHall.seatRows" :min="1" :max="30" /></el-form-item>
        <el-form-item label="列数"><el-input-number v-model="editingHall.seatCols" :min="1" :max="30" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="hallVisible = false">取消</el-button>
        <el-button type="primary" @click="saveHall">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="screeningVisible" :title="editingScreening.screeningId ? '编辑场次' : '添加场次'" width="500px">
      <el-form :model="editingScreening" label-width="80px">
        <el-form-item label="电影">
          <el-select v-model="editingScreening.movieId" placeholder="选择电影" filterable>
            <el-option v-for="m in movies" :key="m.movieId" :value="m.movieId" :label="m.title + ' (ID:' + m.movieId + ')'" />
          </el-select>
        </el-form-item>
        <el-form-item label="影厅">
          <el-select v-model="editingScreening.hallId" placeholder="选择影厅" filterable>
            <el-option v-for="h in screeningHallOptions" :key="h.hallId" :value="h.hallId" :label="getCinemaName(h.cinemaId) + ' - ' + h.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="editingScreening.screeningDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="editingScreening.startTime" placeholder="选择时间" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="editingScreening.endTime" placeholder="选择时间" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="票价">
          <el-input-number v-model="editingScreening.price" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="版本">
          <el-select v-model="editingScreening.version" placeholder="选择版本">
            <el-option value="2D" label="2D" />
            <el-option value="3D" label="3D" />
            <el-option value="IMAX 2D" label="IMAX 2D" />
            <el-option value="IMAX 3D" label="IMAX 3D" />
            <el-option value="杜比全景声" label="杜比全景声" />
          </el-select>
        </el-form-item>
        <el-form-item label="语言">
          <el-select v-model="editingScreening.language" placeholder="选择语言">
            <el-option value="国语" label="国语" />
            <el-option value="英语" label="英语" />
            <el-option value="粤语" label="粤语" />
            <el-option value="原版" label="原版" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="screeningVisible = false">取消</el-button>
        <el-button type="primary" @click="saveScreening">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    return {
      activeTab: 'movie',
      movies: [], cinemas: [], halls: [], screenings: [],
      movieVisible: false, cinemaVisible: false, hallVisible: false, screeningVisible: false,
      editingMovie: {}, editingCinema: {}, editingHall: {}, editingScreening: {},
      posterFileList: [], savingMovie: false,
      hallCinemaFilter: null,
      screeningCinemaFilter: null,
      statusMap: { 1: '即将上映', 2: '热映中', 3: '已下映' },
      couponUserId: '',
      couponAmount: '',
      couponCount: 1,
      // 订单管理
      orderUserId: null,
      userIdList: [],
      allOrders: []
    }
  },
  computed: {
    filteredHalls() {
      if (!this.hallCinemaFilter) return []
      return this.halls.filter(h => h.cinemaId === this.hallCinemaFilter)
    },
    filteredScreenings() {
      if (!this.screeningCinemaFilter) {
        return this.screenings
      }
      const hallIds = this.halls
        .filter(h => h.cinemaId === this.screeningCinemaFilter)
        .map(h => h.hallId)
      return this.screenings.filter(s => hallIds.includes(s.hallId))
    },
    screeningHallOptions() {
      if (this.screeningCinemaFilter) {
        return this.halls.filter(h => h.cinemaId === this.screeningCinemaFilter)
      }
      return this.halls
    }
  },
  created() { this.loadAll(); this.fetchUserIds() },
  methods: {
    async loadAll() {
      try {
        const [m, c, h, s] = await Promise.all([
          fetch('/api/movie/list').then(r => r.json()),
          fetch('/api/cinema/list').then(r => r.json()),
          fetch('/api/admin/hall/list').then(r => r.json()),
          fetch('/api/screening/list').then(r => r.json())
        ])
        this.movies = m.data || []
        this.cinemas = c.data || []
        this.halls = h.data || []
        this.screenings = s.data || []
        if (this.cinemas.length > 0 && !this.hallCinemaFilter) {
          this.hallCinemaFilter = this.cinemas[0].cinemaId
        }
        if (this.cinemas.length > 0 && !this.screeningCinemaFilter) {
          this.screeningCinemaFilter = this.cinemas[0].cinemaId
        }
      } catch (e) { this.$message.error('加载数据失败') }
    },

    getCinemaName(cinemaId) {
      const c = this.cinemas.find(c => c.cinemaId === cinemaId)
      return c ? c.name : '影院' + cinemaId
    },
    getMovieTitle(movieId) {
      const m = this.movies.find(m => m.movieId === movieId)
      return m ? m.title : '电影' + movieId
    },
    getHallName(hallId) {
      const h = this.halls.find(h => h.hallId === hallId)
      return h ? h.name : '影厅' + hallId
    },
    getScreeningCinemaName(screening) {
      const hall = this.halls.find(h => h.hallId === screening.hallId)
      if (hall) return this.getCinemaName(hall.cinemaId)
      return '-'
    },
    onHallFilterChange() { },
    onScreeningFilterChange() { },

    showMovieDialog(row) {
      this.editingMovie = row ? { ...row, releaseDate: row.releaseDate || '' } : { status: 2 }
      this.posterFileList = []
      this.movieVisible = true
    },
    showCinemaDialog(row) { this.editingCinema = row ? { ...row } : {}; this.cinemaVisible = true },
    showHallDialog(row) {
      this.editingHall = row ? { ...row } : { cinemaId: this.hallCinemaFilter }
      this.hallVisible = true
    },
    showScreeningDialog(row) {
      this.editingScreening = row ? { ...row, screeningDate: row.screeningDate || '' } : {}
      this.screeningVisible = true
    },

    handlePosterChange(file) {
      this.posterFileList = [file]
    },

    async uploadPoster() {
      if (this.posterFileList.length === 0) return null
      const file = this.posterFileList[0].raw
      if (!file) return null
      const formData = new FormData()
      formData.append('file', file)
      const res = await fetch('/api/admin/upload', { method: 'POST', body: formData })
      const data = await res.json()
      if (data.code === 200) {
        return data.data.url
      }
      throw new Error(data.message || '上传失败')
    },

    async saveMovie() {
      this.savingMovie = true
      try {
        if (this.posterFileList.length > 0) {
          const url = await this.uploadPoster()
          if (url) this.editingMovie.posterUrl = url
        }
        const isNew = !this.editingMovie.movieId
        const method = isNew ? 'POST' : 'PUT'
        const res = await fetch('/api/admin/movie', {
          method, headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.editingMovie)
        })
        const data = await res.json()
        if (data.code !== 200) {
          throw new Error(data.message || '保存失败')
        }
        this.movieVisible = false
        await this.loadAll()
        this.$message.success('保存成功')
        if (isNew && data.data && data.data.movieId) {
          this.$confirm('电影已保存，是否立即添加放映场次？', '提示', {
            confirmButtonText: '添加场次',
            cancelButtonText: '稍后',
            type: 'info'
          }).then(() => {
            this.showScreeningDialog(null)
            this.editingScreening.movieId = data.data.movieId
            this.activeTab = 'screening'
          }).catch(() => {})
        }
      } catch (e) {
        this.$message.error(e.message || '保存失败')
      } finally {
        this.savingMovie = false
      }
    },
    async saveCinema() {
      const method = this.editingCinema.cinemaId ? 'PUT' : 'POST'
      await fetch('/api/admin/cinema', {
        method, headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.editingCinema)
      })
      this.cinemaVisible = false; this.loadAll(); this.$message.success('保存成功')
    },
    async saveHall() {
      const method = this.editingHall.hallId ? 'PUT' : 'POST'
      await fetch('/api/admin/hall', {
        method, headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.editingHall)
      })
      this.hallVisible = false; this.loadAll(); this.$message.success('保存成功')
    },
    async saveScreening() {
      try {
        const method = this.editingScreening.screeningId ? 'PUT' : 'POST'
        const res = await fetch('/api/admin/screening', {
          method, headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.editingScreening)
        })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '保存失败')
        this.screeningVisible = false; this.loadAll(); this.$message.success('保存成功')
      } catch (e) {
        this.$message.error(e.message || '保存失败')
      }
    },
    async deleteMovie(id) {
      try {
        const res = await fetch('/api/admin/movie/' + id, { method: 'DELETE' })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '删除失败')
        await this.loadAll()
        this.$message.success('已删除')
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      }
    },
    async deleteCinema(id) {
      try {
        const res = await fetch('/api/admin/cinema/' + id, { method: 'DELETE' })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '删除失败')
        await this.loadAll()
        this.$message.success('已删除')
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      }
    },
    async deleteHall(id) {
      try {
        const res = await fetch('/api/admin/hall/' + id, { method: 'DELETE' })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '删除失败')
        await this.loadAll()
        this.$message.success('已删除')
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      }
    },
    async deleteScreening(id) {
      try {
        const res = await fetch('/api/admin/screening/' + id, { method: 'DELETE' })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '删除失败')
        await this.loadAll()
        this.$message.success('已删除')
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      }
    },
    async addCoupons() {
      if (!this.couponUserId || !this.couponAmount) {
        this.$message.warning('请填写用户ID和优惠券金额')
        return
      }
      const uid = Number(this.couponUserId)
      const amount = parseFloat(this.couponAmount)
      if (!Number.isFinite(uid) || uid <= 0) {
        this.$message.warning('用户ID无效')
        return
      }
      if (!Number.isFinite(amount) || amount <= 0) {
        this.$message.warning('优惠券金额必须大于0')
        return
      }
      try {
        const res = await fetch('/api/admin/coupon/add', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ userId: uid, amount: amount, count: this.couponCount })
        })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '发放失败')
        this.$message.success(`成功为用户 ${uid} 发放 ${this.couponCount} 张 ¥${amount} 优惠券`)
        this.couponUserId = ''
        this.couponAmount = ''
        this.couponCount = 1
      } catch (e) {
        this.$message.error(e.message || '发放失败')
      }
    },
    // ==================== 订单管理方法 ====================
    async fetchUserIds() {
      try {
        const res = await fetch('/api/admin/order/users')
        const data = await res.json()
        if (data.code === 200) {
          this.userIdList = data.data || []
        }
      } catch (e) {
        // 静默
      }
    },
    async fetchAdminOrders() {
      if (!this.orderUserId) return
      try {
        const res = await fetch('/api/admin/order/list-by-user?userId=' + this.orderUserId)
        const data = await res.json()
        if (data.code === 200) {
          this.allOrders = data.data || []
        }
      } catch (e) {
        this.$message.error('加载订单失败')
      }
    },
    async deleteUserOrders() {
      try {
        const res = await fetch('/api/admin/order/user/' + this.orderUserId, { method: 'DELETE' })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '删除失败')
        this.$message.success('已删除用户及所有订单')
        this.allOrders = []
        this.fetchUserIds()
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      }
    },
    async deleteOrderById(orderId) {
      try {
        const res = await fetch('/api/admin/order/' + orderId, { method: 'DELETE' })
        const data = await res.json()
        if (data.code !== 200) throw new Error(data.message || '删除失败')
        this.$message.success('已删除订单')
        this.fetchAdminOrders()
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      }
    }
  }
}
</script>

<style scoped>
.admin-page { max-width: 1280px; margin: 0 auto; }

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 24px;
  border: none;
  padding: 0;
  text-align: center;
}

.coupon-form {
  display: flex; align-items: center; gap: 14px;
  padding: 24px; background: var(--bg-card);
  border-radius: 12px; box-shadow: var(--shadow);
}
</style>
