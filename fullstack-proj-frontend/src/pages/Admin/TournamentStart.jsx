import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import chessplaying1 from '/src/assets/chessplaying.webp';
import "./style/TournamentDetailStyle.css";
import { TiSocialSkypeOutline } from 'react-icons/ti';
export default function TournamentStart() {
    const navigate = useNavigate();
    const[user,setUser]=useState([]);
    const[nonParticpatingUser,setNonParticipatingUser]=useState([]);
    const[tournament,setTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const{userId} = useParams()
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Overview');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editedTournament,setEditedTournament] = useState({tournament_name:"", date:"", status:"active", size:"", noOfRounds:0});
    const {tournament_name, date, status, size, noOfRounds} = editedTournament;
    const [isStart, setIsStart] = useState(0);
    const onInputChange=(e)=>{
        setEditedTournament({...editedTournament, [e.target.name]:e.target.value});
        
    }
    const [tournamentRound, setTournamentRound] = useState(1);  
    const[pairing, setPairing] = useState([]);
    const[round, setRound] = useState([]);  

    
    const onSubmit= async (e)=>{
        e.preventDefault();
        console.log(editedTournament);
        const tournamentData = {
            tournament_name,
            date,
            status,
            size,
            noOfRounds
        };
        console.log(tournamentData.tournament_name);
        try {
            const response = await axios.put(`http://localhost:8080/t/${id}`, tournamentData);
            if (response.status === 200){
                alert("Tournament Edited Successfully");
                setIsEditModalOpen(false);
                loadTournament();
            }
            
        } catch (error) {
            console.error("There was an error registering the tournament!", error);
        }
        
    }

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Overview':
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{width:"100%", overflowY:"scroll", height:"100%", marginBottom:"50px"}}>
            <div style={{width:"100%", display:"flex", flexWrap:"wrap", height:"60%"}}>
                    <div style={{width:"100%", height:"100px"}}>
                        <p className="title is-2">Round {tournamentRound}</p>
                    </div>
                    {pairing.map((pair, index) =>
                    <div class="card" style={{width:"100%", minWidth:"400px",height:"120px"}}>
                        <div class="card-content" style={{display:"flex", justifyContent:"center"}}>
                            
                            
                            <div class="content" style={{width:"30%", textAlign:"center"}}>
                                <p class="subtitle" style={{fontSize:"1rem"}}>{pair.player1}</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>Player 1</p>
                            </div>
                            <div style={{width:"20%", display:"flex", alignItems:"center", justifyContent:"center"}}>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold",textAlign:"center"}}>VS</p>
                            </div>
                            <div class="content" style={{width:"30%", textAlign:"center"}}>
                                <p class="subtitle" style={{fontSize:"1rem"}}>{pair.player2}</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>Player 2</p>
                            </div>
                            <div class="content" style={{width:"20%", textAlign:"center", display:"flex", alignItems:"center", gap:"5%"}}>
                                <button className="button is-link">Player 1</button>
                                <button className="button is-primary">Player 2</button>
                            </div>
                            
                            
                        </div>
                    </div>
                    )}
            </div>
        </section>;
      case 'Players':
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
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
        loadNonParticipatingUsers();
        setUser(result.data.participants);
        
        
    };

    const loadNonParticipatingUsers = async () => {
        const token = localStorage.getItem('token');
        console.log("id is" +id);
        const response = await axios.get(`http://localhost:8080/t/users/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        setNonParticipatingUser(response.data);
    };

    

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
                //setIsModalOpen(false);
                alert("Player added Successfully");
                loadTournament();
               
            }
            
        } catch (error) {
            alert("Tournament Full");
            
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
                const response = await axios.get(`http://localhost:8080/t/tournament/${id}/start`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
                
                setRound(response.data.rounds);
                setPairing(response.data.rounds[tournamentRound-1].matchList);
                console.log(pairing[0]);

                setTournament(response.data);
                setUser(response.data.participants);
                if (response.data.status == 'active') {
                    alert("Tournament has not started yet");
                    navigate(`/admin/${userId}/tournament/${id}`);

                } else if (response.data.status == 'ongoing') {
                    setIsStart(1);
                } else {
                    alert("Tournament has ended");
                    navigate(`/admin/${userId}/tournament/${id}`);
                    setIsStart(-1);
                }
                console.log(isStart);
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
        
        //loadUsers();

    }, []);
    
    if (error) {
        return <div>{error}</div>;
    };
    

  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
    }}> 
    <div className="content" style={{width:"100%", height:"100%", overflowY:"scroll"}}>
        <section className="hero is-flex-direction-row fade-in" style={{paddingLeft:"5%", paddingRight:"5%", width:"100%", backgroundColor:"rgba(0, 0, 0, 0.5)"}}>
            <div style={{width:"200px"}}>
                <img src={comp1} width={150}></img>
            </div>
            <div style={{width:"80%", alignContent:"center"}}>
                <p className="title is-family-sans-serif" style={{width:"80%", fontWeight:"bold"}}>{tournament.tournamentName}</p>
                <p class="subtitle">ID: {tournament.id}</p>
            </div>
            <div style={{display:"flex",alignItems:"center", backgroundColor:"black", width:"20%"}}>
                <button className="button is-primary" style={{width:"55%", height:"40px",marginRight:"5%", fontWeight:"bold", color:"white"}} disabled={isStart === -1}>
                    {isStart === 0 ? 'Start' : isStart === 1 ? 'End' : 'Start'}
                </button>
                <button className="button is-link" onClick={() => setIsEditModalOpen(true)} style={{width:"45%", height:"40px",marginRight:"5%", fontWeight:"bold"}}>Edit</button>
            </div>
            
        </section>
        {isModalOpen && (
              <div className="modal is-active fade-in">
              <div className="modal-background"></div>
              <div className="modal-card animate__animated animate__fadeInUpBig">
                <header class="modal-card-head">
                  <p className="modal-card-title">Add Player</p>
                  <button className="delete"  onClick={() => setIsModalOpen(false)} aria-label="close"></button>
                </header>
                <section className="modal-card-body" style={{height:"400px"}}>
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
                    <button class="button is-success" onClick={() => {setIsModalOpen(false); loadTournament()}}>Save changes</button>
                    <button class="button" onClick={() => setIsModalOpen(false)}>Cancel</button>
                  </div>
                </footer>
              </div>
            </div>
            )}
        {isEditModalOpen && (
              <div class="modal is-active fade-in">
              <div class="modal-background"></div>
              <div class="modal-card">
                <header class="modal-card-head">
                  <p class="modal-card-title">Edit Tournament</p>
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
                            value={tournament_name}
                            onChange={(e) =>onInputChange(e)}
                            name="tournament_name"
                        ></input>
                        <label htmlFor="tournament_name">Tournament Name</label>
                        </div>
                        
                        <div className="form-floating mb-3">
                        <input
                            type="text"
                            className="form-control"
                            id="floatingUsername"
                            placeholder="Date"
                            value={date}
                            onChange={(e) =>onInputChange(e)}
                            name="date"
                        />
                        <label htmlFor="Date">Date</label>

                        </div>
                        <div className="form-floating mb-3 mt-3">
                            <select
                                className="form-control"
                                id="floatingRole"
                                value={status}
                                onChange={(e) => onInputChange(e)}
                                name="status"
                            >
                                <option value="active">Active</option>
                                <option value="completer">Not Active</option>
                                <option value="ongoing">Ongoing</option>
                            </select>
                            <label htmlFor="Status">Status</label>
                        </div>
                        <div className="form-floating mb-3">
                        <input
                            type="number"
                            className="form-control"
                            placeholder="size"
                            value={size}
                            onChange={(e) =>onInputChange(e)}
                            name="size"
                        />
                        <label htmlFor="size">Number of participants</label>
                        </div>
                        <div className="form-floating">
                        <input
                            type="number"
                            className="form-control"
                            placeholder="noOfRounds"
                            value={noOfRounds}
                            onChange={(e) =>onInputChange(e)}
                            name="noOfRounds"
                        />
                        <label htmlFor="noOfRounds">Number of rounds</label>
                        </div>
                        <div style={{marginTop:"5%"}}>
                        <button type="submit" className='button is-link is-fullwidth'>Edit Tournament</button>
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
                    <li className={activeTab === 'Players' ? 'is-active' : ''}>
                    <a onClick={() => setActiveTab('Players')}>Players</a>
                    </li>
                    <li className={activeTab === 'Scoreboard' ? 'is-active' : ''}>
                    <a onClick={() => setActiveTab('Scoreboard')}>Scoreboard</a>
                    </li>
                </ul>
                </div>
                <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)", height:"100%", margin:"0", width:"100%"}}>
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
