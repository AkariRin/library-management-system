<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-shield-account" class="mr-8"></v-icon>
    Admin Panel
  </v-card-title>
  <v-divider></v-divider>

  <v-card-text class="my-8">
    <!-- 功能选项卡 -->
    <v-tabs v-model="activeTab" color="primary" class="mb-6">
      <v-tab value="books">
        <v-icon start icon="mdi-book-multiple"></v-icon>
        Manage Books
      </v-tab>
      <v-tab value="copies">
        <v-icon start icon="mdi-book-copy"></v-icon>
        Manage Copies
      </v-tab>
      <v-tab value="records">
        <v-icon start icon="mdi-book-clock"></v-icon>
        All Borrow Records
      </v-tab>
    </v-tabs>

    <v-window v-model="activeTab">
      <!-- 图书管理标签页 -->
      <v-window-item value="books">
        <div class="mb-4">
          <v-btn color="success" variant="elevated" prepend-icon="mdi-plus" @click="openAddBookDialog">
            Add New Book
          </v-btn>
        </div>

        <v-data-table
          :headers="bookHeaders"
          :items="books"
          :loading="loadingBooks"
          :items-per-page="bookPageSize"
          hide-default-footer
          class="elevation-2"
        >
          <template v-slot:item.price="{ item }">
            ¥{{ item.price ? item.price.toFixed(2) : '0.00' }}
          </template>
          <template v-slot:item.availability="{ item }">
            <v-chip :color="(item.availableCopies ?? 0) > 0 ? 'success' : 'error'" size="small" variant="flat">
              {{ item.availableCopies ?? 0 }} / {{ item.totalCopies ?? 0 }}
            </v-chip>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn color="primary" variant="text" size="small" @click="openEditBookDialog(item)" class="mr-1">
              Edit
            </v-btn>
            <v-btn color="error" variant="text" size="small" @click="openDeleteBookDialog(item)">
              Delete
            </v-btn>
          </template>
          <template v-slot:bottom>
            <div class="text-center pa-4">
              <v-pagination
                v-model="bookCurrentPage"
                :length="bookTotalPages"
                :total-visible="7"
                @update:modelValue="loadBooks"
              ></v-pagination>
            </div>
          </template>
        </v-data-table>
      </v-window-item>

      <!-- 副本管理标签页 -->
      <v-window-item value="copies">
        <v-row class="mb-4">
          <v-col cols="12" md="6">
            <v-select
              v-model="selectedBookForCopies"
              label="Select Book"
              variant="outlined"
              :items="bookOptions"
              item-title="text"
              item-value="value"
              prepend-inner-icon="mdi-book"
              @update:modelValue="loadBookCopies"
            ></v-select>
          </v-col>
          <v-col cols="12" md="6">
            <v-btn
              color="success"
              variant="elevated"
              prepend-icon="mdi-plus"
              @click="openAddCopyDialog"
              :disabled="!selectedBookForCopies"
            >
              Add New Copy
            </v-btn>
          </v-col>
        </v-row>

        <v-data-table
          v-if="selectedBookForCopies"
          :headers="copyHeaders"
          :items="bookCopies"
          :loading="loadingCopies"
          :items-per-page="copyPageSize"
          hide-default-footer
          class="elevation-2"
        >
          <template v-slot:item.status="{ item }">
            <v-chip :color="getStatusColor(item.status)" size="small" variant="flat">
              {{ item.statusDescription }}
            </v-chip>
          </template>
          <template v-slot:item.acquisitionPrice="{ item }">
            ¥{{ item.acquisitionPrice ? item.acquisitionPrice.toFixed(2) : '0.00' }}
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn color="primary" variant="text" size="small" @click="openEditCopyDialog(item)" class="mr-1">
              Edit
            </v-btn>
            <v-btn color="error" variant="text" size="small" @click="openDeleteCopyDialog(item)">
              Delete
            </v-btn>
          </template>
          <template v-slot:bottom>
            <div class="text-center pa-4">
              <v-pagination
                v-model="copyCurrentPage"
                :length="copyTotalPages"
                :total-visible="7"
                @update:modelValue="loadBookCopies"
              ></v-pagination>
            </div>
          </template>
        </v-data-table>
        <v-alert v-else type="info" variant="tonal" class="mt-4">
          Please select a book to view and manage its copies
        </v-alert>
      </v-window-item>

      <!-- 借阅记录管理标签页 -->
      <v-window-item value="records">
        <v-row class="mb-4">
          <v-col cols="12" md="4">
            <v-select
              v-model="recordStatusFilter"
              label="Status Filter"
              variant="outlined"
              :items="statusOptions"
              item-title="text"
              item-value="value"
              clearable
              @update:modelValue="loadAllBorrowRecords"
            ></v-select>
          </v-col>
          <v-col cols="12" md="8">
            <v-btn color="primary" variant="elevated" prepend-icon="mdi-refresh" @click="loadAllBorrowRecords" :loading="loadingRecords">
              Refresh
            </v-btn>
          </v-col>
        </v-row>

        <v-data-table
          :headers="recordHeaders"
          :items="borrowRecords"
          :loading="loadingRecords"
          :items-per-page="recordPageSize"
          hide-default-footer
          class="elevation-2"
        >
          <template v-slot:item.user="{ item }">
            <div>
              <div class="font-weight-bold">{{ item.userName }}</div>
              <div class="text-caption text-grey-darken-1">@{{ item.username }}</div>
            </div>
          </template>
          <template v-slot:item.bookInfo="{ item }">
            <div>
              <div class="font-weight-bold">{{ item.bookTitle }}</div>
              <div class="text-caption text-grey-darken-1">{{ item.bookAuthor }}</div>
            </div>
          </template>
          <template v-slot:item.borrowDate="{ item }">
            {{ formatDate(item.borrowDate) }}
          </template>
          <template v-slot:item.dueDate="{ item }">
            <div :class="{ 'text-error font-weight-bold': item.isOverdue }">
              {{ formatDate(item.dueDate) }}
              <v-chip v-if="item.isOverdue" color="error" size="x-small" class="ml-1">Overdue</v-chip>
            </div>
          </template>
          <template v-slot:item.status="{ item }">
            <v-chip :color="getBorrowStatusColor(item.status)" size="small" variant="flat">
              {{ item.statusDescription }}
            </v-chip>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn color="info" variant="text" size="small" @click="openEditRecordDialog(item)">
              Edit
            </v-btn>
          </template>
          <template v-slot:bottom>
            <div class="text-center pa-4">
              <v-pagination
                v-model="recordCurrentPage"
                :length="recordTotalPages"
                :total-visible="7"
                @update:modelValue="loadAllBorrowRecords"
              ></v-pagination>
            </div>
          </template>
        </v-data-table>
      </v-window-item>
    </v-window>
  </v-card-text>

  <!-- 添加/编辑图书对话框 -->
  <v-dialog v-model="bookDialog" max-width="600px">
    <v-card>
      <v-card-title class="text-h5">
        {{ editingBook ? 'Edit Book' : 'Add New Book' }}
      </v-card-title>
      <v-card-text>
        <v-form ref="bookFormRef">
          <v-text-field
            v-model="bookForm.title"
            label="Title *"
            variant="outlined"
            :rules="titleRules"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="bookForm.author"
            label="Author *"
            variant="outlined"
            :rules="authorRules"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="bookForm.isbn"
            label="ISBN"
            variant="outlined"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="bookForm.publisher"
            label="Publisher"
            variant="outlined"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="bookForm.publishDate"
            label="Publish Date"
            variant="outlined"
            type="date"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="bookForm.category"
            label="Category ID"
            variant="outlined"
            type="number"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="bookForm.price"
            label="Price"
            variant="outlined"
            type="number"
            step="0.01"
            :disabled="savingBook"
            class="mb-2"
          ></v-text-field>
          <v-textarea
            v-model="bookForm.summary"
            label="Summary"
            variant="outlined"
            rows="3"
            :disabled="savingBook"
          ></v-textarea>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="closeBookDialog" :disabled="savingBook">Cancel</v-btn>
        <v-btn color="primary" variant="elevated" @click="saveBook" :loading="savingBook">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 删除图书确认对话框 -->
  <v-dialog v-model="deleteBookDialog" max-width="400px">
    <v-card>
      <v-card-title class="text-h5">Confirm Delete</v-card-title>
      <v-card-text>
        Are you sure you want to delete this book? This will also delete all copies.
        <div class="mt-4 font-weight-bold" v-if="bookToDelete">
          {{ bookToDelete.title }}
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="deleteBookDialog = false" :disabled="deletingBook">Cancel</v-btn>
        <v-btn color="error" variant="elevated" @click="deleteBook" :loading="deletingBook">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 添加/编辑副本对话框 -->
  <v-dialog v-model="copyDialog" max-width="600px">
    <v-card>
      <v-card-title class="text-h5">
        {{ editingCopy ? 'Edit Copy' : 'Add New Copy' }}
      </v-card-title>
      <v-card-text>
        <v-form ref="copyFormRef">
          <v-text-field
            v-model="copyForm.barcode"
            label="Barcode *"
            variant="outlined"
            :rules="barcodeRules"
            :disabled="savingCopy"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="copyForm.location"
            label="Location"
            variant="outlined"
            :disabled="savingCopy"
            class="mb-2"
          ></v-text-field>
          <v-select
            v-model="copyForm.status"
            label="Status *"
            variant="outlined"
            :items="copyStatusOptions"
            :rules="statusRules"
            :disabled="savingCopy"
            class="mb-2"
          ></v-select>
          <v-text-field
            v-model="copyForm.acquisitionDate"
            label="Acquisition Date"
            variant="outlined"
            type="date"
            :disabled="savingCopy"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="copyForm.acquisitionPrice"
            label="Acquisition Price"
            variant="outlined"
            type="number"
            step="0.01"
            :disabled="savingCopy"
            class="mb-2"
          ></v-text-field>
          <v-textarea
            v-model="copyForm.notes"
            label="Notes"
            variant="outlined"
            rows="2"
            :disabled="savingCopy"
          ></v-textarea>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="closeCopyDialog" :disabled="savingCopy">Cancel</v-btn>
        <v-btn color="primary" variant="elevated" @click="saveCopy" :loading="savingCopy">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 删除副本确认对话框 -->
  <v-dialog v-model="deleteCopyDialog" max-width="400px">
    <v-card>
      <v-card-title class="text-h5">Confirm Delete</v-card-title>
      <v-card-text>
        Are you sure you want to delete this copy?
        <div class="mt-4" v-if="copyToDelete">
          <div class="font-weight-bold">Barcode: {{ copyToDelete.barcode }}</div>
          <div class="text-caption">{{ copyToDelete.bookTitle }}</div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="deleteCopyDialog = false" :disabled="deletingCopy">Cancel</v-btn>
        <v-btn color="error" variant="elevated" @click="deleteCopy" :loading="deletingCopy">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 编辑借阅记录对话框 -->
  <v-dialog v-model="recordDialog" max-width="600px">
    <v-card>
      <v-card-title class="text-h5">Edit Borrow Record</v-card-title>
      <v-card-text v-if="editingRecord">
        <div class="mb-4">
          <div class="text-subtitle-2 text-grey-darken-1">User</div>
          <div class="text-h6">{{ editingRecord.userName }} (@{{ editingRecord.username }})</div>
        </div>
        <div class="mb-4">
          <div class="text-subtitle-2 text-grey-darken-1">Book</div>
          <div class="text-h6">{{ editingRecord.bookTitle }}</div>
        </div>
        <v-divider class="my-4"></v-divider>
        <v-form ref="recordFormRef">
          <v-text-field
            v-model="recordForm.dueDate"
            label="Due Date"
            variant="outlined"
            type="datetime-local"
            :disabled="savingRecord"
            class="mb-2"
          ></v-text-field>
          <v-text-field
            v-model="recordForm.returnDate"
            label="Return Date"
            variant="outlined"
            type="datetime-local"
            :disabled="savingRecord"
            class="mb-2"
          ></v-text-field>
          <v-select
            v-model="recordForm.status"
            label="Status"
            variant="outlined"
            :items="borrowStatusOptions"
            :disabled="savingRecord"
          ></v-select>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="closeRecordDialog" :disabled="savingRecord">Cancel</v-btn>
        <v-btn color="primary" variant="elevated" @click="saveRecord" :loading="savingRecord">Save</v-btn>
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
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

