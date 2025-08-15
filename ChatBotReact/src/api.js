// api.js
import API_URL from './config';
export const getMessages = async () => {
    try {
      const response = await fetch(`${API_URL}/chat/messages`);
      const data = await response.json();
      return data;
    } catch (error) {
      console.error("Error fetching messages:", error);
    }
  };
  
  export const sendMessage = async (message) => {
    try {
      await fetch(`${API_URL}/chat/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message, sender: 'Admin' }), // Set sender as 'admin'
      });
    } catch (error) {
      console.error("Error sending message:", error);
    }
  };
  