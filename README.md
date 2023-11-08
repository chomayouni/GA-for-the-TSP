# GA-for-the-TSP
This is a genetic algorithm that was developed for testing the optimization of other approaches to the Traveling Salesmen Problem

I'm still figuring out the actual calculations of the GA but below if the rough psuedo-code:

// Main Genetic Algorithm Process
Initialize random tours and adjacency for city distances (matrix math???)

// Main loop for genetic algorithm
For each generation:
    // Selection
    For each island:
        -Perform tournament selection to choose two parents for crossover
        -Store selected parents for crossover

    // Crossover (I'm not sure which version, but we can plug that in)
    For each position in the tour:
        -Perform crossover operation to combine genes (cities) from parents
        -Store new offspring in population

    // Mutation
    For each island:
        -Randomly mutate the tour with a given probability
        -Ensure mutation does not affect the starting city

    // Fitness Evaluation
    For each island:
        -Calculate the total distance (cost) of the tour
        -Calculate the fitness of the tour based on its cost

    // Elitism (optional)
    If using elitism:
        Directly copy the best tours to the next generation

    // Logging
    Periodically output the best tour and its cost for monitoring to the console

// End of genetic algorithm
Output the best tour found, its cost, and plot that data on google

