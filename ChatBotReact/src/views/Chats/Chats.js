// import React, { useState } from 'react'
// import { CCard, CCardBody } from '@coreui/react'
// import FirstColumn from './FirstColumn'
// import SecondColumn from './SecondColumn'
// import ThirdColumn from './ThirdColumn'
// import chatimage from '../../assets/images/avatars/chat.png'

// const Chats = () => {
//   const adminName = sessionStorage.getItem('name')
//   const adminEmail = sessionStorage.getItem('email')
//   const token = sessionStorage.getItem('token')
//   const adminId = sessionStorage.getItem('userid')
//   const [selectedUser, setSelectedUser] = useState('')
//   const handleUserSelect = (user) => {
//     console.log(user)
//     setSelectedUser(user)
//   }

//   console.log("selectedUserchat",selectedUser.sessionId)

//   return (
//     <CCard>
//       <CCardBody className="p-0" style={{ height: '600px' }}>
//         <div className="d-flex h-100">
//           <FirstColumn chatimage={chatimage} 
//           ChatID = {selectedUser.sessionId}
//           />

//           {/* Pass the user selection handler to SecondColumn */}
//           <SecondColumn 
//           currentAdminEmail={adminEmail}
//           onUserSelect={handleUserSelect} />

//           {/* Pass selected chat user info and admin info to ThirdColumn */}
//           <ThirdColumn
//             Name={adminName}
//             sessionId={selectedUser.sessionId}
//             UserName={selectedUser.name}
//             UserId={selectedUser.senderid}
//             jwtToken={token}
//             adminEmail={selectedUser.senderemail}
//             useremail = {selectedUser.receiveremail}
//             adminId={adminId}
//           />
//         </div>
//       </CCardBody>
//     </CCard>
//   )
// }

// export default Chats



// import React, { useState } from 'react'
// import { CCard, CCardBody } from '@coreui/react'
// import FirstColumn from './FirstColumn'
// import SecondColumn from './SecondColumn'
// import ThirdColumn from './ThirdColumn'
// // import GridTable from './GridTable' // New table component
// import GridTable from './GridTable'
// import chatimage from '../../assets/images/avatars/chat.png'

// const Chats = () => {
//   const adminName = sessionStorage.getItem('name')
//   const adminEmail = sessionStorage.getItem('email')
//   const token = sessionStorage.getItem('token')
//   const adminId = sessionStorage.getItem('userid')

//   const [selectedUser, setSelectedUser] = useState('')
//   const [showGridTable, setShowGridTable] = useState(false) // <-- toggle state

//   const handleUserSelect = (user) => {
//     setSelectedUser(user)
//   }

//   return (
//     <CCard>
//       <CCardBody className="p-0" style={{ height: '600px' }}>
//         <div className="d-flex h-100">
//           {/* ✅ Always show FirstColumn */}
//           <FirstColumn chatimage={chatimage} ChatID={selectedUser.sessionId} />

//           {/* ✅ Show either GridTable or Second + Third Column */}
//           {showGridTable ? (
//             <div className="d-flex w-100">
//               <GridTable onBack={() => setShowGridTable(false)} />
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
import { CCard, CCardBody } from '@coreui/react'
import FirstColumn from './FirstColumn'
import SecondColumn from './SecondColumn'
import ThirdColumn from './ThirdColumn'
import GridTable from './GridTable'
import chatimage from '../../assets/images/avatars/chat.png'

const Chats = () => {
  const adminName = sessionStorage.getItem('name')
  const adminEmail = sessionStorage.getItem('email')
  const token = sessionStorage.getItem('token')
  const adminId = sessionStorage.getItem('userid')

  const [selectedUser, setSelectedUser] = useState('')
  const [showGridTable, setShowGridTable] = useState(false) // Toggle for grid view

  const handleUserSelect = (user) => {
    setSelectedUser(user)
  }

  return (
    <CCard>
      <CCardBody className="p-0" style={{ height: '600px' }}>
        <div className="d-flex h-100">
          {/* Left: Always visible */}
          <FirstColumn chatimage={chatimage} ChatID={selectedUser.sessionId} />

          {/* Right: either show GridTable or normal chat UI */}
          {showGridTable ? (
            <div style={{ width: '100%'}}>
              <GridTable 
                onBack={() => setShowGridTable(false)} 
                currentAdminEmail={adminEmail}
              />
            </div>
          ) : (
            <>
              <SecondColumn
                currentAdminEmail={adminEmail}
                onUserSelect={handleUserSelect}
                onGridClick={() => setShowGridTable(true)}
              />
              <ThirdColumn
                Name={adminName}
                sessionId={selectedUser.sessionId}
                UserName={selectedUser.name}
                UserId={selectedUser.senderid}
                jwtToken={token}
                adminEmail={selectedUser.senderemail}
                useremail={selectedUser.receiveremail}
                adminId={adminId}
              />
            </>
          )}
        </div>
      </CCardBody>
    </CCard>
  )
}

export default Chats


