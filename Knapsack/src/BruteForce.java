import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BruteForce {

    static class Item {
        int position;
        int value;
        int weight;

        @Override
        public String toString() {
            return "["+"Position: "+position+" Size: "+weight+" Value: "+value+"]";
        }
    }

    private static int length;
    private static int capacity;
    private static final int numberOfSets = 15;
    private static final String FILE_PATH = "knapsack.txt";

    public BruteForce(){
        Item[] items = collectDataFromFile(FILE_PATH);
        printResult(items);
    }

    public static void main(String[] args){
        long startTime = System.nanoTime();
        BruteForce bruteForce = new BruteForce();
        long endTime = System.nanoTime();
        long totalTime = (endTime - startTime)/1000_000_000;
        System.out.println("Program executed time: " + totalTime + "s ");
    }

    private static String normalize(String raw) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(raw);
        while (scanner.hasNext()) {
            sb.append(scanner.next());
            sb.append(' ');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static Item[] collectDataFromFile(String filePath){
        List<Item[]> set = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            length = Integer.parseInt(line.substring(9,11));
            capacity = Integer.parseInt(line.substring(22,24));
            for (int i = 0; i < numberOfSets; i++){
                Item[] items = new Item[length];
                for (int j = 0; j < items.length; j++){
                    items[j] = new Item();
                }
                line = reader.readLine();
                line = normalize(reader.readLine());
                StringTokenizer token = new StringTokenizer(line.substring(9, line.length() - 1), ", ");
                int positionCounter = 1;
                for (Item item : items){
                    item.weight = Integer.parseInt(token.nextToken());
                }
                line = normalize(reader.readLine());
                token = new StringTokenizer(line.substring(8, line.length() - 1), ", ");
                for (Item item : items){
                    item.value = Integer.parseInt(token.nextToken());
                    item.position = positionCounter;
                    positionCounter++;
                }
                line = reader.readLine();
                set.add(items);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int dataSet = random.nextInt(14);
        System.out.println("Randomly selected dataSet is: " + (dataSet + 1));
        return set.get(dataSet);
    }

    private static void printResult(Item[] items){
        int totalValue = 0;
        int totalWeight = 0;
        Item[] result = new Item[length];
        int numberOfCombinations = (int) Math.pow(2, items.length);
        System.out.println("Total number of combinations: " + numberOfCombinations);

        for (int i = 0; i < numberOfCombinations; i++){
            String combination = Integer.toBinaryString(i);
            StringBuilder newCombination = new StringBuilder();
            for (int j = 0; j <= combination.length() - 1; j++) {
                newCombination.append(combination.charAt(j));
            }
            newCombination.append("0".repeat(Math.max(0, length - combination.length())));
            char[] set = newCombination.toString().toCharArray();
            Item[] newItems = new Item[length];
            int values = 0;
            int weights = 0;
            for (int j = 0; j < set.length; j++) {
                if(set[j] == '1'){
                    values += items[j].value;
                    weights += items[j].weight;
                    newItems[j]=items[j];
                }
            }
            if(weights <= capacity && values > totalValue){
                totalValue = values;
                totalWeight = weights;
                result = newItems;
            }
        }
        for (Item item : result){
            if (item != null){
                System.out.println(item);
            }
        }
        System.out.println("Total value: " + totalValue);
        System.out.println("Total weight: " + totalWeight);
    }
}
