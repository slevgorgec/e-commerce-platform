import { Package } from 'lucide-react'
import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer className="border-t bg-background mt-auto">
      <div className="container max-w-6xl mx-auto px-4 py-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          <Link to="/" className="flex items-center gap-2 font-semibold text-foreground">
            <Package className="h-4 w-4" />
            <span>ShopHub</span>
          </Link>
          <nav className="flex items-center gap-6 text-sm text-muted-foreground">
            <Link to="/products" className="hover:text-foreground transition-colors">
              Ürünler
            </Link>
            <Link to="/orders" className="hover:text-foreground transition-colors">
              Siparişler
            </Link>
          </nav>
          <p className="text-xs text-muted-foreground">
            © {new Date().getFullYear()} ShopHub. Tüm hakları saklıdır.
          </p>
        </div>
      </div>
    </footer>
  )
}