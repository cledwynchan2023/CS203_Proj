import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

import { Link, useParams, useNavigate } from 'react-router-dom';

const TournamentUser = () => {
    const navigate = useNavigate();
    const[currentTournament,setCurrentTournament]=useState([]);
    const[tournament,setTournament]=useState([]);
    const[pastTournament, setPastTournament]=useState([]);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const { id } = useParams();
    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        // Add any other tokens you want to clear here
        // localStorage.removeItem('anotherToken');
        // tokenKeys.forEach(key => {
        //     localStorage.removeItem(key);
        // });
    };

    const joinTournament = async (tournamentId) => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.put(`http://localhost:8080/auth/tournament/${tournamentId}/participant/add?user_id=${id}`);
            const response1 = await axios.put(`http://localhost:8080/user/${id}/participating_tournament/add?tournament_id=${tournamentId}`,{}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log(response.status);
            if (response.status === 200 && response1.status === 200) {
                alert("Joined Tournament Successfully");
                loadCurrentTournaments();
                loadTournament();
            }
        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert("Error Joining Tournament");
                error('An error occurred while joining the tournament.');
            }  
        } 
    }
    const leaveTournament = async (tournament_id) => {
        try {
            const token = localStorage.getItem('token');
            console.log(tournament_id);
            const response = await axios.put(`http://localhost:8080/auth/tournament/${tournament_id}/participant/delete?user_id=${id}`);
            const response1 = await axios.put(`http://localhost:8080/user/${id}/participating_tournament/remove?tournament_id=${tournament_id}`,{}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.status === 200 && response1.status === 200) {
                alert("Leave Tournament Successfully");
                loadCurrentTournaments();
                loadTournament();
            }
        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert("Error Joining Tournament");
                error('An error occurred while joining the tournament.');
            }  
        } 
    };


    const isTokenExpired = () => {
        const expiryTime = localStorage.getItem('tokenExpiry');
        if (!expiryTime) return true;
        return new Date().getTime() > expiryTime;
    };

    const isUserToken = (token) => {
        try {
            const decodedToken = jwtDecode(token);
            console.log(decodedToken)
            console.log(decodedToken.authorities)
            return decodedToken.authorities === 'ROLE_USER'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            console.log(token +" hello");
            
            if (!token || isTokenExpired()|| !isUserToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const response = await axios.get('http://localhost:8080/auth/tournaments', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            
                //setData(response.data);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    alert('Your session has expired. Please login again.');
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };
        fetchData();
        //loadPastTournaments();
        loadCurrentTournaments();
        loadTournament();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadCurrentTournaments= async()=>{
        const token = localStorage.getItem('token');
        const result = await axios.get(`http://localhost:8080/user/${id}/participating_tournament/current`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const filteredTournament = result.data
                .filter(tournament => tournament.status === 'active');
            setCurrentTournament(filteredTournament);
        
    };

    const loadTournament = async () => {
      const result = await axios.get(`http://localhost:8080/auth/tournaments/${id}`);
      const filteredTournament = result.data.filter(tournament => !tournament.participants.includes(id));
      console.log(result);
      setTournament(filteredTournament);
    };

    // const loadPastTournaments= async()=>{
    //     const result = await axios.get("http://localhost:8080/auth/tournaments");
    //     const filteredPastTournament = result.data
    //             .filter(tournament => tournament.status === 'inactive');
    //         setPastTournament(filteredPastTournament);
    // };
    const handleRowClick = (id) => {
        navigate(`/admin/tournament/${id}`);
    };

    return (
        <div>
            {/* {data ? ( */}
                <div >
                    <div style={{ width:"100vh", display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h1>Tournament</h1>
                    </div>
                    <div style={{}}>
                    <div style={{paddingTop:"5px",width:"95%"}}>
                    <div className="border rounded" style={{margin:"4%", width:"100%", minWidth:"400px", height:"40vh", overflowY:"auto"}}>
                        <h1 style={{margin:"5%", textAlign:"center"}}>Joined Tournaments</h1>
                    <div className="table-responsive" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding:"3%"}}>
    
                        <table className="table table-striped  container-fluid">
                            
                            <thead className="thead-dark">
                                <tr>
                                
                                    <th scope="col">ID</th>
                                    <th scope="col">Tournament Name</th>
                                    <th scope="col">Date</th>
                                    <th scope="col">size</th>
                                </tr>
                            </thead>
                            
                            <tbody>
                                {   currentTournament.map((currentTournament, index) =>
                                
                                    <tr key={currentTournament.id} onClick={() => handleRowClick(currentTournament.id)}>
                                       
                                        <td>{currentTournament.id}</td>
                                        <td>{currentTournament.tournament_name}</td>
                                        <td>{currentTournament.date}</td>
                                        <td>{currentTournament.currentSize}  / {currentTournament.size}</td>
                                        <td>

                                            <button className="btn btn-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={(event) => {leaveTournament(currentTournament.id);
                                            event.stopPropagation();
                                            }}>Leave</button>
                                        </td>
                                    </tr>
                                    
                                   
                                 )}
                            </tbody>
                            
                        </table>
                    </div>
                    </div>
                    </div>
                    
                    <div style={{width:"95%"}}>
                    <div className="border rounded" style={{margin:"4%", width:"100%", minWidth:"400px", height:"80vh", overflowY:"auto"}}>
                        <h1 style={{margin:"5%", textAlign:"center"}}>Available Tournaments</h1>
                    <div className="table-responsive" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding:"3%"}}>
    
                        <table className="table table-striped  container-fluid">
                            
                            <thead className="thead-dark">
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">ID</th>
                                    <th scope="col">Tournament Name</th>
                                    <th scope="col">Date</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">size</th>
                                </tr>
                            </thead>
                            
                            <tbody>
                                {   tournament.map((tournament, index) =>
                                
                                    <tr key={tournament.id} onClick={() => handleRowClick(tournament.id)}>
                                        <th scope="row"> {index + 1}</th>
                                        <td>{tournament.id}</td>
                                        <td>{tournament.tournament_name}</td>
                                        <td>{tournament.date}</td>
                                        <td>{tournament.status}</td>
                                        <td>{tournament.currentSize}  / {tournament.size}</td>
                                        <td>
                                            <button className="btn btn-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} onClick={(event) => {joinTournament(tournament.id); event.stopPropagation();}}>Join</button>
                                            
                                        </td>
                                    </tr>
                                    
                                   
                                 )}
                            </tbody>
                            
                        </table>
                    </div>
                    </div>
                    </div>
                    </div>
                    </div>
                    

                    
                
            {/* ) : (
                <div>Loading...</div>
            )} */}
        </div>
    );
};

export default TournamentUser;