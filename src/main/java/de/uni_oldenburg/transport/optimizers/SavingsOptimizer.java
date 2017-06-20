package de.uni_oldenburg.transport.optimizers;

import de.uni_oldenburg.transport.Solution;
import de.uni_oldenburg.transport.Tour;
import de.uni_oldenburg.transport.TransportNetwork;
import de.uni_oldenburg.transport.Location;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;



/**
 * Optimizer with Savings Algorithm
 */
public class SavingsOptimizer implements Optimizer {

    Location startLocation;

    @Override
    public Solution optimizeTransportNetwork(TransportNetwork transportNetwork) {
        startLocation = transportNetwork.getStartLocation();
        HashMap<Location, Integer> shortestWays = depotDistance(transportNetwork);

        for (Map.Entry<Location, Integer> entry : shortestWays.entrySet()) {
            System.out.println("Stadt: " + entry.getKey().getName() + " ist " + entry.getValue() + " km von Hamburg entfernt.");
        }

        System.out.println();

        HashMap<String, Integer> savings = getSavings(shortestWays, transportNetwork);

        for (Map.Entry<String, Integer> entry : savings.entrySet()) {
            System.out.println("Städte: " + entry.getKey() + " saving: " + entry.getValue());
        }

        HashMap<String, ArrayList<Location>> getRoutes = getRoutes(savings, transportNetwork);





        return new Solution(transportNetwork);
    }

    /**
     * Gets the savings for the distances between the Locations and the Depot
     *
     * @param depotDistance
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
     * @param savings
     * @param transportNetwork
     * @return
     */
    private HashMap<String, ArrayList<Location>> getRoutes(HashMap<String, Integer> savings, TransportNetwork transportNetwork) {
        HashMap<String, ArrayList<Location>> routes = new HashMap<>();
        ArrayList<Location> visitedLocations = new ArrayList<>();
        Location actLocation = startLocation;
        ArrayList<Location> routeLocation = new ArrayList<>();

        while (!savings.isEmpty()) {
            int saving = 0;
            int delete = 0;
            String nameDelete = "";
            Location a = null;
            Location b = null;


            for (Map.Entry<Location, Integer> neighbour : actLocation.getNeighbouringLocations().entrySet()) {
                for (Map.Entry<String, Integer> sav : savings.entrySet()) {
                    if (sav.getKey().contains(neighbour.getKey().getName())) {
                        if (sav.getValue() > saving) {
                            saving = sav.getValue();
                            nameDelete = sav.getKey();
                            for (int i = 0; i < transportNetwork.getLocations().length; i++) {
                                if (transportNetwork.getLocations()[i] != a && sav.getKey().contains(transportNetwork.getLocations()[i].getName())) {
                                    if (a == null) {
                                        a = transportNetwork.getLocations()[i];
                                    }
                                }
                                if (transportNetwork.getLocations()[i] != a && sav.getKey().contains(transportNetwork.getLocations()[i].getName())) {
                                    b = transportNetwork.getLocations()[i];
                                }
                            }
                            actLocation = a;
                        }
                    }
                }
                if (!routeLocation.contains(a)) {
                    routeLocation.add(a);
                }
                if (!routeLocation.contains(b)) {
                    routeLocation.add(b);
                }

                visitedLocations.add(a);
                visitedLocations.add(b);
                savings.remove(nameDelete, saving);
                saving = 0;

            }

            actLocation = a;
            a = null;

        }
        String tourName = "tour" + String.valueOf(routes.size());
        routes.put(tourName, routeLocation);
        return routes;
    }

    /**
     * Finds out the shortest way from the startLocation to each Location.
     *
     * @param network
     * @return
     */
    private HashMap depotDistance(TransportNetwork network) {
        HashMap<Location, Integer> depotDistanceHashMap = new HashMap<>();
        ArrayList<Location> visitedLocations = new ArrayList<Location>();
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
        return depotDistanceHashMap;
    }
}
