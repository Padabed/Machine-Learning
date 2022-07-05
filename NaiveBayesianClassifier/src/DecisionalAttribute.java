public class DecisionalAttribute {
    private double possibility;
    private String value;
    public DecisionalAttribute(String value){
        this.value=value;
    }

    public double getPossibility() {
        return possibility;
    }

    public String getValue() {
        return value;
    }

    public void setPossibility(double possibility) {
        this.possibility = possibility;
    }

    @Override
    public String toString() {
        return "{possibility: "+possibility+" Value: "+value+"}";
    }
}