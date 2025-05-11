### Promotions for different type of payment options project:

**The following project features 4 main java files:**

Main.java - Entry point of the program. Loads input data (orders and payment methods) from JSON files and invokes the optimization process.

Order.java - Represents a customer order, including its value and any associated promotion method IDs.

PaymentMethod.java - Represents a payment method with a usage limit and discount percentage.

PaymentOptimiser.java - Core logic that calculates the optimal way to pay for orders using available payment methods and promotions to minimize cost. The algorithm uses a greedy brute-force approach, where it first computes all possible absolute discount values for each order, then sorts orders in descending order by their total value. For each order, it prioritizes the best available promotion or partial point usage, then uses the payment method with the highest available balance to cover the remaining cost.

**And one java file for testing:**

PaymentOptimizerTest.java - Contains JUnit tests that validate the behavior of payment allocation and discount logic in PaymentOptimizer