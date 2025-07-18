package com.VsmartEngine.Chatbot.ScriptGenerate;

import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@CrossOrigin()
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
    
    @Value("${BackendUrl}")
	private String backendurl;
    
//    @Autowired
//    private AdminService adminService;
    
    
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

//        private String generateWidgetScript(UUID id) {
//            return "<script async defer src= backendurl +'/chatbot/widget/" + id + "'></script>";
//        } 
        
        private String generateWidgetScript(UUID id) {
            return "<script async defer src='" + backendurl + "/chatbot/widget/" + id + "'></script>";
        }

        
        
            @GetMapping(value = "/widget/{id}", produces = "application/javascript")
            public ResponseEntity<String> serveWidgetScript(@PathVariable UUID id) {
                ScriptGenerate script = scriptgeneraterepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Widget not found"));

                String logoImg = (script.getLogo() != null)
                        ? "<div style='text-align:" + script.getLogoAlign() + ";'><img src='data:image/png;base64," 
                          + Base64.getEncoder().encodeToString(script.getLogo()) + "' class='chatbot-logo'></div>"
                        : "";

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

                // Escape for JS string literal
                String escapedHtml = widgetHtml
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("`", "\\`")
                        .replace("\n", "")
                        .replace("\r", "");

                // JS wrapper to inject HTML and handle login form submit
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
                                fetch('%s/chatbot/widget/chat/%s', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    body: params.toString()
                                })
                                .then(response => response.text())
                                .then(jsCode => {
    console.log('Received JS to eval:', jsCode);
    eval(jsCode);
})
                                .catch(err => {
                                    console.error('Login failed:', err);
                                });
                            });
                        }
                    })();
                    """.formatted(escapedHtml, backendurl,id.toString());

                return ResponseEntity.ok(js);
            }
            
            @PostMapping(value = "/widget/chat/{id}", produces = "application/javascript")
            public ResponseEntity<String> handleUserSubmit(
                    @PathVariable UUID id,
                    @RequestParam String username,
                    @RequestParam String email) {

                try {
                    String role = "USER";
                    UserInfo user = userinforepository.findByEmail(email)
                            .orElseGet(() -> userinforepository.save(new UserInfo(username, email, role)));

                    String senderIdStr = user.getEmail();
                    String jwtToken = jwtUtil.generateToken(email, user.getId(), role);
                    user.setToken(jwtToken);
                    userinforepository.save(user);

                    ScriptGenerate script = scriptgeneraterepository.findById(id).orElse(null);
                    if (script == null) {
                        return ResponseEntity.ok("alert('Widget not found');");
                    }

                    Trigger trigger = triggerRepository.findByStatusTrue().orElse(null);
                    if (trigger == null) {
                        return ResponseEntity.ok("alert('Active trigger not found');");
                    }

                    String buttonColor = script.getButtonColor() != null ? script.getButtonColor() : "#007bff";

                    StringBuilder headerContent = new StringBuilder();
                    for (String item : script.getAppearence()) {
                        switch (item) {
                            case "Logo" -> {
                                if (script.getLogo() != null) {
                                    String base64Img = Base64.getEncoder().encodeToString(script.getLogo());
                                    headerContent.append("<div style='text-align:")
                                            .append(script.getLogoAlign()).append(";'>")
                                            .append("<img src='data:image/png;base64,")
                                            .append(base64Img).append("' class='chatbot-logo'></div>");
                                }
                            }
                            case "Heading" -> headerContent.append("<div class='chatbot-heading' style='text-align:")
                                    .append(script.getHeadingAlign()).append(";'>")
                                    .append(escapeHtml(script.getHeading())).append("</div>");
                            case "TextArea" -> headerContent.append("<div class='chatbot-text' style='text-align:")
                                    .append(script.getTextAlign()).append(";'>")
                                    .append(escapeHtml(script.getTextArea())).append("</div>");
                        }
                    }

                    StringBuilder welcomeMessages = new StringBuilder();
                    for (String item : trigger.getFirstTrigger()) {
                        switch (item) {
                            case "Text Area" -> {
                                if (trigger.getTextOption() != null) {
                                    String textMessage = escapeHtml(trigger.getTextOption().getText());
                                    welcomeMessages.append("<div style=\"background-color:")
                                            .append(buttonColor).append("; color:white; max-width:85%; font-size:14px; padding:10px; border-radius:5px; margin-bottom:5px;\">")
                                            .append(textMessage).append("</div>");
                                }
                            }
                            case "Department" -> {
                                if (!trigger.getDepartments().isEmpty()) {
                                    welcomeMessages.append("<p style='font-weight:bold;'>Set your categories</p>");
                                    for (SetDepartment dept : trigger.getDepartments()) {
                                        welcomeMessages.append("<p class='chatbot-department' data-dept-id='")
                                                .append(dept.getDepId()).append("' style=\"background-color:")
                                                .append(buttonColor).append("; color:white; padding:10px; max-width:85%; font-size:14px; border-radius:5px; margin-bottom:3px; display:block; cursor:pointer;\">")
                                                .append(escapeHtml(dept.getName())).append("</p>");
                                    }
                                }
                            }
                        }
                    }

                    String baseUrl = "http://localhost:8080";

                         String chatPanelJs = String.format("""
