// import React, { useState, useEffect, useRef } from 'react'
// import { CCardHeader, CButton } from '@coreui/react'
// import CIcon from '@coreui/icons-react'
// import { cilSearch, cilOptions } from '@coreui/icons'
// import img from '../../assets/images/avatars/1.jpg'
// import chatimage from '../../assets/images/avatars/empty-chats 1.png'
// import SockJS from 'sockjs-client'
// import { Client } from '@stomp/stompjs'

// const ThirdColumn = ({ UserEmail, Name, jwtToken, adminEmail }) => {
//   const [messages, setMessages] = useState([])
//   const [inputMsg, setInputMsg] = useState('')
//   const stompClientRef = useRef(null)
//   const messagesEndRef = useRef(null)

//   const date = new Date().toISOString().split('T')[0]; // "YYYY-MM-DD"

//   useEffect(() => {
//     if (!UserEmail || !jwtToken) return

//     // 1. Fetch existing messages
//     const fetchMessages = async () => {
//       try {
//         const res = await fetch(`http://localhost:8080/?email=${UserEmail}&date=${date}`, {
//           headers: {
//             Authorization: `Bearer ${jwtToken}`,
//           },
//         })
//         if (!res.ok) throw new Error('Failed to fetch messages')
//         const data = await res.json()
//         setMessages(data)
//       } catch (err) {
//         console.error('Error fetching messages:', err)
//       }
//     }

//     fetchMessages()

//     // 2. Setup WebSocket
//     if (stompClientRef.current) {
//       stompClientRef.current.deactivate()
//       stompClientRef.current = null
//     }

//     const socket = new SockJS('http://localhost:8080/ws-chat')
//     const stompClient = new Client({
//       webSocketFactory: () => socket,
//       reconnectDelay: 5000,
//       onConnect: () => {
//         console.log('✅ Connected to WebSocket')

//         const topic = `/topic/admin/messages/${adminEmail.replace('@', '_')}`
//         stompClient.subscribe(topic, (message) => {
//           const msg = JSON.parse(message.body)
//           if (msg.senderemail === adminEmail || msg.receiveremail === UserEmail) {
//             setMessages((prev) => [...prev, msg])
//           }
//         })
//       },
//       onStompError: (frame) => {
//         console.error('WebSocket error:', frame.headers['message'])
//       },
//     })

//     stompClient.activate()
//     stompClientRef.current = stompClient

//     return () => {
//       if (stompClientRef.current) {
//         stompClientRef.current.deactivate()
//         stompClientRef.current = null
//       }
//     }
//   }, [UserEmail, adminEmail, jwtToken])

//   useEffect(() => {
//     if (messagesEndRef.current) {
//       messagesEndRef.current.scrollIntoView({ behavior: 'smooth' })
//     }
//   }, [messages])

//   const sendMessage = () => {
//     if (!inputMsg.trim() || !UserEmail) return

//     const msgObj = {
//       content: inputMsg,
//       sendername: Name,
//       senderemail: adminEmail,
//       receiveremail: UserEmail,
//       role: 'ADMIN',
//       timestamp: new Date().toISOString(),
//     }

//     if (stompClientRef.current && stompClientRef.current.connected) {
//       stompClientRef.current.publish({
//         // destination: `/app/chat/${UserEmail}/${jwtToken}`,
//         destination: `/app/chat/${adminEmail}/${jwtToken}`,
//         body: JSON.stringify(msgObj),
//       })

//       // Optimistic update
//       setMessages((prev) => [...prev, msgObj])
//       setInputMsg('')
//     } else {
//       console.warn('WebSocket not connected.')
//     }
//   }

//   return (
//     <div
//       style={{
//         width: '74%',
//         display: 'flex',
//         flexDirection: 'column',
//         height: '100%',
//         borderLeft: '1px solid #dee2e6',
//       }}
//     >
//       {/* Header */}
//       <CCardHeader className="d-flex justify-content-between align-items-center px-3 py-2">
//         <div className="d-flex align-items-center">
//           <img
//             src={img}
//             alt="User"
//             className="rounded-circle"
//             style={{ width: '40px', height: '40px', objectFit: 'cover' }}
//           />
//           <div className="ms-2">
//             <div className="fw-semibold">{UserEmail}</div>
//             <div className="text-success small">Online</div>
//           </div>
//         </div>

