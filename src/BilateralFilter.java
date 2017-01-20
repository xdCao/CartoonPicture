import java.awt.image.BufferedImage;

/**
 * Created by xdcao on 2016/12/21.
 */
public class BilateralFilter{
    private final static double factor = -0.5d;
    private double ds; // distance sigma
    private double rs; // range sigma
    private int radius; // half length of Gaussian kernel Adobe Photoshop
    private double[][] cWeightTable;
    private double[] sWeightTable;
    private int width;
    private int height;

    public BilateralFilter() {
        this.ds = 1.0f;
        this.rs = 1.0f;
    }

    private void buildDistanceWeightTable() {
        int size = 2 * radius + 1;
        cWeightTable = new double[size][size];
        for(int semirow = -radius; semirow <= radius; semirow++) {
            for(int semicol = - radius; semicol <= radius; semicol++) {
                // calculate Euclidean distance between center point and close pixels
                double delta = Math.sqrt(semirow * semirow + semicol * semicol)/ds;
                double deltaDelta = delta * delta;
                cWeightTable[semirow+radius][semicol+radius] = Math.exp(deltaDelta * factor);
            }
        }
    }

    public void buildSimilarityWeightTable() {
        sWeightTable = new double[256]; // since the color scope is 0 ~ 255
        for(int i=0; i<256; i++) {
            double delta = Math.sqrt(i * i ) / rs;
            double deltaDelta = delta * delta;
            sWeightTable[i] = Math.exp(deltaDelta * factor);
        }
    }

    public void setDistanceSigma(double ds) {
        this.ds = ds;
    }

    public void setRangeSigma(double rs) {
        this.rs = rs;
    }

    public BufferedImage filter(BufferedImage src) {
        width = src.getWidth();
        height = src.getHeight();
        //int sigmaMax = (int)Math.max(ds, rs);
        //radius = (int)Math.ceil(2 * sigmaMax);
        radius = (int)Math.max(ds, rs);
        buildDistanceWeightTable();
        buildSimilarityWeightTable();
        BufferedImage dest=null;
        dest = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        src.getRGB(0,0,width,height,inPixels,0,width);
        int index = 0;
        double redSum = 0, greenSum = 0, blueSum = 0;
        double csRedWeight = 0, csGreenWeight = 0, csBlueWeight = 0;
        double csSumRedWeight = 0, csSumGreenWeight = 0, csSumBlueWeight = 0;
        for(int row=0; row<height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                int rowOffset = 0, colOffset = 0;
                int index2 = 0;
                int ta2 = 0, tr2 = 0, tg2 = 0, tb2 = 0;
                for(int semirow = -radius; semirow <= radius; semirow++) {
                    for(int semicol = - radius; semicol <= radius; semicol++) {
                        if((row + semirow) >= 0 && (row + semirow) < height) {
                            rowOffset = row + semirow;
                        } else {
                            rowOffset = 0;
                        }

                        if((semicol + col) >= 0 && (semicol + col) < width) {
                            colOffset = col + semicol;
                        } else {
                            colOffset = 0;
                        }
                        index2 = rowOffset * width + colOffset;
                        ta2 = (inPixels[index2] >> 24) & 0xff;
                        tr2 = (inPixels[index2] >> 16) & 0xff;
                        tg2 = (inPixels[index2] >> 8) & 0xff;
                        tb2 = inPixels[index2] & 0xff;

                        csRedWeight = cWeightTable[semirow+radius][semicol+radius]  * sWeightTable[(Math.abs(tr2 - tr))];
                        csGreenWeight = cWeightTable[semirow+radius][semicol+radius]  * sWeightTable[(Math.abs(tg2 - tg))];
                        csBlueWeight = cWeightTable[semirow+radius][semicol+radius]  * sWeightTable[(Math.abs(tb2 - tb))];

                        csSumRedWeight += csRedWeight;
                        csSumGreenWeight += csGreenWeight;
                        csSumBlueWeight += csBlueWeight;
                        redSum += (csRedWeight * (double)tr2);
                        greenSum += (csGreenWeight * (double)tg2);
                        blueSum += (csBlueWeight * (double)tb2);
                    }
                }

                tr = (int)Math.floor(redSum / csSumRedWeight);
                tg = (int)Math.floor(greenSum / csSumGreenWeight);
                tb = (int)Math.floor(blueSum / csSumBlueWeight);
                outPixels[index] = (ta << 24) | (clamp(tr) << 16) | (clamp(tg) << 8) | clamp(tb);

                // clean value for next time...
                redSum = greenSum = blueSum = 0;
                csRedWeight = csGreenWeight = csBlueWeight = 0;
                csSumRedWeight = csSumGreenWeight = csSumBlueWeight = 0;

            }
        }
        dest.setRGB(0,0,width,height,outPixels,0,width);
        return dest;
    }

    public static int clamp(int p) {
        return p < 0 ? 0 : ((p > 255) ? 255 : p);
    }

//    public static void main(String[] args) {
//        BilateralFilter bf = new BilateralFilter();
//        bf.buildSimilarityWeightTable();
//    }

    private BufferedImage createCompatibleDestImage(BufferedImage src, Object o) {
        BufferedImage dest=new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
        return dest;
    }

}
