<template>
  <div class="admin-login-page">
    <div class="admin-login-card">
      <div class="admin-header">
        <span class="admin-icon">🔐</span>
        <h1 class="admin-title">管理员登录</h1>
        <p class="admin-subtitle">请输入管理员账号密码</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入管理员密码" show-password size="large" />
        </el-form-item>
      </el-form>

      <el-button type="danger" size="large" class="submit-btn" :loading="loading" @click="handleLogin">
        管理员登录
      </el-button>

      <div class="back-row">
        <el-button type="primary" link @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.$refs.formRef.validate()
      } catch {
        return
      }

      this.loading = true
      try {
        const res = await fetch('/api/user/admin-login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(this.form)
        })
        const data = await res.json()

        if (data.code !== 200) {
          // 管理员登录失败，可能包含优惠券消息
          if (data.message && data.message.includes('优惠券已送达')) {
            this.$message.info(data.message)
          } else {
            this.$message.error(data.message || '管理员登录失败')
          }
          this.loading = false
          return
        }

        this.$message.success('管理员登录成功')
        localStorage.setItem('movieAdmin', 'true')
        this.$router.clearAuthCache?.()
        this.$router.push('/admin')
      } catch (e) {
        this.$message.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-login-card {
  width: 420px;
  padding: 40px;
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: var(--shadow);
}

.admin-header {
  text-align: center;
  margin-bottom: 32px;
}

.admin-icon {
  font-size: 48px;
}

.admin-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 12px 0 6px;
}

.admin-subtitle {
  color: var(--text-muted);
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 8px;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  height: 44px;
  font-size: 16px;
}

.back-row {
  text-align: center;
  margin-top: 16px;
}
</style>
