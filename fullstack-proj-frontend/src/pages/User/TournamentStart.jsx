import 'fullstack-proj-frontend/src/Global.js';
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import "./style/TournamentStart.css";
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';


export default function TournamentStart() {
    const navigate = useNavigate();
    const[user,setUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const{userId} = useParams()
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Overview');
    const [editedTournament,setEditedTournament] = useState({tournament_name:"", date:"", status:"active", size:"", noOfRounds:0});
    const {tournament_name, date, status, size, noOfRounds} = editedTournament;
    const [isStart, setIsStart] = useState(1);
    const [nonParticipatingUser, setNonParticipatingUser] = useState([]);
    const [scoreboard,setScoreboard]=useState(new Map());
    const [userPairings, setUserPairings] = useState([]);    
    const [tournamentRound, setTournamentRound] = useState(1);  
    const[pairing, setPairing] = useState([]);
    const[round, setRound] = useState([]);  
    const [currentRound, setCurrentRound] = useState(tournamentRound);
    const [disabledButtons, setDisabledButtons] = useState({});
    
   
    const handlePageClick = (round) => {

        if (round > tournamentRound){
            return;
        } else if (round <= tournamentRound){
            setCurrentRound(round);
            setPairing(tournament.rounds[round-1].matchList);
            setUserPairings(findUserPairingFirst(tournament.rounds[round-1].matchList));
            setRound(tournament.rounds[round-1]);
           
        }
        
    };
    const renderPagination = () => {
        const pages = [];
        for (let i = 1; i <= tournament.noOfRounds; i++) {
            const isCurrentRound = i === tournamentRound;
            const isPastRound = i < tournamentRound;
            pages.push(
                <li key={i}>
                    <a
                        href="#"
                        className={`pagination-link ${isCurrentRound ? 'is-current' : ''}`}
                        aria-label={`Goto page ${i}`}
                        aria-current={isCurrentRound ? 'page' : undefined}
                        style={{
                            color: isCurrentRound ? 'black' : isPastRound ? 'blue' : 'grey',
                            pointerEvents: isCurrentRound || isPastRound ? 'auto' : 'none',
                            cursor: isCurrentRound || isPastRound ? 'pointer' : 'default'
                        }}
                        onClick={() => handlePageClick(i)}
                    >
                        {i}
                    </a>
                </li>
            );
        }
        return pages;
    };


    const getUsername = (selectedId) => {
        for (let i = 0; i < user.length; i++) {
            if (user[i].id === selectedId) {
                return user[i].username;
            }
        }
        return user ? user.username : '';
    };

    const getUser = (selectedId) => {
        
        for (let i = 0; i < user.length; i++) {
            
            if (user[i].id - selectedId == 0) {

                return user[i];
            }
        }
        return user ? user : '';
    };
    

  const renderTabContent = () => {
    const sortedScoreboard = Array.from(scoreboard.entries()).sort((a, b) => b[1] - a[1]);
    switch (activeTab) {
      case 'Overview':
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{width:"100%", overflowY:"scroll", height:"100%", marginBottom:"50px", paddingTop:"0"}}>
            <div style={{width:"100%", height:"60%"}}>
                    <div style={{width:"100%", height:"50px"}}>
                        <p className="title is-2">Round {currentRound}</p>
                    </div>
                    <nav className="pagination" role="navigation" aria-label="pagination" style={{display:"flex",alignItems:"center", height:"auto", width:"100%", marginTop:"20px"}}>
                        <a
                            className={`pagination-previous ${currentRound === 1 ? 'is-disabled' : ''}`}
                            title="Previous Round"
                            onClick={() => currentRound > 1 && handlePageClick(currentRound - 1)}
                        >
                            Previous Round
                        </a>
                        <a  className={`pagination-previous ${currentRound === tournamentRound ? 'is-disabled' : ''}`}
                            title="Next Round"
                            onClick={() => currentRound >= 1 && handlePageClick(currentRound + 1)}>Next round</a>
                        <ul className="pagination-list" style={{height:"auto", marginBottom:"10px", marginLeft:"0"}}>
                            {renderPagination()}
                        </ul>
                    </nav>
                    {user.filter(user => user.id - userId == 0).map( user =>(
                    <div className="card" style={{width:"100%", minWidth:"400px",height:"300px", marginBottom:"50px", border:"5px solid purple"}}>
                            <div style={{textAlign:"center"}}> 
                                <p className="title" style={{fontSize:"2rem", fontWeight:"bold", width:"100%", paddingTop:"10px"}}>Your Match</p>
                            </div>
                        <div className="card-content" style={{display:"flex",alignItems:"center",justifyContent:"center",overflowY:"hidden", overflowX:"scroll", height:"100%", width:"100%",gap:"5%"}}>
                            
                            <div className="content" style={{margin:"0",width:"25%", textAlign:"center", height:"100px"}}>
                                <p className="subtitle" style={{fontSize:"1rem"}}>{"Id: " + userPairings.player1}</p>
                                <p className="title" style={{fontSize:"1.8rem", fontWeight:"bold"}}>{userPairings.player1 - userId == 0 ? 'You' : getUsername(userPairings.player1)}</p>
                            </div>
                            <div style={{width:"15%", display:"flex", alignItems:"center", justifyContent:"center"}}>
                                <p className="title" style={{fontSize:"2.5rem", fontWeight:"bold",textAlign:"center", whiteSpace:"nowrap"}}>VS</p>
                            </div>
                            <div className="content" style={{margin:"0",width:"25%", textAlign:"center", height:"100px"}}>
                                <p className="subtitle" style={{fontSize:"1rem"}}>{"Id "+ userPairings.player2}</p>
                                <p className="title" style={{fontSize:"1.8rem", fontWeight:"bold"}}>{userPairings.player2 - userId == 0 ? 'You' : getUsername(userPairings.player2)}</p>
                            </div>
                        </div>
                    </div>
                    ))}
                    <div>
                        <p className="title" style={{fontSize:"1.5rem", fontWeight:"bold", width:"100%", paddingLeft:"10px", marginBottom:"20px"}}>All Matches' Results</p>
                    </div>
                    {pairing.map((pair, index) => {
                        const backgroundColor = index % 2 === 0 ? '#252525' : '#212121'; // Alternate colors
                        const matchId = round.matchList[index].id; // Assuming each pair has a unique matchId
                        const isDisabled = round.matchList[index].complete;
                        const matchResult = round.matchList[index].result;
                        const getBorderStyle = (playerIndex) => {
                            if (matchResult === 0) {
                                return { border: '5px solid yellow', borderRadius: '20px', backgroundColor:"grey" };
                            } else if (matchResult === -1 && playerIndex === 1) {
                               
                                return { border: '5px solid green', borderRadius: '20px', backgroundColor:"green" };
                            } else if (matchResult === 1 && playerIndex === 2) {
                                return { border: '5px solid green',borderRadius: '20px',  backgroundColor:"green" };
                            } 

                            if (matchResult == -1 && playerIndex ===2){
                                return { border: '5px solid red', borderRadius: '20px', backgroundColor:"red" };
                            } else if (matchResult === 1 && playerIndex === 1) {
                                return { border: '5px solid red', borderRadius: '20px', backgroundColor:"red" };
                            }
                        };
                        const getStatus = (result) => {
                            if (result === null) {
                                return 'Ongoing';
                            } else if (result === -1) {
                                return 'Player 1 Won';
                            } else if (result === 1) {
                                return 'Player 2 Won';
                            } else if (result === 0) {
                                return 'Draw';
                            } else {
                                return 'Unknown Status';
                            }
                        };
                        return (
                            
                            <div className="card" style={{width:"100%", minWidth:"300px",height:"auto", display:"flex", alignItems:"center", marginBottom:"-10px", backgroundColor:""}}>
                       
                       <div className="card-content" style={{display:"flex", justifyContent:"center", overflowX:"scroll", height:"100%", width:"100%", flexWrap:"wrap", backgroundColor:""}}>
                            <div style={{width:"80%", display:'flex', minWidth:"300px", justifyContent:"center", backgroundColor:""}}>
                                <div className="content" style={{width:"35%", textAlign:"center",height:"100%", ...getBorderStyle(1)}}>
                                    <p className="subtitle" style={{fontSize:"1rem"}}>{"Id: " + pair.player1}</p>
                                    <p className="title" style={{fontSize:"1.8rem", fontWeight:"bold"}}>{getUsername(pair.player1)}</p>
                                </div>
                                <div style={{width:"30%", display:"flex", alignItems:"center", justifyContent:"center"}}>
                                    <p className="title" style={{fontSize:"2rem", fontWeight:"bold",textAlign:"center"}}>VS</p>
                                </div>
                                <div className="content" style={{width:"35%", textAlign:"center", height:"100%", ...getBorderStyle(2)}}>
                                    <p className="subtitle" style={{fontSize:"1rem"}}>{"Id: " + pair.player2}</p>
                                    <p className="title" style={{fontSize:"1.8rem", fontWeight:"bold"}}>{getUsername(pair.player2)}</p>
                                </div>
                               
                            </div>
                            <div className="content" style={{backgroundColor:"", width:"20%", justifyContent:"center", display:"flex", alignItems:"center", gap:"3%", minWidth:"300px", marginTop:"20px"}}>
                                <div style={{height:"100%", margin:"0", width:"200px", textAlign:'center' }}>
                                    <p className="subtitle">Status: {getStatus(matchResult)}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                )})}
            </div>
        </section>;
         
      case 'Players':
        return <section className=" is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{height:"600px",width:"100%", justifyContent:"center"}}>
                
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
        return <>
        <section className=" is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
        <div className="card" style={{width:"80%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>
        

            <table className="table is-hoverable custom-table" style={{width:"100%",paddingLeft:"10px"}}>
                <thead>
                    <tr>
                    <th>Pos</th>
                    <th>Username</th>
                    <th>Elo</th>
                    <th>Score</th>
                    </tr>
                </thead>
                <tbody>
                {Array.from(scoreboard.entries()).reverse().map(([id, score], index) => {
                    const user = getUser(id); // Assuming you have a users array with user details
                    return (
                <tr key={id}>
                    <td>{index + 1}</td>
                    <td>{user ? user.username : 'Unknown'}</td>
                    <td>{user ? user.elo : 'Unknown'}</td>
                    <td>{score}</td>
                </tr>
            );
        })}
                    </tbody>
            </table>
        </div>
       
    </section>;
    </>
      default:
        return null;
    }
  };

    
    const clearTokens = () => {
        localStorage.removeItem('token'); 
        localStorage.removeItem('tokenExpiry'); 
       
    };


    const findUserPairingFirst = (pairings) => {
        for (let i =0; i < pairings.length; i++){
            if (pairings[i].player1 - userId == 0 || pairings[i].player2 - userId == 0){
                return pairings[i];
            }
        }
        return null;
    };
    const loadTournament= async()=>{
        const token = localStorage.getItem('token');
        const result = await axios.get(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/tournament/${id}/start`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
  
        const resultName = result.data.tournamentName;
       
    
        setTournament(result.data);

        setTournamentRound(result.data.currentRound);
        setScoreboard(new Map(result.data.rounds[result.data.currentRound - 1].scoreboard.scoreboardEntries.map(entry => [entry.playerId, entry.score])));
        setRound(result.data.rounds[result.data.currentRound-1]);
        setCurrentRound(result.data.currentRound);
       
       
        setPairing(result.data.rounds[result.data.currentRound-1].matchList);
        setUserPairings(findUserPairingFirst(result.data.rounds[result.data.currentRound-1].matchList));
        loadNonParticipatingUsers();
        setUser(result.data.participants);

        if (result.data.status == 'completed') {
            alert("Tournament has ended");
            navigate(`/user/${userId}/tournament/${id}/ended`);

        }
        
        
    };


    const loadNonParticipatingUsers = async () => {
        const token = localStorage.getItem('token');
    
        const response = await axios.get(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/users/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        setNonParticipatingUser(response.data);
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
 
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get(`http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/t/tournament/${id}/start`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
                setCurrentRound(response.data.currentRound);
                setRound(response.data.rounds[response.data.currentRound-1]);
                
                setPairing(response.data.rounds[response.data.currentRound-1].matchList);
                setUserPairings(findUserPairingFirst(response.data.rounds[response.data.currentRound-1].matchList));
                setTournament(response.data);
                setScoreboard(new Map(response.data.rounds[response.data.currentRound - 1].scoreboard.scoreboardEntries.map(entry => [entry.playerId, entry.score])));
                setTournamentRound(response.data.currentRound);
                
                setUser(response.data.participants);
                if (response.data.status == 'active') {
                    alert("Tournament has not started yet");
                    navigate(`/user/${userId}/tournament/${id}`);

                } else if (response.data.status == 'ongoing') {
                    setIsStart(1);
                } else {
                    alert("Tournament has ended");
                    navigate(`/user/${userId}/tournament/${id}/ended`);
                    setIsStart(-1);
                }
               
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
        loadNonParticipatingUsers();
        const socket = new SockJS('http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/ws');

        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
       
            setConnectionStatus("Connected");
            stompClient.subscribe('/topic/matchUpdates', () => {
                // Reload tournament data on match update
                console.log("Received match update");
                loadTournament();
            });
        },(error) => {
           
            setConnectionStatus("Connection failed");

        });

        // Disconnect WebSocket on component unmount
        return () => {
            if (stompClient) stompClient.disconnect(() => {
                console.log("WebSocket connection closed");
                setConnectionStatus("Disconnected");
            });
        };
        
        //loadUsers();

    }, []);
    
    if (error) {
        return <div>{error}</div>;
    };
    

  return (
    <>
    <div className="background-container" style={{ 
       backgroundImage: `url(${backgroundImage})`, 
       backgroundSize: 'cover', 
       backgroundPosition: 'center',
       backgroundRepeat: 'no-repeat',
       flexWrap: 'wrap',
       height:"100vh",
    }}> 
    <div className="content" style={{width:"100%", height:"100%",  backgroundColor:"rgba(0, 0, 0, 0.7)", overflow:"scroll"}}>
        <section className=" fade-in" style={{width:"100%", display:"flex", flexWrap:"wrap", padding:"10px", height:"20%", minHeight:"100px", paddingLeft:"20px", paddingRight:"20px"}}>
            <div style={{display:"flex", justifyContent:'left', alignItems:"center",width:"100%", minWidth:"400px",}}>   
                <div style={{width:"100px"}}>
                    <img src={comp1} width={150}></img>
                </div>
                <div style={{width:"80%", alignContent:"center", display:"flex", flexWrap:"wrap", paddingLeft:"20px", minWidth:"300px",}}>
                    <p className="title is-family-sans-serif" style={{width:"100%", fontWeight:"bold"}}>{tournament.tournamentName}</p>
                    <p className="subtitle" style={{width:"100%"}}>ID: {tournament.id}</p>
                </div>
            </div>
            
            
        </section>
        <section className="hero fade-in" style={{width:"100%", backgroundColor:"rgba(0, 0, 0, 0.8)", height:"80%", marginBottom:"20px", minHeight:"600px",}}>

            <div className="tabs is-left" style={{ height:"10%", minHeight:"70px", }}>
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
            <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)", height:"90%", width:"100%"}}>
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
