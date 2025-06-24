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
//@CrossOrigin(origins = "http://127.0.0.1:5500")
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
	public ResponseEntity<?> addDepartment(@RequestBody DepartmentRequestDto departmentRequest,
	                                       @RequestHeader("Authorization") String token) {
	    try {
	        String role = jwtUtil.getRoleFromToken(token);
	        if (!"ADMIN".equals(role)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body("{\"message\": \"Only admin can add departments\"}");
	        }

	        if (departmentRequest.getAdminIds() == null || departmentRequest.getAdminIds().isEmpty()) {
	            return ResponseEntity.badRequest().body("Admin IDs must not be empty.");
	        }

	        List<AdminRegister> admins = adminregisterrepository.findAllById(departmentRequest.getAdminIds());
	        if (admins.isEmpty()) {
	            return ResponseEntity.badRequest().body("No valid admins found for the provided IDs.");
	        }

	        // Check if any admin is already in another department
	        for (AdminRegister admin : admins) {
	            if (admin.getDepartment() != null) {
	                return ResponseEntity.status(HttpStatus.CONFLICT)
	                        .body("Admin '" + admin.getEmail() + "' is already assigned to a department.");
	            }
	        }

	        Department department = new Department();
	        department.setDepName(departmentRequest.getDepName());
	        department.setDescription(departmentRequest.getDescription());

	        for (AdminRegister admin : admins) {
	            admin.setDepartment(department); // Assign new department
	        }

	        department.setAdmins(admins); // set back-reference

	        Department saved = departmentrepository.save(department); // saves department + admins

	        return ResponseEntity.ok(saved);

	    } catch (Exception e) {
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
	        String role = jwtUtil.getRoleFromToken(token);
	        if (!"ADMIN".equals(role)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body("{\"message\": \"Only admin can delete department\"}");
	        }

	        Optional<Department> optionalDepartment = departmentrepository.findById(id);
	        if (optionalDepartment.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("{\"message\": \"Department not found\"}");
	        }

	        Department department = optionalDepartment.get();

	        // Disassociate all linked admins
	        List<AdminRegister> admins = department.getAdmins();
	        for (AdminRegister admin : admins) {
	            admin.setDepartment(null); // Break FK relation
	        }

	        adminregisterrepository.saveAll(admins); // Persist changes

	        departmentrepository.deleteById(id); // Safe delete

	        return ResponseEntity.status(HttpStatus.NO_CONTENT)
	                .body("{\"message\": \"Department deleted successfully\"}");

	    } catch (Exception e) {
	        e.printStackTrace();
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

	     // Get existing department
	        Department department = optionalDepartment.get();
	        department.setDepName(departmentDTO.getDepName());
	        department.setDescription(departmentDTO.getDescription());

	        // Unassign old admins
	        List<AdminRegister> oldAdmins = department.getAdmins();
	        for (AdminRegister oldAdmin : oldAdmins) {
	            oldAdmin.setDepartment(null);
	        }
	        adminregisterrepository.saveAll(oldAdmins); // persist the change
	        
	        
	        System.out.printf("adminId",departmentDTO.getAdminIds());

	        // Get new admins by ID
	        List<AdminRegister> newAdmins = adminregisterrepository.findAllById(departmentDTO.getAdminIds());
	        if (newAdmins.isEmpty()) {
	            return ResponseEntity.badRequest().body("No valid admins found for the provided IDs.");
	        }

	        // Check if any new admin is already in another department
	        for (AdminRegister admin : newAdmins) {
	            Department currentDep = admin.getDepartment();
	            if (currentDep != null && currentDep.getId() != department.getId()) {
	                return ResponseEntity.status(HttpStatus.CONFLICT)
	                        .body("Admin '" + admin.getEmail() + "' is already assigned to another department.");
	            }
	        }

	        
	        // Assign new admins
	        for (AdminRegister admin : newAdmins) {
	            admin.setDepartment(department);
	        }
	        adminregisterrepository.saveAll(newAdmins); // persist the change

	        // Set new admins to department
	        department.setAdmins(newAdmins);
	        departmentrepository.save(department);

	        return ResponseEntity.ok().body("{\"message\": \"Department updated successfully\"}");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("{\"message\": \"Error while updating department\"}");
	    }
	}
	
}
