<template>
  <div class="app-wrapper">
    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <router-link to="/" class="logo">
            <span class="logo-icon">🎬</span>
            <span class="logo-text">电影购票</span>
          </router-link>
        </div>
        <div class="header-center">
          <el-menu v-if="isLoggedIn" mode="horizontal" :default-active="activeMenu" router class="nav-menu">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/cinemas">影院</el-menu-item>
            <el-menu-item index="/my-orders">我的订单</el-menu-item>
            <el-menu-item index="/admin">后台管理</el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <ThemeToggle />
          <template v-if="isLoggedIn">
            <span class="user-info">👤 {{ username }}</span>
            <el-button type="danger" plain size="small" @click="handleLogout">退出</el-button>
          </template>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
      <el-footer class="app-footer">
        <p>© 2026 电影购票系统 · 课程设计项目</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import ThemeToggle from './components/ThemeToggle.vue'

export default {
  components: { ThemeToggle },
  data() {
    return {
      username: '',
      isLoggedIn: false
    }
  },
  computed: {
    activeMenu() {
      const path = this.$route.path
      if (path.startsWith('/cinemas')) return '/cinemas'
      if (path.startsWith('/my-orders')) return '/my-orders'
      if (path.startsWith('/admin')) return '/admin'
      return '/'
    }
  },
  mounted() {
    this.checkLoginStatus()
  },
  watch: {
    $route() {
      this.checkLoginStatus()
    }
  },
  methods: {
    async checkLoginStatus() {
      try {
        const res = await fetch('/api/user/current', { credentials: 'include' })
        const data = await res.json()
        if (data.code === 200 && data.data) {
          this.isLoggedIn = true
          this.username = data.data.username
          localStorage.setItem('movieUser', JSON.stringify(data.data))
          if (data.data.isAdmin) {
            localStorage.setItem('movieAdmin', 'true')
          }
        } else {
          this.updateFromLocalStorage()
        }
      } catch (e) {
        this.updateFromLocalStorage()
      }
    },
    updateFromLocalStorage() {
      const userStr = localStorage.getItem('movieUser')
      if (userStr) {
        try {
          const u = JSON.parse(userStr)
          this.isLoggedIn = true
          this.username = u.username
        } catch {
          this.isLoggedIn = false
          this.username = ''
        }
      } else {
        this.isLoggedIn = false
        this.username = ''
      }
    },
    async handleLogout() {
      try {
        await fetch('/api/user/logout', { method: 'POST', credentials: 'include' })
      } catch (e) {
        // 忽略
      }
      localStorage.removeItem('movieUser')
      localStorage.removeItem('movieAdmin')
      this.isLoggedIn = false
      this.username = ''
      this.$router.clearAuthCache?.()
      this.$message.success('已退出登录')
      this.$router.push('/login')
    }
  }
}
</script>

<style>
body { margin: 0; padding: 0; }
.app-wrapper { min-height: 100vh; display: flex; flex-direction: column; }

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-header);
  padding: 0 30px;
  height: 60px !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-left { display: flex; align-items: center; }
.logo { display: flex; align-items: center; gap: 8px; text-decoration: none; }
.logo-icon { font-size: 26px; }
.logo-text { font-size: 20px; font-weight: 700; color: var(--header-text-hover); letter-spacing: 1px; }

.header-center { flex: 1; display: flex; justify-content: center; }
.nav-menu {
  background: transparent !important;
  border-bottom: none !important;
}
.nav-menu .el-menu-item {
  color: var(--header-text);
  font-size: 15px;
  font-weight: 500;
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
  border-bottom: 3px solid transparent;
  transition: color 0.3s, border-color 0.3s;
}
.nav-menu .el-menu-item:hover,
.nav-menu .el-menu-item.is-active {
  color: var(--header-text-hover) !important;
  background: transparent !important;
  border-bottom-color: var(--accent);
}

.header-right { display: flex; align-items: center; gap: 12px; }
.user-info { color: var(--header-text); font-size: 14px; font-weight: 500; }

.app-main { flex: 1; min-height: calc(100vh - 100px); padding: 24px 30px; }

.app-footer {
  text-align: center;
  padding: 16px;
  color: var(--text-muted);
  font-size: 13px;
  border-top: 1px solid var(--border-light);
  background: var(--bg-card);
}
</style>
