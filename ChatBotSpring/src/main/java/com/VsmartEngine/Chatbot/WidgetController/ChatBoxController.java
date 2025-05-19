package com.VsmartEngine.Chatbot.WidgetController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/chatbox")
public class ChatBoxController {

    private static final Logger LOGGER = Logger.getLogger(ChatBoxController.class.getName());
    private final PropertyRepo propertyRepo;

    public ChatBoxController(PropertyRepo propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    @GetMapping("/{uniquePropertyId}")
    public ResponseEntity<String> getChatbox(@PathVariable String uniquePropertyId) {
        Optional<Property> propertyOpt = propertyRepo.findByUniquePropertyId(uniquePropertyId);
        
        LOGGER.info("Received request for Property ID: " + uniquePropertyId);

        if (propertyOpt.isEmpty()) {
            LOGGER.warning("Invalid Property ID: " + uniquePropertyId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("<h3>Invalid Property ID</h3>");
        }

        Property property = propertyOpt.get();  
        Long propertyId = property.getId(); 
        String receiver = property.getAdminEmail();
        
        // Safe handling of null chatbotType with default value
        String type = (property.getChatbotType() != null) 
            ? property.getChatbotType().toString() 
            : "DEFAULT";
       
        LOGGER.info(String.format(
            "Property Found - ID: %d, Receiver: %s, Chatbot Type: %s", 
            propertyId, receiver, type
        )); 

        String chatboxHtml = generateChatboxHtml(propertyId, receiver, type);

        if (chatboxHtml == null || chatboxHtml.isEmpty()) {
            LOGGER.severe("Failed to generate chatbox HTML for Property ID: " + propertyId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("<h3>Error generating chatbox</h3>");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");

        return new ResponseEntity<>(chatboxHtml, headers, HttpStatus.OK);
    }



    /** 🔹 Generates HTML String for Chatbox */
    private String generateChatboxHtml(Long propertyId,String receiver, String type) {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LiveChat</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> 
    
    <style>
   
	     #login-message, #register-message {
	        text-align: center; font-weight: bold;margin-top: 5px; padding: 8px; border-radius: 5px; 
	    }
	
		.alert-success {
		    color: white;
		    background-color: #28a745;
		}
		
		.alert-danger {
		    color: white;
		    background-color: #dc3545;
		}
	
	    #messages {
	            height: 370px;
	            overflow-y: auto;
	            font-size: 14px;
	            display: flex;
	            flex-direction: column;
	            gap: 6px;
	            padding: 10px;
	        }

        /* Sent Message */
        .sent {
            background-color: #007bff;
            color: white;
            align-self: flex-end;
            max-width: 50%%;
            text-align: right;
            border-radius: 15px 15px 0 15px;
            padding: 8px 10px;
            font-size: 14px;
            word-wrap: break-word;
        }

        /* Received Message */
        .received {
            background-color: #f1f1f1;
            color: black;
            align-self: flex-start;
            max-width: 70%%;
            text-align: left;
            border-radius: 15px 15px 15px 0;
            padding: 8px 10px;
            font-size: 14px;
            word-wrap: break-word;
        }

        /* Chat Input & Button */
        .chat-input-container {
            display: flex;
            align-items: center;
            padding: 10px;
            border-top: 1px solid #ccc;
        }

        #chat-input {
            flex: 1;
            border-radius: 20px;
            padding: 10px;
            font-size: 14px;
        }

        #send-btn {
            border-radius: 50%%;
            width: 45px;
            height: 45px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .question-container {
		    display: flex;
		    flex-direction: column;
		    gap: 8px;
		    margin-top: 10px;
		}
		
		.question-button {
		    background-color: #f8f9fa; /* Light gray background */
		    color: #007bff; /* Blue text */
		    border: 1px solid #007bff; /* Blue border */
		    padding: 8px 12px;
		    border-radius: 8px;
		    text-align: left;
		    font-size: 14px;
		    cursor: pointer;
		    transition: all 0.3s ease-in-out;
		    width: fit-content;
		    max-width: 75%%;
		    white-space: normal;
		}
		
		.question-button:hover {
		    background-color: #007bff;
		    color: white;
		}
		
		.question-button:active {
		    background-color: #0056b3;
		}

        
        .nav-tabs .nav-link {
	        color: #000;
	        transition: background 0.3s ease;
	    }
    
	    .nav-tabs .nav-link.active {
	    background-color:white;
	        border-bottom: 4px solid blue;
	        color: #000; font-weight:bold;
	    }
    </style>
    
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

</head>
<body class="bg-light">
   
    <!-- Front Page -->
    <div class="container mt-3 p-4 bg-white" id="auth-container" style="max-width: 320px;">
       <h2 class="text-center mb-4"> WELCOME TO LIVE CHAT </h2>
        <ul class="nav nav-tabs">
            <li class="nav-item">
                <a class="nav-link active tab-btn" onclick="showTab('login', event)">Login</a>
            </li>
            <li class="nav-item">
                <a class="nav-link tab-btn" onclick="showTab('register', event)">Register</a>
            </li>
        </ul>
      
		<div id="login-form">
		    <h3 class="mt-4 text-center">LOGIN</h3>
		    <div class="input-group mb-4">
		        <span class="input-group-text"><i class="fa fa-user"></i></span>
		        <input type="text" id="login-username" class="form-control" placeholder="Username">
		    </div>
		    <div class="input-group mb-4">
		        <span class="input-group-text"><i class="fa fa-lock"></i></span>
		        <input type="password" id="login-password" class="form-control" placeholder="Password">
		    </div>
		    <div id="login-message" class="alert d-none"></div>
		    <button id="login-btn" class="btn btn-primary w-100">
		       LOGIN to CHAT
		    </button>
		</div>
	
		<div id="register-form" class="d-none">
		    <h3 class="mt-4 text-center">REGISTER</h3>
		    <div class="input-group mb-4">
		        <span class="input-group-text"><i class="fa fa-user"></i></span>
		        <input type="text" id="register-username" class="form-control" placeholder="Username">
		    </div> 
            <div class="input-group mb-4">
		        <span class="input-group-text"><i class="fa fa-envelope"></i></span>
		        <input type="text" id="register-email" class="form-control" placeholder="Email ID">
		    </div> 
		    <div class="input-group mb-4">
		        <span class="input-group-text"><i class="fa fa-lock"></i></span>
		        <input type="password" id="register-password" class="form-control" placeholder="Password">
		    </div>
		    <div id="register-message" class="alert d-none"></div>
		    <button id="register-btn" class="btn btn-success w-100">
		        REGISTER
		    </button>
		</div>

    </div>
    <!-- Front Page End -->
    
    <!-- Chat Screen -->
    <div class="container p-2 bg-white rounded shadow-sm d-none" id="chat-screen" style="max-width: 400px;">
    
    <div class="bg-primary text-white py-2 px-2 d-flex justify-content-between align-items-center rounded-top">
            <span class="fs-5 fw-bold">LiveChat</span>   
            <!-- Dropdown Menu -->
            <div class="dropdown">
                <button class="btn btn-light btn-sm dropdown-toggle" type="button" id="menu-btn" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="fa-solid fa-bars"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="menu-btn">
                    <li><button id="logout-btn" class="dropdown-item text-danger fw-bold">LOGOUT</button></li>
                </ul>
            </div>
        </div>
    <div
  class="bg-primary text-center text-white"
  style={{
    backgroundColor: "#0066cc",
    padding: "15px",
    borderTopLeftRadius: "8px",
    borderTopRightRadius: "8px"
  }}
>
  <img
    src="https://meegaan.com/wp-content/uploads/2023/09/logo.png"
    alt="logo"
    style={{
      height: "40px",
      marginBottom: "5px"
    }}
  />
  <h5 class="mb-0 ">Hi All</h5>
  <p class="small mb-0">
    Meegaan Tech specializes in delivering software and OTT media solutions, offering
    cutting-edge technology for education and entertainment industries.
  </p>
</div>
    
        <!-- Chat Messages -->
        <div id="messages" class="bg-light"></div>
        <!-- Chat Input Field -->
        <div class="chat-input-container">
            <input type="text" id="chat-input" class="form-control me-2" placeholder="Type a message...">
            <button id="send-btn" class="btn btn-primary">
                <i class="fa-solid fa-paper-plane"></i>
            </button>
        </div>
    </div>
    <!-- Chat Screen End -->



    <script>
    const propertyId = "%s";
    const receiver="%s";
    const type="%s";
    
    let userToken = localStorage.getItem(`userToken_${propertyId}`);

    function showTab(tab, event) {
    document.getElementById("login-form").classList.add("d-none");
    document.getElementById("register-form").classList.add("d-none");

    document.querySelectorAll(".tab-btn").forEach(el => el.classList.remove("active"));
    event.target.classList.add("active");

    document.getElementById(tab + "-form").classList.remove("d-none");
}

    document.addEventListener("DOMContentLoaded", function () {
        let userToken = localStorage.getItem(`userToken_${propertyId}`);
        if (userToken) {
            showChatScreen();
            fetchMessages();
        }

        // Register Button Event
        document.getElementById("register-btn").addEventListener("click", function () {
            const username = document.getElementById("register-username").value.trim();
            const email = document.getElementById("register-email").value.trim();
            const password = document.getElementById("register-password").value.trim();

            if (!username || !password || !email) {
                displayMessage("register-message", "All fields are required!", "error");
                return;
            }

            fetch("http://localhost:8080/user/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username,email, password, propertyId })
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === "success") {
                    displayMessage("register-message", "Registration Successful!", "success");
                    clearForm("register-username","register-email", "register-password");
                } else {
                    displayMessage("register-message", data.message, "error");
                }
            })
            .catch(error => {
                displayMessage("register-message", "Registration failed!", "error");
                console.error("Error:", error);
            });
        });

        // Login Button Event
document.getElementById("login-btn").addEventListener("click", function () {
    const username = document.getElementById("login-username").value.trim();
    const password = document.getElementById("login-password").value.trim();

    if (!username || !password) {
        displayMessage("login-message", "All fields are required!", "error");
        return;
    }

    fetch("http://localhost:8080/user/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password, propertyId })
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === "success") {
            // Store user info in localStorage instead of token
            localStorage.setItem(`user_${propertyId}`, JSON.stringify({
                username: data.username,
                propertyId: data.propertyId
            }));
            displayMessage("login-message", "Login Successful!", "success");
            clearForm("login-username", "login-password");
            showChatScreen();
        } else {
            displayMessage("login-message", data.message || "Login failed!", "error");
        }
    })
    .catch(error => {
        displayMessage("login-message", "Login failed!", "error");
        console.error("Error:", error);
    });
});

