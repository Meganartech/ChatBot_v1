// import React, { useState } from 'react'
// import {
//   CCard,
//   CCardBody,
//   CCol,
//   CRow,
//   CButton,
// } from '@coreui/react'
// import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
// import {
//   faTableColumns,
//   faSliders,
//   faGear,
//   faUserGear,
//   faCommentAlt,
//   faImage,
// } from '@fortawesome/free-solid-svg-icons'

// // Pages
// import Overview from '../views/Overview/Overview'
// // import Channels from '../views/Channels/Channel'
// // import Settings from './Settings'
// // import UserManagement from './UserManagement'

// const AdministrationLayout = () => {
//   const [activeTab, setActiveTab] = useState('Overview')
//   const [isChannelsOpen, setIsChannelsOpen] = useState(false)
//   const [isUserManagementOpen, setIsUserManagementOpen] = useState(false)

//   const renderComponent = () => {
//     switch (activeTab) {
//       case 'Overview': return <Overview />
//       case 'Chat Widget': return <Channels />
//       case 'Widget Content': return <Channels />
//       case 'Settings': return <div>Settings Component</div>
//       case 'Property Members': return <div>Property Members</div>
//       case 'Departments': return <div>Departments</div>
//       default: return <div>Select a section</div>
//     }
//   }

//   return (
//     <CCard className="mb-4">
//       <CCardBody className="p-0">
//         <CRow className="g-0">
//           {/* Sidebar */}
//           <CCol md={3} className="border-end bg-white">
//             <div className="p-3">
//               <h6 className="fw-bold mb-3">Administration</h6>

//               <CButton
//                 color="light"
//                 className="w-100 text-start mb-2"
//                 onClick={() => setActiveTab('Overview')}
//               >
//                 <FontAwesomeIcon icon={faTableColumns} className="me-2" />
//                 Overview
//               </CButton>

//               {/* Channels */}
//               <CButton
//                 color="light"
//                 className="w-100 text-start mb-1"
//                 onClick={() => setIsChannelsOpen(!isChannelsOpen)}
//               >
//                 <FontAwesomeIcon icon={faSliders} className="me-2" />
//                 Channels
//                 <span className="float-end">{isChannelsOpen ? '˅' : '›'}</span>
//               </CButton>
//               {isChannelsOpen && (
//                 <div className="ps-4">
//                   <CButton color="link" className="text-dark text-start w-100" onClick={() => setActiveTab('Chat Widget')}>
//                     <FontAwesomeIcon icon={faCommentAlt} className="me-2" />
//                     Chat Widget
//                   </CButton>
//                   <CButton color="link" className="text-dark text-start w-100" onClick={() => setActiveTab('Widget Content')}>
//                     <FontAwesomeIcon icon={faImage} className="me-2" />
//                     Widget Content
//                   </CButton>
//                 </div>
//               )}

//               {/* Settings */}
//               <CButton
//                 color="light"
//                 className="w-100 text-start mb-2"
//                 onClick={() => setActiveTab('Settings')}
//               >
//                 <FontAwesomeIcon icon={faGear} className="me-2" />
//                 Settings
//               </CButton>

//               {/* User Management */}
//               <CButton
//                 color="light"
//                 className="w-100 text-start mb-1"
//                 onClick={() => setIsUserManagementOpen(!isUserManagementOpen)}
//               >
//                 <FontAwesomeIcon icon={faUserGear} className="me-2" />
//                 User Management
//                 <span className="float-end">{isUserManagementOpen ? '˅' : '›'}</span>
//               </CButton>
//               {isUserManagementOpen && (
//                 <div className="ps-4">
//                   <CButton color="link" className="text-dark text-start w-100" onClick={() => setActiveTab('Property Members')}>
//                     <FontAwesomeIcon icon={faCommentAlt} className="me-2" />
//                     Property Members
//                   </CButton>
//                   <CButton color="link" className="text-dark text-start w-100" onClick={() => setActiveTab('Departments')}>
//                     <FontAwesomeIcon icon={faImage} className="me-2" />
//                     Departments
//                   </CButton>
//                 </div>
//               )}
//             </div>
//           </CCol>

//           {/* Main content */}
//           <CCol md={9}>
//             {renderComponent()}
//           </CCol>
//         </CRow>
//       </CCardBody>
//     </CCard>
//   )
// }

// export default AdministrationLayout


import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  CCard,
  CCardBody,
  CCol,
  CRow,
  CButton,
} from '@coreui/react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faTableColumns,
  faSliders,
  faGear,
  faUserGear,
  faCommentAlt,
  faImage,
} from '@fortawesome/free-solid-svg-icons'

