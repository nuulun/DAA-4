package algo.metrics;

public class Metrics {
    public long dfsVisits = 0;
    public long dfsEdges = 0;
    public long kahnPushes = 0;
    public long kahnPops = 0;
    public long relaxations = 0;
    public long startTime, endTime;

    public void startTimer() { startTime = System.nanoTime(); }
    public void stopTimer() { endTime = System.nanoTime(); }
    public double elapsedTime() { return (double) (endTime - startTime) / 1_000_000; }
}
