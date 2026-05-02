import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { Loader2, Package } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { api } from '@/lib/axios'

const schema = z
  .object({
    firstName: z.string().min(2, 'Ad en az 2 karakter olmalı'),
    lastName: z.string().min(2, 'Soyad en az 2 karakter olmalı'),
    email: z.string().email('Geçerli bir e-posta girin'),
    password: z.string().min(8, 'Şifre en az 8 karakter olmalı'),
    confirmPassword: z.string(),
  })
  .refine((d) => d.password === d.confirmPassword, {
    message: 'Şifreler eşleşmiyor',
    path: ['confirmPassword'],
  })

type FormData = z.infer<typeof schema>

export default function RegisterPage() {
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (values: FormData) => {
    try {
      await api.post('/api/auth/register', {
        firstName: values.firstName,
        lastName: values.lastName,
        email: values.email,
        password: values.password,
      })
      toast.success('Hesabın oluşturuldu! Giriş yapabilirsin.')
      navigate('/login')
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Kayıt başarısız'
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
            <CardTitle className="text-2xl">Kayıt Ol</CardTitle>
            <CardDescription>Ücretsiz hesap oluştur, alışverişe başla</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-2">
                  <Label htmlFor="firstName">Ad</Label>
                  <Input id="firstName" placeholder="Ali" {...register('firstName')} />
                  {errors.firstName && (
                    <p className="text-xs text-destructive">{errors.firstName.message}</p>
                  )}
                </div>
                <div className="space-y-2">
                  <Label htmlFor="lastName">Soyad</Label>
                  <Input id="lastName" placeholder="Yılmaz" {...register('lastName')} />
                  {errors.lastName && (
                    <p className="text-xs text-destructive">{errors.lastName.message}</p>
                  )}
                </div>
              </div>

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

              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Şifre Tekrar</Label>
                <Input
                  id="confirmPassword"
                  type="password"
                  placeholder="••••••••"
                  {...register('confirmPassword')}
                />
                {errors.confirmPassword && (
                  <p className="text-xs text-destructive">{errors.confirmPassword.message}</p>
                )}
              </div>

              <Button type="submit" className="w-full" disabled={isSubmitting}>
                {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Kayıt Ol
              </Button>
            </form>

            <p className="mt-4 text-center text-sm text-muted-foreground">
              Zaten hesabın var mı?{' '}
              <Link to="/login" className="font-medium text-foreground hover:underline">
                Giriş Yap
              </Link>
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
