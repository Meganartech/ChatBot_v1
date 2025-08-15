// import React, { useEffect, useRef, useState } from "react";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faDatabase, faSearch, faUser } from "@fortawesome/free-solid-svg-icons";
// import API_URL from "../../../Config"; // Ensure this path is correct
// import { Client } from '@stomp/stompjs'
// import SockJS from 'sockjs-client'

// const ChatUsers = ({ users = [], selectedUser, onSelect, hasProperty, setSelectedChat }) => {
//   const [incomingChats, setIncomingChats] = useState([]);
//   const [allChats, setAllChats] = useState([]);

//   const stompClientRef = useRef(null)
//   const adminEmailRef = useRef(null)

//   useEffect(() => {
//     // Initial load
//     fetch(`${API_URL}/sessions?receiverEmail=${sessionStorage.getItem('email')}`)
//       .then((res) => res.json())
//       .then((data) => setIncomingChats(data.filter((d) => d.status === false)))
//       .catch((error) => console.error("Error fetching incoming chats:", error));

//     // Optional: load all chats if needed
//     // fetch(`${API_URL}/sessionlist?receiverEmail=${sessionStorage.getItem('email')}`)
//     //   .then((res) => res.json())
//     //   .then((data) => setAllChats(data))
//     //   .catch((error) => console.error("Error fetching all chats:", error));

//     adminEmailRef.current = sessionStorage.getItem('email')
//     const client = new Client({
//       webSocketFactory: () => new SockJS(`${API_URL}/chat`),
//       onConnect: () => {
//         // targeted
//         client.subscribe(`/topic/sessions/${adminEmailRef.current}`, (msg) => {
//           try {
//             const data = JSON.parse(msg.body)
//             setIncomingChats((prev) => {
//               if (prev.find((r) => r.sessionId === data.sessionId)) return prev
//               return [data, ...prev]
//             })
//           } catch (_) {}
//         })
//         // fallback generic
//         client.subscribe('/topic/sessions', (msg) => {
//           try {
//             const data = JSON.parse(msg.body)
//             if (data?.adminemail && data.adminemail !== adminEmailRef.current) return
//             setIncomingChats((prev) => {
//               if (prev.find((r) => r.sessionId === data.sessionId)) return prev
//               return [data, ...prev]
//             })
//           } catch (_) {}
//         })
//       },
//     })
//     client.activate()
//     stompClientRef.current = client
//     return () => {
//       try { client.deactivate() } catch (_) {}
//       stompClientRef.current = null
//     }
//   }, []);

//   return (
//     <div style={styles.chatList}>
//       <div style={styles.chatListHeader}>
//         <h4 style={styles.chatListTitle}>Users</h4>
//         <button style={styles.iconButton}>
//           <FontAwesomeIcon icon={faDatabase} style={styles.headerIcon} />
//         </button>
//       </div>

//       <div style={styles.searchBar}>
//         <FontAwesomeIcon icon={faSearch} style={styles.searchIcon} />
//         <input type="text" placeholder="Search..." style={styles.searchInput} />
//       </div>

//       <div style={styles.chatTabs}>
//         <button style={{ ...styles.tabButton, ...styles.activeTab }}>All</button>
//         <button style={styles.tabButton}>Mine</button>
//       </div>
//       <h6 style={{ marginTop: "20px" }}>Incoming Chats</h6>
//       {incomingChats.length === 0 ? (
//         <p>No incoming chats</p>
//       ) : (
//         incomingChats.map((chat) => (
//           <div key={chat.sessionId || chat.id} style={styles.chatItem} onClick={() => setSelectedChat(chat)}>
//             <FontAwesomeIcon icon={faUser} style={styles.chatAvatar} />
//             <div>
//               <strong>{chat.username || chat.name}</strong>
//               <p>{chat.message || 'New chat'}</p>
//             </div>
//             <span style={styles.chatTime}>{chat.timestamp ? new Date(chat.timestamp).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'}) : ''}</span>
//           </div>
//         ))
//       )}
//       <h6 style={{ marginTop: "20px" }}>All Chats</h6>
      
//       {hasProperty ? (
//         <ul style={{ listStyle: "none", padding: 0 }}>
//           {users.map((user) => (
//             <li
//               key={user.username}
//               style={{
//                 ...styles.chatItem,
//                 backgroundColor:
//                   selectedUser?.username === user.username ? "#d4edda" : "transparent",
//               }}
//               onClick={() => onSelect(user)}
//             >
//               <FontAwesomeIcon icon={faUser} style={styles.chatAvatar} />
//               <div>
//                 <strong>{user.username}</strong>
//               </div>
//             </li>
//           ))}
//         </ul>
//       ) : (
//         <p className="text-gray-500">Select a property first</p>
//       )}

      
//     </div>
//   );
// };

// export default ChatUsers;

// const styles = {
//   chatList: {
//     width: "300px",
//     padding: "15px",
//     background: "#F8FAFF",
//     borderRight: "1px solid #dee2e6",
//     overflowY: "auto",
//     height: "550",
//   },
//   chatListHeader: {
//     display: "flex",
//     justifyContent: "space-between",
//     alignItems: "center",
//   },
//   chatListTitle: {
//     fontSize: "20px",
//     marginTop: "5px",
//     color: "black",
//   },
//   iconButton: {
//     background: "none",
//     border: "none",
//     cursor: "pointer",
//   },
//   headerIcon: {
//     fontSize: "20px",
//     color: "black",
//   },
//   searchBar: {
//     marginTop: "20px",
//     display: "flex",
//     alignItems: "center",
//     background: "#f1f3f5",
//     padding: "5px",
//     height: "40px",
//     borderRadius: "30px",
//     marginBottom: "10px",
//   },
//   searchInput: {
//     border: "none",
//     outline: "none",
//     background: "transparent",
//     width: "100%",
//     padding: "5px",
//   },
//   searchIcon: {
//     marginRight: "5px",
//     color: "gray",
//   },
//   chatTabs: {
//     display: "flex",
//     justifyContent: "space-around",
//     padding: "10px 0",
//     borderBottom: "2px solid #dee2e6",
//   },
//   tabButton: {
//     fontWeight: "bold",
//     border: "2px solid transparent",
//     backgroundColor: "white",
//     color: "black",
//     padding: "5px 10px",
//     cursor: "pointer",
//   },
//   activeTab: {
//     borderBottom: "2px solid blue",
//   },
//   chatItem: {
//     display: "flex",
//     alignItems: "center",
//     gap: "10px",
//     padding: "10px",
//     cursor: "pointer",
//     borderBottom: "1px solid #e9ecef",
//     transition: "background 0.3s",
//   },
//   chatAvatar: {
//     fontSize: "24px",
//     color: "#0162C4",
//   },
//   chatTime: {
//     marginLeft: "auto",
//     fontSize: "12px",
//     color: "gray",
//   },
// };
