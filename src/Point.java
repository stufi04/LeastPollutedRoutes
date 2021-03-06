/**
 * Created by st.ivanov44 on 29/09/2017.
 */
public class Point {

    private int index, id, crossingPaths;
    private double latitute, longitude, pollution;
    private double totalPollution;
    public static int[][] fakeDistances = new int[16][16];

    Point (double latitute, double longitude, double pollution) {
        this.latitute = latitute;
        this.longitude = longitude;
        this.pollution = pollution;
    }

    public Double getHarvesineDistance(Point p) {
        final int R = 6371; // Radius of the earth
        Double lat1 = latitute;
        Double lon1 = longitude;
        Double lat2 = p.getLatitute();
        Double lon2 = p.getLongitude();
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        return distance;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    /*public double getDistance(Point p) {
        return Math.sqrt(getSquaredDistance(p));

    }

    public double getSquaredDistance(Point p) {

        double x = latitute - p.getLatitute();
        double y = longitude - p.getLongitude();
        return x*x + y*y;

    }*/

    public double getFakeDistance(Point p) {
        return fakeDistances[this.getIndex()][p.getIndex()];
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

    public int getCrossingPaths() {
        return crossingPaths;
    }

    public void setCrossingPaths(int crossingPaths) {
        this.crossingPaths = crossingPaths;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPollutionAccordingToRoad(String r) {
        switch (r) {
            case "motorway":case "motorway_link":case "raceway":case "construction":
                this.setPollution(90);
                break;
            case "trunk":case "trunk_link":
                this.setPollution(80);
                break;
            case "primary":case "primary_link":
                this.setPollution(70);
                break;
            case "secondary":case "secondary_link":
                this.setPollution(60);
                break;
            case "tertiary":case "tertiary_link":
                this.setPollution(50);
                break;
            case "unclassified":case "bus_guideway":
                this.setPollution(40);
                break;
            case "residential":
                this.setPollution(25);
                break;
            case "service":case "escape":
                this.setPollution(25);
                break;
            case "living_street":
                this.setPollution(15);
                break;
            case "pedestrian":
                this.setPollution(10);
                break;
            case "footway":case "bridleway":case "steps":
                this.setPollution(5);
                break;
            case "path":case "cycleway":
                this.setPollution(3);
                break;
            case "track":
                this.setPollution(1);
                break;
            default:
                this.setPollution(100);

        }
    }

}
