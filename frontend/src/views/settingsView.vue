<template>
    <v-card-title class="text-h4 mb-6 text-grey-darken-3">
      <v-icon icon="mdi-cog" class="mr-8"></v-icon>
      User Settings
    </v-card-title>
    <v-divider></v-divider>
    <v-card-text class="my-8">
      <div class="d-flex align-center mb-10">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">UUID</div>
        <div class="text-h5 text-grey-darken-3">{{ uuid }}</div>
      </div>
      <div class="d-flex align-center mb-10">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">Username</div>
        <div class="text-h5 text-grey-darken-3">{{ username }}</div>
      </div>
      <div class="d-flex align-center">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">Role</div>
        <div class="text-h5 text-grey-darken-3">
          <v-chip :color="isAdmin ? 'error' : 'primary'" variant="elevated">
            {{ isAdmin ? 'Administrator' : 'User' }}
          </v-chip>
        </div>
      </div>
    </v-card-text>
    <v-divider></v-divider>
    <v-card-actions class="d-flex my-8 ga-6">
      <v-btn color="primary" variant="elevated" prepend-icon="mdi-account-edit" @click="openUsernameDialog">
        Change Username
      </v-btn>
      <v-btn color="warning" variant="elevated" prepend-icon="mdi-lock-reset" @click="openPasswordDialog">
        Change Password
      </v-btn>
      <v-btn color="error" variant="elevated" prepend-icon="mdi-logout" @click="handleLogout">
        Logout
      </v-btn>
    </v-card-actions>

    <v-dialog v-model="usernameDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">Change Name</v-card-title>
        <v-card-text>
          <v-form ref="usernameFormRef">
            <v-text-field
              v-model="newUsername"
              label="New Name"
              variant="underlined"
              prepend-icon="mdi-account"
              :rules="nameRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closeUsernameDialog" :disabled="loading">Cancel</v-btn>
          <v-btn color="primary" variant="elevated" @click="changeUsername" :loading="loading">Confirm</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="passwordDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">Change Password</v-card-title>
        <v-card-text>
          <v-form ref="passwordFormRef">
            <v-text-field
              v-model="currentPassword"
              label="Current Password"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock"
              :rules="passwordRequiredRules"
              :disabled="loading"
              class="mb-2"
            ></v-text-field>
            <v-text-field
              v-model="newPassword"
              label="New Password"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock-reset"
              :rules="passwordRules"
              :disabled="loading"
              class="mb-2"
            ></v-text-field>
            <v-text-field
              v-model="confirmPassword"
              label="Confirm New Password"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock-check"
              :rules="confirmPasswordRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closePasswordDialog" :disabled="loading">Cancel</v-btn>
          <v-btn color="warning" variant="elevated" @click="changePassword" :loading="loading">Confirm</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar
      v-model="snackbar"
      :color="snackbarColor"
      :timeout="3000"
      location="top"
    >
      {{ snackbarText }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserdataStore } from '@/stores/userdata'
import { useRouter } from 'vue-router'
import axios from 'axios'

interface AxiosError {
  response?: { data?: { message?: string } }
  request?: unknown
}

interface FormElement {
  validate: () => Promise<{ valid: boolean }>
  reset: () => void
}

const user = useUserdataStore()
const router = useRouter()

const uuid = computed(() => user.uuid)
const username = computed(() => user.username)
const isAdmin = computed(() => user.admin)

const usernameDialog = ref(false)
const passwordDialog = ref(false)

const newUsername = ref('')
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const loading = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameFormRef = ref<FormElement | null>(null)
const passwordFormRef = ref<FormElement | null>(null)

const nameRules = [
  (v: string) => !!v || 'Username cannot be empty',
  (v: string) => (v && v.length >= 3 && v.length <= 50) || 'Username must be between 3-50 characters'
]

const passwordRequiredRules = [
  (v: string) => !!v || 'Current password cannot be empty'
]

const passwordRules = [
  (v: string) => !!v || 'Password cannot be empty',
  (v: string) => (v && v.length >= 6 && v.length <= 50) || 'Password must be between 6-50 characters'
]

const confirmPasswordRules = [
  (v: string) => v === newPassword.value || 'The passwords entered twice do not match'
]

const openUsernameDialog = () => {
  newUsername.value = username.value || ''
  usernameDialog.value = true
}

const closeUsernameDialog = () => {
  usernameDialog.value = false
  newUsername.value = ''
  if (usernameFormRef.value) {
    usernameFormRef.value.reset()
  }
}

const openPasswordDialog = () => {
  passwordDialog.value = true
}

const closePasswordDialog = () => {
  passwordDialog.value = false
  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.reset()
  }
}

const changeUsername = async () => {
  const validator = usernameFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.post(
      '/api/user/changename',
      {
        userUuid: user.uuid,
        newUsername: newUsername.value
      }
    )

    if (response.data.success && response.data.data) {
      user.updateUsername(response.data.data.username)
      snackbarText.value = 'Username updated successfully'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closeUsernameDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || 'Update failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('Change username error:', error)
    snackbarText.value = axiosError.response?.data?.message || 'Update failed, please try again'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const changePassword = async () => {
  const validator = passwordFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.post(
      '/api/user/changepass',
      {
        userUuid: user.uuid,
        oldPassword: currentPassword.value,
        newPassword: newPassword.value
      }
    )

    if (response.data.success) {
      snackbarText.value = 'Password updated successfully'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closePasswordDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || 'Update failed'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('Change password error:', error)
    snackbarText.value = axiosError.response?.data?.message || 'Update failed, please try again'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const handleLogout = async () => {
  loading.value = true
  try {
    await axios.post('/api/auth/logout')

    user.logout()
    snackbarText.value = 'Logged out successfully'
    snackbarColor.value = 'success'
    snackbar.value = true

    setTimeout(() => {
      router.push('/login')
    }, 1000)
  } catch (error: unknown) {
    console.error('Logout error:', error)
    // Even if logout fails on server, clear local state
    user.logout()
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

