import java.util.HashSet;
import java.util.HashMap;
import java.util.Stack;

public class TSPGraph implements IApproximateTSP {

    @Override
    public void MST(TSPMap map) {
        // Prim's Algorithm: Initialization

        // Initialize Priority Queue
        TreeMapPriorityQueue<Double, Integer> pq = new TreeMapPriorityQueue<>();
        for (int i = 0; i < map.getCount(); i++) {
            pq.add(i, Double.MAX_VALUE);
        }
        pq.decreasePriority(0, 0.0);

        // Initialize set S containing Points already in MST
        HashSet<Integer> S = new HashSet<>();
        S.add(0);

        //Initialize parent hash table
        HashMap<Integer, Integer> parent = new HashMap<>();
        parent.put(0, null);

        while (!pq.isEmpty()) {
            int min = pq.extractMin();

            // Draw link to parent
            if (parent.get(min) != null) {
                map.eraseLink(min);
                map.setLink(min, parent.get(min));
            }

            // Add node to MST
            S.add(min);

            for (int i = 0; i < map.getCount(); i++) {
                if (!S.contains(i)) {
                    if (pq.lookup(i) > map.pointDistance(min, i)) {
                        pq.decreasePriority(i, map.pointDistance(min, i));
                        parent.put(i, min);
                    }
                }
            }
        }
    }

    @Override
    public void TSP(TSPMap map) {
        MST(map);

        // Create nbrList for each node in MST
        HashMap<Integer, Stack<Integer>> nbrList = new HashMap<>();

        // Child Parent relation in MST
        HashMap<Integer, Integer> childParent = new HashMap<>();

        // Initialise Stack for each Node in nbrList
        for (int i = 0; i < map.getCount(); i++) {
            nbrList.put(i, new Stack<>());
        }

        // Fill up nbrList and Child Parent relation
        for (int i = 0; i < map.getCount(); i++) {
            int parent = map.getPoint(i).getLink();
            if (parent != -1) {
                nbrList.get(parent).push(i);
                childParent.put(i, parent);
                map.eraseLink(i);
            }
        }

        // Visited nodes to avoid revisiting
        boolean[] visited = new boolean[map.getCount()];

        int curr = 0;
        int skipped = 0;
        while (curr != 0 || !nbrList.get(0).isEmpty()) {
            if (nbrList.get(curr).isEmpty()) {
                if (!visited[curr]) {
                    visited[curr] = true;
                    skipped = curr;
                }
                curr = childParent.get(curr);
            } else {
                int popped = nbrList.get(curr).pop();
                if (!visited[curr]) {
                    visited[curr] = true;
                    map.setLink(curr, popped);
                } else {
                    map.setLink(skipped, popped);
                }
                curr = popped;
            }
        }
        map.setLink(skipped, 0);
    }

    @Override
    public boolean isValidTour(TSPMap map) {
        // Keep track of the edges
        HashMap<Integer, Integer> childParent = new HashMap<>();

        // Keep track of the number of times each nodes appears
        int[] count = new int[map.getCount()];

        for (int i = 0; i < map.getCount(); i++) {
            int mapTo = map.getPoint(i).getLink();
            if (mapTo == -1) {
                return false;
            }
            if (childParent.get(mapTo) != null) {
                return false;
            }
            childParent.put(mapTo, i);
            count[mapTo] += 1;
            if (count[mapTo] > 1) {
                return false;
            }
        }
        int curr = 0;
        boolean[] visited = new boolean[map.getCount()];
        for (int i = 0; i < map.getCount(); i++) {
            curr = childParent.get(curr);
            if (visited[curr]) {
                return false;
            }
            visited[curr] = true;
        }
        return true;
    }

    @Override
    public double tourDistance(TSPMap map) {
        if (!isValidTour(map)) {
            return -1;
        }
        double result = 0;
        for (int i = 0; i < map.getCount(); i++) {
            int mapTo = map.getPoint(i).getLink();
            double distance = map.pointDistance(i, mapTo);
            result += distance;
        }
        return result;
    }

    public static void main(String[] args) {
        TSPMap map = new TSPMap(args.length > 0 ? args[0] : "fiftypoints.txt");
        TSPGraph graph = new TSPGraph();
        System.out.println(graph.isValidTour(map));
    }
}
