// import React from 'react'
// import { CCard, CCardBody } from '@coreui/react'
// import FirstColumn from './FirstColumn'
// import SecondColumn from './SecondColumn'
// import ThirdColumn from './ThirdColumn'
// import chatimage from '../../assets/images/avatars/chat.png';

// const Chats = () => {

//   const name = sessionStorage.getItem('name');
//   const email = sessionStorage.getItem('email');
//   const token = sessionStorage.getItem('token');
//   return (
//     <CCard>
//       <CCardBody className="p-0" style={{ height: '600px' }}>
//         <div className="d-flex h-100">
//           <FirstColumn chatimage={chatimage} />
//           <SecondColumn />
//           <ThirdColumn 
//           Name ={name}
//           Email = {email}
//           jwtToken={token}
//           />
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
import chatimage from '../../assets/images/avatars/chat.png'

const Chats = () => {
  // Logged-in admin info
  const adminName = sessionStorage.getItem('name')
  const adminEmail = sessionStorage.getItem('email')
  const token = sessionStorage.getItem('token')
  const adminId = sessionStorage.getItem('userid')

  // State for currently selected chat user (default empty)
  const [selectedUser, setSelectedUser] = useState('')

  // Handler to update selected chat user
  const handleUserSelect = (user) => {
    console.log(user)
    setSelectedUser(user)
  }

  console.log("selectedUserchat",selectedUser.sessionId)

  return (
    <CCard>
      <CCardBody className="p-0" style={{ height: '600px' }}>
        <div className="d-flex h-100">
          <FirstColumn chatimage={chatimage} 
          ChatID = {selectedUser.sessionId}
          />

          {/* Pass the user selection handler to SecondColumn */}
          <SecondColumn onUserSelect={handleUserSelect} />

          {/* Pass selected chat user info and admin info to ThirdColumn */}
          <ThirdColumn
            Name={adminName}
            sessionId={selectedUser.sessionId}
            UserName={selectedUser.name}
            UserId={selectedUser.senderid}
            jwtToken={token}
            adminEmail={adminEmail}
            adminId={adminId}
          />
        </div>
      </CCardBody>
    </CCard>
  )
}

export default Chats
