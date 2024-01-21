const foodsURL = "http://localhost:3233/data?"

/**
 *  Calls the backend server for a list of foods for the purposes of displaying on the frontend
 * @returns A list of strings representing the food database (all foods used for calculations) from the backend
 */
export async function getInitialFood() : Promise<string[]| string>{
    try {
      const data_response = await fetch(`${foodsURL}`);
      const data_json = await data_response.json();
      console.log("food data");
      console.log(data_json.foods);
      return data_json.foods;
    } catch (err) {
      return "Unable to retrive food data";
    }
}
