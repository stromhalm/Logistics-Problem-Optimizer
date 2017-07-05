[![build status](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/badges/master/build.svg)](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/commits/master) [![coverage report](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/badges/master/coverage.svg)](https://gitlab.uni-oldenburg.de/transportsysteme-semesteraufgabe/Optimizer/commits/master)

# Transport Systems Optimizer
The fabulous transport systems optimizer provides multiple algorithms to solve an arbitrary transportation problem. In detail those are:
* NortWestCornerKruskalOptimizer using the North-West-Corner-Method and the Kruskal algorithm to fill the table with the ways to the leaf of the Minimal-Spanning-Tree.
* NorthWestCornerOwnOptimizer uses the North-West-Corner-Method and an own implementation relative to the Kruskal algorithm which tries to further optimize a problem by allowing circles in the transportation network if it increases the solution.
* SavingsOptimizer using the Savings Algorithm.
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
where the options can be left blank. Then all algorithms are executed and the default [LogisticNetworkFile](src/main/resources/Logistiknetz.csv) and [DeliveryListFile](src/main/resources/Lieferliste.csv) is used.

The algorithm IDs are:

1. PheromoneOptimizer
2. NorthWestCornerKruskalOptimizer
3. NorthWestCornerOwnOptimizer
4. SavingsOptimizer
5. ShortestPathOptimizer (Dijkstra)
6. SolutionOptimizer
7. Default (all but algorithm 3 and 6) 

Algorithm 3 and 6 are in test mode only.

### Example usage
```bash
java -jar target/optimizer-1.0.jar 7 src/main/resources/LogistiknetzMoreComplex.csv src/main/resources/LieferlisteMoreComplex.csv
```
This command starts the more complex transportation problem with all algorithms.
## Commit conventions
Please use the [Karma commit message conventions](http://karma-runner.github.io/0.10/dev/git-commit-msg.html) in order to simplify navigating through the repository.

## Developer notes
Please write in english (commit messages also).