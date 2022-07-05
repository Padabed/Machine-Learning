import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        NaiveBayesianClassifier bayesClassifier = new NaiveBayesianClassifier("iris_training.txt");
        bayesClassifier .classifyFromFile("iris_test.txt");
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);
        while(loop){
            System.out.println("Do you want to enter new data? Yes / No");
            String answer = scanner.nextLine();
            if(answer.equalsIgnoreCase("Yes")){
                List<String> attrs = new ArrayList<>();
                System.out.println("Please enter data separated by space");
                String data = scanner.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(data," ");
                while (tokenizer.hasMoreTokens()){
                    attrs.add(tokenizer.nextToken());
                }
                bayesClassifier .classify(attrs,true);
            }else{
                loop = false;
            }
        }
    }
}