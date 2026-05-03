import { useEffect, useState, useCallback } from 'react'
import { api } from '@/lib/axios'
import type { Category } from '@/types'

export function useCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)

  const fetch = useCallback(() => {
    setLoading(true)
    return api
      .get<{ data: Category[] }>('/api/categories')
      .then(({ data }) => setCategories(data.data))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => { fetch() }, [fetch])

  return { categories, loading, refetch: fetch }
}