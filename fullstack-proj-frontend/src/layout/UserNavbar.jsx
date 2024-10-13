import {React,useState} from 'react';
import {useLocation, useParams } from 'react-router-dom';
import './navbar.css';

const UserNavbar = () => {
    const location = useLocation();
    const { userId } = useParams();
    const [isActive, setIsActive] = useState(false);
    const toggleBurgerMenu = () => {
        setIsActive(!isActive);
    };
    return (
        <nav className="navbar is-fixed-top" role="navigation" aria-label="main navigation" style={{paddingLeft:"20px", paddingRight:"20px"}}>
            <div className="navbar-brand">
                <a className="navbar-item" style={{fontSize:"1.5rem"}} href={`/admin/${userId}/tournament`}>Chess.io</a>

                <a role="button" className="navbar-burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample" onClick={toggleBurgerMenu}>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                </a>
            </div>

            <div id="navbarExampleTransparentExample" className={`navbar-menu ${isActive ? "is-active" : ""}`}>
                <div class="navbar-end">
            
                <a class="navbar-item" href={`/user/${userId}/home`}> Home </a>
                <a class="navbar-item" href={`/user/${userId}/tournament`}> Tournament </a>
                <a class="navbar-item" href={`/user/${userId}/ranking`}> Ranking </a>
               
            </div>
            

  </div>
</nav>
    );
};

export default UserNavbar;