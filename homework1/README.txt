What happend in short:
1. Profiler sends its profile to Guide
2. Guide gets collection of art from Curator
3. Guide customizes the collection and sends it to Profiler
4. Profiler asks Curator about every piece of art it received


To compile:
javac ProfilerAgent.java GuideAgent.java CuratorAgent.java

To Execute:
java -cp <your-classpath> jade.Boot -gui -agents 'profiler:ProfilerAgent;guide:GuideAgent;curator:CuratorAgent'


To get different results, modify the parameters minAge and maxAge to UserProfile in ProfilerAgent.java
All art is hardcoded and in order to get results, the values should be between 1000 and 2000 :-)

