package com.VsmartEngine.Chatbot.Departments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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
import com.VsmartEngine.Chatbot.Admin.AdminRegisterRepository;
import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;


@CrossOrigin()
@RequestMapping("/chatbot")
@Controller
public class DepartmentController {
	
	@Autowired
	private DepartmentRepository departmentrepository;
	
	@Autowired
	private AdminRegisterRepository adminregisterrepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@PostMapping("/adddepartment")
	public ResponseEntity<?> addDepartment(@RequestBody DepartmentRequestDto departmentRequest,@RequestHeader("Authorization") String token) {
	    try {
	    	
	    	String roles =  jwtUtil.getRoleFromToken(token);

	        if (!"ADMIN".equals(roles)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Only admin can add departments\"}");
	        }
	    	 
	        // Check if adminIds are null or empty
	        if (departmentRequest.getAdminIds() == null || departmentRequest.getAdminIds().isEmpty()) {
	            return ResponseEntity.badRequest().body("Admin IDs must not be empty.");
	        }

	        Department department = new Department();
	        department.setDepName(departmentRequest.getDepName());
	        department.setDescription(departmentRequest.getDescription());

//	         Fetch admins by the provided admin IDs
	        List<AdminRegister> admins = adminregisterrepository.findAllById(departmentRequest.getAdminIds());
	        
	        // If no admins found, return a bad request response
	        if (admins.isEmpty()) {
	            return ResponseEntity.badRequest().body("No valid admins found for the provided IDs.");
	        }

	        department.setAdmins(admins);

	        // Save department
	        Department savedDepartment = departmentrepository.save(department);

	        return ResponseEntity.ok(savedDepartment);
	        
	    } catch (InvalidDataAccessApiUsageException e) {
	        // Handle specific exception and return a custom message
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error occurred while processing the department request: " + e.getMessage());
	    } catch (Exception e) {
	        // Catch any other unexpected exceptions
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("An unexpected error occurred: " + e.getMessage());
	    }
	}
	
	@GetMapping("/getAllDepartment")
	public ResponseEntity<List<DepartmentSummaryDto>> getAllDepartments() {
	    try {
	        List<Department> departments = departmentrepository.findAllByOrderByIdAsc();

	        if (departments.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        }

	        List<DepartmentSummaryDto> response = departments.stream()
	            .map(dep -> new DepartmentSummaryDto(
	            	dep.getId(),
	                dep.getDepName(),
	                dep.getDescription(),
	                dep.getAdmins() != null ? dep.getAdmins().size() : 0
	            ))
	            .collect(Collectors.toList());

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@DeleteMapping("/deleteDep/{id}")
	public ResponseEntity<String> delete(
	        @RequestHeader("Authorization") String token, 
	        @PathVariable Long id
	) {
	    try {
	        // Extract role from the token
	        String role = jwtUtil.getRoleFromToken(token);
	        System.out.println("role: " + role);

	        // Check if the role is "ADMIN"
	        if (!"ADMIN".equals(role)) {
	            // Return a Forbidden response if the role is not "ADMIN"
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                                 .body("{\"message\": \"Only admin can delete department\"}");
	        }

	        // Perform the delete operation if the role is "ADMIN"
	        departmentrepository.deleteById(id);

	        // Return a success message with 204 status
	        return ResponseEntity.status(HttpStatus.NO_CONTENT)
	                             .body("{\"message\": \"department deleted successfully\"}");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("{\"message\": \"An error occurred while deleting the department.\"}");
	    }
	}

	@GetMapping("/getdep/{id}")
	 public ResponseEntity<Department> getUserById(@PathVariable Long id) {
	        Optional<Department> depOptional = departmentrepository.findById(id);
	        if (depOptional.isPresent()) {
	        	Department user = depOptional.get();
	            return ResponseEntity.ok(user);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
	
	@PatchMapping("/updatedepartment/{id}")
	public ResponseEntity<?> updateDepartment(
	        @RequestHeader("Authorization") String token,
	        @PathVariable Long id,
	        @RequestBody DepartmentRequestDto departmentDTO
	) {
	    try {
	        String role = jwtUtil.getRoleFromToken(token);
	        if (!"ADMIN".equals(role)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body("{\"message\": \"Only admin can update department\"}");
	        }

	        Optional<Department> optionalDepartment = departmentrepository.findById(id);
	        if (optionalDepartment.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("{\"message\": \"Department not found\"}");
	        }

	        Department department = optionalDepartment.get();
	        department.setDepName(departmentDTO.getDepName());
	        department.setDescription(departmentDTO.getDescription());

	        // Fetch and set the admin list
	        List<AdminRegister> admins = adminregisterrepository.findAllById(departmentDTO.getAdminIds());
	        department.setAdmins(admins);

	        departmentrepository.save(department);

	        return ResponseEntity.ok().body("{\"message\": \"Department updated successfully\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("{\"message\": \"Error while updating department\"}");
	    }
	}

	
	
	

}
