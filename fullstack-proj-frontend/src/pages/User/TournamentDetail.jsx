import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams } from 'react-router-dom';
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

export default function TournamentDetail() {
    const [isLoading, setIsLoading] = useState(true);
    const[user,setUser]=useState(null);
    const[nonParticpatingUser,setNonParticipatingUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const {userId} = useParams()
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Overview');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editedTournament,setEditedTournament] = useState({tournament_name:"", date:"", status:"active", size:"", noOfRounds:0});
    const {tournament_name, date, status, size, noOfRounds} = editedTournament;
    const [hasJoined, setHasJoined] = useState(false); 
    const onInputChange=(e)=>{
        setEditedTournament({...editedTournament, [e.target.name]:e.target.value});
        
    }


  const renderTabContent = () => {
    switch (activeTab) {
      case 'Overview':
        return <>
        {isLoading ? (
            <div style={{display:"flex", justifyContent:"center", height:"100%"}}>
                <Atom color="#9e34eb" size={100} style={{marginTop:"20%", marginLeft:"50%"}}></Atom>
            </div>
            
        ): (<section className="section is-flex is-family-sans-serif fade-in" style={{height:"80%",width:"100%", overflowY:"scroll"}}>
            <div style={{display:"flex", justifyContent:"space-around", flexWrap:"wrap"}}>
                <div class="card" style={{height:"700px", width:"30%", minWidth:"300px",marginright:"10px"}}>
                    <div class="card-image">
                        <figure class="image is-4by3">
                        <img
                            src={chessplaying1}
                            alt="Placeholder image"
                        />
                        </figure>
                    </div>
                    <div class="card-content">
                        <div class="media">
                        <div class="media-content">
                            <p class="title is-4">Game of Chess</p>
                        </div>
                        </div>

                        <div className="content">
                        Goal is to checkmate the opponent’s king. Players control 16 pieces each, including pawns, rooks, knights, bishops, a queen, and a king.
                        <br />
                        </div>
                    </div>
                </div>
                <div style={{height:"400px",width:"50%", minWidth:"400px",display:"flex",justifyContent:"center", flexWrap:"wrap",gap:"5%"}}>
                    <div className="card" style={{width:"45%", minWidth:"250px"}} onClick={() => setIsModalOpen(true)} >
                        <div className="card-content">
                        <div className="content" style={{ display: "flex", flexWrap: "wrap", alignItems: "center" }}>
                                <div style={{marginRight:"5%"}}>
                                    <IoMdInformationCircleOutline size={45}></IoMdInformationCircleOutline>
                                </div>
                                <div>
                                    <p className="subtitle" style={{fontSize:"1rem"}}>Format</p>
                                    <p className="title" style={{fontSize:"2rem", fontWeight:"bold"}}>Swiss</p>
                                </div>
                                
                            </div>
                        </div>
                    </div>
                    <div className="card" style={{width:"45%", minWidth:"250px"}}>
                        <div className="card-content">
                        <div className="content" style={{display:"flex", flexWrap:"wrap", alignItems:"center"}}>
                                <div style={{marginRight:"5%"}}>
                                    <IoCalendarNumberOutline size={45}></IoCalendarNumberOutline>
                                </div>
                                <div>
                                <p className="subtitle" style={{fontSize:"1rem"}}>Date</p>
                                <p className="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.date}</p>
                                </div> 
                            </div>
                        </div>
                    </div>
                    <div className="card" style={{width:"45%", minWidth:"250px"}}>
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
                    <div className="card" style={{width:"45%", minWidth:"250px"}}>
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
            {isModalOpen && (
             <div class="modal is-active fade-in">
             <div class="modal-background"></div>
             <div class="modal-card">
               <header class="modal-card-head">
                 <p class="modal-card-title">Swiss Tournament</p>
                 <button class="delete"  onClick={() => setIsModalOpen(false)} aria-label="close"></button>
               </header>
               <section class="modal-card-body" style={{height:"400px", overflowY:"scroll"}}>
                <img className="image is-2by1" src={swissPic}>
                </img>
                <div style={{marginTop:"20px"}}>
                    <p className="title">Swiss System</p>
                    <p>
                    If you like to play or watch chess tournaments, you've probably come across the term "Swiss system." 
                    But what exactly does it mean? 
                    Here's everything you need to know about this widely popular tournament format:
                    </p>
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
        </section>
        )}
        </>
      case 'Players':
        return <section className="section is-flex is-family-sans-serif fade-in" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
                <div className="card" style={{width:"80%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>

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
        return <div>Scoreboard Content</div>;
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
        const result = await axios.get(`http://localhost:8080/t/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        console.log(result.data);
        
        const resultName = result.data.tournamentName;
       
        setEditedTournament({
            tournament_name: resultName,
            date: result.data.date,                      
            status: result.data.status || "active",      
            size: result.data.size || "",                
            noOfRounds: result.data.noOfRounds || 0     
        });
    
        setTournament(result.data);
      
       
    };

 

    
    const removePlayer = async () => {
        try {
            const token = localStorage.getItem('token');
            console.log(id + " " + userId);
            const response1= await axios.put(`http://localhost:8080/t/${id}/participant/delete?user_id=${userId}`,
                {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (response1.status === 200){
                alert("Left Tournament Successfully");
                localStorage.setItem('joinedTournament', 'false');
                setHasJoined(false);
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
                
                const response1= await axios.put(`http://localhost:8080/t/${id}/participant/add?user_id=${userId}`,
                    {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (response1.status === 200){
                    localStorage.setItem('joinedTournament', 'true');
                    console.log(localStorage.getItem('joinedTournament'));
                    alert("Joined Tournament Successfully");
                    setHasJoined(true);
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
                console.log(isAdminToken(token));
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get(`http://localhost:8080/t/${id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
                setUser(response.data.participants);
                setIsLoading(false);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    console.log("Invalid TOken")
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setIsLoading(false);
                    console.log(error);
                    setError('An error occurred while fetching data.');
                }
            }
        };

        setTimeout(() => {
            fetchData();
            loadTournament();
            const buttonClicked = localStorage.getItem('joinedTournament');
            console.log(buttonClicked);
            if (buttonClicked === 'true') {
                setHasJoined(true);
            }
        }, 1000);
          
       

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`,
        height: "100vh",
    }}> 
    <div className="content" style={{width:"100%", height:"100%"}}>
        <section className="hero is-flex-direction-row" style={{paddingLeft:"5%", paddingRight:"5%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.5)"}}>
            <div style={{width:"200px"}}>
                <img src={comp1} width={150}></img>
            </div>
            <div style={{width:"90%", alignContent:"center"}}>
                <p className="title is-family-sans-serif" style={{width:"80%", fontWeight:"bold"}}>{tournament.tournamentName}</p>
                <p class="subtitle">ID: {tournament.id}</p>
            </div>
            <div style={{alignContent:"center",width:"500px"}}>
                <button className="button is-link" disabled={hasJoined} onClick={() => addPlayer()} style={{ height:"40px",marginRight:"5%", fontWeight:"bold"}}>{hasJoined ? 'Joined' : 'Join Tournament'}</button>
                <button className="button is-danger" disabled={!hasJoined} onClick={() => removePlayer()} style={{width:"45%", height:"40px", fontWeight:"bold"}}>Leave</button>
            </div>
            
        </section>
        
        <section className="hero" style={{paddingLeft:"2%", paddingRight:"2%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.8)", height:"100%"}}>
            <div style={{width:"100%", height:"20px"}}></div>
            <div className="tabs is-left" style={{ height:"70px"}}>
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
            <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)", height:"90%"}}>
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
