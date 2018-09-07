package InformationRetrieval.Performance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PerformanceCurve {

    private double[] xAxis;
    private double[] yAxis;
    private boolean showPoints;
    private PlotStyle plotStyle;
    private DotStyle dotStyle;
    private int increment;

    public PerformanceCurve(double[] xAxis, double[] yAxis, boolean sort, boolean showPoints, PlotStyle plotStyle, DotStyle dotStyle, int increment){
        double tmp1, tmp2;
        int i, j;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.showPoints = showPoints;
        this.plotStyle = plotStyle;
        this.dotStyle = dotStyle;
        this.increment = increment;
        if (sort){
            for (j = 1; j < xAxis.length; j++){
                tmp1 = xAxis[j];
                tmp2 = yAxis[j];
                i = j - 1;
                while (i >= 0 && xAxis[i] > tmp1){
                    xAxis[i + 1] = xAxis[i];
                    yAxis[i + 1] = yAxis[i];
                    i = i - 1;
                }
                xAxis[i + 1] = tmp1;
                yAxis[i + 1] = tmp2;
            }
        }
    }

    public void plotPsTricks(String fileName){
        FileWriter fw;
        String plotStyleString, dotStyleString;
        try {
            fw = new FileWriter(new File(fileName));
            fw.write("\\begin{pspicture*}(0,0)(" + xAxis[xAxis.length - 1] + ",1)\n");
            fw.write("\\psaxes{->}(0,0)(" + xAxis[xAxis.length - 1] + ",1)\n");
            fw.write("\\savedata{\\items}[{");
            for (int i = 0; i < xAxis.length; i += increment){
                fw.write("{" + xAxis[i] + "," + yAxis[i] + "}");
                if (i != xAxis.length - 1){
                    fw.write(",");
                }
            }
            fw.write("}]\n");
            switch (plotStyle){
                case DOTS:
                    plotStyleString = "dots";
                    break;
                case LINE:
                    plotStyleString = "line";
                    break;
                case CURVE:
                    plotStyleString = "curve";
                    break;
                default:
                    plotStyleString = "dots";
                    break;
            }
            switch (dotStyle){
                case STAR:
                    dotStyleString = "*";
                    break;
                case CIRCLE:
                    dotStyleString = "o";
                    break;
                case PLUS:
                    dotStyleString = "+";
                    break;
                case TRIANGLE:
                    dotStyleString = "triangle";
                    break;
                case SQUARE:
                    dotStyleString = "square";
                    break;
                case PENTAGON:
                    dotStyleString = "pentagon";
                    break;
                default:
                    dotStyleString = "*";
                    break;
            }
            if (showPoints){
                fw.write("\\dataplot[plotstyle=" + plotStyleString + ",showpoints=true,dotstyle=" + dotStyleString + "]{\\items}\n");
            } else {
                fw.write("\\dataplot[plotstyle=" + plotStyleString + "]{\\items}\n");
            }
            fw.write("\\end{pspicture*}\n");
            fw.close();
        } catch (IOException e) {
        }
    }
}
