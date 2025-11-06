import { defineStore } from 'pinia'

export const useUserdataStore = defineStore('userdata', {
  state: () => ({
    token: null as string | null,
    id: null as string | null,
    name: null as string | null,
    balance: 0.00,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  actions: {
    login(token: string, username: string, name: string, balance: number) {
      this.token = token
      this.id = username
      this.name = name
      this.balance = balance
    },
    logout() {
      this.token = null
      this.id = null
      this.name = null
      this.balance = 0.00
    },
    updateName(name: string) {
      this.name = name
    },
    updateBalance(balance: number) {
      this.balance = balance
    },
  },
  persist: true,
})

