import javax.servlet.ServletContext;
import java.util.*;

/**
 * Created by st.ivanov44 on 29/09/2017.
 */
public final class GraphNetwork {

    private static List<Point> points = new ArrayList<>();
    private static Map<Integer, List<Integer>> neighbors = new HashMap<Integer, List<Integer>>();
    private static int source, target;
    private static KDTree kdTree;
    public static String route;
    public static int routeNodesNum = 0;

    public static void setSource (int s) {
        source = s;
    }

    public static void setTarget (int t) {
        target = t;
    }

    public static String getRoute() {
        return route;
    }


    public static double aStar() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        Point t = points.get(target-1);
        queue.add(s);

        double[] pollution = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(pollution, 3000000000.0);

        pollution[source] = s.getPollution();

        while(!queue.isEmpty()) {

            Point p = queue.poll();
            Integer curNode = p.getIndex();
            settled[curNode] = true;

            if (curNode == target) break;

            for(Integer i : neighbors.get(curNode)) {
                if (!settled[i]) {
                    Point neighbor = points.get(i-1);
                    if (pollution[i] > pollution[curNode]+neighbor.getPollution()) {
                        pollution[i] = pollution[curNode]+neighbor.getPollution();
                        neighbor.setTotalPollution(pollution[i] + 70 * neighbor.getHarvesineDistance(t));
                        parent[i] = curNode;
                        queue.add(neighbor);
                    }
                }
            }

        }

