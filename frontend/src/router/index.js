import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { noAuth: true } },
  { path: '/admin-login', name: 'AdminLogin', component: () => import('../views/AdminLogin.vue'), meta: { requiresAuth: true } },
  { path: '/', name: 'MovieList', component: () => import('../views/MovieList.vue'), meta: { requiresAuth: true } },
  { path: '/movie/:movieId', name: 'MovieDetail', component: () => import('../views/MovieDetail.vue'), meta: { requiresAuth: true } },
  { path: '/cinemas', name: 'CinemaList', component: () => import('../views/CinemaList.vue'), meta: { requiresAuth: true } },
  { path: '/sessions/:movieId', name: 'SessionList', component: () => import('../views/SessionList.vue'), meta: { requiresAuth: true } },
  { path: '/seats/:screeningId', name: 'SeatSelection', component: () => import('../views/SeatSelection.vue'), meta: { requiresAuth: true } },
  { path: '/order/confirm', name: 'OrderConfirm', component: () => import('../views/OrderConfirm.vue'), meta: { requiresAuth: true } },
  { path: '/my-orders', name: 'MyOrders', component: () => import('../views/MyOrders.vue'), meta: { requiresAuth: true } },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue'), meta: { requiresAuth: true, requiresAdmin: true } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 登录状态缓存（避免每次导航都请求后端）
let loginCache = null
let loginCacheTime = 0
const CACHE_TTL = 30000 // 30秒内不重复请求

async function verifySession() {
  const now = Date.now()
  if (loginCache && (now - loginCacheTime) < CACHE_TTL) {
    return loginCache
  }
  try {
    const res = await fetch('/api/user/current', { credentials: 'include' })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      loginCache = data.data
      loginCacheTime = now
      localStorage.setItem('movieUser', JSON.stringify(data.data))
      return data.data
    }
    loginCache = null
    loginCacheTime = 0
    localStorage.removeItem('movieUser')
    localStorage.removeItem('movieAdmin')
    return null
  } catch (e) {
    const userStr = localStorage.getItem('movieUser')
    if (userStr) {
      try { return JSON.parse(userStr) } catch {}
    }
    return null
  }
}

// 导航守卫：检查登录状态
router.beforeEach(async (to, from, next) => {
  // 不需要登录的页面直接放行
  if (to.meta.noAuth) {
    const userInfo = await verifySession()
    if (userInfo) {
      next('/')
      return
    }
    next()
    return
  }

  // 需要登录：验证 session
  const userInfo = await verifySession()

  if (!userInfo) {
    next('/login')
    return
  }

  // 需要管理员权限
  if (to.meta.requiresAdmin) {
    if (!userInfo.isAdmin) {
      const adminStr = localStorage.getItem('movieAdmin')
      if (!adminStr) {
        next('/admin-login')
        return
      }
      try {
        const adminRes = await fetch('/api/admin/check', { credentials: 'include' })
        const adminData = await adminRes.json()
        if (adminData.code !== 200) {
          localStorage.removeItem('movieAdmin')
          next('/admin-login')
          return
        }
      } catch {
        next('/admin-login')
        return
      }
    }
  }

  if (to.path === '/admin-login' && userInfo.isAdmin) {
    next('/admin')
    return
  }

  next()
 })

// 供外部调用的缓存刷新方法
router.clearAuthCache = () => {
  loginCache = null
  loginCacheTime = 0
}

export default router
