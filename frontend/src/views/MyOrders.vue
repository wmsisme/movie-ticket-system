<template>
  <div class="my-orders">
    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
    </div>

    <el-empty v-if="orders.length === 0 && !loading" description="暂无订单" />

    <el-card v-for="order in orders" :key="order.orderId" class="order-card" shadow="hover">
      <div class="order-header">
        <div class="order-number">
          <span class="num-label">订单号</span>
          <span class="num-value">{{ order.orderNumber }}</span>
        </div>
        <el-tag :type="order.status === 1 ? 'success' : order.status === 0 ? 'warning' : 'info'" size="large">
          {{ order.status === 1 ? '已支付' : order.status === 0 ? '待支付' : '已取消' }}
        </el-tag>
      </div>

      <div class="order-body">
        <div class="order-movie">
          <span class="movie-icon">🎬</span>
          <span class="movie-name">{{ getMovieTitle(order.screeningId) }}</span>
        </div>
        <div class="order-meta">
          <span>{{ getScreeningInfo(order.screeningId) }}</span>
        </div>
        <div class="order-price">
          <span class="price-label">实付金额</span>
          <span class="price-value">¥{{ order.totalPrice }}</span>
        </div>
      </div>

      <div class="order-footer">
        <span class="order-time">{{ order.createTime }}</span>
        <div class="order-actions" v-if="order.status === 0">
          <el-button type="success" size="default" @click="showPayDialog(order)">选择优惠券支付</el-button>
          <el-button type="primary" size="default" @click="payOrder(order.orderId)">直接支付</el-button>
          <el-button type="danger" size="default" plain @click="cancelOrder(order.orderId)">取消订单</el-button>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="payDialogVisible" title="选择优惠券支付" width="480px">
      <div class="pay-dialog-body">
        <div class="pay-summary">
          <span>原价：</span>
          <span class="pay-original">¥{{ payingOrder?.totalPrice }}</span>
        </div>
        <div v-if="availableCoupons.length > 0" class="pay-coupon-section">
          <p class="pay-coupon-label">选择优惠券：</p>
          <div class="pay-coupon-list">
            <div
              v-for="c in availableCoupons"
              :key="c.couponId"
              class="pay-coupon-item"
              :class="{ active: selectedPayCouponId === c.couponId }"
              @click="selectedPayCouponId = selectedPayCouponId === c.couponId ? null : c.couponId"
            >
              <span class="c-amount">¥{{ c.amount }}</span>
              <span class="c-check" v-if="selectedPayCouponId === c.couponId">✓</span>
            </div>
          </div>
          <div class="pay-total" v-if="selectedPayCouponId">
            优惠后：<span class="pay-final">¥{{ payAfterDiscount }}</span>
          </div>
        </div>
        <el-empty v-else description="暂无可用优惠券" :image-size="80" />
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    return {
      orders: [],
      loading: false,
      movies: [],
      screenings: [],
      payDialogVisible: false,
      payingOrder: null,
      availableCoupons: [],
      selectedPayCouponId: null
    }
  },
  computed: {
    payAfterDiscount() {
      if (!this.payingOrder || !this.selectedPayCouponId) return '0.00'
      const coupon = this.availableCoupons.find(c => c.couponId === this.selectedPayCouponId)
      if (!coupon) return parseFloat(this.payingOrder.totalPrice).toFixed(2)
      return Math.max(0, parseFloat(this.payingOrder.totalPrice) - parseFloat(coupon.amount)).toFixed(2)
    }
  },
  created() {
    this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      this.loading = true
      try {
        const [ordersData, moviesData, screeningsData] = await Promise.all([
          request.get('/order/my'),
          request.get('/movie/list'),
          request.get('/screening/list')
        ])
        this.orders = ordersData || []
        this.movies = moviesData || []
        this.screenings = screeningsData || []
      } catch (e) {
        this.$message.error('加载订单失败')
      } finally {
        this.loading = false
      }
    },
    getMovieTitle(screeningId) {
      const scr = this.screenings.find(s => s.screeningId === screeningId)
      if (scr) {
        const movie = this.movies.find(m => m.movieId === scr.movieId)
        return movie ? movie.title : '未知电影'
      }
      return '场次' + screeningId
    },
    getScreeningInfo(screeningId) {
      const scr = this.screenings.find(s => s.screeningId === screeningId)
      return scr ? `${scr.screeningDate} ${scr.startTime}-${scr.endTime} ${scr.version}/${scr.language}` : ''
    },
    async payOrder(orderId) {
      try {
        await request.put('/order/pay/' + orderId)
        this.$message.success('支付成功')
        this.fetchOrders()
      } catch (e) { this.$message.error(e.message || '支付失败') }
    },
    async showPayDialog(order) {
      this.payingOrder = order
      this.selectedPayCouponId = null
      this.payDialogVisible = true
      try {
        this.availableCoupons = await request.get('/order/coupons')
      } catch (e) {
        this.availableCoupons = []
      }
    },
    async confirmPay() {
      try {
        let url = '/order/pay/' + this.payingOrder.orderId
        if (this.selectedPayCouponId) {
          url += '?couponId=' + this.selectedPayCouponId
        }
        await request.put(url)
        this.$message.success('支付成功')
        this.payDialogVisible = false
        this.fetchOrders()
      } catch (e) {
        this.$message.error(e.message || '支付失败')
      }
    },
    async cancelOrder(orderId) {
      try {
        await request.put('/order/cancel/' + orderId)
        this.$message.success('已取消')
        this.fetchOrders()
      } catch (e) { this.$message.error(e.message || '取消失败') }
    }
  }
}
</script>

