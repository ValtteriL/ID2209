What happend in short:
1. Profilers register themselves at the DF
2. Curator finds all registered profilers
3. Curator sends profilers the price every 3 seconds and between each time reduces the price
4. If the price profiler receives is below the maximum amount profiler is ready to pay, it will purchase the item
5. If the price has fallen below the lowest curator is ready to go, it will tell profilers that there were no bids


To compile:
javac ProfilerAgent.java CuratorAgent.java

To Execute:
java -cp $CLASSPATH:$PWD jade.Boot -agents 'profiler1:ProfilerAgent(50);profiler2:ProfilerAgent(60);profiler3:ProfilerAgent(70);curator:CuratorAgent(100, 10, 80)'

ProfilerAgent takes one argument, the maximum amount its willing to pay
CuratorAgent takes three arguments, the starting price, the amount the price is reduced every round and the lowest price

To get different results, try giving different arguments to the curator and profilers
You can also add more profilers easily

