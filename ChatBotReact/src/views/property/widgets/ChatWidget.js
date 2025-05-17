import React, { useState, useEffect } from "react";
import {
  CCard,
  CCardHeader,
  CCardBody,
  CContainer,
  CRow,
  CCol,
  CButton,
  CForm,
  CFormLabel,
  CFormInput,
} from "@coreui/react";

const WidgetForm = () => {
  const [propertyName, setPropertyName] = useState("");
  const [websiteUrl, setWebsiteUrl] = useState("");
  const [widgetColor, setWidgetColor] = useState("#61a1d1");
  const [scriptText, setScriptText] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [copySuccess, setCopySuccess] = useState(false);

  const isValidURL = (url) => /^http\:\/\/[^"']+$/.test(url);

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

  const cancelChanges = () => {
    setPropertyName("");
    setWebsiteUrl("");
    setWidgetColor("#61a1d1");
    setScriptText("");
    setErrorMessage("");
  };

  return (
    <CContainer fluid className="d-flex flex-column flex-grow-1" style={styles.container}>
      <CCard>
        <CCardHeader style={styles.cardHeader}>
          <h4 className="mb-0">Channels</h4>
        </CCardHeader>
        <CCardBody className="d-flex flex-column flex-grow-1" style={styles.cbody}>
          <CRow className="mt-4 flex-grow-1">
            {/* Left Column: Form Inputs */}
            <CCol md={6}>
              <CForm>
                <div className="mb-4">
                  <CFormLabel style={styles.label}>Property Name</CFormLabel>
                  <CFormInput
                    type="text"
                    value={propertyName}
                    onChange={(e) => setPropertyName(e.target.value)}
                    aria-label="Property Name"
                  />
                </div>
                <div className="mb-4">
                  <CFormLabel style={styles.label}>Website URL</CFormLabel>
                  <CFormInput
                    type="text"
                    value={websiteUrl}
                    onChange={(e) => setWebsiteUrl(e.target.value)}
                    aria-label="Website URL"
                  />
                </div>
                <div className="mb-4">
                  <CFormLabel style={styles.label}>Widget Color</CFormLabel>
                  <div style={styles.colorBoxContainer}>
                    <CFormInput
                      type="color"
                      value={widgetColor}
                      onChange={(e) => setWidgetColor(e.target.value)}
                      style={styles.colorBox}
                      aria-label="Widget Color"
                    />
                    <span style={styles.colorHex}>{widgetColor}</span>
                  </div>
                </div>
                {errorMessage && <p className="text-danger">{errorMessage}</p>}
                <CButton
                  color="success"
                  onClick={generateWidget}
                  disabled={loading}
                  style={styles.generate}
                  aria-label={loading ? "Generating widget" : "Generate widget"}
                >
                  {loading ? "Generating..." : "Generate"}
                </CButton>
              </CForm>
            </CCol>

            {/* Right Column: Widget Code */}
            <CCol md={6} className="d-none d-md-block">
              <CFormLabel style={styles.label}>Widget Code</CFormLabel>
              <div style={{ position: "relative" }}>
                <pre style={styles.codeBox}>{scriptText}</pre>
                {scriptText && (
                  <CButton
                    color="secondary"
                    size="sm"
                    onClick={copyToClipboard}
                    style={styles.copyButton}
                    aria-label="Copy widget code to clipboard"
                  >
                    Copy
                  </CButton>
                )}
              </div>
            </CCol>
          </CRow>

          <div className="d-flex justify-content-end mt-4" style={styles.buttonGroup}>
            <CButton
              color="light"
              className="me-2"
              onClick={cancelChanges}
              aria-label="Cancel changes"
            >
              Cancel
            </CButton>
            <CButton
              color="primary"
              onClick={() => alert("Save functionality not implemented")}
              aria-label="Save changes"
            >
              Save
            </CButton>
          </div>
        </CCardBody>
      </CCard>

      {/* Copy Notification */}
      {copySuccess && (
        <div style={styles.copyNotification}>
          Copied to clipboard!
        </div>
      )}
    </CContainer>
  );
};

const styles = {
  
  container: {
    padding: "inherit",
  },
  cardHeader: {
    position: "sticky",
    top: "0",
    backgroundColor: "#f3f4f7",
    borderBottom: "1px solid #dee2e6",
    zIndex: 10,
  },
  cbody: {
    minHeight: "70vh",
    backgroundColor: "#f8f9fa",
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
  generate: {
    backgroundColor: "#74b047",
    color: "#fff",
    padding: "8px 20px",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "bold",
  },
  copyButton: {
    position: "absolute",
    top: "10px",
    right: "10px",
  },
  buttonGroup: {
    position: "sticky",
    bottom: "20px",
    zIndex: 10,
    backgroundColor: "#f8f9fa",
    padding: "10px",
    borderTop: "1px solid #dee2e6",
  },
  copyNotification: {
    position: "fixed",
    bottom: "20px",
    right: "20px",
    backgroundColor: "#28a745",
    color: "#fff",
    padding: "10px 20px",
    borderRadius: "6px",
    zIndex: 1050,
    boxShadow: "0 2px 10px rgba(0,0,0,0.2)",
  },
};

export default WidgetForm;
