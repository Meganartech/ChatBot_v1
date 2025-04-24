import React from 'react'
import { Navigate } from 'react-router-dom'

const ProtectedRoute = ({ children }) => {
  const token = sessionStorage.getItem('token')
  const isAuthenticated = !!token

  return isAuthenticated ? children : <Navigate to="/login" />
}

export default ProtectedRoute
