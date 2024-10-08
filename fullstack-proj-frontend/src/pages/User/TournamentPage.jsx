import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { Link, useParams, useNavigate } from 'react-router-dom';
import backgroundImage from '/src/assets/image1.webp'; 
import "./style/TournamentPage.css";
import { IoCalendarNumberOutline } from "react-icons/io5";
import { BiGroup } from "react-icons/bi";
import { TiTick } from "react-icons/ti";
import compPic from "/src/assets/comp.webp";
import compPic2 from "/src/assets/comp2.webp";
import compPic3 from "/src/assets/comp3.webp";

export default function TournamentPage() {
    const navigate = useNavigate();
    const[tournament,setTournament]=useState([]);
    const[pastTournament, setPastTournament]=useState([]);
    const [data, setData] = useState('');
    const [error, setError] = useState(null);
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Overview');
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
    const images = [compPic, compPic2, compPic3];


  const getRandomImage = () => {
    const randomIndex = Math.floor(Math.random() * images.length);
    return images[randomIndex];
  };

    const loadTournaments= async()=>{
        const result = await axios.get("http://localhost:8080/t/tournaments/active");
        const result1 = await axios.get("http://localhost:8080/t/tournaments/inactive");
    
        if (!result.data.length == 0){
            setTournament(result.data);
        }
        else{
            setTournament([]);
        }
        if (!result1.data.length == 0){
            setPastTournament(result1.data);
        } else {
            setPastTournament([]);
        }
        
    };

    const renderTabContent = () => {
        switch (activeTab) {
            case 'Overview':
                return <>
                <section className="hero" style={{width:"100%",  paddingTop:"5%", height:"80%", overflowY:"scroll", paddingLeft:"5%", paddingRight:"5%"}}>
                
                <div style={{width:"100%", paddingLeft:"20px", display:"flex", flexWrap:"wrap", justifyContent:"space-between", gap:"20px"}}>
                {tournament.map((tournament) => (
                    <a key={tournament.id} href={`/user/${tournament.id}/tournament`} className="card custom-card" style={{ width: "30%", minWidth: "300px" }}>
                    <div className="card-image">
                        <figure className="image is-16by9">
                        <img
                            src={getRandomImage()} // Replace with your image URL field
                            alt={tournament.name}
                        />
                        </figure>
                    </div>
                    <div className="card-content">
                        <div className="media">
                        <div className="media-content noScroll">
                            <p className="title is-4">{tournament.tournamentName}</p>
                        </div>
                        </div>

                        <div className="content" style={{fontWeight:"bold"}}>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            <IoCalendarNumberOutline size={25} style={{marginRight:"10px"}}></IoCalendarNumberOutline>
                            <p style={{color:"rgb(106, 90, 205)"}}>
                                {tournament.date}
                            </p>
                            </div>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            <BiGroup size={25} style={{marginRight:"10px"}}></BiGroup>
                            <p style={{}}>
                                {tournament.currentSize}/{tournament.size}
                            </p>
                            </div>
                            <div style={{marginBottom:"20px", display:"flex", alignItems:"center"}}>
                            <TiTick size={25} style={{marginRight:"10px"}}></TiTick>
                            <p style={{color:"rgb(60, 179, 113)"}}>
                                {tournament.status}
                            </p>
                            </div>
                            <div>
                            {/* <button className="button is-link is-rounded" onClick={(event) => {
                                join(tournament.id);
                                event.stopPropagation(); // Prevent the click event from bubbling up to the card
                            }}>Join Tournament</button> */}
                            </div>
                        </div>
                    </div>
                    </a>
                ))}
                </div>
                </section>
                </>
            case 'Inactive':
                return <>
                <section className="hero" style={{width:"100%",  paddingTop:"5%", height:"80%", overflowY:"scroll", paddingLeft:"5%", paddingRight:"5%"}}>
                
                <div style={{width:"100%", paddingLeft:"20px", display:"flex", flexWrap:"wrap", justifyContent:"space-between", gap:"20px"}}>
                {pastTournament.map((tournament) => (
                    <a key={tournament.id} href={`/user/${tournament.id}/tournament`} className="card custom-card" style={{ width: "30%", minWidth: "300px" }}>
                    <div className="card-image">
                        <figure className="image is-16by9">
                        <img
                            src={getRandomImage()} // Replace with your image URL field
                            alt={tournament.name}
                        />
                        </figure>
                    </div>
                    <div className="card-content">
                        <div className="media">
                        <div className="media-content noScroll">
                            <p className="title is-4">{tournament.tournamentName}</p>
                        </div>
                        </div>

                        <div className="content" style={{fontWeight:"bold"}}>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            <IoCalendarNumberOutline size={25} style={{marginRight:"10px"}}></IoCalendarNumberOutline>
                            <p style={{color:"rgb(106, 90, 205)"}}>
                                {tournament.date}
                            </p>
                            </div>
                            <div style={{marginBottom:"5px", display:"flex", alignItems:"center"}}>
                            <BiGroup size={25} style={{marginRight:"10px"}}></BiGroup>
                            <p style={{}}>
                                {tournament.currentSize}/{tournament.size}
                            </p>
                            </div>
                            <div style={{marginBottom:"20px", display:"flex", alignItems:"center"}}>
                            <TiTick size={25} style={{marginRight:"10px"}}></TiTick>
                            <p style={{color:"rgb(60, 179, 113)"}}>
                                {tournament.status}
                            </p>
                            </div>
                            <div>
                            {/* <button className="button is-link is-rounded" onClick={(event) => {
                                join(tournament.id);
                                event.stopPropagation(); // Prevent the click event from bubbling up to the card
                            }}>Join Tournament</button> */}
                            </div>
                        </div>
                    </div>
                    </a>
                ))}
                </div>
                </section>
                </>
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
    const join = async (tournament_id) => {
        try {
            console.log(user_id);
            const token = localStorage.getItem('token');
            if (token.decodedToken.authorities !== 'ROLE_USER'){
                const response1= await axios.put(`http://localhost:8080/t/${tournament_id}/participant/add?user_id=${id}`,
                    {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (response1.status === 200){
                    alert("Player added Successfully");
                    loadTournaments();
                }
            } else {
                alert("You cannot join a tournament as an admin!");
            }
               
        } catch (error) {

            setError('An error occurred while deleting the tournament.');
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
        height:"100%"
    }}> 
         <div className="content media-content" style={{width:"100%",height:"100%"}}>
            <section className="hero fade-in" style={{ display:"flex", justifyContent:"start", width:"100%", alignItems:"center", marginBottom:"20px"}}>
                <div style={{width:"100%", paddingTop:"50px", paddingLeft:"40px"}}>
                    <p className="title is-family-sans-serif is-2" style={{width:"100%", fontWeight:"bold", fontStyle:"italic"}}>Browse Tournaments</p>
                </div>
            </section>
            <section className="hero" style={{display:"flex",justifyContent:"start",paddingLeft:"2%", paddingRight:"2%", width:"100%",height:"100%", backgroundColor:"rgba(0, 0, 0, 0.6)", paddingBottom:"50px", overflowY:"scroll"}}>
            <div style={{width:"100%", height:"20px"}}></div>
            <div className="tabs is-left" style={{ height:"70px"}}>
              <ul>
                <li className={activeTab === 'Overview' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Overview')}>Available Tournaments</a>
                </li>
                <li className={activeTab === 'Inactive' ? 'is-active' : ''}>
                  <a onClick={() => setActiveTab('Inactive')}>Finished Tournaments</a>
                </li>
              </ul>
            </div>
            <div style={{backgroundColor: "rgba(0, 0, 0, 0.3)"}}>
              {renderTabContent()}
            </div>
          </section>
            
            
         </div>
    </div>
    <footer className="footer" style={{textAlign:"center", height:"100px",width:"100%"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
	</footer>
    </>

  )
}
