import React, { Suspense, useEffect } from 'react'
import { HashRouter, Routes, Route } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { CSpinner, useColorModes } from '@coreui/react'

import './scss/style.scss'
import './scss/examples.scss'

// Lazy load components
const DefaultLayout = React.lazy(() => import('./layout/DefaultLayout'))
const AdministrationLayout = React.lazy(() => import('./layout/AdministrationLayout'))
const Login = React.lazy(() => import('./views/pages/login/Login'))
const Register = React.lazy(() => import('./views/pages/register/Register'))
const Page404 = React.lazy(() => import('./views/pages/page404/Page404'))
const Page500 = React.lazy(() => import('./views/pages/page500/Page500'))
const Dashboard = React.lazy(() => import('./views/dashboard/Dashboard'))
const ProtectedRoute = React.lazy(() => import('./components/ProtectedRoute'))

const App = () => {
  const { isColorModeSet, setColorMode } = useColorModes('coreui-free-react-admin-template-theme')
  const storedTheme = useSelector((state) => state.theme)

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.href.split('?')[1])
    const theme = urlParams.get('theme') && urlParams.get('theme').match(/^[A-Za-z0-9\s]+/)[0]
    if (theme) {
      setColorMode(theme)
    }

    if (!isColorModeSet()) {
      setColorMode(storedTheme)
    }
  }, []) // eslint-disable-line react-hooks/exhaustive-deps


  return (
    <HashRouter>
      <Suspense
        fallback={
          <div className="pt-3 text-center">
            <CSpinner color="primary" variant="grow" />
          </div>
        }
      >
        <Routes>
          {/* Auth pages */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/404" element={<Page404 />} />
          <Route path="/500" element={<Page500 />} />

          {/* Protected Application Layout */}
          <Route
            path="/*"
            element={
              <ProtectedRoute>
                <DefaultLayout />
              </ProtectedRoute>
            }
          >
            {/* Nested under DefaultLayout */}
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="administration/:section" element={<AdministrationLayout />} />
          </Route>
        </Routes>
      </Suspense>
    </HashRouter>
  )
}

export default App
