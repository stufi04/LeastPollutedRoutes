/**
 * Created by st.ivanov44 on 29/09/2017.
 */
public class Point {

    private int index, id;
    private double latitute, longitude, pollution;
    private double totalPollution;

    Point (double latitute, double longitude, double pollution) {
        this.latitute = latitute;
        this.longitude = longitude;
        this.pollution = pollution;
    }

    public double getDistance(Point p) {
        return Math.sqrt(getSquaredDistance(p));

    }

    public double getSquaredDistance(Point p) {

        double x = latitute - p.getLatitute();
        double y = longitude - p.getLongitude();
        return x*x + y*y;

    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPollution() {
        return pollution;
    }

    public void setPollution(double pollution) {
        this.pollution = pollution;
    }

    public double getTotalPollution() {
        return totalPollution;
    }

    public void setTotalPollution(double totalPollution) {
        this.totalPollution = totalPollution;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