// ==================== 类型定义 ====================
interface Book {
  bookId?: number
  isbn?: string
  title: string
  author: string
  publisher?: string
  publishDate?: string
  category?: number
  summary?: string
  price?: number
  totalCopies?: number
  availableCopies?: number
}

interface BookCopy {
  itemId?: number
  bookId?: number
  bookTitle?: string
  bookAuthor?: string
  barcode?: string
  location?: string
  status: string
  statusDescription?: string
  acquisitionDate?: string
  acquisitionPrice?: number
  notes?: string
}

interface BorrowRecord {
  recordId: string
  userUuid: string
  username: string
  userName: string
  itemId: number
  barcode?: string
  bookId: number
  bookTitle: string
  bookAuthor: string
  borrowDate: string
  dueDate: string
  returnDate?: string
  status: string
  statusDescription: string
  isOverdue: boolean
}

interface PageResponse {
  content: any[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  empty: boolean
}

interface ApiResponse {
  success: boolean
  message?: string
  data?: any
}

interface AxiosError {
  response?: { data?: { message?: string } }
}

interface FormElement {
  validate: () => Promise<{ valid: boolean }>
  reset: () => void
}

// ==================== UI 控制相关 ====================
const activeTab = ref('books')
const loadingBooks = ref(false)
const loadingCopies = ref(false)
const loadingRecords = ref(false)
const savingBook = ref(false)
const deletingBook = ref(false)
const savingCopy = ref(false)
const deletingCopy = ref(false)
const savingRecord = ref(false)

const bookDialog = ref(false)
const deleteBookDialog = ref(false)
const copyDialog = ref(false)
const deleteCopyDialog = ref(false)
const recordDialog = ref(false)

const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

// ==================== 数据相关 ====================
const books = ref<Book[]>([])
const bookCopies = ref<BookCopy[]>([])
const borrowRecords = ref<BorrowRecord[]>([])

const bookCurrentPage = ref(1)
const bookPageSize = ref(10)
const bookTotalPages = ref(0)

const copyCurrentPage = ref(1)
const copyPageSize = ref(10)
const copyTotalPages = ref(0)

const recordCurrentPage = ref(1)
const recordPageSize = ref(10)
const recordTotalPages = ref(0)

const selectedBookForCopies = ref<number | null>(null)
const recordStatusFilter = ref<string | null>(null)

const editingBook = ref<Book | null>(null)
const bookToDelete = ref<Book | null>(null)
const editingCopy = ref<BookCopy | null>(null)
const copyToDelete = ref<BookCopy | null>(null)
const editingRecord = ref<BorrowRecord | null>(null)

const bookForm = ref<Book>({
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  publishDate: '',
  category: undefined,
  summary: '',
  price: undefined
})

const copyForm = ref<BookCopy>({
  barcode: '',
  location: '',
  status: 'Available',
  acquisitionDate: '',
  acquisitionPrice: undefined,
  notes: ''
})

const recordForm = ref({
  dueDate: '',
  returnDate: '',
  status: ''
})

const bookFormRef = ref<FormElement | null>(null)
const copyFormRef = ref<FormElement | null>(null)
const recordFormRef = ref<FormElement | null>(null)

// ==================== 常量和选项 ====================
const statusOptions = [
  { text: 'All', value: null },
  { text: 'Checked Out', value: 'Checked_Out' },
  { text: 'Returned', value: 'Returned' }
]

const copyStatusOptions = ['Available', 'Checked_Out', 'Lost', 'Damaged', 'Withdrawn']
const borrowStatusOptions = ['Checked_Out', 'Returned']

const bookHeaders = [
  { title: 'ID', key: 'bookId', sortable: false },
  { title: 'Title', key: 'title', sortable: false },
  { title: 'Author', key: 'author', sortable: false },
  { title: 'ISBN', key: 'isbn', sortable: false },
  { title: 'Price', key: 'price', sortable: false },
  { title: 'Availability', key: 'availability', sortable: false },
  { title: 'Actions', key: 'actions', sortable: false }
]

const copyHeaders = [
  { title: 'Item ID', key: 'itemId', sortable: false },
  { title: 'Barcode', key: 'barcode', sortable: false },
  { title: 'Location', key: 'location', sortable: false },
  { title: 'Status', key: 'status', sortable: false },
  { title: 'Acq. Price', key: 'acquisitionPrice', sortable: false },
  { title: 'Actions', key: 'actions', sortable: false }
]

const recordHeaders = [
  { title: 'User', key: 'user', sortable: false },
  { title: 'Book Info', key: 'bookInfo', sortable: false },
  { title: 'Borrow Date', key: 'borrowDate', sortable: false },
  { title: 'Due Date', key: 'dueDate', sortable: false },
  { title: 'Status', key: 'status', sortable: false },
  { title: 'Actions', key: 'actions', sortable: false }
]

// 验证规则
const titleRules = [
  (v: string) => !!v || 'Title is required',
  (v: string) => (v && v.length <= 50) || 'Title must be less than 50 characters'
]

const authorRules = [
  (v: string) => !!v || 'Author is required',
  (v: string) => (v && v.length <= 50) || 'Author must be less than 50 characters'
]

const barcodeRules = [
  (v: string) => !!v || 'Barcode is required',
  (v: string) => (v && v.length <= 30) || 'Barcode must be less than 30 characters'
]

const statusRules = [
  (v: string) => !!v || 'Status is required'
]

// ==================== 计算属性 ====================
const bookOptions = computed(() => {
  return books.value.map(book => ({
    text: `${book.title} (ID: ${book.bookId})`,
    value: book.bookId
  }))
})

// ==================== 业务逻辑 - 图书管理 ====================

// 加载图书列表
const loadBooks = async () => {
  loadingBooks.value = true
  try {
    const response = await axios.get('/api/books', {
      params: {
        page: bookCurrentPage.value - 1,
        size: bookPageSize.value,
        sortBy: 'bookId',
        sortDirection: 'desc'
      }
    })
    const data = response.data as ApiResponse
    if (data.success && data.data) {
      const pageData = data.data as PageResponse
      books.value = pageData.content
      bookTotalPages.value = pageData.totalPages
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to load books', 'error')
  } finally {
    loadingBooks.value = false
  }
}

// 打开添加图书对话框
const openAddBookDialog = () => {
  editingBook.value = null
  bookForm.value = {
    title: '',
    author: '',
    isbn: '',
    publisher: '',
    publishDate: '',
    category: undefined,
    summary: '',
    price: undefined
  }
  bookDialog.value = true
}

// 打开编辑图书对话框
const openEditBookDialog = (book: Book) => {
  editingBook.value = book
  bookForm.value = { ...book }
  bookDialog.value = true
}

// 关闭图书对话框
const closeBookDialog = () => {
  bookDialog.value = false
  editingBook.value = null
  if (bookFormRef.value) {
    bookFormRef.value.reset()
  }
}

// 保存图书
const saveBook = async () => {
  const validator = bookFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) return

  savingBook.value = true
  try {
    const payload = {
      ...bookForm.value,
      category: bookForm.value.category || null,
      price: bookForm.value.price || null
    }

    let response
    if (editingBook.value && editingBook.value.bookId) {
      response = await axios.put(`/api/books/${editingBook.value.bookId}`, payload)
      showMessage('Book updated successfully', 'success')
    } else {
      response = await axios.post('/api/books', payload)
      showMessage('Book added successfully', 'success')
    }

    const data = response.data as ApiResponse
    if (data.success) {
      closeBookDialog()
      await loadBooks()
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to save book', 'error')
  } finally {
    savingBook.value = false
  }
}

// 打开删除图书对话框
const openDeleteBookDialog = (book: Book) => {
  bookToDelete.value = book
  deleteBookDialog.value = true
}

// 删除图书
const deleteBook = async () => {
  if (!bookToDelete.value || !bookToDelete.value.bookId) return

  deletingBook.value = true
  try {
    const response = await axios.delete(`/api/books/${bookToDelete.value.bookId}`)
    const data = response.data as ApiResponse
    if (data.success) {
      showMessage('Book deleted successfully', 'success')
      deleteBookDialog.value = false
      bookToDelete.value = null
      await loadBooks()
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to delete book', 'error')
  } finally {
    deletingBook.value = false
  }
}

// ==================== 业务逻辑 - 副本管理 ====================

// 加载图书副本列表
const loadBookCopies = async () => {
  if (!selectedBookForCopies.value) return

  loadingCopies.value = true
  try {
    const response = await axios.get(`/api/book-items/book/${selectedBookForCopies.value}`, {
      params: {
        page: copyCurrentPage.value - 1,
        size: copyPageSize.value
      }
    })

    const data = response.data as ApiResponse
    if (data.success && data.data) {
      const pageData = data.data as PageResponse
      bookCopies.value = pageData.content
      copyTotalPages.value = pageData.totalPages
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to load copies', 'error')
  } finally {
    loadingCopies.value = false
  }
}

// 打开添加副本对话框
const openAddCopyDialog = () => {
  editingCopy.value = null
  copyForm.value = {
    barcode: '',
    location: '',
    status: 'Available',
    acquisitionDate: '',
    acquisitionPrice: undefined,
    notes: ''
  }
  copyDialog.value = true
}

// 打开编辑副本对话框
const openEditCopyDialog = (copy: BookCopy) => {
  editingCopy.value = copy
  copyForm.value = { ...copy }
  copyDialog.value = true
}

// 关闭副本对话框
const closeCopyDialog = () => {
  copyDialog.value = false
  editingCopy.value = null
  if (copyFormRef.value) {
    copyFormRef.value.reset()
  }
}

// 保存副本
const saveCopy = async () => {
  const validator = copyFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) return

  savingCopy.value = true
  try {
    const payload = {
      ...copyForm.value,
      bookId: selectedBookForCopies.value,
      acquisitionPrice: copyForm.value.acquisitionPrice || null
    }

    let response
    if (editingCopy.value && editingCopy.value.itemId) {
      response = await axios.put(`/api/book-items/${editingCopy.value.itemId}`, payload)
      showMessage('Copy updated successfully', 'success')
    } else {
      response = await axios.post('/api/book-items', payload)
      showMessage('Copy added successfully', 'success')
    }

    const data = response.data as ApiResponse
    if (data.success) {
      closeCopyDialog()
      await loadBookCopies()
      await loadBooks()
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to save copy', 'error')
  } finally {
    savingCopy.value = false
  }
}

// 打开删除副本对话框
const openDeleteCopyDialog = (copy: BookCopy) => {
  copyToDelete.value = copy
  deleteCopyDialog.value = true
}

// 删除副本
const deleteCopy = async () => {
  if (!copyToDelete.value || !copyToDelete.value.itemId) return

  deletingCopy.value = true
  try {
    const response = await axios.delete(`/api/book-items/${copyToDelete.value.itemId}`)
    const data = response.data as ApiResponse
    if (data.success) {
      showMessage('Copy deleted successfully', 'success')
      deleteCopyDialog.value = false
      copyToDelete.value = null
      await loadBookCopies()
      await loadBooks()
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to delete copy', 'error')
  } finally {
    deletingCopy.value = false
  }
}

// ==================== 业务逻辑 - 借阅记录管理 ====================

// 加载所有借阅记录
const loadAllBorrowRecords = async () => {
  loadingRecords.value = true
  try {
    const params: any = {
      page: recordCurrentPage.value - 1,
      size: recordPageSize.value
    }

    if (recordStatusFilter.value) {
      params.status = recordStatusFilter.value
    }

    const response = await axios.get('/api/borrow-records', { params })
    const data = response.data as ApiResponse

    if (data.success && data.data) {
      const pageData = data.data as PageResponse
      borrowRecords.value = pageData.content
      recordTotalPages.value = pageData.totalPages
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to load borrow records', 'error')
  } finally {
    loadingRecords.value = false
  }
}

// 打开编辑借阅记录对话框
const openEditRecordDialog = (record: BorrowRecord) => {
  editingRecord.value = record
  recordForm.value = {
    dueDate: formatDateForInput(record.dueDate),
    returnDate: record.returnDate ? formatDateForInput(record.returnDate) : '',
    status: record.status
  }
  recordDialog.value = true
}

// 关闭借阅记录对话框
const closeRecordDialog = () => {
  recordDialog.value = false
  editingRecord.value = null
  if (recordFormRef.value) {
    recordFormRef.value.reset()
  }
}

// 保存借阅记录
const saveRecord = async () => {
  if (!editingRecord.value) return

  savingRecord.value = true
  try {
    const payload: any = {}

    if (recordForm.value.dueDate) {
      payload.dueDate = new Date(recordForm.value.dueDate).toISOString()
    }
    if (recordForm.value.returnDate) {
      payload.returnDate = new Date(recordForm.value.returnDate).toISOString()
    }
    if (recordForm.value.status) {
      payload.status = recordForm.value.status
    }

    const response = await axios.put(`/api/borrow-records/${editingRecord.value.recordId}`, payload)
    const data = response.data as ApiResponse

    if (data.success) {
      showMessage('Record updated successfully', 'success')
      closeRecordDialog()
      await loadAllBorrowRecords()
    }
  } catch (error) {
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || 'Failed to update record', 'error')
  } finally {
    savingRecord.value = false
  }
}

// ==================== 辅助函数 ====================

// 格式化日期显示
const formatDate = (dateString: string): string => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化日期为输入框格式
const formatDateForInput = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

// 获取副本状态颜色
const getStatusColor = (status: string): string => {
  const colorMap: any = {
    'Available': 'success',
    'Checked_Out': 'warning',
    'Lost': 'error',
    'Damaged': 'error',
    'Withdrawn': 'grey'
  }
  return colorMap[status] || 'grey'
}

// 获取借阅状态颜色
const getBorrowStatusColor = (status: string): string => {
  const colorMap: any = {
    'Checked_Out': 'warning',
    'Returned': 'success'
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
  loadBooks()
})
</script>

<style scoped>
</style>