        // Logout Button Event
        document.getElementById("logout-btn").addEventListener("click", function () {
            localStorage.removeItem(`userToken_${propertyId}`);
            location.reload();
        });

       // Chat Send Button Event
		document.getElementById("send-btn").addEventListener("click", async function () {
	    const authToken = localStorage.getItem(`userToken_${propertyId}`);
	    const message = document.getElementById("chat-input").value.trim();
	    const senderType = "user"; // ✅ Sender is always "user"
	    
	    if (!message) {
	        alert("Message cannot be empty.");
	        return;
	    }
	
	    try {
	        // Fetch chatbot type from local storage or API (if stored separately)
	        const chatbotType = type; 
	
	        const response = await fetch("http://localhost:8080/chat/send", {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json",
	                "Authorization": `Bearer ${authToken}`
	            },
	            body: JSON.stringify({ propertyId, message, receiver, senderType })
	        });
	
	        const data = await response.json();
	        console.log("Response Data:", data);
	
	        if (response.ok) {
	            document.getElementById("chat-input").value = ""; 
	            fetchMessages(); 
	        } else {
	            alert(`Error: ${data.message || "Failed to send message."}`);
	        }
	    } catch (error) {
	        console.error("Error sending message:", error);
	        alert("An error occurred while sending the message.");
	    }
	});

      
});

