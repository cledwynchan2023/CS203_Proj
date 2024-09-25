import {useState} from 'react';

import { Link, useNavigate } from "react-router-dom";
import axios from "axios"
import Modal from '../layout/Modal';

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
          const response = await axios.post('http://localhost:8080/admin/signin/validate-admin-token', { token });
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
        // if (usernameTaken) {
        //     alert("Username is already taken");
        //     return;
        // }

        if (role === 'admin') {
            const inputToken = window.prompt("Please enter the admin token:");
            const isValid = await validateAdminToken(inputToken);
            console.log(isValid);
            if (!isValid) {
              alert("Invalid admin token");
              return;
            }
          }

        // if (emailTaken) {
        //     alert("Email is already taken");
        //     return;
        // }

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
            const response = await axios.post("http://localhost:8080/admin/signup/user", userData);
    
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
        <div className="register-container">
          <div className="form-container">
            <form onSubmit={(e) => onSubmit(e)}>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control form-control-lg"
                id="floatingInput"
                placeholder="name@example.com"
                value={email}
                onChange={(e) =>onInputChange(e)}
                name="email"
              ></input>
              <label htmlFor="Email">Email address</label>
            </div>
            
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="floatingUsername"
                placeholder="Username"
                value={username}
                onChange={(e) =>onInputChange(e)}
                name="username"
              />
              <label htmlFor="Username">Username</label>

            </div>
            <div className="form-floating">
              <input
                type="password"
                className="form-control"
                placeholder="Password"
                value={password}
                onChange={(e) =>onInputChange(e)}
                name="password"
              />
              <label htmlFor="Password">Password</label>
            </div>
            <div className="form-floating mt-3">
              <input
                type="password"
                className="form-control"
                id="floatingConfirmPassword"
                placeholder="Confirm Password"
                value={confirmPassword}
                onChange={onInputChange}
                name="confirmPassword"
              />
              <label htmlFor="PasswordConfirm">Confirm Your Password</label>
            </div>
                <div className="form-floating mb-3 mt-3">
                        <select
                            className="form-control"
                            id="floatingRole"
                            //value={role}
                            onChange={(e) => onInputChange(e)}
                            name="role"
                        >
                            <option value="ROLE_USER">User</option>
                            <option value="ROLE_ADMIN">Admin</option>
                        </select>
                        <label htmlFor="Role">Role</label>
                </div>
            <button type="submit" className='btn btn-outline-primary mt-3'>Register</button>
            
            </form>
            <Link type="cancel" className='btn btn-outline-danger' to='/' id="returnrBtn">Cancel</Link>
           
          </div>
         
        </div>
        
      );
}
