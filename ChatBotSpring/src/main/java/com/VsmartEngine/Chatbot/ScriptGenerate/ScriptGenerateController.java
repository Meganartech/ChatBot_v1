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
import com.VsmartEngine.Chatbot.Trigger.Trigger;
import com.VsmartEngine.Chatbot.Trigger.TriggerRepository;

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
                @RequestParam(value = " TextAlign", required = false) String  TextAlign) {
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
                script.setTextAlign(TextAlign);
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
        
        
        @PostMapping("/widget/property")
        public ResponseEntity<?> savePropertyInfo(
                @RequestHeader("Authorization") String token,
                @RequestParam( value = "scriptId",required =false) UUID scriptId,
                @RequestParam("propertyName") String propertyName,
                @RequestParam("websiteURL") String websiteURL,
                @RequestParam("buttonColor") String buttonColor) {
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
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid scriptId. Please create appearance before setting property.");
                }

                ScriptGenerate script = optionalScript.get();

                boolean isAppearanceSet = (script.getHeading() != null && !script.getHeading().isBlank()) ||
                                          (script.getTextArea() != null && !script.getTextArea().isBlank()) ||
                                          (script.getLogo() != null && script.getLogo().length > 0);

                if (!isAppearanceSet) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Appearance not set. Please set appearance before setting property info.");
                }

                script.setPropertyName(propertyName);
                script.setWebsiteURL(websiteURL);
                script.setButtonColor(buttonColor);

                String widgetScript = generateWidgetScript(scriptId);
                script.setWidgetScript(widgetScript);
                scriptgeneraterepository.save(script);

                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(widgetScript);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving property info: " + e.getMessage());
            }
        }

        

