// import React, { useEffect, useState } from 'react'
// import CIcon from '@coreui/icons-react'
// import { cilGrid, cilSearch } from '@coreui/icons'
// import { CCard, CCardBody } from '@coreui/react'
// import img from '../../assets/images/avatars/1.jpg'

// const SecondColumn = ({ onUserSelect }) => {
//   const [chatRooms, setChatRooms] = useState([])
//   const [selectedChatId, setSelectedChatId] = useState(null)
//   const [searchTerm, setSearchTerm] = useState('')

//   const fetchChatRooms = async () => {
//     try {
//       const res = await fetch('http://localhost:8080/sessions')
//       if (!res.ok) throw new Error('Failed to fetch chat rooms')
//       const data = await res.json()
//       setChatRooms(data)
//     } catch (err) {
//       console.error(err)
//     }
//   }

//   console.log("chatrooms",chatRooms);

//   useEffect(() => {
//     fetchChatRooms()
//   }, [])

//   useEffect(() => {
//     const intervalId = setInterval(fetchChatRooms, 3000)
//     return () => clearInterval(intervalId)
//   }, [])

//   const filterBySearch = (rooms) =>
//     rooms.filter((room) =>
//       (room.username || '').toLowerCase().includes(searchTerm.toLowerCase()),
//     )

//   const handleSelect = (room) => {
//     setSelectedChatId(room.sessionId)
//     if (onUserSelect) {
//       onUserSelect({
//         name: room.username,
//         sessionId: room.sessionId,
//         senderid: room.userid,
//         senderemail:room.adminemail,
//         receiveremail:room.useremail,
//       })
//     }
//   }

//   const renderMessageList = (rooms) =>
//     rooms.length === 0 ? (
//       <p className="text-center">No messages found</p>
//     ) : (
//       filterBySearch(rooms).map((room, index) => {
//         const isSelected = room.sessionId === selectedChatId
//         return (
//           <CCard
//             key={index}
//             onClick={() => handleSelect(room)}
//             className="mb-2 mx-2"
//             style={{
//               cursor: 'pointer',
//               backgroundColor: isSelected ? '#d0e7ff' : 'white',
//             }}
//           >
//             <CCardBody className="p-2">
//               <div className="d-flex">
//                 <img
//                   src={img}
//                   alt="avatar"
//                   className="rounded-circle me-3"
//                   style={{ width: '40px', height: '40px' }}
//                 />
//                 <div style={{ flexGrow: 1 }}>
//                   <div
//                     className="fw-semibold text-truncate"
//                     title={room.username}
//                   >
//                     {room.username?.length > 10
//                       ? room.username.slice(0, 10) + '...'
//                       : room.username}
//                   </div>
//                   <div
//                     className="text-muted small text-truncate"
//                     title={room.message}
//                   >
//                     {room.message
//                       ? room.message.length > 15
//                         ? room.message.slice(0, 15) + '...'
//                         : room.message
//                       : 'No message'}
//                   </div>
//                 </div>
//                 <div
//                   className="text-muted small"
//                   style={{
//                     whiteSpace: 'nowrap',
//                     fontSize: '0.75rem',
//                   }}
//                 >
//                   {room.timestamp
//                     ? new Date(room.timestamp).toLocaleTimeString([], {
//                         hour: '2-digit',
//                         minute: '2-digit',
//                       })
//                     : ''}
//                 </div>
//               </div>
//             </CCardBody>
//           </CCard>
//         )
//       })
//     )

//   return (
//     <div
//       className="border-end pe-2 ps-2 pt-3 d-flex flex-column"
//       style={{ width: '20%' }}
//     >
//       <div className="d-flex justify-content-between align-items-center mb-3 mt-2">
//         <strong style={{ fontSize: '20px' }}>Chats</strong>
//         <CIcon icon={cilGrid} />
//       </div>

//       <div className="input-group input-group-sm mb-3">
//         <span
//           className="input-group-text border-end-0"
//           style={{ borderRadius: '20px 0 0 20px' }}
//         >
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

//       <div className="text-center mb-2">
//         <div className="d-flex justify-content-center">
//           <span
//             style={{ cursor: 'pointer', fontWeight: '500', marginRight: '40px' }}
//           >
//             All
//           </span>
//           <span style={{ cursor: 'pointer', fontWeight: '500' }}>Mine</span>
//         </div>
//         <hr className="mt-2 mb-0" style={{ width: '100%' }} />
//       </div>