//         <div className="d-flex align-items-center gap-5">
//           <div className="d-flex align-items-center border rounded px-2">
//             <CIcon icon={cilSearch} className="me-2" />
//             <input
//               type="text"
//               className="form-control form-control-sm border-0"
//               placeholder="Search..."
//               style={{ width: '100%' }}
//             />
//           </div>
//           <CIcon icon={cilOptions} className="cursor-pointer" />
//         </div>
//       </CCardHeader>

//       {/* Chat Body */}
//       <div
//         className="flex-grow-1 px-3 py-2"
//         style={{ overflowY: 'auto', backgroundColor: '#f8f8f8' }}
//       >
//         {messages.length === 0 ? (
//           <div
//             className="d-flex flex-column justify-content-center align-items-center"
//             style={{ height: '100%' }}
//           >
//             <img
//               src={chatimage}
//               alt="No messages"
//               style={{ width: '120px', height: '120px', marginBottom: '1rem', opacity: 0.6 }}
//             />
//             <div className="text-muted">No messages yet</div>
//           </div>
//         ) : (
//          <div style={{ display: 'flex', flexDirection: 'column', padding: '10px' }}>
//       {messages.map((msg, idx) => {
//         const isAdmin = msg.role === 'ADMIN';
//         return (
//           <div
//             key={idx}
//             style={{
//               display: 'flex',
//               flexDirection: 'column',
//               alignItems: isAdmin ? 'flex-end' : 'flex-start',
//               marginBottom: '10px'
//             }}
//           >
//             <div
//               style={{
//                 backgroundColor: isAdmin ? '#007bff' : '#e4e6eb',
//                 color: isAdmin ? 'white' : 'black',
//                 padding: '10px 15px',
//                 borderRadius: '15px',
//                 maxWidth: '60%',
//                 wordBreak: 'break-word',
//                 textAlign: 'left'
//               }}
//             >
//               {msg.content}
//             </div>
//             <div
//               style={{
//                 fontSize: '0.75rem',
//                 color: 'gray',
//                 marginTop: '2px',
//                 alignSelf: isAdmin ? 'flex-end' : 'flex-start'
//               }}
//             >
//               {msg.sendername} | {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
//             </div>
//           </div>
//         );
//       })}
//     </div>

//         )}
//         <div ref={messagesEndRef} />
//       </div>

//       {/* Input */}
//       <div className="d-flex align-items-center p-2" style={{ backgroundColor: '#ECEFF5' }}>
//         <input
//           type="text"
//           className="form-control me-2"
//           placeholder="Type a message..."
//           value={inputMsg}
//           onChange={(e) => setInputMsg(e.target.value)}
//           onKeyDown={(e) => {
//             if (e.key === 'Enter') sendMessage()
//           }}
//         />
//         <CButton color="primary" onClick={sendMessage}>
//           ➤
//         </CButton>
//       </div>
//     </div>
//   )
// }

// export default ThirdColumn


// import React, { useState, useEffect, useRef } from 'react'
// import { CCardHeader, CButton } from '@coreui/react'
// import CIcon from '@coreui/icons-react'
// import { cilSearch, cilOptions } from '@coreui/icons'
// import img from '../../assets/images/avatars/1.jpg'
// import chatimage from '../../assets/images/avatars/empty-chats 1.png'
// import SockJS from 'sockjs-client'
// import { Client } from '@stomp/stompjs'

// const PAGE_SIZE = 20

// const ThirdColumn = ({ UserEmail,chatId, UserName, Name, jwtToken, adminEmail, SessionId, adminId, userId }) => {
//   const [messages, setMessages] = useState([])
//   const [inputMsg, setInputMsg] = useState('')
//   const [page, setPage] = useState(0)
//   const stompClientRef = useRef(null)
//   const messagesEndRef = useRef(null)
//   const [isConnected, setIsConnected] = useState(false);

//   useEffect(() => {
//     if (!SessionId || !jwtToken) return

