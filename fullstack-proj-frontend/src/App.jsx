import { useState } from 'react'
import Navbar from './layout/Navbar.jsx';
//import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import 'bulma/css/bulma.min.css';
import 'animate.css';
import Login from './pages/Login.jsx';
import {BrowserRouter as Router, Route, Routes, useLocation} from "react-router-dom";
import NavbarLogin from './layout/NavbarLogin.jsx';
import Register from './pages/Register.jsx';
import TournamentAdminCreate from './pages/Admin/TournamentAdminCreate.jsx';
import AdminNavbar from './layout/AdminNavbar.jsx';
import PlayerListAdmin from './pages/Admin/Playerlist.jsx';
import TournamentAdminEdit from './pages/Admin/TournamentAdminEdit.jsx';
import RankingAdmin from './pages/Admin/RankingAdmin.jsx';
import TournamentStartAdmin from './pages/Admin/TournamentStartAdmin.jsx';
import NotFound from './pages/NotFound.jsx';
import TournamentLandingPageAdmin from './pages/Admin/TournamentLandingPage.jsx';
import TournamentDetail from './pages/Admin/TournamentDetail.jsx';
import Homepage from './pages/User/Homepage.jsx';
import UserNavbar from './layout/UserNavbar.jsx';
import TournamentPage from './pages/User/TournamentPage.jsx';
import TournamentDetailUser from './pages/User/TournamentDetail.jsx';
import Ranking from './pages/User/Ranking.jsx';
import Profile from './pages/User/Profile.jsx';
import TournamentStart from './pages/Admin/TournamentStart.jsx';
import TournamentCompleted from './pages/Admin/TournamentCompleted.jsx';
import TournamentStartUser from './pages/User/TournamentStart.jsx';
import TournamentEnded from './pages/User/TournamentEnded.jsx';
import ViewProfile from './pages/User/ViewProfile.jsx';
function App() {

  return (
    <>
      <Router>
      
        <Routes>
        <Route exact path="/" element={
          <>
          <NavbarLogin></NavbarLogin>
          <Login/>
          </>
          
        }></Route>

        <Route exact path="/register" element={
          <>
          <NavbarLogin></NavbarLogin>
          <Register/>
          </>
          
        }></Route>
      
        <Route exact path ="/admin/:userId/tournament" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentLandingPageAdmin/>
          </>
        }></Route>
        <Route exact path ="/admin/:userId/tournament/create" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentAdminCreate/>
          </>
        }></Route>
         <Route exact path ="/admin/:userId/tournament/edit/:id" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentAdminEdit/>
          </>
        }></Route>
        {/* <Route exact path ="/admin/:userId/tournament/:id" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentDetailAdmin/>
          </>
        }></Route> */}
        <Route exact path ="/admin/:userId/tournament/:id" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentDetail/>
          </>
        }></Route>
        <Route exact path ="/admin/:userId/playerlist" element={
          <>
          <AdminNavbar></AdminNavbar>
          <PlayerListAdmin/>
          </>
        }></Route>
       
        
        <Route exact path ="/admin/tournament/:id/start_page" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentStartAdmin/>
          </>
        }></Route>
        <Route exact path ="/admin/:userId/ranking" element={
          <>
          <AdminNavbar></AdminNavbar>
          <RankingAdmin/>
          </>
        }></Route>
        <Route exact path ="/admin/:userId/tournament/:id/start" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentStart/>
          </>
        }></Route>

        <Route exact path ="/admin/:userId/tournament/:id/completed" element={
          <>
          <AdminNavbar></AdminNavbar>
          <TournamentCompleted/>
          </>
        }></Route>
        
        {/* <Route exact path ="/user/:id/tournament" element={
          <>
          <Navbar></Navbar>
          <TournamentUser/>
          </>
        }></Route> */}

        <Route exact path ="/user/:userId/home" element={
          <>
          <UserNavbar></UserNavbar>
          <Homepage/>
          </>
        }></Route>
        <Route exact path ="/user/:userId/tournament/:id/start" element={
          <>
          <UserNavbar></UserNavbar>
          <TournamentStartUser/>
          </>
        }></Route>
        <Route exact path ="/user/:userId/tournament/:id/ended" element={
          <>
          <UserNavbar></UserNavbar>
          <TournamentEnded/>
          </>
        }></Route>

      <Route exact path ="/user/:userId/tournament" element={
          <>
          <UserNavbar></UserNavbar>
          <TournamentPage/>
          </>
        }></Route>
        <Route exact path ="/user/:userId/tournament/:id" element={
          <>
          <UserNavbar></UserNavbar>
          <TournamentDetailUser/>
          </>
        }></Route>
         <Route exact path ="/user/:userId/ranking" element={
          <>
          <UserNavbar></UserNavbar>
          <Ranking/>
          </>
        }></Route>
         <Route exact path ="/user/:userId/profile" element={
          <>
          <UserNavbar></UserNavbar>
          <Profile/>
          </>
        }></Route>
        <Route exact path ="/user/:userId/profile/:playerId" element={
          <>
          <UserNavbar></UserNavbar>
          <ViewProfile/>
          </>
        }></Route>

        <Route path="*" element={<NotFound />} />
        </Routes>
       
      </Router>
    </> 
    
  )
}

export default App
