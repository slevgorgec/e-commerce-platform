import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { toast } from 'sonner'
import { Loader2, Package } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useAuthStore } from '@/stores/authStore'
import { api } from '@/lib/axios'

const schema = z.object({
  email: z.string().email('Geçerli bir e-posta girin'),
  password: z.string().min(8, 'Şifre en az 8 karakter olmalı'),
})

type FormData = z.infer<typeof schema>

export default function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { setAuth } = useAuthStore()
  const from = (location.state as { from?: string })?.from ?? '/'

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (values: FormData) => {
    try {
      const { data: authData } = await api.post('/api/auth/login', values)
      const { accessToken, refreshToken } = authData.data
      // Token'ı geçici olarak set edip /me isteği at
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      const { data: profileData } = await api.get('/api/users/me')
      const user = profileData.data
      setAuth(user, accessToken, refreshToken)
      toast.success(`Hoş geldin, ${user.firstName}!`)
      navigate(from, { replace: true })
    } catch (err: unknown) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Giriş başarısız'
      toast.error(msg)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/30 px-4">
      <div className="w-full max-w-sm">
        <div className="flex justify-center mb-6">
          <Link to="/" className="flex items-center gap-2 font-semibold text-foreground">
            <Package className="h-6 w-6" />
            <span className="text-xl">ShopHub</span>
          </Link>
        </div>

        <Card>
          <CardHeader className="text-center">
            <CardTitle className="text-2xl">Giriş Yap</CardTitle>
            <CardDescription>Hesabına giriş yapmak için bilgilerini gir</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">E-posta</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="ornek@email.com"
                  {...register('email')}
                />
                {errors.email && (
                  <p className="text-xs text-destructive">{errors.email.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Şifre</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  {...register('password')}
                />
                {errors.password && (
                  <p className="text-xs text-destructive">{errors.password.message}</p>
                )}
              </div>

              <Button type="submit" className="w-full" disabled={isSubmitting}>
                {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Giriş Yap
              </Button>
            </form>

            <p className="mt-4 text-center text-sm text-muted-foreground">
              Hesabın yok mu?{' '}
              <Link to="/register" className="font-medium text-foreground hover:underline">
                Kayıt Ol
              </Link>
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}