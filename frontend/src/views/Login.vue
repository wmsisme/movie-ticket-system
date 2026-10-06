<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <span class="login-icon">🎬</span>
        <h1 class="login-title">电影购票系统</h1>
        <p class="login-subtitle">{{ isRegister ? '创建新账号' : '登录您的账号' }}</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>
        <el-form-item v-if="isRegister" label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password size="large" />
        </el-form-item>
      </el-form>

      <el-button type="danger" size="large" class="submit-btn" :loading="loading" @click="handleSubmit">
        {{ isRegister ? '注册' : '登录' }}
      </el-button>

      <div class="switch-row">
        <el-button type="primary" link @click="toggleMode">
          {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      isRegister: false,
      loading: false,
      form: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在2-20个字符之间', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 3, max: 30, message: '密码长度在3-30个字符之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    toggleMode() {
      this.isRegister = !this.isRegister
      this.form.confirmPassword = ''
      this.$nextTick(() => {
        this.$refs.formRef?.clearValidate()
      })
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch {
        return
      }

      this.loading = true
      try {
        const url = this.isRegister ? '/api/user/register' : '/api/user/login'
        const body = {
          username: this.form.username,
          password: this.form.password
        }
        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(body)
        })
        const data = await res.json()

        if (data.code !== 200) {
          this.$message.error(data.message || '操作失败')
          this.loading = false
          return
        }

        this.$message.success(this.isRegister ? '注册成功' : '登录成功')
        localStorage.setItem('movieUser', JSON.stringify({ userId: data.data.userId, username: data.data.username }))
        localStorage.removeItem('movieAdmin')
        this.$router.clearAuthCache?.()
        this.$router.push('/')
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
.login-page {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 420px;
  padding: 40px;
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: var(--shadow);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-icon {
  font-size: 48px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 12px 0 6px;
}

.login-subtitle {
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

.switch-row {
  text-align: center;
  margin-top: 16px;
}
</style>
