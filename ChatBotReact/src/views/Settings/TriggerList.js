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


const TriggerList = ({token,GetTrigger,onAddTriggerClick,refreshTrigger,Edit}) => {

  const [selectedTrigger, setSelectedTrigger] = useState([]);
  const [selectAll, setSelectAll] = useState(false);
  console.log("trigger",GetTrigger)

  const handleSelectAll = () => {
    if (selectAll) {
      setSelectedTrigger([]);
    } else {
      const allIds = GetTrigger.map((trigger) => trigger.id);
      setSelectedTrigger(allIds);
    }
    setSelectAll(!selectAll);
  };

  const handleRowCheckboxChange = (id) => {
    if (selectedTrigger.includes(id)) {
      setSelectedTrigger(selectedTrigger.filter((selectedId) => selectedId !== id));
    } else {
      setSelectedTrigger([...selectedTrigger, id]);
    }
  };

  const handleDelete = async (triggerId) => {
    try{
      const response = await axios.delete(`http://localhost:8080/chatbot/deleteTrigger/${triggerId}`,{
        headers:{
          Authorization: token,
        }
      });
      if (response.status <= 204) {
        refreshTrigger();
        alert('Trigger deleted successfully');
      } else {
        console.log(response.data);
      }
    } catch (error) {
      if (error.response) {
        console.error(error.response.data.message);
        alert(error.response.data.message);
      } else {
        console.error('Something went wrong', error.message);
      }
    }
  };

  return (
    
    <>
      <CCardHeader>
        <CRow className="align-items-center">
          <CCol xs="12" sm="10" className="mb-2 mb-sm-0">
            <h6 className="fw-bold mb-0">Triggers</h6>
          </CCol>
          <CCol xs="12" sm="2" className="text-sm-end">
            <CButton  
              style={{backgroundColor:'#74B047',color:'white',fontSize:'12px'}}
              onClick={onAddTriggerClick}>
              <FaPlus className="me-2" />
              Add Trigger
            </CButton>
          </CCol>
        </CRow>
      </CCardHeader>

      <CCardBody style={{ maxHeight: '520px', overflowY: 'auto' }}>
        <CTable>
          <CTableHead >
             <CTableRow>
              <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7' }}>
                <input
                  type="checkbox"
                  checked={selectAll}
                  onChange={handleSelectAll}
                />
              </CTableHeaderCell>
              <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7', width: '30%' }}>Name</CTableHeaderCell>
              <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7'}}>Description</CTableHeaderCell>
              <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7'}}>Type</CTableHeaderCell>
              <CTableHeaderCell scope="col" style={{ backgroundColor: '#F3F4F7',width:'10%'}}>Action</CTableHeaderCell>
             </CTableRow>
          </CTableHead>
          <CTableBody>
            {GetTrigger && GetTrigger.length > 0 ? (
                GetTrigger.map((trigger) => (
                  <CTableRow key={trigger.triggerid}>
                    <CTableHeaderCell scope="row">
                      <input
                        type="checkbox"
                        checked={selectedTrigger.includes(trigger.triggerid)}
                        onChange={() => handleRowCheckboxChange(trigger.triggerid)}
                      />
                    </CTableHeaderCell>
            
                    <CTableDataCell>{trigger.name}</CTableDataCell>
                    <CTableDataCell>{trigger.textOption.text}</CTableDataCell>
                    <CTableDataCell>{trigger.triggerType.triggerType}</CTableDataCell>
            
                    <CTableDataCell>
                      <FaEdit
                        className="me-2 p-2"
                        style={{
                          backgroundColor: '#93BF42',
                          color: 'white',
                          fontSize: '30px',
                          borderRadius: '2px',
                        }}
                        onClick={() => Edit(trigger.triggerid)}
                      />
                      <FaTrash
                        className="me-1 p-2"
                        style={{
                          backgroundColor: '#F42A2A',
                          color: 'white',
                          fontSize: '30px',
                          borderRadius: '2px',
                        }}
                        onClick={() => handleDelete(trigger.triggerid)}
                      />
                    </CTableDataCell>
                  </CTableRow>
                ))
              ) : (
                <CTableRow>
                  <CTableDataCell colSpan="5" className="text-center">
                    No Trigger available.
                  </CTableDataCell>
                </CTableRow>
              )}
          </CTableBody>
        </CTable>
      </CCardBody>
    </>
  )
}

export default TriggerList