import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class NaiveBayesianClassifier {
    private final List<List<String>>trainData;

    double accuracy = 0;

    private double classifiedSetosa = 0;
    private double classifiedVerginica = 0;
    private double classifiedVersicolor = 0;
    //correctly classified as Class x
    private double correctlyClassifiedSetosa = 0;
    private double correctlyClassifiedVerginica = 0;
    private double CorrectlyClassifiedVersicolor = 0;
    //accuracy
    private double accuracySetosa = 0;
    private double accuracyVersicolor = 0;
    private double accuracyVirginica = 0;
    //completeness
    private double fullSetosa = 0;
    private double fullVersicolor = 0;
    private double fullVirginica = 0;
    //F-Measure
    private float FMeasureSetosa = 0;
    private float FMeasureVersicolor = 0;
    private float FMeasureVirginica = 0;
    //error matrix classified as true
    private double SetosaVirginica = 0;
    private double SetosaVersicolor = 0;
    private double VirginicaSetosa = 0;
    private double VirginicaVersicolor = 0;
    private double VersicolorSetosa = 0;
    private double VersicolorVerginica = 0;

    public NaiveBayesianClassifier(String trainSet){
        trainData=loadSetFromFile(trainSet);
    }
    private static List<List<String>> loadSetFromFile(String filePath){
        List<List<String>> set=new ArrayList<>();
        try{
            BufferedReader bf=new BufferedReader(new FileReader(filePath));
            String line;
            while((line = bf.readLine())!=null){
                StringTokenizer tokenizer=new StringTokenizer(line," ");
                String token;
                List<String>attrs=new ArrayList<>();
                while(tokenizer.hasMoreTokens()){
                    token=tokenizer.nextToken();
                    attrs.add(token);
                }
                set.add(attrs);
            }
            bf.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return set;
    }
    void classifyFromFile(String filePath){
        List<List<String>>testData=loadSetFromFile(filePath);
        for(List<String>object:testData) {
            classify(object, false);
        }
        accuracy = (correctlyClassifiedSetosa + correctlyClassifiedVerginica + CorrectlyClassifiedVersicolor)*100/30;
        accuracySetosa = (correctlyClassifiedSetosa / classifiedSetosa)*100;
        accuracyVersicolor = (CorrectlyClassifiedVersicolor / classifiedVersicolor)*100;
        accuracyVirginica = (correctlyClassifiedVerginica / classifiedVerginica)*100;
        fullSetosa = (correctlyClassifiedSetosa / classifiedSetosa)*100;
        fullVersicolor = (CorrectlyClassifiedVersicolor / classifiedVersicolor)*100;
        fullVirginica = (correctlyClassifiedVerginica / classifiedVerginica)*100;
        FMeasureSetosa = (float) (2*(fullSetosa * accuracySetosa)/(accuracySetosa + fullSetosa));
        FMeasureVersicolor = (float) (2*(fullVersicolor * accuracyVersicolor)/(accuracyVersicolor + fullVersicolor));
        FMeasureVirginica = (float) (2*(fullVirginica * accuracyVirginica)/(accuracyVirginica + fullVirginica));

        System.out.println("Accuracy: "+ accuracy +"%");
        System.out.println("Result: ");
        resultTable();
        System.out.println();
        System.out.println("ErrorMatrix: ");
        matrixTable();
    }
    public void classify(List<String>attrs,boolean inputVector){
        //Looks for decision attributes in training data
        List<DecisionalAttribute>decisionalAttributes=findDecisionalAttributes();
        //For each attribute counts probability
        //P(X|line) = P(L1|X) · P(L2|X) · P(L3|X) · P(L4|X) · P(X)
        //P(Y|line) = P(L1|Y) · P(L2|Y) · P(L3|Y) · P(L4|Y) · P(Y)
        //P(Z|line) = P(L1|Z) · P(L2|Z) · P(L3|Z) · P(L4|Y) · P(Z)
        for(DecisionalAttribute decisionalAttribute:decisionalAttributes){
            double possibility = countPossibility(attrs,decisionalAttribute);
            decisionalAttribute.setPossibility(possibility);
        }
        //Version for Vector input
        if(inputVector){
            System.out.print("Attributes: ");
            for(String attr:attrs){
                System.out.print(attr+" ");
            }
            System.out.println("classified to: "+ findDecisionalAttributeWithHighestPossibility(decisionalAttributes));
        }

        switch (findDecisionalAttributeWithHighestPossibility(decisionalAttributes)) {
            case "Iris-setosa":
                classifiedSetosa++;
                if(attrs.get(attrs.size() - 1).equals("Iris-versicolor")){
                    SetosaVersicolor++;
                }else if(attrs.get(attrs.size() - 1).equals("Iris-virginica")){
                    SetosaVirginica++;
                }
                break;
            case "Iris-versicolor":
                classifiedVersicolor++;
                if(attrs.get(attrs.size() - 1).equals("Iris-setosa")){
                    VersicolorSetosa++;
                }else if(attrs.get(attrs.size() - 1).equals("Iris-virginica")){
                    VersicolorVerginica++;
                }
                break;
            case "Iris-virginica":
                classifiedVerginica++;
                if(attrs.get(attrs.size() - 1).equals("Iris-setosa")){
                    VirginicaSetosa++;
                }else if(attrs.get(attrs.size() - 1).equals("Iris-versicolor")){
                    VirginicaVersicolor++;
                }
                break;
        }

        if(findDecisionalAttributeWithHighestPossibility(decisionalAttributes).equals(attrs.get(attrs.size() - 1)) && findDecisionalAttributeWithHighestPossibility(decisionalAttributes).equals("Iris-setosa")){
            correctlyClassifiedSetosa++;
        }else if (findDecisionalAttributeWithHighestPossibility(decisionalAttributes).equals(attrs.get(attrs.size() - 1)) && findDecisionalAttributeWithHighestPossibility(decisionalAttributes).equals("Iris-versicolor")){
            CorrectlyClassifiedVersicolor++;
        }else if (findDecisionalAttributeWithHighestPossibility(decisionalAttributes).equals(attrs.get(attrs.size() - 1)) && findDecisionalAttributeWithHighestPossibility(decisionalAttributes).equals("Iris-virginica")){
            correctlyClassifiedVerginica++;
        }
    }
    private List<DecisionalAttribute>findDecisionalAttributes(){
        List<DecisionalAttribute>appearedPossibilites=new ArrayList<>();
        for(List<String>object:trainData){
            boolean appeared=false;
            String value=object.get(trainData.get(0).size()-1);
            for(DecisionalAttribute appearedValue:appearedPossibilites){
                if (value.equals(appearedValue.getValue())) {
                    appeared = true;
                    break;
                }
            }
            if(!appeared){
                appearedPossibilites.add(new DecisionalAttribute(value));
            }
        }
        return appearedPossibilites;
    }
    private double countPossibility(List<String>attrs,DecisionalAttribute decisionalAttribute){
        //P(SETOSA) for instance 40/120
        double possibility=countAppearanceOfValue(decisionalAttribute.getValue(),trainData.get(0).size()-1,decisionalAttribute)/trainData.size();
        for (int i = 0; i < attrs.size()-1; i++) {
            //P(L1|SETOSA)=How many times did this attribute value occur|number of Setos cases,p(L2 / Setosa), etc...
            possibility*=(((countAppearanceOfValue(attrs.get(i),i,decisionalAttribute))+1)/(trainData.size()));
        }
        //P(SETOSA|TestLine)
        return possibility;
    }
    private double countAppearanceOfValue(String value,int attributeNumber,DecisionalAttribute decisionalAttribute){
        //Counts the number of value appearances
        int counter=0;
        for(List<String>object:trainData){
            if(object.get(attributeNumber).equals(value)&&object.get(object.size()-1).equals(decisionalAttribute.getValue()))
                counter++;
        }
        return counter;
    }

    private String findDecisionalAttributeWithHighestPossibility(List<DecisionalAttribute>decisionalAttributes){
        //Finds the resulting for instance Setosa attribute
        DecisionalAttribute retDecisionalAttribute=decisionalAttributes.get(0);
        for(DecisionalAttribute decisionalAttribute:decisionalAttributes){
            if(!decisionalAttribute.getValue().equals(retDecisionalAttribute.getValue())){
                if(decisionalAttribute.getPossibility()>retDecisionalAttribute.getPossibility())
                    //Checks if the selected attribute is larger/smaller than the one in the loop, the one with the largest will be selected
                    retDecisionalAttribute=decisionalAttribute;
            }
        }
        return retDecisionalAttribute.getValue();
    }

    public void resultTable() {
        //precision, completeness, F-measure expressed as a percentage
        String[][] table = new String[][] { { "Class", "Accuracy", "Completeness", "F-Measure" },
                { "Iris-Setosa", accuracySetosa +"%", fullSetosa +"%", FMeasureSetosa +"%" },
                { "Iris-Versicolor", accuracyVersicolor +"%", fullVersicolor +"%", FMeasureVersicolor +"%" },
                { "Iris-Virginica", accuracyVirginica +"%", fullVirginica +"%", FMeasureVirginica +"%" }};
        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         *
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(table).forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
            columnLengths.putIfAbsent(i, 0);
            if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
            }
        }));
        /*
         * Prepare format String
         */
        final StringBuilder formatString = new StringBuilder();
        String flag = "-";
        columnLengths.forEach((key, value) -> formatString.append("| %").append(flag).append(value).append("s "));
        formatString.append("|\n");
        /*
         * Print table
         */
        Stream.iterate(0, (i -> i < table.length), (i -> ++i))
                .forEach(a -> System.out.printf(formatString.toString(), table[a]));

    }
    public void matrixTable() {
        //error Matrix
        String[][] table = new String[][] { { " ", "True Iris-Setosa", "True Iris-Versicolor", "True Iris-Virginica" },
                { "Predicted as Iris-Setosa", correctlyClassifiedSetosa +"", SetosaVersicolor +"", SetosaVirginica +""  },
                { "Predicted as Iris-Versicolor", VersicolorSetosa +"", CorrectlyClassifiedVersicolor +"", VersicolorVerginica +"" },
                { "Predicted as Iris-Virginica", VirginicaSetosa +"", VirginicaVersicolor +"", correctlyClassifiedVerginica +"" }};
        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         *
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(table).forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
            columnLengths.putIfAbsent(i, 0);
            if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
            }
        }));
        /*
         * Prepare format String
         */
        final StringBuilder formatString = new StringBuilder();
        String flag = "-";
        columnLengths.forEach((key, value) -> formatString.append("| %").append(flag).append(value).append("s "));
        formatString.append("|\n");
        /*
         * Print table
         */
        Stream.iterate(0, (i -> i < table.length), (i -> ++i))
                .forEach(a -> System.out.printf(formatString.toString(), table[a]));

    }
}