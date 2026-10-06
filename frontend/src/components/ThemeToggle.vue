<template>
  <button
    class="theme-toggle"
    :title="isDark ? '切换到亮色模式' : '切换到暗色模式'"
    @click="toggleTheme"
  >
    <span class="toggle-icon" :class="{ 'is-dark': isDark }">
      <svg v-if="isDark" class="icon moon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
      </svg>
      <svg v-else class="icon sun" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="5" />
        <line x1="12" y1="1" x2="12" y2="3" />
        <line x1="12" y1="21" x2="12" y2="23" />
        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
        <line x1="1" y1="12" x2="3" y2="12" />
        <line x1="21" y1="12" x2="23" y2="12" />
        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
        <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
      </svg>
    </span>
  </button>
</template>

<script>
const STORAGE_KEY = 'theme-preference'

export default {
  name: 'ThemeToggle',
  data() {
    return {
      isDark: false
    }
  },
  mounted() {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      this.isDark = stored === 'dark'
    } else {
      this.isDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    this.applyTheme()
  },
  methods: {
    toggleTheme() {
      this.isDark = !this.isDark
      localStorage.setItem(STORAGE_KEY, this.isDark ? 'dark' : 'light')
      this.applyTheme()
    },
    applyTheme() {
      if (this.isDark) {
        document.documentElement.setAttribute('data-theme', 'dark')
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.setAttribute('data-theme', 'light')
        document.documentElement.classList.remove('dark')
      }
    }
  }
}
</script>

<style scoped>
.theme-toggle {
  position: relative;
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  outline: none;
  overflow: hidden;
}

.theme-toggle:hover {
  background: rgba(255, 255, 255, 0.16);
}

.theme-toggle:focus-visible {
  box-shadow: 0 0 0 2px var(--accent);
}

.toggle-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.icon {
  width: 20px;
  height: 20px;
  color: #e2e8f0;
  animation: icon-enter 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.sun {
  color: #fbbf24;
}

.moon {
  color: #a5b4fc;
}

@keyframes icon-enter {
  0% {
    opacity: 0;
    transform: rotate(-90deg) scale(0.3);
  }
  100% {
    opacity: 1;
    transform: rotate(0deg) scale(1);
  }
}
</style>
