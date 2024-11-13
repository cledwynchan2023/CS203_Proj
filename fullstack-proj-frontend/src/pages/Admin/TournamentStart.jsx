import React, { useEffect, useState } from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { Link, useParams, useNavigate } from "react-router-dom";
import backgroundImage from "/src/assets/image1.webp";
import comp1 from "/src/assets/comp1.png";
import "./style/TournamentStart.css";
export default function TournamentStart() {
  const navigate = useNavigate();
  const [user, setUser] = useState([]);
  const [nonParticpatingUser, setNonParticipatingUser] = useState([]);
  const [tournament, setTournament] = useState([]);
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const { userId } = useParams();
  const { id } = useParams();
  const [activeTab, setActiveTab] = useState("Overview");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editedTournament, setEditedTournament] = useState({
    tournament_name: "",
    date: "",
    status: "active",
    size: "",
    noOfRounds: 0,
  });
  const { tournament_name, date, status, size, noOfRounds } = editedTournament;
  const [isStart, setIsStart] = useState(1);
  const [scoreboard, setScoreboard] = useState(new Map());
  const onInputChange = (e) => {
    setEditedTournament({
      ...editedTournament,
      [e.target.name]: e.target.value,
    });
  };
  const [tournamentRound, setTournamentRound] = useState(1);
  const [pairing, setPairing] = useState([]);
  const [round, setRound] = useState([]);
  const [currentRound, setCurrentRound] = useState(tournamentRound);
  const [disabledButtons, setDisabledButtons] = useState({});
  const [noOfMatches, setNoOfMatches] = useState(1);
  const onSubmit = async (e) => {
    e.preventDefault();

    const tournamentData = {
      tournament_name,
      date,
      status,
      size,
      noOfRounds,
    };

    try {
      const response = await axios.put(
        `http://localhost:8080/t/${id}`,
        tournamentData
      );
      if (response.status === 200) {
        alert("Tournament Edited Successfully");
        setIsEditModalOpen(false);
        loadTournament();
      }
    } catch (error) {
      console.error("There was an error registering the tournament!", error);
    }
  };
  const handlePageClick = (round) => {
    if (round > tournamentRound) {
      return;
    } else if (round <= tournamentRound) {
      setCurrentRound(round);
      setPairing(tournament.rounds[round - 1].matchList);
      setRound(tournament.rounds[round - 1]);
    }
  };
  const renderPagination = () => {
    const pages = [];
    for (let i = 1; i <= tournament.noOfRounds; i++) {
      const isCurrentRound = i === tournamentRound;
      const isPastRound = i < tournamentRound;
      pages.push(
        <li key={i}>
          <a
            href="#"
            className={`pagination-link ${isCurrentRound ? "is-current" : ""}`}
            aria-label={`Goto page ${i}`}
            aria-current={isCurrentRound ? "page" : undefined}
            style={{
              color: isCurrentRound ? "black" : isPastRound ? "blue" : "grey",
              pointerEvents: isCurrentRound || isPastRound ? "auto" : "none",
              cursor: isCurrentRound || isPastRound ? "pointer" : "default",
            }}
            onClick={() => handlePageClick(i)}
          >
            {i}
          </a>
        </li>
      );
    }
    return pages;
  };

  const getIsCompleted = async (currentRound) => {
    const token = localStorage.getItem("token");

    const result = await axios.get(
      `http://localhost:8080/t/tournament/${id}/start`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return result.data.rounds[currentRound - 1].isCompleted;
  };

  const handleResult = async (matchId, result) => {
    setDisabledButtons((prev) => ({ ...prev, [matchId]: true }));
    const token = localStorage.getItem("token");

    const response = await axios.put(
      `http://localhost:8080/m/match/${matchId}/update`,
      { result },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    setNoOfMatches(noOfMatches + 1);
    const isCompleted = await getIsCompleted(tournamentRound);
    if (isCompleted) {
      alert("Round completed");
      setNoOfMatches(1);
    }
    loadTournament();
  };

  const getUsername = (selectedId) => {
    for (let i = 0; i < user.length; i++) {
      if (user[i].id === selectedId) {
        return user[i].username;
      }
    }
    return user ? user.username : "";
  };

  const getUser = (selectedId) => {
    for (let i = 0; i < user.length; i++) {
      if (user[i].id - selectedId == 0) {
        return user[i];
      }
    }
    return user ? user : "";
  };

  const renderTabContent = () => {
    const sortedScoreboard = Array.from(scoreboard.entries()).sort(
      (a, b) => b[1] - a[1]
    );
    switch (activeTab) {
      case "Overview":
        return (
          <section
            className="section is-flex is-family-sans-serif animate__animated animate__fadeInUpBig"
            style={{
              width: "100%",
              overflowY: "scroll",
              height: "100%",
              marginBottom: "50px",
              paddingTop: "0",
            }}
          >
            <div style={{ width: "100%", height: "60%" }}>
              <div style={{ width: "100%", height: "50px" }}>
                <p className="title is-2">Round {currentRound}</p>
              </div>
              <nav
                class="pagination"
                role="navigation"
                aria-label="pagination"
                style={{
                  display: "flex",
                  alignItems: "center",
                  height: "auto",
                  width: "100%",
                  marginTop: "20px",
                }}
              >
                <a
                  className={`pagination-previous ${
                    currentRound === 1 ? "is-disabled" : ""
                  }`}
                  title="Previous Round"
                  onClick={() =>
                    currentRound > 1 && handlePageClick(currentRound - 1)
                  }
                >
                  Previous Round
                </a>
                <a
                  className={`pagination-previous ${
                    currentRound === tournamentRound ? "is-disabled" : ""
                  }`}
                  title="Next Round"
                  onClick={() =>
                    currentRound >= 1 && handlePageClick(currentRound + 1)
                  }
                >
                  Next round
                </a>
                <ul
                  class="pagination-list"
                  style={{
                    height: "auto",
                    marginBottom: "10px",
                    marginLeft: "0",
                  }}
                >
                  {renderPagination()}
                </ul>
              </nav>
              {pairing.map((pair, index) => {
                const backgroundColor = index % 2 === 0 ? "#252525" : "#212121"; // Alternate colors
                const matchId = round.matchList[index].id; // Assuming each pair has a unique matchId
                const isDisabled = round.matchList[index].complete;
                const matchResult = round.matchList[index].result;
                const getBorderStyle = (playerIndex) => {
                  if (matchResult === 0) {
                    return {
                      border: "5px solid yellow",
                      borderRadius: "20px",
                      backgroundColor: "grey",
                    };
                  } else if (matchResult === -1 && playerIndex === 1) {
                    return {
                      border: "5px solid green",
                      borderRadius: "20px",
                      backgroundColor: "green",
                    };
                  } else if (matchResult === 1 && playerIndex === 2) {
                    return {
                      border: "5px solid green",
                      borderRadius: "20px",
                      backgroundColor: "green",
                    };
                  }

                  if (matchResult == -1 && playerIndex === 2) {
                    return {
                      border: "5px solid red",
                      borderRadius: "20px",
                      backgroundColor: "red",
                    };
                  } else if (matchResult === 1 && playerIndex === 1) {
                    return {
                      border: "5px solid red",
                      borderRadius: "20px",
                      backgroundColor: "red",
                    };
                  }
                };

                return (
                  <div
                    class="card"
                    style={{
                      width: "100%",
                      minWidth: "300px",
                      height: "auto",
                      display: "flex",
                      alignItems: "center",
                      marginBottom: "-10px",
                      backgroundColor: backgroundColor,
                    }}
                  >
                    <div
                      class="card-content"
                      style={{
                        display: "flex",
                        justifyContent: "center",
                        overflowX: "scroll",
                        height: "100%",
                        width: "100%",
                        flexWrap: "wrap",
                        backgroundColor: "",
                      }}
                    >
                      <div
                        style={{
                          width: "80%",
                          display: "flex",
                          minWidth: "300px",
                          justifyContent: "center",
                          backgroundColor: "",
                        }}
                      >
                        <div
                          class="content"
                          style={{
                            width: "35%",
                            textAlign: "center",
                            height: "100%",
                            ...getBorderStyle(1),
                          }}
                        >
                          <p class="subtitle" style={{ fontSize: "1rem" }}>
                            {"Id: " + pair.player1}
                          </p>
                          <p
                            class="title"
                            style={{ fontSize: "1.8rem", fontWeight: "bold" }}
                          >
                            {getUsername(pair.player1)}
                          </p>
                        </div>
                        <div
                          style={{
                            width: "30%",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                          }}
                        >
                          <p
                            class="title"
                            style={{
                              fontSize: "2rem",
                              fontWeight: "bold",
                              textAlign: "center",
                            }}
                          >
                            VS
                          </p>
                        </div>
                        <div
                          class="content"
                          style={{
                            width: "35%",
                            textAlign: "center",
                            height: "100%",
                            ...getBorderStyle(2),
                          }}
                        >
                          <p class="subtitle" style={{ fontSize: "1rem" }}>
                            {"Id: " + pair.player2}
                          </p>
                          <p
                            class="title"
                            style={{ fontSize: "1.8rem", fontWeight: "bold" }}
                          >
                            {getUsername(pair.player2)}
                          </p>
                        </div>
                      </div>

                      <div
                        class="content"
                        style={{
                          backgroundColor: "",
                          width: "20%",
                          justifyContent: "center",
                          display: "flex",
                          alignItems: "center",
                          gap: "3%",
                          minWidth: "300px",
                          marginTop: "20px",
                        }}
                      >
                        <button
                          className="button is-link"
                          onClick={() => {
                            handleResult(matchId, -1);
                          }}
                          disabled={isDisabled}
                          style={{ width: "30%", minWidth: "100px" }}
                        >
                          Player 1 win
                        </button>
                        <button
                          className="button is-warning"
                          onClick={() => {
                            handleResult(matchId, 0);
                          }}
                          disabled={isDisabled}
                          style={{ width: "30%", minWidth: "100px" }}
                        >
                          Draw
                        </button>
                        <button
                          className="button is-primary"
                          onClick={() => {
                            handleResult(matchId, 1);
                          }}
                          disabled={isDisabled}
                          style={{ width: "30%", minWidth: "100px" }}
                        >
                          Player 2 win
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </section>
        );
      case "Players":
        return (
          <section
            className=" is-flex is-family-sans-serif animate__animated animate__fadeInUpBig"
            style={{ height: "600px", width: "100%", justifyContent: "center" }}
          >
            <div
              className="card"
              style={{
                width: "90%",
                display: "flex",
                justifyContent: "start",
                paddingTop: "30px",
                height: "100%",
                overflowY: "scroll",
              }}
            >
              <table
                className="table is-hoverable custom-table"
                style={{ width: "100%", paddingLeft: "10px" }}
              >
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Elo</th>
                  </tr>
                </thead>
                <tbody>
                  {user.map((user, index) => (
                    <tr>
                      <td>{user.username}</td>
                      <td>{user.elo}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        );
      case "Scoreboard":
        return (
          <>
            <section
              className=" is-flex is-family-sans-serif animate__animated animate__fadeInUpBig"
              style={{
                height: "600px",
                width: "100%",
                justifyContent: "center",
              }}
            >
              <div
                className="card"
                style={{
                  width: "80%",
                  display: "flex",
                  justifyContent: "start",
                  paddingTop: "30px",
                  height: "100%",
                  overflowY: "scroll",
                }}
              >
                <table
                  className="table is-hoverable custom-table"
                  style={{ width: "100%", paddingLeft: "10px" }}
                >
                  <thead>
                    <tr>
                      <th>Pos</th>
                      <th>Username</th>
                      <th>Elo</th>
                      <th>Score</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Array.from(scoreboard.entries())
                      .reverse()
                      .map(([id, score], index) => {
                        const user = getUser(id); // Assuming you have a users array with user details
                        return (
                          <tr key={id}>
                            <td>{index + 1}</td>
                            <td>{user ? user.username : "Unknown"}</td>
                            <td>{user ? user.elo : "Unknown"}</td>
                            <td>{score}</td>
                          </tr>
                        );
                      })}
                  </tbody>
                </table>
              </div>
            </section>
            ;
          </>
        );
      default:
        return null;
    }
  };

  const clearTokens = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenExpiry");
  };
  const loadTournament = async () => {
    const token = localStorage.getItem("token");
    const result = await axios.get(
      `http://localhost:8080/t/tournament/${id}/start`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const resultName = result.data.tournamentName;

    setTournament(result.data);

    setTournamentRound(result.data.currentRound);
    setScoreboard(result.data.rounds[result.data.currentRound - 1].scoreboard);
    setRound(result.data.rounds[result.data.currentRound - 1]);
    setCurrentRound(result.data.currentRound);
    setScoreboard(
      new Map(
        result.data.rounds[
          result.data.currentRound - 1
        ].scoreboard.scoreboardEntries.map((entry) => [
          entry.playerId,
          entry.score,
        ])
      )
    );

    setPairing(result.data.rounds[result.data.currentRound - 1].matchList);
    loadNonParticipatingUsers();
    setUser(result.data.participants);

    if (result.data.status == "completed") {
      alert("Tournament has ended");
      navigate(`/admin/${userId}/tournament/${id}/completed`);
    }
  };

  const loadNonParticipatingUsers = async () => {
    const token = localStorage.getItem("token");

    const response = await axios.get(`http://localhost:8080/t/users/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    setNonParticipatingUser(response.data);
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

  useEffect(() => {
    const fetchData = async () => {
      const token = localStorage.getItem("token");

      if (!token || isTokenExpired() || !isAdminToken(token)) {
        clearTokens();
        window.location.href = "/"; // Redirect to login if token is missing or expired
        return;
      }

      try {
        const response = await axios.get(
          `http://localhost:8080/t/tournament/${id}/start`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setData(response.data);
        setCurrentRound(response.data.currentRound);
        setRound(response.data.rounds[response.data.currentRound - 1]);

        setPairing(
          response.data.rounds[response.data.currentRound - 1].matchList
        );
        setTournament(response.data);

        setScoreboard(
          new Map(
            response.data.rounds[
              response.data.currentRound - 1
            ].scoreboard.scoreboardEntries.map((entry) => [
              entry.playerId,
              entry.score,
            ])
          )
        );
        setTournamentRound(response.data.currentRound);

        setUser(response.data.participants);

        if (response.data.status == "active") {
          alert("Tournament has not started yet");
          navigate(`/admin/${userId}/tournament/${id}`);
        } else if (response.data.status == "ongoing") {
          setIsStart(1);
        } else {
          alert("Tournament has ended");
          navigate(`/admin/${userId}/tournament/${id}/completed`);
          setIsStart(-1);
        }
      } catch (error) {
        console.log("ERROR" + error);
        if (error.response && error.response.status === 401) {
          clearTokens();
          localStorage.removeItem("token"); // Remove token from localStorage
          window.location.href = "/"; // Redirect to login if token is invalid
        } else {
          setError("An error occurred while fetching data.");
        }
      }
    };

    fetchData();
    // Convert the scoreboard Map to an array and reverse it

    loadNonParticipatingUsers();

    //loadUsers();
  }, []);

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <>
      <div
        className="background-container"
        style={{
          backgroundImage: `url(${backgroundImage})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          backgroundRepeat: "no-repeat",
          flexWrap: "wrap",
          height: "100vh",
        }}
      >
        <div
          className="content"
          style={{
            width: "100%",
            height: "100%",
            backgroundColor: "rgba(0, 0, 0, 0.7)",
            overflow: "scroll",
          }}
        >
          <section
            className=" fade-in"
            style={{
              width: "100%",
              display: "flex",
              flexWrap: "wrap",
              padding: "10px",
              height: "20%",
              minHeight: "100px",
              paddingLeft: "20px",
              paddingRight: "20px",
            }}
          >
            <div
              style={{
                display: "flex",
                justifyContent: "left",
                alignItems: "center",
                width: "100%",
                minWidth: "400px",
              }}
            >
              <div style={{ width: "100px" }}>
                <img src={comp1} width={150}></img>
              </div>
              <div
                style={{
                  width: "80%",
                  alignContent: "center",
                  display: "flex",
                  flexWrap: "wrap",
                  paddingLeft: "20px",
                  minWidth: "300px",
                }}
              >
                <p
                  className="title is-family-sans-serif"
                  style={{ width: "80%", fontWeight: "bold" }}
                >
                  {tournament.tournamentName}
                </p>
                <p class="subtitle" style={{ width: "100%" }}>
                  ID: {tournament.id}
                </p>
              </div>
            </div>
          </section>

          <section
            className="hero fade-in"
            style={{
              width: "100%",
              backgroundColor: "rgba(0, 0, 0, 0.8)",
              height: "80%",
              marginBottom: "20px",
              minHeight: "600px",
            }}
          >
            <div
              className="tabs is-left"
              style={{ height: "10%", minHeight: "70px" }}
            >
              <ul>
                <li className={activeTab === "Overview" ? "is-active" : ""}>
                  <a onClick={() => setActiveTab("Overview")}>Overview</a>
                </li>

                <li className={activeTab === "Scoreboard" ? "is-active" : ""}>
                  <a onClick={() => setActiveTab("Scoreboard")}>Scoreboard</a>
                </li>
              </ul>
            </div>
            <div
              style={{
                backgroundColor: "rgba(0, 0, 0, 0.3)",
                height: "90%",
                width: "100%",
              }}
            >
              {renderTabContent()}
            </div>
          </section>
        </div>
      </div>
      <footer
        className="footer"
        style={{ textAlign: "center", marginTop: "100px", height: "100px" }}
      >
        <p>&copy; 2024 CS203. All rights reserved.</p>
      </footer>
    </>
  );
}
