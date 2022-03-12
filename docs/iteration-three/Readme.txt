Elevator System Control Simulator Iteration #3
Team members: Ryan Fife, Paul Okenne, Jacob Charpentier, Favour Olotu, Delight Oluwayemi

System Overview:
The purpose of this program is to simulate a real-time server-client communication. The system is comprised of three
subsystems: a central scheduler, the floor controller, and elevator controller. Inter-system communication utilizes 
a defined message schema over UDP allowing each subsystem to be independently deployed.

Primary Classes:

ElevatorController - This class manages inter-subsystem elevator communications and manages the elevator cars. 

ElevatorCar - The elevator car entities, containing a motor and door.

FloorSubsystem- This class manages sending and receiving of job requests from and to the Scheduler.

Floor - This class is controlled by the FloorSubsystem. It handles job requests and holds the properties 
of the floor ie. floor number, floor buttons and floor lamps.

Scheduler - This class manages the communication between the Elevator and the Floor sub systems.

SubsystemCommunicationRPC - Class for managing inter subsystem communication. In this iteration we use a RDP schema
over UDP.

System Flow:

** All communication between subsystems is done over UDP 

1. Floor Subsystem determines if there is a valid input data (user presses a button)
2. input data is sent to Scheduler
3. Scheduler starts serving the request received by floor, starts moving elevator
4. Elevator moves towards requested floor, notifying scheduler of each floor it passes
5. Scheduler stops Elevator at desired floor
6. Scheduler notifies floor subsystem
6. Floor subsystem sends the elevator button press to the elevator subsystem
7. Elevator sends the request to the scheduler
8. Repeat steps 3 -> 5 

HOW TO RUN THE PROGRAM
Running any Java application begins with executing the static main(String[] args) function.
This application is no different.

	INSTRUCTIONS FOR ECLIPSE USERS
	RUN AS SINGLE PROGRAM
	1.	Import the project to your workspace. On a MacBook, you navigate as shown: 
		i.	File -> Import -> General -> Existing Projects into Workspace
	2.	Follow the import wizard instructions and import the project to your workspace
	3.	Select the SystemExecutor class and run the program by clicking the play button on the top menu.
	
	RUN AS MULTIPLE PROGRAMS
	1.	Import the project to your workspace. On a MacBook, you navigate as shown: 
		i.	File -> Import -> General -> Existing Projects into Workspace
	2.	Follow the import wizard instructions and import the project to your workspace
	3. 	Select one system that will be run on the current computer (ie: FloorSubsystem)
	4.	Traverse to the file SubsystemCommunicationConfigurations.java as shown:
		i.	SYSC3303_ElevatorControlSystem/src/common/remote_procedure/SubsystemCommunicationConfigurations.java
	5.	Look for "public static String SCHEDULER_IP_ADDRESS, FLOOR_IP_ADDRESS, ELEVATOR_IP_ADDRESS;"
	6.	At the IP_ADDRESS declarations 4-6 lines beneath this, select the two systems that aren't your chosen system
	7. 	For both of these systems, replace the line "InetAddress.getLocalHost().getHostAddress();" with the 
		IP address of the computer that is running that system.
	8. 	Repeat steps 1-7 on the other 2 computers so that all 3 subsystems are assigned to a specific computer
	9. 	On all computers navigate to their respective subsystem Main files as shown below:
		i. Floor Subsystem Computer = SYSC3303_ElevatorControlSystem/src/FloorSubsystem/FloorSubsystem.java
		ii. Scheduler Subsystem Computer = SYSC3303_ElevatorControlSystem/src/Scheduler/Scheduler.java
		iii. Elevator Subsystem Computer = SYSC3303_ElevatorControlSystem/src/ElevatorSubsystem/ElevatorController.java
	10. 	Run the program by clicking the play button on the top menu on each computer.


--------------------------------------------------------

Assigned subsystem for this iteration

Scheduler: Paul + Delight
Floor: Jake
Elevator: Ryan + Favour

Each team member is expected to contribute to the UML diagrams, implementation code, and testing for their assigned subsystem this week.

--------------------------------------------------------
