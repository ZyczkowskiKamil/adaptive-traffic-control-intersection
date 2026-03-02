# Adaptive Traffic Control Intersection

An intelligent traffic light simulation system for a 4-way intersection that dynamically adapts to traffic conditions. The system optimizes traffic flow by analyzing vehicle queues and adjusting light phases accordingly.

## 📋 Table of Contents

- [Features](#features)
- [Algorithm Description](#algorithm-description)
- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Simulation](#running-the-simulation)
- [Input/Output Format](#inputoutput-format)
- [Testing](#testing)
- [Class Description](#class-description)
- [Technical Highlights](#technical-highlights)
- [TODO](#todo)

## Features

### Core Requirements
- **4-Way Intersection**: Realistic representation with roads from North, South, East, and West
- **Adaptive Traffic Lights**: Dynamic light cycles based on real-time traffic conditions
- **Multiple Light Phases**: Standard cycles (green, yellow, red) plus specialized phases for turns
- **Safety First**: Prevents conflicting green lights across directions
- **Vehicle Tracking**: Monitors queued vehicles and tracks those leaving the intersection
- **JSON I/O**: Command-based simulation via JSON input/output

### Advanced Features
- **Multi-Lane Roads**: Support for multiple lanes per road with different turn specializations
  - Dedicated left-turn lanes
  - Dedicated right-turn lanes
  - Straight lanes
  - Combined lanes (left-straight, straight-right, all-directional)
- **Intelligent Lane Assignment**: Vehicles automatically select the optimal lane based on:
  - Destination compatibility
  - Current lane occupancy
  - Lane specialization priority
- **GUI Mode**: Interactive JavaFX interface for visual simulation monitoring
- **Safe Transitions**: Realistic light transition sequences:
  - Green → Yellow (3 time units)
  - Yellow → All Red (2 time units - clearance interval)
  - All Red → Green (3 time units)
- **Flexible Light Phases**: 9 different phase configurations including:
  - North-South straight/right
  - East-West straight/right
  - Protected left turns with opposing right turns
  - Single direction with all movements
  - All red (beginning of simulation)
- **Testing**: Unit tests

## Algorithm Description

### Traffic Light Adaptation Strategy

The system uses a **lane phase importance formula** that evaluates all possible light phases and selects one that would allow the most vehicles to proceed:

1. **Phase Evaluation**: At each simulation step during an active phase:
   - Calculate each phase importance based on formula:
       - `(a * W1) + (b * W2) + W3`
       - `a` - how many cars can go in light phase
       - `b` - time passed from when phase was last active
       - `W1` - weight of number of cars
       - `W2` - weight of phase wait time
       - `W3` - weight of stability - prevents switch when just one car comes

2. **Phase Selection**: 
   - `MIN_ACTIVE_PHASE_TIME` - prevents rapid switching
   - `MAX_ACTIVE_PHASE_TIME` - ensures other phases can be choosen when one has lots of cars
   - If not enought time passed keep current phase
   - If too much time passed - ensures that other phase is choosen
   - Selects phase with highest importance value

3. **Safe Transitions**: When changing phases:
   - **Transition to Red** (3 time units): Yellow lights warn vehicles to stop
   - **All Red Phase** (2 time units): Clearance interval ensures intersection is empty
   - **Transition to Green** (3 time units): New direction's green lights activate
   - **Active Phase**: Vehicles proceed through intersection (1 time unit per vehicle)

4. **Phase Timing Constraints**:
   - Minimum active phase: 3 time units (prevents switching too fast)
   - Maximum active phase: 10 time units (ensures fairness)
   - Transition sequence: 8 time units total (3 + 2 + 3)

### Lane Management

- **Dynamic Lane Creation**: Roads start with one multi-directional lane; specialized lanes can be added dynamically
- **Smart Vehicle Placement**: When a vehicle arrives:
  - Filter lanes that allow the vehicle's destination
  - Select the lane with the fewest vehicles
  - Prioritize more specialized lanes (e.g., LEFT lane over LEFT_STRAIGHT for left turns)
- **Conflict Prevention**: Lane types enforce valid turn movements based on cardinal directions, each light phase has not colliding ways

### Light Phases

The system supports 9 distinct phases to handle various traffic patterns:
- `NS_STRAIGHT_RIGHT`: North & South can go straight or right
- `EW_STRAIGHT_RIGHT`: East & West can go straight or right
- `NS_LEFT_EW_RIGHT`: North & South left turns, East & West right turns
- `EW_LEFT_NS_RIGHT`: East & West left turns, North & South right turns
- `NORTH_ALL_EAST_RIGHT`: North all directions, East right turn only
- `EAST_ALL_SOUTH_RIGHT`: East all directions, South right turn only
- `SOUTH_ALL_WEST_RIGHT`: South all directions, West right turn only
- `WEST_ALL_NORTH_RIGHT`: West all directions, North right turn only
- `ALL_DIRECTIONS_STOP`: Beginning/Emergency/transition phase - all red

## Prerequisites

- **Java**: Version 25 or higher
- **Maven**: For dependency management and building
- **JavaFX**: Included as dependency (for GUI mode)

## Building the Project

```bash
cd traffic-lights-simulation
mvn clean package
```

This creates an executable JAR in the `target/` directory.

## Running the Simulation

### Console Mode (JSON Input/Output)

```bash
java --enable-native-access=ALL-UNNAMED -jar target/traffic-lights-simulation-1.0-SNAPSHOT.jar input.json output.json
```

**Parameters:**
- `input.json`: Path to the input file containing simulation commands
- `output.json`: Path where simulation results will be saved

### GUI Mode

```bash
java --enable-native-access=ALL-UNNAMED -jar target/traffic-lights-simulation-1.0-SNAPSHOT.jar --gui
```

This launches an interactive JavaFX window where you can:
- Visualize the intersection
- Use simulation creator
- Import and export simulation file
- Control simulation execution
- Export simulation output

## Input/Output Format

### Input Format

```json
{
  "commands": [
    {
      "type": "addVehicle",
      "vehicleId": "vehicle1",
      "startRoad": "south",
      "endRoad": "north"
    },
    {
      "type": "step"
    }
  ]
}
```

**Command Types:**
- `addVehicle`: Adds a vehicle to the intersection
  - `vehicleId`: Unique identifier for the vehicle
  - `startRoad`: Origin road (`north`, `south`, `east`, or `west`)
  - `endRoad`: Destination road (determines turn direction)
- `step`: Executes one simulation step
  - Advances time until vehicles can proceed
  - Processes traffic light transitions
  - Moves vehicles that have green lights

### Output Format

```json
{
  "stepStatuses": [
    {
      "leftVehicles": ["vehicle1", "vehicle2"]
    },
    {
      "leftVehicles": []
    }
  ]
}
```

**Output Structure:**
- `stepStatuses`: Array with one entry per `step` command
- `leftVehicles`: List of vehicle IDs that exited the intersection during that step

## Testing

Run the test suite:

```bash
mvn test
```


## Class description


The `Main` class - launches the program in CLI or GUI mode depending on selected option
- package `controller`:
    - class `CommandListCell` - row object in command view in GUI
    - class `SimulationController` - logic for GUI in JavaFx
- package `gui`:
    - class `SimulationGui`
- package `model`:
    - package `dto` - data transfer objects for simulation input and output
        - class record `Command` - representation of command from input
        - class enum `CommandType` - available command types enum 
        - class record `SimulationInput` - representation of list of commands from input
        - class record `SimulationOutput` - representation of list of step statuses
        - class record `StepStatus` - list of vehicleId from vehicles that left intersection during step
    - package `infrastructure`
        - class enum `Direction` - represents cardinal direction(e.g. north, east)
        - class `Intersection` - intersection model - it has 4 roads(in each direction), singleton
        - class `Lane` - represents one lane on road
        - class enum `LaneType` - where vehicles can turn from lane(e.g. left, straight-right)
        - class `Road` - represents one of four roads on intersection, has traffic lights
        - class enum `TurnDirection` - where vehicle wants to turn
    - package `trafficlight`
        - class enum `LightPhase` - enumerator for phase of lights (e.g. North and South straight and right)
        - class record `LightSet` - set of lights (left turn, straight, right turn) (e.g. Red, Green, Green)
        - class enum `LightTransitionState` - light transition states to represent changing lights
        - class `RoadTrafficLights` - represents traffic lights for each road on intersection
        - class enum `TrafficLightColor` - represents color that traffic light can have
    - package `vehicle`
        - class `Vehicle` - represents vehicle on intersection
- package `service`:
    - class `TrafficLightsService` - simulated traffic lights - has logic - here is located algorithm
    - class `TrafficSimulator` - simulated traffic - running simulation and handling vehicles
- package `utils`:
    - class `SimulationParser` - parser for simulation input and output


## Technical Highlights

### Architecture
- **Separation of Concerns**: Distinction between model, service, and controller layers


### Technology Stack
- **Language**: Java 25
- **Build Tool**: Maven
- **GUI Framework**: JavaFX 25
- **JSON Processing**: Jackson 2.19.2
- **Testing**: JUnit Jupiter 6.0.3


## TODO

List of thing to consider:
- Better test coverage(currently around 35%)
- Better name for Direction - maybe for CardinalDirection?
- Improve GUI
- Better exception handling:
  - Loading wrong file as input
  - Adding vehicle going to it's start road(e.g. NORTH -> NORTH)