//will have search bar, home button, login butotn, and contact button

import React from "react";
// const grannyImage = require("../public/old-woman-gardening.avif");
const TextBox = () => {
  {
    return (
      <div className="mission statement">
        {/* <img src={grannyImage} /> */}
        <h3>Our Mission</h3>
        <h4 className="mission ">
          Founded in 2023 in Providence, Rhode Island by 4 Brown University
          Students, PurePlate aims to provide nutritional and gardening advice
          to nursing homes across the world. Nursing home patients are often
          left idle and maintain poor diets with minimal nutritional value. We
          aim to change that. Here at PurePlate we aim to encourage sustainable,
          delicious, and nutrient dense foods to senior citizens whilst also
          ensuring they maintain active throughout their senior years. Our state
          of the art application aims to recommend easy to grow vegetables to
          help nourish the aging population through a complex mathematical
          algorithm taking into account multiple factors. We reference the Mifflin St Jeor
          equation in our software.
          <br />
          Click the Software button to begin using the software.
        </h4>
      </div>
    );
  }
};

export default TextBox;
