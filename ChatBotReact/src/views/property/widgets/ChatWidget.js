import { bottom, right } from "@popperjs/core";
import React, { useState } from "react";

const WidgetForm = () => {
  const [propertyName, setPropertyName] = useState("");
  const [websiteUrl, setWebsiteUrl] = useState("");
  const [widgetColor, setWidgetColor] = useState("#61a1d1");

  const scriptText = `
<!-- Start of Tawk.to Script -->
<script type="text/javascript">
var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
(function(){
var s1=document.createElement("script"),s0=document.getElementsByTagName("script")[0];
s1.async=true;
s1.src='https://embed.tawk.to/60e872719bd1f31184d8f638/1f0q5sz5ru';
s1.charset='UTF-8';
s0.parentNode.insertBefore(s1,s0);
})();
</script>`;

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
          <button style={styles.Generate}>Generate</button>
        </div>

        <div className="col-md-6">
          <label style={styles.label}>Widget Code</label>
          <pre style={styles.codeBox}>{scriptText}</pre>
        </div>
      </div>
      <div></div>
      <hr ></hr>
      <div className="d-flex justify-content-end mt-4" style={styles.actionButtons}>   
        <button className="btn btn-light me-2">Cancel</button>
        <button className="btn" style={styles.Save}>Save</button>
      </div>
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
        fontWeight: "bold"
    },
    actionButtons: {
        position: "absolute",
        bottom: "12%",
        right   : "5%",
        
    },
  container: {
    background: "#f8f9fa",
    borderRadius: "8px",
    marginTop: "1px !important",
  },
  title: {
    borderBottom: "1px solid #ccc",
    paddingBottom: "10px"
  },
  label: {
    fontWeight: "500",
    marginBottom: "5px",
    display: "block"
  },
  colorBoxContainer: {
    display: "flex",
    alignItems: "center",
    gap: "10px",
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    backgroundColor: "#fff"
  },
  colorBox: {
    width: "40px",
    height: "40px",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer"
  },
  colorHex: {
    fontSize: "14px",
    color: "#333"
  },
  codeBox: {
    backgroundColor: "#e6f0ec",
    border: "1px solid #ccc",
    borderRadius: "8px",
    padding: "10px",
    height: "200px",
    whiteSpace: "pre-wrap",
    fontSize: "12px",
    color: "#333"
  },
    Generate: {
        backgroundColor: "#74b047",
        color: "#fff",
        padding: "8px 20px",
        border: "none",
        borderRadius: "6px",
        cursor: "pointer",
        fontSize: "14px",
        fontWeight: "bold"
    }
};

export default WidgetForm;
