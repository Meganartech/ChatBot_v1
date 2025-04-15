import React, { useState } from "react";

const scriptText = `
  <!-- Start of Tawk.to Script -->
  <script type="text/javascript">
  var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
  (function(){
  var s1=document.createElement("script"),s0=document.getElementsByTagName("script")[0];
  s1.async=true;
  s1.src='https://embed.tawk.to/61ee87719bd1f31184d8f638/1fq5s25ru';
  s1.charset='UTF-8';
  s1.setAttribute('crossorigin','*');
  s0.parentNode.insertBefore(s1,s0);
  })();
  </script>
  <!-- End of Tawk.to Script -->
`;

const WidgetBody = () => {
  const [propertyName, setPropertyName] = useState("");
  const [propertyURL, setPropertyURL] = useState("");
  const [status, setStatus] = useState(true);
  const [propertyImage, setPropertyImage] = useState(null);

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      setPropertyImage(URL.createObjectURL(file));
    }
  };

  const styles = {
    content: {
      flexGrow: 1,
      padding: "30px",
      background: "#fff",
      borderLeft: "1px solid #ccc",
      maxWidth: "80%",
      maxHeight: "100%",
      backgroundColor: "#f8f9fa",
    },
    contentHeader: {
      fontSize: "18px",
      paddingBottom: "10px",
      marginBottom: "30px",
      borderBottom: "1px solid #ccc"
    },
    form: {
      display: "flex",
      flexDirection: "column",
      gap: "15px",
    },
    
    formGroupUpload: {
      alignItems: "center",
    },
    formGroup: {
      alignItems: "center",
      gap: "10px",
    },
    formLabel: {
      width: "150px",
      height: "30px", 
      fontWeight: "bold"
    },
    formInput: {
      flexGrow: 1,
      padding: "8px",
      height: "30px",
      border: "1px solid #ccc",
      borderRadius: "5px"
    },
    toggleContainer: {
      display: "flex",
      backgroundColor: "#f3f4f7",
      border: "1px solid #ccc",
      borderRadius: "5px",
      alignItems: "center",
      gap: "10px"
    },
    switch: {
      position: "relative",
      display: "inline-block",
      width: "34px",
      marginRight: "10px",
      height: "20px"
    },
    slider: {
      position: "absolute",
      cursor: "pointer",
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: "#ccc",
      transition: "0.4s",
      borderRadius: "10px"
    },
    sliderBefore: {
      position: "absolute",
      content: '""',
      height: "14px",
      width: "14px",
      left: "3px",
      bottom: "3px",
      backgroundColor: "white",
      transition: "0.4s",
      borderRadius: "50%"
    },
    uploadBox: {
      width: "120px",
      height: "120px",
      border: "2px dashed #ccc",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      cursor: "pointer",
      position: "relative"
    },
    uploadInput: {
      position: "absolute",
      width: "100%",
      height: "100%",
      opacity: 0,
      cursor: "pointer"
    },
    actionButtons: {
      display: "flex",
      justifyContent: "flex-end",
      gap: "10px",
      marginTop: "20px"
    },
    cancelButton: {
      background: "white",
      color:"#6200ea",
      border:"1px solid black",
      borderRadius: "5px",
      width: "100px",
      width: "100px",
    },
    saveButton: {
      color:"white",
      backgroundColor: "#6200ea",
      border:"1px solid black",
      borderRadius: "5px",
      width: "100px",
      

    },
    arrangeContainer: {
      display: "flex",
      justifyContent: "space-around",
      alignItems: "flex-start",
      gap: "30px",
      width: "100%",
      height: "75%",
      overflow: "hidden"
    },
    channelLinkText: {
      display: "flex",
      flexDirection: "column",
      alignItems: "flex-start",
      overflowY: "auto",
      maxHeight: "85%",
      maxWidth: "100%"
    },
    channelLink: {
      flex: 1,
      display: "flex",
      flexDirection: "column",
      alignItems: "flex-start",
      background: "#f8f9fa",
      border: "1px solid #ccc",
      borderRadius: "5px",
      overflowY: "auto",
      maxHeight: "85%",
      maxWidth: "100%"
    },
    channelLinkHeader: {
      fontSize: "18px",
      marginLeft: "10px", 
    },
    tawktoCode: {
      backgroundColor: "#f8f9fa",
      border: "1px solid #ccc",
      padding: "10px",
      borderRadius: "5px",
      fontSize: "10px",
      color: "#333"
    },
    Active: {
      display: "flex",
      marginLeft: "10px",
      justifyContent:"space-between",
      width: "100%",  

    }
  };

  return (
    <div style={styles.content}>
      <h2 style={styles.contentHeader}>Overview</h2>
      <div style={styles.arrangeContainer}>
        <div style={styles.form}>
          {/* Property Name */}
          <div style={styles.formGroup}>
            <label style={styles.formLabel}>Property Name</label>
            <input
              type="text"
              value={propertyName}
              onChange={(e) => setPropertyName(e.target.value)}
              style={styles.formInput}
            />
            <button disabled={!propertyName}>Save</button>
          </div>

          {/* Status Toggle */}
          <div style={styles.formGroup}>
            <label style={styles.formLabel}>Status</label>
            <div style={styles.toggleContainer}>
              
              <span style={styles.Active}>{status ? "Active" : "Inactive"}</span>
              <label style={styles.switch}>
                <input
                  type="checkbox"
                  checked={status}
                  onChange={() => setStatus(!status)}
                  style={{ opacity: 0, width: 0, height: 0 }}
                ></input>
                <span style={{
                  ...styles.slider,
                  backgroundColor: status ? "#6200ea" : "#ccc"
                }}>
                  <span style={{
                    ...styles.sliderBefore,
                    transform: status ? "translateX(14px)" : "none"
                  }} />
                </span>
              </label>
            </div>
          </div>

          {/* Property URL */}
          <div style={styles.formGroup}>
            <label style={styles.formLabel}>Property URL</label>
            <input
              type="text"
              value={propertyURL}
              onChange={(e) => setPropertyURL(e.target.value)}
              style={styles.formInput}
            />
            <button disabled={!propertyURL}>Save</button>
          </div>

          {/* Property Image Upload */}
          
        </div>
        <div style={styles.formGroupUpload}>
        <label style={styles.formLabel}>Property Image</label>
            <div style={styles.uploadBox}>
              {propertyImage ? (
                <img src={propertyImage} alt="Uploaded Preview" style={{ width: "100%", height: "100%", objectFit: "cover" }} />
              ) : (
                <p>Upload</p>
              )}
              <input 
                type="file" 
                accept="image/*" 
                onChange={handleFileUpload}
                style={styles.uploadInput}
              />
            </div>
            <button disabled={!propertyImage}>Save</button>
          </div>
        {/* <div style={styles.channelLinkText}>
        <h2 style={styles.channelLinkHeader}>Widget Code</h2>
        <div style={styles.channelLink}>
          <div>
            <p style={styles.tawktoCode}>
              <code dangerouslySetInnerHTML={{ __html: scriptText.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g, "<br>") }} />
            </p>
          </div>
        </div>
      </div>*/}
      </div> 

      {/* Bottom Action Buttons */}
      <div style={styles.actionButtons}>
        <button style={styles.cancelButton}>Cancel</button>
        <button style={styles.saveButton}>Save</button>
      </div>
    </div>
  );
};

export default WidgetBody;