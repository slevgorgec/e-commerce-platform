import { Link, useNavigate } from 'react-router-dom'
import { ShoppingCart, User, LogOut, Package, LayoutDashboard, Menu } from 'lucide-react'
import { Button, buttonVariants } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet'
import { useAuthStore } from '@/stores/authStore'
import { useCartStore } from '@/stores/cartStore'
import { cn } from '@/lib/utils'
import { useEffect } from 'react'

export default function Navbar() {
  const navigate = useNavigate()
  const { user, isAuthenticated, clearAuth } = useAuthStore()
  const { totalItems, fetchCart } = useCartStore()

  useEffect(() => {
    if (isAuthenticated) fetchCart()
  }, [isAuthenticated, fetchCart])

  const handleLogout = () => {
    clearAuth()
    navigate('/login')
  }

  const cartCount = totalItems()

  const NavLinks = () => (
    <>
      <Link
        to="/products"
        className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
      >
        Ürünler
      </Link>
      {user?.role === 'ADMIN' && (
        <>
          <Link
            to="/admin/products"
            className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
          >
            Ürün Yönetimi
          </Link>
          <Link
            to="/admin/categories"
            className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
          >
            Kategori Yönetimi
          </Link>
        </>
      )}
    </>
  )

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-14 max-w-6xl items-center justify-between mx-auto px-4">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 font-semibold text-foreground">
          <Package className="h-5 w-5" />
          <span>ShopHub</span>
        </Link>

        {/* Desktop nav */}
        <nav className="hidden md:flex items-center gap-6">
          <NavLinks />
        </nav>

        {/* Actions */}
        <div className="flex items-center gap-2">
          {isAuthenticated && (
            <Button
              variant="ghost"
              size="icon"
              className="relative"
              onClick={() => navigate('/cart')}
            >
              <ShoppingCart className="h-5 w-5" />
              {cartCount > 0 && (
                <Badge className="absolute -top-1 -right-1 h-5 w-5 rounded-full p-0 flex items-center justify-center text-xs">
                  {cartCount > 99 ? '99+' : cartCount}
                </Badge>
              )}
            </Button>
          )}

          {isAuthenticated ? (
            <DropdownMenu>
              <DropdownMenuTrigger className={cn(buttonVariants({ variant: 'ghost', size: 'icon' }))}>
                <User className="h-5 w-5" />
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48">
                <div className="px-2 py-1.5">
                  <p className="text-sm font-medium">{user?.firstName} {user?.lastName}</p>
                  <p className="text-xs text-muted-foreground">{user?.email}</p>
                </div>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => navigate('/orders')}>
                  <Package className="mr-2 h-4 w-4" />
                  Siparişlerim
                </DropdownMenuItem>
                {user?.role === 'ADMIN' && (
                  <>
                    <DropdownMenuItem onClick={() => navigate('/admin/products')}>
                      <LayoutDashboard className="mr-2 h-4 w-4" />
                      Ürün Yönetimi
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => navigate('/admin/categories')}>
                      <LayoutDashboard className="mr-2 h-4 w-4" />
                      Kategori Yönetimi
                    </DropdownMenuItem>
                  </>
                )}
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleLogout} className="text-destructive">
                  <LogOut className="mr-2 h-4 w-4" />
                  Çıkış Yap
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <div className="hidden md:flex items-center gap-2">
              <Button variant="ghost" size="sm" onClick={() => navigate('/login')}>
                Giriş Yap
              </Button>
              <Button size="sm" onClick={() => navigate('/register')}>
                Kayıt Ol
              </Button>
            </div>
          )}

          {/* Mobile menu */}
          <Sheet>
            <SheetTrigger className={cn(buttonVariants({ variant: 'ghost', size: 'icon' }), 'md:hidden')}>
              <Menu className="h-5 w-5" />
            </SheetTrigger>
            <SheetContent side="right" className="w-64">
              <nav className="flex flex-col gap-4 mt-8">
                <NavLinks />
                {!isAuthenticated && (
                  <>
                    <Button variant="ghost" onClick={() => navigate('/login')}>
                      Giriş Yap
                    </Button>
                    <Button onClick={() => navigate('/register')}>Kayıt Ol</Button>
                  </>
                )}
              </nav>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>
  )
}
