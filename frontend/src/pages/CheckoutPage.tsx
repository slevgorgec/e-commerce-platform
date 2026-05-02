import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { Loader2 } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useCartStore } from '@/stores/cartStore'
import { api } from '@/lib/axios'
import { formatPrice } from '@/lib/utils'

const schema = z.object({
  fullName: z.string().min(3, 'Ad soyad en az 3 karakter'),
  phone: z.string().regex(/^[0-9]{10,11}$/, 'Geçerli telefon numarası girin'),
  addressLine1: z.string().min(5, 'Adres en az 5 karakter'),
  addressLine2: z.string().optional(),
  city: z.string().min(2, 'Şehir girin'),
  district: z.string().min(2, 'İlçe girin'),
  postalCode: z.string().regex(/^\d{5}$/, '5 haneli posta kodu'),
})

type FormData = z.infer<typeof schema>

export default function CheckoutPage() {
  const navigate = useNavigate()
  const { cart, totalPrice, clearCart } = useCartStore()

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (values: FormData) => {
    if (!cart?.items?.length) {
      toast.error('Sepetiniz boş')
      return
    }
    try {
      const payload = {
        shippingAddress: values,
        items: cart.items.map((i) => ({
          productId: i.productId,
          variantId: i.variantId,
          quantity: i.quantity,
        })),
      }
      const { data } = await api.post('/api/orders', payload)
      clearCart()
      toast.success('Siparişiniz alındı!')
      navigate(`/orders/${data.data.id}`)
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Sipariş oluşturulamadı'
      toast.error(msg)
    }
  }

  const items = cart?.items ?? []

  return (
    <div className="container max-w-5xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Siparişi Tamamla</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Address Form */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Teslimat Adresi</CardTitle>
            </CardHeader>
            <CardContent>
              <form id="checkout-form" onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>Ad Soyad</Label>
                    <Input placeholder="Ali Yılmaz" {...register('fullName')} />
                    {errors.fullName && <p className="text-xs text-destructive">{errors.fullName.message}</p>}
                  </div>
                  <div className="space-y-2">
                    <Label>Telefon</Label>
                    <Input placeholder="05001234567" {...register('phone')} />
                    {errors.phone && <p className="text-xs text-destructive">{errors.phone.message}</p>}
                  </div>
                </div>

                <div className="space-y-2">
                  <Label>Adres</Label>
                  <Input placeholder="Mahalle, cadde, sokak, no" {...register('addressLine1')} />
                  {errors.addressLine1 && <p className="text-xs text-destructive">{errors.addressLine1.message}</p>}
                </div>

                <div className="space-y-2">
                  <Label>Adres 2 (opsiyonel)</Label>
                  <Input placeholder="Daire, blok vb." {...register('addressLine2')} />
                </div>

                <div className="grid grid-cols-3 gap-3">
                  <div className="space-y-2">
                    <Label>Şehir</Label>
                    <Input placeholder="İstanbul" {...register('city')} />
                    {errors.city && <p className="text-xs text-destructive">{errors.city.message}</p>}
                  </div>
                  <div className="space-y-2">
                    <Label>İlçe</Label>
                    <Input placeholder="Kadıköy" {...register('district')} />
                    {errors.district && <p className="text-xs text-destructive">{errors.district.message}</p>}
                  </div>
                  <div className="space-y-2">
                    <Label>Posta Kodu</Label>
                    <Input placeholder="34700" {...register('postalCode')} />
                    {errors.postalCode && <p className="text-xs text-destructive">{errors.postalCode.message}</p>}
                  </div>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>

        {/* Order Summary */}
        <div>
          <Card className="sticky top-20">
            <CardHeader>
              <CardTitle className="text-base">Sipariş Özeti</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {items.map((item) => (
                <div key={item.variantId} className="flex justify-between text-sm">
                  <span className="text-muted-foreground line-clamp-1 flex-1">
                    {item.productName} ({item.variantValue}) × {item.quantity}
                  </span>
                  <span className="ml-2 font-medium">
                    {formatPrice(item.priceSnapshot * item.quantity)}
                  </span>
                </div>
              ))}
              <Separator />
              <div className="flex justify-between font-semibold">
                <span>Toplam</span>
                <span>{formatPrice(totalPrice())}</span>
              </div>
              <Button
                form="checkout-form"
                type="submit"
                className="w-full"
                disabled={isSubmitting || items.length === 0}
              >
                {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Siparişi Ver
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
