import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import './style/TournamentPageStyle.css';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp'; 
export default function TournamentLandingPage() {
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

    const handleRowClick = (tournamentId) => {
        navigate(`/admin/${id}/tournament/${tournamentId}`);
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
        loadTournaments();

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
  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
        backgroundSize: 'cover', 
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
		flexWrap: 'wrap',
        marginTop:"80px",
        height:"100%"
    }}> 
    <div className="content container fade-in" style={{height:"auto",paddingTop:"100px", paddingBottom:"50px"}}>
        {/* <section className="hero">
            <div className="hero-body">
                <p className="title">Tournament</p>
            </div>
        </section> */}
        <section className="section is-large" style={{ paddingTop:"30px", backgroundColor:"rgba(0, 0, 0, 0.5)", borderRadius:"35px", height:"auto", overflowX:"scroll"}}>
            <div className="hero-body" style={{marginBottom:"5%"}}>
                <p className="title is-size-2 is-family-sans-serif">Tournament</p>
                <Link className="button is-link is-rounded" to={`/admin/${id}/tournament/create`}>Create Tournament</Link>
            </div>
            <table className="table is-hoverable custom-table" >
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tournament Name</th>
                        <th>Start Date</th>
                        <th>Status</th>
                        <th>Capacity</th>
                    </tr>
                </thead>
                <tbody>
                    {tournament.map((tournament, index) =>
                        <tr key={tournament.id} onClick={() => handleRowClick(tournament.id)}>
                            <td>{tournament.id}</td>
                            <td>{tournament.tournamentName}</td>
                            <td>{tournament.date}</td>
                            <td>{tournament.status}</td>
                            <td>{tournament.currentSize}  / {tournament.size}</td>
                            <button className="button is-text" style={{ height:'40px',width: '80px',borderRadius: '20px', maxWidth:'100px', textAlign: 'center', marginBottom:"25px" }} onClick={(event) => {removePlayer(user.id);
                                            event.stopPropagation();
                            }}>Remove</button>
                        </tr>   
                    )}
                </tbody>
            </table>
        </section>
    </div>
    
    </div>
    <footer className="footer" style={{textAlign:"center"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
    </>
  )
}
