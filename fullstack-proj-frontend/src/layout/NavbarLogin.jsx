import {React,useState} from 'react';
import {useLocation, useParams } from 'react-router-dom';
import './navbar.css';

const NavbarLogin = () => {
    const location = useLocation();
    const { userId } = useParams();
    const [isActive, setIsActive] = useState(false);
    const toggleBurgerMenu = () => {
        setIsActive(!isActive);
    };
    return (
        <nav className="navbar is-fixed-top" role="navigation" aria-label="main navigation" style={{paddingLeft:"20px", paddingRight:"20px", backgroundColor:"rgba(0,0,0,0.2)"}}>
            <div className="navbar-brand">
                <a className="navbar-item" style={{fontSize:"1.5rem"}} href={`/admin/${userId}/tournament`}>ChessComp</a>

                
            </div>

            <div id="navbarExampleTransparentExample" className={`navbar-menu ${isActive ? "is-active" : ""}`}>
                <div class="navbar-end">
            
      
               
            </div>
            

  </div>
</nav>
    );
};

export default NavbarLogin;