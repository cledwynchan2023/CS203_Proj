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
    const initSSE = () => {
        const eventSource = new EventSource('http://localhost:8080/update/sse/tournament');

        eventSource.onmessage = (event) => {
            const tournament = JSON.parse(event.data);
            console.log(users);
            
            setUser(tournament);
            setData(filteredUsers);
        };

        eventSource.onerror = (error) => {
            console.error("SSE failure:", error);
            setError("Loading...");
            eventSource.close();
        };

        return () => {
            eventSource.close();
        };
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
                    alert('Your session has expired. Please login again.');
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 1000);
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };

        fetchData();
        //loadPastTournaments();
        //loadTournaments();
        initSSE();

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadTournaments= async()=>{
        const result = await axios.get("http://localhost:8080/t/tournaments");
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

    const handleRowClick = (tournamentId) => {
        navigate(`/admin/${id}/tournament/${tournamentId}`);
    };

    return (
        <div style={{ paddingTop: '50px' }}>
           
        <div>
            <h1 className="text-center" style={{ marginBottom:"0", color: "rgba(0, 0, 0, 0.5)", }}>Active Tournaments</h1>
        </div>
                
        <section className="section">
        <div className="container">

        {/* Table */}
        <table className="table is-striped is-fullwidth ">
          <thead>
            <tr>
              <th>
                <input type="checkbox" />
              </th>
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
                                        <td>{tournament.tournamentName}</td>
                                        <td>{tournament.date}</td>
                                        <td>{tournament.status}</td>
                                        <td>{tournament.currentSize}  / {tournament.size}</td>
                                        <td>
                                            <Link className="btn btn-primary" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginRight:"20px" }} to={`/admin/${id}/tournament/edit/${tournament.id}`}onClick={(event) => event.stopPropagation()}>Edit</Link>
                                            <button className="button btn-danger" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center' }} onClick={(event) => {deleteTournament(tournament.id);
                                            event.stopPropagation();
                                            }}>Delete</button>
                                        </td>
                                    </tr>
                                    
                                   
                                 )}
                            </tbody>
        </table>
      </div>
    </section>
        </div>
    );
};

export default TournamentAdmin;