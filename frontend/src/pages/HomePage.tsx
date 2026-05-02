import { Link } from 'react-router-dom'
import { ArrowRight, ShieldCheck, Truck, RefreshCw } from 'lucide-react'
import { buttonVariants } from '@/components/ui/button'
import { cn } from '@/lib/utils'

const features = [
  {
    icon: Truck,
    title: 'Hızlı Teslimat',
    description: 'Siparişleriniz aynı gün kargoya verilir.',
  },
  {
    icon: ShieldCheck,
    title: 'Güvenli Ödeme',
    description: 'Iyzico altyapısıyla güvende öde.',
  },
  {
    icon: RefreshCw,
    title: 'Kolay İade',
    description: '14 gün içinde koşulsuz iade.',
  },
]

export default function HomePage() {
  return (
    <div>
      {/* Hero */}
      <section className="bg-muted/30 border-b">
        <div className="container max-w-6xl mx-auto px-4 py-20 md:py-28 text-center">
          <h1 className="text-4xl md:text-5xl font-bold tracking-tight mb-4">
            Alışverişin En Kolay Hali
          </h1>
          <p className="text-lg text-muted-foreground max-w-xl mx-auto mb-8">
            Binlerce ürün, güvenli ödeme ve hızlı teslimat ile alışveriş deneyimini yeniden keşfet.
          </p>
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <Link to="/products" className={cn(buttonVariants({ size: 'lg' }))}>
              Alışverişe Başla <ArrowRight className="ml-2 h-4 w-4" />
            </Link>
            <Link to="/register" className={cn(buttonVariants({ size: 'lg', variant: 'outline' }))}>
              Ücretsiz Kayıt Ol
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="container max-w-6xl mx-auto px-4 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {features.map(({ icon: Icon, title, description }) => (
            <div key={title} className="flex flex-col items-center text-center gap-3">
              <div className="rounded-full bg-muted p-3">
                <Icon className="h-6 w-6 text-foreground" />
              </div>
              <h3 className="font-semibold">{title}</h3>
              <p className="text-sm text-muted-foreground">{description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="border-t bg-muted/30">
        <div className="container max-w-6xl mx-auto px-4 py-16 text-center">
          <h2 className="text-2xl font-bold mb-3">Tüm ürünleri keşfet</h2>
          <p className="text-muted-foreground mb-6">
            Kategorilere göre filtrele, fiyata göre sırala.
          </p>
          <Link to="/products" className={cn(buttonVariants())}>Ürünlere Git</Link>
        </div>
      </section>
    </div>
  )
}
