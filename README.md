[![build status](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/badges/master/build.svg)](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/commits/master) [![coverage report](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/badges/master/coverage.svg)](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/commits/master)

# Logistics Optimizer
This logistics optimizer provides multiple algorithms to solve a giben transportation problem. In detail those are:

* NortWestCornerKruskalOptimizer using the NorthWestCorner method and the Kruskal algorithm to fill the table with the ways to the leaf of the minimal spanning tree.
* NorthWestCornerOwnOptimizer uses the NorthWestCorner-Method and an own implementation relative to the Kruskal algorithm which tries to further optimize a problem by allowing circles in the transportation network if it increases the solution.
* SavingsOptimizer uses the Savings Algorithm.
* PheromoneOptimizer which sets a pheromone scent to each location. The intensity of the scent depends on the remaining locations delivery amount needed. As much as the truck gets more empty the pheromone scent of the start location or the home location gets higher. This ensures the trucks get home and do not run in circles infinitely. 

## Installation guide
```bash
git clone git@gitlab.uni-oldenburg.de:transportsysteme-semesteraufgabe/Optimizer.git
cd Optimizer
mvn clean install
```
In case of any errors follow the stacktrace or check your maven or java installation.

## Usage guide
To start the application use the following command pattern from within the project root dir.
```bash
java -jar target/optimizer-1.0.jar [algorithmID] [logisticNetworkPath] [deliveryListPath]
```
where the options can be left blank. Then all algorithms are executed and the default [LogisticNetworkFile](src/main/resources/Logistiknetz.csv) and [DeliveryListFile](src/main/resources/Lieferliste.csv) are used.

The algorithm IDs are:

1. PheromoneOptimizer
2. NorthWestCornerKruskalOptimizer
3. NorthWestCornerOwnOptimizer
4. SavingsOptimizer
5. ShortestPathOptimizer (Dijkstra)
6. SolutionOptimizer
7. Default (all but algorithm 3 and 6) 

Algorithm 3 and 6 are in test mode only.

### Extended Logistics Optimizer

This command starts a more complex transportation problem:

```bash
java -jar target/optimizer-1.0.jar 7 src/main/resources/LogistiknetzMoreComplex.csv src/main/resources/LieferlisteMoreComplex.csv
```

## UML Diagrams
![](UMLDiagram.png)
![](AuxiliaryClasses.png)

