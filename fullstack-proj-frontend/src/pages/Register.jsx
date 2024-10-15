import {useState} from 'react';

import { Link, useNavigate } from "react-router-dom";
import axios from "axios"
import './Register.css';
import background from '/src/assets/background_2.jpg';
export default function Register() {
    let navigate=useNavigate();
    const [user,setUser] = useState({username:"", password:"", email:"", confirmPassword:"", role:"ROLE_USER"});
    const{username, password, email, confirmPassword, role} = user;

    const onInputChange=(e)=>{
        setUser({...user, [e.target.name]:e.target.value});
       
    }
   
    const isValidEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const isDomainValid = async (email) => {
        const domain = email.split('@')[1];
        if (!domain) return false;

        try {
            const response = await fetch(`https://dns.google/resolve?name=${domain}&type=MX`);
            const data = await response.json();
            return data.Answer && data.Answer.length > 0; // Check if MX records exist
        } catch {
            return false;
        }
    };

    const validateAdminToken = async (token) => {
        try {
          const response = await axios.post('http://localhost:8080/auth/validate-admin-token', { token });
          console.log(response.data.valid);
          return response.data.valid;
        } catch (error) {
          console.error('Error validating admin token', error);
          return false;
        }
      };
    

    const onSubmit= async (e)=>{
        e.preventDefault();
        console.log(role);
        
        if (!isValidEmail(email)) {
            alert("Invalid email address");
            return;
        }

        if (role === 'ROLE_ADMIN') {
            const inputToken = window.prompt("Please enter the admin token:");
            const isValid = await validateAdminToken(inputToken);
            console.log(isValid);
            if (!isValid) {
              alert("Invalid admin token");
              return;
            }
          }

        const domainValid = await isDomainValid(email);
        if (!domainValid) {
            alert("Email domain does not exist or cannot receive emails");
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords do not match");
            return;
        } else if (password.length <= 6){
            alert("Password needs to be more than 6 characters");
            return;
        }
        // System.out.println(role);
        const userData = {
            username,
            password,
            email,
            role
        };

        try {
            const response = await axios.post("http://localhost:8080/auth/signup", userData);
    
            if (response.status === 201){
                alert("User registered successfully!");
                navigate("/");
            }
        } 
        catch (error) {
            if (error.response.status === 409) {
                alert("Username or Email already taken.");
                console.error( "Conflict: Username or Email already exists!");
            } else {
                console.error("There was an error registering the user!", error);
            }
            
        }

        
    }

    return (
      <>
        <div className="register-container" style={{ 
         backgroundImage: `url(${background})`,
          backgroundSize: 'cover', 
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
  flexWrap: 'wrap',
      }}>
          <div className="form-container fade-in" style={{backgroundColor:"rgba(0, 0, 0, 0.8)"}}>
            <form onSubmit={(e) => onSubmit(e)}>
              <div className="field">
                <label className="label has-text-primary-light">Email</label>
                <div className="control">
                  <input
                    type="email"
                    className="input"
                    id="floatingInput"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) =>onInputChange(e)}
                    name="email"
                  ></input>
                </div>
              </div>
            
            <div className="field">
              <label className="label has-text-primary-light">Username</label>
              <div className="control">
                <input
                  type="name"
                  className="input"
                  id="floatingUsername"
                  placeholder="Username"
                  value={username}
                  onChange={(e) =>onInputChange(e)}
                  name="username"
                />
              </div>
            </div>
            <div className="field">
              <label className="label has-text-primary-light">Password</label>
              <div className="control">
                <input
                  type="password"
                  className="input"
                  placeholder="Password"
                  value={password}
                  onChange={(e) =>onInputChange(e)}
                  name="password"
                />
              </div>
            </div>
            <div className="field">
              <label className="label has-text-primary-light">Confirm Password</label>
              <div className="control">
                <input
                  type="password"
                  className="input"
                  id="floatingConfirmPassword"
                  placeholder="Confirm Password"
                  value={confirmPassword}
                  onChange={onInputChange}
                  name="confirmPassword"
                />
              </div>
            </div>
            <div className="field">
                <label className="label has-text-primary-light">Select Role</label>
                <div className="control is-expanded">
                    <div classname="select is-fullwidth">
                    <select
                            
                            id="floatingRole"
                            //value={role}
                            onChange={(e) => onInputChange(e)}
                            name="role"
                        >
                            <option value="ROLE_USER">User</option>
                            <option value="ROLE_ADMIN">Admin</option>
                    </select>
                  </div>
              </div>
              </div>
            <button type="submit" className="button is-link is-fullwidth">Register</button>
            
            </form>
            <div className="block">
            <Link type="cancel" className='button is-text is-fullwidth' to='/' id="returnrBtn">Cancel</Link>
            </div>
            
           
          </div>
         
        </div>
        <footer className="footer">
        <p>&copy; 2024 CS203. All rights reserved.</p>
        </footer>
        </>
      );
}
