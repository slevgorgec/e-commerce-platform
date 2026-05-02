import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from '@/stores/authStore'

interface Props {
  requiredRole?: 'USER' | 'ADMIN'
}

export default function PrivateRoute({ requiredRole }: Props) {
  const { isAuthenticated, user } = useAuthStore()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (requiredRole === 'ADMIN' && user?.role !== 'ADMIN') {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}