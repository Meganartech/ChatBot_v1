import React, { useEffect, useRef, useState } from 'react'
import { useNavigate, NavLink, useLocation } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { toast } from 'react-toastify'
import {
  CContainer,
  CHeader,
  CHeaderNav,
  CHeaderToggler,
  CNavLink,
  CNavItem,
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  useColorModes,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import {
  cilBell,
  cilContrast,
  cilMoon,
  cilSun,
  cilMenu,
  cilAccountLogout,
  cilUser,
} from '@coreui/icons'
import { AppBreadcrumb } from './index'
import { AppHeaderDropdown } from './header/index'

const AppHeader = () => {
  const headerRef = useRef()
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const location = useLocation()
  const sidebarShow = useSelector((state) => state.sidebarShow)
  const { colorMode, setColorMode } = useColorModes('coreui-free-react-admin-template-theme')

  const [admin, setAdmin] = useState(null)

  // Fetch Admin from localStorage
  useEffect(() => {
    const storedAdmin = localStorage.getItem("admin")
    if (storedAdmin) {
      setAdmin(JSON.parse(storedAdmin))
    }
  }, [])

  // Scroll Shadow Effect
  useEffect(() => {
    const handleScroll = () => {
      if (headerRef.current) {
        headerRef.current.classList.toggle('shadow-sm', document.documentElement.scrollTop > 0)
      }
    }
    document.addEventListener('scroll', handleScroll)
    return () => document.removeEventListener('scroll', handleScroll)
  }, [])

  // Token Validation
  // useEffect(() => {
  //   const token = localStorage.getItem('token')
  //   if (!token) return navigate('/login')

  //   const validateToken = async () => {
  //     try {
  //       const res = await fetch('http://localhost:8080/auth/validate', {
  //         method: 'POST',
  //         headers: {
  //           Authorization: `Bearer ${token}`,
  //         },
  //       })
  //       const data = await res.json()
  //       if (data.status !== 'success') {
  //         toast.error("Session expired. Please login again.")
  //         localStorage.removeItem("token")
  //         localStorage.removeItem("admin")
  //         navigate('/login')
  //       }
  //     } catch (err) {
  //       toast.error("Session validation failed!")
  //       navigate('/login')
  //     }
  //   }

  //   validateToken()
  //   const interval = setInterval(validateToken, 30000)
  //   return () => clearInterval(interval)
  // }, [location])

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem("token")
      if (token) {
        await fetch("http://localhost:8080/auth/logout", {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
      }
      localStorage.removeItem("token")
      localStorage.removeItem("admin")
      navigate("/login")
    } catch (err) {
      toast.error("Something went wrong.")
    }
  }

  return (
    <CHeader position="sticky" className="mb-2 p-0" ref={headerRef}>
      <CContainer className="border-bottom px-4" fluid>
        <CHeaderToggler
          onClick={() => dispatch({ type: 'set', sidebarShow: !sidebarShow })}
          style={{ marginInlineStart: '-14px' }}
        >
          <CIcon icon={cilMenu} size="lg" />
        </CHeaderToggler>

        <CHeaderNav className="d-none d-md-flex">
          <CNavItem>
            <CNavLink to="/dashboard" as={NavLink}>
              Dashboard
            </CNavLink>
          </CNavItem>
        </CHeaderNav>

        <CHeaderNav className="ms-auto">
          <CNavItem>
            <CNavLink href="#">
              <CIcon icon={cilBell} size="lg" />
            </CNavLink>
          </CNavItem>
        </CHeaderNav>

        {/* Theme Toggle */}
        <CHeaderNav>
          <li className="nav-item py-1">
            <div className="vr h-100 mx-2 text-body text-opacity-75"></div>
          </li>
          <CDropdown variant="nav-item" placement="bottom-end">
            <CDropdownToggle caret={false}>
              {colorMode === 'dark' ? (
                <CIcon icon={cilMoon} size="lg" />
              ) : colorMode === 'auto' ? (
                <CIcon icon={cilContrast} size="lg" />
              ) : (
                <CIcon icon={cilSun} size="lg" />
              )}
            </CDropdownToggle>
            <CDropdownMenu>
              <CDropdownItem onClick={() => setColorMode('light')} active={colorMode === 'light'}>
                <CIcon className="me-2" icon={cilSun} /> Light
              </CDropdownItem>
              <CDropdownItem onClick={() => setColorMode('dark')} active={colorMode === 'dark'}>
                <CIcon className="me-2" icon={cilMoon} /> Dark
              </CDropdownItem>
              <CDropdownItem onClick={() => setColorMode('auto')} active={colorMode === 'auto'}>
                <CIcon className="me-2" icon={cilContrast} /> Auto
              </CDropdownItem>
            </CDropdownMenu>
          </CDropdown>
          <li className="nav-item py-1">
            <div className="vr h-100 mx-2 text-body text-opacity-75"></div>
          </li>

          {/* Profile Dropdown */}
          <CDropdown variant="nav-item" placement="bottom-end">
            <CDropdownToggle caret={false}>
              <CIcon icon={cilUser} size="lg" />
              <span className="ms-2">{admin?.name || 'Admin'}</span>
            </CDropdownToggle>
            <CDropdownMenu>
              <CDropdownItem onClick={() => navigate("/profile")}>
                <CIcon className="me-2" icon={cilUser} /> Profile
              </CDropdownItem>
              <CDropdownItem onClick={handleLogout}>
                <CIcon className="me-2" icon={cilAccountLogout} /> Logout
              </CDropdownItem>
            </CDropdownMenu>
          </CDropdown>
        </CHeaderNav>
      </CContainer>

      {/* <CContainer className="px-4" fluid>
        <AppBreadcrumb />
      </CContainer> */}
    </CHeader>
  )
}

export default AppHeader