// Component Views
import Overview from '../views/Overview/Overview'
import WidgetBody from '../views/property/widgets/WidgetBody'
import ChatWidget from '../views/property/widgets/ChatWidget';
import Members from '../views/UserManagement/Members'

const AdministrationLayout = () => {
  const { section } = useParams()
  const navigate = useNavigate()

  const sectionToTab = {
    'overview': 'Overview',
    'chat-widget': 'Chat Widget',
    'widget-content': 'Widget Content',
    'settings': 'Settings',
    'property-members': 'Property Members',
    'departments': 'Departments',
  }

  const [activeTab, setActiveTab] = useState(sectionToTab[section] || 'Overview')
  const [isChannelsOpen, setIsChannelsOpen] = useState(false)
  const [isUserManagementOpen, setIsUserManagementOpen] = useState(false)

  useEffect(() => {
    setActiveTab(sectionToTab[section] || 'Overview')
  }, [section])

  const handleTabClick = (tabKey) => {
    const entry = Object.entries(sectionToTab).find(([k, v]) => v === tabKey)
    if (entry) {
      navigate(`/administration/${entry[0]}`)
    }
  }

  const renderComponent = () => {
    switch (activeTab) {
      case 'Overview': return <WidgetBody />
      case 'Chat Widget': return <ChatWidget />
      case 'Widget Content': return <div>Widget Content Component</div>
      case 'Settings': return <div>Settings Component</div>
      case 'Property Members': return <Members />
      case 'Departments': return <div>Dep</div>
      default: return <div>Select a section</div>
    }
  }

  const getButtonColor = (tab) => (activeTab === tab ? 'light' : '')

  return (
  <CCard className="mb-4" style={{ height: '550px' }}>
  <CCardBody className="p-0">
    <CRow className="g-0" style={{ height: '100%' }}>
    <CCol sm={3} md={2} className="border-end" style={{ height: '100%', overflowY: 'auto' }}>
  <div >
    <h6 className="fw-bold mb-3 small p-2">Administration</h6>

    <CButton color={getButtonColor('Overview')} className="w-100 text-start mb-2 py-2 px-2 small" onClick={() => handleTabClick('Overview')}>
      <FontAwesomeIcon icon={faTableColumns} className="me-2" /> Overview
    </CButton>

    <CButton className="w-100 text-start mb-1 py-2 px-2 small" onClick={() => setIsChannelsOpen(!isChannelsOpen)}>
      <FontAwesomeIcon icon={faSliders} className="me-2" /> Channels
      <span className="float-end">{isChannelsOpen ? '˅' : '›'}</span>
    </CButton>

    {isChannelsOpen && (
      <div className="ps-3">
        <CButton color={getButtonColor('Chat Widget')} className="text-dark text-start w-100 py-2 px-2 mb-1 small" onClick={() => handleTabClick('Chat Widget')}>
          <FontAwesomeIcon icon={faCommentAlt} className="me-2" /> Chat Widget
        </CButton>
        <CButton color={getButtonColor('Widget Content')} className="text-dark text-start w-100 py-2 px-2 mb-1 small" onClick={() => handleTabClick('Widget Content')}>
          <FontAwesomeIcon icon={faImage} className="me-2" /> Widget Content
        </CButton>
      </div>
    )}

    <CButton color={getButtonColor('Settings')} className="w-100 text-start mb-2 py-2 px-2 small" onClick={() => handleTabClick('Settings')}>
      <FontAwesomeIcon icon={faGear} className="me-2" /> Settings
    </CButton>

    <CButton className="w-100 text-start mb-1 py-2 px-2 small" onClick={() => setIsUserManagementOpen(!isUserManagementOpen)}>
      <FontAwesomeIcon icon={faUserGear} className="me-2" /> User Management
      <span className="float-end">{isUserManagementOpen ? '˅' : '›'}</span>
    </CButton>

    {isUserManagementOpen && (
      <div className="ps-3">
        <CButton color={getButtonColor('Property Members')} className="text-dark text-start w-100 py-2 px-2 mb-1 small" onClick={() => handleTabClick('Property Members')}>
          <FontAwesomeIcon icon={faCommentAlt} className="me-2" /> Property Members
        </CButton>
        <CButton color={getButtonColor('Departments')} className="text-dark text-start w-100 py-2 px-2 mb-1 small" onClick={() => handleTabClick('Departments')}>
          <FontAwesomeIcon icon={faImage} className="me-2" /> Departments
        </CButton>
      </div>
    )}
  </div>
</CCol>


      {/* Main Content */}
      <CCol md={10} style={{ height: '100%', overflowY: 'auto' ,backgroundColor:'#F3F4F7'}}>
        <div>
          {renderComponent()}
        </div>
      </CCol>
    </CRow>
  </CCardBody>
</CCard>

  )
}

export default AdministrationLayout
