
import React from "react";
import { useNavigate } from "react-router-dom";

interface HeaderProps {}

// This is the Header class responsible for 
const Header = () => {
    const navigate = useNavigate();
    //Thsi function routes to the Home page
    function handleClickHome() {
      // Logic to handle the "Home" button click
      if (window.location.href == "http://localhost:8000/software") {
        window.location.href = "http://localhost:8000";
      }
      window.scrollTo({
        top: 0,
        left: 0,
        behavior: "smooth",
      });
    }
    //This function routes to the Team homepage
    function handleClickTeam() {
      if (window.location.href.includes("/software")) {
        navigate("/");
        setTimeout(() => {
          window.scrollTo({ top: 600, left: 0, behavior: "smooth" });
        }, 500); // Adjust the timeout as needed
      } else {
        window.scrollTo({
          top: 600,
          left: 0,
          behavior: "smooth",
        });
      }
    }

    return (
      <div className="header">
        <h1 id="headerbar">
          PurePlate
          <button className="HomeButton" onClick={handleClickHome}>
            Home
          </button>
          <button className="TeamButton" onClick={handleClickTeam}>
            Meet The Team
          </button>
          <button
            className="ApplicationButton"
            onClick={() => navigate("/software")}
          >
            Software
          </button>
        </h1>
      </div>
    );
};

export default Header;
