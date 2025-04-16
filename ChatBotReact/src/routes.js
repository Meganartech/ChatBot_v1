import React from 'react'
import HomeScreen from './views/home/home'
const Dashboard = React.lazy(() => import('./views/dashboard/Dashboard'))
const Colors = React.lazy(() => import('./views/theme/colors/Colors'))
const Typography = React.lazy(() => import('./views/theme/typography/Typography'))
const Channels = React.lazy(() => import('./views/property/widgets/MainWidget'))
// Base
const Chats = React.lazy(() => import('./views/base/chats/chats'))

const routes = [
  { path: '/', exact: true, name: 'Home' },
  { path: '/dashboard', name: 'Dashboard', element: Dashboard },
  { path: '/base/Compose', name: 'Theme', element: Colors },
  { path: '/base/Chats', name: 'Chats', element: Chats },
  { path: '/base/Ticket', name: 'Ticket', element: Typography },
  { path: '/base/Channels', name: 'Channels', element: Channels },
  { path: '/base/addContact', name: 'Add Contact', element: Typography },
  { path: '/base/People', name: 'People', element: Typography },
  { path: '/base/Organization', name: 'Organization', element: Typography },
  { path: '/base/Articles', name: 'Articles', element: Typography },
  { path: '/base/Categories', name: 'Categories', element: Typography },
  { path: '/base/chatVolume', name: 'Chat Volume', element: Typography },
  { path: '/base/missedChats', name: 'Missed Chats', element: Typography },
  { path: '/base/offlineMessage', name: 'Offline Message', element: Typography },
  { path: '/base/averageChatDuration', name: 'Average Chat Duration', element: Typography },
  { path: '/base/userSatisfaction', name: 'User Satifaction', element: Typography },
  { path: '/base/firstResponseTime', name: 'First Response Time', element: Typography },
  { path: '/base/newTicketVolume', name: 'New Ticket Volume', element: Typography },
  { path: '/base/solvedTickets', name: 'Solved Tickets', element: Typography },
  { path: '/base/ticketSource', name: 'Ticket Source', element: Typography },
  { path: '/base/resolutionTime', name: 'Resolution Time', element: Typography },  
  { path: '/base/ticketReopened', name: 'Ticket Reopend', element: Typography },
  { path: '/base/firstResponseTime', name: 'First Response Time', element: Typography },
  { path: '/base/chatWidget', name: 'Chat Widget', element: Typography },
  { path: '/base/Page', name: 'Page', element: Typography },
  { path: '/base/knowledgeBase', name: 'Knowledge Base', element: Typography },
  { path: '/base/Shortcuts', name: 'Shortcuts', element: Typography },
  { path: '/base/Trigger', name: 'Trigger', element: Typography },
  { path: '/base/Tabs', name: 'Tabs', element: Typography },
  { path: '/base/propertyMember', name: 'Property Member', element: Typography },
  { path: '/base/Departments', name: 'Departments', element: Typography },
  { path: '/base/agentAlerts', name: 'Agent Alerts', element: Typography },
  { path: '/base/banList', name: 'Ban List', element: Typography },
]

export default routes