//     const fetchMessages = async () => {
//       try {
//         const res = await fetch(`http://localhost:8080/messages/by-chat/${chatId}`, {
//           headers: {
//             Authorization: `Bearer ${jwtToken}`,
//           },
//         })
//         if (!res.ok) throw new Error('Failed to fetch messages')
//         const data = await res.json()
//         setMessages(prev => [...data.reverse(), ...prev]) // older messages on top
//       } catch (err) {
//         console.error('Error fetching messages:', err)
//       }
//     }

//     fetchMessages()
//   }, [SessionId, page, jwtToken])

//   console.log(messages);


// useEffect(() => {
//   if (!adminId || !jwtToken || !userId) return;

//   const socket = new SockJS('http://localhost:8080/ws');
//   const stompClient = new Client({
//     webSocketFactory: () => socket,
//     connectHeaders: {
//     Authorization: 'Bearer ' + jwtToken,
//   },
//     reconnectDelay: 5000,
//     onConnect: () => {
//       setIsConnected(true);
//       stompClient.subscribe("/user/queue/messages", (message) => {
//   const msg = JSON.parse(message.body);
//   console.log("Received from user:", msg);
//         setMessages((prev) => [...prev, msg]);
//       });
//     },
//     onStompError: (frame) => {
//       console.error('STOMP error:', frame.headers['message']);
//     },
//   });

//   stompClient.activate();
//   stompClientRef.current = stompClient;

//   return () => {
//     if (stompClientRef.current) {
//       stompClientRef.current.deactivate();
//     }
//   };
// }, [adminId, userId, jwtToken]);



// const sendMessage = () => {
//   if (inputMsg.trim() === '' || !stompClientRef.current || !isConnected) return;

//   const msgObj = {
//     senderId: adminId,
//     receiverId: userId,
//     message: inputMsg,
//     sessionId: SessionId,
//     token: jwtToken,
//   };

//   stompClientRef.subscribe("/user/queue/messages", (message) => {
//   const msg = JSON.parse(message.body);
//   console.log("📥 Received message:", msg); // Log to browser console
//   setMessages((prev) => [...prev, msg]);
// });


//   setMessages((prev) => [...prev, msgObj]);
//   setInputMsg('');
// };

//   useEffect(() => {
//   if (messagesEndRef.current) {
//     messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
//   }
// }, [messages]);


//   return (
//     <div
//       style={{
//         width: '74%',
//         display: 'flex',
//         flexDirection: 'column',
//         height: '100%',
//         borderLeft: '1px solid #dee2e6',
//       }}
//     >
//       {/* Header */}
//       <CCardHeader className="d-flex justify-content-between align-items-center px-3 py-2">
//         <div className="d-flex align-items-center">
//           <img
//             src={img}
//             alt="User"
//             className="rounded-circle"
//             style={{ width: '40px', height: '40px', objectFit: 'cover' }}
//           />
//           <div className="ms-2">
//             <div className="fw-semibold">{UserName}</div>
//             <div className="text-success small">Online</div>
//           </div>
//         </div>

//         <div className="d-flex align-items-center gap-5">
//           <div className="d-flex align-items-center border rounded px-2">
//             <CIcon icon={cilSearch} className="me-2" />
//             <input
//               type="text"
//               className="form-control form-control-sm border-0"
//               placeholder="Search..."
//               style={{ width: '100%' }}
//             />
//           </div>
//           <CIcon icon={cilOptions} className="cursor-pointer" />
//         </div>
//       </CCardHeader>

//       {/* Chat Body */}
//       <div
//         className="flex-grow-1 px-3 py-2"
//         style={{ overflowY: 'auto', backgroundColor: '#f8f8f8' }}
//       >
//         {messages.length === 0 ? (
//           <div
//             className="d-flex flex-column justify-content-center align-items-center"
//             style={{ height: '100%' }}
//           >
//             <img
//               src={chatimage}
//               alt="No messages"
//               style={{ width: '120px', height: '120px', marginBottom: '1rem', opacity: 0.6 }}
//             />
//             <div className="text-muted">No messages yet</div>
//           </div>
//         ) : (
//           <div style={{ display: 'flex', flexDirection: 'column', padding: '10px' }}>
//             {messages.map((msg, idx) => {
//   const isUser = msg.role === 'USER';

