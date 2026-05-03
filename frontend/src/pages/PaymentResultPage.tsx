import { useSearchParams, useNavigate } from 'react-router-dom'
import { CheckCircle, XCircle, AlertCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export default function PaymentResultPage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()

  const status = params.get('status')
  const orderId = params.get('orderId')

  const isSuccess = status === 'success'
  const isFailure = status === 'failure'

  return (
    <div className="container max-w-md mx-auto px-4 py-16">
      <Card>
        <CardHeader className="text-center">
          <div className="flex justify-center mb-4">
            {isSuccess && <CheckCircle className="h-16 w-16 text-green-500" />}
            {isFailure && <XCircle className="h-16 w-16 text-red-500" />}
            {!isSuccess && !isFailure && <AlertCircle className="h-16 w-16 text-yellow-500" />}
          </div>
          <CardTitle className="text-xl">
            {isSuccess && 'Ödeme Başarılı!'}
            {isFailure && 'Ödeme Başarısız'}
            {!isSuccess && !isFailure && 'Ödeme Durumu Bilinmiyor'}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4 text-center">
          <p className="text-muted-foreground">
            {isSuccess && 'Siparişiniz onaylandı. Teslimat bilgileriniz e-posta ile iletilecektir.'}
            {isFailure && 'Ödemeniz alınamadı. Lütfen tekrar deneyiniz.'}
            {!isSuccess && !isFailure && 'Ödeme durumu belirlenemiyor. Siparişlerinizi kontrol edin.'}
          </p>

          <div className="flex flex-col gap-2 pt-2">
            {isSuccess && orderId && (
              <Button onClick={() => navigate(`/orders/${orderId}`)}>
                Sipariş Detayını Görüntüle
              </Button>
            )}
            {isFailure && (
              <Button variant="destructive" onClick={() => navigate('/checkout')}>
                Tekrar Dene
              </Button>
            )}
            <Button variant="outline" onClick={() => navigate('/orders')}>
              Siparişlerim
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
