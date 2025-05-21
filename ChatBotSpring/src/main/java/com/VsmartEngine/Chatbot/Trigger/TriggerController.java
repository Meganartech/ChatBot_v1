package com.VsmartEngine.Chatbot.Trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.VsmartEngine.Chatbot.Admin.AdminRegister;
import com.VsmartEngine.Chatbot.Departments.Department;
import com.VsmartEngine.Chatbot.Departments.DepartmentRepository;
import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;

import jakarta.transaction.Transactional;


@CrossOrigin()
@RequestMapping("/chatbot")
@Controller
public class TriggerController {
	
	@Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private TriggerTypeRepository triggerTypeRepository;

    @Autowired
    private SetDepartmentRepository departmentRepository;
    
    @Autowired
    private DepartmentRepository departmentrepository;
    
    @Autowired
	private JwtUtil jwtUtil;
    
    @PostMapping("/AddTrigger")
    public ResponseEntity<?> createTrigger(@RequestBody TriggerRequestDto request) {
        try {
            // Fetch TriggerType
            Triggertype triggerType = triggerTypeRepository.findById(request.getTriggerTypeId())
                    .orElseThrow(() -> new RuntimeException("Trigger type not found"));

            // Create new Trigger
            Trigger trigger = new Trigger();
            trigger.setName(request.getName());
            trigger.setDelay(request.getDelay());
            trigger.setTriggerType(triggerType);
            trigger.setFirstTrigger(request.getFirstTrigger());

            // Only create TextOption if text is provided
            if (request.getText() != null && !request.getText().isBlank()) {
                TextOption textOption = new TextOption();
                textOption.setText(request.getText());
                textOption.setTrigger(trigger);
                trigger.setTextOption(textOption);
            }

            // Only create SetDepartments if departmentIds are provided
            if (request.getDepartmentIds() != null && !request.getDepartmentIds().isEmpty()) {
                List<SetDepartment> setDepartments = new ArrayList<>();
                for (Long deptId : request.getDepartmentIds()) {
                    Department dept = departmentrepository.findById(deptId)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + deptId));

                    SetDepartment setDept = new SetDepartment();
                    setDept.setName(dept.getDepName());
                    setDept.setTrigger(trigger);

                    setDepartments.add(setDept);
                }
                trigger.setDepartments(setDepartments);
            }
            
            

            // Save and return
            Trigger savedTrigger = triggerRepository.save(trigger);
            return ResponseEntity.ok(savedTrigger);

        } catch (Exception e) {
            // Log the error (optional)
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating trigger: " + e.getMessage());
        }
    }
    
    
    
    @GetMapping("/getTriggerType")
    public ResponseEntity<List<Triggertype>> getTriggertype(){
    	try {
    		List<Triggertype> gettriggertype = triggerTypeRepository.findAll();

            // Check if the list is empty
            if (gettriggertype.isEmpty()) {
                return new ResponseEntity(HttpStatus.NO_CONTENT); // Return 204 No Content
            }
            // Return the list of users with 200 OK
            return new ResponseEntity<>(gettriggertype, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   
    @GetMapping("/getAllTrigger")
    public ResponseEntity<List<Trigger>> getAllTrigger() {
    	try {
            // Fetch all users from the repository
            List<Trigger> gettrigger = triggerRepository.findAllByOrderByTriggeridAsc();
            
            // Check if the list is empty
            if (gettrigger.isEmpty()) {
                return new ResponseEntity(HttpStatus.NO_CONTENT); // Return 204 No Content
            }
            // Return the list of users with 200 OK
            return new ResponseEntity<>(gettrigger, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return 500 Internal Server Error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/gettrigger/{id}")
	 public ResponseEntity<Trigger> geTriggerById(@PathVariable Long id) {
	        Optional<Trigger> triggerOptional = triggerRepository.findById(id);
	        if (triggerOptional.isPresent()) {
	        	Trigger trigger = triggerOptional.get();
	            return ResponseEntity.ok(trigger);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
    
    
    @GetMapping("/getTrigger")
    public ResponseEntity<Trigger> getOneTriggerByType() {
        try {
            Optional<Trigger> trigger = triggerRepository.findByTriggerType_TriggerType("Basic");

            if (trigger.isPresent()) {
                return new ResponseEntity<>(trigger.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    
    @DeleteMapping("/deleteTrigger/{id}")
    public ResponseEntity<String> deleteTrigger(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        try {
            String role = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("{\"message\": \"Only admin can delete department\"}");
            }

            Optional<Trigger> triggerOpt = triggerRepository.findById(id);
            if (triggerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("{\"message\": \"Trigger not found\"}");
            }

            triggerRepository.delete(triggerOpt.get());

            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                 .body("{\"message\": \"Trigger and its related data deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"message\": \"An error occurred while deleting the trigger.\"}");
        }
    }

    
    @PatchMapping("/UpdateTrigger/{id}")
    public ResponseEntity<?> updateTrigger(@PathVariable Long id, @RequestBody TriggerRequestDto request) {
        try {
            // Find existing trigger
            Trigger trigger = triggerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Trigger not found with ID: " + id));

            // Update trigger details
            trigger.setName(request.getName());
            trigger.setDelay(request.getDelay());

            // Update trigger type
            Triggertype triggerType = triggerTypeRepository.findById(request.getTriggerTypeId())
                    .orElseThrow(() -> new RuntimeException("Trigger type not found"));
            trigger.setTriggerType(triggerType);

            trigger.setFirstTrigger(request.getFirstTrigger());

            // Update text option
            if (request.getText() != null && !request.getText().isBlank()) {
                if (trigger.getTextOption() != null) {
                    trigger.getTextOption().setText(request.getText());
                } else {
                    TextOption textOption = new TextOption();
                    textOption.setText(request.getText());
                    textOption.setTrigger(trigger);
                    trigger.setTextOption(textOption);
                }
            } else {
                // Optionally remove text option if no text provided
                trigger.setTextOption(null);
            }

         // Update departments properly:
            if (request.getDepartmentIds() != null) {
                // Clear existing departments instead of replacing the collection
                trigger.getDepartments().clear();

                for (Long deptId : request.getDepartmentIds()) {
                    Department dept = departmentrepository.findById(deptId)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + deptId));

                    SetDepartment setDept = new SetDepartment();
                    setDept.setName(dept.getDepName());
                    setDept.setTrigger(trigger);

                    trigger.getDepartments().add(setDept);
                }
            } else {
                // Clear all departments if none provided
                trigger.getDepartments().clear();
            }


            // Save updates
            Trigger updatedTrigger = triggerRepository.save(trigger);
            return ResponseEntity.ok(updatedTrigger);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating trigger: " + e.getMessage());
        }
    }


    

}


