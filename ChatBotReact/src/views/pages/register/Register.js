import React, { useState } from "react";
import {
  CButton,
  CCard,
  CCardBody,
  CCol,
  CContainer,
  CForm,
  CFormInput,
  CInputGroup,
  CInputGroupText,
  CRow,
  CFormSelect,
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { cilLockLocked, cilUser, cilEnvelopeClosed } from "@coreui/icons";
import { toast } from "react-toastify";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "react-toastify/dist/ReactToastify.css";

const Register = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [chatbotType, setChatbotType] = useState("");
  const [otp, setOtp] = useState("");
  const [showOtpModal, setShowOtpModal] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [isResending, setIsResending] = useState(false);

  const chatbotOptions = [
    { value: "", label: "Select Chatbot Type" },
    { value: "ONE_TO_ONE", label: "One-to-One Chat" },
    { value: "KNOWLEDGE_BASED", label: "Knowledge-Based Chat" },
    { value: "AI_CHAT", label: "AI Chat" },
  ];

  const navigate = useNavigate();

  const handleRegister = async () => {
    if (!name || !email || !password || !repeatPassword) {
      toast.dismiss();
      toast.error("All fields are required!");
      return;
    }
  
    if (password !== repeatPassword) {
      toast.dismiss();
      toast.error("Passwords do not match!");
      return;
    }
  
    try {
      const formData = new FormData();
      formData.append("username", name); // match backend param
      formData.append("email", email);
      formData.append("password", password);
      // formData.append("role", role); // optional if needed
  
      const response = await axios.post("http://localhost:8080/chatbot/register", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
  
      if (response.status === 200) {
        toast.success("User registered successfully");
        navigate("/login");
      } else {
        toast.error(response.data || "Registration failed!");
      }
    } catch (error) {
      toast.error(error.response?.data || "Error registering user!");
    }
  };

  // const handleVerifyOtp = async () => {
  //   if (!otp) {
  //     toast.error("Enter OTP!");
  //     return;
  //   }

  //   setIsVerifying(true);

  //   try {
  //     const response = await axios.post("http://localhost:8080/auth/verify", {
  //       email,
  //       otp,
  //       chatbotType,
  //     });

  //     if (response.data.status === "success") {
  //       toast.success(response.data.message);
  //       setShowOtpModal(false);
  //       navigate("/login");
  //     } else {
  //       toast.error(response.data.message || "Invalid OTP or OTP Expired!");
  //     }
  //   } catch (error) {
  //     toast.error(error.response?.data?.message || "Error verifying OTP!");
  //   } finally {
  //     setIsVerifying(false);
  //   }
  // };

  // const handleResendOtp = async () => {
  //   if (isResending) return;

  //   setIsResending(true);

  //   try {
  //     const response = await axios.post("http://localhost:8080/auth/resend", {
  //       email,
  //     });

  //     if (response.data.status === "success") {
  //       toast.success("New OTP sent to your email!");
  //       setOtp("");
  //     } else {
  //       toast.error("Failed to resend OTP!");
  //     }
  //   } catch (error) {
  //     toast.error("Error resending OTP!");
  //   } finally {
  //     setTimeout(() => setIsResending(false), 10000);
  //   }
  // };

  return (
    <div className="bg-body-tertiary min-vh-100 d-flex flex-row align-items-center">
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={9} lg={7} xl={6}>
            <CCard className="mx-4">
              <CCardBody className="p-4">
                <CForm>
                  <h1>Register</h1>
                  <p className="text-body-secondary">Create your account</p>

                  <CInputGroup className="mb-3">
                    <CInputGroupText>
                      <CIcon icon={cilUser} />
                    </CInputGroupText>
                    <CFormInput
                      placeholder="Name"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      autoComplete="name"
                    />
                  </CInputGroup>

                  <CInputGroup className="mb-3">
                    <CInputGroupText>
                      <CIcon icon={cilEnvelopeClosed} />
                    </CInputGroupText>
                    <CFormInput
                      placeholder="Email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      autoComplete="email"
                    />
                  </CInputGroup>

                  <CInputGroup className="mb-3">
                    <CInputGroupText>
                      <CIcon icon={cilLockLocked} />
                    </CInputGroupText>
                    <CFormInput
                      type="password"
                      placeholder="Password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      autoComplete="new-password"
                    />
                  </CInputGroup>

                  <CInputGroup className="mb-3">
                    <CInputGroupText>
                      <CIcon icon={cilLockLocked} />
                    </CInputGroupText>
                    <CFormInput
                      type="password"
                      placeholder="Repeat password"
                      value={repeatPassword}
                      onChange={(e) => setRepeatPassword(e.target.value)}
                      autoComplete="new-password"
                    />
                  </CInputGroup>

                  {/* <CFormSelect
                    className="mb-3"
                    value={chatbotType}
                    onChange={(e) => setChatbotType(e.target.value)}
                    aria-label="Chatbot type select"
                  >
                    {chatbotOptions.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </CFormSelect> */}

                  <div className="d-grid">
                    <CButton color="success" onClick={handleRegister}>
                      Create Account
                    </CButton>  
                  </div>
                  <div className="text-center mt-3"></div>
                    <p>
                      Already have an account?{" "}
                      <a href="/login" className="text-decoration-none">
                        Login
                      </a>
                    </p>
                </CForm>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
      </CContainer>

      {/* OTP Modal
      {showOtpModal && (
        <div className="modal-backdrop show d-flex justify-content-center align-items-center">
          <div className="bg-white p-4 rounded shadow" style={{ width: "350px" }}>
            <h5 className="text-center mb-3">Enter OTP</h5>
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              placeholder="Enter OTP"
              className="form-control mb-3"
            />
            <div className="d-flex justify-content-between">
              <button
                className="btn btn-primary"
                onClick={handleVerifyOtp}
                disabled={isVerifying}
              >
                {isVerifying ? "Verifying..." : "Verify OTP"}
              </button>
              <button
                className="btn btn-secondary"
                onClick={handleResendOtp}
                disabled={isResending}
              >
                {isResending ? "Resending..." : "Resend OTP"}
              </button>
            </div>
          </div>
        </div>
      )} */}
    </div>
  );
};

export default Register;
