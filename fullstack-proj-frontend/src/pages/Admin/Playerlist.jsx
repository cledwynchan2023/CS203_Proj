import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import "./style/PlayerlistStyle.css";
import { Link, useParams } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';

export default function Playerlist() {
    const[user,setUser]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const { userId } = useParams();
    const[selectedUser, setSelectedUser]=useState(null);
    const [selectedId, setSelectedId] = useState([]);
    const [createdUser,setCreatedUser] = useState({username:"", password:"", email:"", confirmPassword:"", role:"ROLE_USER", elo:0});
    const{username, password, email, confirmPassword, role, elo} = createdUser;
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditedModalOpen, setIsEditedModalOpen] = useState(false);
    const [editedUser,setEditedUser] = useState({editedUsername:"", editedEmail:"", editedRole:"ROLE_USER", editedElo:0});
    const{editedUsername, editedPassword, editedEmail, editedConfirmPassword, editedRole, editedElo} = editedUser;

    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        // Add any other tokens you want to clear here
        // localStorage.removeItem('anotherToken');
        // tokenKeys.forEach(key => {
        //     localStorage.removeItem(key);
        // });
    };
    // const initSSE = () => {
    //     const eventSource = new EventSource('http://localhost:8080/update/sse/users');

    //     eventSource.onmessage = (event) => {
    //         const users = JSON.parse(event.data);
    //         console.log(users);
    //         const filteredUsers = users
    //             .filter(user => user.role === 'ROLE_USER')
    //             .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
    //         setUser(filteredUsers);
    //         setData(filteredUsers);
    //     };

    //     eventSource.onerror = (error) => {
    //         console.error("SSE failure:", error);
    //         setError("Loading...");
    //         eventSource.close();
    //     };

    //     return () => {
    //         eventSource.close();
    //     };
    // };

    const deleteUser = async (user_id) => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.delete(`http://localhost:8080/u/${user_id}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            if (response.status === 204){
                alert("User Deleted Successfully");
                loadUsers();
            }
            
        } catch (error) {
            setError('An error occurred while deleting the user.');
        }
    };

    const onInputChange=(e)=>{
        setCreatedUser({...createdUser, [e.target.name]:e.target.value});
    }
    const onEditInputChange=(e)=>{
        setEditedUser({...editedUser, [e.target.name]:e.target.value});
    }

    const isTokenExpired = () => {
        const expiryTime = localStorage.getItem('tokenExpiry');
        if (!expiryTime) return true;
        return new Date().getTime() > expiryTime;
    };

    const isAdminToken = (token) => {
        try {
            const decodedToken = jwtDecode(token);
            console.log("decoded token" + decodedToken);
            console.log(decodedToken.authorities);
            return decodedToken.authorities === 'ROLE_ADMIN'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };
    const handleRowClick = (user) => {

        setEditedUser({editedUsername: user.username, editedEmail: user.email, editedRole: user.role, editedElo: user.elo});
       setSelectedId(user.id);
        setIsEditedModalOpen(true); 
        
    }
    useEffect(() => {
        console.log(editedUser.editedUsername + " user: " + editedUser.editedUsername);
    }, [editedUser]);

    const onSubmit= async ()=>{

        console.log(role);
        

        if (password !== confirmPassword) {
            alert("Passwords do not match");
            return;
        } else if (password.length <= 6){
            alert("Password needs to be more than 6 characters");
            return;
        }

        const userData = {
            username,
            email,
            password,
            role,
            elo
        };

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post("http://localhost:8080/auth/signup", userData, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
    
            if (response.status === 201){
                alert("User registered successfully!");
                setIsModalOpen(false);
                loadUsers();
            }
            console.log("hello");
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
 

    const onEditSubmit= async ()=>{
       
        

        const userData = {
            username: editedUsername,
            email: editedEmail,
            role: editedRole,
            elo: editedElo
        };
        const token = localStorage.getItem('token');
        try {
            const response = await axios.put(`http://localhost:8080/u/user/${selectedId}`, userData, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
    
            if (response.status === 200){
                alert("User Edited successfully!");
                
            }
        } 
        catch (error) {
            
                console.error("There was an error registering the user!", error);
            
            
        }

        
    }

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            console.log(token +" hello");
            
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                loadUsers();
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };
        fetchData();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadUsers= async()=>{
        const token = localStorage.getItem('token');
        const result = await axios.get('http://localhost:8080/u/users', {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
      
        setUser(result.data);
    };
    
  return (
    <>
        <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
        }}> 
            <div className="content container fade-in" style={{height:"100%", width:"100%",paddingTop:"100px", paddingBottom:"50px"}}>
       
        <section className="section is-large" style={{ height:"100%", width:"100%", paddingTop:"30px", backgroundColor:"rgba(0, 0, 0, 0.5)", borderRadius:"35px", overflowY:"scroll",overflowX:"scroll"}}>
            <div className="hero-body" style={{marginBottom:"5%"}}>
                <p className="title is-size-2 is-family-sans-serif">Player List</p>
                <button className="button is-link is-rounded" onClick={()=> {setIsModalOpen(true)}}>Create Players</button>
            </div>
            <table className="table is-hoverable custom-table" >
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Elo</th>
                        <th>Role</th>
                    </tr>
                </thead>
                <tbody>
                    {user.map((user, index) =>
                        <tr key={user.id} onClick={() => handleRowClick(user)}>
                            <td>{user.id}</td>
                            <td>{user.username}</td>
                            <td>{user.elo}</td>
                            <td>{user.role}</td>
                            <button className="button is-text" style={{marginTop:"20px", marginLeft:"20px"}} onClick={(event) => {deleteUser(user.id);
                            event.stopPropagation();
                            }}>Remove</button>           
                        </tr>   
                    )}
                </tbody>
            </table>
        </section>
        {isModalOpen && (
              <div class="modal is-active fade-in">
              <div class="modal-background"></div>
              <div class="modal-card">
                <header class="modal-card-head">
                  <p class="modal-card-title">Create User</p>
                  <button class="delete"  onClick={() => setIsModalOpen(false)} aria-label="close"></button>
                </header>
                <section class="modal-card-body" style={{height:"400px"}}>
               
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
                        <label htmlFor="tournament_name">Email Address</label>
                        </div>
                        
                        <div className="form-floating mb-3">
                        <input
                            type="text"
                            className="form-control"
                            id="floatingUsername"
                            placeholder="username"
                            value={username}
                            onChange={(e) =>onInputChange(e)}
                            name="username"
                        />
                        <label htmlFor="Username">Username</label>

                        </div>
                        <div className="form-floating mb-3 mt-3">
                            <input
                                type="password"
                                className="form-control"
                                id="floatingRole"
                                value={password}
                                onChange={(e) => onInputChange(e)}
                                name="password"
                            
                            />
                            <label htmlFor="Status">Password</label>
                        </div>
                        <div className="form-floating mb-3 mt-3">
                            <input
                                type="password"
                                className="form-control"
                                id="floatingRole"
                                value={confirmPassword}
                                onChange={(e) => onInputChange(e)}
                                name="confirmPassword"
                            
                            />
                            <label htmlFor="Status">Confirm Password</label>
                        </div>
                        <div className="form-floating mb-3 mt-3">
                            <input
                            type="number"
                            className="form-control"
                            placeholder="elo"
                            value={elo}
                            onChange={(e) =>onInputChange(e)}
                            name="elo"
                        />
                        <label htmlFor="PasswordConfirm">Elo</label>
                            </div>
                        <div style={{marginTop:"5%"}}>
                        <button onClick={()=> {onSubmit()}} className='button is-link is-fullwidth'>Create Player</button>
                        </div>

                    </form>
            
                </section>
                <footer class="modal-card-foot">
                  <div class="buttons">
                    
                    <button class="button" onClick={() => setIsModalOpen(false)}>Cancel</button>
                  </div>
                </footer>
              </div>
            </div>
            )}

        {isEditedModalOpen && (
              <div class="modal is-active fade-in">
              <div class="modal-background"></div>
              <div class="modal-card">
                <header class="modal-card-head">
                  <p class="modal-card-title">Edit User {selectedId}</p>
                  <button class="delete"  onClick={() => setIsEditedModalOpen(false)} aria-label="close"></button>
                </header>
                <section class="modal-card-body" style={{height:"400px"}}>
               
                    <form onSubmit={(e) => onEditSubmit(e)}>
                        <div className="form-floating mb-3">
                        <input
                            type="text"
                            className="form-control form-control-lg"
                            id="floatingInput"
                            placeholder="name@example.com"
                            value={editedEmail}
                            onChange={(e) =>onEditInputChange(e)}
                            name="editedEmail"
                        ></input>
                        <label htmlFor="tournament_name">Email Address</label>
                        </div>
                        
                        <div className="form-floating mb-3">
                        <input
                            type="text"
                            className="form-control"
                            id="floatingUsername"
                            placeholder="username"
                            value={editedUsername}
                            onChange={(e) =>onEditInputChange(e)}
                            name="editedUsername"
                        />
                        <label htmlFor="Username">Username</label>

                        </div>
                        
                        <div className="form-floating mb-3 mt-3">
                            <input
                            type="number"
                            className="form-control"
                            placeholder="elo"
                            value={editedElo}
                            onChange={(e) =>onEditInputChange(e)}
                            name="editedElo"
                        />
                        <label htmlFor="PasswordConfirm">Elo</label>
                            </div>
                        <div style={{marginTop:"5%"}}>
                        <button onClick={()=> {onEditSubmit()}} className='button is-link is-fullwidth'>Edit Player</button>
                        </div>

                    </form>
            
                </section>
                <footer class="modal-card-foot">
                  <div class="buttons">
                    
                    <button class="button" onClick={() => setIsEditedModalOpen(false)}>Cancel</button>
                  </div>
                </footer>
              </div>
            </div>
            )}
    </div>
        </div>

        <footer className="footer" style={{textAlign:"center",height:"100px"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
