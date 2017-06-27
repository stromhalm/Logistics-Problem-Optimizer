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

    @Override
    public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
        startLocation = transportNetwork.getStartLocation();
        Solution solution = new Solution(transportNetwork);

        HashMap<Location, Integer> shortestWays = depotDistance(transportNetwork);
        HashMap<String, Integer> savings = getSavings(shortestWays, transportNetwork);

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
                System.out.println("Beladung Truck " + j + ": " + amountPossible);
            }

            for (int j = 0; j < tours.size(); j++) {
                int amountPossible = tours.get(j).getTruck().getCapacity();
                Location startTourLocation = startLocation;
                Tour tour = tours.get(j);
                int totalExpense = 0;

                for (int k = 0; k < tourLocations.size(); k++) {
                    if (amountPossible > 0) {
                        Location actLocation = tourLocations.get(k);
                        int unload = 0;
                        int unloadAmount = 0;
                        int expense = getExpense(shortestWays, startTourLocation, actLocation);

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
                        tour.addDestination(tourDestination, expense);
                        amountPossible -= unload;

                        System.out.println("LKW " + j + " drives " + expense + " kilometers from " + startTourLocation.getName() + " to " + actLocation.getName() + " and unloads " + unloadAmount + " at tour number " + j);

                        startTourLocation = actLocation;
                    }

                }

            }

            int n = 0;
            for (Tour tour2 : tours) {
                System.out.println("Tour " + n++ + " has " + tour2.getKilometersToDrive() + " kilometers to drive with an overall fuel expense of " + tour2.getConsumption() + ".");
            }
            System.out.println();
            allTours.add(tours);


        }
        for (int i = 0; i < allTours.size(); i++) {
            for (int j = 0; j < allTours.get(i).size(); j++) {
                solution.addTour(allTours.get(i).get(j));
                System.out.println("Tour " + j + " has " + allTours.get(i).get(j).getKilometersToDrive() + " kilometers to drive with an overall fuel expense of " + allTours.get(i).get(j).getConsumption() + ".");
            }
        }

        return solution;

    }

    /**
     * Gets the savings for the distances between the Locations and the Depot
     *
     * @param depotDistance HashMap with the distances to the depot
     * @param transportNetwork
     * @return
     */
    private HashMap<String, Integer> getSavings(HashMap<Location, Integer> depotDistance, TransportNetwork transportNetwork) {
        ArrayList<Location> visitedLocations = new ArrayList<>();
        Location actLocation = startLocation;
        HashMap<String, Integer> savings = new HashMap<>();

        while (transportNetwork.getNumberOfLocations() > visitedLocations.size()) {
            int distance = 0;
            Location location = null;
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
     * @param savings HashMap with each saving for two locations
     * @param transportNetwork
     * @return
     */
    private ArrayList<ArrayList<Location>> getRoutes(HashMap<String, Integer> savings, TransportNetwork transportNetwork) {

        ArrayList<ArrayList<Location>> routeList = new ArrayList<>();
        ArrayList<Location> routeLocation = new ArrayList<>();
        ArrayList<Location> visitedLocations = new ArrayList<>();
        ArrayList<Location> locations = new ArrayList<>();
        ArrayList<Object> sortedSav = new ArrayList<>();

        HashMap<String, Integer> sorted = savings;

        Location actLocation = startLocation;
        routeLocation.add(startLocation);

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
            System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
                    + ((Map.Entry<String, Integer>) e).getValue());
        }

        System.out.println();

        for (int i = 0; i < transportNetwork.getLocations().length; i++) {
            locations.add(transportNetwork.getLocations()[i]);
        }


        while (visitedLocations.size() < transportNetwork.getNumberOfLocations() - 5) {
            Location a = null;
            Location b = null;
            ArrayList<Location> routingList = new ArrayList<>();
            // routingList.add(startLocation);

            for (Object e : o) {

                for (Map.Entry<Location, Integer> neighbours : actLocation.getNeighbouringLocations().entrySet()) {
                    Location neighbour = neighbours.getKey();

                    if (((Map.Entry<String, Integer>) e).getKey().contains(neighbour.getName())) {
                        a = neighbour;
                        for (int i = 0; i < locations.size(); i++) {

                            if (locations.get(i) != a && ((Map.Entry<String, Integer>) e).getKey().contains(locations.get(i).getName())) {
                                b = locations.get(i);
                            }
                        }
                    } else if (neighbour.getNeighbouringLocations().size() <= 1 && !visitedLocations.contains(neighbour)) {

                        ArrayList<Location> singleRoute = new ArrayList<>();
                        //  singleRoute.add(startLocation);
                        singleRoute.add(neighbour);
                        singleRoute.add(startLocation);
                        routeList.add(singleRoute);
                        if (!visitedLocations.contains(neighbour)) {
                            visitedLocations.add(neighbour);
                        }
                    }

                    actLocation = neighbour;
                    if (b != null) {
                        actLocation = b;
                    }

                }

                if (!routingList.contains(a) && a != null) {
                    routingList.add(a);
                    locations.remove(a);
                }

                if (!routingList.contains(b) && b != null) {
                    routingList.add(b);
                    locations.remove(b);
                }

                if (!visitedLocations.contains(a) && a != null) {
                    visitedLocations.add(a);
                }

                if (!visitedLocations.contains(b) && b != null) {
                    visitedLocations.add(b);
                }

            }
            routingList.add(startLocation);
            routeList.add(routingList);

        }

        for (int i = 0; i < transportNetwork.getLocations().length; i++) {
            if (!visitedLocations.contains(transportNetwork.getLocations()[i]) && transportNetwork.getLocations()[i] != startLocation) {
                ArrayList<Location> restRoutes = new ArrayList<>();
                //  restRoutes.add(startLocation);
                restRoutes.add(transportNetwork.getLocations()[i]);
                restRoutes.add(startLocation);
                routeList.add(restRoutes);
                visitedLocations.add(transportNetwork.getLocations()[i]);
            }
        }

        System.out.println("Anzahl Touren: " + routeList.size());
        for (int j = 0; j < routeList.size(); j++) {
            ArrayList<Location> route = routeList.get(j);
            System.out.print("Route mit " + route.size() + " destinations: ");
            for (int i = 0; i < route.size(); i++) {
                System.out.print(route.get(i).getName() + " ");
            }
            System.out.println();
        }
        System.out.println();

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
            Location location = null;
            visitedLocations.add(startLocation);
            for (int i = 0; i < visitedLocations.size(); i++) {
                if (!actLocation.getNeighbouringLocations().isEmpty()) {
                    for (Map.Entry<Location, Integer> entry : actLocation.getNeighbouringLocations().entrySet()) {
                        distance = entry.getValue();
                        location = entry.getKey();
                        for (Map.Entry<Location, Integer> dis : depotDistanceHashMap.entrySet()) {
                            Location locationDis = dis.getKey();
                            int dist = dis.getValue();
                            if (actLocation.toString().equals(locationDis.toString())) {
                                distance = distance + dist;
                            }
                        }
                        if (!depotDistanceHashMap.containsKey(location))
                            depotDistanceHashMap.put(location, distance);
                        if (!visitedLocations.contains(location)) {
                            visitedLocations.add(location);
                            distance = 0;
                        }
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

    /**
     * Check whether a destination location is already served by a truck.
     *
     * @param solution    The solution found so far. The solution does not have to be final.
     * @param destination The destination to check whether it is already served by a tour.
     * @return A boolean value indicating whether the destination is served already or not.
     */
    private boolean checkForLocationAlreadyServed(Solution solution, Location destination) {
        for (Tour tour : solution.getTruckTours()) {
            for (TourDestination tourDestination : tour.getTourDestinations()) {
                if (tourDestination.getDestination().equals(destination)) {
                    if (tourDestination.getUnload() == tourDestination.getDestination().getAmount()) {
                        return true;
                    }
                }
            }
            // TODO modify the method to return the tour that serves the location and whether the tour serves the location in full.
        }
        return false;
    }
}