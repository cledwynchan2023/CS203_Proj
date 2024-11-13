import React, { useEffect, useState } from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { Link, useParams, useNavigate } from "react-router-dom";
import backgroundImage from "/src/assets/image1.webp";

import "./style/TournamentDetailStyle.css";
import { CgProfile } from "react-icons/cg";
import { Atom } from "react-loading-indicators";
import { PieChart } from "@mui/x-charts";
import { LineChart } from "@mui/x-charts/LineChart";

export default function ViewProfile() {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [user, setUser] = useState([]);

  const [tournament, setTournament] = useState([]);
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const { userId } = useParams();
  const { playerId } = useParams();
  const [joinedTournaments, setJoinedTournaments] = useState([]);
  const [startTournaments, setStartTournaments] = useState([]);
  const [activeTab, setActiveTab] = useState("Stats");
  const [editedUser, setEditedUser] = useState({
    username: "",
    password: "",
    email: "",
    role: "ROLE_USER",
    confirmPassword: "",
    elo: "",
  });
  const { username, password, email, role, confirmPassword, elo } = editedUser;
  const [ranking, setRanking] = useState([]);

  const handleRowClick = (tournament) => {};

  const getTournaments = () => {
    const completedTournaments = joinedTournaments.filter(
      (tournament) => tournament.status === "completed"
    );

    const tournamentsPlayed = Array.from(
      { length: completedTournaments.length + 1 },
      (_, index) => index
    );

    return tournamentsPlayed;
  };

  const getEloChangesFromEachTournament = () => {
    const completedTournaments = joinedTournaments.filter(
      (tournament) => tournament.status === "completed"
    );
    const eloChanges = completedTournaments.map((tournament) =>
      getEndingElo(tournament)
    );
    return [100, ...eloChanges];
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case "Overview":
        return (
          <>
            {isLoading ? (
              <div
                style={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                }}
              >
                <Atom
                  color="#9e34eb"
                  size={100}
                  style={{ marginTop: "20%", marginLeft: "50%" }}
                ></Atom>
              </div>
            ) : joinedTournaments.filter(
                (tournament) => tournament.status !== "completed"
              ).length === 0 ? (
              <div style={{ textAlign: "center", marginBottom: "20px" }}>
                <p style={{ fontSize: "20px" }}>
                  Player is currently not participating in any tournaments at
                  the moment
                </p>
              </div>
            ) : (
              <section
                className="hero"
                style={{
                  width: "100%",
                  paddingTop: "5%",
                  height: "100%",
                  overflowY: "scroll",
                  paddingLeft: "5%",
                  paddingRight: "5%",
                  margin: "0",
                }}
              >
                <div>
                  <div
                    style={{
                      width: "100%",
                      textAlign: "left",
                      height: "auto",
                      marginBottom: "20px",
                    }}
                  >
                    <p
                      className="title is-family-sans-serif"
                      style={{ textAlign: "left", fontWeight: "bold" }}
                    >
                      Joined Tournaments
                    </p>
                  </div>
                  <table className="table is-hoverable custom-table animate__animated animate__fadeIn">
                    <thead>
                      <tr style={{ height: "50px", paddingBottom: "5px" }}>
                        <th>ID</th>
                        <th>Tournament Name</th>
                        <th>Start Date</th>
                      </tr>
                    </thead>
                    <tbody>
                      {joinedTournaments
                        .filter(
                          (tournament) => tournament.status !== "completed"
                        )
                        .map((tournament) => (
                          <tr
                            key={tournament.id}
                            onClick={() =>
                              handleRowClick(tournament.id, tournament.status)
                            }
                          >
                            <td>{tournament.id}</td>
                            <td>{tournament.tournamentName}</td>
                            <td>{tournament.date}</td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              </section>
            )}
          </>
        );
      case "Stats":
        return (
          <>
            <section
              className="section animate__animated animate__fadeInUpBig"
              style={{
                width: "100%",
                paddingTop: "5%",
                height: "100%",
                overflowY: "scroll",
                paddingLeft: "5%",
                paddingRight: "5%",
                margin: "0",
                gap: "0",
              }}
            >
              <div
                style={{
                  width: "100%",
                  display: "flex",
                  marginBottom: "100px",
                  flexWrap: "wrap",
                  justifyContent: "center",
                  minWidth: "",
                }}
              >
                <div
                  style={{
                    width: "50%",
                    minWidth: "400px",
                    display: "flex",
                    gap: "5%",
                    justifyContent: "center",
                    flexWrap: "wrap",
                    marginBottom: "40px",
                  }}
                >
                  <div style={{ width: "45%" }}>
                    <p
                      className="title is-family-sans-serif"
                      style={{ textAlign: "left", fontWeight: "bold" }}
                    >
                      {" "}
                      Matches Played:
                    </p>
                    <p
                      className="subtitle is-family-sans-serif"
                      style={{ textAlign: "left", marginTop: "10px" }}
                    >
                      {" "}
                      {getTotalMatchesPlayed()} Matches
                    </p>
                  </div>
                  <div style={{ width: "45%" }}>
                    <p
                      className="title is-family-sans-serif"
                      style={{ textAlign: "left", fontWeight: "bold" }}
                    >
                      Win Rate:
                    </p>
                    <p
                      className="subtitle is-family-sans-serif"
                      style={{ textAlign: "left", marginTop: "10px" }}
                    >
                      {" "}
                      {getWinningPercentage()}%
                    </p>
                  </div>
                  <div style={{ width: "45%" }}>
                    <p
                      className="title is-family-sans-serif"
                      style={{ textAlign: "left", fontWeight: "bold" }}
                    >
                      Lose Rate:
                    </p>
                    <p
                      className="subtitle is-family-sans-serif"
                      style={{ textAlign: "left", marginTop: "10px" }}
                    >
                      {" "}
                      {getLosingPercentage()}%
                    </p>
                  </div>
                  <div style={{ width: "45%" }}>
                    <p
                      className="title is-family-sans-serif"
                      style={{ textAlign: "left", fontWeight: "bold" }}
                    >
                      Draw Rate:
                    </p>
                    <p
                      className="subtitle is-family-sans-serif"
                      style={{ textAlign: "left", marginTop: "10px" }}
                    >
                      {" "}
                      {getDrawPercentage()}%
                    </p>
                  </div>
                </div>
                <div
                  style={{
                    width: "50%",
                    display: "flex",
                    flexWrap: "wrap",
                    minWidth: "400px",
                    overflow: "hidden",
                  }}
                >
                  <div
                    style={{
                      width: "50%",
                      minWidth: "350px",
                      overflow: "hidden",
                    }}
                  >
                    <PieChart
                      series={[
                        {
                          data: [
                            {
                              id: 0,
                              value: getWinningPercentage(),
                              label: "Win Rate",
                              color: "purple",
                            },
                            {
                              id: 1,
                              value: getDrawPercentage(),
                              label: "Draw Rate",
                            },
                            {
                              id: 2,
                              value: getLosingPercentage(),
                              label: "Lose Rate",
                              color: "rgba(255, 0, 157, 0.8)",
                            },
                          ],
                          label: { fill: "white" },
                        },
                      ]}
                      sx={{
                        "& .MuiChartsPie-label": {
                          strokeWidth: "0.4",
                          fill: "white",
                          color: "white",
                        },
                      }}
                      width={500}
                      height={300}
                    />
                    <p className="subtitles" style={{ textAlign: "center" }}>
                      Win/Loss/Draws
                    </p>
                  </div>
                  <div style={{ width: "50%", minWidth: "350px" }}>
                    <LineChart
                      xAxis={[
                        {
                          data: getTournaments(),
                          axisLine: { stroke: "purple" },
                          tick: { fill: "purple" },
                        },
                      ]}
                      series={[
                        {
                          data: getEloChangesFromEachTournament(),
                          color: "purple",
                        },
                      ]}
                      sx={{
                        "& .MuiChartsAxis-left .MuiChartsAxis-tickLabel": {
                          strokeWidth: "0.4",
                          fill: "white",
                        },
                        "& .MuiChartsAxis-bottom .MuiChartsAxis-line": {
                          stroke: "white",
                          strokeWidth: 2,
                          strokeDashoffset: 0,
                        },
                        "& .MuiChartsAxis-left .MuiChartsAxis-line": {
                          stroke: "white",
                          strokeWidth: 2,
                          strokeDashoffset: 0,
                        },
                        // change bottom label styles
                        "& .MuiChartsAxis-bottom .MuiChartsAxis-tickLabel": {
                          strokeWidth: "",
                          fill: "white",
                        },
                      }}
                      width={400}
                      height={300}
                    />
                    <p className="subtitles" style={{ textAlign: "center" }}>
                      Elo Changes
                    </p>
                  </div>
                </div>
              </div>
              <div style={{ width: "100%", marginTop: "50px" }}>
                <div
                  style={{
                    width: "100%",
                    textAlign: "left",
                    height: "auto",
                    marginBottom: "20px",
                  }}
                >
                  <p
                    className="title is-family-sans-serif"
                    style={{ textAlign: "left", fontWeight: "bold" }}
                  >
                    Completed Tournaments
                  </p>
                </div>
                <table className="table is-hoverable custom-table animate__animated animate__fadeIn">
                  <thead>
                    <tr style={{ height: "50px", paddingBottom: "5px" }}>
                      <th>ID</th>
                      <th>Tournament Name</th>
                      <th>Start Date</th>
                      <th>Elo Changes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {joinedTournaments
                      .filter((tournament) => tournament.status === "completed")
                      .reverse()
                      .map((tournament) => (
                        <tr
                          key={tournament.id}
                          onClick={() =>
                            handleRowClick(tournament.id, tournament.status)
                          }
                          style={{
                            backgroundColor:
                              getEloChange(tournament) > 0
                                ? "rgba(0,255,0,0.3)"
                                : getEloChange(tournament) < 0
                                ? "rgba(255,0,0,0.45)"
                                : "grey",
                          }}
                        >
                          <td>{tournament.id}</td>
                          <td>{tournament.tournamentName}</td>
                          <td>{tournament.date}</td>
                          <td>{getEloChange(tournament)}</td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              </div>
            </section>
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

  const findRanking = (data) => {
    for (let i = 0; i < data.length; i++) {
      if (data[i].id - playerId == 0) {
        if (i == 0) {
          return 1;
        }
        return i + 1;
      }
    }
  };
  const loadUser = async () => {
    const token = localStorage.getItem("token");
    const result = await axios.get(`http://localhost:8080/u/id/${playerId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    const response2 = await axios.get(`http://localhost:8080/u/users/sorted`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    setSortedUsers(response2.data);
    setRanking(findRanking(response2.data));

    setEditedUser({
      username: result.data.username,
      email: result.data.email,
      elo: result.data.elo,
    });

    setUser(result.data);
  };

  const getEloChangesFromMatch = (match) => {
    if (match.player1 - playerId == 0) {
      return match.eloChange1;
    } else if (match.player2 - playerId == 0) {
      return match.eloChange2;
    }
  };
  const getStartingEloFromMatch = (match) => {
    if (match.player1 - playerId == 0) {
      return match.player1StartingElo;
    } else if (match.player2 - playerId == 0) {
      return match.player2StartingElo;
    }
  };

  const getDrawMatches = (rounds) => {
    let count = 0;
    for (let i = 0; i < rounds.length; i++) {
      const matchList = rounds[i].matchList;
      const match = findMatch(matchList);
      if (match.result == 0) {
        if (match.player1 - playerId == 0 || match.player2 - playerId == 0) {
          count++;
        }
      }
    }

    return count;
  };
  const getDrawPercentage = () => {
    const completedTournaments = joinedTournaments.filter(
      (tournament) => tournament.status === "completed"
    );
    let totalDraw = 0;
    for (let i = 0; i < completedTournaments.length; i++) {
      const tournament = completedTournaments[i];
      const noOfRounds = tournament.noOfRounds;
      totalDraw = totalDraw + getDrawMatches(tournament.rounds);
    }
    const totalMatches = getTotalMatches(completedTournaments);

    return (totalDraw / totalMatches).toFixed(2) * 100;
  };

  const getWinningMatches = (rounds) => {
    let count = 0;
    for (let i = 0; i < rounds.length; i++) {
      const matchList = rounds[i].matchList;
      const match = findMatch(matchList);
      if (match.result == -1) {
        if (match.player1 - playerId == 0) {
          count++;
        }
      } else if (match.result == 1) {
        if (match.player2 - playerId == 0) {
          count++;
        }
      }
    }

    return count;
  };
  const getTotalMatches = (tournament) => {
    let totalMatches = 0;
    for (let i = 0; i < tournament.length; i++) {
      const currTournament = tournament[i];
      const noOfRounds = currTournament.noOfRounds;
      totalMatches = totalMatches + noOfRounds;
    }

    return totalMatches;
  };

  const getTotalMatchesPlayed = () => {
    const completedTournaments = joinedTournaments.filter(
      (tournament) => tournament.status === "completed"
    );
    return getTotalMatches(completedTournaments);
  };

  const getLosingMatches = (rounds) => {
    let count = 0;
    for (let i = 0; i < rounds.length; i++) {
      const matchList = rounds[i].matchList;
      const match = findMatch(matchList);
      if (match.result == -1) {
        if (match.player2 - playerId == 0) {
          count++;
        }
      } else if (match.result == 1) {
        if (match.player1 - playerId == 0) {
          count++;
        }
      }
    }

    return count;
  };
  const getLosingPercentage = () => {
    const completedTournaments = joinedTournaments.filter(
      (tournament) => tournament.status === "completed"
    );
    let totalLose = 0;
    for (let i = 0; i < completedTournaments.length; i++) {
      const tournament = completedTournaments[i];
      const noOfRounds = tournament.noOfRounds;
      totalLose = totalLose + getLosingMatches(tournament.rounds);
    }
    const totalMatches = getTotalMatches(completedTournaments);

    return (totalLose / totalMatches).toFixed(2) * 100;
  };
  const getWinningPercentage = () => {
    const completedTournaments = joinedTournaments.filter(
      (tournament) => tournament.status === "completed"
    );
    let totalWin = 0;
    for (let i = 0; i < completedTournaments.length; i++) {
      const tournament = completedTournaments[i];
      const noOfRounds = tournament.noOfRounds;
      totalWin = totalWin + getWinningMatches(tournament.rounds);
    }
    const totalMatches = getTotalMatches(completedTournaments);

    return (totalWin / totalMatches).toFixed(2) * 100;
  };
  const findMatch = (matchList) => {
    for (let i = 0; i < matchList.length; i++) {
      if (
        matchList[i].player1 - playerId == 0 ||
        matchList[i].player2 - playerId == 0
      ) {
        return matchList[i];
      }
    }
  };

  const getEloChange = (tournament) => {
    const noOfRounds = tournament.noOfRounds;

    const firstMatch = findMatch(tournament.rounds[0].matchList);
    const startingElo = getStartingEloFromMatch(firstMatch);
    const matchListTemp = tournament.rounds[noOfRounds - 1].matchList;

    const lastMatch = findMatch(matchListTemp);
    const endingElo =
      getStartingEloFromMatch(lastMatch) + getEloChangesFromMatch(lastMatch);

    return endingElo - startingElo;
  };

  const getEndingElo = (tournament) => {
    const noOfRounds = tournament.noOfRounds;
    const matchListTemp = tournament.rounds[noOfRounds - 1].matchList;

    const lastMatch = findMatch(matchListTemp);
    const endingElo =
      getStartingEloFromMatch(lastMatch) + getEloChangesFromMatch(lastMatch);

    return endingElo;
  };

  const loadTournaments = async () => {
    const result3 = await axios.get(
      `http://localhost:8080/u/${playerId}/currentTournament`
    );

    if (!result3.data.length == 0) {
      setJoinedTournaments(result3.data);
    } else {
      setJoinedTournaments([]);
    }
  };

  const isTokenExpired = () => {
    const expiryTime = localStorage.getItem("tokenExpiry");
    if (!expiryTime) return true;
    return new Date().getTime() > expiryTime;
  };

  const isAdminToken = (token) => {
    try {
      const decodedToken = jwtDecode(token);

      if (
        (decodedToken.authorities === "ROLE_ADMIN" ||
          decodedToken.authorities === "ROLE_USER") &&
        decodedToken.userId == userId
      ) {
        return true;
      } else {
        return false;
      }
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
          `http://localhost:8080/u/id/${playerId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setData(response.data);
        setUser(response.data);
      } catch (error) {
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
    loadTournaments();
    loadUser();
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
        }}
      >
        <div
          className="content"
          style={{ width: "100%", height: "100%", overflowY: "scroll" }}
        >
          <section
            className="hero is-flex-direction-row fade-in"
            style={{
              paddingLeft: "5%",
              paddingRight: "5%",
              width: "100%",
              backgroundColor: "rgba(0, 0, 0, 0.8)",
              paddingBottom: "20px",
              paddingTop: "20px",
            }}
          >
            <div style={{ width: "200px", marginRight: "20px" }}>
              <CgProfile style={{ width: "100%", height: "100%" }} />
            </div>
            <div style={{ width: "90%", alignContent: "center" }}>
              <p
                className="title is-family-sans-serif"
                style={{ width: "80%", fontWeight: "bold" }}
              >
                {user.username}
              </p>
              <p className="subtitle">ID: {user.id}</p>
              <p className="subtitle" style={{ marginTop: "-10px" }}>
                Elo: {user.elo}, Ranking: {ranking}{" "}
              </p>
            </div>
          </section>

          <section
            className="hero fade-in"
            style={{
              paddingLeft: "2%",
              paddingRight: "2%",
              width: "100%",
              backgroundColor: "rgba(0, 0, 0, 0.8)",
              height: "100%",
            }}
          >
            <div
              className="tabs is-left"
              style={{ height: "80px", margin: "0" }}
            >
              <ul>
                <li className={activeTab === "Stats" ? "is-active" : ""}>
                  <a onClick={() => setActiveTab("Stats")}>Stats</a>
                </li>
                <li className={activeTab === "Overview" ? "is-active" : ""}>
                  <a onClick={() => setActiveTab("Overview")}>Overview</a>
                </li>
              </ul>
            </div>
            <div
              style={{ backgroundColor: "rgba(0, 0, 0, 0.3)", height: "92%" }}
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
