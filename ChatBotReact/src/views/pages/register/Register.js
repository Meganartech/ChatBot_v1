// import React, { useState } from "react";
// import {
//   CButton,
//   CCard,
//   CCardBody,
//   CCol,
//   CContainer,
//   CForm,
//   CFormInput,
//   CInputGroup,
//   CInputGroupText,
//   CRow,
//   CFormSelect,
// } from "@coreui/react";
// import CIcon from "@coreui/icons-react";
// import { cilLockLocked, cilUser, cilEnvelopeClosed } from "@coreui/icons";
// import { toast } from "react-toastify";
// import axios from "axios";
// import { useNavigate } from "react-router-dom";
// import "react-toastify/dist/ReactToastify.css";

// const Register = () => {
//   const [name, setName] = useState("");
//   const [email, setEmail] = useState("");
//   const [password, setPassword] = useState("");
//   const [repeatPassword, setRepeatPassword] = useState("");

//   const navigate = useNavigate();

//   const handleRegister = async () => {
//     if (!name || !email || !password || !repeatPassword) {
//       toast.dismiss();
//       toast.error("All fields are required!");
//       return;
//     }
  
//     if (password !== repeatPassword) {
//       toast.dismiss();
//       toast.error("Passwords do not match!");
//       return;
//     }
  
//     try {
//       const formData = new FormData();
//       formData.append("username", name); // match backend param
//       formData.append("email", email);
//       formData.append("password", password);
//       // formData.append("role", role); // optional if needed
  
//       const response = await axios.post("http://localhost:8080/chatbot/register", formData, {
//         headers: {
//           "Content-Type": "multipart/form-data",
//         },
//       });
  
//       if (response.status === 200) {
//         toast.success("User registered successfully");
//         navigate("/login");
//       } else {
//         toast.error(response.data || "Registration failed!");
//       }
//     } catch (error) {
//       toast.error(error.response?.data || "Error registering user!");
//     }
//   };

//   return (
//     <div className="bg-body-tertiary min-vh-100 d-flex flex-row align-items-center">
//       <CContainer>
//         <CRow className="justify-content-center">
//           <CCol md={9} lg={7} xl={6}>
//             <CCard className="mx-4">
//               <CCardBody className="p-4">
//                 <CForm>
//                   <h1>Register</h1>
//                   <p className="text-body-secondary">Create your account</p>

//                   <CInputGroup className="mb-3">
//                     <CInputGroupText>
//                       <CIcon icon={cilUser} />
//                     </CInputGroupText>
//                     <CFormInput
//                       placeholder="Name"
//                       value={name}
//                       onChange={(e) => setName(e.target.value)}
//                       autoComplete="name"
//                     />
//                   </CInputGroup>

//                   <CInputGroup className="mb-3">
//                     <CInputGroupText>
//                       <CIcon icon={cilEnvelopeClosed} />
//                     </CInputGroupText>
//                     <CFormInput
//                       placeholder="Email"
//                       type="email"
//                       value={email}
//                       onChange={(e) => setEmail(e.target.value)}
//                       autoComplete="email"
//                     />
//                   </CInputGroup>

//                   <CInputGroup className="mb-3">
//                     <CInputGroupText>
//                       <CIcon icon={cilLockLocked} />
//                     </CInputGroupText>
//                     <CFormInput
//                       type="password"
//                       placeholder="Password"
//                       value={password}
//                       onChange={(e) => setPassword(e.target.value)}
//                       autoComplete="new-password"
//                     />
//                   </CInputGroup>

//                   <CInputGroup className="mb-3">
//                     <CInputGroupText>
//                       <CIcon icon={cilLockLocked} />
//                     </CInputGroupText>
//                     <CFormInput
//                       type="password"
//                       placeholder="Repeat password"
//                       value={repeatPassword}
//                       onChange={(e) => setRepeatPassword(e.target.value)}
//                       autoComplete="new-password"
//                     />
//                   </CInputGroup>

//                   <div className="d-grid">
//                     <CButton color="success" onClick={handleRegister}>
//                       Create Account
//                     </CButton>  
//                   </div>
//                   <div className="text-center mt-3"></div>
//                     <p>
//                       Already have an account?{" "}
//                       <a href="/login" className="text-decoration-none">
//                         Login
//                       </a>
//                     </p>
//                 </CForm>
//               </CCardBody>
//             </CCard>
//           </CCol>
//         </CRow>
//       </CContainer>

//     </div>
//   );
// };

// export default Register;


import React, { useState, useEffect } from "react";
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
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import { cilLockLocked, cilUser, cilEnvelopeClosed, cilDisabled } from "@coreui/icons";
import { toast } from "react-toastify";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "react-toastify/dist/ReactToastify.css";
import API_URL from "../../../Config";

const Register = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState(""); // optional if you want to display
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [isPreFilled, setIsPreFilled] = useState(false);

  

  const navigate = useNavigate();

  useEffect(() => {
    const hashParams = new URLSearchParams(window.location.hash.split("?")[1]);
    const token = hashParams.get("token");

    if (token) {
      axios
        .get(`${API_URL}/chatbot/register-token/${token}`)
        .then((res) => {
          setEmail(res.data.email);
          setRole(res.data.role);
          setIsPreFilled(true); // Disable input because it's pre-filled from token
        })
        .catch(() => {
          toast.error("Invalid or expired registration link.");
          navigate("/404")
        });
    }
  }, []);
  console.log(role,email)

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
      formData.append("username", name);
      formData.append("email", email);
      formData.append("password", password);
      formData.append("role", role); // include role if required by backend

      const response = await axios.post(
        `${API_URL}/chatbot/register`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
              
          },
        }
      );

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
  disabled={isPreFilled}  // use a separate flag for disabling
  autoComplete="email"
  onChange={(e) => setEmail(e.target.value)}
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

                  <div className="d-grid">
                    <CButton color="success" onClick={handleRegister}>
                      Create Account
                    </CButton>
                  </div>

                  <div className="text-center mt-3">
                    <p>
                      Already have an account?{" "}
                      <a href="/login" className="text-decoration-none">
                        Login
                      </a>
                    </p>
                  </div>
                </CForm>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  );
};

export default Register;

