import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ChevronLeft } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { Skeleton } from '@/components/ui/skeleton'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { api } from '@/lib/axios'
import { formatPrice, formatDate, ORDER_STATUS_LABELS, ORDER_STATUS_COLORS, cn } from '@/lib/utils'
import type { Order } from '@/types'

export default function OrderDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [order, setOrder] = useState<Order | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api
      .get<{ data: Order }>(`/api/orders/${id}`)
      .then(({ data }) => setOrder(data.data))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) {
    return (
      <div className="container max-w-4xl mx-auto px-4 py-8 space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-48 w-full rounded-lg" />
        <Skeleton className="h-32 w-full rounded-lg" />
      </div>
    )
  }

  if (!order) {
    return (
      <div className="container max-w-4xl mx-auto px-4 py-16 text-center text-muted-foreground">
        Sipariş bulunamadı.
      </div>
    )
  }

  return (
    <div className="container max-w-4xl mx-auto px-4 py-8">
      <Button variant="ghost" size="sm" className="mb-6" onClick={() => navigate('/orders')}>
        <ChevronLeft className="h-4 w-4 mr-1" />
        Siparişlerim
      </Button>

      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl font-bold">
            Sipariş #{order.id.slice(-8).toUpperCase()}
          </h1>
          <p className="text-sm text-muted-foreground">{formatDate(order.createdAt)}</p>
        </div>
        <Badge className={cn('border-0', ORDER_STATUS_COLORS[order.status])}>
          {ORDER_STATUS_LABELS[order.status]}
        </Badge>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Items */}
        <div className="md:col-span-2 space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Ürünler</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {order.items?.map((item) => (
                <div key={item.id} className="flex justify-between items-center">
                  <div className="flex-1">
                    <p className="text-sm font-medium">{item.productNameSnapshot}</p>
                    <p className="text-xs text-muted-foreground">
                      {item.variantValueSnapshot} × {item.quantity}
                    </p>
                  </div>
                  <p className="text-sm font-semibold">
                    {formatPrice(item.unitPriceSnapshot * item.quantity)}
                  </p>
                </div>
              ))}
              <Separator />
              <div className="flex justify-between font-semibold">
                <span>Toplam</span>
                <span>{formatPrice(order.totalAmount)}</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Shipping */}
        <div>
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Teslimat Adresi</CardTitle>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground space-y-1">
              <p className="font-medium text-foreground">{order.shippingAddress.fullName}</p>
              <p>{order.shippingAddress.phone}</p>
              <p>{order.shippingAddress.addressLine1}</p>
              {order.shippingAddress.addressLine2 && <p>{order.shippingAddress.addressLine2}</p>}
              <p>
                {order.shippingAddress.district}, {order.shippingAddress.city}{' '}
                {order.shippingAddress.postalCode}
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
