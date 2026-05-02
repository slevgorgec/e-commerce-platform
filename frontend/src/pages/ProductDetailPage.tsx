import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { ShoppingCart, Loader2, ChevronLeft } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Separator } from '@/components/ui/separator'
import { api } from '@/lib/axios'
import { formatPrice } from '@/lib/utils'
import { useAuthStore } from '@/stores/authStore'
import { useCartStore } from '@/stores/cartStore'
import type { Product, ProductVariant } from '@/types'

export default function ProductDetailPage() {
  const { slug } = useParams<{ slug: string }>()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const { addItem } = useCartStore()

  const [product, setProduct] = useState<Product | null>(null)
  const [loading, setLoading] = useState(true)
  const [selectedVariant, setSelectedVariant] = useState<ProductVariant | null>(null)
  const [quantity, setQuantity] = useState(1)
  const [adding, setAdding] = useState(false)

  useEffect(() => {
    api
      .get<{ data: Product }>(`/api/products/${slug}`)
      .then(({ data }) => {
        setProduct(data.data)
        if (data.data.variants?.length) setSelectedVariant(data.data.variants[0])
      })
      .finally(() => setLoading(false))
  }, [slug])

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/products/${slug}` } })
      return
    }
    if (!product || !selectedVariant) return
    setAdding(true)
    try {
      await addItem({
        productId: product.id,
        variantId: selectedVariant.id,
        productName: product.name,
        variantValue: selectedVariant.variantValue,
        priceSnapshot: selectedVariant.price,
        quantity,
      })
      toast.success('Sepete eklendi!')
    } catch {
      toast.error('Sepete eklenemedi')
    } finally {
      setAdding(false)
    }
  }

  if (loading) {
    return (
      <div className="container max-w-6xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <Skeleton className="aspect-square rounded-lg" />
          <div className="space-y-4">
            <Skeleton className="h-8 w-3/4" />
            <Skeleton className="h-6 w-1/4" />
            <Skeleton className="h-24 w-full" />
          </div>
        </div>
      </div>
    )
  }

  if (!product) {
    return (
      <div className="container max-w-6xl mx-auto px-4 py-16 text-center text-muted-foreground">
        Ürün bulunamadı.
      </div>
    )
  }

  const availableStock = selectedVariant
    ? selectedVariant.stock - selectedVariant.reservedStock
    : 0

  return (
    <div className="container max-w-6xl mx-auto px-4 py-8">
      <Button
        variant="ghost"
        size="sm"
        className="mb-6"
        onClick={() => navigate(-1)}
      >
        <ChevronLeft className="h-4 w-4 mr-1" />
        Geri
      </Button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
        {/* Image */}
        <div className="aspect-square bg-muted rounded-xl overflow-hidden flex items-center justify-center">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
          ) : (
            <span className="text-8xl">🛍️</span>
          )}
        </div>

        {/* Info */}
        <div className="flex flex-col gap-4">
          <div>
            <Badge variant="secondary" className="mb-2">{product.categoryName}</Badge>
            <h1 className="text-2xl font-bold">{product.name}</h1>
          </div>

          <p className="text-3xl font-bold">
            {formatPrice(selectedVariant?.price ?? product.basePrice)}
          </p>

          {product.description && (
            <p className="text-muted-foreground text-sm leading-relaxed">{product.description}</p>
          )}

          <Separator />

          {/* Variants */}
          {product.variants && product.variants.length > 1 && (
            <div>
              <p className="text-sm font-medium mb-2">Seçenek</p>
              <div className="flex flex-wrap gap-2">
                {product.variants.map((v) => (
                  <Button
                    key={v.id}
                    variant={selectedVariant?.id === v.id ? 'default' : 'outline'}
                    size="sm"
                    disabled={v.stock - v.reservedStock === 0}
                    onClick={() => setSelectedVariant(v)}
                  >
                    {v.variantValue}
                    {v.stock - v.reservedStock === 0 && ' (Tükendi)'}
                  </Button>
                ))}
              </div>
            </div>
          )}

          {/* Quantity */}
          <div>
            <p className="text-sm font-medium mb-2">Adet</p>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="icon"
                className="h-8 w-8"
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
              >
                −
              </Button>
              <span className="w-8 text-center font-medium">{quantity}</span>
              <Button
                variant="outline"
                size="icon"
                className="h-8 w-8"
                onClick={() => setQuantity(Math.min(availableStock, quantity + 1))}
                disabled={quantity >= availableStock}
              >
                +
              </Button>
              <span className="text-xs text-muted-foreground ml-2">
                {availableStock > 0 ? `${availableStock} adet stokta` : 'Stokta yok'}
              </span>
            </div>
          </div>

          <Button
            size="lg"
            onClick={handleAddToCart}
            disabled={adding || availableStock === 0}
            className="mt-2"
          >
            {adding ? (
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            ) : (
              <ShoppingCart className="mr-2 h-4 w-4" />
            )}
            {availableStock === 0 ? 'Stokta Yok' : 'Sepete Ekle'}
          </Button>
        </div>
      </div>
    </div>
  )
}