document.addEventListener("DOMContentLoaded", function () {
    let userToken = localStorage.getItem("userToken");
    if (userToken) {
        showChatScreen();
        fetchMessages(); // Fetch messages when chat screen loads
    }
});

// Function to fetch and display messages
async function fetchMessages() {
    try {
        let userToken = localStorage.getItem(`userToken_${propertyId}`);
        if (!userToken) {
            console.error("User is not authenticated.");
            return;
        }

        // Define API URLs
        const sentMessagesUrl = `http://localhost:8080/chat/${propertyId}/sent/${receiver}`;
        const receivedMessagesUrl = `http://localhost:8080/chat/${propertyId}/${receiver}/received`;

        // Fetch sent & received messages in parallel
        const [sentRes, receivedRes] = await Promise.all([
            fetch(sentMessagesUrl, { headers: { "Authorization": `Bearer ${userToken}` } }),
            fetch(receivedMessagesUrl, { headers: { "Authorization": `Bearer ${userToken}` } })
        ]);

        if (!sentRes.ok || !receivedRes.ok) {
            console.error("Failed to fetch messages.");
            return;
        }

        // Parse JSON response
        const sentData = await sentRes.json().catch(() => ({ chat: [] }));
        const receivedData = await receivedRes.json().catch(() => ({ chat: [] }));

        console.log("Full Received Data:", JSON.stringify(receivedData, null, 2));

        // Extract message arrays
        const sentMessages = sentData?.chat || [];
        const receivedMessages = receivedData?.chat || [];

        // Merge and sort messages by timestamp
        const allMessages = [...sentMessages, ...receivedMessages];
        allMessages.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

        // Update UI based on chat type
        if (type === "KNOWLEDGE_BASED") {
            renderKnowledgeBasedUI(allMessages);
        }else{
        updateChatUI(allMessages);
        }

    } catch (error) {
        console.error("Error fetching messages:", error);
    }
}

