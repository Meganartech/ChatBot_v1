import React, { useState } from "react";
import Logo from "./Logo.png";
import Bot from "./Bot.png";
const WidgetContent = () => {
  const [language, setLanguage] = useState("English");
  const [cards, setCards] = useState([
    { id: 1, type: "Logo", enabled: true },
    { id: 2, type: "Heading", enabled: false },
    { id: 3, type: "Text Area", enabled: true }
  ]);

  const toggleCard = (id) => {
    setCards((prev) =>
      prev.map((card) =>
        card.id === id ? { ...card, enabled: !card.enabled } : card
      )
    );
  };

  return (
    <div style={styles.wrapper}>
      <div className="container px-4 py-3 d-flex flex-column flex-grow-1" style={styles.container}>
        <h4 style={styles.title}>Channels</h4>
        <div className="row mt-4 flex-grow-1">
          {/* Left Column */}
          <div className="col-md-6">
            <h6 style={styles.sectionTitle}>WIDGET CONTENT</h6>
            <div className="form-group mb-4">
              <label style={styles.label}>Language</label>
              <select
                className="form-select"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
              >
                <option>English</option>
                <option>Spanish</option>
                <option>French</option>
              </select>
            </div>

            <div style={styles.headerRow} className="d-flex justify-content-between align-items-center mb-2">
              <label style={styles.label}>Header Cards</label>
              <button className="btn btn-light btn-sm">+ Add</button>
            </div>

            {cards.map((card) => (
              <div
                key={card.id}
                className="d-flex align-items-center justify-content-between p-2 mb-2"
                style={styles.cardBox}
              >
                <div className="d-flex align-items-center gap-2">
                  <span style={styles.dragIcon}>⋮⋮</span>
                  <span>{card.type}</span>
                </div>
                <div className="d-flex align-items-center gap-2">
                  <button className="btn btn-light btn-sm">
                    {card.type === "Heading" ? "+" : "✎"}
                  </button>
                  <div className="form-check form-switch m-0">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      checked={card.enabled}
                      onChange={() => toggleCard(card.id)}
                      style={styles.toggle}
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Right Column */}
          <div className="col-md-6 d-flex flex-column align-items-center">
            <label style={styles.label}>Widget Preview</label>
            <div style={styles.PreviewBackground}>
            <div style={styles.previewBox}>
              <div style={styles.previewHeader}>
                <img
                  src={{Logo}}
                  alt="logo"
                  style={{ height: "40px", marginBottom: "5px" }}
                />
                <h5 className="text-white mb-0">Hi All</h5>
                <p className="text-white small mb-0">
                  Meegaan Tech specializes in delivering software and OTT media solutions, offering
                  cutting-edge technology for education and entertainment industries.
                </p>
              </div>
              <div style={styles.previewBody}></div>
              <img
                src={{Bot}}
                alt="bot"
                style={styles.chatIcon}
              />
            </div>
            </div>
          </div>
        </div>

        <div className="d-flex justify-content-end mt-4">
          <button className="btn btn-light me-2">Cancel</button>
          <button className="btn btn-primary">Save</button>
        </div>
      </div>
    </div>
  );
};

const styles = {
  PreviewBackground: {
    backgroundColor: "white",
    border: "1px solid #dee2e6",
    borderRadius: "8px",
    maxWidth: "950px",
    flexGrow: 1,
    height: "100%",
    display: "flex",
    justifyContent: "center",
    alignItems: "center"
  },
  wrapper: {
    display: "flex",
    flexDirection: "column"
  },
  container: {
    background: "#f8f9fa",
    border: "1px solid #dee2e6",
    borderRadius: "8px",
    maxWidth: "950px",
    flexGrow: 1,
    height: "100%"
  },
  title: {
    borderBottom: "1px solid #ccc",
    paddingBottom: "10px"
  },
  sectionTitle: {
    fontWeight: "600",
    fontSize: "14px",
    color: "#333",
    marginBottom: "10px"
  },
  label: {
    fontWeight: "500",
    display: "block",
    marginBottom: "5px"
  },
  cardBox: {
    backgroundColor: "#fff",
    border: "1px solid #ccc",
    borderRadius: "6px",
    paddingLeft: "10px",
    paddingRight: "10px"
  },
  dragIcon: {
    fontWeight: "bold",
    fontSize: "16px",
    cursor: "grab"
  },
  toggle: {
    backgroundColor: "#6f42c1"
  },
  headerRow: {
    borderTop: "1px solid #ddd",
    paddingTop: "10px",
    marginTop: "20px"
  },
  previewBox: {
    border: "1px solid #ccc",
    borderRadius: "8px",
    backgroundColor: "#fff",
    boxShadow: "2px 2px 10px rgba(0,0,0,0.1)",
    position: "relative",
    height: "90%",
    width: "90%",
  },
  previewHeader: {
    backgroundColor: "#0066cc",
    padding: "15px",
    borderTopLeftRadius: "8px",
    borderTopRightRadius: "8px",
    textAlign: "center"
  },
  previewBody: {
    height: "150px"
  },
  chatIcon: {
    position: "absolute",
    bottom: "10px",
    right: "10px",
    width: "35px"
  }
};

export default WidgetContent;
