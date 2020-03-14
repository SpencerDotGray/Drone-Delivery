package Simulation;

import Food.Meal;
import Food.Order;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Simulation {
    ArrayList<Order> orderQueue = new ArrayList<Order>();
    ArrayList<Meal> mealList = new ArrayList<Meal>();
    PriorityQueue<Order> currentOrderQueue = new PriorityQueue<Order>();

    private void startSimulation(){
        //should add default meals (for now) to meal list
        //is what calls the run FIFO and runKnapsack methods a specified amount of times
        //calls generateOrderQueue and copy orderQueue when appropriate
    }

    private void generateOrderQueue(){
        //generates a new order queue at the beginning of each pair of simulations
    }

    private void copyOrderQueue(){
        //makes a deep copy of the orderQueue and makes in into a priority queue
    }

    private void runFIFO(){
        //runs FIFO simulations
    }

    private void runKnapsack(){
        //runs knapsack simulations
    }
}
