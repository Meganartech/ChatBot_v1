import React, { useState, useEffect } from 'react';
import axios from 'axios';
import DepartmentList from './DepartmentList';
import AddDepartment from './AddDepartment';
import { useLocation } from 'react-router-dom';

const Departments = () => {
  const [isOpenAddDep, setIsOpenAddDep] = useState(false);
  const [departments, setDepartments] = useState([]);
  const [department, setDepartment] = useState({
    name: '',
    description: '',
    adminIds: [],
  });
  const [users, setUsers] = useState([]);
  const [editDepId,setEditDepId] = useState();

  const handleEdit = (DepId) =>{
      setEditDepId(DepId);
  }

  useEffect(() => {
    fetchDepartments();
    fetchMembers();
    }, []);
  

  const fetchDepartments = async () => {
    try {
      const response = await axios.get('http://localhost:8080/chatbot/getAllDepartment');
      setDepartments(response.data);
    } catch (error) {
      console.error('Error fetching departments:', error);
      setDepartments([]);
    }
  };

  const fetchMembers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/chatbot/getadminnames');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching members:', error);
      setUsers([]);
    }
  };

  const token = sessionStorage.getItem('token');
  const location = useLocation();

  useEffect(() => {
    if (location.pathname.endsWith('/departments')) {
      setIsOpenAddDep(false);
      setEditDepId(undefined); // ✅ clear edit mode
    }
  }, [location]);

  return (
    <>
    {isOpenAddDep || editDepId ? (
    <AddDepartment 
    department={department}
    setDepartment={setDepartment}
    token={token}
    users={users}
    editDepId={editDepId}
    onCancel={() => {
      setIsOpenAddDep(false);
      setEditDepId(undefined);
    }}
    />
    ):(
      <DepartmentList
      token={token}
      departments={departments}
      onAddDepClick={() => setIsOpenAddDep(true)}
      refreshDepartment={fetchDepartments}
      Edit={handleEdit}
      />
    )
  }
    </>
  )
}

export default Departments