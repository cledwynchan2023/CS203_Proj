import { Link, useParams, useNavigate } from "react-router-dom";
import background from "/src/assets/background_2.jpg";
import "../pages/Login.css";
import { LifeLine } from "react-loading-indicators";
export default function NotFound() {
  return (
    <div
      className="login-container"
      style={{
        backgroundImage: `url(${background})`,
        marginTop: "0",
      }}
    >
      <div
        className="content"
        style={{
          width: "100%",
          height: "100%",
          textAlign: "center",
          display: "flex",
          justifyContent: "center",
          flexWrap: "wrap",
          
          overflowX: "hidden",
          alignContent: "center",
        }}
      >
        <div className="fade-in" style={{backgroundColor: "rgba(0,0,0,0.8)",width:"100%", height:"100%", display:'flex', alignItems:"center", justifyContent:"center"}}>
          <div className="animate__animated animate__fadeInUpBig" style={{width:"100%", height:"50%"}}>
            <LifeLine color="purple" size="medium" text="" textColor="" />
            <h1>404 - Page Not Found</h1>
            <p>Sorry, the page you are looking for does not exist.</p>
            <Link to="/">Go to Home</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
