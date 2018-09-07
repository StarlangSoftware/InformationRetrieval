package InformationRetrieval.Performance;

public class ConfusionMatrix {
    private int truePositives;
    private int falsePositives;
    private int trueNegatives;
    private int falseNegatives;

    public ConfusionMatrix(int truePositives, int falsePositives, int trueNegatives, int falseNegatives){
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
    }

    public double precision(){
        return truePositives / (truePositives + falsePositives + 0.0);
    }

    public double recall(){
        return truePositives / (truePositives + falseNegatives + 0.0);
    }

    public double accuracy(){
        return (truePositives + trueNegatives) / (truePositives + trueNegatives + falsePositives + falseNegatives + 0.0);
    }

    public double error(){
        return (falsePositives + falseNegatives) / (truePositives + trueNegatives + falsePositives + falseNegatives + 0.0);
    }

    public double sensitivity(){
        return recall();
    }

    public double specificity(){
        return trueNegatives / (falsePositives + trueNegatives + 0.0);
    }

    public double truePositiveRate(){
        return recall();
    }

    public double falsePositiveRate(){
        return 1 - specificity();
    }

    public double fMeasure(){
        return (2 * precision() * recall()) / (precision() + recall());
    }
}
