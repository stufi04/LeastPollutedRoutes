import javax.servlet.ServletContext;
import javax.xml.crypto.Data;
import java.util.*;

/**
 * Created by st.ivanov44 on 29/09/2017.
 */
public final class GraphNetwork {

    private static List<Point> points = new ArrayList<>();
    private static Map<Integer, List<Integer>> neighbors = new HashMap<Integer, List<Integer>>();
    private static int source, target;
    public static String route;

    public static void setSource (int s) {
        source = s;
    }

    public static void setTarget (int t) {
        target = t;
    }

    public static String getRoute() {
        return route;
    }


    public static double dijkstraByPollution() {

        PriorityQueue<Point> queue = new PriorityQueue<>(11, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                if (p1.getTotalPollution() < p2.getTotalPollution()) return 0;
                else return 1;
            }
        });
        queue.add(points.get(source));

        double[] pollution = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(pollution, 3000000000.0);

        pollution[source] = points.get(source).getPollution();

        while(!queue.isEmpty()) {

            Point p = queue.poll();
            Integer curNode = p.getIndex();
            settled[curNode] = true;

            if (curNode == target) break;

            for(Integer i : neighbors.get(curNode)) {
                if (!settled[i]) {
                    if (pollution[i] > pollution[curNode]+points.get(i).getPollution()) {
                        pollution[i] = pollution[curNode]+points.get(i).getPollution();
                        parent[i] = curNode;
                        queue.add(points.get(i));
                    }
                }
            }

        }

        route = GraphNetwork.saveRouteNodes(points, target, parent);
        return pollution[target];

    }

    public static double dijkstraByDistance() {

        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.add(source);

        double[] dist = new double[100001];
        int[] parent = new int[100001];
        boolean[] settled = new boolean[100001];

        Arrays.fill(dist, 3000000000.0);

        dist[source] = 0;

        while(!queue.isEmpty()) {

            Integer curNode = queue.poll();
            settled[curNode] = true;

            if (curNode == target) break;

            for(Integer i : neighbors.get(curNode)) {
                if (!settled[i]) {
                    double d = points.get(curNode).getDistance(points.get(i));
                    if (dist[i] > dist[curNode] + d) {
                        dist[i] = dist[curNode] + d;
                        parent[i] = curNode;
                        queue.add(i);
                    }
                }
            }

        }

        route = GraphNetwork.saveRouteNodes(points, target, parent);
        return dist[target];

    }

    public static String saveRouteNodes (List<Point> points, int target, int[] parent) {

        String str = "";
        int curNode = target;
        while (curNode != 0) {
            str += points.get(curNode).getLongitude() + " " + points.get(curNode).getLatitute() + "\n";
            curNode = parent[curNode];
        }
        return str;

    }


    //public static void main(String[] args) {

    public static void initialiseGraph(ServletContext context) {

        DataIO.setContext(context);

        points = DataIO.readPointsWithID();
        neighbors = DataIO.readAdjacencyList();
        GraphNetwork.setSource(3554);
        GraphNetwork.setTarget(12212);

        //print the list of neighbors

//        for (Integer i : graphNetwork.neighbors.keySet()) {
//            Point p = graphNetwork.points.get(i);
//            System.out.print( p.getLatitute() + ", " + p.getLongitude() + ": ");
//            for (Integer j : graphNetwork.neighbors.get(i)) {
//                Point neighbor = graphNetwork.points.get(j);
//                System.out.print("(" + neighbor.getLatitute() + ", " + neighbor.getLongitude() + ") ");
//            }
//            System.out.println("");
//        }

        double leastPollution = GraphNetwork.dijkstraByPollution();

        //System.out.println(leastPollution);

        /*String csvFile = "data/data1.csv";
        graphNetwork.readCSV(csvFile);

        graphNetwork.createGeoJSON();

        for(Point p : graphNetwork.points) {
            System.out.println("(" + p.getLatitute() + ", " + p.getLongitude() + "), pollution: " + p.getPollution());
        }*/
    }

}