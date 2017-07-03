package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.*;
import de.uni_oldenburg.transport.trucks.LargeTruck;
import de.uni_oldenburg.transport.trucks.MediumTruck;
import de.uni_oldenburg.transport.trucks.SmallTruck;

import java.util.*;


/**
 * Optimizer with Savings Algorithm
 */
public class SavingsOptimizer implements Optimizer {

    Location startLocation;
    ArrayList<ArrayList<Location>> routeList = new ArrayList<>();
    @Override
    public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
        startLocation = transportNetwork.getStartLocation();
        Solution solution = new Solution(transportNetwork);

        HashMap<Location, Integer> shortestWays = depotDistance(transportNetwork);
        LinkedHashMap<String, Integer> savings = getSavings(shortestWays, transportNetwork);

        ArrayList<ArrayList<Location>> getRoutes = getRoutes(savings, transportNetwork);
        ArrayList<Tour> tours = null;
        ArrayList<ArrayList<Tour>> allTours = new ArrayList<>();


        for (int i = 0; i < getRoutes.size(); i++) {
            ArrayList<Location> tourLocations = getRoutes.get(i);
            tours = new ArrayList<>();
            int totalAmount = 0;
            for (Location tourDestination : tourLocations) {
                totalAmount += tourDestination.getAmount();
            }
            int totalsAmountCopy = totalAmount;
            while (totalsAmountCopy > 0) {
                if (totalsAmountCopy > MediumTruck.CAPACITY && totalsAmountCopy <= LargeTruck.CAPACITY) {
                    tours.add(new Tour(new LargeTruck(), startLocation));
                    totalsAmountCopy -= LargeTruck.CAPACITY;
                } else if (totalsAmountCopy > SmallTruck.CAPACITY && totalsAmountCopy <= MediumTruck.CAPACITY) {
                    tours.add(new Tour(new MediumTruck(), startLocation));
                    totalsAmountCopy -= MediumTruck.CAPACITY;
                } else if (totalsAmountCopy <= SmallTruck.CAPACITY) {
                    tours.add(new Tour(new SmallTruck(), startLocation));
                    totalsAmountCopy -= SmallTruck.CAPACITY;
                } else {

                    do {
                        tours.add(new Tour(new LargeTruck(), startLocation));
                        totalsAmountCopy -= LargeTruck.CAPACITY;
                    } while (totalsAmountCopy > MediumTruck.CAPACITY);
                }
            }

            for (int j = 0; j < tours.size(); j++) {
                int amountPossible = tours.get(j).getTruck().getCapacity();
                Location startTourLocation = startLocation;
                Location emptyTruckLocation = new Location("");
                Tour tour = tours.get(j);
                int expense = 0;
                int unL = 0;

                for (int k = 0; k < tourLocations.size(); k++) {
                    if (amountPossible > 0) {
                        Location actLocation = tourLocations.get(k);
                        int unload = 0;
                        int unloadAmount = 0;
                        expense = getExpense(shortestWays, startTourLocation, actLocation);
                        TourDestination tourDestination = new TourDestination(actLocation, tourLocations.get(k).getAmount());
                        unload = tourDestination.getUnload();
                        if (unload <= amountPossible) {
                            tourDestination.setUnload(actLocation.getAmount());
                            tourLocations.get(k).setAmount(0);
                            unloadAmount = unload;
                        } else {
                            int restAmount = tourLocations.get(k).getAmount() - amountPossible;
                            unloadAmount = amountPossible;
                            tourDestination.setUnload(unloadAmount);
                            tourLocations.get(k).setAmount(restAmount);
                        }
                        tour.addDestination(tourDestination);
                        amountPossible -= unload;
                        startTourLocation = actLocation;
                        emptyTruckLocation = actLocation;
                    } else if (k < tourLocations.size()) {

                        LinkedHashMap<Location, Integer> route = transportNetwork.getShortestPath(emptyTruckLocation, startLocation);

                        for (Map.Entry<Location, Integer> entry : route.entrySet()) {
                            TourDestination destNoLoad = new TourDestination(entry.getKey(), unL);
                            if (!entry.getKey().equals(emptyTruckLocation))
                                tour.addDestination(destNoLoad);
                        }
                        break;

                    }
                }
            }

            allTours.add(tours);
        }
        for (int i = 0; i < allTours.size(); i++) {
            for (int j = 0; j < allTours.get(i).size(); j++) {
                if (allTours.get(i).get(j).isValid())
                    solution.addTour(allTours.get(i).get(j));
            }
        }

