Elevator System Control Simulator Iteration #1
Team members: Ryan Fife, Paul Okenne, Jacob Charpentier, Favour Olotu, Delight Oluwayemi

System Overview:
The purpose of this program is to simulate a real-time server-client communication. In this 
configuration the scheduler serves the floor and elevator clients. Each component is simulated 
using its own thread.

Primary Classes:

ElevatorCar - This class manages sending and receiving of job requests from the scheduler.

Floor - This class is controlled by the FloorSubsystem. It handles job requests and holds the properties 
of the floor ie. floor number, floor buttons and floor lamps. 

FloorSubsystem- This class manages sending and receiving of job requests from and to the Scheduler.

Scheduler - This class manages the communication between the Elevator and the Floor sub systems.

System Flow:
Floor Subsystem determines if there is a valid input data (user presses a button)
Using the Floor Transmission Channel the job request is sent to the Scheduler
Scheduler sends a job request to the ElevatorCar using the elevator transmission channel
ElevatorCar receives the job request and responds to the scheduler
Scheduler updates the floor subsystem

HOW TO RUN THE PROGRAM
Running any Java application begins with executing the static main(String[] args) function.
This application is no different.

	INSTRUCTIONS FOR ECLIPSE USERS
	1.	Import the project to your workspace. On a MacBook, you navigate as shown: 
		i.	File -> Import -> General -> Existing Projects into Workspace
	2.	Follow the import wizard instructions and import the project to your workspace
	3.	Select the SystemExecutor class and run the program by clicking the play button on the top menu.


--------------------------------------------------------

Assigned subsystem for this iteration

Scheduler: Ryan + Favour
Floor: Paul
Elevator: Jake + Delight

Each team member is expected to contribute to the UML diagrams, implementation code, and testing for their assigned subsystem this week.

--------------------------------------------------------
