import { useEffect, useState, useCallback } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { Search, SlidersHorizontal } from 'lucide-react'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { api } from '@/lib/axios'
import { formatPrice } from '@/lib/utils'
import type { Product, Category, PageResponse } from '@/types'

export default function ProductsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)

  const page = Number(searchParams.get('page') ?? 0)
  const search = searchParams.get('search') ?? ''
  const categoryId = searchParams.get('categoryId') ?? ''
  const sort = searchParams.get('sort') ?? 'createdAt,desc'

  const fetchProducts = useCallback(async () => {
    setLoading(true)
    try {
      const params: Record<string, string | number> = { page, size: 12, sort: sort ?? 'createdAt,desc' }
      if (search) params.search = search
      if (categoryId) params.categoryId = categoryId

      const { data } = await api.get<{ data: PageResponse<Product> }>('/api/products', { params })
      setProducts(data.data.content)
      setTotalPages(data.data.totalPages)
    } finally {
      setLoading(false)
    }
  }, [page, search, categoryId, sort])

  useEffect(() => {
    fetchProducts()
  }, [fetchProducts])

  useEffect(() => {
    api.get<{ data: Category[] }>('/api/categories').then(({ data }) => setCategories(data.data))
  }, [])

  const setParam = (key: string, value: string) => {
    const next = new URLSearchParams(searchParams)
    if (value) next.set(key, value)
    else next.delete(key)
    next.delete('page')
    setSearchParams(next)
  }

  return (
    <div className="container max-w-6xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold mb-1">Ürünler</h1>
        <p className="text-muted-foreground text-sm">Tüm ürünlerimizi keşfedin</p>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3 mb-6">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Ürün ara..."
            className="pl-9"
            defaultValue={search}
            onChange={(e) => setParam('search', e.target.value)}
          />
        </div>

        <Select value={categoryId || 'all'} onValueChange={(v: string | null) => setParam('categoryId', v === 'all' || !v ? '' : v)}>
          <SelectTrigger className="w-full sm:w-48">
            <SlidersHorizontal className="h-4 w-4 mr-2" />
            <SelectValue placeholder="Kategori" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Tüm Kategoriler</SelectItem>
            {categories.map((c) => (
              <SelectItem key={c.id} value={c.id}>{c.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select value={sort ?? 'createdAt,desc'} onValueChange={(v: string | null) => v && setParam('sort', v)}>
          <SelectTrigger className="w-full sm:w-48">
            <SelectValue placeholder="Sırala" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="createdAt,desc">En Yeni</SelectItem>
            <SelectItem value="basePrice,asc">Fiyat: Düşükten Yükseğe</SelectItem>
            <SelectItem value="basePrice,desc">Fiyat: Yüksekten Düşüğe</SelectItem>
            <SelectItem value="name,asc">A-Z</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Product Grid */}
      {loading ? (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {Array.from({ length: 12 }).map((_, i) => (
            <div key={i} className="space-y-3">
              <Skeleton className="h-48 w-full rounded-lg" />
              <Skeleton className="h-4 w-3/4" />
              <Skeleton className="h-4 w-1/2" />
            </div>
          ))}
        </div>
      ) : products.length === 0 ? (
        <div className="text-center py-16 text-muted-foreground">
          <p>Ürün bulunamadı.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {products.map((product) => (
            <Link key={product.id} to={`/products/${product.slug}`}>
              <Card className="h-full hover:shadow-md transition-shadow group overflow-hidden">
                <div className="aspect-square bg-muted flex items-center justify-center overflow-hidden">
                  {product.imageUrl ? (
                    <img
                      src={product.imageUrl}
                      alt={product.name}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                  ) : (
                    <div className="h-24 w-24 rounded-full bg-muted-foreground/10 flex items-center justify-center text-4xl">
                      🛍️
                    </div>
                  )}
                </div>
                <CardContent className="p-3">
                  <Badge variant="secondary" className="text-xs mb-1">
                    {product.categoryName}
                  </Badge>
                  <h3 className="font-medium text-sm line-clamp-2 group-hover:text-primary transition-colors">
                    {product.name}
                  </h3>
                  <p className="font-semibold mt-1">{formatPrice(product.basePrice)}</p>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-8">
          <Button
            variant="outline"
            size="sm"
            disabled={page === 0}
            onClick={() => setParam('page', String(page - 1))}
          >
            Önceki
          </Button>
          <span className="flex items-center px-3 text-sm text-muted-foreground">
            {page + 1} / {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            disabled={page >= totalPages - 1}
            onClick={() => setParam('page', String(page + 1))}
          >
            Sonraki
          </Button>
        </div>
      )}
    </div>
  )
}
