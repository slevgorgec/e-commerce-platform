export interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  role: 'USER' | 'ADMIN'
}

export interface AuthTokens {
  accessToken: string
  refreshToken: string
}

export interface Category {
  id: string
  name: string
  slug: string
  parentId?: string
  imageUrl?: string
}

export interface ProductVariant {
  id: string
  sku: string
  variantValue: string
  price: number
  stock: number
  reservedStock: number
}

export interface Product {
  id: string
  name: string
  slug: string
  description?: string
  categoryId: string
  categoryName?: string
  basePrice: number
  active: boolean
  imageUrl?: string
  variants: ProductVariant[]
  createdAt: string
}

export interface CartItem {
  productId: string
  variantId: string
  productName: string
  variantValue: string
  priceSnapshot: number
  quantity: number
  addedAt: string
}

export interface Cart {
  userId: string
  items: CartItem[]
  updatedAt: string
}

export interface ShippingAddress {
  fullName: string
  phone: string
  addressLine1: string
  addressLine2?: string
  city: string
  district: string
  postalCode: string
}

export interface OrderItem {
  id: string
  productId: string
  variantId: string
  productNameSnapshot: string
  variantValueSnapshot: string
  unitPriceSnapshot: number
  quantity: number
}

export type OrderStatus =
  | 'PENDING'
  | 'STOCK_RESERVED'
  | 'PAYMENT_REQUESTED'
  | 'CONFIRMED'
  | 'CANCELLED'

export interface Order {
  id: string
  userId: string
  status: OrderStatus
  totalAmount: number
  shippingAddress: ShippingAddress
  items: OrderItem[]
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface ApiResponse<T> {
  data: T
  timestamp: string
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}