function renderKnowledgeBasedUI(messages) {
    const chatContainer = document.getElementById("chat-screen"); 
    const messagesContainer = document.getElementById("messages"); 

    if (!chatContainer || !messagesContainer) {
        console.error("Error: chat-screen or messages container not found in the DOM.");
        return;
    }

    messagesContainer.innerHTML = ""; // Clear only the messages, not the input or buttons

    messages.forEach(msg => {
        if (msg.message.startsWith("Here are some related questions")) {
            const questionText = msg.message.replace("Here are some related questions,", "").trim();
            const questions = questionText.split(",").map(q => q.trim());

            console.log("Extracted Questions:", questions);

            const questionContainer = document.createElement("div");
            questionContainer.classList.add("question-container");

            questions.forEach(question => {
                const questionButton = document.createElement("button");
                questionButton.innerText = question;
                questionButton.classList.add("question-button");

                console.log(`Question Clicked: ${question}, Property ID: ${propertyId}`);

                questionButton.onclick = () => fetchAnswer(propertyId, question);

                questionContainer.appendChild(questionButton);
            });

            messagesContainer.appendChild(questionContainer);
        } else {
            const messageDiv = document.createElement("div");
            messageDiv.classList.add("message");

            if (msg.sender === receiver) {
                messageDiv.classList.add("received");
            } else {
                messageDiv.classList.add("sent");
            }

            messageDiv.innerText = msg.message;
            messagesContainer.appendChild(messageDiv);
        }
    });

    chatContainer.scrollTop = chatContainer.scrollHeight;
}

// ✅ Function to Fetch Answer from Backend API
function fetchAnswer(propertyId, questionText) {
    const authToken = localStorage.getItem(`userToken_${propertyId}`); // Retrieve JWT token
    if (!authToken) {
        console.error("JWT Token not found");
        return;
    }

    console.log(`Fetching Answer for Question: "${questionText}", Property ID: ${propertyId}`);

    fetch("http://localhost:8080/questions/answer", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${authToken}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            propertyId: propertyId,
            question: questionText  // ✅ Corrected question variable
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === "error") {
            console.error("Error:", data.message);
            return;
        }

        // ✅ Log question and answer in console
        console.log("User Question:", questionText);
        console.log("Bot Answer:", data.answer);
        console.log("✅ Answer stored in chat. It will be displayed automatically when messages are fetched.");
    })
    .catch(error => console.error("Error fetching answer:", error));
}


// Function to update chat UI for normal messages
function updateChatUI(messages) {
    console.log("Updating chat UI with messages:", messages);

    const messagesContainer = document.getElementById("messages");
    messagesContainer.innerHTML = ""; // Clear previous messages

    messages.forEach(msg => {
        const msgElement = document.createElement("div");
        msgElement.classList.add("message");

        if (msg.sender === receiver) {
            msgElement.classList.add("received");
        } else {
            msgElement.classList.add("sent");
        }

        msgElement.textContent = msg.message;
        messagesContainer.appendChild(msgElement);
    });

    // Scroll to bottom after updating messages
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

    function displayMessage(elementId, message, type) {
    const msgElement = document.getElementById(elementId);
    if (msgElement) {
        msgElement.textContent = message;
        msgElement.className = `alert ${type === "success" ? "alert-success" : "alert-danger"}`;
        msgElement.classList.remove("d-none");
    }
}

    function clearForm(...ids) {
        ids.forEach(id => {
            const element = document.getElementById(id);
            if (element) element.value = "";
        });
    }

    function showChatScreen() {
        document.getElementById("auth-container").classList.add("d-none");
document.getElementById("chat-screen").classList.remove("d-none");
        fetchMessages();
        setInterval(fetchMessages, 5000);
    }
</script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

               """.formatted(propertyId,receiver,type);
    }
}
