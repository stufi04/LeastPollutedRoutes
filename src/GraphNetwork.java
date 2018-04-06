import javax.servlet.ServletContext;
import java.util.*;

/**
 * Created by st.ivanov44 on 29/09/2017.
 */
public final class GraphNetwork {

    private static List<Point> points = new ArrayList<>();
    private static Map<Integer, List<Integer>> neighbors = new HashMap<Integer, List<Integer>>();
    private static int source, target;
    private static KDTree kdTree, airspecksTree;
    //public static String routeString;
    public static ArrayList<Point> routePoints;
    //public static int routeNodesNum = 0;
    public static ArrayList<ArrayList<Point>> separateRoutes = new ArrayList<>();
    public static List<Point> airspeckPositions = new ArrayList<>();
    public static List<Point> finalAirspecks = new ArrayList<>();
    private static double circleRadius = 0, circleCenterX = 0, circleCenterY = 0;
    private static int allowedAirspecks = 3;

    public static void setSource (int s) {
        source = s;
    }

    public static void setTarget (int t) {
        target = t;
    }

    //public static String getRouteString() {
    //    return routeString;
    //}


    public static Route aStar() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        Point t = points.get(target-1);
        queue.add(s);

        double[] dist = new double[100001];
        double[] pollution = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(pollution, 3000000000.0);

        dist[source] = 0;
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
                        dist[i] = dist[curNode] + neighbor.getHarvesineDistance(p);
                        neighbor.setTotalPollution(pollution[i] + 70 * neighbor.getHarvesineDistance(t));
                        parent[i] = curNode;
                        queue.add(neighbor);
                    }
                }
            }

        }

        routePoints = new ArrayList<Point>();
        String routeString = GraphNetwork.saveRouteNodes(points, target, parent);

        Route route = new Route(routePoints, routeString, dist[target], pollution[target]);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time A*: " + (endTime-startTime));

        return route;

    }

    public static Route dijkstraByPollution() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        queue.add(s);

        double[] dist = new double[100001];
        double[] pollution = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(pollution, 3000000000.0);

        dist[source] = 0;
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
                        dist[i] = dist[curNode] + neighbor.getHarvesineDistance(p);
                        neighbor.setTotalPollution(pollution[i]);
                        parent[i] = curNode;
                        queue.add(neighbor);
                    }
                }
            }

        }

        routePoints = new ArrayList<Point>();
        String routeString = GraphNetwork.saveRouteNodes(points, target, parent);

        Route route = new Route(routePoints, routeString, dist[target], pollution[target]);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time Dijkstra: " + (endTime-startTime));

        return route;

    }

    public static Route dijkstraByDistance() {

        long startTime = System.currentTimeMillis();

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
            }
        });
        Point s = points.get(source-1);
        queue.add(s);

        double[] dist = new double[100001];
        double[] pol = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(dist, 3000000000.0);

        dist[source] = 0;
        pol[source] = s.getPollution();

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
                        pol[i] = pol[curNode] + neighbor.getPollution();
                        neighbor.setTotalPollution(dist[i]);
                        parent[i] = curNode;
                        queue.add(neighbor);
                    }
                }
            }

        }

        routePoints = new ArrayList<Point>();
        String routeString = GraphNetwork.saveRouteNodes(points, target, parent);

        Route route = new Route(routePoints, routeString, dist[target], pol[target]);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);

        return route;

    }

