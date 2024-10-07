import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp'; 
import "./style/TournamentPage.css";
export default function TournamentPage() {
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
  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
    }}> 
         <div className="content" style={{width:"100%",height:"100vh", backgroundColor:"rgba(0, 0, 0, 0.5)"}}>
            <section className="hero fade-in" style={{ display:"flex", justifyContent:"start", width:"100%", alignItems:"center"}}>
                <div style={{width:"100%", paddingTop:"50px", paddingLeft:"40px"}}>
                    <p className="title is-family-sans-serif is-2" style={{width:"100%", fontWeight:"bold", fontStyle:"italic", marginBottom:"20px"}}>Browse Tournaments</p>
                    
                </div>
            </section>
            <section className="hero" style={{width:"100%", backgroundColor:"rgba(0, 0, 0, 0.5)", paddingTop:"5%", height:"80%", overflowY:"scroll"}}>
                
                <div style={{width:"100%", paddingLeft:"20px", display:"flex", justifyContent:"space-evenly", flexWrap:"wrap"}}>
                {tournament.map((tournament) => (
                    <a key={tournament.id} href={`/user/${tournament.id}/tournament`} className="card custom-card" style={{ width: "30%", minWidth: "300px", height: "200px" }}>
                    <div className="card-image">
                        {/* <figure className="image is-4by3">
                        <img
                            src={tournament.imageUrl} // Replace with your image URL field
                            alt={tournament.name}
                        />
                        </figure> */}
                    </div>
                    <div className="card-content">
                        <div className="media">
                        <div className="media-content">
                            <p className="title is-4">{tournament.tournamentName}</p>
                        </div>
                        </div>
                        <div className="content">
                        Date: {tournament.date}
                        <br />
                        Players: {tournament.currentSize}/{tournament.size}
                        <br />
                        Status: {tournament.status}
                        </div>
                    </div>
                    </a>
                ))}
                </div>
            
            </section>
            
         </div>
    </div>
    <footer className="footer" style={{textAlign:"center"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
	</footer>
    </>

  )
}
