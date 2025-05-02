import React, { useState } from 'react';
import { CCardHeader, CCardBody, CTable, CTableHead, CTableRow, CTableHeaderCell, CTableBody, CTableDataCell, CButton, CRow, CCol, CInputGroup, CInputGroupText, CFormInput } from '@coreui/react';
import { FaSearch, FaPlus, FaEdit, FaTrash } from 'react-icons/fa';


const AddMember = ({rows, setRows, token,onCancel,editId}) => {

  console.log("editID",editId)

    const handleAddRow = () => {
            setRows([...rows, { email: '', role: '' }])
          }
        
    const handleDeleteRow = (index) => {
        const updated = [...rows]
        updated.splice(index, 1)
        setRows(updated)
    }

    const handleSendInvitations = async () => {
            for (const row of rows) {
              if (!row.email || !row.role) {
                alert("Please enter both email and role for all rows.");
                return;
              }
          
              try {
                const response = await fetch("http://localhost:8080/chatbot/invite", {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    Authorization: token,
                  },
                  body: new URLSearchParams({
                    email: row.email,
                    role: row.role,
                  }),
                });
          
                const result = await response.text();
                if (!response.ok) {
                  alert(`Failed to invite ${row.email}: ${result}`);
                } else {
                  console.log(`Invitation sent to ${row.email}`);
                }
              } catch (error) {
                console.error("Error sending invitation:", error);
                alert(`Error sending to ${row.email}`);
              }
            }
          
            alert("Invitations sent!");
          };
        
  return (
   <>
   <CCardHeader>Add Members</CCardHeader>

   <CCardBody className="p-1" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
  
   <div style={{ flex: 1, overflowY: 'auto', maxHeight: '480px' }}>
     <CTable>
       <CTableHead>
         <CTableRow>
             
           <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7', width: '50%' }}>
           Email
           </CTableHeaderCell>
           <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7', width: '40%' }}>
             Role
           </CTableHeaderCell>
           <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>
             Action
           </CTableHeaderCell>
         </CTableRow>
       </CTableHead>
       <CTableBody>
         {rows.map((row, index) => (
          <CTableRow key={index}>
            <CTableDataCell>
              <CFormInput
                type="email"
                placeholder="Enter the email address"
                style={{ width: '90%' }}
                value={row.email}
                onChange={(e) => {
                  const updated = [...rows];
                  updated[index].email = e.target.value;
                  setRows(updated);
                }}
              />
            </CTableDataCell>

            <CTableDataCell>
              <div className="d-flex gap-4">
                <div>
                  <input
                    type="radio"
                    id={`admin-${index}`}
                    name={`role-${index}`}
                    value="admin"
                    checked={row.role === 'admin'}
                    onChange={(e) => {
                      const updated = [...rows];
                      updated[index].role = e.target.value;
                      setRows(updated);
                    }}
                  />
                  <label htmlFor={`admin-${index}`} className="ms-2">Admin</label>
                </div>
                <div>
                  <input
                    type="radio"
                    id={`agent-${index}`}
                    name={`role-${index}`}
                    value="agent"
                    checked={row.role === 'agent'}
                    onChange={(e) => {
                      const updated = [...rows];
                      updated[index].role = e.target.value;
                      setRows(updated);
                    }}
                  />
                  <label htmlFor={`agent-${index}`} className="ms-2">Agent</label>
                </div>
              </div>
            </CTableDataCell>

            <CTableDataCell>
              {rows.length > 1 && (
                <FaTrash
                  className="me-3 p-2"
                  style={{
                    backgroundColor: '#F42A2A',
                    color: 'white',
                    fontSize: '30px',
                    borderRadius: '2px',
                    cursor: 'pointer'
                  }}
                  onClick={() => handleDeleteRow(index)}
                />
              )}
            </CTableDataCell>
          </CTableRow>
        ))}
      </CTableBody>
    </CTable>

    <div className="d-flex justify-content-end mt-3">
      <CButton style={{ backgroundColor: '#F3F4F7', border: '1px solid black' }} onClick={handleAddRow}>
        <FaPlus className="me-2" />
        Add Another
      </CButton>
    </div>
  </div>

  <hr/>
  {/* Footer buttons inside the card */}
  <div style={{  display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
   
  <CButton
      style={{ backgroundColor: '#F3F4F7', border: '1px solid #79747E' }}
      onClick={onCancel}
    >
      Cancel
    </CButton>
    <CButton
      style={{ backgroundColor: '#5856D6', color: '#FFFFFF' }}
      onClick={handleSendInvitations}
    >
      Send 
    </CButton>
  </div>

</CCardBody>
  

   </>
  )
}

export default AddMember