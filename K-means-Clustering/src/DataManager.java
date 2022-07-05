import java.io.*;
import java.util.*;

public class DataManager {

    static class Record {
        HashMap<String, Double> record;
        Integer clusterNo;

        public Record(HashMap<String, Double> record){
            this.record = record;
        }

        public void setClusterNo(int clusterNo) {
            this.clusterNo = clusterNo;
        }

        public HashMap<String, Double> getRecord() {
            return record;
        }
    }

    private final LinkedList<String> attrNames = new LinkedList<>();
    public final LinkedList<Record> records = new LinkedList<>();
    private final LinkedList<Integer> indicesOfCentroids = new LinkedList<>();
    private final HashMap<String, Double> minimums = new HashMap<>();
    private final HashMap<String, Double> maximums = new HashMap<>();
    private static final Random random = new Random();

    public DataManager(String csvFileName) throws IOException {
        String row;
        try(BufferedReader reader = new BufferedReader(new FileReader(csvFileName))) {

            if((row = reader.readLine()) != null){
                String[] data = row.trim().split("\\s+");
                Collections.addAll(attrNames, data);
            }

            while ((row = reader.readLine()) != null) {
                String[] data = row
                        .trim()
                        .replace("Iris-setosa", "10")
                        .replace("Iris-versicolor", "20")
                        .replace("Iris-virginica", "30")
                        .replace(",", ".")
                        .split("\\s+");

                HashMap<String, Double> record = new HashMap<>();

                if(attrNames.size() == data.length) {
                    for (int i = 0; i < attrNames.size(); i++) {
                        String name = attrNames.get(i);
                        double val = Double.parseDouble(data[i]);
                        record.put(name, val);
                        updateMin(name, val);
                        updateMax(name, val);
                    }
                } else{
                    throw new IOException("Incorrect file reading");
                }

                records.add(new Record(record));
            }

        }
    }

    public void createOutputFile(String outputFileName){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for(int i = 0; i < attrNames.size(); i++){
                writer.write(attrNames.get(i));
                writer.write("          ");
            }

            writer.write("ID_Cluster");
            writer.write("\n");

            for(var record : records){
                for(int i = 0; i < attrNames.size(); i++) {
                    writer.write(record.getRecord().get(attrNames.get(i)).toString()
                            .replace("10.0", "Iris-setosa    ")
                            .replace("20.0", "Iris-versicolor")
                            .replace("30.0", "Iris-virginica "));
                    writer.write("   |   ");
                }
                writer.write(String.valueOf(record.clusterNo));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMin(String name, Double val){
        if(minimums.containsKey(name)){
            if(val < minimums.get(name)){
                minimums.put(name, val);
            }
        } else{
            minimums.put(name, val);
        }
    }

    private void updateMax(String name, Double val){
        if(maximums.containsKey(name)){
            if(val > maximums.get(name)){
                maximums.put(name, val);
            }
        } else{
            maximums.put(name, val);
        }
    }

    public Double meanOfAttr(String attrName, LinkedList<Integer> indices){
        Double sum = 0.0;
        for(int i : indices){
            if(i < records.size()){
                sum += records.get(i).getRecord().get(attrName);
            }
        }
        return sum / indices.size();
    }

    public HashMap<String, Double> calculateCentroid(int clusterNo){
        HashMap<String, Double> centroid = new HashMap<>();

        LinkedList<Integer> recsInCluster = new LinkedList<>();
        for(int i=0; i < records.size(); i++){
            var record = records.get(i);
            if(record.clusterNo == clusterNo){
                recsInCluster.add(i);
            }
        }

        for(String name : attrNames){
            centroid.put(name, meanOfAttr(name, recsInCluster));
        }
        return centroid;
    }

    public LinkedList<HashMap<String,Double>> recomputeCentroids(int K){
        LinkedList<HashMap<String,Double>> centroids = new LinkedList<>();
        for(int i = 0; i < K; i++){
            centroids.add(calculateCentroid(i));
        }
        return centroids;
    }

    public static Double euclideanDistance(HashMap<String, Double> a, HashMap<String, Double> b){
        if(!a.keySet().equals(b.keySet())){
            return Double.POSITIVE_INFINITY;
        }
        double sum = 0.0;
        for(String attrName : a.keySet()){
            sum += Math.pow(a.get(attrName) - b.get(attrName), 2);
        }
        return Math.sqrt(sum);
    }

    public Double calculateClusterSSE(HashMap<String, Double> centroid, int clusterNo){
        double SSE = 0.0;
        int groupCounter = 0;
        for(int i = 0; i < records.size(); i++){
            if(records.get(i).clusterNo == clusterNo){
                groupCounter++;
                SSE += Math.pow(euclideanDistance(centroid, records.get(i).getRecord()), 2);
            }
        }
        System.out.println("Cluster ID: "+clusterNo+" SSE: "+SSE+" Quantity: "+groupCounter);
        return SSE;
    }

    public Double calculateTotalSSE(LinkedList<HashMap<String,Double>> centroids){
        Double SSE = 0.0;
        for(int i = 0; i < centroids.size(); i++) {
            SSE += calculateClusterSSE(centroids.get(i), i);
        }
        return SSE;
    }

    public HashMap<String,Double> calculateWeighedCentroid(){
        double sum = 0.0;

        for(int i = 0; i < records.size(); i++){
            if(!indicesOfCentroids.contains(i)){
                double minDist = Double.MAX_VALUE;
                for(int index : indicesOfCentroids){
                    double dist = euclideanDistance(records.get(i).getRecord(), records.get(index).getRecord());
                    if(dist < minDist)
                        minDist = dist;
                }
                if(indicesOfCentroids.isEmpty())
                    sum = 0.0;
                sum += minDist;
            }
        }

        double threshold = sum * random.nextDouble();

        for(int i = 0; i < records.size(); i++){
            if(!indicesOfCentroids.contains(i)){
                double minDist = Double.MAX_VALUE;
                for(int ind : indicesOfCentroids){
                    double dist = euclideanDistance(records.get(i).getRecord(), records.get(ind).getRecord());
                    if(dist < minDist)
                        minDist = dist;
                }
                sum += minDist;

                if(sum > threshold){
                    indicesOfCentroids.add(i);
                    return records.get(i).getRecord();
                }
            }
        }

        return new HashMap<>();
    }

    public LinkedList<Record> getRecords() {
        return records;
    }
}
