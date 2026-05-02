import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatPrice(amount: number): string {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(amount)
}

export function formatDate(dateStr: string): string {
  return new Intl.DateTimeFormat('tr-TR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(dateStr))
}

export const ORDER_STATUS_LABELS: Record<string, string> = {
  PENDING: 'Beklemede',
  STOCK_RESERVED: 'Stok Rezerve',
  PAYMENT_REQUESTED: 'Ödeme Bekleniyor',
  CONFIRMED: 'Onaylandı',
  CANCELLED: 'İptal Edildi',
}

export const ORDER_STATUS_COLORS: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  STOCK_RESERVED: 'bg-blue-100 text-blue-800',
  PAYMENT_REQUESTED: 'bg-purple-100 text-purple-800',
  CONFIRMED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
}
