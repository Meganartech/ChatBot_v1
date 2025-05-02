package com.VsmartEngine.Chatbot.WidgetController;

import com.VsmartEngine.Chatbot.WidgetController.Property;
import com.VsmartEngine.Chatbot.WidgetController.PropertyRepo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/widget")
@CrossOrigin(origins = "*")  // Allow any origin (for development purposes only)
public class WidgetController {

    private final PropertyRepo propertyRepo;

    public WidgetController(PropertyRepo propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    @GetMapping(value = "/{uniquePropertyId}", produces = "application/javascript")
    public ResponseEntity<String> getWidgetScript(@PathVariable String uniquePropertyId) {
        Optional<Property> propertyOpt = propertyRepo.findByUniquePropertyId(uniquePropertyId);
        if (propertyOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Property ID");
        }

        Property property = propertyOpt.get();
        String buttonColor = property.getButtonColor(); // Get the stored button color

        String widgetScript = String.format("""
            (function() {
                function loadChatWidget() {
                    var chatboxFrame = null;

                    var widgetContainer = document.createElement('div');
                    widgetContainer.style.position = "fixed";
                    widgetContainer.style.bottom = "20px";
                    widgetContainer.style.right = "20px";
                    widgetContainer.style.textAlign = "center";

                    var chatLabel = document.createElement('div');
                    chatLabel.innerText = "Let's CHAT";
                    chatLabel.style.background = "%2$s";
                    chatLabel.style.color = "white";
                    chatLabel.style.padding = "5px 10px";
                    chatLabel.style.borderRadius = "10px";
                    chatLabel.style.marginBottom = "5px";
                    chatLabel.style.fontSize = "14px";
                    chatLabel.style.boxShadow = "0px 2px 4px rgba(0, 0, 0, 0.2)";

                    var toggleButton = document.createElement('button');
                    toggleButton.innerText = "💬";
                    toggleButton.style.width = "50px";
                    toggleButton.style.height = "50px";
                    toggleButton.style.borderRadius = "50%%";
                    toggleButton.style.border = "none";
                    toggleButton.style.background = "%2$s";  // Set dynamic button color
                    toggleButton.style.color = "white";
                    toggleButton.style.cursor = "pointer";
                    toggleButton.style.fontSize = "20px";
                    toggleButton.style.boxShadow = "0px 4px 6px rgba(0, 0, 0, 0.2)";

                    widgetContainer.appendChild(chatLabel);
                    widgetContainer.appendChild(toggleButton);
                    document.body.appendChild(widgetContainer);

                    toggleButton.addEventListener('click', function() {
                        if (!chatboxFrame) {
                            chatboxFrame = document.createElement('iframe');
                            chatboxFrame.src = "http://localhost:8080/chatbox/%1$s";
                            chatboxFrame.style.position = "fixed";
                            chatboxFrame.style.bottom = "75px";
                            chatboxFrame.style.right = "20px";
                            chatboxFrame.style.width = "350px";
                            chatboxFrame.style.height = "500px";
                            chatboxFrame.style.border = "none";
                            chatboxFrame.style.boxShadow = "0px 4px 6px rgba(0, 0, 0, 0.1)";
                            chatboxFrame.sandbox = "allow-scripts allow-same-origin allow-forms allow-modals allow-popups allow-downloads allow-top-navigation-by-user-activation";
                            document.body.appendChild(chatboxFrame);
                        } else {
                            chatboxFrame.remove();
                            chatboxFrame = null;
                        }
                    });
                }

                if (document.readyState === "complete" || document.readyState === "interactive") {
                    loadChatWidget();
                } else {
                    document.addEventListener("DOMContentLoaded", loadChatWidget);
                }
            })();
        """, uniquePropertyId, buttonColor);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/javascript");

        return new ResponseEntity<>(widgetScript, headers, HttpStatus.OK);
    }
}
