package Simulation;

import Food.Food;
import Food.Meal;
import Food.Order;
import Mapping.Waypoint;
import Mapping.Map;
import Mapping.Waypoint;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Simulation {
    private ArrayList<Order> orderQueue;
    private ArrayList<Meal> mealList; //stores all different possible meals. Will be passed on constructor
    private ArrayList<Order> currentOrderQueue; //for knapsack, skipped orders are prioritized and to a priority list
    private int numShifts, timesToBeRan; //passed in constructor, stores number of hours to do the simulation and number of dif sims
    public int[] ordersPerHour, times;
    public Map simMap;

    //Creation of drone for testing purposes at this point
    public Drone drone = new Drone();

    public Simulation(){
        ArrayList<Waypoint> testPoints = new ArrayList<Waypoint>();
        testPoints.add(new Waypoint("Point 1", 1, 1, true));
        testPoints.add(new Waypoint("Point 2", 2, 1, true));
        testPoints.add(new Waypoint("Point 3", 3, 1, true));
        simMap = new Map(new Waypoint("Starting", 0, 0, true), testPoints);
        orderQueue = new ArrayList<Order>();
        mealList = new ArrayList<Meal>();
        currentOrderQueue = new ArrayList<Order>();

        numShifts = 4;
        ordersPerHour = new int[numShifts];
        timesToBeRan = 1;
        ordersPerHour[0] = 15;
        ordersPerHour[1] = 17;
        ordersPerHour[2] = 22;
        ordersPerHour[3] = 15;

        times = new int[ordersPerHour[0] + ordersPerHour[1] + ordersPerHour[2] + ordersPerHour[3]];

        ArrayList<Food> temp = new ArrayList<Food>();
        temp.add(new Food("Hamburger", 0.375));
        temp.add(new Food("Drink", 0.875));
        temp.add(new Food("Fries", 0.25));

        mealList.add(new Meal("One of Each", temp, 0.55));

        temp.add(new Food("Hamburger", 0.375));
        mealList.add(new Meal("Two burgers, one drink, one fry", temp, 0.1));

        temp.remove(2);
        temp.remove(2);
        mealList.add(new Meal("Burger and Drink", temp, 0.2));

        temp.add(new Food("Hamburger", 0.375));
        mealList.add(new Meal("Two Burgers and a Drink", temp, 0.15));
    }

    //the check for adding the probabilities needs to be somewhere. Should be outside of this class
    //for simulations with default settings ie sprint 1
    public Simulation(Map m){
        simMap = m;
        orderQueue = new ArrayList<Order>();
        mealList = new ArrayList<Meal>();
        currentOrderQueue = new ArrayList<Order>();

        numShifts = 4;
        ordersPerHour = new int[numShifts];
        timesToBeRan = 1;
        ordersPerHour[0] = 15;
        ordersPerHour[1] = 17;
        ordersPerHour[2] = 22;
        ordersPerHour[3] = 15;

        times = new int[ordersPerHour[0] + ordersPerHour[1] + ordersPerHour[2] + ordersPerHour[3]];

        ArrayList<Food> temp = new ArrayList<Food>();
        temp.add(new Food("Hamburger", 0.375));
        temp.add(new Food("Drink", 0.875));
        temp.add(new Food("Fries", 0.25));

        mealList.add(new Meal(temp, 0.55));

        temp.add(new Food("Hamburger", 0.375));
        mealList.add(new Meal(temp, 0.1));

        temp.remove(2);
        temp.remove(2);
        mealList.add(new Meal(temp, 0.2));

        temp.add(new Food("Hamburger", 0.375));
        mealList.add(new Meal(temp, 0.15));
    }

    public Simulation(int numberOfShifts, int timesToRun, ArrayList<Meal> listOfMeals, int[] avgOrdersEachHour, Map m){
        //this will be used when we start having settings for the simulation
        numShifts = numberOfShifts;
        timesToBeRan = timesToRun;
        //deep copy of meals list

    }

    public void runSimulation(){
        //should add default meals (for now) to meal list
        //is what calls the run FIFO and runKnapsack methods a specified amount of times
        //calls generateOrderQueue and copy orderQueue when appropriate

        for(int i = 0; i < timesToBeRan; i++){
            generateOrderQueue();
            copyOrderQueue();

            //runFIFO();

            copyOrderQueue();

            //runKnapsack();
        }

        //drones must be reset before each simulation
    }

    /**
     * Generates a list of random orders and puts them in the simulations order queue. Uses the probabilities for each order to appear and
     * randomly selects a point on the map to generate the specified number of orders in each hour using the meals available in the meals list.
     */
    private void generateOrderQueue(){
        int count = 0, curPos, mapPos;
        double prob, curProb;
        boolean found;

        //clears out the previous order queue
        for(int r = orderQueue.size(); r > 0; r--){
            orderQueue.remove(r - 1);
        }

        for(int i = 0; i < numShifts; i++){
            for(int j = 0; j < ordersPerHour[i]; j++){
                curProb = 0;
                curPos = 0;
                found = false;

                //generates orders according to their given probabilities
                prob = (Math.random()*(1));
                while(curPos < mealList.size() && !found){
                    //System.out.println("The probability is " + prob + "\tThe Current pos is " + curPos + "\t Im checking if it's under " + mealList.get(curPos).getProbability() + curProb + "\twhich equals " + mealList.get(curPos).getProbability() + "+" + curProb);
                    if(prob < mealList.get(curPos).getProbability() + curProb){
                        found = true;
                    } else {
                        curProb += mealList.get(curPos).getProbability();
                        curPos++;
                    }
                }
                //finds a random delivery location to give an order based off of the current map
                mapPos = ((int) (Math.random()*(simMap.getSize())));
                orderQueue.add(new Order(mealList.get(curPos), simMap.getMapPoint(mapPos)));
                times[count] = (i * 60) + (int) (1 + Math.random() * ((60 - 1) + 1));
                count++;
            }
        }

        //sorts the times array from least to greatest
        heapSort(times);

        for(int r = 0; r < orderQueue.size(); r++){
            System.out.println("At time " + times[r] + " An order with " + orderQueue.get(r).getMeal().getName() + " to " + orderQueue.get(r).getDestination().getName() + " will appear in the queue.");
        }
    }

    /**
     * Creates a deep copy of the orderQueue and stores it in currentOrderQueue to allow modifications during a simulation
     */
    private void copyOrderQueue(){
        for(int i = 0; i < orderQueue.size(); i++){
            currentOrderQueue.add(orderQueue.get(i));
        }
    }

    private void runFIFO(){
        //runs FIFO simulations
        //at start, wait for 5 min or until full
        double currentTime = 0;
        int currentOrder = 0;
        boolean launched = false, canLoad = true;
        //currentOrder tracks the current order. Will be used with the times array for checking

        while (currentOrderQueue.size() > 0 || drone.getNumOrders() > 0){
            if(drone.getCurrentPosition().isStarting()){
                launched = false;
                canLoad = true;
                if(drone.getTurnAroundTime() == 0){

                    //when the drone is at the starting point and it's within the first five minutes of the simulation
                    if(currentTime <= 5){
                        //the drone will add any available orders if it can. If the drone is full then it will launch
                        while(times[currentOrder] <= currentTime && !launched){
                            if(drone.getCurrentWeight() + currentOrderQueue.get(0).getMeal().getTotalWeight() < drone.getWeightCapacity()){ //check weight
                                drone.addOrderToDrone(currentOrderQueue.get(0));
                                currentOrderQueue.remove(0);
                                currentOrder++;
                            } else { //launch
                                //calculate tsp, remaining transit, then add the time it takes to get to the destination with current time
                                //set the drones current location to the orders waypoint
                                //adds the calculated delivery time to the delivery times list
                                launched = true;
                            }
                        }

                        //when the drone is at the starting position and it's not within the first five minutes of the simulation
                    } else {
                        while(times[currentOrder] <= currentTime && canLoad){
                            if(drone.getCurrentWeight() + currentOrderQueue.get(0).getMeal().getTotalWeight() < drone.getWeightCapacity()){ //check weight
                                drone.addOrderToDrone(currentOrderQueue.get(0));
                                currentOrderQueue.remove(0);
                                currentOrder++;
                            } else {
                                canLoad = false;
                            }
                        }
                        if(drone.getCurrentWeight() > 0){
                            //calculate tsp, remaining transit, then add the time it takes to get to the destination with current time
                            //set the drones current location to the orders waypoint
                            //adds the calculated delivery time to the delivery times list
                        }

                        //when the drone is not at the starting position but is at a waypoint
                    }
                } else {
                    currentTime += drone.getTurnAroundTime();
                    drone.setTurnAroundTime(0);
                }
            } else {
                //if the drone is carrying nothing then it returns home (Calculate distance to home)
                //else call tsp/a list of destinations already found by tsp to get the drones next route
                //reset the drones target destination and the distance to get there
                //deliver the order (add appropriate time)
            }
        }
    }

    private void runKnapsack(){
        //runs knapsack simulations
        int currentTime = 0;
        //assign a value to every order in queue, grab the heaviest and put it in the bag, then next heaviest, then so forth. if it fits, put it in.
        //skipped me counter, make sure skipped meals are prioritized
        while (currentTime < numShifts * 60){
            //fifo
        }
    }

    //The TSP Route Calculator - Josh
    //Searches the Routes Search tree recursively with DFS
    //and uses backtracking to efficiently find best route
    private int[] tspRoute(double[][] graph, int[] solution, int[] bestSolution, boolean[] visited,
                          int currentNode, int level, int numNodes) {
        boolean debug = false;

        if(debug)
            System.out.println("I'm at Node: " + currentNode);

        //modify solution SO FAR
        solution[level] = currentNode;

        //calculate the COST SO FAR
        double costSoFar = 0.0;
        int lastNode = 0;
        for(int i = 1; i <= level; i++) {
            int node1 = solution[i - 1];
            int node2 = solution[i];
            costSoFar += graph[node1][node2];
            if(i == level)
                lastNode = solution[i];
        }

        //find BEST COST SO FAR
        double bestCostSoFar = 0.0;
        for(int i = 1; i < bestSolution.length; i++) {
            int node1 = bestSolution[i - 1];
            int node2 = bestSolution[i];
            bestCostSoFar += graph[node1][node2];
        }


        //BACKTRACK if I've passed the best cost so far
        //AND we're not just at the beginning node
        //AND we actually have a bestCostSoFar
        //(Agent will go down left side of tree and find the first complete cost
        // and that cost will be set as the first bestCostSoFar)
        if(debug)
            System.out.println("costSoFar: " + costSoFar + " bestCostSoFar: " + bestCostSoFar);
        if(costSoFar > bestCostSoFar && level != 0 && bestCostSoFar != 0.0) {
            if(debug)
                System.out.println("RETURNING");
            return solution;
        }


        //BOTTOM OF RECURSIVE CALL
        //I've hit the second to last level of tree, still need to check path back to 1
        if(level == numNodes - 1) {
            if(debug)
                System.out.println("HIT BOTTOM");

            //add going to 0 to my current solution
            solution[solution.length - 1] = 0;
            costSoFar += graph[lastNode][0];

            //For testing purposes only
            if(debug ) {
                System.out.print("CURRENT SOLUTION: ");
                for(int i = 0; i < solution.length; i++)
                    System.out.print(solution[i] + " ");
                System.out.println("COST: " + costSoFar);
            }

            // check solution against best solution
            // If my current cost is better than my best cost so far, OR
            // if I don't yet have a best cost so far, make one
            if(costSoFar < bestCostSoFar || bestCostSoFar == 0) {
                if(debug)
                    System.out.println("REASSIGNING BESTSOLUTION");
                for(int i = 0; i < bestSolution.length; i++) {
                    bestSolution[i] = solution[i];
                }
            }

            if(debug) {
                System.out.println("RETURNING TO PARENT...");
                System.out.println("");
            }
            return solution;
        }

        visited[currentNode] = true;
        for(int i = 0; i < numNodes; i++) {
            if(!visited[i]) {
                if(debug)
                    System.out.println("I'm in level " + level + " node " + currentNode + " and visiting node " + i);
                solution = tspRoute(graph, solution, bestSolution, visited,
                        i, level + 1, numNodes);
                if(debug)
                    System.out.println("I'm back in level " + level + " and node " + currentNode);
            }
        }
        visited[currentNode] = false;

        if(level == 0) {
            if(debug)
                System.out.println("FINAL RETURN...");
            return bestSolution;
        }
        else {
            if(debug)
                System.out.println("RETURNING TO PARENT...");
            return solution;
        }
    }

    //This uses the TSP algorithm to find the best possible route
    //returns: an ordered version of the "orders" arraylist passed in
    private ArrayList<Order> sortOrders(ArrayList<Order> orders) {
        //will use the drones current location and current orders to solve the traveling salesman problem
        //This is the same as my main in BackTrackingTSP.java - Josh

        // I'll need to automatically put the SAC as the starting point
        Waypoint sac = new Waypoint("SAC", 41.154870, -80.077945, true);
        orders.add(0, new Order(new Meal(), sac));


        int numNodes = orders.size();
        double[][] graph = new double[numNodes][numNodes];

        //Now that I have the points, I need to make the graph
        for(int node = 0; node < orders.size(); node++) {
            for(int otherNode = 0; otherNode < orders.size(); otherNode++) {
                // First get difference in longitude
                double deltaX = orders.get(node).getDestination().getLongitude() -
                        orders.get(otherNode).getDestination().getLongitude();
                // Then get difference in latitude
                double deltaY = orders.get(node).getDestination().getLatitude() -
                        orders.get(otherNode).getDestination().getLatitude();
                double powX = Math.pow(deltaX, 2.0);
                double powY = Math.pow(deltaY, 2.0);
                //The distance between the points using the pythagorean theorem
                graph[node][otherNode] = (Math.sqrt(powX + powY));
            }
        }

        int[] solution = new int[numNodes + 1];
        int[] bestSolution = new int[numNodes + 1];
        for(int i = 0; i < numNodes + 1; i++) {
            solution[i] = 0;
            bestSolution[i] = 0;
        }

        boolean[] visited = new boolean[numNodes];
        for(int i = 0; i < numNodes; i++)
            visited[i] = false;

        bestSolution = tspRoute(graph, solution, bestSolution, visited, 0, 0, numNodes);

        //Reorder 'orders', at this point, it's "SAC", "HAL", "Ketler",...
        ArrayList<Order> sortedOrders = new ArrayList<>();

        // We want to start at the first
        for(int i = 1; i < bestSolution.length - 1; i++) {
            //System.out.println("Putting the " + bestSolution[i] + "th index of orders in sortedOrders");
            sortedOrders.add(orders.get(bestSolution[i]));
        }

        return sortedOrders;

    }

    public void heapSort(int a[])
    {
        int size = a.length;

        for (int j = size/2 - 1; j >= 0; j--) {
            createHeap(a, j, size);
        }
        int temp;

        for (int i = size - 1; i >= 0; i--) {
            temp = a[0];
            a[0] = a[i];
            a[i] = temp;

            createHeap(a, 0, i);
        }

    }

    void createHeap(int a[], int root, int size)
    {
        int largest = root, left = 2 * root + 1, right = 2 * root + 2;

        if (left < size && a[left] > a[largest]){
            largest = left;
        }
        if (right < size && a[right] > a[largest]){
            largest = right;
        }
        if (largest != root){
            int temp = a[root];
            a[root] = a[largest];
            a[largest] = temp;
            createHeap(a, largest, size);
        }
    }

    //Average and worst delivery time variable, found by getting the average and worst from deliveryTimes
    //public Double average = getAverage(drone.deliveryTimes);
    //public Double worst = getWorst(drone.deliveryTimes);

    public static Double getAverage(Double [] times){
        double sum = 0;
        for(int i = 0; i<times.length; i++){
            sum += times[i];
        }
        return sum/times.length;
    }
    public static Double getWorst(Double [] times){
        double worst = 0;
        for(int i = 0; i<times.length; i++){
            if(times[i]>worst){
                worst = times[i];
            }
        }
        return worst;
    }
}
