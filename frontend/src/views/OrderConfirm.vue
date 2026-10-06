<template>
  <div class="order-confirm">
    <div class="page-header">
      <h2 class="page-title">确认订单</h2>
    </div>

    <div class="confirm-card">
      <div class="order-section">
        <h3 class="section-title">订单信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">场次编号</span>
            <span class="info-value">#{{ screeningId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">座位数量</span>
            <span class="info-value">{{ count }} 个</span>
          </div>
          <div class="info-item">
            <span class="info-label">单价</span>
            <span class="info-value">¥{{ price }}</span>
          </div>
          <div class="info-item highlight">
            <span class="info-label">总价</span>
            <span class="info-value price">¥{{ finalPrice }}</span>
          </div>
        </div>
      </div>

      <div class="order-section" v-if="currentUser">
        <h3 class="section-title">用户信息</h3>
        <span class="user-display">👤 {{ currentUser.username }}（ID: {{ currentUser.userId }}）</span>
      </div>

      <div class="order-section" v-if="coupons.length > 0">
        <h3 class="section-title">
          选择优惠券
          <span class="section-badge">{{ coupons.length }}张可用</span>
        </h3>
        <div class="coupon-list">
          <div
            v-for="c in coupons"
            :key="c.couponId"
            class="coupon-item"
            :class="{ selected: selectedCouponId === c.couponId }"
            @click="selectCoupon(c.couponId)"
          >
            <div class="coupon-left">
              <div class="coupon-amount">¥{{ c.amount }}</div>
              <div class="coupon-label">优惠券</div>
            </div>
            <div class="coupon-right">
              <div class="coupon-check" v-if="selectedCouponId === c.couponId">✓</div>
              <div class="coupon-hint" v-else>点击使用</div>
            </div>
          </div>
        </div>
        <p v-if="selectedCouponId" class="discount-info">
          使用优惠券后：<span class="original-price">¥{{ totalPrice }}</span>
          → <span class="final-price-display">¥{{ finalPrice }}</span>
        </p>
      </div>

      <div class="order-section" v-else>
        <el-button text type="primary" @click="loadCoupons">查看可用优惠券</el-button>
      </div>

      <div class="submit-area">
        <el-button type="danger" size="large" round @click="submitOrder" class="submit-btn">提交订单</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import request from '../api'

export default {
  data() {
    const q = this.$route.query
    return {
      screeningId: q.screeningId,
      seatIds: q.seatIds ? q.seatIds.split(',').map(Number) : [],
      price: Number(q.price) || 0,
      count: Number(q.count) || 0,
      currentUser: null,
      coupons: [],
      selectedCouponId: null
    }
  },
  computed: {
    totalPrice() { return (this.price * this.count).toFixed(2) },
    finalPrice() {
      if (this.selectedCouponId) {
        const coupon = this.coupons.find(c => c.couponId === this.selectedCouponId)
        if (coupon) {
          const p = parseFloat(this.totalPrice) - parseFloat(coupon.amount)
          return Math.max(0, p).toFixed(2)
        }
      }
      return this.totalPrice
    }
  },
  created() {
    this.loadCurrentUser()
    this.loadCoupons()
  },
  methods: {
    async loadCurrentUser() {
      try {
        const res = await fetch('/api/user/current', { credentials: 'include' })
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.currentUser = data.data
        }
      } catch (e) {
        this.$message.error('获取用户信息失败')
      }
    },
    async loadCoupons() {
      try {
        this.coupons = await request.get('/order/coupons')
        if (!this.coupons || this.coupons.length === 0) {
          this.$message.info('暂无可用优惠券')
        } else {
          this.$message.success('已加载 ' + this.coupons.length + ' 张优惠券')
        }
      } catch (e) {
        this.$message.error('加载优惠券失败')
      }
    },
    selectCoupon(couponId) {
      this.selectedCouponId = this.selectedCouponId === couponId ? null : couponId
    },
    async submitOrder() {
      if (!this.currentUser) {
        this.$message.warning('请先登录')
        return
      }
      const sid = Number(this.screeningId)
      if (!Number.isFinite(sid) || sid <= 0) {
        this.$message.warning('场次信息无效，请重新选择场次')
        return
      }
      if (!this.seatIds.length) {
        this.$message.warning('未选择座位')
        return
      }
      try {
        await request.post('/order/create', {
          screeningId: sid,
          seatIds: this.seatIds
        })
        this.$message.success('订单创建成功')
        this.$router.push('/my-orders')
      } catch (e) {
        this.$message.error(e.message || '创建订单失败')
      }
    }
  }
}
</script>

<style scoped>
.order-confirm { max-width: 700px; margin: 0 auto; }

.page-header { text-align: center; margin-bottom: 28px; }
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  border: none;
  padding: 0;
}

.confirm-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 28px;
  box-shadow: var(--shadow);
}

.order-section { margin-bottom: 24px; }
.user-display { font-size: 15px; color: var(--text-secondary); font-weight: 500; }
.section-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.section-badge {
  font-size: 13px;
  font-weight: 400;
  color: var(--accent);
  background: rgba(233, 69, 96, 0.1);
  padding: 2px 10px;
  border-radius: 12px;
}

.info-grid { display: flex; flex-wrap: wrap; gap: 16px; }
.info-item {
  flex: 1;
  min-width: 140px;
  padding: 12px 16px;
  background: var(--bg-primary);
  border-radius: 10px;
  display: flex; flex-direction: column; gap: 4px;
}
.info-label { font-size: 13px; color: var(--text-muted); }
.info-value { font-size: 16px; font-weight: 600; color: var(--text-primary); }
.info-item.highlight { background: rgba(233, 69, 96, 0.08); }
.price { color: var(--accent); font-size: 22px; }

.coupon-list { display: flex; gap: 12px; flex-wrap: wrap; }
.coupon-item {
  display: flex; align-items: stretch;
  border-radius: 10px; overflow: hidden;
  cursor: pointer; border: 2px solid var(--border-color);
  transition: all 0.2s; min-width: 140px;
}
.coupon-item:hover { border-color: var(--accent); }
.coupon-item.selected { border-color: var(--accent); box-shadow: 0 0 0 2px rgba(233, 69, 96, 0.2); }

.coupon-left {
  background: var(--accent);
  color: #fff;
  padding: 12px 16px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  min-width: 90px;
}
.coupon-amount { font-size: 24px; font-weight: 700; }
.coupon-label { font-size: 12px; opacity: 0.8; margin-top: 2px; }

.coupon-right {
  padding: 12px 14px;
  display: flex; align-items: center; justify-content: center;
  background: var(--bg-card);
}
.coupon-check { color: var(--accent); font-size: 22px; font-weight: 700; }
.coupon-hint { color: var(--text-muted); font-size: 13px; }

.discount-info { margin-top: 12px; color: var(--text-secondary); font-size: 14px; }
.original-price { text-decoration: line-through; color: var(--text-muted); }
.final-price-display { color: var(--accent); font-weight: 700; font-size: 16px; }

.submit-area { text-align: center; padding-top: 10px; }
.submit-btn { padding: 14px 60px !important; font-size: 18px !important; font-weight: 600 !important; }
</style>
