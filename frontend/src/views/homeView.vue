<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-home" class="mr-8"></v-icon>
    Home
  </v-card-title>
  <v-divider></v-divider>

  <v-card-text class="my-8">
    <!-- 欢迎区域 -->
    <v-row class="mb-8">
      <v-col cols="12">
        <v-card color="primary" variant="elevated" class="pa-6">
          <div class="text-h3 text-white mb-2">
            Welcome back, {{ userName }}! 👋
          </div>
          <div class="text-h6 text-white opacity-90">
            {{ greeting }}
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 统计信息卡片 -->
    <v-row class="mb-8">
      <v-col cols="12" md="4">
        <v-card color="blue-lighten-5" variant="elevated" class="pa-6">
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-book-multiple" size="40" color="blue-darken-2" class="mr-3"></v-icon>
            <div>
              <div class="text-h4 text-blue-darken-2">{{ totalBooks }}</div>
              <div class="text-body-1 text-grey-darken-1">Total Books</div>
            </div>
          </div>
          <v-progress-linear
            :model-value="100"
            color="blue-darken-2"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card color="green-lighten-5" variant="elevated" class="pa-6">
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-book-check" size="40" color="green-darken-2" class="mr-3"></v-icon>
            <div>
              <div class="text-h4 text-green-darken-2">{{ currentBorrowedBooks }}</div>
              <div class="text-body-1 text-grey-darken-1">Currently Borrowed</div>
            </div>
          </div>
          <v-progress-linear
            :model-value="borrowProgress"
            color="green-darken-2"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card :color="overdueBooks > 0 ? 'red-lighten-5' : 'grey-lighten-4'" variant="elevated" class="pa-6">
          <div class="d-flex align-center mb-2">
            <v-icon
              icon="mdi-alert-circle"
              size="40"
              :color="overdueBooks > 0 ? 'red-darken-2' : 'grey-darken-1'"
              class="mr-3"
            ></v-icon>
            <div>
              <div class="text-h4" :class="overdueBooks > 0 ? 'text-red-darken-2' : 'text-grey-darken-1'">
                {{ overdueBooks }}
              </div>
              <div class="text-body-1 text-grey-darken-1">Overdue Books</div>
            </div>
          </div>
          <v-progress-linear
            :model-value="overdueBooks > 0 ? 100 : 0"
            :color="overdueBooks > 0 ? 'red-darken-2' : 'grey'"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </v-card>
      </v-col>
    </v-row>

    <!-- 快捷操作 -->
    <v-row class="mb-8">
      <v-col cols="12">
        <v-card variant="outlined" class="pa-6">
          <div class="text-h5 mb-4 text-grey-darken-3">
            <v-icon icon="mdi-lightning-bolt" class="mr-2"></v-icon>
            Quick Actions
          </div>
          <v-row>
            <v-col cols="12" sm="6" md="3">
              <v-btn
                block
                color="primary"
                variant="elevated"
                size="large"
                prepend-icon="mdi-book-search"
                @click="navigateTo('/books')"
              >
                Browse Books
              </v-btn>
            </v-col>
            <v-col cols="12" sm="6" md="3">
              <v-btn
                block
                color="success"
                variant="elevated"
                size="large"
                prepend-icon="mdi-book-clock"
                @click="navigateTo('/borrow')"
              >
                My Borrows
              </v-btn>
            </v-col>
            <v-col cols="12" sm="6" md="3">
              <v-btn
                block
                color="info"
                variant="elevated"
                size="large"
                prepend-icon="mdi-cog"
                @click="navigateTo('/settings')"
              >
                Settings
              </v-btn>
            </v-col>
            <v-col cols="12" sm="6" md="3" v-if="isAdmin">
              <v-btn
                block
                color="warning"
                variant="elevated"
                size="large"
                prepend-icon="mdi-shield-account"
                @click="navigateTo('/admin')"
              >
                Admin Panel
              </v-btn>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- 系统信息 -->
    <v-row>
      <v-col cols="12">
        <v-card variant="outlined" class="pa-6">
          <div class="text-h5 mb-4 text-grey-darken-3">
            <v-icon icon="mdi-information" class="mr-2"></v-icon>
            System Information
          </div>
          <v-row>
            <v-col cols="12" md="6">
              <v-list density="compact" bg-color="transparent">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon icon="mdi-account" color="primary"></v-icon>
                  </template>
                  <v-list-item-title>Username: {{ username }}</v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon icon="mdi-shield-check" color="success"></v-icon>
                  </template>
                  <v-list-item-title>Role: {{ isAdmin ? 'Administrator' : 'User' }}</v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon icon="mdi-calendar-clock" color="info"></v-icon>
                  </template>
                  <v-list-item-title>Current Time: {{ currentTime }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6">
              <v-card color="blue-grey-lighten-5" variant="flat" class="pa-4">
                <div class="text-body-1 mb-2">
                  <v-icon icon="mdi-lightbulb-on" color="amber" class="mr-2"></v-icon>
                  <strong>Tip of the Day</strong>
                </div>
                <div class="text-body-2 text-grey-darken-1">
                  {{ dailyTip }}
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
  </v-card-text>

  <!-- 加载提示 -->
  <v-overlay :model-value="loading" class="align-center justify-center">
    <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
  </v-overlay>

  <!-- 消息提示 -->
  <v-snackbar
    v-model="snackbar"
    :color="snackbarColor"
    :timeout="3000"
    location="top"
  >
    {{ snackbarText }}
    <template v-slot:actions>
      <v-btn variant="text" @click="snackbar = false">Close</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

// 类型定义
interface PageResponse<T> {
  content: T[]
  totalElements: number
}

interface Book {
  bookId: number
  title: string
  author: string
}

interface BorrowRecord {
  recordId: string
  status: string
  isOverdue: boolean
}

interface AxiosError {
  response?: { data?: { message?: string } }
}

// 路由和状态管理
const router = useRouter()
const userStore = useUserdataStore()

// UI 控制相关
const loading = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')
const currentTime = ref('')
let timeInterval: number | null = null

// 用户信息
const userName = computed(() => userStore.name || 'Guest')
const username = computed(() => userStore.username || 'unknown')
const isAdmin = computed(() => userStore.admin)

// 统计数据
const totalBooks = ref(0)
const currentBorrowedBooks = ref(0)
const overdueBooks = ref(0)

// 计算属性
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good morning! Have a great day! ☀️'
  if (hour < 18) return 'Good afternoon! Hope you\'re having a productive day! 🌤️'
  return 'Good evening! Time to relax with a good book! 🌙'
})

