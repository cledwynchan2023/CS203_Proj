import 'fullstack-proj-frontend/src/Global.js';
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import chessplaying1 from '/src/assets/chessplaying.webp';
import "./style/TournamentDetailStyle.css";
import { IoCalendarNumberOutline } from "react-icons/io5";
import { BiGroup } from "react-icons/bi";
import { TiTick } from "react-icons/ti";
import { IoMdInformationCircleOutline } from "react-icons/io";
import swissPic from '/src/assets/swiss.png';
import {Atom} from "react-loading-indicators"
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';


export default function TournamentDetail() {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const[user,setUser]=useState(null);
    const[nonParticpatingUser,setNonParticipatingUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const {userId} = useParams()
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Overview');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [hasJoined, setHasJoined] = useState(false); 
   



  const renderTabContent = () => {
    switch (activeTab) {
      case 'Overview':
        return <>
        {isLoading ? (
            <div style={{display:"flex", justifyContent:"center", height:"100%"}}>
                <Atom color="#9e34eb" size={100} style={{marginTop:"20%", marginLeft:"50%"}}></Atom>
            </div>
            
        ): (
            <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{width:"100%", height:"600px"}}>
            <div style={{display:"flex", justifyContent:"space-around", flexWrap:"wrap", gap:"5%"}}>
                <div className="card" style={{width:"30%", minWidth:"300px",marginright:"10px"}}>
                    <div className="card-image">
                        <figure className="image is-4by3">
                        <img
                            src={chessplaying1}
                            alt="Placeholder image"
                        />
                        </figure>
                    </div>
                    <div className="card-content">
                        <div className="media">
                            <div className="media-content">
                                <p className="title is-4">Game of Chess</p>
                            </div>
                        </div>

                        <div className="content">
                        Goal is to checkmate the opponent’s king. Players control 16 pieces each, including pawns, rooks, knights, bishops, a queen, and a king.
                        <br />
                        </div>
                    </div>
                </div>

                <div style={{width:"50%", minWidth:"300px",display:"flex",justifyContent:"center", flexWrap:"wrap",gap:"5%", backgroundColor:"", alignContent:"center"}}>
                    <div style={{width:"100%", height:"50px", textAlign:"center"}}>
                        <p className="title is-4" >Tournament Details</p>
                    </div>
                    <div className="card" style={{width:"45%", minWidth:"250px", height:"150px"}} onClick={() => setIsModalOpen(true)} >
                        <div className="card-content" style={{width:"100%", height:"100%"}}>
                    
                            <div style={{ display: "flex", alignItems: "center", width:"100%",height:"50%"}}> 
                                <div style={{marginRight:"5%"}}>
                                        <IoMdInformationCircleOutline size={45}></IoMdInformationCircleOutline>
                                    </div>
                                    <div>
                                        <p className="subtitle" style={{fontSize:"1rem"}}>Format</p>
                                        <p className="title" style={{fontSize:"2rem", fontWeight:"bold"}}>Swiss</p>
                                    </div>
                            </div>

                                <div style={{height:"50%", paddingTop:"50px"}}>
                                    <p>Click here to find out more!</p>
                                </div>
                                
                            </div>
                   
                    </div>
                    <div className="card" style={{width:"45%", minWidth:"250px",  height:"150px"}}>
                            <div className="card-content">
                                <div className="content" style={{display:"flex", flexWrap:"wrap", alignItems:"center"}}>
                                    <div style={{marginRight:"5%"}}>
                                     <IoCalendarNumberOutline size={35}></IoCalendarNumberOutline>
                                    </div>
                                    <div>
                                        <p className="subtitle" style={{fontSize:"1rem"}}>Date</p>
                                        <p className="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.date}</p>
                                    </div> 
                                </div>
                            </div>
                     </div>
                     <div className="card" onClick={() => setActiveTab('Players')} style={{width:"45%", minWidth:"250px", height:"150px"}}>
                        <div className="card-content">
                        <div className="content" style={{display:"flex", flexWrap:"wrap", alignItems:"center"}}>
                                <div style={{marginRight:"5%"}}>
                                    <BiGroup size={45}></BiGroup>
                                </div>
                                <div>
                                <p className="subtitle" style={{fontSize:"1rem"}}>Capacity</p>
                                <p className="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.currentSize}/{tournament.size}</p>
                                </div> 
                            </div>
                        </div>
                           
                    </div>
                    <div className="card" style={{width:"45%", minWidth:"250px", height:"150px"}}>
                        <div className="card-content">
                        <div className="content" style={{display:"flex", flexWrap:"wrap", alignItems:"center"}}>
                                <div style={{marginRight:"5%"}}>
                                    <TiTick size={40}></TiTick>
                                </div>
                                <div>
                                <p className="subtitle" style={{fontSize:"1rem"}}>Status</p>
                                <p className="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.status}</p>
                                </div> 
                            </div>
                        </div>
                    </div>
                </div>
                
            </div>
        </section>
        
        )}
        </>
        
      case 'Players':
        return <section className=" is-flex is-family-sans-serif fade-in" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
                <div className="card" style={{width:"95%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>

                    <table className="table is-hoverable custom-table" style={{width:"100%",paddingLeft:"10px"}}>
                        <thead>
                            <tr>
                            <th>Username</th>
                            <th>Elo</th>
                            </tr>
                        </thead>
                        <tbody>
                            {   
                                user.map((user, index) =>
                                    <tr>
                                        <td>{user.username}</td>
                                        <td>{user.elo}</td>
                                        
                                    </tr>
                                 )}
                            </tbody>
                    </table>
                </div>
            </section>;
      case 'Scoreboard':
        return <section className=" is-flex is-family-sans-serif fade-in" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
        <div className="card" style={{width:"90%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>

            <table className="table is-hoverable custom-table" style={{width:"100%",paddingLeft:"10px"}}>
                <thead>
                    <tr>
                    <th>Username</th>
                    <th>Score</th>
                    <th>Previous Games</th>
                    </tr>
                </thead>
                <tbody>
                    {   
                        user.map((user, index) =>
                            <tr>
                                <td>{user.username}</td>
                                <td>0</td>
                                <td>Nil</td>
                            </tr>
                         )}
                    </tbody>
            </table>
        </div>
    </section>;
      default:
        return null;
    }
  };

    
    const clearTokens = () => {
        localStorage.removeItem('token'); 
        localStorage.removeItem('tokenExpiry'); 
        
    };
    const loadTournament= async()=>{
        const token = localStorage.getItem('token');
        try {
            const result = await axios.get(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/${id}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            
        setTournament(result.data);
        setUser(result.data.participants);
        setHasJoined(checkJoinedTournamentFirst(result.data.participants));
        checkStatus(result.data.status);
        } catch (error) {
           
                alert("Tournament does not exist or tournament has been removed");
                navigate(`/user/${userId}/tournament`);
            
        }
       
       
    };


    const checkJoinedTournamentFirst = (users) => {
        for (let i = 0; i < users.length; i++){
            if (users[i].id == userId){
                return true;
            }
        }
        return false;
    }


 

    
    const removePlayer = async () => {
        try {
            const token = localStorage.getItem('token');
            const response1= await axios.put(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/${id}/participant/delete?user_id=${userId}`,

                {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (response1.status === 200){
                alert("Left Tournament Successfully");
                const joinedTournaments = JSON.parse(localStorage.getItem('joinedTournaments')) || {};
                delete joinedTournaments[id];
                loadUsers();
                loadTournament();
            }
            
        } catch (error) {
            
            setError('An error occurred while deleting the tournament.');
        }
    };
    const addPlayer = async () => {
        try {
            const token = localStorage.getItem('token');
            const decodedToken = jwtDecode(token);


            if (decodedToken.authorities === 'ROLE_USER'){
                
                const response1= await axios.put(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/${id}/participant/add?user_id=${userId}`,
                    {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (response1.status === 200){
                    loadUsers();
                    alert("Joined Tournament Successfully");
                    loadTournament();
                    
                }
            } 
            if (decodedToken.authorities === 'ROLE_ADMIN'){
                alert("You cannot join a tournament as an admin!");
            }
               
        } catch (error) {
            console.log(error);
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
            if ((decodedToken.authorities === 'ROLE_ADMIN' || decodedToken.authorities === 'ROLE_USER') && decodedToken.userId == userId){
                if (decodedToken.authorities === 'ROLE_ADMIN'){
                    setHasJoined(true);
                }
                return true;
            } else {
                
                return false;
            }
          
        } catch (error) {
            
            return false;
        }
    };
    
    const loadUsers = async () => {
        const token = localStorage.getItem('token');
        try {
            const response = await axios.get(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/${id}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            setUser(response.data.participants);
    } catch (error) {}
    };

    const checkStatus = (status) => {
        if (status == 'active') {
            return true;
        } else if (status == 'ongoing') {
            alert("Tournament has started");
            navigate(`/user/${userId}/tournament/${id}/start`);
        } else {
            alert("Tournament has ended");
            navigate(`/user/${userId}/tournament/${id}/ended`);
        }
    }

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
 
            
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                clearTokens();

                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/${id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.data.status == 'active') {
                    setData(response.data);
                    setIsLoading(false);
                    setTournament(response.data);
                    setUser(response.data.participants);
                    setHasJoined(checkJoinedTournamentFirst(response.data.participants));
                } else if (response.data.status == 'ongoing') {
                    alert("Tournament has started");
                    navigate(`/user/${userId}/tournament/${id}/start`);
                } else {
                    alert("Tournament has ended");
                    navigate(`/user/${userId}/tournament/${id}/ended`);
                }
                
                
            } catch (error) {
                if (error.response && error.response.status === 401) {
              
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setIsLoading(false);
                    console.log(error);
                }
            }
        };

        setTimeout(() => {
            fetchData();
        },1000);

        const socket = new SockJS('http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {

            stompClient.subscribe('/topic/tournamentCreate', () => {
                // Reload tournament data on match update
                console.log("Received new tournament data");
                loadTournament();
            
            });
        },(error) => {
            console.error("WebSocket connection error", error);
            //setConnectionStatus("Connection failed");
        });

        // Disconnect WebSocket on component unmount
        return () => {
            if (stompClient) stompClient.disconnect(() => {
                console.log("WebSocket connection closed");
                setConnectionStatus("Disconnected");
            });
        };
          
       

    }, []);

    

  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`,
        height: "100vh",
    }}> 
    <div className="content" style={{width:"100%", height:"100%",  backgroundColor:"rgba(0, 0, 0, 0.8)", overflow:"scroll"}}>
    <section className=" fade-in" style={{width:"100%", display:"flex", flexWrap:"wrap", padding:"10px", height:"20%", minHeight:"200px", marginBottom:"20px"}}>
        <div style={{display:"flex", justifyContent:'left', alignItems:"center",width:"70%", minWidth:"400px"}}>
            <div style={{width:"200px"}}>
                <img src={comp1} width={150}></img>
            </div>
            <div style={{width:"90%", alignContent:"center"}}>
                <p className="title is-family-sans-serif" style={{width:"80%", fontWeight:"bold"}}>{tournament.tournamentName}</p>
                <p className="subtitle">ID: {tournament.id}</p>
            </div>
        </div>
            <div style={{display:"flex", alignItems:"center", width:"30%",paddingleft:"10px", gap:"10px", justifyContent:"center", minWidth:"320px"}}>
                <button className="button is-link" disabled={hasJoined} onClick={() => addPlayer()} style={{ height:"40px",marginRight:"5%", fontWeight:"bold", width:"50%"}}>{hasJoined ? 'Joined' : 'Join Tournament'}</button>
                <button className="button is-danger" disabled={!hasJoined} onClick={() => removePlayer()} style={{width:"35%", height:"40px", fontWeight:"bold"}}>Leave</button>
            </div>
            
        </section>
        
        <section className="hero fade-in" style={{width:"100%", backgroundColor:"rgba(0, 0, 0, 0.8)", height:"80%", overflow:"scroll", marginBottom:"20px", minHeight:"600px"}}>
            <div style={{width:"100%", height:"20px"}}></div>
            <div className="tabs is-left" style={{ height:"10%", minHeight:"70px"}}>
              <ul>
                <li className={activeTab === 'Overview' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Overview')}>Overview</a>
                </li>
                <li className={activeTab === 'Players' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Players')}>Players</a>
                </li>
                <li className={activeTab === 'Scoreboard' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Scoreboard')}>Scoreboard</a>
                </li>
              </ul>
            </div>
            <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)", height:"90%", display:"flex", justifyContent:"center", width:"100%"}}>
              {renderTabContent()}
            </div>
          </section>
    </div>
    {isModalOpen && (
             <div className="modal is-active fade-in">
             <div className="modal-background"></div>
             <div className="modal-card animate__animated animate__fadeInUpBig"  style={{padding:"10px", marginTop:"100px"}}>
               <header className="modal-card-head">
                 <p className="modal-card-title">Swiss Tournament</p>
                 <button className="delete"  onClick={() => setIsModalOpen(false)} aria-label="close"></button>
               </header>
               <section className="modal-card-body" style={{height:"400px", overflowY:"scroll"}}>
                <img className="image is-2by1" src={swissPic}>
                </img>
                <div style={{marginTop:"20px"}}>
                    <p className="title">Swiss System</p>
                    <p style={{fontSize:"20px"}}>
                    The Swiss system in chess is a tournament format where players are paired based on their scores after each round, 
                    with winners playing other winners and losers playing other losers. No one is eliminated, and the goal is to have players of similar skill levels face off, allowing for a clear winner in fewer rounds than a round-robin format.
                    </p>
                </div>
                <div className="video-container" style={{marginTop:"20px",position: "relative", paddingBottom: "56.25%", height: 0, overflow: "hidden", maxWidth: "100%", background: "#000"}}>
                        <iframe 
                            style={{position: "absolute", top: 0, left: 0, width: "100%", height: "100%"}}
                            src="https://www.youtube.com/embed/vrxVSRlt-Qo" 
                            frameBorder="0" 
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                            allowFullScreen
                            title="Swiss System Video"
                        ></iframe>
                    </div>
               
               </section>
               <footer className="modal-card-foot">
                 <div className="buttons">
                   
                   <button className="button" onClick={() => setIsModalOpen(false)}>Cancel</button>
                 </div>
               </footer>
             </div>
           </div>
            )}
    </div>
    <footer className="footer" style={{textAlign:"center",height:"100px"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
