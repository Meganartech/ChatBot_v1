// import React, { useEffect, useState } from 'react'
// import CIcon from '@coreui/icons-react'
// import { cilGrid, cilSearch } from '@coreui/icons'
// import { CCard, CCardBody } from '@coreui/react'
// import img from '../../assets/images/avatars/1.jpg' // placeholder avatar

// const SecondColumn = ({ onUserSelect }) => {
//   const [incomingMessages, setIncomingMessages] = useState([])
//   const [allMessages, setAllMessages] = useState([])
//   const [selectedEmail, setSelectedEmail] = useState(null)
//   const [searchTerm, setSearchTerm] = useState('')

//   useEffect(() => {
//     // Fetch incoming (today's) messages
//     const fetchIncoming = async () => {
//       try {
//         const res = await fetch('http://localhost:8080/latest')
//         if (!res.ok) throw new Error('Failed to fetch incoming messages')
//         const data = await res.json()
//         setIncomingMessages(data)
//       } catch (err) {
//         console.error(err)
//       }
//     }

//     // Fetch all messages before today
//     const fetchAll = async () => {
//       try {
//         const res = await fetch('http://localhost:8080/all')
//         if (!res.ok) throw new Error('Failed to fetch all messages')
//         const data = await res.json()
//         setAllMessages(data)
//       } catch (err) {
//         console.error(err)
//       }
//     }

//     fetchIncoming()
//     fetchAll()
//   }, [])

//   // Filter function for both lists
//   const filterBySearch = (messages) =>
//     messages.filter((senderGroup) =>
//       senderGroup.sendername.toLowerCase().includes(searchTerm.toLowerCase()),
//     )

//   const handleSelect = (senderGroup) => {
//     setSelectedEmail(senderGroup.senderemail)
//     if (onUserSelect) {
//       onUserSelect({ email: senderGroup.senderemail, name: senderGroup.sendername })
//     }
//   }

//   const renderMessageList = (messages) =>
//     messages.length === 0 ? (
//       <p className="text-center">No messages found</p>
//     ) : (
//       filterBySearch(messages).map((senderGroup, index) => {
//         const isSelected = senderGroup.senderemail === selectedEmail
//         const latestMsg = senderGroup.messages[senderGroup.messages.length - 1]

//         return (
//           <CCard
//             key={index}
//             onClick={() => handleSelect(senderGroup)}
//             className="mb-2 mx-2"
//             style={{
//               position: 'relative',
//               cursor: 'pointer',
//               backgroundColor: isSelected ? '#d0e7ff' : 'white',
//             }}
//           >
//             <CCardBody className="p-2">
//               <div className="d-flex" style={{ position: 'relative', width: '100%' }}>
//                 <div className="d-flex" style={{ minWidth: 0 }}>
//                   <img
//                     src={img}
//                     alt="avatar"
//                     className="rounded-circle me-3"
//                     style={{ width: '40px', height: '40px' }}
//                   />
//                   <div style={{ minWidth: 0 }}>
//                     <div
//                       className="fw-semibold text-truncate"
//                       style={{ maxWidth: '100px' }}
//                       title={senderGroup.sendername}
//                     >
//                       {senderGroup.sendername}
//                     </div>
//                     <div
//                       className="text-muted small text-truncate"
//                       style={{ maxWidth: '120px' }}
//                       title={`${latestMsg.content} [${latestMsg.role}]`}
//                     >
//                       {latestMsg.content}
//                     </div>
//                   </div>
//                 </div>

//                 <div
//                   className="text-muted small"
//                   style={{
//                     position: 'absolute',
//                     top: '5px',
//                     right: '-2px',
//                     whiteSpace: 'nowrap',
//                     fontSize: '0.75rem',
//                   }}
//                 >
//                   {new Date(latestMsg.timestamp).toLocaleTimeString([], {
//                     hour: '2-digit',
//                     minute: '2-digit',
//                   })}
//                 </div>
//               </div>
//             </CCardBody>
//           </CCard>
//         )
//       })
//     )

//   return (
//     <div className="border-end pe-2 ps-2 pt-3 d-flex flex-column" style={{ width: '20%' }}>
//       {/* Header */}
//       <div className="d-flex justify-content-between align-items-center mb-3 mt-2">
//         <strong style={{ fontSize: '20px' }}>Chats</strong>
//         <CIcon icon={cilGrid} />
//       </div>

//       {/* Search */}
//       <div className="input-group input-group-sm mb-3">
//         <span className="input-group-text border-end-0" style={{ borderRadius: '20px 0 0 20px' }}>
//           <CIcon icon={cilSearch} />
//         </span>
//         <input
//           type="text"
//           className="form-control border-start-0"
//           placeholder="Search..."
//           style={{ borderRadius: '0 20px 20px 0' }}
//           value={searchTerm}
//           onChange={(e) => setSearchTerm(e.target.value)}
//         />
//       </div>

