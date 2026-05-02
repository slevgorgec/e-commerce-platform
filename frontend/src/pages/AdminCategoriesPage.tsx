import { useEffect, useState, useRef } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { Plus, Pencil, Trash2, Loader2, Upload, X } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
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
import { api } from '@/lib/axios'
import type { Category } from '@/types'

const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME as string
const UPLOAD_PRESET = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET as string

const schema = z.object({
  name: z.string().min(2, 'Ad en az 2 karakter'),
  slug: z.string().min(2, 'Slug en az 2 karakter').regex(/^[a-z0-9-]+$/, 'Sadece küçük harf, rakam ve tire'),
  imageUrl: z.string().optional(),
})

type FormData = z.infer<typeof schema>

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<Category | null>(null)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  const fetchCategories = async () => {
    const { data } = await api.get<{ data: Category[] }>('/api/categories')
    setCategories(data.data)
  }

  useEffect(() => {
    fetchCategories().finally(() => setLoading(false))
  }, [])

  const openCreate = () => {
    reset({ name: '', slug: '', imageUrl: '' })
    setImagePreview(null)
    setEditTarget(null)
    setDialogOpen(true)
  }

  const openEdit = (category: Category) => {
    reset({ name: category.name, slug: category.slug, imageUrl: category.imageUrl ?? '' })
    setImagePreview(category.imageUrl ?? null)
    setEditTarget(category)
    setDialogOpen(true)
  }

  const handleImageUpload = async (file: File) => {
    setUploading(true)
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('upload_preset', UPLOAD_PRESET)
      formData.append('folder', 'categories')

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
      const payload = { ...values, imageUrl: values.imageUrl || null }
      if (editTarget) {
        await api.put(`/api/categories/${editTarget.id}`, payload)
        toast.success('Kategori güncellendi')
      } else {
        await api.post('/api/categories', payload)
        toast.success('Kategori oluşturuldu')
      }
      setDialogOpen(false)
      await fetchCategories()
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'İşlem başarısız'
      toast.error(msg)
    }
  }

  const handleDelete = async (category: Category) => {
    if (!confirm(`"${category.name}" silinsin mi?`)) return
    try {
      await api.delete(`/api/categories/${category.id}`)
      toast.success('Kategori silindi')
      await fetchCategories()
    } catch {
      toast.error('Silinemedi')
    }
  }

  return (
    <div className="container max-w-4xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Kategori Yönetimi</h1>
          <p className="text-sm text-muted-foreground">{categories.length} kategori</p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Yeni Kategori
        </Button>
      </div>

      {loading ? (
        <div className="space-y-2">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      ) : (
        <div className="border rounded-lg overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Görsel</TableHead>
                <TableHead>Ad</TableHead>
                <TableHead>Slug</TableHead>
                <TableHead className="text-right">İşlemler</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {categories.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center text-muted-foreground py-8">
                    Henüz kategori yok
                  </TableCell>
                </TableRow>
              ) : (
                categories.map((category) => (
                  <TableRow key={category.id}>
                    <TableCell>
                      <div className="h-10 w-10 rounded-md bg-muted overflow-hidden flex items-center justify-center">
                        {category.imageUrl ? (
                          <img src={category.imageUrl} alt={category.name} className="w-full h-full object-cover" />
                        ) : (
                          <span className="text-lg">📁</span>
                        )}
                      </div>
                    </TableCell>
                    <TableCell className="font-medium">{category.name}</TableCell>
                    <TableCell className="text-muted-foreground text-sm font-mono">{category.slug}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8"
                          onClick={() => openEdit(category)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-destructive hover:text-destructive"
                          onClick={() => handleDelete(category)}
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
            <DialogTitle>{editTarget ? 'Kategoriyi Düzenle' : 'Yeni Kategori'}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label>Kategori Adı</Label>
              <Input placeholder="Örn: Teknoloji" {...register('name')} />
              {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
            </div>

            <div className="space-y-2">
              <Label>Slug</Label>
              <Input placeholder="Örn: teknoloji" {...register('slug')} />
              {errors.slug && <p className="text-xs text-destructive">{errors.slug.message}</p>}
            </div>

            {/* Image Upload */}
            <div className="space-y-2">
              <Label>Kategori Görseli</Label>
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
