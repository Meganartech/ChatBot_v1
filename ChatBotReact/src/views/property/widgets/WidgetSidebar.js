import React, { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTableColumns,
  faSliders,
  faGear,
  faUserGear,
  faCommentAlt,
  faImage,
} from "@fortawesome/free-solid-svg-icons";

const WidgetSidebar = () => {
  const [isChannelsOpen, setIsChannelsOpen] = useState(false);

  const styles = {
    sidebar: {
      width: "240px",
      background: "#fff",
      padding: "20px 10px",
      height: "75vh",
      borderRight: "1px solid #eee",
      fontFamily: "sans-serif",
    },
    header: {
      fontSize: "14px",
      fontWeight: "600",
      marginBottom: "20px",
      paddingLeft: "10px",
    },
    button: {
      display: "flex",
      alignItems: "center",
      padding: "10px",
      width: "100%",
      background: "transparent",
      border: "none",
      borderRadius: "8px",
      cursor: "pointer",
      fontSize: "14px",
      fontWeight: "500",
      color: "#111",
      marginBottom: "5px",
      justifyContent: "space-between",
    },
    leftContent: {
      display: "flex",
      alignItems: "center",
      gap: "10px",
    },
    active: {
      backgroundColor: "#f2f3f5",
    },
    arrow: {
      fontSize: "14px",
      color: "#888",
    },
    dropdown: {
      paddingLeft: "32px",
      display: "flex",
      flexDirection: "column",
      gap: "5px",
      marginBottom: "10px",
    },
    subButton: {
      display: "flex",
      alignItems: "center",
      gap: "8px",
      fontSize: "13px",
      color: "#333",
      background: "transparent",
      border: "none",
      textAlign: "left",
      cursor: "pointer",
      padding: "6px 0",
    },
  };

  const MenuButton = ({ icon, label, hasArrow, active, onClick, isOpen }) => (
    <button
      onClick={onClick}
      style={{ ...styles.button, ...(active ? styles.active : {}) }}
    >
      <div style={styles.leftContent}>
        <FontAwesomeIcon icon={icon} />
        {label}
      </div>
      {hasArrow && (
        <span style={styles.arrow}>{isOpen ? "˅" : "›"}</span>
      )}
    </button>
  );

  return (
    <div style={styles.sidebar}>
      <div style={styles.header}>Administration</div>

      <MenuButton icon={faTableColumns} label="Overview" active />

      <MenuButton
        icon={faSliders}
        label="Channels"
        hasArrow
        isOpen={isChannelsOpen}
        onClick={() => setIsChannelsOpen(!isChannelsOpen)}
      />
      {isChannelsOpen && (
        <div style={styles.dropdown}>
          <button style={styles.subButton} >
            <FontAwesomeIcon icon={faCommentAlt} />
            Chat Widget
          </button>
          <button style={styles.subButton}>
            <FontAwesomeIcon icon={faImage} />
            Widget Content
          </button>
        </div>
      )}

      <MenuButton icon={faGear} label="Settings" hasArrow />
      <MenuButton icon={faUserGear} label="User Management" hasArrow />
    </div>
  );
};

export default WidgetSidebar;