//       <div className="mt-3 px-2">
//         <h6 className="fw-bold">Incoming</h6>
//       </div>
//       {renderMessageList(chatRooms)}

//       <div className="mt-4 px-2">
//         <h6 className="fw-bold">All</h6>
//       </div>
//     </div>
//   )
// }

// export default SecondColumn


import React, { useEffect, useState } from 'react'
import CIcon from '@coreui/icons-react'
import { cilGrid, cilSearch } from '@coreui/icons'
import { CCard, CCardBody } from '@coreui/react'
import img from '../../assets/images/avatars/1.jpg'
import API_URL from '../../Config'

const SecondColumn = ({ onUserSelect, currentAdminEmail, onGridClick }) => {
  const [chatRooms, setChatRooms] = useState([])
  const [selectedChatId, setSelectedChatId] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [filterType, setFilterType] = useState('all') // "all" or "mine"

  const fetchChatRooms = async () => {
    try {
      const res = await fetch(`${API_URL}/sessions?receiverEmail=${currentAdminEmail}`)
      if (!res.ok) throw new Error('Failed to fetch chat rooms')
      const data = await res.json()
      setChatRooms(data)
    } catch (err) {
      console.error(err)
    }
  }

  useEffect(() => {
    fetchChatRooms()
    const intervalId = setInterval(fetchChatRooms, 3000)
    return () => clearInterval(intervalId)
  }, [])

  const handleSelect = (room) => {
    setSelectedChatId(room.sessionId)
    if (onUserSelect) {
      onUserSelect({
        name: room.username,
        sessionId: room.sessionId,
        senderid: room.userid,
        senderemail: room.adminemail,
        receiveremail: room.useremail,
        status: room.status,
      })
    }
  }

  const filterChats = (statusFilter) => {
    return chatRooms
      .filter((room) =>
        (room.username || '').toLowerCase().includes(searchTerm.toLowerCase())
      )
      .filter((room) => {
        if (filterType === 'mine') {
          return room.adminemail === currentAdminEmail
        }
        return true
      })
      .filter((room) => room.status === statusFilter)
  }

  const renderMessageList = (rooms) => {
    if (rooms.length === 0) return <p className="text-center">No messages found</p>

    return rooms.map((room, index) => {
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
                <div className="fw-semibold text-truncate" title={room.username}>
                  {room.username?.length > 8 ? room.username.slice(0, 8) + '...' : room.username}
                </div>
                <div className="text-muted small text-truncate" title={room.message}>
                  {room.message
                    ? room.message.length > 11
                      ? room.message.slice(0, 11) + '...'
                      : room.message
                    : 'No message'}
                </div>
              </div>
              <div className="text-muted small" style={{ whiteSpace: 'nowrap', fontSize: '0.75rem' }}>
                {room.timestamp
                  ? new Date(room.timestamp).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit',
                    })
                  : ''}
              </div>
            </div>
          </CCardBody>
        </CCard>
      )
    })
  }

  return (
    <div className="border-end pe-2 ps-2 pt-3 d-flex flex-column" style={{ width: '20%' }}>
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-3 mt-2">
        <strong style={{ fontSize: '20px' }}>Chats</strong>
        <CIcon icon={cilGrid} style={{ cursor: 'pointer' }} onClick={onGridClick} />
      </div>

      {/* Search bar */}
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

      {/* Filter Tabs */}
      <div className="text-center mb-2">
        <div className="d-flex justify-content-center">
          <span
            style={{
              cursor: 'pointer',
              fontWeight: '500',
              marginRight: '40px',
              color: filterType === 'all' ? '#007bff' : 'inherit',
            }}
            onClick={() => setFilterType('all')}
          >
            All
          </span>
          <span
            style={{
              cursor: 'pointer',
              fontWeight: '500',
              color: filterType === 'mine' ? '#007bff' : 'inherit',
            }}
            onClick={() => setFilterType('mine')}
          >
            Mine
          </span>
        </div>
        <hr className="mt-2 mb-0" />
      </div>

      {/* Incoming Section */}
      <div className="mt-3 px-2">
        <h6 className="fw-bold">Incoming</h6>
      </div>
      <div style={{ maxHeight: '180px', overflowY: 'auto' }}>
        {renderMessageList(filterChats(false))}
      </div>

      {/* All Section */}
      <div className="mt-4 px-2">
        <h6 className="fw-bold">All</h6>
      </div>
      <div style={{ maxHeight: '180px', overflowY: 'auto' }}>
        {renderMessageList(filterChats(true))}
      </div>
    </div>
  )
}

export default SecondColumn