//        @PostMapping("/widget/property")
//        public ResponseEntity<?> savePropertyInfo(
//                @RequestHeader("Authorization") String token,
//                @RequestParam("scriptId") UUID scriptId,
//                @RequestParam("propertyName") String propertyName,
//                @RequestParam("websiteURL") String websiteURL,
//                @RequestParam("buttonColor") String buttonColor) {
//            try {
//                String role = jwtUtil.getRoleFromToken(token);
//                if (!"ADMIN".equals(role)) {
//                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can update property info.");
//                }
//
//                Optional<ScriptGenerate> optionalScript = scriptgeneraterepository.findById(scriptId);
//                if (optionalScript.isEmpty()) {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid script ID.");
//                }
//
//                ScriptGenerate script = optionalScript.get();
//                if (script.getHeading() == null && script.getTextArea() == null && script.getLogo() == null) {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Set appearance first.");
//                }
//
//                script.setPropertyName(propertyName);
//                script.setWebsiteURL(websiteURL);
//                script.setButtonColor(buttonColor);
//
//                String widgetScript = generateWidgetScript(scriptId);
//                script.setWidgetScript(widgetScript);
//                scriptgeneraterepository.save(script);
//
//                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(widgetScript);
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Error saving property info: " + e.getMessage());
//            }
//        }
      
        @PatchMapping("/widget/appearance/{id}")
        public ResponseEntity<?> updateWidgetAppearance(
                @RequestHeader("Authorization") String token,
                @PathVariable UUID id,
                @RequestParam(value = "Language", required = false) String language,
                @RequestParam(value = "logo", required = false) MultipartFile logo,
                @RequestParam(value = "heading", required = false) String heading,
                @RequestParam(value = "TextArea", required = false) String textArea,
                @RequestParam(value = "logoAlign", required = false) String logoAlign,
                @RequestParam(value = "headingAlign", required = false) String headingAlign) {
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

        @PatchMapping("/widget/property/{id}")
        public ResponseEntity<?> updatePropertyInfo(
                @RequestHeader("Authorization") String token,
                @PathVariable UUID id,
                @RequestParam(value = "propertyName", required = false) String propertyName,
                @RequestParam(value = "websiteURL", required = false) String websiteURL,
                @RequestParam(value = "buttonColor", required = false) String buttonColor) {
            try {
                String role = jwtUtil.getRoleFromToken(token);
                if (!"ADMIN".equalsIgnoreCase(role)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only admin can update property info.");
                }

                Optional<ScriptGenerate> optionalScript = scriptgeneraterepository.findById(id);
                if (optionalScript.isEmpty()) {
                    return ResponseEntity.badRequest().body("Script not found.");
                }

                ScriptGenerate script = optionalScript.get();

                if (propertyName != null) script.setPropertyName(propertyName);
                if (websiteURL != null) script.setWebsiteURL(websiteURL);
                if (buttonColor != null) script.setButtonColor(buttonColor);

                scriptgeneraterepository.save(script);

                return ResponseEntity.ok("success");
                        
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating property info: " + e.getMessage());
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
                    ? "<img src='data:image/png;base64," + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'>"
                    : "";

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
                        text-align: %s;
                    }
                    .chatbot-logo {
                        max-width: 60px;
                        max-height: 60px;
                        margin: 0 auto 10px;
                        display: block;
                    }
                    .chatbot-heading {
                        font-size: 20px;
                        margin: 0 0 5px;
                    }
                    .chatbot-text {
                        font-size: 14px;
                        margin-bottom: 0;
                    }
                    #chatbot-body {
                        display: flex;
                        flex-direction: column;
                        height: 300px;
                        padding: 15px;
                        color: #333;
                        text-align: left;
                    }
                    #chatbot-messages {
                        flex: 1;
                        overflow-y: auto;
                        margin-bottom: 10px;           
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
                    <div class="chatbot-header">
                        %s
                        <div class="chatbot-heading">%s</div>
                        <div class="chatbot-text">%s</div>
                    </div>
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
                script.getHeadingAlign(),
                script.getButtonColor(),
                script.getButtonColor(),
                logoImg,
                script.getHeading(),
                script.getTextArea()
            );

            String escapedHtml = widgetHtml
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("`", "\\`")
                    .replace("\n", "")
                    .replace("\r", "");

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

            // Save user info
            UserInfo user = new UserInfo();
            user.setUsername(username);
            user.setEmail(email);
            userinforepository.save(user);

            // Fetch widget
            ScriptGenerate script = scriptgeneraterepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Widget not found"));

            // Logo image as base64
            String logoImg = (script.getLogo() != null)
                    ? "<img src='data:image/png;base64," + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'>"
                    : "";

            // Load all triggers of type "Basic"
            List<Trigger> triggers = triggerRepository.findAllByTriggerType_TriggerType("Basic");
            triggers.sort(Comparator.comparingInt(Trigger::getDelay)); // sort by delay (optional)

            // HTML template
            String welcomeHtml = """
                <div class="chatbot-header">
                    %s
                    <div class="chatbot-heading">%s</div>
                    <div class="chatbot-text">%s</div>
                </div>
                <div id="chatbot-body">
                    <div id="chatbot-messages">
                        <p>👋 Hi <strong>%s</strong>, welcome to our chat!</p>
                    </div>
                    <div class="chatbot-input-container">
                        <input type="text" id="chatbot-user-input" placeholder="Type your message..." />
                        <button id="chatbot-send-button">➤</button>
                    </div>
                </div>
                """.formatted(
                logoImg,
                script.getHeading(),
                script.getTextArea(),
                username
            );

            // Generate JavaScript for each trigger message with delay
            StringBuilder timeouts = new StringBuilder();
            for (Trigger t : triggers) {
                String message = t.getTextOption().getText();
                int delayMs = t.getDelay() * 1000;

                timeouts.append("""
                    setTimeout(function() {
                        var msgContainer = document.getElementById('chatbot-messages');
                        if (msgContainer) {
                            var p = document.createElement('p');
                            p.textContent = "%s";
                            msgContainer.appendChild(p);
                        }
                    }, %d);
                """.formatted(message.replace("\"", "\\\""), delayMs));
            }

            // Final JS to inject into page
            String jsUpdate = """
                (function() {
                    var panel = document.getElementById('chatbot-panel');
                    if (panel) {
                        panel.innerHTML = `%s`;
                    }

                    %s

                    document.getElementById('chatbot-send-button').addEventListener('click', function() {
                        const input = document.getElementById('chatbot-user-input');
                        const text = input.value.trim();
                        if (text) {
                            const msg = document.createElement('p');
                            msg.textContent = 'You: ' + text;
                            document.getElementById('chatbot-messages').appendChild(msg);
                            input.value = '';
                        }
                    });
                })();
                """.formatted(
                welcomeHtml.replace("`", "\\`").replace("\n", "").replace("\r", ""),
                timeouts.toString()
            );

            return ResponseEntity.ok(jsUpdate);
        }


}

    



