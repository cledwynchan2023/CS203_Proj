import {React,useEffect,useState} from 'react';
import {useLocation, useParams } from 'react-router-dom';
import './navbar.css';
import { jwtDecode } from 'jwt-decode';
const UserNavbar = () => {
    const location = useLocation();
    const { userId } = useParams();
    const [isActive, setIsActive] = useState(false);
    const token = localStorage.getItem('token');
    const toggleBurgerMenu = () => {
        setIsActive(!isActive);
    };
    const isAdminToken = (token) => {
        try {
            const decodedToken = jwtDecode(token);
           
            if ((decodedToken.authorities === 'ROLE_ADMIN')){

                return true;
            } else {
                
                return false;
            }
          
        } catch (error) {
            
            return false;
        }
    };
   
    const href = isAdminToken(token) ? `/admin/${userId}/tournament` : `/user/${userId}/home`;
   


    return (
        <nav className="navbar is-fixed-top" role="navigation" aria-label="main navigation" style={{paddingLeft:"20px", paddingRight:"20px"}}>
            <div className="navbar-brand">
                <a className="navbar-item" style={{fontSize:"1.5rem"}} href={href}>ChessComp</a>

                <a role="button" className="navbar-burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample" onClick={toggleBurgerMenu}>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                </a>
            </div>

            <div id="navbarExampleTransparentExample" className={`navbar-menu ${isActive ? "is-active" : ""}`}>
                <div class="navbar-end">
            
                <a class="navbar-item" href={href}> Home </a>
                <a class="navbar-item" href={`/user/${userId}/tournament`}> Tournament </a>
                <a class="navbar-item" href={`/user/${userId}/ranking`}> Ranking </a>
                <a class="navbar-item" href={`/user/${userId}/profile`}> Profile </a>
               
            </div>
            

  </div>
</nav>
    );
};

export default UserNavbar;