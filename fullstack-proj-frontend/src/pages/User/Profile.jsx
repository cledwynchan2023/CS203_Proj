import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import chessplaying1 from '/src/assets/chessplaying.webp';
import "./style/TournamentDetailStyle.css";
import { CgProfile } from "react-icons/cg";

export default function Profile() {
    const[user,setUser]=useState([]);
    const[nonParticpatingUser,setNonParticipatingUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const{userId} = useParams()

    const [activeTab, setActiveTab] = useState('Overview');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editedUser,setEditedUser] = useState({username:"", password:"", email:"", role:"ROLE_USER", confirmPassword:"", elo:""});
    const {username, password, email, role, confirmPassword, elo} = editedUser;

    const onInputChange=(e)=>{
        setEditedUser({...editedUser, [e.target.name]:e.target.value});
        
    }


    const onSubmit= async (e)=>{
        e.preventDefault();
        console.log(editedUser);
        const userData = {
            username,
            password,
            email,
            role,
            elo
        };
        console.log(userData.username);
        try {
            const response = await axios.put(`http://localhost:8080/u/${userId}`, userData);
            if (response.status === 200){
                alert("User Edited Successfully");
                setIsEditModalOpen(false);
                loadUser();
            }
            
        } catch (error) {
            console.error("There was an error registering the tournament!", error);
        }
        
    }

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Overview':
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{width:"100%", overflowY:"scroll", height:"600px", marginBottom:"50px"}}>
            <div style={{display:"flex", justifyContent:"space-around", flexWrap:"wrap"}}>
             
                
            </div>
        </section>;
      case 'History':
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
                
            </section>;
      case 'Stats':
        return <div>Scoreboard Content</div>;
      default:
        return null;
    }
  };

    
    const clearTokens = () => {
        localStorage.removeItem('token'); 
        localStorage.removeItem('tokenExpiry'); 
       
    };
    const loadUser= async()=>{
        const token = localStorage.getItem('token');
        const result = await axios.get(`http://localhost:8080/u/id/${userId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        console.log(result.data);
        
     
        setEditedUser({
            username: result.data.username,
            email: result.data.email,                      
            elo: result.data.elo     
        });
    
      
        setUser(result.data);
        
        
    };


    const deleteTournament = async (id) => {
        try {
            if (tournament.currentSize > 0) {
                setError('Cannot delete a tournament with participants.');
                return;
            }
            const response = await axios.delete(`http://localhost:8080/t/tournament/${id}`);
            // Refresh the tournament list after deletion
            if (response.status === 200){
                alert("Tournament Deleted Successfully");
                loadTournaments();
                Navigate(`/admin/${userId}/tournament`);
            }
            
        } catch (error) {
            setError('An error occurred while deleting the tournament.');
        }
    };

   
   

    const isTokenExpired = () => {
        const expiryTime = localStorage.getItem('tokenExpiry');
        if (!expiryTime) return true;
        return new Date().getTime() > expiryTime;
    };

    const isAdminToken = (token) => {
        try {
            const decodedToken = jwtDecode(token);
            console.log(decodedToken)
            console.log(decodedToken.userId);
            console.log(decodedToken.authorities)
            if ((decodedToken.authorities === 'ROLE_ADMIN' || decodedToken.authorities === 'ROLE_USER') && decodedToken.userId == userId){

                return true;
            } else {
                
                return false;
            }
          
        } catch (error) {
            
            return false;
        }
    };

    

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
                const response = await axios.get(`http://localhost:8080/u/id/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
                setUser(response.data);
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
        loadUser();
    }, []);

    if (error) {
        return <div>{error}</div>;
    }

  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
    }}> 
    <div className="content" style={{width:"100%", height:"100%", overflowY:"scroll"}}>
        <section className="hero is-flex-direction-row fade-in" style={{paddingLeft:"5%", paddingRight:"5%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.5)"}}>
            <div style={{width:"200px",marginRight:"20px"}}>
                <CgProfile style={{width:"100%", height:"100%"}}/>
            </div>
            <div style={{width:"90%", alignContent:"center"}}>
                <p className="title is-family-sans-serif" style={{width:"80%", fontWeight:"bold"}}>{user.username}</p>
                <p class="subtitle">ID: {user.id}</p>
            </div>
            <div style={{display:"flex",alignItems:"center", width:"50%"}}>
               
                <button className="button is-link" onClick={() => setIsEditModalOpen(true)} style={{width:"45%", height:"40px",marginRight:"5%", fontWeight:"bold"}}>Edit</button>
                <button className="button is-danger" style={{width:"45%", height:"40px", fontWeight:"bold"}}>Delete</button>
            </div>
            
        </section>
        {isEditModalOpen && (
              <div class="modal is-active fade-in">
              <div class="modal-background"></div>
              <div class="modal-card">
                <header class="modal-card-head">
                  <p class="modal-card-title">Edit Profile</p>
                  <button class="delete"  onClick={() => setIsEditModalOpen(false)} aria-label="close"></button>
                </header>
                <section class="modal-card-body" style={{height:"400px"}}>
               
                    <form onSubmit={(e) => onSubmit(e)}>
                        <div className="form-floating mb-3">
                        <input
                            type="text"
                            className="form-control form-control-lg"
                            id="floatingInput"
                            placeholder="name@example.com"
                            value={username}
                            onChange={(e) =>onInputChange(e)}
                            name="username"
                        ></input>
                        <label htmlFor="username">Username</label>
                        </div>
                        
                        <div className="form-floating mb-3">
                        <input
                            type="email"
                            className="form-control"
                            id="floatingUsername"
                            placeholder="email"
                            value={email}
                            onChange={(e) =>onInputChange(e)}
                            name="email"
                        />
                        <label htmlFor="Email">Email</label>

                        </div>
                        <div className="form-floating mb-3 mt-3">
                            <input
                                type="Password"
                                className="form-control"
                                id="floatingRole"
                                value={password}
                                onChange={(e) => onInputChange(e)}
                                name="password"
                            >
                            </input>
                            <label htmlFor="Password">Password</label>
                        </div>
                        <div className="form-floating mb-3 mt-3">
                            <input
                                type="Password"
                                className="form-control"
                                id="floatingRole"
                                value={confirmPassword}
                                onChange={(e) => onInputChange(e)}
                                name="confirmPassword"
                            >
                            </input>
                            <label htmlFor="Password">Confirm Password</label>
                        </div>
                        <div style={{marginTop:"5%"}}>
                        <button type="submit" className='button is-link is-fullwidth'>Edit Profile</button>
                        </div>
                    </form>
            
                </section>
                <footer class="modal-card-foot">
                  <div class="buttons">
                    
                    <button class="button" onClick={() => setIsEditModalOpen(false)}>Cancel</button>
                  </div>
                </footer>
              </div>
            </div>
            )}
        <section className="hero fade-in" style={{paddingLeft:"2%", paddingRight:"2%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.8)", height:"100%"}}>

                <div className="tabs is-left" style={{ height:"70px"}}>
                <ul>
                    <li className={activeTab === 'Overview' ? 'is-active' : ''}>
                    <a onClick={() => setActiveTab('Overview')}>Overview</a>
                    </li>
                    <li className={activeTab === 'History' ? 'is-active' : ''}>
                    <a onClick={() => setActiveTab('History')}>History</a>
                    </li>
                    <li className={activeTab === 'Stats' ? 'is-active' : ''}>
                    <a onClick={() => setActiveTab('Stats')}>Stats</a>
                    </li>
                </ul>
                </div>
                <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)", height:"100%"}}>
                {renderTabContent()}
                </div>
          </section>
    </div>
    </div>
    <footer className="footer" style={{textAlign:"center",marginTop:"100px",height:"100px"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