//   return (
//     <div
//       key={idx}
//       style={{
//         display: 'flex',
//         flexDirection: 'column',
//         alignItems: isUser ? 'flex-start' : 'flex-end',
//         marginBottom: '10px',
//       }}
//     >
//       <div
//         style={{
//           backgroundColor: isUser ? '#e4e6eb' : '#007bff',
//           color: isUser ? 'black' : 'white',
//           padding: '10px 15px',
//           maxWidth: '60%',
//           wordBreak: 'break-word',
//           textAlign: 'left',
//           borderRadius: isUser
//             ? '0 15px 15px 15px' // User: no round top-left corner
//             : '15px 0 15px 15px', // Admin: no round top-right corner
//         }}
//       >
//         {msg.message}
//       </div>
//       <div
//         style={{
//           fontSize: '0.75rem',
//           color: 'gray',
//           marginTop: '2px',
//           alignSelf: isUser ? 'flex-start' : 'flex-end',
//         }}
//       >
//         {msg.senderName || (isUser ? 'User' : 'Admin')} |{' '}
//         {new Date(msg.createdAt || msg.timestamp).toLocaleTimeString([], {
//           hour: '2-digit',
//           minute: '2-digit',
//         })}
//       </div>
//     </div>
//   )
// })}

//           </div>
//         )}
//         <div ref={messagesEndRef} />
//       </div>

//       {/* Input */}
//       <div className="d-flex align-items-center p-2" style={{ backgroundColor: '#ECEFF5' }}>
//         <input
//           type="text"
//           className="form-control me-2"
//           placeholder="Type a message..."
//           value={inputMsg}
//           onChange={(e) => setInputMsg(e.target.value)}
//           onKeyDown={(e) => {
//             if (e.key === 'Enter') sendMessage()
//           }}
//         />
//         <CButton color="primary" onClick={sendMessage}>
//           ➤
//         </CButton>
//       </div>
//     </div>
//   )
// }

// export default ThirdColumn


import React, { useState, useEffect, useRef } from 'react'
import { CCardHeader, CButton } from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilSearch, cilOptions } from '@coreui/icons'
import img from '../../assets/images/avatars/1.jpg'
import chatimage from '../../assets/images/avatars/empty-chats 1.png'

