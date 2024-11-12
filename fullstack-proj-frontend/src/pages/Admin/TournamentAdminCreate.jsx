import React, {useState, useEffect} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import { Link, useNavigate, useParams } from "react-router-dom";
import axios from "axios"
import { jwtDecode } from 'jwt-decode';
import backgroundImage from '/src/assets/image1.webp'; 
import './style/TournamentPageStyle.css';
export default function TournamentAdminCreate() {

    let navigate=useNavigate();
    const { userId } = useParams();
    const [tournament,setTournament] = useState({tournament_name:"", date:"", status:"active", size:"", noOfRounds:0});
    const{tournament_name, date, status, size, noOfRounds} = tournament;
    


    const onInputChange=(e)=>{
        setTournament({...tournament, [e.target.name]:e.target.value});
        

        // if (e.target.name === "email") {
        //     checkEmailAvailability(e.target.value);
        // }
    }

    const isValidDateFormat = (input) => {
        const regex = /^\d{2}\/\d{2}\/\d{4}$/;
        
        return regex.test(input);
    };
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
        
            return decodedToken.authorities === 'ROLE_ADMIN'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };


    const onSubmit= async (e)=>{
        e.preventDefault();
        
        if (!isValidDateFormat(date)) {
            alert("Invalid date! Please enter in the format MM/DD/YYYY");
            return;
        }
      
        const tournamentData = {
            tournament_name,
            date,
            status,
            size,
            noOfRounds
        };

        try {
          const token = localStorage.getItem('token');
          const response = await axios.post("http://ec2-18-143-64-214.ap-southeast-1.compute.amazonaws.com/admin/tournament", tournamentData, {
              headers: {
                  'Authorization': `Bearer ${token}`
              }
          });
            if (response.status === 201){
                alert("Tournament Created Successfully");
                navigate(`/admin/${userId}/tournament`);
            }
        } catch (error) {
            console.error("There was an error registering the tournament!", error);
        }
        
    }
    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
           
            
            if (!token || isTokenExpired()|| !isAdminToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }
            
        };

        fetchData();

    }, []);

    return (
        <div className="background-container" style={{ 
          backgroundImage: `url(${backgroundImage})`, 
          backgroundSize: 'cover', 
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          display: 'flex',
          flexWrap: 'wrap',
          height: "100vh",
          justifyContent: 'center',
          alignContent: 'center',
          
      }}>
        <div className="content fade-in" style={{width:"100%", height:"100%", backgroundColor:"rgba(0,0,0,0.8)",}}>
        <div className="container animate__animated animate__fadeInUpBig" style={{ width:"100%", height:"60%", paddingLeft:"15%", paddingRight:"15%", paddingTop:"5%",  justifyContent:"center"}}>
          <p style={{fontSize:"20px"}}>Create Tournament</p>
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
                    //value={role}
                    onChange={(e) => onInputChange(e)}
                    name="status"
                >
                    <option value="active">Active</option>
                    <option value="ongoing">Ongoing</option>
                    <option value="completed">Completed</option>
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
            <button type="submit" className='button is-link is-fullwidth'>Create Tournament</button>
            </div>

            </form>
            <Link className='button is-text is-fullwidth' to={`/admin/${userId}/tournament`} id="returnrBtn">Cancel</Link>
          </div>
        </div>
          
        </div>
      );
}
