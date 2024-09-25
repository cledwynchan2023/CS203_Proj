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

    const joinTournament = (tournamentId) => {
        try {
            const response = axios.put(`http://localhost:8080/auth/tournament/${tournamentId}/participant/add?user_id=${id}`);
            const response1 = axios.put(`http://localhost:8080/auth/user/${id}/participating_tournament/add?tournament_id=${tournamentId}`);
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
                setData(response.data);
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
        const result = await axios.get(`http://localhost:8080/auth/user/${id}/participating_tournament/current`);
        const filteredTournament = result.data
                .filter(tournament => tournament.status === 'active');
            setCurrentTournament(filteredTournament);
        
    };

    const loadTournament = async () => {
      const result = await axios.get(`http://localhost:8080/auth/tournaments`);
      const filteredTournament = result.data.filter(tournament => !tournament.participants.includes(id));
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
        <div style={{ padding: '20px' }}>
            {data ? (
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h1>Tournament</h1>
                        <div style={{ gap: '5px' }}>
                            <Link className="btn mb-4 btn-outline-primary" style={{ height:'40px',width: '100px',borderRadius: '20px', maxWidth:'150px' }} to="/admin/tournament/create">Create</Link>
                        </div>
                    </div>
                    <div style={{display:"flex"}}>
                    <div className="border rounded" style={{margin:"4%", width:"100%", minWidth:"400px", height:"80vh", overflowY:"auto"}}>
                        <h1 style={{margin:"5%", textAlign:"center"}}>Joined Tournaments</h1>
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
                                {   currentTournament.map((currentTournament, index) =>
                                
                                    <tr key={currentTournament.id} onClick={() => handleRowClick(currentTournament.id)}>
                                        <th scope="row"> {index + 1}</th>
                                        <td>{currentTournament.id}</td>
                                        <td>{currentTournament.tournament_name}</td>
                                        <td>{currentTournament.date}</td>
                                        <td>{currentTournament.status}</td>
                                        <td>{currentTournament.currentSize}  / {currentTournament.size}</td>
                                        <td>
                                            <Link className="btn btn-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} to={`/admin/tournament/edit/${tournament.id}`}onClick={(event) => event.stopPropagation()}>Edit</Link>
                                            <button className="btn btn-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={(event) => {deleteTournament(tournament.id);
                                            event.stopPropagation();
                                            }}>Delete</button>
                                        </td>
                                    </tr>
                                    
                                   
                                 )}
                            </tbody>
                            
                        </table>
                    </div>
                    </div>
                    </div>
                    
                    <div style={{display:"flex"}}>
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
                                            <button className="btn btn-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} onClick={joinTournament(tournament.id)}>Join</button>
                                            <button className="btn btn-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={(event) => {
                                           
                                            }}>View</button>
                                        </td>
                                    </tr>
                                    
                                   
                                 )}
                            </tbody>
                            
                        </table>
                    </div>
                    </div>
                    </div>
                    </div>
                    

                    
                
            ) : (
                <div>Loading...</div>
            )}
        </div>
    );
};

export default TournamentUser;