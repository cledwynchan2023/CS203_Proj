import React, { useState } from 'react'; 
import axios from 'axios'; 
import { useNavigate } from 'react-router-dom'; 
import { jwtDecode } from 'jwt-decode';
import '../pages/Login.css';
import background from '/src/assets/background_2.jpg';
import logo from '/src/assets/chesscomp.png';
import {Atom} from "react-loading-indicators"

function Login() { 
    let navigate=useNavigate();
	const [username, setUsername] = useState(''); 
	const [password, setPassword] = useState(''); 
	const [error, setError] = useState(''); 
	const [isLoading, setIsLoading] = useState(false);
	const [isExiting, setIsExiting] = useState(false);

	const handleLogin = async () => { 
		try { 
			if (!username || !password) { 
				setError('Please enter both username and password.'); 
				return; 
			} 
            setIsLoading(true);
			const response = await axios.post('http://localhost:8080/auth/signin', { username, password }); 
	
			const token = response.data.jwt; 
			localStorage.setItem('token', token);
			setIsLoading(false)
			setIsExiting(true);
			setTimeout(() => {
			const expiryTime = new Date().getTime() + 900 * 1000; //expiry time to 15 mins for testing
			localStorage.setItem('tokenExpiry', expiryTime);
			const decodedToken = jwtDecode(token); 
			console.log('Decoded Token:', decodedToken); 
			const userId = decodedToken.userId;
			localStorage.setItem('userId', userId); // Store userId in localStorage
			
			const userRole = decodedToken.authorities;
			console.log(userRole + " HELLO");
			
			if (userRole == "ROLE_ADMIN"){
				
					navigate(`/admin/${userId}/tournament`);
				
				
			} else if (userRole=="ROLE_USER"){
				
					navigate(`/user/${userId}/tournament`);
			}
			}, 1000);
			
		} catch (error) { 
			setIsLoading(false)
			console.error('Login failed:', error.response ? error.response.data : error.message); 
			setError('Invalid username or password.'); 
		} 
	}; 

	return ( 
		<>
		<div className="login-container" style={{ 
                backgroundImage: `url(${background})`,
                marginTop:"0",
				
            }}>  
			<div className="content is-family-sans-serif fade-in" style={{width:"100%", height:"100%", textAlign:"center", display:"flex", justifyContent:"center", flexWrap:"wrap", width:"100%", backgroundColor:"rgba(0,0,0,0.3)"}}>
				<div className="is-medium animate__animated animate__fadeInDown" style={{ width:"100%", paddingTop:"20px", display:"flex", justifyContent:"center", alignItems:"center"}}>
					<img src={logo} style={{width:"100px", height:"100px", backgroundColor: "transparent", marginRight:"20px"}}></img>
					<p className="text-center " style={{fontSize:"50px", fontWeight:"bold", color: "rgb(0,0,0,0.9)"}}>Chess.io</p>
				</div>
			<div className= {`content animate__animated animate__backInUp  ${isExiting ? 'animate__animated animate__zoomOut' : ''}`} style={{ height: '40%', borderRadius:"30px", padding:"10px", width:"50%", minWidth:"500px", display:"flex", justifyContent:"center"}}> 
				<div className="content" style={{width:"100%", paddingLeft:"40px", paddingRight:"40px", height:"100%", minWidth:"500px"}}> 
					<p className="text" style={{fontSize:"20px", fontWeight:"bold", marginBottom:"0"}}>Login</p> 
					<div style={{width:"100%",  height:"35%",minHeight:"150px", marginBottom:"50px", marginTop:"10px"}}>
						<input className='input custom-input' style={{backgroundColor:"rgba(0,0,0,0.7)", height:"40%", width:"55%",minWidth:"400px", borderRadius:"40px", marginBottom:"30px", border:"none", paddingLeft: "15px"}} placeholder='Email address' id='email' value={username} type='email' onChange={(e) => setUsername(e.target.value)} /> 
						<input className="input custom-input" style={{backgroundColor:"rgba(0,0,0,0.7)", height:"40%", width:"55%",minWidth:"400px", borderRadius:"40px", marginBottom:"30px", border:"none"}} placeholder='Password' id='password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} />

					</div>
					
					
					{isLoading ? (
                        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '40%' }}>
							<div>
								<Atom size={24} color="white" />
								<p style={{width:"100%"}}>Login in....</p>
							</div>

                        </div>
                    ) : (
                        <button className="button is-link" style={{ height:'15%',width: '50%',minWidth:"400px", borderRadius:"30px" }} onClick={handleLogin}>Sign in</button>
                    )}
					<div className="text-center" style={{ marginTop: "20px", fontSize: "17px" }}>
					{error && <p className="text-danger" style={{ fontSize:"1rem"}}>{error}</p>}
                            <p style={{color:"white"}}>Not a member? <a href="/register" style={{ textDecoration: "underline" }}>Register</a></p>
                        </div>
				</div> 
			</div> 
			</div>
			
		</div> 
		
		<footer className="footer" style={{textAlign:"center"}}>
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
		</>
	); 
} 

export default Login; 
