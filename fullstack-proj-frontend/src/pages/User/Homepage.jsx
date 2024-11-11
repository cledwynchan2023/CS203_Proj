import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp'; 
import hikaru from '/src/assets/hikaru.webp';
import liang from '/src/assets/liang.png';
import europe from '/src/assets/europe.jpg';
import "./style/Homepage.css";
export default function Homepage() {
    const navigate = useNavigate();
    
    const [data, setData] = useState('');
    const [error, setError] = useState(null);
    const { userId } = useParams();

    const clearTokens = () => {
        localStorage.removeItem('token'); // Remove the main token
        localStorage.removeItem('tokenExpiry'); // Remove the token expiry time
        
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
    const isUserToken = (token) => {
        try {
            const decodedToken = jwtDecode(token);
           
            return decodedToken.authorities === 'ROLE_USER'; // Adjust this based on your token's structure
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
                    // setTimeout(() => {
                        window.location.href = '/';
                    // }, 1000);
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };

        fetchData();
        //loadPastTournaments();
 

    }, []);
    
  return (
    <>
    <div className="background-container" style={{ 
        backgroundImage: `url(${backgroundImage})`, 
        height:"100vh"
    }}> 
         <div className="content" style={{width:"100%",height:"100%", backgroundColor:"rgba(0, 0, 0, 0.7)", paddingTop:"50px", overflowY:"scroll",}}>
            <section className="hero fade-in" style={{display:"flex", justifyContent:"start", width:"100%", alignItems:"center", height:"auto"}}>
                <div style={{width:"70%", textAlign:"center"}}>
                    <p className="title is-family-sans-serif is-2" style={{width:"100%", fontWeight:"bold", fontStyle:"italic"}}>Master Your Moves, Conquer the Board – Join the Ultimate Chess Challenge!</p>
                    <p className="subtitles" style={{width:"100%", fontSize:"20px"}}>
                    Welcome to the premier destination for chess enthusiasts of all skill levels! Whether you’re a grandmaster or a beginner, our platform offers exciting tournaments that challenge your strategic thinking and push your skills to new heights. 
                    Engage with players from around the globe, compete in real-time matches, 
                    and rise through the ranks to claim your place among the champions. With intuitive features, live leaderboards, and exclusive insights from top players, you’ll have everything you need to master 
                    the game and conquer the board. The ultimate chess challenge awaits – are you ready?
                    </p>
                </div>
            </section>
            <section className="hero" style={{width:"100%", paddingTop:"5%", height:"auto"}}>
                <div className="section" style={{width:"100%", display:"flex", justifyContent:"center", flexWrap:"wrap", gap:"20px"}}>
                <a href={`/user/${userId}/tournament`} className="card custom-card" style={{width:"30%", minWidth:"400px", height:"200px"}}>
                   
                    <div class="card-content">
                        <div class="media">
                        <div class="media-content">
                            <p class="title is-4">Find Tournament</p>
                        </div>
                        </div>

                        <div class="content" style={{fontSize:"1.2rem"}}>
                            Click here to browse available tournaments and join the one that suits your skill level and schedule.
                        <br />
                        </div>
                    </div>
                </a>
              
                    <a href={`/user/${userId}/ranking`} className="card custom-card" style={{width:"30%", minWidth:"400px", height:"200px", padding:"25px"}}>
                   
                        <div class="media">
                        <div class="media-content">
                            <p class="title is-4">Leaderboard</p>
                        </div>
                        </div>

                        <div class="content" style={{fontSize:"1.2rem"}}>
                        Click here to view the leaderboard and see how you stack up against other players.
                        <br />
                        </div>
                    
                    </a>
        
                    <a href={`/user/${userId}/profile`} className="card custom-card" style={{width:"30%", minWidth:"400px", height:"200px"}}>
                    <div class="card-image">
                        {/* <figure class="image is-4by3">
                        <img
                            
                            alt="Placeholder image"
                        />
                        </figure> */}
                    </div>
                    <div class="card-content">
                        <div class="media">
                        <div class="media-content">
                            <p class="title is-4">My Stats</p>
                        </div>
                        </div>

                        <div class="content" style={{fontSize:"1.2rem"}}>
                        Click here to view your personal stats and track your progress over time.
                        </div>
                    </div>
                </a>
                </div> 
            </section>
            <div style={{paddingLeft:"5%", height:"50px"}}>
                <p className="title is-1" style={{fontWeight:"bold"}}>Chess Latest News </p>
            </div>
            <section className="hero" style={{width:"100%", height:"auto",  paddingTop:"5%", paddingLeft:"5%", paddingRight:"5%", paddingBottom:"50px", overflow:"scroll"}}>

                <div style={{width:"100%", display:"flex", justifyContent:"left", gap:"5%", height:"70%"}}>
                    <a target="_blank" href="https://edition.cnn.com/2024/10/20/sport/hikaru-nakamura-chess-streaming-revolution-spt-intl" className="card custom-card" style={{ width: "50%", minWidth: "350px", height:"auto", minHeight:"400px" }}>
                    <div className="card-image">
                        <figure className="image is-16by9">
                        <img
                            src={hikaru} // Replace with your image URL field
                           
                        />
                        </figure>
                    </div>
                    <div className="card-content">
                        <div className="media">
                        <div className="media-content noScroll">
                            <p className="title is-4">‘What has happened online actually dwarfs what Magnus has done’: Grandmaster Hikaru Nakamura on chess’ streaming revolution</p>
                        </div>
                        </div>

                        <div className="content" style={{fontWeight:"bold"}}>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                           
                            <p style={{color:"rgb(106, 90, 205)"}}>
                                
                            </p>
                            </div>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            
                            <p style={{}}>
                              
                            </p>
                            </div>
                            <div style={{marginBottom:"20px", display:"flex", alignItems:"center"}}>
                            
                            <p style={{color:"rgb(60, 179, 113)"}}>
                                
                            </p>
                            </div>
                            <div>
                            
                            </div>
                        </div>
                    </div>
                    </a>
                    <a target="_blank" href="https://www.chess.com/news/view/2024-chesscom-seirawan-chess-championship-liang-wins" className="card custom-card" style={{ width: "50%", minWidth: "350px", height:"auto", minHeight:"400px" }}>
                    <div className="card-image">
                        <figure className="image is-16by9">
                        <img
                            src={liang} // Replace with your image URL field
                           
                        />
                        </figure>
                    </div>
                    <div className="card-content">
                        <div className="media">
                        <div className="media-content noScroll">
                            <p className="title is-4">Liang Wins S-Chess Championship, Beats Ex-Bughouse-Partner Xiong In Grand Final</p>
                        </div>
                        </div>

                        <div className="content" style={{fontWeight:"bold"}}>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                           
                            <p style={{color:"rgb(106, 90, 205)"}}>
                                
                            </p>
                            </div>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            
                            <p style={{}}>
                              
                            </p>
                            </div>
                            <div style={{marginBottom:"20px", display:"flex", alignItems:"center"}}>
                            
                            <p style={{color:"rgb(60, 179, 113)"}}>
                                
                            </p>
                            </div>
                            <div>
                            
                            </div>
                        </div>
                    </div>
                    </a>
                    <a target="_blank" href="https://www.europechess.org/10-players-with-maximum-score-at-the-european-individual-chess-championship-2024/hscom-seirawan-chess-championship-liang-wins" className="card custom-card" style={{ width: "50%", minWidth: "350px", height:"auto", minHeight:"400px" }}>
                    <div className="card-image">
                        <figure className="image is-16by9">
                        <img
                            src={europe} // Replace with your image URL field
                           
                        />
                        </figure>
                    </div>
                    <div className="card-content">
                        <div className="media">
                        <div className="media-content noScroll">
                            <p className="title is-4">10 players with maximum score at the European Individual Chess Championship 2024</p>
                        </div>
                        </div>

                        <div className="content" style={{fontWeight:"bold"}}>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                           
                            <p style={{color:"rgb(106, 90, 205)"}}>
                                
                            </p>
                            </div>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            
                            <p style={{}}>
                              
                            </p>
                            </div>
                            <div style={{marginBottom:"20px", display:"flex", alignItems:"center"}}>
                            
                            <p style={{color:"rgb(60, 179, 113)"}}>
                                
                            </p>
                            </div>
                            <div>
                            
                            </div>
                        </div>
                    </div>
                    </a>
                    
                    
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