        route = GraphNetwork.saveRouteNodes(points, target, parent);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);

        return pollution[target];

    }

    public static double dijkstraByPollution() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        queue.add(s);

        double[] pollution = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(pollution, 3000000000.0);

        pollution[source] = s.getPollution();

        while(!queue.isEmpty()) {

            Point p = queue.poll();
            Integer curNode = p.getIndex();
            settled[curNode] = true;

            if (curNode == target) break;

            for(Integer i : neighbors.get(curNode)) {
                if (!settled[i]) {
                    Point neighbor = points.get(i-1);
                    if (pollution[i] > pollution[curNode]+neighbor.getPollution()) {
                        pollution[i] = pollution[curNode]+neighbor.getPollution();
                        neighbor.setTotalPollution(pollution[i]);
                        parent[i] = curNode;
                        queue.add(neighbor);
                    }
                }
            }

        }

        route = GraphNetwork.saveRouteNodes(points, target, parent);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);

        return pollution[target];

    }

    public static double dijkstraByDistance() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        queue.add(s);

        double[] dist = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(dist, 3000000000.0);

        dist[source] = 0;

        while(!queue.isEmpty()) {

            Point p = queue.poll();
            Integer curNode = p.getIndex();
            settled[curNode] = true;

            if (curNode == target) break;

            for(Integer i : neighbors.get(curNode)) {
                if (!settled[i]) {
                    Point neighbor = points.get(i-1);
                    Double d = p.getHarvesineDistance(neighbor);
                    if (dist[i] > dist[curNode] + d) {
                        dist[i] = dist[curNode] + d;
                        neighbor.setTotalPollution(dist[i]);
                        parent[i] = curNode;
                        queue.add(neighbor);
                    }
                }
            }

        }

        route = GraphNetwork.saveRouteNodes(points, target, parent);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);

        return dist[target];

    }

    public static double bidirectionalDijkstraByDistance() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue_forward = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        PriorityQueue<Point> queue_backward = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        Point t = points.get(target-1);
        queue_forward.add(s);
        queue_backward.add(t);

        double[] dist_forward = new double[100001];
        double[] dist_backward = new double[100001];
        int[] parent_forward = new int[100001];
        int[] parent_backward = new int[100001];
        boolean[] settled_forward = new boolean[100001];
        boolean[] settled_backward = new boolean[100001];

        Arrays.fill(dist_forward, 3000000000.0);
        Arrays.fill(dist_backward, 3000000000.0);

        dist_forward[source] = 0;
        dist_backward[target] = 0;

        boolean turnFlag = true;
        double shortestPathLength = 0;
        int meetingPoint = 0;

        while(true) {

            if (turnFlag && queue_forward.isEmpty()) continue;
            if (!turnFlag && queue_backward.isEmpty()) continue;
            if (queue_forward.isEmpty() && queue_backward.isEmpty()) break;

            if (turnFlag) {
                Point p = queue_forward.poll();
                Integer curNode = p.getIndex();
                while (settled_forward[curNode]) {
                    p = queue_forward.poll();
                    curNode = p.getIndex();
                }
                settled_forward[curNode] = true;

                if (settled_backward[curNode] == true) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode]; //-p.getPollution()
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_forward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = p.getHarvesineDistance(neighbor);
                        if (dist_forward[i] > dist_forward[curNode] + d) {
                            dist_forward[i] = dist_forward[curNode] + d;
                            neighbor.setTotalPollution(dist_forward[i]);
                            parent_forward[i] = curNode;
                            queue_forward.add(neighbor);
                        }
                    }
                }
            } else {
                Point p = queue_backward.poll();
                Integer curNode = p.getIndex();
                while (settled_backward[curNode]) {
                    p = queue_backward.poll();
                    curNode = p.getIndex();
                }
                settled_backward[curNode] = true;

                if (settled_forward[curNode] == true) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode]; // - p.getPollution();
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_backward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = p.getHarvesineDistance(neighbor);
                        if (dist_backward[i] > dist_backward[curNode] + d) {
                            dist_backward[i] = dist_backward[curNode] + d;
                            neighbor.setTotalPollution(dist_backward[i]);
                            parent_backward[i] = curNode;
                            queue_backward.add(neighbor);
                        }
                    }
                }
            }

            turnFlag = !turnFlag;
        }

        if (shortestPathLength == 0) return 0;
        while(!queue_forward.isEmpty()) {
            Point p = queue_forward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                Point neighbor = points.get(i-1);
                Double d = p.getHarvesineDistance(neighbor);
                if (shortestPathLength > dist_forward[curNode] + dist_backward[i] + d) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[i] + d;
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }
        while(!queue_backward.isEmpty()) {
            Point p = queue_backward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                Point neighbor = points.get(i-1);
                Double d = p.getHarvesineDistance(neighbor);
                if (shortestPathLength > dist_backward[curNode] + dist_forward[i] + d) {
                    shortestPathLength = dist_backward[curNode] + dist_forward[i] + d;
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }

        route = saveRouteNodesReversed(points, meetingPoint, parent_backward) + GraphNetwork.saveRouteNodes(points, meetingPoint, parent_forward);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime-startTime));

        return shortestPathLength;

    }

    public static double bidirectionalDijkstraByPollution() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue_forward = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        PriorityQueue<Point> queue_backward = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        Point t = points.get(target-1);
        queue_forward.add(s);
        queue_backward.add(t);

        double[] dist_forward = new double[100001];
        double[] dist_backward = new double[100001];
        int[] parent_forward = new int[100001];
        int[] parent_backward = new int[100001];
        boolean[] settled_forward = new boolean[100001];
        boolean[] settled_backward = new boolean[100001];

        Arrays.fill(dist_forward, 3000000000.0);
        Arrays.fill(dist_backward, 3000000000.0);

        dist_forward[source] = s.getPollution();
        dist_backward[target] = t.getPollution();

        boolean turnFlag = true;
        double shortestPathLength = 0;
        int meetingPoint = 0;

        while(true) {

            if (turnFlag && queue_forward.isEmpty()) continue;
            if (!turnFlag && queue_backward.isEmpty()) continue;
            if (queue_forward.isEmpty() && queue_backward.isEmpty()) break;

            if (turnFlag) {
                Point p = queue_forward.poll();
                Integer curNode = p.getIndex();
                while (settled_forward[curNode]) {
                    p = queue_forward.poll();
                    curNode = p.getIndex();
                }
                settled_forward[curNode] = true;

                if (settled_backward[curNode] == true) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode] - p.getPollution();
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_forward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = neighbor.getPollution();
                        if (dist_forward[i] > dist_forward[curNode] + d) {
                            dist_forward[i] = dist_forward[curNode] + d;
                            neighbor.setTotalPollution(dist_forward[i]);
                            parent_forward[i] = curNode;
                            queue_forward.add(neighbor);
                        }
                    }
                }
            } else {
                Point p = queue_backward.poll();
                Integer curNode = p.getIndex();
                while (settled_backward[curNode]) {
                    p = queue_backward.poll();
                    curNode = p.getIndex();
                }
                settled_backward[curNode] = true;

                if (settled_forward[curNode] == true) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode] - p.getPollution();
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_backward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = neighbor.getPollution();
                        if (dist_backward[i] > dist_backward[curNode] + d) {
                            dist_backward[i] = dist_backward[curNode] + d;
                            neighbor.setTotalPollution(dist_backward[i]);
                            parent_backward[i] = curNode;
                            queue_backward.add(neighbor);
                        }
                    }
                }
            }

            turnFlag = !turnFlag;
        }

        if (shortestPathLength == 0) return 0;
        while(!queue_forward.isEmpty()) {
            Point p = queue_forward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                if (shortestPathLength > dist_forward[curNode] + dist_backward[i]) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[i];
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }
        while(!queue_backward.isEmpty()) {
            Point p = queue_backward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                if (shortestPathLength > dist_backward[curNode] + dist_forward[i]) {
                    shortestPathLength = dist_backward[curNode] + dist_forward[i];
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }

        route = saveRouteNodesReversed(points, meetingPoint, parent_backward) + GraphNetwork.saveRouteNodes(points, meetingPoint, parent_forward);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime-startTime));

        return shortestPathLength;

    }

    public static double bidirectionalAStar() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue_forward = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        PriorityQueue<Point> queue_backward = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        Point t = points.get(target-1);
        queue_forward.add(s);
        queue_backward.add(t);

        double[] dist_forward = new double[100001];
        double[] dist_backward = new double[100001];
        int[] parent_forward = new int[100001];
        int[] parent_backward = new int[100001];
        boolean[] settled_forward = new boolean[100001];
        boolean[] settled_backward = new boolean[100001];

        Arrays.fill(dist_forward, 3000000000.0);
        Arrays.fill(dist_backward, 3000000000.0);

        dist_forward[source] = s.getPollution();
        dist_backward[target] = t.getPollution();

        boolean turnFlag = true;
        double shortestPathLength = 0;
        int meetingPoint = 0;

        while(true) {

            if (turnFlag && queue_forward.isEmpty()) continue;
            if (!turnFlag && queue_backward.isEmpty()) continue;
            if (queue_forward.isEmpty() && queue_backward.isEmpty()) break;

            if (turnFlag) {
                Point p = queue_forward.poll();
                Integer curNode = p.getIndex();
                while (settled_forward[curNode]) {
                    p = queue_forward.poll();
                    curNode = p.getIndex();
                }
                settled_forward[curNode] = true;

                if (settled_backward[curNode] == true) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode] - p.getPollution();
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_forward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = neighbor.getPollution();
                        if (dist_forward[i] > dist_forward[curNode] + d) {
                            dist_forward[i] = dist_forward[curNode] + d;
                            neighbor.setTotalPollution(dist_forward[i] + 70 * neighbor.getHarvesineDistance(t));
                            parent_forward[i] = curNode;
                            queue_forward.add(neighbor);
                        }
                    }
                }
            } else {
                Point p = queue_backward.poll();
                Integer curNode = p.getIndex();
                while (settled_backward[curNode]) {
                    p = queue_backward.poll();
                    curNode = p.getIndex();
                }
                settled_backward[curNode] = true;

                if (settled_forward[curNode] == true) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode] - p.getPollution();
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_backward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = neighbor.getPollution();
                        if (dist_backward[i] > dist_backward[curNode] + d) {
                            dist_backward[i] = dist_backward[curNode] + d;
                            neighbor.setTotalPollution(dist_backward[i] + 70 * neighbor.getHarvesineDistance(s));
                            parent_backward[i] = curNode;
                            queue_backward.add(neighbor);
                        }
                    }
                }
            }

            turnFlag = !turnFlag;
        }

        if (shortestPathLength == 0) return 0;
        while(!queue_forward.isEmpty()) {
            Point p = queue_forward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                if (shortestPathLength > dist_forward[curNode] + dist_backward[i]) {
                    shortestPathLength = dist_forward[curNode] + dist_backward[i];
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }
        while(!queue_backward.isEmpty()) {
            Point p = queue_backward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                if (shortestPathLength > dist_backward[curNode] + dist_forward[i]) {
                    shortestPathLength = dist_backward[curNode] + dist_forward[i];
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }

        route = saveRouteNodesReversed(points, meetingPoint, parent_backward) + GraphNetwork.saveRouteNodes(points, meetingPoint, parent_forward);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime-startTime));

        return shortestPathLength;


    }

    public static String saveRouteNodes (List<Point> points, int target, int[] parent) {

        String str = "";
        int curNode = target;
        while (curNode != 0) {
            str += points.get(curNode-1).getLatitute() + " " + points.get(curNode-1).getLongitude() + "\n";
            curNode = parent[curNode];
            routeNodesNum++;
        }
        return str;

    }

    public static String saveRouteNodesReversed (List<Point> points, int target, int[] parent) {

        String str = "";
        int curNode = parent[target];
        while (curNode != 0) {
            str = points.get(curNode-1).getLatitute() + " " + points.get(curNode-1).getLongitude() + "\n" + str;
            curNode = parent[curNode];
            routeNodesNum++;
        }
        return str;

    }


    public static String getNodesWithPollution() {
        String str = "";
        for (Point p : points) {
            str += p.getLatitute() + " " + p.getLongitude() + " " + p.getPollution() + "\n";
        }
        return str;
    }


    //public static void main(String[] args) {

    public static void initialiseGraph(ServletContext context) {

        DataIO.setContext(context);

        points = DataIO.readPointsWithID();
        //points = DataIO.readPointsWithPollution();
        points = DataIO.generatePollutionAccordingToRoads(points);
        neighbors = DataIO.readAdjacencyList();
        //DataIO.readFakeDistances();

        List<Point> connectedPoints = new ArrayList<>();
        for (Point p : points) {
            int idx = p.getIndex();
            List<Integer> curNeighbors = neighbors.get(idx);
            if (curNeighbors != null && !curNeighbors.isEmpty()) connectedPoints.add(p);
        }

        kdTree = new KDTree(connectedPoints, true);

    }

    public static void findRoute(double lat1, double lng1, double lat2, double lng2) {

        Point s = kdTree.findNearest(lat1, lng1, true);
        Point t = kdTree.findNearest(lat2, lng2, true);
        source = s.getIndex();
        target = t.getIndex();

        GraphNetwork.setSource(source);
        GraphNetwork.setTarget(target);

        double leastPollution = GraphNetwork.bidirectionalAStar();

        System.out.println("Least pollution: " + leastPollution);

        // print source, target and distances between actual user input points
//        System.out.println(lat1 + " " + lng1 + " " + lat2 + " " + lng2);
//        System.out.println(s.getLatitute() + " " + s.getLongitude() + " " + t.getLatitute() + " " + t.getLongitude());
//        System.out.println((lat1-s.getLatitute())*(lat1-s.getLatitute())+(lng1-s.getLongitude())*(lng1-s.getLongitude()) + " " + (lat2-t.getLatitute())*(lat2-t.getLatitute())+(lng2-t.getLongitude())*(lng2-t.getLongitude()));

    }

}