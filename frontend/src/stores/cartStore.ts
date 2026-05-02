import { create } from 'zustand'
import type { Cart, CartItem } from '@/types'
import { api } from '@/lib/axios'

interface CartState {
  cart: Cart | null
  isLoading: boolean
  fetchCart: () => Promise<void>
  addItem: (item: Omit<CartItem, 'addedAt'>) => Promise<void>
  updateQuantity: (variantId: string, quantity: number) => Promise<void>
  removeItem: (variantId: string) => Promise<void>
  clearCart: () => void
  totalItems: () => number
  totalPrice: () => number
}

export const useCartStore = create<CartState>((set, get) => ({
  cart: null,
  isLoading: false,

  fetchCart: async () => {
    set({ isLoading: true })
    try {
      const { data } = await api.get('/api/cart')
      set({ cart: data.data })
    } catch {
      set({ cart: null })
    } finally {
      set({ isLoading: false })
    }
  },

  addItem: async (item) => {
    await api.post('/api/cart/items', item)
    await get().fetchCart()
  },

  updateQuantity: async (variantId, quantity) => {
    await api.put(`/api/cart/items/${variantId}`, { quantity })
    await get().fetchCart()
  },

  removeItem: async (variantId) => {
    await api.delete(`/api/cart/items/${variantId}`)
    await get().fetchCart()
  },

  clearCart: () => set({ cart: null }),

  totalItems: () => {
    const { cart } = get()
    return cart?.items.reduce((sum, item) => sum + item.quantity, 0) ?? 0
  },

  totalPrice: () => {
    const { cart } = get()
    return (
      cart?.items.reduce(
        (sum, item) => sum + item.priceSnapshot * item.quantity,
        0
      ) ?? 0
    )
  },
}))
