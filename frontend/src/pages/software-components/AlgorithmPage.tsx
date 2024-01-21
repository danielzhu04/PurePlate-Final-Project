//will have name of patient box, age box, and weight box
import React, { useState, useEffect } from "react";
import Header from "../../Header";
import SearchHistory from "./SearchHistory";
import { getInitialFood } from "./FetchFoodData";

import { ChangeEvent } from "react";
import {
  Autocomplete,
  TextField
} from "@mui/material";
import { getPurePlateData } from "./FetchReccomendations";


/**
 * This returns a display for the software page
 * @returns an HTML element representing the entire software page
 */
function AlgorithmPage() {

  const [age, setAge] = useState("");
  const [weight, setWeight] = useState("");
  const [height, setHeight] = useState("");
  const [gender, setGender] = useState("");
  const [activityLevel, setActivityLevel] = useState("");
  const [growable, setGrowable] = useState("");
  const [history, setHistory] = useState<(string | string[])[]>([[]]);


  // This function handles the changing of activity checkbox
  function handleActivityLevelChange(event: ChangeEvent<HTMLInputElement>) {
    setActivityLevel(event.target.value);
  }

  //This function handles the changing of gender checkbox
  function handleGenderChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    if (value === "Male" || value === "Female") {
      setGender(value);
    } else {
      setGender("");
      window.alert("Please select a gender");
    }
  }

  //This function handles the changing of weight vale
  function handleWeightChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    if (value !== "" && !isNaN(parseFloat(value)) && parseFloat(value).toString() === value
    ) {
      setWeight(value); // Update the state if the input is a valid number
    } else if (value !== "") {
      setWeight("");
      window.alert("Value entered is not a valid number");
    } else {
      // Handle the case where the input is empty
      setWeight("");
    }
  }

  //This function handles the changing of height value
  function handleHeightChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    if (
      value !== "" && !isNaN(parseInt(value)) &&
      parseInt(value).toString() === value
    ) {
      setHeight(value); // Update the state if the input is a valid number
    } else if (value !== "") {
      console.log("error");
      setHeight("");
      window.alert("Value entered is not a valid number");
    } else {
      setHeight("")
    }
  }

  //This function handles the age change
  function handleAgeChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    if (value !== "" && !isNaN(parseInt(value)) && parseInt(value).toString() === value) {
      setAge(value);
    } else if (value !== "") {
      setAge("");
      window.alert("Value entered is not a valid age");
    } else {
      setAge("")
    }
  }
  
  // This function handles checking the growable
  function handleGrowableChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    if (value === "Yes" || value === "No") {
      setGrowable(value);
    } else {
      setGrowable("");
      window.alert("Please select if growable");
    }
  }

  /**
   * This function handles the submitting of the input. If any of the input is not filled, submit
   * does not call the backend. If all inputs are filled and valid, the function fetches from the backend
   */
  async function handleSubmit(): Promise<void> {
    
    if (
      weight !== "" &&
      height !== "" &&
      age !== "" &&
      gender !== "" &&
      activityLevel !== "" &&
      growable !== "" &&
      selectedFoods.length > 0
    ) {
      setHistory([
        ...history,
        await getPurePlateData(
          weight,
          age,
          height,
          gender,
          activityLevel,
          growable,
          selectedFoods
        ),
      ]);
    } else {
      console.log("One parameter is empty");
    }
  }

  const [foodOptions, setFoodOptions] = useState<string[]>([""]);
  const [selectedFoods, setSelectedFoods] = useState<string[]>([""]);

  // Use Effect used in the autocomplete box
  useEffect(() => {
    const fetchInitialFood = async () => {
      const initialFoodData: string | string[] = await getInitialFood();
      if (
        Array.isArray(initialFoodData) &&
        typeof initialFoodData[0] === "string"
      ) {
        setFoodOptions(initialFoodData);
      } else {
      }
    };

    fetchInitialFood();
  }, []);

  return (
    <div className="AlgorithmPage">
      <Header />
      <div className="Search History">
        <SearchHistory historyData={history} />
      </div>
      <div className="form-container">
        <div className="Weight-container">
          <h1>Weight (kg)</h1>
          <input id="txtbx3" onChange={handleWeightChange}></input>
          <label htmlFor="txtbx3"> </label>
        </div>
        <div className="Height-container">
          <h1>Height (cm)</h1>
          <input id="txtbx2" onChange={handleHeightChange}></input>
          <label htmlFor="txtbx2"> </label>
        </div>
        <div className="age-container">
          <h1>Age</h1>
          <input id="txtbx1" onChange={handleAgeChange}></input>
          <label htmlFor="txtbx1"> </label>
        </div>
        <div className="gender-container">
          <h1>Gender</h1>
          <input
            type="radio"
            id="cb4"
            value="Male"
            checked={gender === "Male"}
            onChange={handleGenderChange}
          ></input>
          <label htmlFor="cb4">Male</label>
          <input
            type="radio"
            id="cb5"
            value="Female"
            checked={gender === "Female"}
            onChange={handleGenderChange}
          ></input>
          <label htmlFor="cb4">Female</label>
        </div>
        {/* <select> */}
        <div className="activity-level-container">
          <h1>Activity Level</h1>
          <input
            type="radio"
            id="rb6"
            value="Sedentary" // might change later
            checked={activityLevel === "Sedentary"}
            onChange={handleActivityLevelChange}
          ></input>
          <label htmlFor="rb6">Sedentary </label>
          <input
            type="radio"
            id="rb7"
            value="Lightly Active"
            checked={activityLevel === "Lightly Active"}
            onChange={handleActivityLevelChange}
          ></input>
          <label htmlFor="rb7"> Lightly Active </label>
          <input
            type="radio"
            id="rb3"
            value="Moderately Active"
            checked={activityLevel === "Moderately Active"}
            onChange={handleActivityLevelChange}
          ></input>
          <label htmlFor="rb3"> Moderately Active </label>
          <input
            type="radio"
            id="rb4"
            value="Very Active"
            checked={activityLevel === "Very Active"}
            onChange={handleActivityLevelChange}
          ></input>
          <label htmlFor="rb4"> Very Active </label>
          <input
            type="radio"
            id="rb5"
            value="Extra Active"
            checked={activityLevel === "Extra Active"}
            onChange={handleActivityLevelChange}
          ></input>
          <label htmlFor="rb5"> Extra Active </label>
        </div>
        <div className="growable-container">
          <h1 className="Growable">Only Search Growable Foods? </h1>
          <input
            type="radio"
            id="rb1"
            value="Yes"
            checked={growable === "Yes"}
            onChange={handleGrowableChange}
          ></input>
          <label htmlFor="rb1"> Yes </label>
          <input
            type="radio"
            id="rb2"
            value="No"
            checked={growable === "No"}
            onChange={handleGrowableChange}
          ></input>
          <label htmlFor="rb2"> No </label>
        </div>
        <div className="selectFoods">Select Foods</div>
        <div className="food-container">
          <Autocomplete
            multiple
            id="foods-autocomplete"
            options={foodOptions}
            value={selectedFoods}
            onChange={(event, newValue) => setSelectedFoods(newValue)}
            disableCloseOnSelect
            getOptionLabel={(option) => option}
            renderInput={(params) => (
              <TextField {...params} variant="standard"/>
            )}
          />
        </div>
        <button
          aria-label="Submit Button"
          id="Submit_Button"
          aria-description="This is the submit button. Click this to submit command you inputted."
          onClick={() => handleSubmit()}
          value="Save"
        >
          Submit
        </button>
      </div>
    </div>
  );
}

export default AlgorithmPage;