(function() {
    function loadScript(src, callback) {
        var s = document.createElement('script');
        s.src = src;
        s.onload = callback;
        document.head.appendChild(s);
    }

    loadScript("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js", function () {
        loadScript("https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js", function () {
            const senderIdStr = '%s';
            let stompClient = null;
            let selectedAdmin = null;
            let sessionId = null;

            const panel = document.getElementById('chatbot-panel');
            panel.innerHTML = `
                <div class="chatbot-header" style="background-color: %s; color: white; padding: 10px;">
                    %s
                </div>
                <div id="chatbot-messages" style="height:250px;overflow-y:auto;padding:10px;background:#f8f8f8;"></div>
                <div class="chatbot-input-container" style="padding:10px;">
                    <input type="text" id="chatbot-user-input" placeholder="Type your message..." />
                    <button id="chatbot-send-button">Send</button>
                </div>
            `;

            const messagesEl = document.getElementById('chatbot-messages');
            messagesEl.innerHTML = `%s`;

            document.addEventListener('click', e => {
                if (e.target.matches('.chatbot-department')) {
                    const deptId = e.target.dataset.deptId;
                    appendMessage('Connecting to department...', 'BOT');
                    fetch('%s/chatbot/getdep/' + deptId)
                        .then(r => r.json())
                        .then(dep => {
                            if (dep.admins.length) {
                                selectedAdmin = dep.admins[0];
                                appendMessage('Chatting with ' + selectedAdmin.username, 'BOT');

                                fetch('%s/start', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    body: new URLSearchParams({
                                        sender: senderIdStr,
                                        receiver: selectedAdmin.email
                                    })
                                })
                                .then(r => r.json())
                                .then(data => {
                                    sessionId = data.sessionId;
                                    connectWebSocket(sessionId);
                                });
                            } else {
                                appendMessage('No admin available.', 'BOT');
                            }
                        })
                        .catch(() => appendMessage('Error fetching department.', 'BOT'));
                }
            });

            const input = document.getElementById('chatbot-user-input');
            const btn = document.getElementById('chatbot-send-button');
            btn.onclick = send;
            input.onkeydown = e => e.key === 'Enter' ? send() : null;

            function send() {
                const text = input.value.trim();
                if (!text || !sessionId || !stompClient || !selectedAdmin) {
                    appendMessage("Please complete session setup.", "BOT");
                    return;
                }

               <!-- appendMessage(text, 'USER'); -->

                stompClient.send("/app/send", {}, JSON.stringify({
                    sessionId: sessionId,
                    sender: senderIdStr,
                    receiver: selectedAdmin.email,
                    content: text,
                    role: 'USER'
                }));

                input.value = '';
            }

            function appendMessage(text, role) {
                const d = document.createElement('div');
                d.textContent = text;
                d.className = 'chatbot-message';
                d.style.marginBottom = '8px';
                d.style.padding = '8px 12px';
                d.style.borderRadius = '10px';
                d.style.maxWidth = '70%%';
                d.style.display = 'inline-block';
                d.style.clear = 'both';

                if (role === 'USER') {
                    d.style.backgroundColor = '#007bff';
                    d.style.color = 'white';
                    d.style.textAlign = 'right';
                    d.style.float = 'right';
                } else {
                    d.style.backgroundColor = '#e0e0e0';
                    d.style.color = 'black';
                    d.style.textAlign = 'left';
                    d.style.float = 'left';
                }

                messagesEl.appendChild(d);
                const br = document.createElement('div');
                br.style.clear = 'both';
                messagesEl.appendChild(br);
                messagesEl.scrollTop = messagesEl.scrollHeight;
            }

            function connectWebSocket(sessionId) {
                const socket = new SockJS('%s/chat');
                stompClient = Stomp.over(socket);

                stompClient.connect({}, function () {
                    console.log("Connected to WebSocket");
                    stompClient.subscribe(`/topic/messages/` + sessionId, function (msg) {
                        const message = JSON.parse(msg.body);
                        appendMessage(message.content, message.role);
                    });
                });
            }
        });
    });
})();
""",
    escapeJs(senderIdStr),
    buttonColor,
    escapeJsForTemplateLiteral(headerContent.toString()),
    escapeJsForTemplateLiteral(welcomeMessages.toString()),
    backendurl, backendurl, backendurl
);


                    return ResponseEntity.ok(chatPanelJs);

                } catch (Exception e) {
                    return ResponseEntity.ok("alert('Error: " + escapeJs(e.getMessage()) + "');");
                }
            }

            private String escapeJs(String input) {
                return input == null ? "" : input.replace("'", "\\'");
            }

            private String escapeJsForTemplateLiteral(String input) {
                if (input == null) return "";
                return input.replace("\\", "\\\\")
                            .replace("`", "\\`")
                            .replace("${", "\\${")
                            .replace("\r", "")
                            .replace("\n", "\\n");
            }

            private String escapeHtml(String input) {
                return input == null ? "" : input.replace("&", "&amp;")
                                                 .replace("<", "&lt;")
                                                 .replace(">", "&gt;")
                                                 .replace("\"", "&quot;")
                                                 .replace("'", "&#x27;");
            }

            
//            @PostMapping(value = "/widget/chat/{id}", produces = "application/javascript")
//            public ResponseEntity<String> handleUserSubmit(
//                    @PathVariable UUID id,
//                    @RequestParam String username,
//                    @RequestParam String email) {
//
//                String role = "USER";
//                UserInfo user;
//                
//                Optional<UserInfo> optUser = userinforepository.findByEmail(email);
//                if (optUser.isPresent()) {
//                    user = optUser.get();
//                } else {
//                    user = new UserInfo();
//                    user.setUsername(username);
//                    user.setEmail(email);
//                    user.setRole(role);
//                    user = userinforepository.save(user); // Save to generate ID
//                }
//
//                String senderIdStr = String.valueOf(user.getId());
//                String jwtToken = jwtUtil.generateToken(email, user.getId(), role);
//                user.setToken(jwtToken);
//                userinforepository.save(user);
//                
//                System.out.println("jwtToken" + jwtToken);
//
//                ScriptGenerate script = scriptgeneraterepository.findById(id)
//                        .orElseThrow(() -> new RuntimeException("Widget not found"));
//                String buttonColor = script.getButtonColor() != null ? script.getButtonColor() : "#007bff";
//
//                Trigger trigger = triggerRepository.findByStatusTrue()
//                        .orElseThrow(() -> new RuntimeException("Active trigger not found"));
//
//                List<String> appearanceOrder = script.getAppearence();
//                StringBuilder headerContent = new StringBuilder();
//                for (String item : appearanceOrder) {
//                    switch (item) {
//                        case "Logo" -> {
//                            if (script.getLogo() != null) {
//                                String base64Img = Base64.getEncoder().encodeToString(script.getLogo());
//                                headerContent.append("<div style='text-align:").append(script.getLogoAlign()).append(";'>")
//                                        .append("<img src='data:image/png;base64,").append(base64Img).append("' class='chatbot-logo'>")
//                                        .append("</div>");
//                            }
//                        }
//                        case "Heading" -> headerContent.append("<div class='chatbot-heading' style='text-align:")
//                                .append(script.getHeadingAlign()).append(";'>")
//                                .append(escapeHtml(script.getHeading())).append("</div>");
//                        case "TextArea" -> headerContent.append("<div class='chatbot-text' style='text-align:")
//                                .append(script.getTextAlign()).append(";'>")
//                                .append(escapeHtml(script.getTextArea())).append("</div>");
//                    }
//                }
//
//                List<String> firstTriggerOrder = trigger.getFirstTrigger();
//                boolean hasDepartment = !trigger.getDepartments().isEmpty();
//                boolean hasTextArea = trigger.getTextOption() != null;
//
//                StringBuilder welcomeMessages = new StringBuilder();
//                for (String item : firstTriggerOrder) {
//                    switch (item) {
//                        case "Text Area" -> {
//                            if (hasTextArea) {
//                                String textMessage = escapeHtml(trigger.getTextOption().getText());
//                                welcomeMessages.append("<div style=\"background-color:")
//                                        .append(buttonColor)
//                                        .append("; color:white; max-width:85%; font-size:14px; padding:10px; border-radius:5px; margin-bottom:5px;\">")
//                                        .append(textMessage)
//                                        .append("</div>");
//                            }
//                        }
//                        case "Department" -> {
//                            if (hasDepartment) {
//                                welcomeMessages.append("<p style='font-weight:bold; margin-bottom:1px;'>Set your categories</p>");
//                                for (SetDepartment dept : trigger.getDepartments()) {
//                                    welcomeMessages.append("<p class='chatbot-department' data-dept-id='")
//                                            .append(dept.getDepId())
//                                            .append("' style=\"background-color:")
//                                            .append(buttonColor)
//                                            .append("; color:white; padding:10px; max-width:85%; font-size:14px; border-radius:5px; margin-bottom:3px; display:block; cursor:pointer;\">")
//                                            .append(escapeHtml(dept.getName()))
//                                            .append("</p>");
//                                }
//                            }
//                        }
//                    }
//                }
//
//                String baseUrl = "http://localhost:8080";
//
//                String chatPanelJs = String.format("""
//                	    (function() {
//                	        const panel = document.getElementById('chatbot-panel');
//                	        panel.innerHTML = `
//                	            <div class="chatbot-header" style="background-color: %s; color: white; padding: 10px;">
//                	                %s
//                	            </div>
//                	            <div id="chatbot-messages" style="height:250px;overflow-y:auto;padding:10px;background:#f8f8f8;"></div>
//                	            <div class="chatbot-input-container" style="padding:10px;">
//                	                <input type="text" id="chatbot-user-input" placeholder="Type your message..." />
//                	                <button id="chatbot-send-button">Send</button>
//                	            </div>
//                	        `;
//
//                	        const messagesEl = document.getElementById('chatbot-messages');
//                	        messagesEl.innerHTML = `%s`;
//
//                	        let selectedAdmin = null;
//                	        let sessionId = null;
//
//                	        document.addEventListener('click', e => {
//                	            if (e.target.matches('.chatbot-department')) {
//                	                const deptId = e.target.dataset.deptId;
//                	                appendMessage('Connecting to department...', 'BOT');
//                	                fetch('%s/chatbot/getdep/' + deptId)
//                	                    .then(r => r.json())
//                	                    .then(dep => {
//                	                        if (dep.admins.length) {
//                	                            selectedAdmin = dep.admins[0];
//                	                            appendMessage('Chatting with ' + selectedAdmin.username, 'BOT');
//
//                	                            fetch('%s/chatbot/chatbot/get-session-id?sender=' + '%s' + '&receiver=' + selectedAdmin.id)
//                	                                .then(r => r.json())
//                	                                .then(session => {
//                	                                    sessionId = session.sessionId;
//                	                                    console.log("sessionId", sessionId);
//                	                                    fetchMessages();
//                	                                });
//                	                        } else {
//                	                            appendMessage('No admin available.', 'BOT');
//                	                        }
//                	                    })
//                	                    .catch(() => appendMessage('Error fetching department.', 'BOT'));
//                	            }
//                	        });
//
//                	        const input = document.getElementById('chatbot-user-input');
//                	        const btn = document.getElementById('chatbot-send-button');
//                	        btn.onclick = send;
//                	        input.onkeydown = e => e.key === 'Enter' ? send() : null;
//
//                	        function send() {
//                	            const text = input.value.trim();
//                	            if (!text) return;
//                	            if (!selectedAdmin) {
//                	                appendMessage('Please select a department first.', 'BOT');
//                	                return;
//                	            }
//
//                	            // Append user's message immediately
//                	            appendMessage(text, 'USER');
//
//                	            fetch('%s/chatbot/send', {
//                	                method: 'POST',
//                	                headers: {
//                	                    'Content-Type': 'application/json',
//                	                    'Authorization': '%s'
//                	                },
//                	                body: JSON.stringify({
//                	                    sender: '%s',
//                	                    receiver: selectedAdmin.id,
//                	                    message: text
//                	                })
//                	            }).then(res => {
//                	                if (!res.ok) throw new Error('Message send failed');
//                	                return res.json();
//                	            }).then(response => {
//                	                fetchMessages();
//                	                console.log('Message sent:', response);
//                	            }).catch(err => {
//                	                appendMessage('Failed to send message.', 'BOT');
//                	            });
//
//                	            input.value = '';
//                	        }
//
//                	        function appendMessage(text, role) {
//                	            const d = document.createElement('div');
//                	            d.textContent = text;
//                	            d.className = 'chatbot-message';
//                	            d.style.marginBottom = '8px';
//                	            d.style.padding = '8px 12px';
//                	            d.style.borderRadius = '10px';
//                	            d.style.maxWidth = '70%%';
//                	            d.style.display = 'inline-block';
//
//                	            if (role === 'USER') {
//                	                d.style.backgroundColor = '#007bff';
//                	                d.style.color = 'white';
//                	                d.style.textAlign = 'right';
//                	                d.style.float = 'right';
//                	            } else {
//                	                d.style.backgroundColor = '#e0e0e0';
//                	                d.style.color = 'black';
//                	                d.style.textAlign = 'left';
//                	                d.style.float = 'left';
//                	            }
//
//                	            messagesEl.appendChild(d);
//
//                	            const br = document.createElement('div');
//                	            br.style.clear = 'both';
//                	            messagesEl.appendChild(br);
//
//                	            messagesEl.scrollTop = messagesEl.scrollHeight;
//                	        }
//
//                	        async function fetchMessages() {
//                	            try {
//                	                const sentUrl = `%s/chatbot/${sessionId}/sent/` + selectedAdmin.id;
//                	                const receivedUrl = `%s/chatbot/${sessionId}/` + selectedAdmin.id + `/received`;
//
//                	                const [sentRes, receivedRes] = await Promise.all([
//                	                    fetch(sentUrl, { headers: { "Authorization": "%s" } }),
//                	                    fetch(receivedUrl, { headers: { "Authorization": "%s" } })
//                	                ]);
//
//                	                if (!sentRes.ok || !receivedRes.ok) {
//                	                    appendMessage("Failed to fetch messages.", "BOT");
//                	                    return;
//                	                }
//
//                	                const sentData = await sentRes.json().catch(() => ({ chat: [] }));
//                	                const receivedData = await receivedRes.json().catch(() => ({ chat: [] }));
//
//                	                const sentMessages = sentData?.chat || [];
//                	                const receivedMessages = receivedData?.chat || [];
//
//                	                const allMessages = [...sentMessages, ...receivedMessages];
//                	                allMessages.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
//
//                	                messagesEl.innerHTML = ''; // Clear previous messages
//                	                allMessages.forEach(msg => {
//                	                    appendMessage(msg.message, msg.role);
//                	                });
//
//                	            } catch (err) {
//                	                appendMessage("Error fetching chat history.", "BOT");
//                	            }
//                	        }
//
//                	        // Poll every 3s
//                	        setInterval(() => {
//                	            if (sessionId && selectedAdmin) {
//                	                fetchMessages();
//                	            }
//                	        }, 3000);
//                	        
//                	        
//                	        
//                	        setInterval(() => {
//  fetch("http://localhost:8080/chatbot/ping", {
//    method: "POST",
//    headers: {
//      "Authorization": "Bearer " + %s,
//      "Content-Type": "application/json"
//    }
//    
//  }).catch(err => console.error("Ping failed:", err));
//}, 3000);
//
//console.log("token",%s);
//
//
//                	    })();
//                	    """,
//                	    buttonColor,
//                	    escapeJsForTemplateLiteral(headerContent.toString()),
//                	    escapeJsForTemplateLiteral(welcomeMessages.toString()),
//                	    baseUrl,                                   // getdep
//                	    baseUrl, escapeJs(senderIdStr),            // get-session-id
//                	    baseUrl, escapeJs(jwtToken), escapeJs(senderIdStr), // send
//                	    baseUrl, baseUrl,                          // fetchMessages (sent & received)
//                	    escapeJs(jwtToken), escapeJs(jwtToken),    // Authorization headers
//                	    escapeJs(senderIdStr),
//                	    escapeJs(jwtToken),
//                	    escapeJs(jwtToken)
//                	    
//                	    // sender ID (used in backend comparison if needed)
//                	);
//
//                return ResponseEntity.ok(chatPanelJs);
//            }
//
//            private String escapeJs(String input) {
//                return input == null ? "" : input.replace("'", "\\'");
//            }
//
//            private String escapeJsForTemplateLiteral(String input) {
//                if (input == null) return "";
//                return input.replace("\\", "\\\\")
//                            .replace("`", "\\`")
//                            .replace("${", "\\${")
//                            .replace("\r", "")
//                            .replace("\n", "\\n");
//            }
//
//            private String escapeHtml(String input) {
//                return input == null ? "" : input.replace("&", "&amp;")
//                                                 .replace("<", "&lt;")
//                                                 .replace(">", "&gt;")
//                                                 .replace("\"", "&quot;")
//                                                 .replace("'", "&#x27;");
//            }
            
}
            
            
