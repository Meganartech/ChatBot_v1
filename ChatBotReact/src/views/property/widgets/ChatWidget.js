import React, { useState, useEffect } from "react";

const WidgetForm = () => {
  const [propertyName, setPropertyName] = useState("");
  const [websiteUrl, setWebsiteUrl] = useState("");
  const [widgetColor, setWidgetColor] = useState("#61a1d1");
  const [scriptText, setScriptText] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [copySuccess, setCopySuccess] = useState(false);

  const isValidURL = (url) => /^http:\/\/[^"']+$/.test(url);

  const generateWidget = async () => {
    setErrorMessage("");
    setScriptText("");
  
    if (!propertyName || !websiteUrl) {
      setErrorMessage("Property Name and Website URL required.");
      return;
    }
  
    if (!isValidURL(websiteUrl)) {
      setErrorMessage("Please enter a valid Website URL starting with http://");
      return;
    }
  
    setLoading(true);
  
    try {
      const token = localStorage.getItem("token");
      const formData = new FormData();
      formData.append("propertyName", propertyName);
      formData.append("websiteURL", websiteUrl);
      formData.append("buttonColor", widgetColor);
      // Note: No imageFile is being appended here
  
      const response = await fetch("http://localhost:8080/properties/add", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });
  
      const data = await response.json();
      setLoading(false);
  
      if (response.ok) {
        setScriptText(data.widgetScript);
      } else {
        setErrorMessage(data.message || "Something went wrong!");
      }
    } catch (error) {
      setLoading(false);
      setErrorMessage("Failed to generate widget. Try again later.");
    }
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(scriptText).then(() => {
      setCopySuccess(true);
      setTimeout(() => setCopySuccess(false), 2000);
    });
  };

  return (
    <div className="container px-4 py-3" style={styles.container}>
      <h4 style={styles.title}>Channels</h4>
      <div className="row mt-4">
        <div className="col-md-6">
          <div className="form-group mb-4">
            <label style={styles.label}>Property Name</label>
            <input
              type="text"
              className="form-control"
              value={propertyName}
              onChange={(e) => setPropertyName(e.target.value)}
            />
          </div>
          <div className="form-group mb-4">
            <label style={styles.label}>Website URL</label>
            <input
              type="text"
              className="form-control"
              value={websiteUrl}
              onChange={(e) => setWebsiteUrl(e.target.value)}
            />
          </div>
          <div className="form-group mb-4">
            <label style={styles.label}>Widget Color</label>
            <div style={styles.colorBoxContainer}>
              <input
                type="color"
                value={widgetColor}
                onChange={(e) => setWidgetColor(e.target.value)}
                style={styles.colorBox}
              />
              <span style={styles.colorHex}>{widgetColor}</span>
            </div>
          </div>

          {errorMessage && <p className="text-danger">{errorMessage}</p>}

          <button style={styles.Generate} onClick={generateWidget} disabled={loading}>
            {loading ? "Generating..." : "Generate"}
          </button>
        </div>

        <div className="col-md-6">
          <label style={styles.label}>Widget Code</label>
          <div className="position-relative">
            <pre style={styles.codeBox}>{scriptText}</pre>
            {scriptText && (
              <button
                onClick={copyToClipboard}
                className="btn btn-sm btn-secondary position-absolute top-0 end-0 m-2"
              >
                Copy
              </button>
            )}
          </div>
        </div>
      </div>
      <hr />
      <div className="d-flex justify-content-end mt-4" style={styles.actionButtons}>
        <button className="btn btn-light me-2">Cancel</button>
        <button className="btn" style={styles.Save}>Save</button>
      </div>

      {/* Copy Notification */}
      {copySuccess && (
        <div
          className="position-fixed bottom-0 end-0 m-4 bg-success text-white px-4 py-2 rounded shadow"
          style={{ zIndex: 1050 }}
        >
          Copied to clipboard!
        </div>
      )}
    </div>
  );
};

const styles = {
  Save: {
    backgroundColor: "#5856d6",
    color: "#fff",
    padding: "5px 20px",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "bold",
  },
  actionButtons: {
    position: "absolute",
    bottom: "12%",
    right: "5%",
  },
  container: {
    background: "#f8f9fa",
    borderRadius: "8px",
    marginTop: "1px !important",
  },
  title: {
    borderBottom: "1px solid #ccc",
    paddingBottom: "10px",
  },
  label: {
    fontWeight: "500",
    marginBottom: "5px",
    display: "block",
  },
  colorBoxContainer: {
    display: "flex",
    alignItems: "center",
    gap: "10px",
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    backgroundColor: "#fff",
  },
  colorBox: {
    width: "40px",
    height: "40px",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
  },
  colorHex: {
    fontSize: "14px",
    color: "#333",
  },
  codeBox: {
    backgroundColor: "#e6f0ec",
    border: "1px solid #ccc",
    borderRadius: "8px",
    padding: "10px",
    height: "200px",
    whiteSpace: "pre-wrap",
    fontSize: "12px",
    color: "#333",
    overflow: "auto",
  },
  Generate: {
    backgroundColor: "#74b047",
    color: "#fff",
    padding: "8px 20px",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "bold",
  },
};

export default WidgetForm;