        return solution;

    }

    /**
     * Gets the savings for the distances between the Locations and the Depot
     *
     * @param depotDistance    HashMap with the distances to the depot
     * @param transportNetwork
     * @return
     */
    private LinkedHashMap<String, Integer> getSavings(HashMap<Location, Integer> depotDistance, TransportNetwork transportNetwork) {
        ArrayList<Location> visitedLocations = new ArrayList<>();
        Location actLocation = startLocation;
        LinkedHashMap<String, Integer> savings = new LinkedHashMap<>();

        while (transportNetwork.getNumberOfLocations() > visitedLocations.size()) {
            int distance = 0;
            Location location;
            visitedLocations.add(startLocation);
            String names = "";
            int d1 = 0;
            int d2 = 0;
            int save = 0;
            for (int i = 0; i < visitedLocations.size(); i++) {
                if (!actLocation.getNeighbouringLocations().isEmpty()) {
                    for (Map.Entry<Location, Integer> entry : actLocation.getNeighbouringLocations().entrySet()) {
                        distance = entry.getValue();
                        location = entry.getKey();

                        for (Map.Entry<Location, Integer> dis : depotDistance.entrySet()) {
                            Location locationDis = dis.getKey();

                            if (actLocation.toString().equals(locationDis.toString())) {
                                d1 = dis.getValue();
                                names += locationDis.getName();
                            }
                            if (location.toString().equals(locationDis.toString())) {
                                d2 = dis.getValue();
                                names += location.getName();
                            }
                            save = d1 + d2 - distance;
                        }

                        if (!visitedLocations.contains(location)) {
                            visitedLocations.add(location);
                            distance = 0;
                        }

                        if (!names.contains("Hamburg")) {
                            savings.put(names, save);
                        }
                        names = "";
                    }
                }
                actLocation = visitedLocations.get(i);
            }
        }
        return savings;
    }

    /**
     * Computes the routes with multiple destinations.
     *
     * @param savings          HashMap with each saving for two locations
     * @param transportNetwork
     * @return
     */
    private ArrayList<ArrayList<Location>> getRoutes(LinkedHashMap<String, Integer> savings, TransportNetwork transportNetwork) {

        ArrayList<ArrayList<Location>> routeList = new ArrayList<>();
        ArrayList<Location> routeLocation = new ArrayList<>();
        ArrayList<Location> visitedLocations = new ArrayList<>();
        Location[] locations = transportNetwork.getLocations();
        LinkedHashMap<String, Integer> sorted = savings;
        Location actLocation = startLocation;
        routeLocation.add(startLocation);
        visitedLocations.add(startLocation);
        Object[] o = sorted.entrySet().toArray();



        ArrayList<ArrayList<Location>> routesPart1 = computeRoutes(actLocation, transportNetwork, savings, visitedLocations);
        for (ArrayList<Location> routing1 : routesPart1) {
            //routeList.add(routing1);
            for(Location visited : routing1) {
                if(!visitedLocations.contains(visited)) {
                    visitedLocations.add(visited);
                }
            }
        }

        for (Location visitedL : locations) {
            if (!visitedLocations.contains(visitedL)) {
                actLocation = visitedL;
                break;
            }
        }
        ArrayList<ArrayList<Location>> routesPart2 = computeRoutes(actLocation, transportNetwork, savings, visitedLocations);
        for (ArrayList<Location> routing2 : routesPart2) {
            if(!routeList.contains(routing2))
            routeList.add(routing2);
        }


        System.out.println("Anzahl Touren: " + routeList.size());
        for (int j = 0; j < routeList.size(); j++) {
            ArrayList<Location> route1 = routeList.get(j);
            System.out.print("Route mit " + route1.size() + " destinations: ");
            for (int i = 0; i < route1.size(); i++) {
                System.out.print(route1.get(i).getName() + " ");
            }
            System.out.println();
        }
        System.out.println();

        return routeList;
    }

    /**
     *Computes the Routes between the Depot an the Locations.
     * @param actLocation
     * @param transportNetwork
     * @param savings
     * @param visitedLocation
     * @return
     */

    private ArrayList<ArrayList<Location>> computeRoutes(Location actLocation, TransportNetwork transportNetwork, LinkedHashMap<String, Integer> savings, ArrayList<Location> visitedLocation) {

        ArrayList<Location> routeLocation = new ArrayList<>();
        ArrayList<Location> visitedLocations = visitedLocation;
        ArrayList<Location> locations = new ArrayList<>();
        ArrayList<Object> sortedSav = new ArrayList<>();
        boolean[] savingsCheck = new boolean[savings.size()];
        LinkedHashMap<String, Integer> sorted = savings;
        Location actLocation2 = actLocation;
        routeLocation.add(startLocation);
        visitedLocations.add(startLocation);
        Object[] o = sorted.entrySet().toArray();

        Arrays.sort(o, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });

        System.out.println("Savings: ");
        for (Object e : o) {
            sortedSav.add(e);
            sorted.put(((Map.Entry<String, Integer>) e).getKey(), ((Map.Entry<String, Integer>) e).getValue());
            System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
                    + ((Map.Entry<String, Integer>) e).getValue());
        }

        System.out.println();

        for (int i = 0; i < transportNetwork.getLocations().length; i++) {
            locations.add(transportNetwork.getLocations()[i]);
        }
        int k = 0;
        int checkCounter = 0;
        transportNetwork.computeShortestPaths();
        while (k < 1) {

            for (Object e : o) {

                ArrayList<Location> savingsLocations = new ArrayList<>();
                ArrayList<Location> singleRoute = new ArrayList<>();
                ArrayList<Location> firstRoute = new ArrayList<>();
                Location destination = new Location("");
                boolean startRouting = false;


                if (!actLocation.getNeighbouringLocations().isEmpty()) {
                    for (Map.Entry<Location, Integer> entry : actLocation2.getNeighbouringLocations().entrySet()) {
                        destination = entry.getKey();
                        if (((Map.Entry<String, Integer>) e).getKey().contains(destination.getName())) {
                            startRouting = true;

                        }
                    }
                }

                if (startRouting) {
                    for (Location loc : locations) {
                        if (((Map.Entry<String, Integer>) e).getKey().contains(loc.getName())) {
                            if (!savingsLocations.contains(loc)) {
                                savingsLocations.add(loc);
                            }
                        }
                    }

                    if (routeList.size() == 0) {
                        LinkedHashMap<Location, Integer> route = transportNetwork.getShortestPath(startLocation, savingsLocations.get(0));

                        for (Map.Entry<Location, Integer> entry : route.entrySet()) {
                            if (!entry.getKey().equals(startLocation))
                                firstRoute.add(entry.getKey());
                            if (!visitedLocations.contains(entry.getKey())) {
                                visitedLocations.add(entry.getKey());
                                actLocation = entry.getKey();
                            }
                        }

                        LinkedHashMap<Location, Integer> route2 = transportNetwork.getShortestPath(savingsLocations.get(1), startLocation);

                        for (Map.Entry<Location, Integer> entry : route2.entrySet()) {
                            firstRoute.add(entry.getKey());
                            if (!visitedLocations.contains(entry.getKey())) {
                                visitedLocations.add(entry.getKey());
                                actLocation = entry.getKey();
                            }
                        }
                        routeList.add(firstRoute);
                        savingsCheck[checkCounter] = true;

                        k++;

                    } else {
                        boolean checkSaving = true;
                        for (ArrayList<Location> list : routeList) {

                            if (list.contains(savingsLocations.get(0)) && list.contains(savingsLocations.get(1))) {
                                checkSaving = false;

                            } else if (list.contains(savingsLocations.get(1))) {
                                for (ArrayList<Location> list2 : routeList) {
                                    if (list2.contains(savingsLocations.get(0))) {
                                        checkSaving = false;
                                    }
                                }
                            } else if (list.contains(savingsLocations.get(0))) {
                                for (ArrayList<Location> list2 : routeList) {
                                    if (list2.contains(savingsLocations.get(1))) {
                                        checkSaving = false;
                                    }
                                }
                            } else {
                                checkSaving = true;
                            }
                        }

                        Iterator<ArrayList<Location>> iter = routeList.iterator();

                        while (iter.hasNext()) {
                            ArrayList<Location> list = iter.next();
                            if (list.contains(savingsLocations.get(0)) && list.contains(savingsLocations.get(1))) {
                                break;
                            } else if (checkSaving == true) {

                                if (list.contains(savingsLocations.get(1))) {
                                    for (ArrayList<Location> list2 : routeList) {
                                        if (list2.contains(savingsLocations.get(0))) {
                                            break;
                                        }
                                    }
                                    LinkedHashMap<Location, Integer> route = transportNetwork.getShortestPath(startLocation, savingsLocations.get(0));
                                    ArrayList<Location> routePart = new ArrayList<>();
                                    if (!visitedLocations.contains(savingsLocations.get(0))) {
                                        for (Map.Entry<Location, Integer> entry : route.entrySet()) {
                                            if (!entry.getKey().equals(startLocation))
                                                singleRoute.add(entry.getKey());
                                            routePart.add(entry.getKey());
                                            actLocation = entry.getKey();
                                            if (!visitedLocations.contains(entry.getKey())) {
                                                visitedLocations.add(entry.getKey());
                                            }
                                        }

                                        int count = singleRoute.size();

                                        while (count < list.size() + 1) {
                                            singleRoute.add(list.get(count - 1));
                                            if (!visitedLocations.contains(list.get(count - 1))) {
                                                visitedLocations.add(list.get(count - 1));
                                            }
                                            count++;
                                        }
                                        for (Location destination2 : list) {
                                            if (!singleRoute.contains(destination2)) {
                                                singleRoute.add(destination2);
                                            }
                                        }
                                        savingsCheck[checkCounter] = true;
                                        routeList.remove(list);
                                        break;
                                    }
                                } else if (list.contains(savingsLocations.get(0))) {
                                    for (ArrayList<Location> list2 : routeList) {
                                        if (list2.contains(savingsLocations.get(1))) {
                                            break;
                                        }
                                    }
                                    LinkedHashMap<Location, Integer> route2 = transportNetwork.getShortestPath(startLocation, savingsLocations.get(1));
                                    ArrayList<Location> routePart = new ArrayList<>();
                                    if (!visitedLocations.contains(savingsLocations.get(1))) {
                                        for (Map.Entry<Location, Integer> entry : route2.entrySet()) {
                                            routePart.add(entry.getKey());
                                            actLocation = entry.getKey();
                                            if (!visitedLocations.contains(entry.getKey())) {
                                                visitedLocations.add(entry.getKey());
                                            }
                                        }
                                        int count = 0;
                                        for (int i = 0; i < routePart.size(); i++) {
                                            if (!routePart.get(i).equals(startLocation)) {
                                                if (routePart.get(i).equals(list.get(i))) {
                                                    singleRoute.add(list.get(i));
                                                    if (!visitedLocations.contains(list.get(i))) {
                                                        visitedLocations.add(list.get(i));
                                                    }
                                                    count++;
                                                } else {
                                                    singleRoute.add(routePart.get(i));
                                                    count++;
                                                }
                                            }
                                        }
                                        while (count < list.size() + 1) {
                                            singleRoute.add(list.get(count - 1));
                                            count++;
                                        }
                                        routeList.remove(list);
                                        savingsCheck[checkCounter] = true;
                                        break;
                                    }
                                } else {
                                    LinkedHashMap<Location, Integer> route = transportNetwork.getShortestPath(startLocation, savingsLocations.get(0));
                                    if (!visitedLocations.contains(savingsLocations.get(0))) {
                                        for (Map.Entry<Location, Integer> entry : route.entrySet()) {
                                            if (!entry.getKey().equals(startLocation)) {
                                                if (!singleRoute.contains(entry.getKey())) {
                                                    singleRoute.add(entry.getKey());
                                                    actLocation = entry.getKey();
                                                }
                                            }
                                            if (!visitedLocations.contains(entry.getKey())) {
                                                visitedLocations.add(entry.getKey());
                                            }
                                        }

                                        LinkedHashMap<Location, Integer> route2 = transportNetwork.getShortestPath(savingsLocations.get(1), startLocation);

                                        for (Map.Entry<Location, Integer> entry : route2.entrySet()) {
                                            if (!entry.getKey().equals(visitedLocations.get(visitedLocations.size() - 1))) {
                                                singleRoute.add(entry.getKey());
                                                actLocation = entry.getKey();
                                            }
                                            if (!visitedLocations.contains(entry.getKey())) {
                                                visitedLocations.add(entry.getKey());
                                            }
                                        }
                                        savingsCheck[checkCounter] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }


                boolean check = false;

                for (int g = 0; g < singleRoute.size(); g++) {
                    for (Map.Entry<Location, Integer> neighbour : singleRoute.get(g).getNeighbouringLocations().entrySet()) {
                        if (g + 1 < singleRoute.size())
                            if (neighbour.getKey().equals(singleRoute.get(g + 1))) {
                                check = true;
                            }
                    }

                }


                if (singleRoute.size() != 0 && check == true) {
                    routeList.add(singleRoute);
                }
                k++;
                checkCounter++;
            }
        }
        return routeList;
    }


    /**
     * Finds out the shortest way from the startLocation to each Location.
     *
     * @param network
     * @return
     */
    private HashMap depotDistance(TransportNetwork network) {
        HashMap<Location, Integer> depotDistanceHashMap = new HashMap<>();
        ArrayList<Location> visitedLocations = new ArrayList<>();
        Location actLocation = startLocation;
        depotDistanceHashMap.put(startLocation, 0);

        while (network.getNumberOfLocations() > visitedLocations.size()) {
            int distance = 0;
            Location location;
            visitedLocations.add(startLocation);
            network.computeShortestPaths();
            for (int i = 0; i < visitedLocations.size(); i++) {
                if (!actLocation.getNeighbouringLocations().isEmpty()) {
                    for (Map.Entry<Location, Integer> entry : actLocation.getNeighbouringLocations().entrySet()) {
                        location = entry.getKey();
                        LinkedHashMap<Location, Integer> dista = network.getShortestPath(startLocation, location);
                        for (Map.Entry<Location, Integer> d : dista.entrySet()) {
                            int dist = d.getValue();
                            distance += dist;
                        }

                        if (!depotDistanceHashMap.containsKey(location))
                            depotDistanceHashMap.put(location, distance);
                        if (!visitedLocations.contains(location)) {
                            visitedLocations.add(location);

                        }
                        distance = 0;
                    }
                }
                actLocation = visitedLocations.get(i);
            }
        }
        System.out.println("Entfernungen zum Depot: ");
        for (Map.Entry<Location, Integer> entry : depotDistanceHashMap.entrySet()) {
            System.out.println(entry.getKey().getName() + " ist " + entry.getValue() + " km von Hamburg entfernt.");
        }
        System.out.println();
        return depotDistanceHashMap;
    }

    /**
     * @param shortestWays
     * @param location
     * @return
     */
    private int getExpense(HashMap<Location, Integer> shortestWays, Location startLocation, Location location) {
        int expense = 0;

        if (startLocation.equals(this.startLocation)) {
            for (Map.Entry<Location, Integer> entry : shortestWays.entrySet()) {
                if (entry.getKey().equals(location)) {
                    expense = entry.getValue();
                }
            }
        } else if (location.equals(this.startLocation)) {
            for (Map.Entry<Location, Integer> entry : shortestWays.entrySet()) {
                if (entry.getKey().equals(startLocation)) {
                    expense = entry.getValue();
                }
            }
        } else {
            for (Map.Entry<Location, Integer> neighbour : startLocation.getNeighbouringLocations().entrySet()) {
                if (neighbour.getKey().equals(location)) {
                    expense = neighbour.getValue();
                }
            }
        }
        return expense;
    }


}

