import { useEffect, useState } from 'react'
import { api } from '@/lib/axios'
import type { Product, ProductVariant } from '@/types'

export function useProduct(slug: string | undefined) {
  const [product, setProduct] = useState<Product | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!slug) return
    setLoading(true)
    api
      .get<{ data: Product }>(`/api/products/slug/${slug}`)
      .then(async ({ data }) => {
        const p = data.data
        const variantsRes = await api.get<{ data: ProductVariant[] }>(`/api/products/${p.id}/variants`)
        setProduct({ ...p, variants: variantsRes.data.data })
      })
      .finally(() => setLoading(false))
  }, [slug])

  return { product, loading }
}