const ThirdColumn = ({ sessionId, UserName, jwtToken, adminId, UserId }) => {
  const [messages, setMessages] = useState([])
  const [inputMsg, setInputMsg] = useState('')
  const messagesEndRef = useRef(null)

  // Fetch sent and received messages
  const fetchMessages = async () => {
    try {
      const sentUrl = `http://localhost:8080/chatbot/${sessionId}/sent/${UserId}`
      const receivedUrl = `http://localhost:8080/chatbot/${sessionId}/${UserId}/received`

      const [sentRes, receivedRes] = await Promise.all([
        fetch(sentUrl, {
          headers: {
            Authorization: jwtToken,
          },
        }),
        fetch(receivedUrl, {
          headers: {
            Authorization: jwtToken,
          },
        }),
      ])

      if (!sentRes.ok || !receivedRes.ok) {
        console.error('Failed to fetch messages')
        return
      }

      const sentData = await sentRes.json()
      const receivedData = await receivedRes.json()

      const sentMessages = sentData.chat || []
      const receivedMessages = receivedData.chat || []

      const combined = [...sentMessages, ...receivedMessages]

      // Sort messages by timestamp
      combined.sort((a, b) =>
        new Date(a.createdAt || a.timestamp) - new Date(b.createdAt || b.timestamp),
      )

      setMessages(combined)
    } catch (err) {
      console.error('Error fetching messages:', err)
    }
  }

  console.log("messages",messages)

  // // Load messages when sessionId or UserId changes
  // useEffect(() => {
  //   if (sessionId && UserId) {
  //     fetchMessages()
  //   }
  // }, [sessionId, UserId])

  // Polling every 3 seconds
    // useEffect(() => {
    //   if (sessionId && UserId) {
    //     fetchMessages();
    //   const intervalId = setInterval(fetchMessages, 3000);
    //   }
  
    //   return () => {
    //     clearInterval(intervalId); // cleanup on unmount
    //   };
    // }, []);

  useEffect(() => {
  let intervalId;

  if (sessionId && UserId) {
    fetchMessages(); // initial fetch
    intervalId = setInterval(fetchMessages, 3000); // fetch every 3 seconds
  }

  return () => {
    if (intervalId) clearInterval(intervalId); // cleanup on unmount
  };
}, [sessionId, UserId])


  // Auto-scroll to latest message
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [messages])

  // Send message
  const sendMessage = async () => {
    if (inputMsg.trim() === '') return

    const msgObj = {
      sender: adminId,
      receiver: UserId,
      message: inputMsg,
      // role: 'ADMIN',
      // createdAt: new Date().toISOString(),
    }

    try {
      const response = await fetch('http://localhost:8080/chatbot/send', {
        method: 'POST',
        headers: {
          Authorization: jwtToken,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(msgObj),
      })

      if (!response.ok) {
        throw new Error('Failed to send message')
      }

      setInputMsg('')
      await fetchMessages() // Refresh all messages after sending
    } catch (err) {
      console.error('Error sending message:', err)
    }
  }

  return (
    <div
      style={{
        width: '74%',
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        borderLeft: '1px solid #dee2e6',
      }}
    >
      {/* Header */}
      <CCardHeader className="d-flex justify-content-between align-items-center px-3 py-2">
        <div className="d-flex align-items-center">
          <img
            src={img}
            alt="User"
            className="rounded-circle"
            style={{ width: '40px', height: '40px', objectFit: 'cover' }}
          />
          <div className="ms-2">
            <div className="fw-semibold">{UserName}</div>
            <div className="text-success small">Online</div>
          </div>
        </div>

        <div className="d-flex align-items-center gap-5">
          <div className="d-flex align-items-center border rounded px-2">
            <CIcon icon={cilSearch} className="me-2" />
            <input
              type="text"
              className="form-control form-control-sm border-0"
              placeholder="Search..."
              style={{ width: '100%' }}
            />
          </div>
          <CIcon icon={cilOptions} className="cursor-pointer" />
        </div>
      </CCardHeader>

      {/* Chat Body */}
      <div
        className="flex-grow-1 px-3 py-2"
        style={{ overflowY: 'auto', backgroundColor: '#f8f8f8' }}
      >
        {messages.length === 0 ? (
          <div
            className="d-flex flex-column justify-content-center align-items-center"
            style={{ height: '100%' }}
          >
            <img
              src={chatimage}
              alt="No messages"
              style={{ width: '120px', height: '120px', marginBottom: '1rem', opacity: 0.6 }}
            />
            <div className="text-muted">No messages yet</div>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', padding: '10px' }}>
            {messages.map((msg, idx) => {
              const isUser = msg.role === 'USER'
              return (
                <div
                  key={idx}
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: isUser ? 'flex-start' : 'flex-end',
                    marginBottom: '10px',
                  }}
                >
                  <div
                    style={{
                      backgroundColor: isUser ? '#e4e6eb' : '#007bff',
                      color: isUser ? 'black' : 'white',
                      padding: '10px 15px',
                      maxWidth: '60%',
                      wordBreak: 'break-word',
                      textAlign: 'left',
                      borderRadius: isUser ? '0 15px 15px 15px' : '15px 0 15px 15px',
                    }}
                  >
                    {msg.message || msg.content}
                  </div>
                  <div
                    style={{
                      fontSize: '0.75rem',
                      color: 'gray',
                      marginTop: '2px',
                      alignSelf: isUser ? 'flex-start' : 'flex-end',
                    }}
                  >
                    {(msg.senderName || (isUser ? 'User' : 'Admin')) +
                      ' | ' +
                      new Date(msg.createdAt || msg.timestamp).toLocaleTimeString([], {
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                  </div>
                </div>
              )
            })}
            <div ref={messagesEndRef} />
          </div>
        )}
      </div>

      {/* Input Box */}
      <div className="d-flex align-items-center p-2" style={{ backgroundColor: '#ECEFF5' }}>
        <input
          type="text"
          className="form-control me-2"
          placeholder="Type a message..."
          value={inputMsg}
          onChange={(e) => setInputMsg(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') sendMessage()
          }}
        />
        <CButton color="primary" onClick={sendMessage}>
          ➤
        </CButton>
      </div>
    </div>
  )
}

export default ThirdColumn
