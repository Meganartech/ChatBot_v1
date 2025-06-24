import React, { useState, useEffect } from 'react'
import axios from 'axios'
import {
  CCardHeader,
  CCardBody,
  CFormInput,
  CFormLabel,
  CFormSelect,
  CRow,
  CCol,
  CButton,
  CFormTextarea,
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CFormSwitch,
} from '@coreui/react'
import { cilPlus, cilMenu } from '@coreui/icons'
import CIcon from '@coreui/icons-react'
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import {
  arrayMove,
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { GripVertical } from 'lucide-react'

const DraggableItem = ({ id, children }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({ id })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  }

return (
    <div ref={setNodeRef} style={style} className="mb-2 border rounded p-2 bg-white">
    
    {/* Left: Drag handle */}
    <div className="d-flex align-items-center">
      <span
        {...attributes}
        {...listeners}
        className="me-3 cursor-grab"
        style={{ display: 'flex', alignItems: 'center' }}
      >
        <GripVertical size={16} />
      </span>
      <span className="fw-medium" style={{width:'100%'}}>
        {children}{/* e.g., "Text Area" or "Department" */}
      </span>
    </div>

  </div>
  )
}

const TriggerAdd = ({triggerTypes,departments,editTriggerId}) => {
  const [triggerName, setTriggerName] = useState('')
  const [selectedTriggerType, setSelectedTriggerType] = useState('')
  const [delay, setDelay] = useState('No Delay')
  const [textAreaContent, setTextAreaContent] = useState('')
  const [savedTextMessage, setSavedTextMessage] = useState('')
  const [isTextAreaEnabled, setIsTextAreaEnabled] = useState(false)
  const [isDepartmentEnable, setIsDepartmentEnable] = useState(false)
  const [expandTextArea, setExpandTextArea] = useState(false)
  const [expandDepartment, setExpandDepartment] = useState(false)
  const [selectedDepartments, setSelectedDepartments] = useState([])
  

  const [items, setItems] = useState([])

  console.log(items)
  console.log("selectedDepartments",selectedDepartments)

  useEffect(() => {
    if (editTriggerId) {
      fetchTrigger(editTriggerId);
    }
  }, [editTriggerId]);

  const fetchTrigger = async (editTriggerId) => {
  try {
    const response = await axios.get(`http://localhost:8080/chatbot/gettrigger/${editTriggerId}`);
    const data = response.data;

    setTriggerName(data.name);
    setDelay(data.delay === 0 ? 'No Delay' : String(data.delay));
    setSelectedTriggerType(data.triggerType?.id || '');
    setItems(data.firstTrigger || []);

    // Set text area if present
    if (data.textOption && data.textOption.text) {
      setTextAreaContent(data.textOption.text);
      setSavedTextMessage(data.textOption.text);
      setIsTextAreaEnabled(true);
    }

    // Set departments if present
    if (Array.isArray(data.departments) && data.departments.length > 0) {
  const departmentNames = data.departments.map((dep) => dep.name); 
  setSelectedDepartments(departmentNames);
  setIsDepartmentEnable(true);
}


    console.log('Fetched trigger:', data);
  } catch (error) {
    console.error('Error fetching trigger:', error);
    alert('Failed to load trigger for editing.');
  }
};


  useEffect(() => {
    if (triggerTypes.length > 0 && !selectedTriggerType) {
      setSelectedTriggerType(triggerTypes[0].id)
    }
  }, [triggerTypes])

  const handleAddTriggerOption = (option) => {
    if (!items.includes(option)) setItems((prev) => [...prev, option])
  }

  const sensors = useSensors(useSensor(PointerSensor))

  const handleDragEnd = (event) => {
    const { active, over } = event
    if (active.id !== over.id) {
      const oldIndex = items.indexOf(active.id)
      const newIndex = items.indexOf(over.id)
      setItems(arrayMove(items, oldIndex, newIndex))
    }
  }

  const handleDepartmentToggle = (depName) => {
    setSelectedDepartments((prev) =>
      prev.includes(depName) ? prev.filter((name) => name !== depName) : [...prev, depName]
    )
  }

  const handleSaveTrigger = async () => {
  try {
    const selectedDepartmentIds = departments
      .filter((dept) => selectedDepartments.includes(dept.depName))
      .map((dept) => dept.id);

    const payload = {
      name: triggerName,
      delay: delay === 'No Delay' ? 0 : parseInt(delay),
      triggerTypeId: Number(selectedTriggerType),
      text: isTextAreaEnabled ? savedTextMessage : null,
      departmentIds: isDepartmentEnable ? selectedDepartmentIds : [],
      firstTrigger: items,
    };

    let response;
    if (editTriggerId) {
      // PATCH for update
      response = await axios.patch(`http://localhost:8080/chatbot/UpdateTrigger/${editTriggerId}`, payload);
      alert('Trigger updated successfully!');
    } else {
      // POST for create
      response = await axios.post('http://localhost:8080/chatbot/AddTrigger', payload);
      alert('Trigger created successfully!');
    }

    console.log('Trigger saved:', response.data);
  } catch (error) {
    console.error('Error saving trigger:', error);
    alert('Failed to save trigger.');
  }
};

  return (
    <>
      <CCardHeader>Add Trigger</CCardHeader>
      <CCardBody style={{ maxHeight: '520px', overflowY: 'auto' }}>
        <CRow>
          <CCol md={6}>
            <div className="mb-3">
              <CFormLabel>Trigger Name</CFormLabel>
              <CFormInput
                value={triggerName}
                onChange={(e) => setTriggerName(e.target.value)}
                placeholder="Enter trigger name"
              />
            </div>

            <div className="mb-3">
              <CFormLabel>Trigger Type</CFormLabel>
              <CFormSelect
                value={selectedTriggerType}
                onChange={(e) => setSelectedTriggerType(e.target.value)}
              >
                {triggerTypes.map((type) => (
                  <option key={type.id} value={type.id}>
                    {type.triggerType}
                  </option>
                ))}
              </CFormSelect>
            </div>

            <div className="mb-3">
              <CFormLabel>Delay</CFormLabel>
              <CFormSelect value={delay} onChange={(e) => setDelay(e.target.value)}>
                <option>No Delay</option>
                <option>1</option>
                <option>5</option>
                <option>10</option>
              </CFormSelect>
            </div>

            <div className="mb-3 d-flex justify-content-between align-items-center">
              <CFormLabel className="mb-0">Set your trigger</CFormLabel>
              <CDropdown>
                <CDropdownToggle style={{ backgroundColor: 'white' }}>+ Add</CDropdownToggle>
                <CDropdownMenu>
                  <CDropdownItem onClick={() => handleAddTriggerOption('Text Area')}>
                    Text Area
                  </CDropdownItem>
                  <CDropdownItem onClick={() => handleAddTriggerOption('Department')}>
                    Department
                  </CDropdownItem>
                </CDropdownMenu>
              </CDropdown>
            </div>

            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
              <SortableContext items={items} strategy={verticalListSortingStrategy}>
                {items.map((item) => (
                  <DraggableItem key={item} id={item}>
                    {item === 'Text Area' && (
                      <div className="rounded bg-white">
                        <div className="d-flex justify-content-between align-items-center">
                          <strong>Text Area</strong>
                          <div className="d-flex align-items-center gap-2 ms-auto">
                            <CButton color="link" onClick={() => setExpandTextArea((prev) => !prev)}>
                              <CIcon icon={cilPlus} />
                            </CButton>
                            <CFormSwitch
                              id="switchTextArea"
                              checked={isTextAreaEnabled}
                              onChange={() => setIsTextAreaEnabled((prev) => !prev)}
                            />
                          </div>
                        </div>
                        {expandTextArea && (
                          <div className="mt-2">
                            <CFormTextarea
                              rows={3}
                              value={textAreaContent}
                              onChange={(e) => setTextAreaContent(e.target.value)}
                              placeholder="Enter your message..."
                            />
                            <div className="d-flex gap-2 mt-2">
                              <CButton color="secondary" onClick={() => setExpandTextArea(false)}>
                                Cancel
                              </CButton>
                              <CButton
  color="primary"
  disabled={!textAreaContent.trim()} // disable if empty
  onClick={() => {
    if (textAreaContent.trim()) {
      setSavedTextMessage(textAreaContent.trim());
      setExpandTextArea(false);
    }
  }}
>
 Save
</CButton>

                            </div>
                          </div>
                        )}
                      </div>
                    )}

                    {item === 'Department' && (
                      <div className="rounded bg-white">
                        <div className="d-flex justify-content-between align-items-center">
                          <strong>Department</strong>
                          <div className="d-flex align-items-center gap-2 ms-auto">
                            <CButton color="link" onClick={() => setExpandDepartment((prev) => !prev)}>
                              <CIcon icon={cilPlus} />
                            </CButton>
                            <CFormSwitch
                              id="switchDepartment"
                              checked={isDepartmentEnable}
                              onChange={() => setIsDepartmentEnable((prev) => !prev)}
                            />
                            
                          </div>
                         
                        </div>
                        {expandDepartment && (
                          <div className="mt-2">
                            {departments.map((dept) => (
                              <CButton
                                key={dept.id}
                                size="sm"
                                className={`me-2 mb-2 ${
                                  selectedDepartments.includes(dept.depName)
                                    ? 'bg-secondary text-white'
                                    : 'bg-light'
                                }`}
                                onClick={() => handleDepartmentToggle(dept.depName)}
                              >
                                {dept.depName}
                              </CButton>
                              
                            ))}
                            <div className="d-flex gap-2 mt-2">
                              <CButton color="secondary" onClick={() => setExpandDepartment(false)}>
                                Cancel
                              </CButton>
                            </div>
                          </div>
                          
                        )}
                      </div>

                    )}
                     
                  </DraggableItem>
                ))}
              </SortableContext>
            </DndContext>
          </CCol>

          {/* Preview */}
          <CCol md={6}>
            <div className="border rounded p-4" style={{ background: '#f9f9fb', height: '100%' }}>
              <div
                className="mx-auto rounded p-1"
                style={{
                  background: '#F3F4F7',
                  width: '100%',
                  maxWidth: '360px',
                  minHeight: '430px',
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                }}
              >
                <div>
                  {items.map((item, index) => {
                    if (item === 'Text Area' && savedTextMessage && isTextAreaEnabled) {
                      return (
                        <div
                          key={index}
                          className="rounded p-3 mb-2 text-white"
                          style={{
                            backgroundColor: '#5A49E8',
                            width: 'fit-content',
                            maxWidth: '85%',
                            fontSize: '14px',
                          }}
                        >
                          {savedTextMessage}
                        </div>
                      )
                    }

                    if (item === 'Department' && selectedDepartments.length > 0 && isDepartmentEnable) {
                      return (
                        <div key={index}>
                          <div className="fw-semibold mb-2" style={{ fontSize: '14px' }}>
                            Set your categories
                          </div>
                          <div className="mb-3 d-flex flex-column gap-2">
                            {selectedDepartments.map((dept, i) => (
                              <CButton key={i} color="primary" size="sm" className="w-50">
                                {dept}
                              </CButton>
                            ))}
                          </div>
                        </div>
                      )
                    }

                    return null
                  })}
                </div>

                <div className="input-group">
                  <input
                    type="text"
                    className="form-control border-0 shadow-none p-1"
                    placeholder="Write a message..."
                    style={{ fontSize: '14px' }}
                  />
                  <CButton color="primary" size="sm" className="input-group-text ms-2">
                    ➤
                  </CButton>
                </div>
              </div>
            </div>
          </CCol>
        </CRow>
      </CCardBody>

      <div className="d-grid gap-2 d-md-flex justify-content-md-end">
        <CButton
          className="me-md-2"
          style={{ backgroundColor: '#F3F4F7', border: '1px solid #79747E' }}
        >
          Cancel
        </CButton>
        <CButton style={{ backgroundColor: '#5856D6', color: '#FFFFFF' }} onClick={handleSaveTrigger}>
          Save
        </CButton>
      </div>
    </>
  )
}

export default TriggerAdd
