import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import chessplaying1 from '/src/assets/chessplaying.webp';
import "./style/TournamentDetailStyle.css";
export default function TournamentDetail() {
    const[user,setUser]=useState([]);
    const[nonParticpatingUser,setNonParticipatingUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const{userId} = useParams()
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Overview');
    const [isModalOpen, setIsModalOpen] = useState(false);

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Overview':
        return <section className="section is-flex is-family-sans-serif fade-in" style={{width:"100%", overflowY:"scroll", height:"600px", marginBottom:"50px"}}>
            <div style={{display:"flex", justifyContent:"space-around", flexWrap:"wrap"}}>
                <div class="card" style={{width:"30%", minWidth:"300px",marginright:"10px"}}>
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

                        <div class="content">
                        Goal is to checkmate the opponent’s king. Players control 16 pieces each, including pawns, rooks, knights, bishops, a queen, and a king.
                        <br />
                        </div>
                    </div>
                </div>
                <div style={{height:"400px",width:"50%", minWidth:"400px",display:"flex",justifyContent:"center", flexWrap:"wrap",gap:"5%"}}>
                    <div class="card" style={{width:"45%",height:"100px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Format</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>Swiss</p>
                            </div>
                        </div>
                    </div>
                    <div class="card" style={{width:"45%",height:"100px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Date</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.date}</p>
                            </div>
                        </div>
                    </div>
                    <div class="card" style={{width:"45%",height:"100px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Capacity</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.currentSize}/{tournament.size}</p>
                            </div>
                        </div>
                    </div>
                    <div class="card" style={{width:"45%",height:"100px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Status</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.status}</p>
                            </div>
                        </div>
                    </div>
                </div>
                
            </div>
        </section>;
      case 'Players':
        return <section className="section is-flex is-family-sans-serif fade-in" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
                <div className="card" style={{width:"80%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>
                <div style={{display:"flex", justifyContent:"flex-end", paddingRight:"20px"}}>
                    <button className="button is-link" style={{width:"80px", height:"30px"}} onClick={() => setIsModalOpen(true)}>Add</button>
                </div>

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
                                        <td style={{display:"flex", justifyContent:"flex-end"}}>
                                            <button className="button is-text" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginBottom:"25px" }} onClick={(event) => {removePlayer(user.id);
                                            event.stopPropagation();
                                            }}>Remove</button>
                                        </td>
                                    </tr>
                                 )}
                            </tbody>
                    </table>
                </div>
                {isModalOpen && (
              <div class="modal is-active fade-in">
              <div class="modal-background"></div>
              <div class="modal-card">
                <header class="modal-card-head">
                  <p class="modal-card-title">Add Player</p>
                  <button class="delete"  onClick={() => setIsModalOpen(false)} aria-label="close"></button>
                </header>
                <section class="modal-card-body" style={{height:"400px"}}>
                <table className="table is-hoverable" >
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Elo</th>
                    </tr>
                </thead>
                <tbody>
                    {nonParticpatingUser.map((user, index) =>
                        <tr key={user.id} onClick={() => addPlayer(user.id)}>
                            <td>{user.username}</td>
                            <td>{user.elo}</td>
                        </tr>   
                    )}
                </tbody>
            </table>
                </section>
                <footer class="modal-card-foot">
                  <div class="buttons">
                    <button class="button is-success">Save changes</button>
                    <button class="button" onClick={() => setIsModalOpen(false)}>Cancel</button>
                  </div>
                </footer>
              </div>
            </div>
            )}
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
        const result = await axios.get(`http://localhost:8080/t/${id}`);
        setTournament(result.data);
        setUser(result.data.participants);
        console.log(result.data);
    };

    const loadNonParticipatingUsers = async () => {
        const token = localStorage.getItem('token');
        const response = await axios.get(`http://localhost:8080/t/users/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        console.log(response.data);
        setNonParticipatingUser(response.data);
    };
    // const deleteTournament = async (id) => {
    //     try {
    //         if (user.length > 0) {
    //             setError('Cannot delete a tournament with participants.');
    //             return;
    //         }
    //         const response = await axios.delete(`http://localhost:8080/t/tournament/${id}`);
    //         // Refresh the tournament list after deletion
    //         if (response.status === 200){
    //             alert("Tournament Deleted Successfully");
    //             loadTournaments();
    //             Navigate(`/admin/${userId}/tournament`);
    //         }
            
    //     } catch (error) {
    //         setError('An error occurred while deleting the tournament.');
    //     }
    // };

    const removePlayer = async (user_id) => {
        try {
            const token = localStorage.getItem('token');
            const response1= await axios.put(`http://localhost:8080/t/${id}/participant/delete?user_id=${user_id}`,
                {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (response1.status === 200){
                alert("Player Removed Successfully");
                loadTournament();
            }
            
        } catch (error) {
            
            setError('An error occurred while deleting the tournament.');
        }
    };
    const addPlayer = async (user_id) => {
        try {
            console.log(user_id);
            const token = localStorage.getItem('token');
            const response1= await axios.put(`http://localhost:8080/t/${id}/participant/add?user_id=${user_id}`,
                {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (response1.status === 200){
                setIsModalOpen(false);
                alert("Player added Successfully");
                loadTournament();
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
            console.log(decodedToken.authorities)
            return decodedToken.authorities === 'ROLE_ADMIN'; // Adjust this based on your token's structure
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
                const response = await axios.get('http://localhost:8080/t/tournaments', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
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
        loadTournament();
        loadNonParticipatingUsers();
        //loadUsers();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
    }}> 
    <div className="content" style={{width:"100%"}}>
        <section className="hero is-flex-direction-row" style={{paddingLeft:"5%", paddingRight:"5%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.5)"}}>
            <div style={{width:"200px"}}>
                <img src={comp1} width={150}></img>
            </div>
            <div style={{width:"90%", alignContent:"center"}}>
                <p className="title is-family-sans-serif" style={{width:"80%", fontWeight:"bold"}}>{tournament.tournamentName}</p>
                <p class="subtitle">ID: {tournament.id}</p>
            </div>
            <div style={{alignContent:"center",width:"300px"}}>
                <button className="button is-link" style={{width:"45%", height:"40px",marginRight:"5%"}}>Edit</button>
                <button className="button is-danger" style={{width:"45%", height:"40px"}}>Delete</button>
            </div>
            
        </section>

        <section className="hero" style={{paddingLeft:"2%", paddingRight:"2%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.8)"}}>
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
            <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)"}}>
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
