import 'fullstack-proj-frontend/src/Global.js';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import './style/TournamentPageStyle.css';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp'; 
import {Atom} from "react-loading-indicators"

export default function TournamentLandingPage() {
    const navigate = useNavigate();
    const[tournament,setTournament]=useState([]);
    const[pastTournament, setPastTournament]=useState([]);
    const [data, setData] = useState('');
    const [error, setError] = useState(null);
    const { userId } = useParams();
    const [isDropdownActive, setIsDropdownActive] = useState(false);
    const [selectedDropdownContent, setSelectedDropdownContent] = useState('Click to filter');
    const [isLoading, setIsLoading] = useState(true);

    

    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        // Add any other tokens you want to clear here
        // localStorage.removeItem('anotherToken');
        // tokenKeys.forEach(key => {
        //     localStorage.removeItem(key);
        // });
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
            console.log(decodedToken.authorities)
            return decodedToken.authorities === 'ROLE_ADMIN'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };

    const handleRowClick = (tournamentId, tournamentStatus) => {
        if (tournamentStatus === 'active') {
            navigate(`/admin/${userId}/tournament/${tournamentId}`);
        } else if (tournamentStatus === 'ongoing') {
            navigate(`/admin/${userId}/tournament/${tournamentId}/start`);
        } else if (tournamentStatus === 'completed') {
            navigate(`/admin/${userId}/tournament/${tournamentId}/completed`);
        }

    };

    const toggleDropdown = () => {
        setIsDropdownActive(!isDropdownActive);
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
                const response = await axios.get('http://localhost:8080/t/tournaments', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setIsLoading(false);
                if (response.data ==null){
                    setTournament(response.data);
                } else {
                    setTournament(response.data);
                }
                setData(response.data);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    alert('Your session has expired. Please login again.');
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 1000);
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };

        setTimeout(() => {
            fetchData();
            //loadTournaments();
        }, 2000);
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            console.log("WebSocket connection successful");
            
            stompClient.subscribe('/topic/tournamentCreate', () => {
                // Reload tournament data on match update
                console.log("Received tournament update");
                loadTournaments();
            });
        },(error) => {
            console.error("WebSocket connection error", error);
            
        });

        // Disconnect WebSocket on component unmount
        return () => {
            if (stompClient) stompClient.disconnect(() => {
                console.log("WebSocket connection closed");
                //setConnectionStatus("Disconnected");
            });
        };
        

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadTournamentsByName= async(content)=>{
        setSelectedDropdownContent(content);
        setIsDropdownActive(false);
        const result = await axios.get("http://localhost:8080/t/tournaments/name");
        console.log(result.data);
        if (!result.data.length == 0){
            setTournament(result.data);
        }
        else{
            setTournament([]);
        }
    };

    const loadTournamentsByDate= async(content)=>{
        setSelectedDropdownContent(content);
        setIsDropdownActive(false);
        const result = await axios.get("http://localhost:8080/t/tournaments/date");
        console.log(result.data);
        if (!result.data.length == 0){
            setTournament(result.data);
        }
        else{
            setTournament([]);
        }
    };

    const loadTournamentsByCapacity= async(content)=>{
        setSelectedDropdownContent(content);
        setIsDropdownActive(false);
        const result = await axios.get("http://localhost:8080/t/tournaments/capacity");
        console.log(result.data);
        if (!result.data.length == 0){
            setTournament(result.data);
        }
        else{
            setTournament([]);
        }
    };


    const loadTournaments= async()=>{
        const result = await axios.get("http://localhost:8080/t/tournaments");
        console.log(result.data);
        if (!result.data.length == 0){
            setTournament(result.data);
            setIsLoading(false);
        }
        else{
            setTournament([]);
            setIsLoading(false);
        }
    };

    const deleteTournament= async(tournament_id)=>{
        const token = localStorage.getItem('token');
        const result = await axios.delete(`http://localhost:8080/admin/tournament/${tournament_id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (result.status == 200){
            loadTournaments();
            alert("Tournament deleted successfully");

        }
    };
  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
        backgroundSize: 'cover', 
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
		flexWrap: 'wrap',
        marginTop:"80px",
        height:"100%",
    }}> 
    <div className="content container fade-in" style={{height:"auto",paddingTop:"100px", paddingBottom:"50px",margin:"0"}}>
        {/* <section className="hero">
            <div className="hero-body">
                <p className="title">Tournament</p>
            </div>
        </section> */}
        <section className="section is-large animate__animated animate__fadeInUpBig" style={{ paddingTop:"30px", backgroundColor:"rgba(0, 0, 0, 0.5)", borderRadius:"35px", height:"auto", overflowX:"scroll"}}>
            <div className="hero-body" style={{marginBottom:"5%"}}>
                <p className="title is-size-2 is-family-sans-serif">Tournament</p>
                <div style={{display:"flex", width:"100%"}}>
                    <div style={{width:"80%"}}>
                        <Link className="button is-link is-rounded" to={`/admin/${userId}/tournament/create`}>Create Tournament</Link>
                    </div>
                    <div style={{display:"flex", width:"50%", display:"flex", justifyContent:"center"}}>
                        <p>Filter by:</p>
                        <div style={{marginLeft:"20px"}} className={`dropdown ${isDropdownActive ? 'is-active' : ''}`}>
                            <div className="dropdown-trigger" >
                                <button className="button" aria-haspopup="true" aria-controls="dropdown-menu" onClick={toggleDropdown}>
                                    <span>{selectedDropdownContent}</span>
                                </button>
                            </div>
                            <div className="dropdown-menu" id="dropdown-menu" role="menu" style={{height:"100%", backgroundColor:"black"}}>
                                <div className="dropdown-content">
                                    <span style={{fontSize:"1.3rem"}} href="#" class="dropdown-item" onClick={()=>{loadTournamentsByName("Name")}}> Name</span>
                                    <span style={{fontSize:"1.3rem"}} href="#" class="dropdown-item" onClick={()=>{loadTournamentsByDate("Date")}}> Date</span>
                                    <span style={{fontSize:"1.3rem"}} href="#" class="dropdown-item" onClick={()=>{loadTournamentsByCapacity("Capacity")}}> Capacity</span>
                                </div>
                            </div>
                        </div>
                        
                    </div>
                </div>
                
            </div>
            {isLoading ? (
            <div style={{display:"flex", justifyContent:"center", alignItems:"center"}}>
                <Atom color="#9e34eb" size={100} style={{marginTop:"20%", marginLeft:"50%"}}></Atom>
             </div>    
                ) : (
                    tournament.length === 0 ? (
                        <div style={{textAlign: "center", marginTop: "20px"}}>
                            <p style={{fontSize:"20px"}}>No tournaments available. Create one!</p>
                        </div>
                    ) : (
            <table className="table is-hoverable custom-table animate__animated animate__fadeIn" >
                <thead>
                    <tr style={{height:"50px", paddingBottom:"5px"}}>
                        <th>ID</th>
                        <th>Tournament Name</th>
                        <th>Start Date</th>
                        <th>Status</th>
                        <th>Capacity</th>
                    </tr>
                </thead>
                <tbody>
                    {tournament.map((tournament, index) =>
                        <tr key={tournament.id} onClick={() => handleRowClick(tournament.id, tournament.status)}>
                            <td>{tournament.id}</td>
                            <td>{tournament.tournamentName}</td>
                            <td>{tournament.date}</td>
                            <td>{tournament.status}</td>
                            <td>{tournament.currentSize}  / {tournament.size}</td>
                            
                                <button className="button is-text" style={{marginTop:"20px", marginLeft:"20px"}} onClick={(event) => {
                                  event.stopPropagation();
                                  deleteTournament(tournament.id);
                                }}>Remove</button>
                         
                        </tr>   
                    )}
                </tbody>
            </table>
            ))}
        </section>
    </div>
    
    </div>

    <footer className="footer" style={{textAlign:"center"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
