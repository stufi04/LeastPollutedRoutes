import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;

/**
 * Created by st.ivanov44 on 28/10/2017.
 */
public final class DataIO {

    public static ServletContext context;

    public static void setContext (ServletContext context) {
        DataIO.context = context;
    }

    public static List<Point> readPointsWithPollution() {

        List<Point> points = new ArrayList<>();

        try {
            InputStream input = context.getResourceAsStream("/WEB-INF/data/points1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int i = 0;

            while (line != null) {
                i++;
                String[] nums = line.split("\\s+");
                double latitude = Double.parseDouble(nums[0]);
                double longitude = Double.parseDouble(nums[1]);
                double pollution = Double.parseDouble(nums[2]);
                Point p = new Point(latitude, longitude, pollution);
                p.setIndex(i);
                points.add(p);
                line = br.readLine();
            }
        } catch (Exception e) {
        }

        return points;
    }

    public static List<Point> readPointsWithID() {

        List<Point> points = new ArrayList<>();

        try {

            Random r = new Random();

            InputStream input = context.getResourceAsStream("/WEB-INF/data/nodes1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            String line = br.readLine();
            int i = 0;

            while (line != null) {

                i++;
                String[] nums = line.split("\\s+");
                double latitude = Double.parseDouble(nums[2]);
                double longitude = Double.parseDouble(nums[1]);
                //double pollution = 2;                                                      // same pollution everywhere
                //if (longitude < -3.194139 || longitude > -3.187981) pollution = 30;        // a less polluted horizontal strip
                //if (latitude < 55.943608 || latitude > 55.945963) pollution = 30;          // a less polluted vertical strip
                //double pollution = 30 * r.nextDouble();                                    // random pollution everytime
                //double pollution = i%30;                                                   // semi-random pollution that doesn't change
                double pollution = 100;                                                      // initial big pollution, to be used with road properties extracted from OSM
                Point p = new Point(latitude, longitude, pollution);
                p.setIndex(i);
                points.add(p);
                line = br.readLine();
            }

        } catch (Exception e) {
        }

        return points;
    }

    public static List<Point> generatePollutionAccordingToRoads(List<Point> points) {

        try {

            InputStream input = context.getResourceAsStream("/WEB-INF/data/roads.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            String line = br.readLine();

            while (line != null) {

                String[] nums = line.split("\\s+");
                String roadType = nums[0];

                for(int i=1;i<nums.length;i++) {
                    int node = Integer.parseInt(nums[i]);
                    points.get(node-1).setPollutionAccordingToRoad(roadType);
                }

                line = br.readLine();
            }

        } catch (Exception e) {
        }

        return points;

    }

    public static Map<Integer, List<Integer>> readEdges() {

        Map<Integer, List<Integer>> neighbors = new HashMap<Integer, List<Integer>>();

        try {
            InputStream input = context.getResourceAsStream("/WEB-INF/data/edges1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                String[] nums = line.split("\\s+");
                int p1 = Integer.parseInt(nums[0]);
                int p2 = Integer.parseInt(nums[1]);
                if (!neighbors.containsKey(p1)) {
                    neighbors.put(p1, new ArrayList<Integer>());
                }
                if (!neighbors.containsKey(p2)) {
                    neighbors.put(p2, new ArrayList<Integer>());
                }
                neighbors.get(p1).add(p2);
                neighbors.get(p2).add(p1);
                line = br.readLine();
            }
        } catch (Exception e) {
        }

        return neighbors;
    }

    public static Map<Integer, List<Integer>> readAdjacencyList() {

        Map<Integer, List<Integer>> neighbors = new HashMap<Integer, List<Integer>>();

        try {
            InputStream input = context.getResourceAsStream("/WEB-INF/data/adjacency1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int i = 0;

            while (line != null) {
                i++;
                neighbors.put(i, new ArrayList<Integer>());
                String[] divided = line.split(":");
                divided[1] = divided[1].substring(1);
                String[] nums = divided[1].split("\\s+");
                for (String num : nums) {
                    int p = Integer.parseInt(num);
                    neighbors.get(i).add(p);
                }
                line = br.readLine();
            }
        } catch (Exception e) {
        }

        return neighbors;
    }

    /*public static void readFakeDistances() {

        try {
            InputStream input = context.getResourceAsStream("/WEB-INF/data/fakedistances.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                String[] nums = line.split("\\s+");
                int p1 = Integer.parseInt(nums[0]);
                int p2 = Integer.parseInt(nums[1]);
                int d = Integer.parseInt(nums[2]);
                Point.fakeDistances[p1][p2] = d;
                Point.fakeDistances[p2][p1] = d;
                line = br.readLine();
            }
        } catch (Exception e) {
        }

    }*/

    public static List<Point> readCSV(String csvFile) {

        List<Point> points = new ArrayList<>();

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                points.add(new Point(Double.parseDouble(data[24]), Double.parseDouble(data[23]), Double.parseDouble(data[3])));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }

    /*public static void saveRouteNodes (List<Point> points, int target, int[] parent) {

        String str = "";
        int curNode = target;
        while (curNode != 0) {
            str += points.get(curNode).getLongitude() + " " + points.get(curNode).getLatitute() + "\n";
            curNode = parent[curNode];
        }
        try{
            String path = context.getRealPath("WEB-INF/data/route.txt");
            File file = new File(path);
            PrintWriter writer = new PrintWriter(file);
            writer.println(str);
            writer.close();
        } catch (IOException e) {
        }

    }*/


    public static void createGeoJSON(List<Point> points) {

        String geojson = "eqfeed_callback({\"type\":\"FeatureCollection\",\"features\":[";
        boolean firstPoint = true;

        for (Point p : points) {

            if (Double.isNaN(p.getLatitute()) || Double.isNaN(p.getLongitude())) continue;

            if (!firstPoint) geojson += ", ";
            else firstPoint = false;

            geojson += "{\"type\": \"Feature\", \"geometry\": ";
            geojson += "{\"type\": \"Point\", \"coordinates\": [";
            geojson += p.getLongitude() + ", " + p.getLatitute() + "]}}";
        }

        geojson += "]});";

        try{
            PrintWriter writer = new PrintWriter("WEB-INF/data/geoJSON1.js", "UTF-8");
            writer.println(geojson);
            writer.close();
        } catch (IOException e) {
        }

    }

    public static List<Point> getPointsFromRawGrid (String pollutionGrid) {

        List<Point> gridPoints = new ArrayList<>();
        pollutionGrid = pollutionGrid.substring(2, pollutionGrid.length()-2);
        String[] nums = pollutionGrid.split("\\],\\[");
        for (int i = 0; i<nums.length; i++) {
            String[] curNums = nums[i].split(",");
            double latitude = Double.parseDouble(curNums[0]);
            double longitude = Double.parseDouble(curNums[1]);
            double pollution = Double.parseDouble(curNums[2]);
            Point p = new Point(latitude, longitude, pollution);
            gridPoints.add(p);
        }
        return gridPoints;

    }

    public static List<Point> getPollutionFromGridTree (List<Point> points, KDTree gridTree) {
        for (Point p : points) {
            Point nearest = gridTree.findNearest(p.getLatitute(), p.getLongitude(), true);
            p.setPollution(nearest.getPollution());
        }
        return points;
    }
}
