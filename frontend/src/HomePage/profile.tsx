

import React from "react";
import wilsonPhoto from "../public/IMG_1095.jpg";
import kylesPhoto from "../public/1696464658844.png";
import danielsPhoto from "../public/square-1488906206-daniel-craig.jpg";
import gracesPhoto from "../public/78745131.jpg";
// This returns HTML for the profile descriptions and images on the meet the team page
const Profile = () => {
  {
    return (
      <div className="Team Descriptions">
        <h3 id="MeetTheTeam">Meet The Team</h3> <br />
        <h1 id="Daniel">
          Daniel Zhu <br />
          Backend Developer <br />
          Daniel_zhu1@Brown.edu
        </h1>
        <div className="image-container3">
          <img id="danielsPhoto" src={danielsPhoto}></img>
        </div>
        <h1 id="Kyle">
          Kyle Yeh <br />
          Backend Developer <br />
          Kyle_Yeh@Brown.edu
        </h1>
        <div className="image-container2">
          <img id="kylesPhoto" src={kylesPhoto}></img>
        </div>
        <h1 id="Grace">
          Grace Chen <br />
          Frontend Developer <br />
          Grace_A_Chen@Brown.edu
        </h1>
        <div className="image-container4">
          <img id="gracesPhoto" src={gracesPhoto}></img>
        </div>
        <h1 id="Wilson">
          Wilson Vo <br />
          Frontend and UI/UX Developer <br />
          Wilson_Vo@Brown.edu
        </h1>
        <div className="image-container">
          <img id="wilsonsPhoto" src={wilsonPhoto}></img>
        </div>
      </div>
    );
  }
};

export default Profile;
