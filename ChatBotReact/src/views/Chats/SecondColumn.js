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


import React, { useEffect, useRef, useState } from 'react'
import CIcon from '@coreui/icons-react'
import { cilGrid, cilSearch } from '@coreui/icons'
import { CCard, CCardBody, CButton } from '@coreui/react'
import img from '../../assets/images/avatars/1.jpg'
import API_URL from '../../Config'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const SecondColumn = ({ onUserSelect, currentAdminEmail, onGridClick, unreadCounts = {}, onUnreadSync, onIncomingUserMessage }) => {
  const [chatRooms, setChatRooms] = useState([])
  const [selectedChatId, setSelectedChatId] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [filterType, setFilterType] = useState('all') // "all" or "mine"

  const stompClientRef = useRef(null)
  const subscriptionsRef = useRef({}) // sessionId -> subscription
  const sessionAnnounceSubRef = useRef(null)
  const adminEmailRef = useRef(currentAdminEmail)

  useEffect(() => {
    adminEmailRef.current = currentAdminEmail
  }, [currentAdminEmail])

  const fetchChatRooms = async () => {
    try {
      const res = await fetch(`${API_URL}/sessions?receiverEmail=${currentAdminEmail}`)
      if (!res.ok) throw new Error('Failed to fetch chat rooms')
      const data = await res.json()
      setChatRooms(data)
      // sync unread counts from server if provided
      if (Array.isArray(data) && typeof onUnreadSync === 'function') {
        const map = {}
        data.forEach((d) => {
          if (d.sessionId && typeof d.unreadCount === 'number') {
            map[d.sessionId] = d.unreadCount
          }
        })
        onUnreadSync(map)
      }
    } catch (err) {
      console.error(err)
    }
  }

  useEffect(() => {
    fetchChatRooms()
  }, [currentAdminEmail])

  // Fallbacks: refetch when tab becomes visible and every 30s just in case
  useEffect(() => {
    const onVisibility = () => {
      if (document.visibilityState === 'visible') {
        fetchChatRooms()
      }
    }
    document.addEventListener('visibilitychange', onVisibility)
    const id = setInterval(fetchChatRooms, 30000)
    return () => {
      document.removeEventListener('visibilitychange', onVisibility)
      clearInterval(id)
    }
  }, [])

  // Setup a single websocket client to listen for all session updates for this admin
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/chat`),
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: (s) => console.log(s),
      onConnect: () => {
        // Ensure initial list is filled as soon as socket connects
        fetchChatRooms()
        subscribeSessionAnnouncements(currentAdminEmail)
        // Fallback: subscribe to general stream in case adminEmail is missing or mapping differs
        try {
          client.subscribe(`/topic/sessions`, (msg) => {
            try {
              const display = JSON.parse(msg.body)
              const target = (display?.adminemail || '').toLowerCase()
              const mine = (adminEmailRef.current || '').toLowerCase()
              if (target && mine && target !== mine) return
              setChatRooms((prev) => {
                if (prev.find((r) => r.sessionId === display.sessionId)) return prev
                return [display, ...prev]
              })
            } catch (_) {}
          })
        } catch (_) {}
        ensureSubscriptions(chatRooms)
      },
    })
    client.activate()
    stompClientRef.current = client
    return () => {
      // cleanup subs
      Object.values(subscriptionsRef.current).forEach((sub) => {
        try { sub.unsubscribe() } catch (_) {}
      })
      subscriptionsRef.current = {}
      if (sessionAnnounceSubRef.current) {
        try { sessionAnnounceSubRef.current.unsubscribe() } catch (_) {}
        sessionAnnounceSubRef.current = null
      }
      try { client.deactivate() } catch (_) {}
      stompClientRef.current = null
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // When chatRooms change or client connects, subscribe/unsubscribe per session
  useEffect(() => {
    ensureSubscriptions(chatRooms)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [chatRooms])

  // Resubscribe to session announcements whenever admin email becomes available/changes
  useEffect(() => {
    if (stompClientRef.current && stompClientRef.current.connected) {
      subscribeSessionAnnouncements(currentAdminEmail)
    }
  }, [currentAdminEmail])

  const subscribeSessionAnnouncements = (adminEmail) => {
    const client = stompClientRef.current
    if (!client || !client.connected || !adminEmail) return
    if (sessionAnnounceSubRef.current) {
      try { sessionAnnounceSubRef.current.unsubscribe() } catch (_) {}
      sessionAnnounceSubRef.current = null
    }
    try {
      sessionAnnounceSubRef.current = client.subscribe(`/topic/sessions/${adminEmail}`, (msg) => {
        try {
          const display = JSON.parse(msg.body)
          setChatRooms((prev) => {
            if (prev.find((r) => r.sessionId === display.sessionId)) return prev
            return [display, ...prev]
          })
        } catch (_) {}
      })
    } catch (_) {}
  }

  const ensureSubscriptions = (rooms) => {
    const client = stompClientRef.current
    if (!client || !client.connected) return
    const wanted = new Set((rooms || []).map((r) => r.sessionId).filter(Boolean))
    // Unsubscribe old
    Object.keys(subscriptionsRef.current).forEach((sid) => {
      if (!wanted.has(sid)) {
        try { subscriptionsRef.current[sid].unsubscribe() } catch (_) {}
        delete subscriptionsRef.current[sid]
      }
    })
    // Subscribe new
    wanted.forEach((sid) => {
      if (subscriptionsRef.current[sid]) return
      const sub = client.subscribe(`/topic/messages/${sid}`, (msg) => {
        try {
          const payload = JSON.parse(msg.body)
          // Update preview for the room (message and timestamp)
          setChatRooms((prev) => prev.map((r) => (
            r.sessionId === sid
              ? { ...r, message: payload.content || r.message, timestamp: new Date().toISOString() }
              : r
          )))
          // Notify parent to bump unread where appropriate
          if (payload?.role === 'USER' && typeof onIncomingUserMessage === 'function') {
            onIncomingUserMessage(sid)
          }
        } catch (e) {
          // ignore parse errors
        }
      })
      subscriptionsRef.current[sid] = sub
    })
  }

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

  // const filterPending = () => {
  //   return chatRooms
  //     .filter((room) =>
  //       (room.username || '').toLowerCase().includes(searchTerm.toLowerCase())
  //     )
  //     .filter((room) => {
  //       if (filterType === 'mine') {
  //         return room.adminemail === currentAdminEmail
  //       }
  //       return true
  //     })
  //     .filter((room) => (unreadCounts[room.sessionId] || 0) > 0)
  // }

  const renderMessageList = (rooms) => {
    if (rooms.length === 0) return <p className="text-center">No messages found</p>

    return rooms.map((room, index) => {
      const isSelected = room.sessionId === selectedChatId
      const unreadCount = unreadCounts[room.sessionId] || 0
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
  <div className="d-flex align-items-center">
    {/* Avatar Circle with First Letter */}
    <div
      className="rounded-circle d-flex align-items-center justify-content-center me-3"
      style={{
        width: '40px',
        height: '40px',
        backgroundColor: '#6c757d', // muted gray (you can randomize for different users)
        color: 'white',
        fontWeight: 'bold',
        fontSize: '1rem',
        textTransform: 'uppercase',
      }}
    >
      {room.username ? room.username.charAt(0) : '?'}
    </div>

    {/* Username & Message */}
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

    {/* Timestamp and unread badge */}
    <div className="d-flex flex-column align-items-end" style={{ minWidth: '48px' }}>
      <div
        className="text-muted small"
        style={{ whiteSpace: 'nowrap', fontSize: '0.75rem' }}
      >
        {room.timestamp
          ? new Date(room.timestamp).toLocaleTimeString([], {
              hour: '2-digit',
              minute: '2-digit',
            })
          : ''}
      </div>
      {unreadCount > 0 && (
        <span
          className="badge bg-primary mt-1"
          style={{ alignSelf: 'end' }}
        >
          {unreadCount}
        </span>
      )}
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
        <div className="d-flex align-items-center gap-2">
          {/* <CButton size="sm" color="light" onClick={fetchChatRooms}>Refresh</CButton> */}
          <CIcon icon={cilGrid} style={{ cursor: 'pointer' }} onClick={onGridClick} />
        </div>
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

      {/* Pending Section
      <div className="mt-3 px-2">
        <h6 className="fw-bold">Pending</h6>
      </div>
      <div style={{ maxHeight: '160px', overflowY: 'auto' }}>
        {renderMessageList(filterPending())}
      </div> */}

      {/* Incoming Section */}
      <div className="mt-3 px-2">
        <h6 className="fw-bold">Incoming</h6>
      </div>
      <div style={{ maxHeight: '160px', overflowY: 'auto' }}>
        {renderMessageList(filterChats(false))}
      </div>

      {/* All Section */}
      <div className="mt-4 px-2">
        <h6 className="fw-bold">All</h6>
      </div>
      <div style={{ maxHeight: '160px', overflowY: 'auto' }}>
        {renderMessageList(filterChats(true))}
      </div>
    </div>
  )
}

export default SecondColumn