//    public static double bidirectionalDijkstraByDistance() {
//
//        long startTime = System.currentTimeMillis();
//
//        PriorityQueue<Point> queue_forward = new PriorityQueue<>(11, new Comparator<Point>() {
//            public int compare(Point p1, Point p2) {
//                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
//            }
//        });
//        PriorityQueue<Point> queue_backward = new PriorityQueue<>(11, new Comparator<Point>() {
//            public int compare(Point p1, Point p2) {
//                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
//            }
//        });
//        Point s = points.get(source-1);
//        Point t = points.get(target-1);
//        queue_forward.add(s);
//        queue_backward.add(t);
//
//        double[] dist_forward = new double[100001];
//        double[] dist_backward = new double[100001];
//        int[] parent_forward = new int[100001];
//        int[] parent_backward = new int[100001];
//        boolean[] settled_forward = new boolean[100001];
//        boolean[] settled_backward = new boolean[100001];
//
//        Arrays.fill(dist_forward, 3000000000.0);
//        Arrays.fill(dist_backward, 3000000000.0);
//
//        dist_forward[source] = 0;
//        dist_backward[target] = 0;
//
//        boolean turnFlag = true;
//        double shortestPathLength = 0;
//        int meetingPoint = 0;
//
//        while(true) {
//
//            if (turnFlag && queue_forward.isEmpty()) continue;
//            if (!turnFlag && queue_backward.isEmpty()) continue;
//            if (queue_forward.isEmpty() && queue_backward.isEmpty()) break;
//
//            if (turnFlag) {
//                Point p = queue_forward.poll();
//                Integer curNode = p.getIndex();
//                while (settled_forward[curNode]) {
//                    p = queue_forward.poll();
//                    curNode = p.getIndex();
//                }
//                settled_forward[curNode] = true;
//
//                if (settled_backward[curNode] == true) {
//                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode]; //-p.getPollution()
//                    meetingPoint = curNode;
//                    break;
//                }
//
//                for(Integer i : neighbors.get(curNode)) {
//                    if (!settled_forward[i]) {
//                        Point neighbor = points.get(i-1);
//                        Double d = p.getHarvesineDistance(neighbor);
//                        if (dist_forward[i] > dist_forward[curNode] + d) {
//                            dist_forward[i] = dist_forward[curNode] + d;
//                            neighbor.setTotalPollution(dist_forward[i]);
//                            parent_forward[i] = curNode;
//                            queue_forward.add(neighbor);
//                        }
//                    }
//                }
//            } else {
//                Point p = queue_backward.poll();
//                Integer curNode = p.getIndex();
//                while (settled_backward[curNode]) {
//                    p = queue_backward.poll();
//                    curNode = p.getIndex();
//                }
//                settled_backward[curNode] = true;
//
//                if (settled_forward[curNode] == true) {
//                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode]; // - p.getPollution();
//                    meetingPoint = curNode;
//                    break;
//                }
//
//                for(Integer i : neighbors.get(curNode)) {
//                    if (!settled_backward[i]) {
//                        Point neighbor = points.get(i-1);
//                        Double d = p.getHarvesineDistance(neighbor);
//                        if (dist_backward[i] > dist_backward[curNode] + d) {
//                            dist_backward[i] = dist_backward[curNode] + d;
//                            neighbor.setTotalPollution(dist_backward[i]);
//                            parent_backward[i] = curNode;
//                            queue_backward.add(neighbor);
//                        }
//                    }
//                }
//            }
//
//            turnFlag = !turnFlag;
//        }
//
//        if (shortestPathLength == 0) return 0;
//        while(!queue_forward.isEmpty()) {
//            Point p = queue_forward.poll();
//            Integer curNode = p.getIndex();
//            for(Integer i : neighbors.get(curNode)) {
//                Point neighbor = points.get(i-1);
//                Double d = p.getHarvesineDistance(neighbor);
//                if (shortestPathLength > dist_forward[curNode] + dist_backward[i] + d) {
//                    shortestPathLength = dist_forward[curNode] + dist_backward[i] + d;
//                    parent_forward[i] = curNode;
//                    meetingPoint = i;
//                }
//            }
//        }
//        while(!queue_backward.isEmpty()) {
//            Point p = queue_backward.poll();
//            Integer curNode = p.getIndex();
//            for(Integer i : neighbors.get(curNode)) {
//                Point neighbor = points.get(i-1);
//                Double d = p.getHarvesineDistance(neighbor);
//                if (shortestPathLength > dist_backward[curNode] + dist_forward[i] + d) {
//                    shortestPathLength = dist_backward[curNode] + dist_forward[i] + d;
//                    parent_forward[i] = curNode;
//                    meetingPoint = i;
//                }
//            }
//        }
//
//        routeString = saveRouteNodesReversed(points, meetingPoint, parent_backward) + GraphNetwork.saveRouteNodes(points, meetingPoint, parent_forward);
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("Execution time: " + (endTime-startTime));
//
//        return shortestPathLength;
//
//    }
//
//    public static double bidirectionalDijkstraByPollution() {
//
//        long startTime = System.currentTimeMillis();
//
//        PriorityQueue<Point> queue_forward = new PriorityQueue<>(11, new Comparator<Point>() {
//            public int compare(Point p1, Point p2) {
//                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
//            }
//        });
//        PriorityQueue<Point> queue_backward = new PriorityQueue<>(11, new Comparator<Point>() {
//            public int compare(Point p1, Point p2) {
//                return Double.compare(p1.getTotalPollution(), p2.getTotalPollution());
//            }
//        });
//        Point s = points.get(source-1);
//        Point t = points.get(target-1);
//        queue_forward.add(s);
//        queue_backward.add(t);
//
//        double[] dist_forward = new double[100001];
//        double[] dist_backward = new double[100001];
//        int[] parent_forward = new int[100001];
//        int[] parent_backward = new int[100001];
//        boolean[] settled_forward = new boolean[100001];
//        boolean[] settled_backward = new boolean[100001];
//
//        Arrays.fill(dist_forward, 3000000000.0);
//        Arrays.fill(dist_backward, 3000000000.0);
//
//        dist_forward[source] = s.getPollution();
//        dist_backward[target] = t.getPollution();
//
//        boolean turnFlag = true;
//        double shortestPathLength = 0;
//        int meetingPoint = 0;
//
//        while(true) {
//
//            if (turnFlag && queue_forward.isEmpty()) continue;
//            if (!turnFlag && queue_backward.isEmpty()) continue;
//            if (queue_forward.isEmpty() && queue_backward.isEmpty()) break;
//
//            if (turnFlag) {
//                Point p = queue_forward.poll();
//                Integer curNode = p.getIndex();
//                while (settled_forward[curNode]) {
//                    p = queue_forward.poll();
//                    curNode = p.getIndex();
//                }
//                settled_forward[curNode] = true;
//
//                if (settled_backward[curNode] == true) {
//                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode] - p.getPollution();
//                    meetingPoint = curNode;
//                    break;
//                }
//
//                for(Integer i : neighbors.get(curNode)) {
//                    if (!settled_forward[i]) {
//                        Point neighbor = points.get(i-1);
//                        Double d = neighbor.getPollution();
//                        if (dist_forward[i] > dist_forward[curNode] + d) {
//                            dist_forward[i] = dist_forward[curNode] + d;
//                            neighbor.setTotalPollution(dist_forward[i]);
//                            parent_forward[i] = curNode;
//                            queue_forward.add(neighbor);
//                        }
//                    }
//                }
//            } else {
//                Point p = queue_backward.poll();
//                Integer curNode = p.getIndex();
//                while (settled_backward[curNode]) {
//                    p = queue_backward.poll();
//                    curNode = p.getIndex();
//                }
//                settled_backward[curNode] = true;
//
//                if (settled_forward[curNode] == true) {
//                    shortestPathLength = dist_forward[curNode] + dist_backward[curNode] - p.getPollution();
//                    meetingPoint = curNode;
//                    break;
//                }
//
//                for(Integer i : neighbors.get(curNode)) {
//                    if (!settled_backward[i]) {
//                        Point neighbor = points.get(i-1);
//                        Double d = neighbor.getPollution();
//                        if (dist_backward[i] > dist_backward[curNode] + d) {
//                            dist_backward[i] = dist_backward[curNode] + d;
//                            neighbor.setTotalPollution(dist_backward[i]);
//                            parent_backward[i] = curNode;
//                            queue_backward.add(neighbor);
//                        }
//                    }
//                }
//            }
//
//            turnFlag = !turnFlag;
//        }
//
//        if (shortestPathLength == 0) return 0;
//        while(!queue_forward.isEmpty()) {
//            Point p = queue_forward.poll();
//            Integer curNode = p.getIndex();
//            for(Integer i : neighbors.get(curNode)) {
//                if (shortestPathLength > dist_forward[curNode] + dist_backward[i]) {
//                    shortestPathLength = dist_forward[curNode] + dist_backward[i];
//                    parent_forward[i] = curNode;
//                    meetingPoint = i;
//                }
//            }
//        }
//        while(!queue_backward.isEmpty()) {
//            Point p = queue_backward.poll();
//            Integer curNode = p.getIndex();
//            for(Integer i : neighbors.get(curNode)) {
//                if (shortestPathLength > dist_backward[curNode] + dist_forward[i]) {
//                    shortestPathLength = dist_backward[curNode] + dist_forward[i];
//                    parent_forward[i] = curNode;
//                    meetingPoint = i;
//                }
//            }
//        }
//
//        routeString = saveRouteNodesReversed(points, meetingPoint, parent_backward) + GraphNetwork.saveRouteNodes(points, meetingPoint, parent_forward);
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("Execution time: " + (endTime-startTime));
//
//        return shortestPathLength;
//
//    }

    public static Route bidirectionalAStar() {

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

        double[] pol_forward = new double[100001];
        double[] pol_backward = new double[100001];
        double[] dist_forward = new double[100001];
        double[] dist_backward = new double[100001];
        int[] parent_forward = new int[100001];
        int[] parent_backward = new int[100001];
        boolean[] settled_forward = new boolean[100001];
        boolean[] settled_backward = new boolean[100001];

        Arrays.fill(pol_forward, 3000000000.0);
        Arrays.fill(pol_backward, 3000000000.0);

        pol_forward[source] = s.getPollution();
        pol_backward[target] = t.getPollution();
        dist_forward[source] = 0;
        dist_backward[target] = 0;

        boolean turnFlag = true;
        double leastPollution = 0;
        double distance = 0;
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
                    leastPollution = pol_forward[curNode] + pol_backward[curNode] - p.getPollution();
                    distance = dist_forward[curNode] + dist_backward[curNode];
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_forward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = neighbor.getPollution();
                        if (pol_forward[i] > pol_forward[curNode] + d) {
                            pol_forward[i] = pol_forward[curNode] + d;
                            dist_forward[i] = dist_forward[curNode] + neighbor.getHarvesineDistance(p);
                            neighbor.setTotalPollution(pol_forward[i] + 70 * neighbor.getHarvesineDistance(t));
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
                    leastPollution = pol_forward[curNode] + pol_backward[curNode] - p.getPollution();
                    distance = dist_forward[curNode] + dist_backward[curNode];
                    meetingPoint = curNode;
                    break;
                }

                for(Integer i : neighbors.get(curNode)) {
                    if (!settled_backward[i]) {
                        Point neighbor = points.get(i-1);
                        Double d = neighbor.getPollution();
                        if (pol_backward[i] > pol_backward[curNode] + d) {
                            pol_backward[i] = pol_backward[curNode] + d;
                            dist_backward[i] = dist_backward[curNode] + neighbor.getHarvesineDistance(p);
                            neighbor.setTotalPollution(pol_backward[i] + 70 * neighbor.getHarvesineDistance(s));
                            parent_backward[i] = curNode;
                            queue_backward.add(neighbor);
                        }
                    }
                }
            }

            turnFlag = !turnFlag;
        }

        if (leastPollution == 0) return null;
        while(!queue_forward.isEmpty()) {
            Point p = queue_forward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                if (leastPollution > pol_forward[curNode] + pol_backward[i]) {
                    leastPollution = pol_forward[curNode] + pol_backward[i];
                    distance = dist_forward[curNode] + dist_backward[curNode];
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }
        while(!queue_backward.isEmpty()) {
            Point p = queue_backward.poll();
            Integer curNode = p.getIndex();
            for(Integer i : neighbors.get(curNode)) {
                if (leastPollution > pol_backward[curNode] + pol_forward[i]) {
                    leastPollution = pol_backward[curNode] + pol_forward[i];
                    distance = dist_forward[curNode] + dist_backward[curNode];
                    parent_forward[i] = curNode;
                    meetingPoint = i;
                }
            }
        }

        routePoints = new ArrayList<Point>();
        String routeString = saveRouteNodesReversed(points, meetingPoint, parent_backward) + GraphNetwork.saveRouteNodes(points, meetingPoint, parent_forward);

        Route route = new Route(routePoints, routeString, distance, leastPollution);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time Bidirectional A*: " + (endTime-startTime));

        return route;


    }

    public static String saveRouteNodes (List<Point> points, int target, int[] parent) {

        String str = "";
        int curNode = target;
        while (curNode != 0) {
            str += points.get(curNode-1).getLatitute() + " " + points.get(curNode-1).getLongitude() + "\n";
            routePoints.add(points.get(curNode-1));
            curNode = parent[curNode];
        }
        return str;

    }

    public static String saveRouteNodesReversed (List<Point> points, int target, int[] parent) {

        String str = "";
        int curNode = parent[target];
        while (curNode != 0) {
            str = points.get(curNode-1).getLatitute() + " " + points.get(curNode-1).getLongitude() + "\n" + str;
            routePoints.add(0, points.get(curNode-1));
            curNode = parent[curNode];
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

    public static void initialiseGraph(ServletContext context, String pollutionGrid) {

        DataIO.setContext(context);

        points = DataIO.readPointsWithID();
        //points = DataIO.readPointsWithPollution();
        //points = DataIO.generatePollutionAccordingToRoads(points);
        if (pollutionGrid == null || pollutionGrid.isEmpty() || pollutionGrid.equals("null")) {
            //points = DataIO.generatePollutionAccordingToRoads(points);
        } else {
            List<Point> gridPoints = DataIO.getPointsFromRawGrid(pollutionGrid);
            KDTree gridTree = new KDTree(gridPoints, true);
            points = DataIO.getPollutionFromGridTree(points, gridTree);

        }
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

    public static String findRoute(double lat1, double lng1, double lat2, double lng2) {

        Point s = kdTree.findNearest(lat1, lng1, true);
        Point t = kdTree.findNearest(lat2, lng2, true);
        source = s.getIndex();
        target = t.getIndex();

        GraphNetwork.setSource(source);
        GraphNetwork.setTarget(target);

        routePoints = new ArrayList<>();
        Route shortestRoute = GraphNetwork.dijkstraByDistance();
        //Route anotherRoute = GraphNetwork.aStar();
        Route leastPollutedRoute = GraphNetwork.bidirectionalAStar();


        System.out.println("LEAST POLLUTED ROUTE");
        System.out.println("pollution: " + leastPollutedRoute.getPollution());
        System.out.println("distance: " + leastPollutedRoute.getDist());
        System.out.println("SHORTEST ROUTE");
        System.out.println("pollution: " + shortestRoute.getPollution());
        System.out.println("distance: " + shortestRoute.getDist());

        return leastPollutedRoute.getRouteString() + "X" + shortestRoute.getRouteString();

       // System.out.println("Nodes:" + routeNodesNum);
       // System.out.println("Harvesine:" + s.getHarvesineDistance(t));

        // print source, target and distances between actual user input points
//        System.out.println(lat1 + " " + lng1 + " " + lat2 + " " + lng2);
//        System.out.println(s.getLatitute() + " " + s.getLongitude() + " " + t.getLatitute() + " " + t.getLongitude());
//        System.out.println((lat1-s.getLatitute())*(lat1-s.getLatitute())+(lng1-s.getLongitude())*(lng1-s.getLongitude()) + " " + (lat2-t.getLatitute())*(lat2-t.getLatitute())+(lng2-t.getLongitude())*(lng2-t.getLongitude()));

    }

    public static String getRoutesFromHomesToUniAndAirspeckPositions(String str, String airspecksNum) {

        allowedAirspecks = Integer.parseInt(airspecksNum);

        separateRoutes.clear();
        airspeckPositions.clear();

        String routes = "";

        Point library = kdTree.findNearest(55.942856, -3.189039, true);
        source = library.getIndex();

        str = str.substring(1, str.length()-1);
        String[] nums = str.split(",");
        double maxDist = 0;

        for(int i=0;i<nums.length;i+=2) {
            double lat = Double.parseDouble(nums[i]);
            double lng = Double.parseDouble(nums[i+1]);
            Point home = kdTree.findNearest(lat, lng, true);
            double dist = home.getHarvesineDistance(library);
            if (dist > maxDist) maxDist = dist;
            target = home.getIndex();
            GraphNetwork.setSource(source);
            GraphNetwork.setTarget(target);
            Route r = GraphNetwork.bidirectionalAStar();
            routes += r.getRouteString() + "@";
            Collections.reverse(routePoints);
            separateRoutes.add(routePoints);
        }

        circleRadius = maxDist/2;
        circleCenterX = library.getLatitute();
        circleCenterY = library.getLongitude();

        String airspecks = findAirspeckPositions(separateRoutes);
        return routes.substring(0,routes.length()-1) + "X" + airspecks;

    }

    public static String findAirspeckPositions (ArrayList<ArrayList<Point>> routes) {

        int n = routes.size();

        List<Integer> a = new ArrayList<Integer>();
        for(int i=0;i<n;i++) {
            a.add(i);
        }

        recSplit(0, a);

        airspecksTree = new KDTree(airspeckPositions, true);

        double minDist = 0;
        for(int angle = 0; angle < 360/allowedAirspecks; angle += 10) {

            List<Point> currentAirspecks = new ArrayList<>();
            double curDist = 0;
            for (int i = 0; i < allowedAirspecks ; i++){
                double x = circleCenterX + circleRadius * Math.cos(2 * Math.PI * i / allowedAirspecks + angle);
                double y = circleCenterY + circleRadius * Math.sin(2 * Math.PI * i / allowedAirspecks + angle);
                Point p = airspecksTree.findNearest(x, y, true);
                curDist += Math.sqrt((x - p.getLatitute())*(x - p.getLatitute()) + (y - p.getLongitude())*(y - p.getLongitude()));
                currentAirspecks.add(p);
            }
            if (minDist == 0 || minDist > curDist) {minDist = curDist; finalAirspecks = currentAirspecks;}

        }

        String str = "";
        for (Point p : finalAirspecks) {
            str += p.getLatitute() + " " + p.getLongitude() + "\n";
        }

        return str;
    }

    public static void recSplit (int pos, List<Integer> routes) {

        if (routes.size() == 1) return;

        short[] used = new short[1024];
        for (int i=0; i<routes.size(); i++)
            used[i] = 0;

        while(true) {

            pos++;

            boolean breakpoint = false;
            List<Integer> a = new ArrayList<Integer>();
            for(int i=0; i<routes.size(); i++) {
                if (separateRoutes.get(routes.get(i)).size() != pos) {
                    a.add(i);
                } else {
                    breakpoint = true;
                }
            }
            if (breakpoint) {
                Point curP = separateRoutes.get(routes.get(0)).get(pos-1);
                curP.setCrossingPaths(1);
                airspeckPositions.add(curP);
                recSplit(pos, a);
                break;
            }

            boolean crossing = false;

            for (int i=0; i<routes.size(); i++) {
                for (int j=i+1; j<routes.size(); j++) {
                    if (separateRoutes.get(routes.get(i)).get(pos).getIndex() != separateRoutes.get(routes.get(j)).get(pos).getIndex()) {
                        crossing = true;
                        break;
                    }
                    if (crossing) break;
                }
            }

            if (!crossing) continue;

            int cnt=0;
            for (int i=0; i<routes.size(); i++) {
                if (used[i] != 0) continue;
                cnt++;
                a.clear();
                a.add(routes.get(i));
                for (int j=i+1; j<routes.size(); j++) {
                    if (separateRoutes.get(routes.get(i)).get(pos).getIndex() == separateRoutes.get(routes.get(j)).get(pos).getIndex()) {
                        used[j] = 1;
                        a.add(routes.get(j));
                    }
                }
                recSplit(pos, a);
            }

            Point curP = separateRoutes.get(routes.get(0)).get(pos-1);
            curP.setCrossingPaths(cnt);
            airspeckPositions.add(curP);

            break;

        }

    }

}