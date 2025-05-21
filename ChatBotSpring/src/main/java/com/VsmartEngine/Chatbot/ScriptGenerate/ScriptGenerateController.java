package com.VsmartEngine.Chatbot.ScriptGenerate;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.VsmartEngine.Chatbot.TokenGeneration.JwtUtil;
import com.VsmartEngine.Chatbot.Trigger.SetDepartment;
import com.VsmartEngine.Chatbot.Trigger.Trigger;
import com.VsmartEngine.Chatbot.Trigger.TriggerRepository;
import com.VsmartEngine.Chatbot.UserInfo.UserInfo;
import com.VsmartEngine.Chatbot.UserInfo.UserInfoRepository;

import jakarta.transaction.Transactional;

@CrossOrigin
@RequestMapping("/chatbot")
@RestController
public class ScriptGenerateController {

    @Autowired
    private ScriptGeneratorRepository scriptgeneraterepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired 
    private UserInfoRepository userinforepository;
    
    @Autowired
    private TriggerRepository triggerRepository;
    
    
    @PostMapping("/widget/appearance")
    public ResponseEntity<?> saveWidgetAppearance(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "Language", required = false) String language,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "heading", required = false) String heading,
            @RequestParam(value = "TextArea", required = false) String textArea,
            @RequestParam(value = "logoAlign", required = false) String logoAlign,
            @RequestParam(value = "headingAlign", required = false) String headingAlign,
            @RequestParam(value = "TextAlign", required = false) String textAlign,
            @RequestParam(value = "appearence", required = false) List<String> appearence 
    ) {
        try {
            String role = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can set appearance.");
            }

            ScriptGenerate script = new ScriptGenerate();
            script.setLanguage(language);
            script.setHeading(heading);
            script.setTextArea(textArea);
            script.setLogoAlign(logoAlign);
            script.setHeadingAlign(headingAlign);
            script.setTextAlign(textAlign);
            script.setAppearence(appearence);

            if (logo != null && !logo.isEmpty()) {
                script.setLogo(logo.getBytes());
            }

            ScriptGenerate saved = scriptgeneraterepository.save(script);
            return ResponseEntity.ok(saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving appearance: " + e.getMessage());
        }
    }


        
     // Generate script preview without saving anything
        @PostMapping("/property/generate")
        public ResponseEntity<?> generatePropertyScript(
                @RequestHeader("Authorization") String token,
                @RequestParam(value = "scriptId", required = false) UUID scriptId,
                @RequestParam("propertyName") String propertyName,
                @RequestParam("websiteURL") String websiteURL,
                @RequestParam("buttonColor") String buttonColor) {
            try {
                // Authorization check (optional here, or allow any logged user)
                String role = jwtUtil.getRoleFromToken(token);
                if (!"ADMIN".equals(role)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can generate widget.");
                }

                if (scriptId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("scriptId is required. Please create appearance first.");
                }

                Optional<ScriptGenerate> optionalScript = scriptgeneraterepository.findById(scriptId);
                if (optionalScript.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid scriptId.");
                }

                ScriptGenerate script = optionalScript.get();

                // Check if appearance is set (same as your logic)
                boolean isAppearanceSet = (script.getHeading() != null && !script.getHeading().isBlank()) ||
                                          (script.getTextArea() != null && !script.getTextArea().isBlank()) ||
                                          (script.getLogo() != null && script.getLogo().length > 0);

                if (!isAppearanceSet) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Appearance not set. Please set appearance before generating script.");
                }

                // Just generate the script dynamically without saving
                String widgetScript = generateWidgetScript(scriptId);

                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(widgetScript);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error generating widget script: " + e.getMessage());
            }
        }
        
        // Save property info and return saved widget script
        @PostMapping("/property/save")
        public ResponseEntity<?> savePropertyInfo(
                @RequestHeader("Authorization") String token,
                @RequestParam(value = "scriptId", required = false) UUID scriptId,
                @RequestParam("propertyName") String propertyName,
                @RequestParam("websiteURL") String websiteURL,
                @RequestParam("buttonColor") String buttonColor,
                @RequestParam("widgetScript") String widgetScript) {
            try {
                String role = jwtUtil.getRoleFromToken(token);
                if (!"ADMIN".equals(role)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can update property info.");
                }

                if (scriptId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("scriptId is required. Please create appearance first.");
                }

                Optional<ScriptGenerate> optionalScript = scriptgeneraterepository.findById(scriptId);
                if (optionalScript.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid scriptId.");
                }

                ScriptGenerate script = optionalScript.get();

                boolean isAppearanceSet = (script.getHeading() != null && !script.getHeading().isBlank()) ||
                                          (script.getTextArea() != null && !script.getTextArea().isBlank()) ||
                                          (script.getLogo() != null && script.getLogo().length > 0);

                if (!isAppearanceSet) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Appearance not set. Please set appearance before saving.");
                }

                // Save the property info
                script.setPropertyName(propertyName);
                script.setWebsiteURL(websiteURL);
                script.setButtonColor(buttonColor);
                script.setWidgetScript(widgetScript);

                scriptgeneraterepository.save(script);

                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(widgetScript);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving property info: " + e.getMessage());
            }
        }
       
        @GetMapping("/GetAppearance")
        @Transactional
        public ResponseEntity<AppearanceDto> getWidgetAppearance() {
            try {
                Optional<ScriptGenerate> scriptgenerateOpt = scriptgeneraterepository.findFirstByOrderByIdAsc();

                if (scriptgenerateOpt.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                ScriptGenerate script = scriptgenerateOpt.get();
                AppearanceDto dto = new AppearanceDto();
                dto.setId(script.getId());
                dto.setPropertyName(script.getPropertyName());
                dto.setWebsiteURL(script.getWebsiteURL());
                dto.setWidgetScript(script.getWidgetScript());
                dto.setButtonColor(script.getButtonColor());
                dto.setLanguage(script.getLanguage());   
                dto.setHeading(script.getHeading());
                dto.setTextArea(script.getTextArea());
                dto.setLogoAlign(script.getLogoAlign());
                dto.setHeadingAlign(script.getHeadingAlign());
                dto.setTextAlign(script.getTextAlign());
                dto.setAppearence(script.getAppearence());
                // Convert byte[] logo to Base64 string
                if (script.getLogo() != null && script.getLogo().length > 0) {
                    String base64Logo = Base64.getEncoder().encodeToString(script.getLogo());
                    dto.setLogoBase64(base64Logo);
                } else {
                    dto.setLogoBase64(null);
                }
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();  // Log the error
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
      
        
        @PatchMapping("/widget/appearance/{id}")
        public ResponseEntity<?> updateWidgetAppearance(
                @RequestHeader("Authorization") String token,
                @PathVariable UUID id,
                @RequestParam(value = "Language", required = false) String language,
                @RequestParam(value = "logo", required = false) MultipartFile logo,
                @RequestParam(value = "heading", required = false) String heading,
                @RequestParam(value = "TextArea", required = false) String textArea,
                @RequestParam(value = "logoAlign", required = false) String logoAlign,
                @RequestParam(value = "headingAlign", required = false) String headingAlign,
                @RequestParam(value = "TextAlign", required = false) String TextAlign,
                @RequestParam(value = "appearence", required = false) List<String> appearence ) {
            try {
                String role = jwtUtil.getRoleFromToken(token);
                if (!"ADMIN".equalsIgnoreCase(role)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only admin can update appearance.");
                }

                Optional<ScriptGenerate> optionalScript = scriptgeneraterepository.findById(id);
                if (optionalScript.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Script not found.");
                }

                ScriptGenerate script = optionalScript.get();

                if (language != null) script.setLanguage(language);
                if (heading != null) script.setHeading(heading);
                if (textArea != null) script.setTextArea(textArea);
                if (logoAlign != null) script.setLogoAlign(logoAlign);
                if (headingAlign != null) script.setHeadingAlign(headingAlign);
                if (TextAlign != null) script.setTextAlign(TextAlign);
                if (appearence != null) script.setAppearence(appearence);
                
                if (logo != null && !logo.isEmpty()) {
                    script.setLogo(logo.getBytes());
                }

                scriptgeneraterepository.save(script);
                return ResponseEntity.ok("Appearance updated successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating appearance: " + e.getMessage());
            }
        }

        private String generateWidgetScript(UUID id) {
            return "<script async defer src='http://localhost:8080/chatbot/widget/" + id + "'></script>";
        }  
        
        @GetMapping(value = "/widget/{id}", produces = "application/javascript")
        public ResponseEntity<String> serveWidgetScript(@PathVariable UUID id) {
            ScriptGenerate script = scriptgeneraterepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Widget not found"));

            String logoImg = (script.getLogo() != null)
                    ? "<div style='text-align:" + script.getLogoAlign() + ";'><img src='data:image/png;base64," 
                        + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'></div>"
                    : "";


            // Dynamic header content based on appearance order
            List<String> appearanceOrder = script.getAppearence(); 
            StringBuilder headerContent = new StringBuilder();

            for (String item : appearanceOrder) {
                switch (item) {
                    case "Logo" -> headerContent.append(logoImg);
                    case "Heading" -> headerContent.append("<div class='chatbot-heading' style='text-align:")
                            .append(script.getHeadingAlign()).append(";'>")
                            .append(script.getHeading()).append("</div>");
                    case "TextArea" -> headerContent.append("<div class='chatbot-text' style='text-align:")
                            .append(script.getTextAlign()).append(";'>")
                            .append(script.getTextArea()).append("</div>");
                }
            }

            String widgetHtml = """
                <style>
                    #chatbot-launcher {
                        position: fixed;
                        bottom: 20px;
                        right: 20px;
                        width: 60px;
                        height: 60px;
                        background-color: %s;
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        cursor: pointer;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                        z-index: 9999;
                    }
                    #chatbot-launcher img {
                        width: 30px;
                        height: 30px;
                    }
                    #chatbot-panel {
                        position: fixed;
                        bottom: 90px;
                        right: 20px;
                        width: 350px;
                        background: white;
                        border-radius: 10px;
                        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
                        display: none;
                        z-index: 9999;
                        font-family: Arial, sans-serif;
                        overflow: hidden;
                    }
                    .chatbot-header {
                        background-color: %s;
                        padding: 10px;
                        color: white;
                        text-align: center;
                    }
                    .chatbot-logo {
                        max-width: 60px;
                        max-height: 60px;
                        margin-bottom: 5px;     
                        display: inline-block;
                    }
                    .chatbot-heading {
                        font-size: 20px;
                    }
                    .chatbot-text {
                        font-size: 14px;
                        margin-bottom: 0;
                    }
                    #chatbot-body {
                        display: flex;
                        flex-direction: column;
                        height: 300px;
                        padding: 5px;
                        color: #333;
                        text-align: left;
                    }
                    #chatbot-messages {
                        flex: 1;
                        overflow-y: auto;
                        padding: 3px;
                        margin-bottom: 2px;
                        width: fix-content;
                        max-width: 85%%;
                        font-size: 14px;           
                    }
                    .chatbot-form-title {
                        font-size: 18px;
                        font-weight: bold;
                        margin-bottom: 15px;
                    }
                    .chatbot-form {
                        display: flex;
                        flex-direction: column;
                        gap: 30px;
                        align-items: center;
                    }
                    .chatbot-form input {
                        padding: 10px;
                        width: 90%%;
                        border: 1px solid #ccc;
                        border-radius: 5px;
                        font-size: 14px;
                    }
                    .chatbot-form button {
                        background-color: %s;
                        color: white;
                        padding: 10px;
                        width: 90%%;
                        border: none;
                        border-radius: 5px;
                        cursor: pointer;
                        font-size: 14px;
                    }
                    .chatbot-input-container {
                        display: flex;
                        gap: 10px;
                    }
                    #chatbot-user-input {
                        flex: 1;
                        padding: 10px;
                        border: 1px solid #ccc;
                        border-radius: 5px;
                    }
                    #chatbot-send-button {
                        background-color: %s;
                        color: white;
                        border: none;
                        padding: 10px 15px;
                        border-radius: 5px;
                        cursor: pointer;
                    }
                </style>
                <div id="chatbot-launcher">
                    <img src="https://cdn-icons-png.flaticon.com/512/4712/4712027.png" alt="Chatbot">
                </div>
                <div id="chatbot-panel">
                    <div class="chatbot-header">%s</div>
                    <div id="chatbot-body">
                        <div class="chatbot-form-title">Login</div>
                        <form class="chatbot-form">
                            <input type="text" name="username" placeholder="Username" required>
                            <input type="email" name="email" placeholder="Email" required>
                            <button type="submit">➤ Start Chat</button>
                        </form>
                    </div>
                </div>
                """.formatted(
                    script.getButtonColor(),
                    script.getButtonColor(),
                    script.getButtonColor(),
                    script.getButtonColor(),
                    headerContent.toString()
            );

            // Escape JavaScript-unsafe characters
            String escapedHtml = widgetHtml
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("`", "\\`")
                    .replace("\n", "")
                    .replace("\r", "");

            // JS wrapper to inject HTML + handle events
            String js = """
                (function() {
                    var wrapper = document.createElement('div');
                    wrapper.innerHTML = `%s`;
                    document.body.appendChild(wrapper);

                    var launcher = document.getElementById('chatbot-launcher');
                    var panel = document.getElementById('chatbot-panel');

                    if (launcher && panel) {
                        launcher.addEventListener('click', function () {
                            panel.style.display = (panel.style.display === 'none') ? 'block' : 'none';
                        });
                    }

                    var form = document.querySelector('.chatbot-form');
                    if (form) {
                        form.addEventListener('submit', function(event) {
                            event.preventDefault();
                            var username = form.querySelector('input[name="username"]').value;
                            var email = form.querySelector('input[name="email"]').value;

                            var params = new URLSearchParams();
                            params.append('username', username);
                            params.append('email', email);

                            fetch('http://localhost:8080/chatbot/widget/chat/%s', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/x-www-form-urlencoded'
                                },
                                body: params.toString()
                            })
                            .then(response => response.text())
                            .then(jsCode => {
                                eval(jsCode);
                            })
                            .catch(err => {
                                console.error('Login failed:', err);
                            });
                        });
                    }
                })();
                """.formatted(escapedHtml, id.toString());

            return ResponseEntity.ok(js);
        }
        
        
        @PostMapping(value = "/widget/chat/{id}", produces = "application/javascript")
        public ResponseEntity<String> handleUserSubmit(
                @PathVariable UUID id,
                @RequestParam String username,
                @RequestParam String email) {

            // Get or create user
            String usernameToUse;
            Optional<UserInfo> optUser = userinforepository.findByEmail(email);
            if (optUser.isPresent()) {
                usernameToUse = optUser.get().getUsername();
            } else {
                UserInfo user = new UserInfo();
                user.setUsername(username);
                user.setEmail(email);
                userinforepository.save(user);
                usernameToUse = username;
            }

            // Fetch widget script
            ScriptGenerate script = scriptgeneraterepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Widget not found"));

            // Load trigger
            Optional<Trigger> optionalTrigger = triggerRepository.findByTriggerType_TriggerType("Basic");
            if (optionalTrigger.isEmpty()) {
                return ResponseEntity.ok("console.error('No trigger found.');");
            }
            Trigger trigger = optionalTrigger.get();

            // Build header using appearance order
            List<String> appearanceOrder = script.getAppearence();
            StringBuilder headerContent = new StringBuilder();

            String logoImg = (script.getLogo() != null)
                    ? "<div style='text-align:" + script.getLogoAlign() + ";'><img src='data:image/png;base64,"
                        + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'></div>"
                    : "";

            for (String item : appearanceOrder) {
                switch (item) {
                    case "Logo" -> headerContent.append(logoImg);
                    case "Heading" -> headerContent.append("<div class='chatbot-heading' style='text-align:")
                            .append(script.getHeadingAlign()).append(";'>")
                            .append(script.getHeading()).append("</div>");
                    case "TextArea" -> headerContent.append("<div class='chatbot-text' style='text-align:")
                            .append(script.getTextAlign()).append(";'>")
                            .append(script.getTextArea()).append("</div>");
                }
            }

            // Compose welcomeHtml with header + messages + input + send button
            String welcomeHtml = String.format("""
                <div class="chatbot-header" style="background-color: %s; color: white; padding: 10px;">
                    %s
                </div>
                <div id="chatbot-body" style="padding: 10px;">
                    <div id="chatbot-messages" style="overflow-y:auto; height:300px; margin-bottom:10px;"></div>
                    <div class="chatbot-input-container" style="display:flex;">
                        <input type="text" id="chatbot-user-input" placeholder="Type your message..." style="flex-grow:1; padding:8px; font-size:14px;" />
                        <button id="chatbot-send-button" style="padding: 8px 12px; font-size:16px; cursor:pointer;">➤</button>
                    </div>
                </div>
                """, script.getButtonColor(), headerContent.toString());
         // Build timed messages JS
            StringBuilder timeouts = new StringBuilder();

         // Start from base delay (e.g., from trigger delay)
            int baseDelay = trigger.getDelay() * 1000;

            // Show "Set your categories" once
            timeouts.append("setTimeout(function() {\n")
                    .append("  var msgContainer = document.getElementById('chatbot-messages');\n")
                    .append("  if (msgContainer) {\n")
                    .append("    var p = document.createElement('p');\n")
                    .append("    p.textContent = 'Set your categories';\n")
                    .append("    p.style.fontWeight = 'bold';\n")
                    .append("    msgContainer.appendChild(p);\n")
                    .append("  }\n")
                    .append("}, ").append(baseDelay).append(");\n");

            // Delay 2 seconds for next messages
            baseDelay += 2000;

            // Display each department one by one with blue background and 2 seconds gap
            if (trigger.getFirstTrigger().contains("Department")) {
                for (SetDepartment dept : trigger.getDepartments()) {
                    String deptNameEscaped = dept.getName().replace("\"", "\\\"");
                    timeouts.append("setTimeout(function() {\n")
                            .append("  var msgContainer = document.getElementById('chatbot-messages');\n")
                            .append("  if (msgContainer) {\n")
                            .append("    var p = document.createElement('p');\n")
                            .append("    p.textContent = \"").append(deptNameEscaped).append("\";\n")
                            .append("    p.style.backgroundColor = '#0000FF';\n")  // Blue background
                            .append("    p.style.color = 'white';\n")               // White text
                            .append("    p.style.padding = '6px 10px';\n")
                            .append("    p.style.borderRadius = '5px';\n")
                            .append("    p.style.display = 'inline-block';\n")
                            .append("    p.style.marginBottom = '6px';\n")
                            .append("    msgContainer.appendChild(p);\n")
                            .append("  }\n")
                            .append("}, ").append(baseDelay).append(");\n");

                    // Increase delay after each department message
                    baseDelay += 2000;
                }
            }

            // After all departments, display TextArea message if exists, with current baseDelay
            if (trigger.getFirstTrigger().contains("Text Area") && trigger.getTextOption() != null) {
                String textMessage = trigger.getTextOption().getText().replace("\"", "\\\"");
                timeouts.append("setTimeout(function() {\n")
                        .append("  var msgContainer = document.getElementById('chatbot-messages');\n")
                        .append("  if (msgContainer) {\n")
                        .append("    var textDiv = document.createElement('div');\n")
                        .append("    textDiv.textContent = \"").append(textMessage).append("\";\n")
                        .append("    textDiv.style.backgroundColor = '#5A49E8';\n")
                        .append("    textDiv.style.color = 'white';\n")
                        .append("    textDiv.style.width = 'fit-content';\n")
                        .append("    textDiv.style.maxWidth = '85%';\n")
                        .append("    textDiv.style.fontSize = '14px';\n")
                        .append("    textDiv.style.padding = '10px';\n")
                        .append("    textDiv.style.borderRadius = '5px';\n")
                        .append("    msgContainer.appendChild(textDiv);\n")
                        .append("  }\n")
                        .append("}, ").append(baseDelay).append(");\n");
            }

            // Final JS with welcomeHtml + timed messages + send button event
            String finalJs = String.format("""
                (function() {
                    var panel = document.getElementById('chatbot-panel');
                    if (panel) {
                        panel.innerHTML = `%s`;
                    }

                    %s

                    var sendBtn = document.getElementById('chatbot-send-button');
                    if (sendBtn) {
                        sendBtn.addEventListener('click', function() {
                            var input = document.getElementById('chatbot-user-input');
                            var text = input.value.trim();
                            if (text) {
                                var msgContainer = document.getElementById('chatbot-messages');
                                var userMsg = document.createElement('p');
                                userMsg.textContent = 'You: ' + text;
                                userMsg.style.fontWeight = 'bold';
                                msgContainer.appendChild(userMsg);
                                input.value = '';
                                msgContainer.scrollTop = msgContainer.scrollHeight;
                            }
                        });
                    }
                })();
                """, 
                welcomeHtml.replace("`", "\\`").replace("\n", "").replace("\r", ""),
                timeouts.toString());

            return ResponseEntity.ok(finalJs);
        }


        
        
//        @PostMapping(value = "/widget/chat/{id}", produces = "application/javascript")
//        public ResponseEntity<String> handleUserSubmit(
//                @PathVariable UUID id,
//                @RequestParam String username,
//                @RequestParam String email) {
//
//            // Get or create user
//            String usernameToUse;
//            Optional<UserInfo> optUser = userinforepository.findByEmail(email);
//            if (optUser.isPresent()) {
//                usernameToUse = optUser.get().getUsername();
//            } else {
//                UserInfo user = new UserInfo();
//                user.setUsername(username);
//                user.setEmail(email);
//                userinforepository.save(user);
//                usernameToUse = username;
//            }
//
//            // Fetch widget script
//            ScriptGenerate script = scriptgeneraterepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Widget not found"));
//
//            // Build header using appearance order
//            List<String> appearanceOrder = script.getAppearence();
//            StringBuilder headerContent = new StringBuilder();
//
//            String logoImg = (script.getLogo() != null)
//                    ? "<div style='text-align:" + script.getLogoAlign() + ";'><img src='data:image/png;base64,"
//                        + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'></div>"
//                    : "";
//
//            for (String item : appearanceOrder) {
//                switch (item) {
//                    case "Logo" -> headerContent.append(logoImg);
//                    case "Heading" -> headerContent.append("<div class='chatbot-heading' style='text-align:")
//                            .append(script.getHeadingAlign()).append(";'>")
//                            .append(script.getHeading()).append("</div>");
//                    case "TextArea" -> headerContent.append("<div class='chatbot-text' style='text-align:")
//                            .append(script.getTextAlign()).append(";'>")
//                            .append(script.getTextArea()).append("</div>");
//                }
//            }
//
//            // Load trigger
//            Optional<Trigger> optionalTrigger = triggerRepository.findByTriggerType_TriggerType("Basic");
//            if (optionalTrigger.isEmpty()) {
//                return ResponseEntity.ok("console.error('No trigger found.');");
//            }
//
//            Trigger trigger = optionalTrigger.get();
//
//            // Dynamically build JS timeout messages
//            StringBuilder timeouts = new StringBuilder();
//            int baseDelay = trigger.getDelay();
//
//            for (String item : trigger.getFirstTrigger()) {
//                int delayMs = baseDelay * 1000;
//
//                if (item.equals("Department")) {
//                    for (SetDepartment dept : trigger.getDepartments()) {
//                        String deptMessage = "Set your categories" + dept.getName();
//                        timeouts.append("setTimeout(function() {\n")
//                                .append("var msgContainer = document.getElementById('chatbot-messages');\n")
//                                .append("if (msgContainer) {\n")
//                                .append("var p = document.createElement('p');\n")
//                                .append("p.textContent = \"").append(deptMessage.replace("\"", "\\\"")).append("\";\n")
//                                .append("msgContainer.appendChild(p);\n")
//                                .append("}\n")
//                                .append("}, ").append(delayMs).append(");\n");
//                        baseDelay += 2; // Add delay for next message
//                    }
//                } else if (item.equals("Text Area")) {
//                    String textMessage = trigger.getTextOption().getText();
//                    timeouts.append("setTimeout(function() {\n")
//                            .append("var msgContainer = document.getElementById('chatbot-messages');\n")
//                            .append("if (msgContainer) {\n")
//                            .append("var p = document.createElement('p');\n")
//                            .append("p.textContent = \"").append(textMessage.replace("\"", "\\\"")).append("\";\n")
//                            .append("msgContainer.appendChild(p);\n")
//                            .append("}\n")
//                            .append("}, ").append(delayMs).append(");\n");
//                    baseDelay += 2;
//                }
//            }
//
//            // Final HTML content for the chatbot panel
//            String welcomeHtml = String.format("""
//                <div class="chatbot-header" style="background-color: %s; color: white;">
//                    %s
//                </div>
//                <div id="chatbot-body">
//                    <div id="chatbot-messages"></div>
//                    <div class="chatbot-input-container">
//                        <input type="text" id="chatbot-user-input" placeholder="Type your message..." />
//                        <button id="chatbot-send-button">➤</button>
//                    </div>
//                </div>
//                """, script.getButtonColor(), headerContent.toString());
//
//            // Combine JS response
//            String jsUpdate = String.format("""
//                (function() {
//                    var panel = document.getElementById('chatbot-panel');
//                    if (panel) {
//                        panel.innerHTML = `%s`;
//                    }
//
//                    %s
//
//                    document.getElementById('chatbot-send-button').addEventListener('click', function() {
//                        const input = document.getElementById('chatbot-user-input');
//                        const text = input.value.trim();
//                        if (text) {
//                            const msg = document.createElement('p');
//                            msg.textContent = 'You: ' + text;
//                            document.getElementById('chatbot-messages').appendChild(msg);
//                            input.value = '';
//                        }
//                    });
//                })();
//                """,
//                welcomeHtml.replace("`", "\\`").replace("\n", "").replace("\r", ""),
//                timeouts.toString()
//            );
//
//            return ResponseEntity.ok(jsUpdate);
//        }
        
}
        
        
       

        
//        @PostMapping(value = "/widget/chat/{id}", produces = "application/javascript")
//        public ResponseEntity<String> handleUserSubmit(
//                @PathVariable UUID id,
//                @RequestParam String username,
//                @RequestParam String email) {
//
//            // Get or create user
//            String usernameToUse;
//            Optional<UserInfo> optUser = userinforepository.findByEmail(email);
//            if (optUser.isPresent()) {
//                usernameToUse = optUser.get().getUsername();
//            } else {
//                UserInfo user = new UserInfo();
//                user.setUsername(username);
//                user.setEmail(email);
//                userinforepository.save(user);
//                usernameToUse = username;
//            }
//
//            // Fetch widget
//            ScriptGenerate script = scriptgeneraterepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Widget not found"));
//
//         // Build header using appearance order
//            List<String> appearanceOrder = script.getAppearence();
//            StringBuilder headerContent = new StringBuilder();
//
//            String logoImg = (script.getLogo() != null)
//                    ? "<div style='text-align:" + script.getLogoAlign() + ";'><img src='data:image/png;base64,"
//                        + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'></div>"
//                    : "";
//
//            for (String item : appearanceOrder) {
//                switch (item) {
//                    case "Logo" -> headerContent.append(logoImg);
//                    case "Heading" -> headerContent.append("<div class='chatbot-heading' style='text-align:")
//                            .append(script.getHeadingAlign()).append(";'>")
//                            .append(script.getHeading()).append("</div>");
//                    case "TextArea" -> headerContent.append("<div class='chatbot-text' style='text-align:")
//                            .append(script.getTextAlign()).append(";'>")
//                            .append(script.getTextArea()).append("</div>");
//                }
//            }
//
//            // Load all triggers of type "Basic"
//            List<Trigger> triggers = triggerRepository.findAllByTriggerType_TriggerType("Basic");
//            triggers.sort(Comparator.comparingInt(Trigger::getDelay));
//
//         // Insert into chatbot-header
//            String welcomeHtml = """
//                <div class="chatbot-header" style="background-color: %s; color: white;">
//                    %s
//                </div>
//                <div id="chatbot-body">
//                    <div id="chatbot-messages">
//                       
//                    </div>
//                    <div class="chatbot-input-container">
//                        <input type="text" id="chatbot-user-input" placeholder="Type your message..." />
//                        <button id="chatbot-send-button">➤</button>
//                    </div>
//                </div>
//                """.formatted(
//                    script.getButtonColor(),  // Match header color from before
//                    headerContent.toString(),
//                    usernameToUse
//                );
//
//            // JS for trigger messages
//            StringBuilder timeouts = new StringBuilder();
//            for (Trigger t : triggers) {
//                String message = t.getTextOption().getText();
//                int delayMs = t.getDelay() * 1000;
//
//                timeouts.append("""
//                    setTimeout(function() {
//                        var msgContainer = document.getElementById('chatbot-messages');
//                        if (msgContainer) {
//                            var p = document.createElement('p');
//                            p.textContent = "%s";
//                            msgContainer.appendChild(p);
//                        }
//                    }, %d);
//                """.formatted(message.replace("\"", "\\\""), delayMs));
//            }
//
//            // Final JS
//            String jsUpdate = """
//                (function() {
//                    var panel = document.getElementById('chatbot-panel');
//                    if (panel) {
//                        panel.innerHTML = `%s`;
//                    }
//
//                    %s
//
//                    document.getElementById('chatbot-send-button').addEventListener('click', function() {
//                        const input = document.getElementById('chatbot-user-input');
//                        const text = input.value.trim();
//                        if (text) {
//                            const msg = document.createElement('p');
//                            msg.textContent = 'You: ' + text;
//                            document.getElementById('chatbot-messages').appendChild(msg);
//                            input.value = '';
//                        }
//                    });
//                })();
//                """.formatted(
//                welcomeHtml.replace("`", "\\`").replace("\n", "").replace("\r", ""),
//                timeouts.toString()
//            );
//
//            return ResponseEntity.ok(jsUpdate);
//        }





//@GetMapping(value = "/widget/{id}", produces = "application/javascript")
//public ResponseEntity<String> serveWidgetScript(@PathVariable UUID id) {
//  ScriptGenerate script = scriptgeneraterepository.findById(id)
//          .orElseThrow(() -> new RuntimeException("Widget not found"));
//
//  String logoImg = (script.getLogo() != null)
//          ? "<img src='data:image/png;base64," + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'>"
//          : "";
//
//  String widgetHtml = """
//      <style>
//          #chatbot-launcher {
//              position: fixed;
//              bottom: 20px;
//              right: 20px;
//              width: 60px;
//              height: 60px;
//              background-color: %s;
//              border-radius: 50%%;
//              display: flex;
//              align-items: center;
//              justify-content: center;
//              cursor: pointer;
//              box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
//              z-index: 9999;
//          }
//          #chatbot-launcher img {
//              width: 30px;
//              height: 30px;
//          }
//          #chatbot-panel {
//              position: fixed;
//              bottom: 90px;
//              right: 20px;
//              width: 350px;
//              background: white;
//              border-radius: 10px;
//              box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
//              display: none;
//              z-index: 9999;
//              font-family: Arial, sans-serif;
//              overflow: hidden;
//          }
//          .chatbot-header {
//              background-color: %s;
//              padding: 10px;
//              color: white;
//              text-align: %s;
//          }
//          .chatbot-logo {
//              max-width: 60px;
//              max-height: 60px;
//              margin: 0 auto 10px;
//              display: block;
//          }
//          .chatbot-heading {
//              font-size: 20px;
//              margin: 0 0 5px;
//          }
//          .chatbot-text {
//              font-size: 14px;
//              margin-bottom: 0;
//          }
//          #chatbot-body {
//              display: flex;
//              flex-direction: column;
//              height: 300px;
//              padding: 15px;
//              color: #333;
//              text-align: left;
//          }
//          #chatbot-messages {
//              flex: 1;
//              overflow-y: auto;
//              margin-bottom: 10px;           
//          }
//          .chatbot-form-title {
//              font-size: 18px;
//              font-weight: bold;
//              margin-bottom: 15px;
//          }
//          .chatbot-form {
//              display: flex;
//              flex-direction: column;
//              gap: 30px;
//              align-items: center;
//          }
//          .chatbot-form input {
//              padding: 10px;
//              width: 90%%;
//              border: 1px solid #ccc;
//              border-radius: 5px;
//              font-size: 14px;
//          }
//          .chatbot-form button {
//              background-color: %s;
//              color: white;
//              padding: 10px;
//              width: 90%%;
//              border: none;
//              border-radius: 5px;
//              cursor: pointer;
//              font-size: 14px;
//          }
//          .chatbot-input-container {
//              display: flex;
//              gap: 10px;
//          }
//          #chatbot-user-input {
//              flex: 1;
//              padding: 10px;
//              border: 1px solid #ccc;
//              border-radius: 5px;
//          }
//          #chatbot-send-button {
//              background-color: %s;
//              color: white;
//              border: none;
//              padding: 10px 15px;
//              border-radius: 5px;
//              cursor: pointer;
//          }
//      </style>
//      <div id="chatbot-launcher">
//          <img src="https://cdn-icons-png.flaticon.com/512/4712/4712027.png" alt="Chatbot">
//      </div>
//      <div id="chatbot-panel">
//          <div class="chatbot-header">
//              %s
//              <div class="chatbot-heading">%s</div>
//              <div class="chatbot-text">%s</div>
//          </div>
//          <div id="chatbot-body">
//              <div class="chatbot-form-title">Login</div>
//              <form class="chatbot-form">
//                  <input type="text" name="username" placeholder="Username" required>
//                  <input type="email" name="email" placeholder="Email" required>
//                  <button type="submit">➤ Start Chat</button>
//              </form>
//          </div>
//      </div>
//      """.formatted(
//      script.getButtonColor(),
//      script.getButtonColor(),
//      script.getHeadingAlign(),
//      script.getButtonColor(),
//      script.getButtonColor(),
//      logoImg,
//      script.getHeading(),
//      script.getTextArea()
//  );
//
//  String escapedHtml = widgetHtml
//          .replace("\\", "\\\\")
//          .replace("\"", "\\\"")
//          .replace("`", "\\`")
//          .replace("\n", "")
//          .replace("\r", "");
//
//  String js = """
//      (function() {
//          var wrapper = document.createElement('div');
//          wrapper.innerHTML = `%s`;
//          document.body.appendChild(wrapper);
//
//          var launcher = document.getElementById('chatbot-launcher');
//          var panel = document.getElementById('chatbot-panel');
//
//          if (launcher && panel) {
//              launcher.addEventListener('click', function () {
//                  panel.style.display = (panel.style.display === 'none') ? 'block' : 'none';
//              });
//          }
//
//          var form = document.querySelector('.chatbot-form');
//          if (form) {
//              form.addEventListener('submit', function(event) {
//                  event.preventDefault();
//                  var username = form.querySelector('input[name="username"]').value;
//                  var email = form.querySelector('input[name="email"]').value;
//
//                  var params = new URLSearchParams();
//                  params.append('username', username);
//                  params.append('email', email);
//
//                  fetch('http://localhost:8080/chatbot/widget/chat/%s', {
//                      method: 'POST',
//                      headers: {
//                          'Content-Type': 'application/x-www-form-urlencoded'
//                      },
//                      body: params.toString()
//                  })
//                  .then(response => response.text())
//                  .then(jsCode => {
//                      eval(jsCode);
//                  })
//                  .catch(err => {
//                      console.error('Login failed:', err);
//                  });
//              });
//          }
//      })();
//      """.formatted(escapedHtml, id.toString());
//
//  return ResponseEntity.ok(js);
//}

    



