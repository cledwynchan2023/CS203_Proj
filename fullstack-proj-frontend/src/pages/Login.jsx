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
                backgroundImage: 'url(src/assets/image.webp)', 
                backgroundSize: 'cover', 
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
				display: 'flex',
				flexWrap: 'wrap',
				height: "100vh",
				justifyContent: 'center',
				alignContent: 'center',
            }}>  
			<div style={{width:"70%",backgroundColor:"rgba(0,0,0,0.5)", height:"70%", minHeight:"450px", textAlign:"center", display:"flex", justifyContent:"center", flexWrap:"wrap", borderRadius:"30px"}}>
			<div className="content is-medium" style={{ width:"100%", paddingTop:"20px"}}>
				<h1 className="text-center " style={{ marginBottom:"0", color: "rgba(255, 255, 255, 0.8)", fontWeight:"bold"}}>Welcome to Chess.io</h1>
			</div>
			<div className= "box has-background-light fade-in" style={{ width: '500px', height: '400px', borderRadius:"30px", padding:"50px"}}> 
				<MDBContainer className="p-3"> 
					<h2 className="mb-4 text-center">Login to Chess.io</h2> 
					<MDBInput wrapperClass='mb-4' placeholder='Email address' id='email' value={username} type='email' onChange={(e) => setUsername(e.target.value)} /> 
					
					<MDBInput wrapperClass='mb-4' placeholder='Password' id='password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} /> 
					{error && <p className="text-danger">{error}</p>} {/* Render error message if exists */} 
					<button className="btn mb-4 btn-outline-primary" style={{ height:'50px',width: '100%', borderRadius:"30px" }} onClick={handleLogin}>Sign in</button> 
					<div className="text-center"> 
						<p>Not a member? <a href="/register" >Register</a></p> 
					</div> 
				</MDBContainer> 
			</div> 
			</div>
			
		</div> 
		
		<footer className="footer">
		<p>&copy; 2024 CS203. All rights reserved.</p>
		</footer>
		</>
	); 
} 

export default Login; 
