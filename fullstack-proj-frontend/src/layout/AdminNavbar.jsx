import {React,useState} from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import './navbar.css';

const AdminNavbar = () => {
    const location = useLocation();
    const { id } = useParams();
    const [isActive, setIsActive] = useState(false);
    const toggleBurgerMenu = () => {
        setIsActive(!isActive);
    };
    return (
        <nav className="navbar is-fixed-top" role="navigation" aria-label="main navigation" style={{paddingLeft:"20px", paddingRight:"20px"}}>
            <div className="navbar-brand">
                <a className="navbar-item" style={{fontSize:"1.5rem"}} href={`/admin/${id}/tournament`}>Chess.io</a>

                <a role="button" className="navbar-burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample" onClick={toggleBurgerMenu}>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                </a>
            </div>

            <div id="navbarExampleTransparentExample" className={`navbar-menu ${isActive ? "is-active" : ""}`}>
                <div class="navbar-end">
                <a class="navbar-item" href={`/admin/${id}/tournament`}> Tournament </a>
                <a class="navbar-item" href={`/admin/${id}/playerlist`}> PlayerList </a>
                <a class="navbar-item" href={`/admin/${id}/ranking`}> Ranking </a>
                <a class="navbar-item" href={`/user/${id}/home`}> UserPage </a>
               
            </div>
            

  </div>
</nav>
    );
};

export default AdminNavbar;