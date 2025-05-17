import React, { useState, useEffect } from 'react';
import axios from 'axios';
import TriggerList from './TriggerList'
import TriggerAdd from './TriggerAdd'


const Trigger = () => {
  const [isOpenAddTrigger, setIsOpenAddTrigger] = useState(false);
  const [GetTrigger, setGetTrigger] = useState([]);
  const [Trigger, setTrigger] = useState({
      name: '',
      delay: '',
      triggerTypeId: '',
      text:'',
      departmentIds: [],
    });
  const [triggerTypes, setTriggerTypes] = useState([])
  const token = sessionStorage.getItem('token');

  console.log(token);

  useEffect(() => {
    fetchTriggers();
    fetchTriggerType();
    }, []);
    
  
  const fetchTriggers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/chatbot/getAllTrigger');
      setGetTrigger(response.data);
    } catch (error) {
      console.error('Error fetching departments:', error);
      setGetTrigger([]);
    }
  };

  const fetchTriggerType = async () => {
    try {
      const response = await axios.get('http://localhost:8080/chatbot/getTriggerType')
      setTriggerTypes(response.data)
    } catch (error) {
      console.error('error fetching triggertype', error)
      setTriggerTypes([])
    }
  }

  console.log(GetTrigger)
  return (
    <>
      {isOpenAddTrigger ? (
      <TriggerAdd
        Trigger={Trigger}
        setTrigger={setTrigger}
        token={token}
        triggerTypes={triggerTypes}
        // editDepId={editDepId}
        onCancel={() => {
        setIsOpenAddTrigger(false);
        // setEditDepId(undefined);
      }}
      />
      ):(
      <TriggerList 
        token={token}
        GetTrigger={GetTrigger}
        onAddTriggerClick={() => setIsOpenAddTrigger(true)}
        refreshDepartment={fetchTriggers}
        // Edit={handleEdit}
      />
      )}
    </>
  )
}

export default Trigger