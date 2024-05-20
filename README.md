# Simulator

## My setup

* Java 21
* Gradle 8.2

## How to use

There are `setting.json` file with model parameters.

Car proportions.

```
  "Speed": -1,
  "Weight": 1500,
  "Width": 30,
  "Length": 50,
```

Car physics parameters

```
  "FrontCornerStiffness": 200,
  "RearCornerStiffness": 200,
  "Inertia": 10000,
```

Rest parameters

```
  "Iterations": 120000,
  "MinBuildingAngle": -60,
  "MaxBuildingAngle": 60,
  "SteeringInc": 1,
  "FinalStateProc": 0.10
```

To start simulator you can edit this file, even when app is launched.

You can use mouse to draw objects on the field.

To just simulate path planning, set `Speed` param to a value more than 0.  
If you want to collect lots of data about speed, path length and time to pass, set speed to negative value.