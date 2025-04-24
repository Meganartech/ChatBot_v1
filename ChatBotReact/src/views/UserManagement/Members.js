import React, { useState,useEffect } from "react";
import axios from "axios";
import {
  CCard,
  CCardHeader,
  CCardBody,
  CFormInput,
  CButton,
  CRow,
  CCol,
  CInputGroup,
  CInputGroupText,
  CTable,
  CTableBody,
  CTableCaption,
  CTableDataCell,
  CTableHead,
  CTableHeaderCell,
  CTableRow,
} from '@coreui/react'
import { FaSearch, FaPlus } from 'react-icons/fa'
import { FaEdit, FaTrash } from 'react-icons/fa'; // Import the icons

const Members = () => {

  const [admins, setAdmins] = useState([]);
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    axios.get('http://localhost:8080/chatbot/getAllAdmin') // Adjust port if needed
      .then(response => {
        setAdmins(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching admin data:', error);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading...</div>;

  const handleEdit = (adminId) => {
    // You can either log the ID or handle the edit functionality here
    console.log("Edit admin with ID:", adminId);
  
    // Example: Redirect to an edit page (if you're using React Router)
    // history.push(`/edit-admin/${adminId}`);
  
    // Example: Open a modal to edit the admin details
    // setIsModalOpen(true); // Open modal to edit the admin
  };

  return (
    <>
      <CCardHeader>
        <CRow className="align-items-center">
          {/* Title */}
          <CCol xs="12" sm="6" className="mb-2 mb-sm-0">
            <h6 className="fw-bold mb-0">Property Members</h6>
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
            <CButton  style={{backgroundColor:'#74B047',color:'white'}}>
              <FaPlus className="me-2" />
              Add Member
            </CButton>
          </CCol>
        </CRow>
      </CCardHeader>

      <CCardBody>
      <CTable>
      <CTableHead >
  <CTableRow>
    <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>#</CTableHeaderCell>
    <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7', width: '40%' }}>Agent</CTableHeaderCell>
    <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>Role</CTableHeaderCell>
    <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>Agent Status</CTableHeaderCell>
    <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>Action</CTableHeaderCell>
  </CTableRow>
</CTableHead>

                <CTableBody>
                  {admins.map((admin) => (
                    <CTableRow key={admin.id}>
                      <CTableHeaderCell scope="row">{admin.id}</CTableHeaderCell>
                      <CTableDataCell>{admin.username}</CTableDataCell>
                      <CTableDataCell>{admin.role?.role || 'N/A'}</CTableDataCell>
                      <CTableDataCell>
                        {admin.status === true ? (
                          <span style={{ backgroundColor: '#F9B115', padding: '4px 8px', borderRadius: '4px', color: 'white' }}>
                            Active
                          </span>
                        ) : (
                          <span style={{ backgroundColor: '#E55353', padding: '4px 8px', borderRadius: '4px', color: 'white' }}>
                            Pending
                          </span>
                        )}
                      </CTableDataCell>

                      <CTableDataCell>
                          <FaEdit
                            className="me-2 p-2"
                            style={{
                              backgroundColor: '#93BF42',
                              color: 'white',
                              fontSize: '30px', // Adjust the font size here
                              borderRadius:'2px',
                            }}
                            onClick={()=>handleEdit(admin.id)}
                          />
                          <FaTrash 
                            className="me-1 p-2"
                            style={{
                              backgroundColor:'#F42A2A',
                              color:'white',
                              fontSize:'30px',
                              borderRadius:'2px',
                            }}
                          /> {/* Trash icon for Delete */}
                      </CTableDataCell>

                    </CTableRow>
                  ))}
                </CTableBody>

              </CTable>
      </CCardBody>
    </>
  )
}

export default Members
