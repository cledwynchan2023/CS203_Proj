import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import chessplaying1 from '/src/assets/chessplaying.webp';
import { IoCalendarNumberOutline } from "react-icons/io5";
import { BiGroup } from "react-icons/bi";
import { TiTick } from "react-icons/ti";
import { IoMdInformationCircleOutline } from "react-icons/io";
import swissPic from '/src/assets/swiss.png';
import { CgProfile } from "react-icons/cg";

export default function Ranking() {
    const navigate = useNavigate();
    const [selectedId, setSelectedId] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const[user,setUser]=useState([]);
    const {userId} = useParams()
    const[selectedUser,setSelectedUser]=useState([]);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('Global');
    const[ranking , setRanking]=useState([]);
    const isTokenExpired = () => {
        const expiryTime = localStorage.getItem('tokenExpiry');
        if (!expiryTime) return true;
        return new Date().getTime() > expiryTime;
    };
    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        
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

    const handleRowClick = (user, ranking) => {
        setRanking(ranking);
        setSelectedUser(user);
        setIsModalOpen(true);
    };

    const handleViewProfile = (userid) =>{
        setIsModalOpen(false);
        navigate(`/user/${userId}/profile/${userid}`);
    }

    const renderTabContent = () => {
        switch (activeTab) {
            case 'Global':
                return <>
                <section className="section is-large animate__animated animate__fadeInUpBig" style={{ paddingTop:"30px", borderRadius:"35px", height:"auto", overflowX:"scroll", width:"90%"}}>
            
            <table className="table is-hoverable custom-table" >
                <thead>
                    <tr>
                        <th style={{width:"100px"}}>Ranking</th>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Elo</th>
                    </tr>
                </thead>
                <tbody>
                    {user.map((user, index) =>
                        <tr key={user.id} onClick={() => handleRowClick(user, index + 1)}>
                            <td style={{width:"100px"}}>{index + 1}</td>
                            <td>{user.id}</td>
                            <td>{user.username}</td>
                            <td>{user.elo}</td>
                        </tr>   
                    )}
                </tbody>
            </table>
        </section>
        {isModalOpen && (
              <div class="modal is-active fade-in">
              <div class="modal-background"></div>
              <div class="modal-card animate__animated animate__fadeInDown">
                <header class="modal-card-head">
                  <p class="modal-card-title">{selectedUser.username}</p>
                  <button class="delete" onClick={() => setIsModalOpen(false)} aria-label="close"></button>
                </header>
                <section class="modal-card-body" style={{height:"250px"}}>
                    <div style={{width:"100%"}}>
                        <div style={{display:"flex", alignItems:"center", height:"100%"}}>
                            <CgProfile style={{fontSize:"170px", color:"white"}}/>
                            <div style={{paddingLeft:"20px"}}>
                                <p className="title" style={{fontWeight:"bold", marginBottom:"10px"}}>{selectedUser.username}</p>
                                <p className="subtitle" style={{marginBottom:"0"}}>Elo: {selectedUser.elo}</p>
                                <p className="subtitle">Ranking: {ranking}</p>
                                <button className="button is-link" onClick={() => handleViewProfile(selectedUser.id)}>View Profile</button>
                            </div>

                        </div>
                    </div>
            
                </section>
                <footer class="modal-card-foot">
                  <div class="buttons">
                    
                    <button class="button" onClick={() => setIsModalOpen(false)}>Cancel</button>
                  </div>
                </footer>
              </div>
            </div>
            )}
                </>
            case 'Region':
                return <>
                <section className="section is-large" style={{ paddingTop:"30px", borderRadius:"35px", height:"auto", overflowX:"scroll", width:"70%"}}>
            <table className="table is-hoverable custom-table" >
                <thead>
                    <tr>
                        <th style={{width:"100px"}}>Ranking</th>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Elo</th>
                    </tr>
                </thead>
                <tbody>
                    {user.map((user, index) =>
                        <tr key={user.id} onClick={() => handleRowClick(tournament.id)}>
                             <td style={{width:"100px"}}>{index + 1}</td>
                            <td>{user.id}</td>
                            <td>{user.username}</td>
                            <td>{user.elo}</td>
                        </tr>   
                    )}
                </tbody>
            </table>
        </section>
                </>
        }
        };

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            console.log(token +" hello");
            
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                clearTokens();
                console.log(isAdminToken(token));
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get('http://localhost:8080/u/users', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    console.log("Invalid TOken")
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };

        fetchData();
        
        loadUsers();

    }, []);

    const loadUsers= async()=>{
        const token = localStorage.getItem('token');
        try {
            const result = await axios.get("http://localhost:8080/u/users", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            const filteredUsers = result.data
                .filter(user => user.role === 'ROLE_USER')
                .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
            setUser(filteredUsers);
        } catch (error) {
            setError("Error loading users");
            console.error("Error loading users:", error);
        }
    };
    
  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`,
        height:"100vh"
    }}>
        <div className="content" style={{width:"100%", height:"100%", display:"flex", justifyContent:"center"}}>

            <section className="hero fade-in" style={{height:"100%",display:"flex",justifyContent:"start",paddingLeft:"2%", paddingRight:"2%", width:"100%",height:"100%", backgroundColor:"rgba(0, 0, 0, 0.6)", paddingBottom:"50px", overflowY:"scroll", borderRadius:"40px"}}>
            <div style={{width:"100%", paddingTop:"50px", paddingLeft:"40px"}}>
                    <p className="title is-family-sans-serif is-2" style={{width:"100%", fontWeight:"bold", fontStyle:"italic"}}>Ranking</p>
                </div>
            <div style={{width:"100%"}}></div>
            <div className="tabs is-left" style={{ height:"70px"}}>
              <ul>
                <li className={activeTab === 'Global' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Global')}>Global</a>
                </li>
                <li className={activeTab === 'Region' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Region')}>Region</a>
                </li>
              </ul>
            </div>
            <div className="fade-in" style={{backgroundColor: "rgba(0, 0, 0, 0)", display:"flex", justifyContent:"center", width:"100%"}}>
              {renderTabContent()}
            </div>
          </section>
        </div>

    </div>
    <footer className="footer" style={{textAlign:"center"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
