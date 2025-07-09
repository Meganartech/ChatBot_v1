import React, { useState, useEffect, useRef } from 'react'
import { CCardHeader, CButton } from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilSearch, cilOptions } from '@coreui/icons'
import img from '../../assets/images/avatars/1.jpg'
import chatimage from '../../assets/images/avatars/empty-chats 1.png'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const ThirdColumn = ({ sessionId, UserName, adminEmail, useremail }) => {
  const [messages, setMessages] = useState([])
  const [inputMsg, setInputMsg] = useState('')
  const messagesEndRef = useRef(null) // ✅ Fix: defined here
  const stompClient = useRef(null)

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/chat'),
      onConnect: () => {
        client.subscribe(`/topic/messages/${sessionId}`, (msg) => {
          const message = JSON.parse(msg.body)
          setMessages((prev) => [...prev, message])
        })
      },
      debug: (str) => console.log(str),
    })

    client.activate()
    stompClient.current = client

    fetch(`http://localhost:8080/api/history/${sessionId}`)
      .then((res) => res.json())
      .then((data) => setMessages(data))

    return () => {
      client.deactivate()
    }
  }, [sessionId])

  // ✅ Auto-scroll to the bottom when messages update
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [messages])

  const sendMessage = () => {
    if (stompClient.current && stompClient.current.connected) {
      stompClient.current.publish({
        destination: '/app/send',
        body: JSON.stringify({
          sessionId,
          sender: adminEmail,
          receiver: useremail,
          content: inputMsg,
          role: 'ADMIN',
        }),
      })
      setInputMsg('')
    } else {
      console.warn('STOMP client not connected.')
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
