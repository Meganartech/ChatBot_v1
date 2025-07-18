import React from 'react'
import CIcon from '@coreui/icons-react'
import {
  cilPencil,
  cilChatBubble,
  cilTags,
  cilWarning,
  cilTrash
} from '@coreui/icons'
import API_URL from '../../Config'

const FirstColumn = ({ chatimage,ChatID}) => {

  console.log("ChatId",ChatID)

  return (
    <div className="border-end pe-2 d-flex flex-column align-items-center pt-3 ps-1 h-100" style={{ width: '6%' }}>
      <div>
        <img
          src={chatimage}
          alt="Chatbot"
          style={{
            width: '100%',
            height: '100%',
            padding: '5px',
            borderRadius: '5px',
            border: '1px solid #B3B3B3',
            marginTop: '2px'
          }}
        />
      </div>

      <div className="flex-grow-1 d-flex flex-column justify-content-center">
        {[
          { icon: cilPencil, label: 'Compose' },
          { icon: cilChatBubble, label: 'Chat' },
          { icon: cilTags, label: 'Ticket' },
          { icon: cilWarning, label: 'Spam' },
          { icon: cilTrash, label: 'Trash' }
        ].map(({ icon, label }) => (
          <div className="d-flex flex-column align-items-center mb-2" key={label}>
            <div
              className="d-flex justify-content-center align-items-center"
              style={{
                width: '50px',
                height: '50px',
                border: '1px solid black',
                borderRadius: '6px'
              }}
            >
              <CIcon icon={icon} title={label} style={{ fontSize: '18px' }} />
            </div>
            <span style={{ fontSize: '12px', marginTop: '6px' }}>{label}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

export default FirstColumn
