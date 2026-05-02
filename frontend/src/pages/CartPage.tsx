import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Trash2, Plus, Minus, ShoppingBag } from 'lucide-react'
import { toast } from 'sonner'

import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { Skeleton } from '@/components/ui/skeleton'
import { useCartStore } from '@/stores/cartStore'
import { formatPrice } from '@/lib/utils'

export default function CartPage() {
  const navigate = useNavigate()
  const { cart, isLoading, fetchCart, updateQuantity, removeItem, totalPrice } = useCartStore()

  useEffect(() => {
    fetchCart()
  }, [fetchCart])

  const handleRemove = async (variantId: string, name: string) => {
    try {
      await removeItem(variantId)
      toast.success(`${name} sepetten çıkarıldı`)
    } catch {
      toast.error('İşlem başarısız')
    }
  }

  const handleQuantityChange = async (variantId: string, quantity: number) => {
    if (quantity < 1) return
    try {
      await updateQuantity(variantId, quantity)
    } catch {
      toast.error('Miktar güncellenemedi')
    }
  }

  if (isLoading) {
    return (
      <div className="container max-w-4xl mx-auto px-4 py-8 space-y-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <Skeleton key={i} className="h-24 w-full rounded-lg" />
        ))}
      </div>
    )
  }

  const items = cart?.items ?? []

  if (items.length === 0) {
    return (
      <div className="container max-w-4xl mx-auto px-4 py-16 text-center">
        <ShoppingBag className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
        <h2 className="text-xl font-semibold mb-2">Sepetiniz boş</h2>
        <p className="text-muted-foreground mb-6">Alışverişe başlamak için ürünleri inceleyin.</p>
        <Button onClick={() => navigate('/products')}>Ürünlere Git</Button>
      </div>
    )
  }

  return (
    <div className="container max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Sepetim ({items.length} ürün)</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Items */}
        <div className="lg:col-span-2 space-y-3">
          {items.map((item) => (
            <div
              key={item.variantId}
              className="flex items-center gap-4 p-4 border rounded-lg bg-card"
            >
              <div className="h-16 w-16 bg-muted rounded-md flex items-center justify-center text-2xl flex-shrink-0">
                🛍️
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-sm line-clamp-1">{item.productName}</p>
                <p className="text-xs text-muted-foreground">{item.variantValue}</p>
                <p className="font-semibold text-sm mt-1">{formatPrice(item.priceSnapshot)}</p>
              </div>
              <div className="flex items-center gap-1">
                <Button
                  variant="outline"
                  size="icon"
                  className="h-7 w-7"
                  onClick={() => handleQuantityChange(item.variantId, item.quantity - 1)}
                >
                  <Minus className="h-3 w-3" />
                </Button>
                <span className="w-6 text-center text-sm">{item.quantity}</span>
                <Button
                  variant="outline"
                  size="icon"
                  className="h-7 w-7"
                  onClick={() => handleQuantityChange(item.variantId, item.quantity + 1)}
                >
                  <Plus className="h-3 w-3" />
                </Button>
              </div>
              <p className="font-semibold text-sm w-20 text-right">
                {formatPrice(item.priceSnapshot * item.quantity)}
              </p>
              <Button
                variant="ghost"
                size="icon"
                className="h-8 w-8 text-muted-foreground hover:text-destructive"
                onClick={() => handleRemove(item.variantId, item.productName)}
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          ))}
        </div>

        {/* Summary */}
        <div className="lg:col-span-1">
          <div className="border rounded-lg p-4 bg-card sticky top-20">
            <h2 className="font-semibold mb-4">Sipariş Özeti</h2>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Ara Toplam</span>
                <span>{formatPrice(totalPrice())}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Kargo</span>
                <span className="text-green-600">Ücretsiz</span>
              </div>
            </div>
            <Separator className="my-3" />
            <div className="flex justify-between font-semibold">
              <span>Toplam</span>
              <span>{formatPrice(totalPrice())}</span>
            </div>
            <Button className="w-full mt-4" onClick={() => navigate('/checkout')}>
              Siparişi Tamamla
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
