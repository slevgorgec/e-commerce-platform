import { useEffect, useState, useRef } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { Plus, Pencil, Trash2, Loader2, Upload, X } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { api } from '@/lib/axios'
import { formatPrice } from '@/lib/utils'
import type { Product, Category } from '@/types'

const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME as string
const UPLOAD_PRESET = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET as string

const schema = z.object({
  name: z.string().min(2, 'Ad en az 2 karakter'),
  description: z.string().optional(),
  categoryId: z.string().min(1, 'Kategori seçin'),
  basePrice: z.string().refine((v) => !isNaN(Number(v)) && Number(v) > 0, 'Fiyat pozitif olmalı'),
  imageUrl: z.string().optional(),
})

type FormData = z.infer<typeof schema>

export default function AdminProductsPage() {
  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<Product | null>(null)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  const fetchProducts = async () => {
    const { data } = await api.get('/api/products', { params: { size: 100 } })
    setProducts(data.data.content)
  }

  useEffect(() => {
    Promise.all([
      fetchProducts(),
      api.get<{ data: Category[] }>('/api/categories').then(({ data }) => setCategories(data.data)),
    ]).finally(() => setLoading(false))
  }, [])

  const openCreate = () => {
    reset({ name: '', description: '', categoryId: '', basePrice: '', imageUrl: '' })
    setImagePreview(null)
    setEditTarget(null)
    setDialogOpen(true)
  }

  const openEdit = (product: Product) => {
    reset({
      name: product.name,
      description: product.description ?? '',
      categoryId: product.categoryId,
      basePrice: String(product.basePrice),
      imageUrl: product.imageUrl ?? '',
    })
    setImagePreview(product.imageUrl ?? null)
    setEditTarget(product)
    setDialogOpen(true)
  }

  const handleImageUpload = async (file: File) => {
    setUploading(true)
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('upload_preset', UPLOAD_PRESET)
      formData.append('folder', 'products')

      const res = await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`, {
        method: 'POST',
        body: formData,
      })
      if (!res.ok) throw new Error('Yükleme başarısız')
      const json = await res.json()
      const url: string = json.secure_url
      setValue('imageUrl', url, { shouldValidate: true })
      setImagePreview(url)
      toast.success('Görsel yüklendi')
    } catch {
      toast.error('Görsel yüklenemedi')
    } finally {
      setUploading(false)
    }
  }

  const onSubmit = async (values: FormData) => {
    try {
      const payload = {
        ...values,
        basePrice: Number(values.basePrice),
        imageUrl: values.imageUrl || null,
      }
      if (editTarget) {
        await api.put(`/api/products/${editTarget.id}`, payload)
        toast.success('Ürün güncellendi')
      } else {
        await api.post('/api/products', payload)
        toast.success('Ürün oluşturuldu')
      }
      setDialogOpen(false)
      await fetchProducts()
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'İşlem başarısız'
      toast.error(msg)
    }
  }

  const handleDelete = async (product: Product) => {
    if (!confirm(`"${product.name}" silinsin mi?`)) return
    try {
      await api.delete(`/api/products/${product.id}`)
      toast.success('Ürün silindi')
      await fetchProducts()
    } catch {
      toast.error('Silinemedi')
    }
  }

  return (
    <div className="container max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Ürün Yönetimi</h1>
          <p className="text-sm text-muted-foreground">{products.length} ürün</p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Yeni Ürün
        </Button>
      </div>

      {loading ? (
        <div className="space-y-2">
          {Array.from({ length: 5 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      ) : (
        <div className="border rounded-lg overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Görsel</TableHead>
                <TableHead>Ürün</TableHead>
                <TableHead>Kategori</TableHead>
                <TableHead>Fiyat</TableHead>
                <TableHead>Durum</TableHead>
                <TableHead className="text-right">İşlemler</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {products.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center text-muted-foreground py-8">
                    Henüz ürün yok
                  </TableCell>
                </TableRow>
              ) : (
                products.map((product) => (
                  <TableRow key={product.id}>
                    <TableCell>
                      <div className="h-10 w-10 rounded-md bg-muted overflow-hidden flex items-center justify-center">
                        {product.imageUrl ? (
                          <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
                        ) : (
                          <span className="text-lg">🛍️</span>
                        )}
                      </div>
                    </TableCell>
                    <TableCell className="font-medium">{product.name}</TableCell>
                    <TableCell className="text-muted-foreground text-sm">
                      {product.categoryName}
                    </TableCell>
                    <TableCell>{formatPrice(product.basePrice)}</TableCell>
                    <TableCell>
                      <Badge variant={product.active ? 'default' : 'secondary'}>
                        {product.active ? 'Aktif' : 'Pasif'}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8"
                          onClick={() => openEdit(product)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-destructive hover:text-destructive"
                          onClick={() => handleDelete(product)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      )}

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editTarget ? 'Ürünü Düzenle' : 'Yeni Ürün'}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label>Ürün Adı</Label>
              <Input placeholder="Örn: Pamuklu T-Shirt" {...register('name')} />
              {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
            </div>

            <div className="space-y-2">
              <Label>Açıklama</Label>
              <Textarea
                placeholder="Ürün hakkında kısa açıklama..."
                rows={3}
                {...register('description')}
              />
            </div>

            <div className="space-y-2">
              <Label>Kategori</Label>
              <Select
                value={watch('categoryId')}
                onValueChange={(v: string | null) => v && setValue('categoryId', v, { shouldValidate: true })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Kategori seçin" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((c) => (
                    <SelectItem key={c.id} value={c.id}>{c.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.categoryId && (
                <p className="text-xs text-destructive">{errors.categoryId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label>Fiyat (TL)</Label>
              <Input type="number" step="0.01" placeholder="0.00" {...register('basePrice')} />
              {errors.basePrice && (
                <p className="text-xs text-destructive">{errors.basePrice.message}</p>
              )}
            </div>

            {/* Image Upload */}
            <div className="space-y-2">
              <Label>Ürün Görseli</Label>
              <input type="hidden" {...register('imageUrl')} />
              {imagePreview ? (
                <div className="relative rounded-lg overflow-hidden border">
                  <img src={imagePreview} alt="Önizleme" className="w-full h-40 object-cover" />
                  <Button
                    type="button"
                    variant="destructive"
                    size="icon"
                    className="absolute top-2 right-2 h-7 w-7"
                    onClick={() => {
                      setImagePreview(null)
                      setValue('imageUrl', '', { shouldValidate: true })
                    }}
                  >
                    <X className="h-3 w-3" />
                  </Button>
                </div>
              ) : (
                <div
                  className="border-2 border-dashed border-muted-foreground/25 rounded-lg p-6 text-center cursor-pointer hover:border-muted-foreground/50 transition-colors"
                  onClick={() => fileInputRef.current?.click()}
                >
                  {uploading ? (
                    <div className="flex flex-col items-center gap-2 text-muted-foreground">
                      <Loader2 className="h-6 w-6 animate-spin" />
                      <span className="text-sm">Yükleniyor...</span>
                    </div>
                  ) : (
                    <div className="flex flex-col items-center gap-2 text-muted-foreground">
                      <Upload className="h-6 w-6" />
                      <span className="text-sm">Görsel seçmek için tıklayın</span>
                      <span className="text-xs">JPG, PNG, WEBP — maks 10MB</span>
                    </div>
                  )}
                </div>
              )}
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files?.[0]
                  if (file) handleImageUpload(file)
                  e.target.value = ''
                }}
              />
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setDialogOpen(false)}>
                İptal
              </Button>
              <Button type="submit" disabled={isSubmitting || uploading}>
                {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                {editTarget ? 'Güncelle' : 'Oluştur'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  )
}