<style scoped>
.my-orders { max-width: 960px; margin: 0 auto; }

.page-header { text-align: center; margin-bottom: 28px; }
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  border: none;
  padding: 0;
}

.filter-bar { display: flex; align-items: center; gap: 14px; margin-bottom: 24px; flex-wrap: wrap; }
.filter-label { font-size: 15px; color: var(--text-secondary); font-weight: 500; }

.order-card { margin-bottom: 16px; border-radius: 12px; overflow: hidden; }
.order-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.num-label { color: var(--text-muted); font-size: 13px; margin-right: 8px; }
.num-value { color: var(--text-primary); font-weight: 600; font-size: 15px; }

.order-body { display: flex; align-items: center; gap: 18px; flex-wrap: wrap; margin-bottom: 14px; }
.order-movie { display: flex; align-items: center; gap: 8px; }
.movie-icon { font-size: 20px; }
.movie-name { font-size: 17px; font-weight: 600; color: var(--text-primary); }
.order-meta { color: var(--text-muted); font-size: 13px; }
.order-price { margin-left: auto; text-align: right; }
.price-label { display: block; color: var(--text-muted); font-size: 12px; }
.price-value { color: var(--accent); font-size: 22px; font-weight: 700; }

.order-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 14px; border-top: 1px solid var(--border-light); }
.order-time { color: var(--text-muted); font-size: 13px; }
.order-actions { display: flex; gap: 8px; }

/* 支付弹窗 */
.pay-dialog-body { padding: 0 4px; }
.pay-summary { display: flex; align-items: center; gap: 8px; margin-bottom: 20px; font-size: 16px; color: var(--text-secondary); }
.pay-original { color: var(--accent); font-size: 20px; font-weight: 700; }
.pay-coupon-label { color: var(--text-secondary); font-size: 14px; margin-bottom: 10px; }
.pay-coupon-list { display: flex; gap: 10px; flex-wrap: wrap; }
.pay-coupon-item {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 16px; border-radius: 8px;
  border: 2px solid var(--border-color);
  cursor: pointer; transition: all 0.2s;
  background: var(--bg-card);
}
.pay-coupon-item:hover { border-color: var(--accent); }
.pay-coupon-item.active { border-color: var(--accent); background: rgba(233,69,96,0.05); }
.c-amount { font-size: 18px; font-weight: 700; color: var(--accent); }
.c-check { color: var(--accent); font-weight: 700; }
.pay-total { margin-top: 16px; font-size: 16px; color: var(--text-secondary); }
.pay-final { color: var(--accent); font-size: 22px; font-weight: 700; }
</style>
