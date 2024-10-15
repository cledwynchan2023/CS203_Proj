import React, { useState } from 'react'; 
import axios from 'axios'; 
import { useNavigate } from 'react-router-dom'; 
import { 
	MDBContainer, 
	MDBInput, 
	MDBBtn, 
} from 'mdb-react-ui-kit'; 
import { jwtDecode } from 'jwt-decode';
import '../pages/Login.css';
import background from '/src/assets/background_2.jpg';
import logo from '/src/assets/chesscomp.png';

function Login() { 
    let navigate=useNavigate();
	const [username, setUsername] = useState(''); 
	const [password, setPassword] = useState(''); 
	const [error, setError] = useState(''); 
	
	const history = useNavigate(); 

	const handleLogin = async () => { 
		try { 
			if (!username || !password) { 
				setError('Please enter both username and password.'); 
				return; 
			} 
            
			const response = await axios.post('http://localhost:8080/auth/signin', { username, password }); 
	
			const token = response.data.jwt; 
			localStorage.setItem('token', token);
		
			const expiryTime = new Date().getTime() + 900 * 1000; //expiry time to 15 mins for testing
			localStorage.setItem('tokenExpiry', expiryTime);
			// Decode the token and print the whole payload
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
			
		} catch (error) { 
			console.error('Login failed:', error.response ? error.response.data : error.message); 
			setError('Invalid username or password.'); 
		} 
	}; 

	return ( 
		<>
		<div className="" style={{ 
                backgroundImage: `url(${background})`,
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
				display: 'flex',
				flexWrap: 'wrap',
				height: "100vh",
				justifyContent: 'center',
				alignContent: 'center',
            }}>  
			<div className="content is-family-sans-serif" style={{width:"100%",backgroundColor:"rgba(0,0,0,0.2)", height:"100%", textAlign:"center", display:"flex", justifyContent:"center", flexWrap:"wrap", borderRadius:"30px", width:"100%"}}>
				<div className="content is-medium" style={{ width:"100%", paddingTop:"20px", display:"flex", justifyContent:"center", alignItems:"center"}}>
					<img src={logo} style={{width:"100px", height:"100px", backgroundColor: "transparent", marginRight:"20px"}}></img>
					<p className="text-center " style={{fontSize:"50px", fontWeight:"bold",marginBottom:"0", color: "rgba(0, 0, 0, 0.7)"}}>Chess.io</p>
				</div>
			<div className= "fade-in" style={{ height: '400px', borderRadius:"30px", padding:"50px", width:"50%"}}> 
				<div className="content" style={{width:"100%"}}> 
					<p className="text" style={{fontSize:"20px", fontWeight:"bold"}}>Login</p> 
					<div style={{width:"100%", marginBottom:"20px"}}>
					<input className='input custom-input' style={{backgroundColor:"rgba(0,0,0,0.7)", height:"65px", width:"450px", borderRadius:"30px", marginBottom:"30px", border:"none", paddingLeft: "15px"}} placeholder='Email address' id='email' value={username} type='email' onChange={(e) => setUsername(e.target.value)} /> 
					<input className="input custom-input" style={{backgroundColor:"rgba(0,0,0,0.7)", height:"65px", width:"450px", borderRadius:"30px", marginBottom:"30px", border:"none"}} placeholder='Password' id='password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} /> 
					{error && <p className="text-danger" style={{ fontSize:"1rem"}}>{error}</p>}

					</div>
					
					
					<button className="button is-link" style={{ height:'50px',width: '450px', borderRadius:"30px" }} onClick={handleLogin}>Sign in</button> 
					<div className="text-center" style={{marginTop:"20px", fontSize:"17px"}}> 
						<p>Not a member? <a href="/register" style={{textDecoration: "underline"}}>Register</a></p> 
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
