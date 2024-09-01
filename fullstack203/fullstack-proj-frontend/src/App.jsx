import { useState } from 'react'
import Navbar from './layout/Navbar.jsx';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Login from './pages/Login.jsx';
import {BrowserRouter as Router, Route, Routes, useLocation} from "react-router-dom";
import NavbarLogin from './layout/NavbarLogin.jsx';
import Register from './pages/Register.jsx';
function App() {

  return (
    <>
      <Router>
      
        <Routes>
        <Route exact path="/login" element={
          <>
          <NavbarLogin></NavbarLogin>
          <Login/>
          </>
          
        }></Route>

        <Route exact path="/register" element={
          <>
          <NavbarLogin></NavbarLogin>
          <Register/>
          </>
          
        }></Route>
        </Routes>
       
      </Router>
    </> 
    
  )
}

export default App
