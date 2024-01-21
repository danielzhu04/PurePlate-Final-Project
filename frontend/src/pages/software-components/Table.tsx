import React from 'react';
import "/src/styles/SearchHistory.css";

/**
 * A prop that has a 2D array of strings
 */
interface Data {
    data: string[]
}

/**
 * Converts an array of strings into a table component
 * 
 * @param - prop that contains an array of strings
 */
export function RecommendationToTable(props: Data) {
  console.log("these are the props");
  console.log(props);
  console.log(props.data);
  return (
    <table className="csvTable">
      <tbody>
        {props.data.map((food, rowIndex) => (
          <tr key={rowIndex} className = "tableRow">
              <td className = "tableColumn">{food}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}