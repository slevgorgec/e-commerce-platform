import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Package } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { api } from '@/lib/axios'
import { formatPrice, formatDate, ORDER_STATUS_LABELS, ORDER_STATUS_COLORS } from '@/lib/utils'
import { cn } from '@/lib/utils'
import type { Order, PageResponse } from '@/types'

export default function OrdersPage() {
  const navigate = useNavigate()
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api
      .get<{ data: PageResponse<Order> }>('/api/orders', { params: { page: 0, size: 20 } })
      .then(({ data }) => setOrders(data.data.content))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="container max-w-4xl mx-auto px-4 py-8 space-y-3">
        {Array.from({ length: 4 }).map((_, i) => (
          <Skeleton key={i} className="h-24 w-full rounded-lg" />
        ))}
      </div>
    )
  }

  return (
    <div className="container max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Siparişlerim</h1>

      {orders.length === 0 ? (
        <div className="text-center py-16">
          <Package className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
          <h2 className="text-xl font-semibold mb-2">Henüz sipariş yok</h2>
          <p className="text-muted-foreground mb-6">İlk siparişini vermek için ürünleri incele.</p>
          <Button onClick={() => navigate('/products')}>Alışverişe Başla</Button>
        </div>
      ) : (
        <div className="space-y-3">
          {orders.map((order) => (
            <div
              key={order.id}
              className="border rounded-lg p-4 bg-card hover:shadow-sm transition-shadow cursor-pointer"
              onClick={() => navigate(`/orders/${order.id}`)}
            >
              <div className="flex items-center justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <p className="text-sm font-medium">
                      Sipariş #{order.id.slice(-8).toUpperCase()}
                    </p>
                    <Badge
                      className={cn(
                        'text-xs border-0',
                        ORDER_STATUS_COLORS[order.status]
                      )}
                    >
                      {ORDER_STATUS_LABELS[order.status]}
                    </Badge>
                  </div>
                  <p className="text-xs text-muted-foreground">{formatDate(order.createdAt)}</p>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    {order.items?.length ?? 0} ürün
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-semibold">{formatPrice(order.totalAmount)}</p>
                  <Button variant="ghost" size="sm" className="text-xs mt-1 h-7">
                    Detaylar →
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
