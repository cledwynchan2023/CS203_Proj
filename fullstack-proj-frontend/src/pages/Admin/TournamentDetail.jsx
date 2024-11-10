import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp';
import comp1 from '/src/assets/comp1.png';
import chessplaying1 from '/src/assets/chessplaying.webp';
import "./style/TournamentDetailStyle.css";
export default function TournamentDetail() {
    const [sortButton, setSortButton] = useState(false);
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

    const convertToCreateTournamentRequest = (editedTournament) => {
        
        return {
          tournament_name: editedTournament.tournamentName,
          date: editedTournament.date,
          status: editedTournament.status,
          size: editedTournament.size,
          noOfRounds: editedTournament.noOfRounds || 0,
          currentSize: editedTournament.currentSize || 0,
        };
      };
    
    const onSubmit= async (e)=>{
        e.preventDefault();
        const tournamentData = {
            tournament_name,
            date,
            status,
            size,
            noOfRounds
        };
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
    const handleRowClick = (id) => {
        navigate(`/user/${userId}/profile/${id}`);
    }

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Overview':
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{width:"100%", height:"600px"}}>
            <div style={{display:"flex", justifyContent:"space-around", flexWrap:"wrap", gap:"5%"}}>
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

                <div style={{width:"50%", minWidth:"300px",display:"flex",justifyContent:"center", flexWrap:"wrap",gap:"5%", backgroundColor:"", alignContent:"center"}}>
                    <div style={{width:"100%", height:"50px", textAlign:"center"}}>
                        <p class="title is-4" >Tournament Details</p>
                    </div>
                    <div class="card" style={{width:"45%",height:"150px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Format</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>Swiss</p>
                            </div>
                        </div>
                    </div>
                    <div class="card" style={{width:"45%",height:"150px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Date</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.date}</p>
                            </div>
                        </div>
                    </div>
                    <div class="card" style={{width:"45%",height:"150px", minWidth:"250px"}}>
                        <div class="card-content">
                            <div class="content">
                                <p class="subtitle" style={{fontSize:"1rem"}}>Capacity</p>
                                <p class="title" style={{fontSize:"2rem", fontWeight:"bold"}}>{tournament.currentSize}/{tournament.size}</p>
                            </div>
                        </div>
                    </div>
                    <div class="card" style={{width:"45%",height:"150px", minWidth:"250px"}}>
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
        return <section className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{height:"600px",width:"100%", justifyContent:"center", margin:"0", padding:"0"}}>
            
                <div className="card" style={{width:"90%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>
                    <div style={{width:"100%",  display:"flex", marginBottom:"20px"}}>
                        <div style={{width:"50%", paddingLeft:"20px"}}>
                            <p className="subtitle is-family-sans-serif" style={{width:"100%", fontWeight:"bold"}}>Players: {user.length}/{tournament.size}</p>
                        </div>
                        <div style={{display:"flex", justifyContent:"flex-end", paddingRight:"20px", width:"50%"}}>
                            <button className="button is-link" style={{width:"80px", height:"30px"}} onClick={() => setIsModalOpen(true)}>Add</button>
                        </div>
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
        return <section className=" is-flex is-family-sans-serif animate__animated animate__fadeInUpBig" style={{height:"600px",width:"100%", justifyContent:"center"}}>
            
        <div className="card" style={{width:"90%", display:"flex", justifyContent:"start", paddingTop:"30px",height:"100%",overflowY:"scroll" }}>
        

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
                    {   
                        user.map((user, index) =>
                            <tr onClick={() => handleRowClick(user.id)}>
                                <td>{index + 1}</td>
                                <td>{user.username}</td>
                                <td>{user.elo}</td>
                                <td>0</td>
                                
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
        const result = await axios.get(`http://localhost:8080/t/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        
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
        const response = await axios.get(`http://localhost:8080/t/users/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        sortUserElo(response.data);
    };
   
    const deleteTournament= async(tournament_id)=>{
        const confirmation = window.confirm("Are you sure you want to delete this user?");
        if (!confirmation) return;
        const token = localStorage.getItem('token');
        const result = await axios.delete(`http://localhost:8080/admin/tournament/${tournament_id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (result.status == 200){
            
            alert("Tournament deleted successfully");
            navigate(`/admin/${userId}/tournament`);

        }
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
            return decodedToken.authorities === 'ROLE_ADMIN'; // Adjust this based on your token's structure
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
                const response = await axios.get(`http://localhost:8080/t/${id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setData(response.data);
                setTournament(response.data);
                console.log(response.data);
                
                if (response.data.status == 'active') {
                    setIsStart(0);
                } else if (response.data.status == 'ongoing') {
                    navigate(`/admin/${userId}/tournament/${id}/start`);
                } else {
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
        loadTournament();
        //loadUsers();

    }, []);

    const startTournament = async () => {
        const token = localStorage.getItem('token');
        const response = await axios.put(`http://localhost:8080/t/${id}/start`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

         if (isStart === 0) {
             navigate(`/admin/${userId}/tournament/${id}/start`);
         }
     };

    if (error) {
        return <div>{error}</div>;
    }

    const sortUserElo = (users) => {
        if (!sortButton) {
            setNonParticipatingUser(users.sort((a, b) => b.elo - a.elo));
            setSortButton(true);
        } else {
            setNonParticipatingUser(users.sort((a, b) => a.elo - b.elo));
            setSortButton(false);
        }
    }
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
    <div className="content" style={{width:"100%", height:"100%",  backgroundColor:"rgba(0, 0, 0, 0.8)", overflow:"scroll"}}>
        
        <section className=" fade-in" style={{width:"100%", display:"flex", flexWrap:"wrap", padding:"10px", height:"20%", minHeight:"200px"}}>
            <div style={{display:"flex", justifyContent:'left', alignItems:"center",width:"50%", minWidth:"400px"}}>
                <div style={{width:"100px"}}>
                    <img src={comp1} width={150}></img>
                </div>
                <div style={{width:"80%", alignContent:"center", display:"flex", flexWrap:"wrap", paddingLeft:"20px", minWidth:"300px"}}>
                    <p className="title is-family-sans-serif" style={{width:"80%", fontWeight:"bold"}}>{tournament.tournamentName}</p>
                    <p class="subtitle" style={{width:"100%"}}>ID: {tournament.id}</p>
                </div>
            </div>
            
            <div style={{display:"flex", alignItems:"center", width:"50%",paddingleft:"10px", gap:"10px", justifyContent:"center", minWidth:"320px"}}>
                <button className="button is-primary" onClick={startTournament} style={{minWidth:"100px",width:"30%", height:"40px", fontWeight:"bold"}} disabled={isStart === -1}>
                    {isStart === 0 ? 'Start' : isStart === 1 ? 'End' : 'Start'}
                </button>
                <button className="button is-link" onClick={() => setIsEditModalOpen(true)} style={{minWidth:"100px",width:"30%", height:"40px", fontWeight:"bold"}}>Edit</button>
                <button className="button is-danger" onClick={() => deleteTournament(id)} style={{width:"30%", height:"40px", fontWeight:"bold",minWidth:"100px"}}>Delete</button>
            </div>
            
        </section>
        {isModalOpen && (
              <div className="modal is-active fade-in">
              <div className="modal-background"></div>
              <div className="modal-card animate__animated animate__fadeInUpBig" style={{padding:"10px", marginTop:"100px"}}>
              <header class="modal-card-head" style={{height:"20%"}}>
                  <p className="modal-card-title" style={{paddingTop:"5%"}}>Add Player</p>
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
                    <button class="button" onClick={() => sortUserElo(nonParticpatingUser)}>Sort Elo</button>
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
                                <option value="completed">Completed</option>
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
        <section className="hero fade-in" style={{width:"100%", backgroundColor:"rgba(0, 0, 0, 0.8)", height:"80%", overflow:"scroll", marginBottom:"20px", minHeight:"600px"}}>

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
    </div>
    <footer className="footer" style={{textAlign:"center",height:"100px"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
