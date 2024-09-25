import React from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';

const AdminNavbar = () => {
    const location = useLocation();
    const { id } = useParams();
    console.log(id + " THE ID");
    // useEffect(() => {
    //     const fetchData = async () => {
    //         const token = localStorage.getItem('token');
    //         console.log(token +" hello");
    //     }
    // }, []);
    return (
        <nav className="navbar navbar-expand-lg navbar-info bg-light" >
            <div className="container-fluid" >
                <a className="navbar-brand" href={`/admin/${id}/tournament`} style={{fontSize:"1.5rem"}}>ChessComp.io</a>
                <button className="navbar-toggler ms-auto" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to={`/admin/${id}/tournament`}
                                style={{ color: location.pathname === `/admin/${id}/tournament` ? 'black' : 'gray' }}
                            >
                                Tournaments
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to={`/admin/${id}/ranking`} 
                                style={{ color: location.pathname === `/admin/${id}/ranking` ? 'black' : 'grey' }}
                            >
                                Ranking
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link 
                                className="nav-link" 
                                to={`/admin/${id}/playerlist`}
                                style={{ color: location.pathname === `/admin/${id}/playerlist` ? 'black' : 'grey' }}
                            >
                                Player List
                            </Link>
                        </li>
                    
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default AdminNavbar;