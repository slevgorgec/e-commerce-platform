import { Link } from 'react-router-dom'
import { buttonVariants } from '@/components/ui/button'

export default function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center py-24 text-center px-4">
      <h1 className="text-8xl font-bold text-muted-foreground/30 mb-4">404</h1>
      <h2 className="text-2xl font-semibold mb-2">Sayfa Bulunamadı</h2>
      <p className="text-muted-foreground mb-8 max-w-sm">
        Aradığınız sayfa mevcut değil veya taşınmış olabilir.
      </p>
      <Link to="/" className={buttonVariants()}>Ana Sayfaya Dön</Link>
    </div>
  )
}