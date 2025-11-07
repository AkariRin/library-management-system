<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-book-search" class="mr-8"></v-icon>
    Book Search
  </v-card-title>
  <v-divider></v-divider>

  <v-card-text class="my-8">
    <!-- 搜索过滤器区域 -->
    <v-expansion-panels class="mb-6">
      <v-expansion-panel>
        <v-expansion-panel-title>
          <v-icon icon="mdi-filter" class="mr-2"></v-icon>
          Search Filters
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="searchFilters.title"
                label="Book Title"
                variant="underlined"
                prepend-inner-icon="mdi-book"
                clearable
                @keyup.enter="searchBooks"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="searchFilters.author"
                label="Author"
                variant="underlined"
                prepend-inner-icon="mdi-account-edit"
                clearable
                @keyup.enter="searchBooks"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="searchFilters.category"
                label="Category ID"
                variant="underlined"
                prepend-inner-icon="mdi-tag"
                type="number"
                clearable
                @keyup.enter="searchBooks"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="searchFilters.sortBy"
                label="Sort By"
                variant="underlined"
                prepend-inner-icon="mdi-sort"
                :items="sortOptions"
                item-title="text"
                item-value="value"
              ></v-select>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" class="d-flex ga-4">
              <v-btn color="primary" variant="elevated" prepend-icon="mdi-magnify" @click="searchBooks" :loading="loading">
                Search
              </v-btn>
              <v-btn color="grey" variant="text" prepend-icon="mdi-refresh" @click="resetFilters">
                Reset
              </v-btn>
            </v-col>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

    <!-- 图书列表数据表格 -->
    <v-data-table
      :headers="headers"
      :items="books"
      :loading="loading"
      :items-per-page="pageSize"
      hide-default-footer
      class="elevation-2"
    >
      <template v-slot:item.price="{ item }">
        ¥{{ item.price ? item.price.toFixed(2) : '0.00' }}
      </template>
      <template v-slot:item.availability="{ item }">
        <v-chip :color="item.availableCopies > 0 ? 'success' : 'error'" variant="flat" size="small">
          {{ item.availableCopies }} / {{ item.totalCopies }}
        </v-chip>
      </template>
      <template v-slot:item.actions="{ item }">
        <v-btn color="info" variant="text" size="small" @click="viewBookDetails(item)">
          Details
        </v-btn>
      </template>
      <template v-slot:bottom>
        <div class="text-center pa-4">
          <v-pagination
            v-model="currentPage"
            :length="totalPages"
            :total-visible="7"
            @update:modelValue="onPageChange"
          ></v-pagination>
          <div class="text-caption text-grey-darken-1 mt-2">
            Total: {{ totalElements }} books | Page {{ currentPage }} of {{ totalPages }}
          </div>
        </div>
      </template>
    </v-data-table>
  </v-card-text>

  <!-- 图书详情对话框 -->
  <v-dialog v-model="detailDialog" max-width="800px">
    <v-card>
      <v-card-title class="text-h5 bg-primary">
        <v-icon icon="mdi-book-open-page-variant" class="mr-2"></v-icon>
        Book Details
      </v-card-title>
      <v-card-text class="pa-6" v-if="selectedBook">
        <v-row>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Book ID</div>
              <div class="text-h6">{{ selectedBook.bookId }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">ISBN</div>
              <div class="text-h6">{{ selectedBook.isbn || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Title</div>
              <div class="text-h5">{{ selectedBook.title }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Author</div>
              <div class="text-h6">{{ selectedBook.author }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Publisher</div>
              <div class="text-h6">{{ selectedBook.publisher || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Publish Date</div>
              <div class="text-h6">{{ selectedBook.publishDate || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Category</div>
              <div class="text-h6">{{ selectedBook.category || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Price</div>
              <div class="text-h6">¥{{ selectedBook.price ? selectedBook.price.toFixed(2) : '0.00' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Availability</div>
              <div class="text-h6">
                <v-chip :color="selectedBook.availableCopies > 0 ? 'success' : 'error'" variant="elevated">
                  {{ selectedBook.availableCopies }} available / {{ selectedBook.totalCopies }} total
                </v-chip>
              </div>
            </div>
          </v-col>
          <v-col cols="12">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">Summary</div>
              <div class="text-body-1">{{ selectedBook.summary || 'No summary available' }}</div>
            </div>
          </v-col>
        </v-row>

        <v-divider class="my-4"></v-divider>

        <!-- 可用副本列表 -->
        <div class="mb-4">
          <div class="text-h6 mb-3">Available Copies</div>
          <v-btn
            color="primary"
            variant="text"
            size="small"
            prepend-icon="mdi-refresh"
            @click="loadBookCopies"
            :loading="loadingCopies"
            class="mb-3"
          >
            Load Copies
          </v-btn>

          <v-data-table
            v-if="bookCopies.length > 0"
            :headers="copiesHeaders"
            :items="bookCopies"
            :loading="loadingCopies"
            density="compact"
            class="elevation-1"
          >
            <template v-slot:item.status="{ item }">
              <v-chip :color="getStatusColor(item.status)" size="small" variant="flat">
                {{ item.statusDescription }}
              </v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
              <v-btn
                v-if="item.status === 'Available'"
                color="success"
                variant="text"
                size="small"
                @click="borrowBook(item)"
                :disabled="borrowingItemId === item.itemId"
              >
                Borrow
              </v-btn>
            </template>
          </v-data-table>
          <v-alert v-else-if="!loadingCopies" type="info" variant="tonal" class="mt-2">
            Click "Load Copies" to view available copies
          </v-alert>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="closeDetailDialog">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

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
import { ref, onMounted } from 'vue'
import axios from 'axios'

// ==================== 类型定义 ====================
interface Book {
  bookId: number
  isbn?: string
  title: string
  author: string
  publisher?: string
  publishDate?: string
  category?: number
  summary?: string
  price?: number
  totalCopies: number
  availableCopies: number
}

interface BookCopy {
  itemId: number
  bookId: number
  bookTitle: string
  bookAuthor: string
  barcode?: string
  location?: string
  status: string
  statusDescription: string
  acquisitionDate?: string
  acquisitionPrice?: number
  notes?: string
}

interface PageResponse<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  empty: boolean
}

interface AxiosError {
  response?: { data?: { message?: string } }
}

// ==================== UI 控制相关 ====================
const loading = ref(false)
const loadingCopies = ref(false)
const borrowingItemId = ref<number | null>(null)
const detailDialog = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

// ==================== 数据相关 ====================
const books = ref<Book[]>([])
const selectedBook = ref<Book | null>(null)
const bookCopies = ref<BookCopy[]>([])

const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const searchFilters = ref({
  title: '',
  author: '',
  category: null as number | null,
  sortBy: 'bookId'
})

const sortOptions = [
  { text: 'Book ID', value: 'bookId' },
  { text: 'Title', value: 'title' },
  { text: 'Author', value: 'author' },
  { text: 'Publish Date', value: 'publishDate' },
  { text: 'Price', value: 'price' }
]

const headers = [
  { title: 'ID', key: 'bookId', sortable: false },
  { title: 'Title', key: 'title', sortable: false },
  { title: 'Author', key: 'author', sortable: false },
  { title: 'Publisher', key: 'publisher', sortable: false },
  { title: 'Price', key: 'price', sortable: false },
  { title: 'Availability', key: 'availability', sortable: false },
  { title: 'Actions', key: 'actions', sortable: false }
]

const copiesHeaders = [
  { title: 'Item ID', key: 'itemId' },
  { title: 'Barcode', key: 'barcode' },
  { title: 'Location', key: 'location' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions' }
]

// ==================== 业务逻辑相关 ====================

// 搜索图书
const searchBooks = async () => {
  loading.value = true
  try {
    const params: Record<string, string | number> = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sortBy: searchFilters.value.sortBy,
      sortDirection: 'desc'
    }

    if (searchFilters.value.title) {
      params.title = searchFilters.value.title
    }
    if (searchFilters.value.author) {
      params.author = searchFilters.value.author
    }
    if (searchFilters.value.category !== null) {
      params.category = searchFilters.value.category
    }

    const response = await axios.get<{ success: boolean; message: string; data: PageResponse<Book> }>('/api/books', { params })

    if (response.data.success && response.data.data) {
      books.value = response.data.data.content
      totalElements.value = response.data.data.totalElements
      totalPages.value = response.data.data.totalPages
    } else {
      showMessage(response.data.message || 'Failed to load books', 'error')
    }
  } catch (error) {
    console.error('Search books error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to load books', 'error')
  } finally {
    loading.value = false
  }
}

// 重置过滤器
const resetFilters = () => {
  searchFilters.value = {
    title: '',
    author: '',
    category: null,
    sortBy: 'bookId'
  }
  currentPage.value = 1
  searchBooks()
}

// 页码变化处理
const onPageChange = (page: number) => {
  currentPage.value = page
  searchBooks()
}

// 查看图书详情
const viewBookDetails = (book: Book) => {
  selectedBook.value = book
  bookCopies.value = []
  detailDialog.value = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  detailDialog.value = false
  selectedBook.value = null
  bookCopies.value = []
}

// 加载图书副本列表
const loadBookCopies = async () => {
  if (!selectedBook.value) return

  loadingCopies.value = true
  try {
    const response = await axios.get<{ success: boolean; message: string; data: PageResponse<BookCopy> }>(
      `/api/book-items/book/${selectedBook.value.bookId}`,
      {
        params: {
          page: 0,
          size: 100,
          status: 'Available'
        }
      }
    )

    if (response.data.success && response.data.data) {
      bookCopies.value = response.data.data.content
    } else {
      showMessage(response.data.message || 'Failed to load book copies', 'error')
    }
  } catch (error) {
    console.error('Load book copies error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to load book copies', 'error')
  } finally {
    loadingCopies.value = false
  }
}

// 借阅图书
const borrowBook = async (bookCopy: BookCopy) => {
  borrowingItemId.value = bookCopy.itemId
  try {
    const response = await axios.post<{ success: boolean; message: string }>(
      '/api/borrow-records/borrow',
      { itemId: bookCopy.itemId }
    )

    if (response.data.success) {
      showMessage('Book borrowed successfully!', 'success')
      // 重新加载副本列表和图书列表
      await loadBookCopies()
      await searchBooks()
    } else {
      showMessage(response.data.message || 'Failed to borrow book', 'error')
    }
  } catch (error) {
    console.error('Borrow book error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to borrow book', 'error')
  } finally {
    borrowingItemId.value = null
  }
}

// 获取状态颜色
const getStatusColor = (status: string): string => {
  const colorMap: Record<string, string> = {
    'Available': 'success',
    'Checked_Out': 'warning',
    'Lost': 'error',
    'Damaged': 'error',
    'Withdrawn': 'grey'
  }
  return colorMap[status] || 'grey'
}

// 显示消息
const showMessage = (text: string, color: string) => {
  snackbarText.value = text
  snackbarColor.value = color
  snackbar.value = true
}

// ==================== 生命周期 ====================
onMounted(() => {
  searchBooks()
})
</script>

<style scoped>
</style>

