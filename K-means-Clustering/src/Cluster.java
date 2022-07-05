import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Cluster {

    private static final Double ACCURACY = 0.0;

    public static void main(String[] args) {
        try {

            DataManager data = new DataManager("iris_training.txt");

            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter a value of k");
            int k = sc.nextInt();

            kMeans(data, k);

            data.createOutputFile("clustering_result.txt");

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void kMeans(DataManager data, int K){
        // select K initial centroids
        LinkedList<HashMap<String,Double>> centroids = initialCentroids(data, K);

        // initialize Sum of Squared Errors
        Double SSE = Double.MAX_VALUE;
        int iterationsCounter = 0;
        while (true) {

            // assign observations to centroids
            var records = data.getRecords();

            for(var record : records){
                Double minDist = Double.MAX_VALUE;
                // find the centroid at a minimum distance from it and add the record to its cluster
                for(int i = 0; i < centroids.size(); i++){
                    Double dist = DataManager.euclideanDistance(centroids.get(i), record.getRecord());
                    if(dist < minDist){
                        minDist = dist;
                        record.setClusterNo(i);
                    }
                }

            }

            // recompute centroids according to new cluster assignments
            centroids = data.recomputeCentroids(K);

            // exit condition, SSE changed less than ACCURACY parameter
            Double newSSE = data.calculateTotalSSE(centroids);
            iterationsCounter++;
            System.out.println("Sum of distance squared: " +newSSE+ " Iteration: "+iterationsCounter);
            if(SSE-newSSE <= ACCURACY){
                break;
            }
            SSE = newSSE;
        }
    }

    public static LinkedList<HashMap<String, Double>> initialCentroids(DataManager data, int K){
        LinkedList<HashMap<String,Double>> centroids = new LinkedList<>();
        for (int i = 0; i < K; i++) {
            centroids.add(data.records.get(i).getRecord());
        }
        for(int i = 1; i < K; i++){
            centroids.add(data.calculateWeighedCentroid());
        }
        return centroids;
    }

}
