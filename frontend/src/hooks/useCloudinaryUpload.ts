import { useState } from 'react'
import { toast } from 'sonner'

const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME as string
const UPLOAD_PRESET = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET as string

export function useCloudinaryUpload(folder: 'products' | 'categories') {
  const [uploading, setUploading] = useState(false)

  const upload = async (file: File): Promise<string | null> => {
    setUploading(true)
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('upload_preset', UPLOAD_PRESET)
      formData.append('folder', folder)

      const res = await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`, {
        method: 'POST',
        body: formData,
      })
      if (!res.ok) throw new Error()
      const json = await res.json()
      toast.success('Görsel yüklendi')
      return json.secure_url as string
    } catch {
      toast.error('Görsel yüklenemedi')
      return null
    } finally {
      setUploading(false)
    }
  }

  return { upload, uploading }
}
