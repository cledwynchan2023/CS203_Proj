import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import backgroundImage from '/src/assets/image1.webp'; 
import { Link, useParams } from 'react-router-dom';

const RankingAdmin = () => {
    const[user,setUser]=useState([]);
    const [data, setData] = useState(null);
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

    const deleteUser = async (id) => {
        try {
            
            await axios.delete(`http://localhost:8080/auth/user/${id}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            // Refresh the tournament list after deletion
            loadUsers();
        } catch (error) {
            setError('An error occurred while deleting the user.');
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
            return decodedToken.authorities === 'ROLE_ADMIN'; // Adjust this based on your token's structure
        } catch (error) {
            return false;
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');

            console.log(localStorage.getItem('token') +" hello");
            console.log(!token);
            console.log(isAdminToken(token));
            console.log(isTokenExpired());
            if (!token || isTokenExpired() || !isAdminToken(token)) {
                clearTokens();
                window.location.href = '/'; // Redirect to login if token is missing or expired
                return;
            }

            try {
                const result = await axios.get("http://localhost:8080/u/users", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                const filteredUsers = result.data
                    .filter(user => user.role === 'ROLE_USER')
                    .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
               
                setData(filteredUsers);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    clearTokens();
                    localStorage.removeItem('token'); // Remove token from localStorage
                    window.location.href = '/'; // Redirect to login if token is invalid
                } else {
                    setError('An error occurred while fetching data.');
                }
            }
        };
        loadUsers()
        fetchData();
        

    }, []);

    if (error) {
        return <div>{error}</div>;
    }

    const loadUsers= async()=>{
        const token = localStorage.getItem('token');
        try {
            const result = await axios.get("http://localhost:8080/u/users", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            const filteredUsers = result.data
                .filter(user => user.role === 'ROLE_USER')
                .sort((a, b) => b.elo - a.elo); // Sort by highest Elo first
            setUser(filteredUsers);
        } catch (error) {
            setError("Error loading users");
            console.error("Error loading users:", error);
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
        <div className="content" style={{width:"100%"}}>
        <div style={{ padding: '20px', width:"100%" }}>
            {data ? (
                <div className="">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h1>Ranking</h1>
                        
                    </div>
                    <div className="">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">ID</th>
                                    <th scope="col">Username</th>
                                    <th scope="col">Elo</th>
                                    <th scope="col">Role</th>
                                    {/* <th scope="col">Status</th>
                                    <th scope="col">size</th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {   user.map((user, index) =>
                                    <tr>
                                        <th scope="row" key={index} > {index + 1}</th>
                                        <td>{user.id}</td>
                                        <td>{user.username}</td>
                                        <td>{user.elo}</td>
                                        <td>{user.role}</td>
                                        {/* <td>{tournament.status}</td>
                                        <td>{tournament.size}</td> */}
                                        
                                    </tr>
                                 )}
                            </tbody>
                        </table>
                    </div>
                </div>
            ) : (
                <div>Loading...</div>
            )}
        </div>
        </div>
        
        </div>
        </>
        
    );
};

export default RankingAdmin;