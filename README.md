# Term Project: PurePlate
Designed and Built by Daniel Zhu, Kyle Yeh, Grace Chen, and Wilson Vo for CSCI0320: Introduction to Software Engineering at Brown University Fall of 2023.

This project aims to build a website and software for elderly care-takers and everyday citizens to inform themselves and others about nutrient deficincies 

## Frontend
### Design Choices
In our frontend we decided to have a few different design choices these inclcude
* A clean, and minimal pallete such that there is no issues for those who have reading disabillities. Orginally we have a font called Megrim which was much more difficult to read.
* A homepage with pages which showcase our complexity and UI design using React's routing DOM.
* Defensive error handling for when user do not input values in the required parameters for the software.
* Defensive error handling for when the user inputs invalid values which are letter values for the textboxes when it should be a double or an integer. 
* A Meet the Team page in order to have the user/clients submit any possible issues that may arise or suggestions that they have to us individually or as a team.
* Accessible tabbing for easy access for those who do not have access to a keyboard.
### Errors and Bugs
There are no current errors or bugs as of December 20th, 2023.  
## Backend
* On our backend api server, we have two main endpoints (food data and pure plate). 
* The food data handler is used
by our frontend to retrieve a list of valid foods to select from. The pureplate handler is what actually calculates the recommended list of foods. 
* We use the proxy and strategy pattern and delegate our USDA food API calling through the Nutrition Data Source. 
### Errors and Bugs
There are no current errors or bugs as of December 20th, 2023.  
## Testing
Here are the following tests which we have decided to include in our program:
* Testing if all the components are visible when the application loads
* Testing when certain values are inputted/selected we get the following results. 
* Testing when values are invalid, such as negative or non-integer/non-double values
* Testing that informative errors (window alerts) pop up.
* Using mocks to test invalid 
* Testing if the caloric requirement if that matches up with the Mifflin St Jeor equation.
* Testing parsing the food base
* Many more tests which cannot be outlined all on this README.

## How to run the program
In order to run the program copy the GitHub Repository to your desired integrate development environment. Navigate to the following:

 Backend -> src -> main -> PurePlateServer

Click the green arrow on the topheader to run the server. Then navigate to the Frontend in your terminal by running "cd Frontend". From here you will run "npm install" and let that install. Check if there is a filed named node_modules in your files. Now, navigate to the src folder by running in your terminal "cd src". Then run "npm install" in your terminal. This should start the server which begins as "localhost://0000". Click on this link are you are now on the local webpage. 

Thank you for looking at our repository and checking our our project!