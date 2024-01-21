import jsPDF from "jspdf";

// PDF generated that was not implemented before demo
const generatePDF = (content: string[][]) => {
  const doc = new jsPDF();

  // Add text to PDF.
  let currentYPosition = 10; 

  content.forEach((lineGroup) => {
    lineGroup.forEach((line) => {
      doc.text(line, 10, currentYPosition);
      currentYPosition += 10; 
    });

    currentYPosition += 10; // Add extra space between groups
  });

  doc.save("download.pdf");
};

export default generatePDF;