//       {/* Tabs */}
//       <div className="text-center mb-2">
//         <div className="d-flex justify-content-center">
//           <span style={{ cursor: 'pointer', fontWeight: '500', marginRight: '40px' }}>All</span>
//           <span style={{ cursor: 'pointer', fontWeight: '500' }}>Mine</span>
//         </div>
//         <hr className="mt-2 mb-0" style={{ width: '100%' }} />
//       </div>

//       {/* Incoming Section */}
//       <div className="mt-3 px-2">
//         <h6 className="fw-bold">Incoming</h6>
//       </div>
//       {renderMessageList(incomingMessages)}

//       {/* All Messages Section */}
//       <div className="mt-4 px-2">
//         <h6 className="fw-bold">All</h6>
//       </div>
//       {renderMessageList(allMessages)}
//     </div>
//   )
// }

// export default SecondColumn


import React, { useEffect, useState } from 'react'
import CIcon from '@coreui/icons-react'
import { cilGrid, cilSearch } from '@coreui/icons'
import { CCard, CCardBody } from '@coreui/react'
import img from '../../assets/images/avatars/1.jpg'

const SecondColumn = ({ onUserSelect }) => {
  const [chatRooms, setChatRooms] = useState([])
  const [selectedChatId, setSelectedChatId] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')

  const fetchChatRooms = async () => {
    try {
      const res = await fetch('http://localhost:8080/chatbot/all');
      if (!res.ok) throw new Error('Failed to fetch chat rooms');
      const data = await res.json();
      setChatRooms(data);
    } catch (err) {
      console.error(err);
    }
  };

  // Initial fetch when component mounts
  useEffect(() => {
    fetchChatRooms();
  }, []);

  // Polling every 3 seconds
  useEffect(() => {
    const intervalId = setInterval(fetchChatRooms, 3000);

    return () => {
      clearInterval(intervalId); // cleanup on unmount
    };
  }, []);

  console.log(chatRooms)

  const filterBySearch = (rooms) =>
    rooms.filter((room) =>
      room.senderName.toLowerCase().includes(searchTerm.toLowerCase()),
    )

  const handleSelect = (room) => {
    setSelectedChatId(room.sessionId)
    if (onUserSelect) {
      onUserSelect({
        name: room.senderName,
        sessionId: room.sessionId,
        senderid:room.senderid,
      })
    }
  }

  const renderMessageList = (rooms) =>
    rooms.length === 0 ? (
      <p className="text-center">No messages found</p>
    ) : (
      filterBySearch(rooms).map((room, index) => {
        const isSelected = room.sessionId === selectedChatId
        return (
          <CCard
            key={index}
            onClick={() => handleSelect(room)}
            className="mb-2 mx-2"
            style={{
              cursor: 'pointer',
              backgroundColor: isSelected ? '#d0e7ff' : 'white',
            }}
          >
            <CCardBody className="p-2">
              <div className="d-flex">
                <img
                  src={img}
                  alt="avatar"
                  className="rounded-circle me-3"
                  style={{ width: '40px', height: '40px' }}
                />
                <div style={{ flexGrow: 1 }}>
                  <div className="fw-semibold text-truncate" title={room.senderName}>
                    {room.senderName.length > 10 ? room.senderName.slice(0, 10) + '...' : room.senderName}
                  </div>
                  <div className="text-muted small text-truncate" title={room.message}>
                    {room.message.length > 15 ? room.message.slice(0, 10) + '...' : room.message}
                  </div>

                </div>
                <div
                  className="text-muted small"
                  style={{
                    whiteSpace: 'nowrap',
                    fontSize: '0.75rem',
                  }}
                >
                  {new Date(room.timestamp).toLocaleTimeString([], {
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </div>
              </div>
            </CCardBody>
          </CCard>
        )
      })
    )

  return (
    <div className="border-end pe-2 ps-2 pt-3 d-flex flex-column" style={{ width: '20%' }}>
      <div className="d-flex justify-content-between align-items-center mb-3 mt-2">
        <strong style={{ fontSize: '20px' }}>Chats</strong>
        <CIcon icon={cilGrid} />
      </div>

      <div className="input-group input-group-sm mb-3">
        <span className="input-group-text border-end-0" style={{ borderRadius: '20px 0 0 20px' }}>
          <CIcon icon={cilSearch} />
        </span>
        <input
          type="text"
          className="form-control border-start-0"
          placeholder="Search..."
          style={{ borderRadius: '0 20px 20px 0' }}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="text-center mb-2">
        <div className="d-flex justify-content-center">
          <span style={{ cursor: 'pointer', fontWeight: '500', marginRight: '40px' }}>All</span>
          <span style={{ cursor: 'pointer', fontWeight: '500' }}>Mine</span>
        </div>
        <hr className="mt-2 mb-0" style={{ width: '100%' }} />
      </div>

      <div className="mt-3 px-2">
        <h6 className="fw-bold">Incoming</h6>
      </div>
      {renderMessageList(chatRooms)}

      <div className="mt-4 px-2">
        <h6 className="fw-bold">All</h6>
      </div>
    </div>
  )
}

export default SecondColumn
