import React from "react";
import Header from "../Header";
import Pagebreaker from "../Page-Breaker";
import "../styles/App.css";
import TextBox from "../HomePage/text";
import Profile from "../HomePage/profile";

import granImage from "../public/gardening.png";
import pot from "../public/6360237.jpg";

//This is the high level file that contains all of the content for the first page.
class App extends React.Component {
  render() {
    return (
      <div className="app">
        <Header />

        <div className="granny-image-container">
          <img id="grannyImage " src={granImage} />
        </div>

        <div className="potted-plant-image-container">
          <img id="pottedPlant " src={pot} />
        </div>

        <>
          <TextBox></TextBox>
          <Pagebreaker></Pagebreaker>
          <Profile></Profile>
        </>
      </div>
    );
  }
}

export default App;
