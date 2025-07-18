// import React, { useState } from 'react'
// import { CCard, CCardBody } from '@coreui/react'
// import FirstColumn from './FirstColumn'
// import SecondColumn from './SecondColumn'
// import ThirdColumn from './ThirdColumn'
// import GridTable from './GridTable'
// import chatimage from '../../assets/images/avatars/chat.png'

// const Chats = () => {
//   const adminName = sessionStorage.getItem('name')
//   const adminEmail = sessionStorage.getItem('email')
//   const token = sessionStorage.getItem('token')
//   const adminId = sessionStorage.getItem('userid')

//   const [selectedUser, setSelectedUser] = useState('')
//   const [showGridTable, setShowGridTable] = useState(false) // Toggle for grid view
  

//   const handleUserSelect = (user) => {
//     setSelectedUser(user)
//   }

//   return (
//     <CCard>
//       <CCardBody className="p-0" style={{ height: '600px' }}>
//         <div className="d-flex h-100">
//           {/* Left: Always visible */}
//           <FirstColumn chatimage={chatimage} ChatID={selectedUser.sessionId} />

//           {/* Right: either show GridTable or normal chat UI */}
//           {showGridTable ? (
//             <div style={{ width: '100%'}}>
//               <GridTable 
//                 onBack={() => setShowGridTable(false)} 
//                 currentAdminEmail={adminEmail}
//               />
//             </div>
//           ) : (
//             <>
//               <SecondColumn
//                 currentAdminEmail={adminEmail}
//                 onUserSelect={handleUserSelect}
//                 onGridClick={() => setShowGridTable(true)}
//               />
//               <ThirdColumn
//                 Name={adminName}
//                 sessionId={selectedUser.sessionId}
//                 UserName={selectedUser.name}
//                 UserId={selectedUser.senderid}
//                 jwtToken={token}
//                 adminEmail={selectedUser.senderemail}
//                 useremail={selectedUser.receiveremail}
//                 adminId={adminId}
//               />
//             </>
//           )}
//         </div>
//       </CCardBody>
//     </CCard>
//   )
// }

// export default Chats


import React, { useState } from 'react'
import { CCard, CCardBody, CButton } from '@coreui/react'
import FirstColumn from './FirstColumn'
import SecondColumn from './SecondColumn'
import ThirdColumn from './ThirdColumn'
import GridTable from './GridTable'
import chatimage from '../../assets/images/avatars/chat.png'
import img from '../../assets/images/avatars/1.jpg'
import API_URL from '../../Config';

const Chats = () => {
  const adminName = sessionStorage.getItem('name')
  const adminEmail = sessionStorage.getItem('email')
  const token = sessionStorage.getItem('token')
  const adminId = sessionStorage.getItem('userid')

  const [selectedUser, setSelectedUser] = useState(null)
  const [showGridTable, setShowGridTable] = useState(false)
  const [showJoinPrompt, setShowJoinPrompt] = useState(false)
  const [chatJoined, setChatJoined] = useState(false)

  const handleUserSelect = (user) => {
    setSelectedUser(user)
    setShowJoinPrompt(true)
    setChatJoined(false)
  }

  const handleJoinChat = () => {
    setChatJoined(true)
    setShowJoinPrompt(false)
  }

  const handleRejectChat = () => {
    setSelectedUser(null)
    setShowJoinPrompt(false)
    setChatJoined(false)
  }

  return (
    <CCard>
      <CCardBody className="p-0" style={{ height: '600px' }}>
        <div className="d-flex h-100 position-relative">
          {/* Left Sidebar */}
          <FirstColumn chatimage={chatimage} ChatID={selectedUser?.sessionId} />

          {/* Conditional Grid Table or Chat UI */}
          {showGridTable ? (
            <div style={{ width: '100%' }}>
              <GridTable
                onBack={() => setShowGridTable(false)}
                currentAdminEmail={adminEmail}
              />
            </div>
          ) : (
            <>
              {/* Second Column (chat list) */}
              <SecondColumn
                currentAdminEmail={adminEmail}
                onUserSelect={handleUserSelect}
                onGridClick={() => setShowGridTable(true)}
              />

              {/* Third Column (chat window) */}
              <ThirdColumn
                Name={adminName}
                sessionId={selectedUser?.sessionId}
                UserName={selectedUser?.name}
                UserId={selectedUser?.senderid}
                jwtToken={token}
                adminEmail={selectedUser?.senderemail}
                useremail={selectedUser?.receiveremail}
                status={selectedUser?.status}
                adminId={adminId}
                chatJoined={chatJoined}
              />
            </>
          )}

          {showJoinPrompt && selectedUser && (
  <CCard
    className="position-absolute"
    style={{
      bottom: '20px',
      right: '20px',
      zIndex: 1000,
      minWidth: '300px',
      borderRadius: '12px',
      boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
    }}
  >
    <CCardBody>
      <div className="d-flex justify-content-between align-items-start mb-3">
        <div className="d-flex">
          <img
            src={img}
            alt="User"
            className="rounded-circle me-3"
            style={{ width: '50px', height: '50px', objectFit: 'cover' }}
          />
          <div>
            <div className="fw-semibold">{selectedUser.name}</div>
            <div className="text-muted small">{selectedUser.receiveremail}</div>
          </div>
        </div>
        <button
          onClick={handleRejectChat}
          className="btn-close"
          style={{ fontSize: '0.9rem' }}
        />
      </div>

      <div className="d-flex justify-content-between gap-2">
  <CButton className='w-100'  color="light" onClick={handleRejectChat}>
    Reject
  </CButton>
  <CButton className='w-100' color="primary" onClick={handleJoinChat}>
    Join
  </CButton>
</div>

    </CCardBody>
  </CCard>
)}

        </div>
      </CCardBody>
    </CCard>
  )
}

export default Chats
