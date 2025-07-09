import React, {useState , useEffect} from 'react';
import { CCardHeader, CCardBody, CTable, CTableHead, CTableRow, CTableHeaderCell, CTableBody, CTableDataCell, CButton, CRow, CCol, CInputGroup, CInputGroupText, CFormInput } from '@coreui/react';
import { FaSearch,FaTrash } from 'react-icons/fa';
import axios from 'axios';

const GridTable = ({currentAdminEmail}) => {

    const [sessiondetails,setSessiondetails] = useState([]);

    const fetchChatSession = async () => {
        try {
          const res = await fetch(`http://localhost:8080/sessionlist?receiverEmail=${currentAdminEmail}`)
          if (!res.ok) throw new Error('Failed to fetch chat rooms')
          const data = await res.json()
          setSessiondetails(data)
        } catch (err) {
          console.error(err)
        }
      }
    
      useEffect(() => {
        fetchChatSession()
        // const intervalId = setInterval(fetchChatSession, 3000)
        // return () => clearInterval(intervalId)
      }, [])


      console.log("sessiondetails",sessiondetails)


  return (
    <>
        <CCardHeader>
            <CRow className="align-items-center">
              {/* Title */}
              <CCol xs="12" sm="6" className="mb-2 mb-sm-0">
                <h6 className="fw-bold mb-0">Visitors Details</h6>
              </CCol>
        
              {/* Search Input */}
              <CCol xs="12" sm="4" className="mb-2 mb-sm-0">
                <CInputGroup className='p-0'>
                  <CInputGroupText>
                    <FaSearch />
                  </CInputGroupText>
                  <CFormInput placeholder="Search members..." />
                </CInputGroup>
              </CCol>
        
              {/* Add Member Button */}
              <CCol xs="12" sm="2" className="text-sm-end">
                <CButton  style={{color:'white',color:'black'}}>
                    <FaTrash/>
                </CButton>
             </CCol>
            </CRow>
        </CCardHeader>

        <CCardBody style={{ maxHeight: '520px', overflowY: 'auto' }}>
            <CTable>
                <CTableHead>
                    <CTableRow>
                        <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>
                           <input
                                type="checkbox"
                                // onChange={handleSelectAll}
                                // checked={selectAll}
                           />
                        </CTableHeaderCell>
                        <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7', width: '30%' }}>Username</CTableHeaderCell>
                        <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7'}}>Total Message</CTableHeaderCell>
                        <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7'}}>Agents</CTableHeaderCell>
                        <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7'}}>Status</CTableHeaderCell>
                        <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7'}}>Updated</CTableHeaderCell>
                    </CTableRow>

                    
                </CTableHead>
                
                <CTableBody>
                    {sessiondetails && sessiondetails.length > 0 ? (
  sessiondetails.map((sessiondetail, index) => (
    <CTableRow key={index}>
        <CTableHeaderCell scope="row">
          <input
            type="checkbox"
            // checked={selectedAdmins.includes(sessiondetail.id)}
            // onChange={() => handleRowCheckboxChange(sessiondetail.id)}
          />
        </CTableHeaderCell>
        <CTableDataCell>
  <div>
    <div className="fw-bold">{sessiondetail.username}</div>
    <div className="text-muted small">{sessiondetail.useremail}</div>
  </div>
</CTableDataCell>

        <CTableDataCell>{sessiondetail.totalMessage}</CTableDataCell>
        <CTableDataCell>{sessiondetail.agents}</CTableDataCell>
        <CTableDataCell>{sessiondetail.status ? 'Online' : 'Offline'}</CTableDataCell>
        <CTableDataCell>{sessiondetail.time}</CTableDataCell>
        
    </CTableRow>
  ))
) : (
  <CTableRow>
    <CTableDataCell colSpan="5" className="text-center">
      No session details found.
    </CTableDataCell>
  </CTableRow>
)}
            
                
                </CTableBody>
            </CTable>
        </CCardBody>

        


    </>
  )
}

export default GridTable