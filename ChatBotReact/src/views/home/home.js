import React, { useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import chat3d from '../home/chat3d.jpg'; // Assuming you have a CSS file for additional styles

const HomeScreen = () => {
  useEffect(() => {
    const style = document.createElement('style');
    style.innerHTML = `
      .home-wrapper {
        background-size: cover;
        padding: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .glass-card {
        background: rgba(255, 255, 255, 0.1);
        border-radius: 20px;
        backdrop-filter: blur(10px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        max-width: 600px;
        text-align: center;
        padding: 40px;
        animation: fadeInUp 1s ease;
        transition: transform 0.3s ease-in-out;
      }

      .glass-card:hover {
        transform: scale(1.02);
      }

      .chatbot-3d {
        perspective: 1000px;
      }

      .chatbot-img {
        width: 150px;
        transform: rotateY(0deg);
        animation: rotate3d 6s infinite linear;
      }

      @keyframes rotate3d {
        0% { transform: rotateY(0deg); }
        100% { transform: rotateY(360deg); }
      }

      @keyframes fadeInUp {
        from {
          opacity: 0;
          transform: translateY(40px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }
    `;
    document.head.appendChild(style);
    return () => {
      document.head.removeChild(style);
    };
  }, []);

  return (
    <div className="home-wrapper">
      <div className="glass-card">
        <h1 className="display-4 fw-bold mb-3">Welcome to ChatGenie 🤖</h1>
        <p className="lead mb-4">
          Your smart 24/7 chatbot assistant for instant support and conversations.
        </p>
        <div className="chatbot-3d">
          <img src={chat3d} alt="Chatbot 3D" className="img-fluid chatbot-img" />
        </div>
        <a href="/chat" className="btn btn-primary btn-lg mt-4 shadow">
          Start Chatting
        </a>
      </div>
    </div>
  );
};

export default HomeScreen;
