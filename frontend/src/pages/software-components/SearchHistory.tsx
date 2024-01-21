import React from "react";
import { RecommendationToTable } from "./Table";

interface SearchHistoryProps {
  historyData: (string | string[])[]; // Assuming historyData is an array of strings
}

/**
 * Displays the search history for all previous submissions. The Search history is scrollable and 
 * automatically scrolls to the bottom of the scroll box
 */
class SearchHistory extends React.Component<SearchHistoryProps> {
  historyRef: React.RefObject<HTMLDivElement>;
  constructor(props: SearchHistoryProps) {
    super(props);
    this.historyRef = React.createRef();
  }
  componentDidUpdate() {
    const element = this.historyRef.current;
    if (element) {
      element.scrollTop = element.scrollHeight;
    }
  }
  render() {
    return (
      <div className="searchHistory">
        Search History
        <div className="scroll-box" ref={this.historyRef}>
          <p>{getResult(this.props.historyData)}</p>
        </div>
      </div>
    );
  }
}

/**
 * Displays the recommended list of foods in an HTML table format
 * @param historyData All previous queries to get a recommended list of foods
 * @returns HTML that displays a properly formatted list of foods
 */
function getResult(historyData: (string | string[])[]) {
  return historyData.map((data, index) => {
    if (typeof data === "string") {
      // For string, return as paragraph
      return (
        <div key={index} className="history-entry">
          <p key={index}>{data}</p>
        </div>
      );
    } else if (Array.isArray(data)) {
      // For string array, use RecommendationToTable
      return (
        <div key={index} className="history-entry">
          <RecommendationToTable key={index} data={data} />
        </div>
      );
    } else {
      return <p></p>;
    }
  });
}
export default SearchHistory;
