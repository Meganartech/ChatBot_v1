package com.VsmartEngine.Chatbot.Overview;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;

@Controller
public class OverviewController {

	@Autowired
	private OverviewRepository overviewrepository;
	
	@Autowired
    private JwtUtil jwtUtil;
	
	
	public ResponseEntity<?> AddProperty(String propertyname,             Boolean active,
			 String propertyurl,
			 MultipartFile propertyimage,
			 String token){
		try {
			String role = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can set appearance.");
            }
            
            Overview overview = new Overview();
            overview.setActive(active);
            overview.setPropertyimage(propertyimage.getBytes());
            overview.setPropertyname(propertyname);
            overview.setPropertyurl(propertyurl);
            
            Overview saved = overviewrepository.save(overview);
            
            return ResponseEntity.ok(saved);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An unexpected error occurred: " + e.getMessage());
	    }
    }
	
	
	
	public ResponseEntity<?> getPropertyById(Long id) {
	    try {
	        Optional<Overview> propertyOptional = overviewrepository.findById(id);
	        if (propertyOptional.isPresent()) {
	            Overview property = propertyOptional.get();
	            return ResponseEntity.ok(property);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property not found with ID: " + id);
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An unexpected error occurred: " + e.getMessage());
	    }
	}

	
	@PatchMapping("/UpdateOverview/{id}")
	public ResponseEntity<?> updateProperty(
	        @PathVariable Long id,
	        @RequestParam(value = "propertyname", required = false) String propertyname,
	        @RequestParam(value = "active", required = false) Boolean active,
	        @RequestParam(value = "propertyurl", required = false) String propertyurl,
	        @RequestParam(value = "propertyimage", required = false) MultipartFile propertyimage,
	        @RequestHeader("Authorization") String token) {

	    try {
	        String role = jwtUtil.getRoleFromToken(token);
	        if (!"ADMIN".equals(role)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can update appearance.");
	        }

	        Optional<Overview> optionalOverview = overviewrepository.findById(id);
	        if (optionalOverview.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Overview not found.");
	        }

	        Overview overview = optionalOverview.get();

	        if (propertyname != null) overview.setPropertyname(propertyname);
	        if (active != null) overview.setActive(active);
	        if (propertyurl != null) overview.setPropertyurl(propertyurl);
	        if (propertyimage != null && !propertyimage.isEmpty()) {
	            overview.setPropertyimage(propertyimage.getBytes());
	        }

	        Overview updated = overviewrepository.save(overview);
	        return ResponseEntity.ok(updated);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An unexpected error occurred: " + e.getMessage());
	    }
	}

	
	
	
	
}


