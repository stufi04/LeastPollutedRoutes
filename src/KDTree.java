import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by st.ivanov44 on 29/10/2017.
 */
public class KDTree {

    public int numNodes;
    public double division;
    public Point median;
    public KDTree leftChild = null, rightChild = null;

    KDTree(List<Point> nodes, boolean axis) {

        int n = nodes.size();
        numNodes = n;

        //System.out.println(n + " nodes at current level");

        if (n==0) return;
        if (n == 1) {
            median = nodes.get(0);
            return;
        }

        // get up to 20 random nodes to estimate median from
        List<Point> randomNodes = new ArrayList<>();
        for(int i=1; i<=Math.min(n, 20); i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, n);
            if (n<=20) randomNum = i-1;
            randomNodes.add(nodes.get(randomNum));
        }

        // sort those according to the proper axis
        if (axis) {
            Collections.sort(randomNodes, new Comparator<Point>() {
                public int compare(Point p1, Point p2) {
                    return Double.compare(p1.getLatitute(), p2.getLatitute());
                }
            });
        } else {
            Collections.sort(randomNodes, new Comparator<Point>() {
                public int compare(Point p1, Point p2) {
                    return Double.compare(p1.getLongitude(), p2.getLongitude());
                }
            });
        }

        // get median and border
        median = randomNodes.get(randomNodes.size()/2);
        if (axis) division = median.getLatitute();
        else division = median.getLongitude();

        // divide points according to median
        List<Point> leftNodes = new ArrayList<>(), rightNodes = new ArrayList<>();
        for(int i=0; i<n; i++) {
            if (axis) {
                if (nodes.get(i).getLatitute() < division) leftNodes.add(nodes.get(i));
                else rightNodes.add(nodes.get(i));
            } else {
                if (nodes.get(i).getLongitude() < division) leftNodes.add(nodes.get(i));
                else rightNodes.add(nodes.get(i));
            }
        }

        if (leftNodes.size() + rightNodes.size() != nodes.size()) {
            System.out.println("FUCK");
        }

        // recursively create child nodes
        leftChild = new KDTree(leftNodes, !axis);
        rightChild = new KDTree(rightNodes, !axis);

    }


    public Point findNearest(double lat, double lng, boolean axis) {

        if (numNodes == 1) {
            return median;
        }

        if (leftChild == null || leftChild.numNodes == 0) {
            return rightChild.findNearest(lat, lng, !axis);
        }
        if (rightChild == null || rightChild.numNodes == 0) {
            return leftChild.findNearest(lat, lng, !axis);
        }

        double coordinate, distanceToBorder;
        Point curNearest, newNearest;
        if (axis) {
            coordinate = lat;
        } else {
            coordinate = lng;
        }
        distanceToBorder = (coordinate - division) * (coordinate - division);

        KDTree firstAttempt = leftChild, secondAttempt = rightChild;
        if (coordinate >= division) {
            firstAttempt = rightChild;
            secondAttempt = leftChild;
        }

        curNearest = firstAttempt.findNearest(lat, lng, !axis);
        double curBest = (lat - curNearest.getLatitute())*(lat - curNearest.getLatitute()) + (lng - curNearest.getLongitude()) * (lng - curNearest.getLongitude());
        if (distanceToBorder <= curBest) {
            newNearest = secondAttempt.findNearest(lat, lng, !axis);
            double newBest = (lat - newNearest.getLatitute())*(lat - newNearest.getLatitute()) + (lng - newNearest.getLongitude())*(lng - newNearest.getLongitude());
            if (newBest < curBest) {
                curNearest = newNearest;
            }
        }

        return curNearest;

    }



}
