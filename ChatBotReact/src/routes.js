import React from 'react'
import AdministrationLayout from './layout/AdministrationLayout'
const Dashboard = React.lazy(() => import('./views/dashboard/Dashboard'))
const Channels = React.lazy(() => import('./views/property/widgets/MainWidget'))
// Base
const Chats = React.lazy(() => import('./views/base/chats/chats'))
// const Overview = React.lazy(() => import('./views/Overview/Overview'))

const routes = [
  // { path: '/', exact: true, name: 'Home' },
  { path: '/dashboard', name: 'Dashboard', element: Dashboard },
  { path: '/base/Chats', name: 'Chats', element: Chats },
  { path: '/base/Channels', name: 'Channels', element: Channels },
  { path: '/administration/:section', name: 'Administration', element: AdministrationLayout },
]

export default routes
