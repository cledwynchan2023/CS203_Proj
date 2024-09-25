import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import './style/TournamentAdminStyle.css';
import { Link, useParams, useNavigate } from 'react-router-dom';

const TournamentAdmin = () => {
    const navigate = useNavigate();
    const[tournament,setTournament]=useState([]);
    const[pastTournament, setPastTournament]=useState([]);
    const [data, setData] = useState('');
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

    const deleteTournament = async (tournament_id) => {
        try {
            
            
            const response = await axios.delete(`http://localhost:8080/auth/tournament/${tournament_id}`);
            // Refresh the tournament list after deletion
            if (response.status === 200){
                alert("Tournament Deleted Successfully");
                loadTournaments();
                
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
        loadTournaments();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadTournaments= async()=>{
        const result = await axios.get("http://localhost:8080/auth/tournaments");
        console.log(result.data);
        if (!result.data.length == 0){
            console.log("No Active Tournaments");
            const filteredTournament = result.data.filter(tournament => tournament.status === 'active');
            console.log(filteredTournament);
            setTournament(filteredTournament);
        }
        else{
            setTournament([]);
        }
        
    };

    const loadPastTournaments= async()=>{
        const result = await axios.get("http://localhost:8080/auth/tournaments");
  
        const filteredPastTournament = result.data.tournaments
                .filter(tournament => tournament.status === 'inactive');
            setPastTournament(filteredPastTournament);
    };
    const handleRowClick = (tournamentId) => {
        navigate(`/admin/${id}/tournament/${tournamentId}`);
    };

    return (
        <div style={{ padding: '20px' }}>
           
                <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h1>Tournament</h1>
                        <div style={{ gap: '5px' }}>
                            <Link className="btn mb-4 btn-outline-primary" style={{ height:'40px',width: '100px',borderRadius: '20px', maxWidth:'150px' }} to={`/admin/${id}/tournament/create`}>Create</Link>
                        </div>
                    </div>
                    <div style={{display:"flex"}}>
                    <div className="border rounded" style={{margin:"4%", width:"100%", minWidth:"400px", height:"80vh", overflowY:"auto"}}>
                        <h1 style={{margin:"5%", textAlign:"center"}}>Active Tournaments</h1>
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
                                            <Link className="btn btn-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} to={`/admin/${id}/tournament/edit/${tournament.id}`}onClick={(event) => event.stopPropagation()}>Edit</Link>
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
                    </div>
                
           
        </div>
    );
};

export default TournamentAdmin;