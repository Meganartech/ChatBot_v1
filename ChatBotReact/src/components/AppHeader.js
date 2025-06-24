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

const AppHeader = () => {
  const headerRef = useRef()
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const location = useLocation()
  const sidebarShow = useSelector((state) => state.sidebarShow)
  const { colorMode, setColorMode } = useColorModes('coreui-free-react-admin-template-theme')

  const [admin, setAdmin] = useState(null)

  useEffect(() => {
    const storedAdmin = localStorage.getItem("admin")
    if (storedAdmin) {
      const parsedAdmin = JSON.parse(storedAdmin)

      // Prefix image path with backend URL if necessary
      if (parsedAdmin.image_path && !parsedAdmin.image_path.startsWith('http')) {
        parsedAdmin.imageUrl = `http://localhost:8080/${parsedAdmin.image_path}`
        
      }

      setAdmin(parsedAdmin)
    }
  }, [])

  useEffect(() => {
    if (admin?.imageUrl) {
      localStorage.setItem("admin", JSON.stringify(admin))
    }
  }, [admin?.imageUrl])

  useEffect(() => {
    const handleScroll = () => {
      if (headerRef.current) {
        headerRef.current.classList.toggle('shadow-sm', document.documentElement.scrollTop > 0)
      }
    }
    document.addEventListener('scroll', handleScroll)
    return () => document.removeEventListener('scroll', handleScroll)
  }, [])

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
            <CDropdownToggle caret={false} className="d-flex align-items-center">
    {admin?.image_path ? (
  <img
    src={`http://localhost:8080/${admin.image_path}`}
    alt="profile"
    style={{
      width: '32px',
      height: '32px',
      borderRadius: '50%',
      objectFit: 'cover',
      marginRight: '8px',
    }}
  />
) : (
  <>
    <CIcon icon={cilUser} size="lg" className="me-2" />
    {console.log(admin?.image_path, "admin image path")}
  </>
)}

<h1>{admin?.image_path}</h1>


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
    </CHeader>
  )
}

export default AppHeader