const borrowProgress = computed(() => {
  if (totalBooks.value === 0) return 0
  return Math.min((currentBorrowedBooks.value / totalBooks.value) * 100, 100)
})

const dailyTip = computed(() => {
  const tips = [
    'Remember to return your books on time to avoid late fees!',
    'You can borrow up to multiple books at once.',
    'Check out the new arrivals in our catalog!',
    'Use the search filters to find books by title, author, or category.',
    'Update your profile in Settings to keep your information current.',
    'Overdue books may incur fines. Return them as soon as possible!',
    'Browse our collection to discover hidden gems!',
    'You can view all your borrow history in the Borrow Records section.'
  ]
  const dayOfYear = Math.floor((Date.now() - new Date(new Date().getFullYear(), 0, 0).getTime()) / 86400000)
  return tips[dayOfYear % tips.length]
})

// 业务逻辑

// 加载图书总数
const loadTotalBooks = async () => {
  try {
    const response = await axios.get<{ success: boolean; data: PageResponse<Book> }>('/api/books', {
      params: { page: 0, size: 1 }
    })
    if (response.data.success && response.data.data) {
      totalBooks.value = response.data.data.totalElements
    }
  } catch (error) {
    console.error('Load total books error:', error)
  }
}

// 加载借阅统计
const loadBorrowStatistics = async () => {
  try {
    // 获取当前借阅的书籍
    const checkedOutResponse = await axios.get<{ success: boolean; data: PageResponse<BorrowRecord> }>(
      '/api/borrow-records/my',
      {
        params: { page: 0, size: 100, status: 'Checked_Out' }
      }
    )
    if (checkedOutResponse.data.success && checkedOutResponse.data.data) {
      const records = checkedOutResponse.data.data.content
      currentBorrowedBooks.value = records.length
      // 计算逾期数量
      overdueBooks.value = records.filter(record => record.isOverdue).length
    }
  } catch (error) {
    console.error('Load borrow statistics error:', error)
  }
}

// 加载所有数据
const loadDashboardData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadTotalBooks(),
      loadBorrowStatistics()
    ])
  } catch (error) {
    console.error('Load dashboard data error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to load dashboard data', 'error')
  } finally {
    loading.value = false
  }
}

// 导航到指定页面
const navigateTo = (path: string) => {
  router.push(path)
}

// 显示消息
const showMessage = (text: string, color: string) => {
  snackbarText.value = text
  snackbarColor.value = color
  snackbar.value = true
}

// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  loadDashboardData()
  updateCurrentTime()
  // 每秒更新时间
  timeInterval = window.setInterval(updateCurrentTime, 1000)
})

onUnmounted(() => {
  if (timeInterval !== null) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.opacity-90 {
  opacity: 0.9;
}
</style>
