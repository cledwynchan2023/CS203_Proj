import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "../Admin/Register.css";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { useParams } from "react-router-dom";
export default function TournamentAdminEdit() {
  let navigate = useNavigate();

  const [tournament, setTournament] = useState({
    tournament_name: "",
    date: "",
    status: "active",
    size: "",
    noOfRounds: 0,
  });
  const { tournament_name, date, status, size, noOfRounds } = tournament;
  const { userId } = useParams();
  const { id } = useParams();

  const onInputChange = (e) => {
    setTournament({ ...tournament, [e.target.name]: e.target.value });
  };

  const isValidDateFormat = (input) => {
    const regex = /^\d{2}\/\d{2}\/\d{4}$/;

    return regex.test(input);
  };

  const isTokenExpired = () => {
    const expiryTime = localStorage.getItem("tokenExpiry");
    if (!expiryTime) return true;
    return new Date().getTime() > expiryTime;
  };

  const isAdminToken = (token) => {
    try {
      const decodedToken = jwtDecode(token);

      return decodedToken.authorities === "ROLE_ADMIN"; // Adjust this based on your token's structure
    } catch (error) {
      return false;
    }
  };
  const clearTokens = () => {
    localStorage.removeItem("token"); // Remove the main token
    localStorage.removeItem("tokenExpiry"); // Remove the token expiry time
    // Add any other tokens you want to clear here
    // localStorage.removeItem('anotherToken');
    // tokenKeys.forEach(key => {
    //     localStorage.removeItem(key);
    // });
  };

  const onSubmit = async (e) => {
    e.preventDefault();

    if (!isValidDateFormat(date)) {
      alert("Invalid date! Please enter in the format MM/DD/YYYY");
      return;
    }

    const tournamentData = {
      tournament_name,
      date,
      status,
      size,
      noOfRounds,
    };

    try {
      const response = await axios.put(
        `http://localhost:8080/auth/tournament/${id}`,
        tournamentData
      );
      if (response.status === 200) {
        alert("Tournament Edited Successfully");
        navigate(`/admin/${userId}/tournament`);
      }
    } catch (error) {
      console.error("There was an error registering the tournament!", error);
    }
  };

  const loadTournaments = async () => {
    const result = await axios.get(
      `http://localhost:8080/auth/tournament/${id}`
    );
    setTournament(result.data);
  };
  useEffect(() => {
    const fetchData = async () => {
      const token = localStorage.getItem("token");

      if (!token || isTokenExpired() || !isAdminToken(token)) {
        clearTokens();
        window.location.href = "/"; // Redirect to login if token is missing or expired
        return;
      }
    };

    fetchData();
    loadTournaments(id);
  }, []);

  return (
    <div className="register-container">
      <div className="form-container">
        <form onSubmit={(e) => onSubmit(e)}>
          <div className="form-floating mb-3">
            <input
              type="text"
              className="form-control form-control-lg"
              id="floatingInput"
              placeholder="name@example.com"
              value={tournament_name}
              onChange={(e) => onInputChange(e)}
              name="tournament_name"
            ></input>
            <label htmlFor="tournament_name">Tournament Name</label>
          </div>

          <div className="form-floating mb-3">
            <input
              type="text"
              className="form-control"
              id="floatingUsername"
              placeholder="Date"
              value={date}
              onChange={(e) => onInputChange(e)}
              name="date"
            />
            <label htmlFor="Date">Date</label>
          </div>
          <div className="form-floating mb-3 mt-3">
            <select
              className="form-control"
              id="floatingRole"
              value={status}
              onChange={(e) => onInputChange(e)}
              name="status"
            >
              <option value="active">Active</option>
              <option value="completed">Completed</option>
            </select>
            <label htmlFor="Status">Status</label>
          </div>
          <div className="form-floating mb-3">
            <input
              type="number"
              className="form-control"
              placeholder="size"
              value={size}
              onChange={(e) => onInputChange(e)}
              name="size"
            />
            <label htmlFor="size">Number of participants</label>
          </div>

          <div className="form-floating">
            <input
              type="number"
              className="form-control"
              placeholder="noOfRounds"
              value={noOfRounds}
              onChange={(e) => onInputChange(e)}
              name="noOfRounds"
            />
            <label htmlFor="noOfRounds">Number of rounds</label>
          </div>

          <button type="submit" className="btn btn-outline-primary mt-3">
            Edit Tournament
          </button>
        </form>
        <Link
          type="cancel"
          className="btn btn-outline-danger"
          to={`/admin/${userId}/tournament`}
          id="returnrBtn"
        >
          Cancel
        </Link>
      </div>
    </div>
  );
}
