const recommendationsURL = "http://localhost:3233/pureplate?"

/**
 * Converts the list of foods into a delimited string to be placed into url
 * @param list list of foods that the user has selected
 * @returns a string representation of the list of foods
 */
function createFoodString(list: string[]) : string{
  let condensedList = ""
  condensedList = list[0]
  for (let i = 1; i < list.length - 1; i++) {
    condensedList = condensedList + list[i] + "`";
  }
  condensedList += list[list.length - 1]
  return condensedList.replace(/ /g, "%20").trim();

}

/**
 * Call the PurePlate server to calculate a list of recommended foods based on demographic information and 
 * a list of foods that already contribute to their diet
 * @param weight weight of the individual (in kg)
 * @param age age of the individual
 * @param height height of the individual (in cm)
 * @param gender gender of the individual (male or female)
 * @param activityLevel the activity level (5 levels)
 * @param growable whether you are looking specifically for growable foods
 * @param foods the list of chosen foods
 * @returns 
 */
export async function getPurePlateData(weight: string, age: string, height: string, gender: string, activityLevel: string, growable: string, foods: string[]): Promise<string | string[]> {
    try {
        // return "hi"
        const foodList = createFoodString(foods);
        const pureplate_response = await fetch(
          `${recommendationsURL}weight=${weight}&height=${height}&age=${age}&gender=${gender}&activity=${activityLevel}&growable=${growable}&foods=${createFoodString(foods)}`
        );
        console.log(
          `${recommendationsURL}weight=${weight}&height=${height}&age=${age}&gender=${gender}&activity=${activityLevel}&growable=${growable}&foods=${createFoodString(foods)}`
        );
        const pureplate_json = await pureplate_response.json();
        if (!isSuccessfulRecommendationCall(pureplate_json)) {
          if ("message" in pureplate_json) {
            return pureplate_json.message;
          }
          return "Input data was not in the correct format";
        }
        const recommendations = pureplate_json.recommendations;
        console.log(typeof recommendations);
        if (recommendations.length === 0 && typeof recommendations === "string" && Array.isArray(recommendations)) {
          recommendations.push("You have met all required daily nutrient needs.")
        }
        return recommendations;
        // let state = area_api_json.results[0].state_name;
        // let county = area_api_json.results[0].county_name;
        // const split_state = state.split(" ");
        // return recommendations; // maybe represented as a table?
      } catch (err) {
        return "Unable to retrive nutrition data";
      }
}

interface SuccessfulRecommendationCall {
  result: string;
  recommendations: string;
}

/**
 * Checks if the return of the fetch call has the recommended foods
 * @param rjson the json from the fetch call
 * @returns true if the json is of the valid format
 */
function isSuccessfulRecommendationCall(
  rjson: any
): rjson is SuccessfulRecommendationCall {
  if (!("result" in rjson)) return false;
  if (!("recommendations" in rjson)) return false;
  return true;
}
