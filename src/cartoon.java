import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by xdcao on 2016/12/21.
 */
public class cartoon {

    public static void main(String[] args) throws IOException {
        BufferedImage BI = null;
        BufferedImage dest=null;
        BufferedImage dest1=null;
        BufferedImage temp =null;
        BufferedImage finalImg=null;
        File file = new File("D://IMGP2103.JPG");
        BI = ImageIO.read(file);

        CannyEdgeFilter cannyEdgeFilter=new CannyEdgeFilter();
        dest=cannyEdgeFilter.filter(BI,dest);


        BufferedImage scale=scaleBmp(BI,4);


        BilateralFilter bilateralFilter=new BilateralFilter();
        bilateralFilter.setDistanceSigma(3.0f);
        bilateralFilter.setRangeSigma(12.0f);

        temp = bilateralFilter.filter(scale);

        temp=bilateralFilter.filter(temp);
        temp=bilateralFilter.filter(temp);
        temp=bilateralFilter.filter(temp);
        temp=bilateralFilter.filter(temp);
        temp=bilateralFilter.filter(temp);
        temp=bilateralFilter.filter(temp);

        dest1=bilateralFilter.filter(temp);

        dest1=bigger(dest1,4);

//        dest1=colorFilter(dest1);

        int[] array1=new int[dest.getWidth()*dest.getHeight()];
        dest.getRGB(0,0,dest.getWidth(),dest.getHeight(),array1,0,dest.getWidth());
        for(int i=0;i<array1.length;i++){
            if(array1[i]==-16777216){
                if(i>=3){
                    array1[i-1]=-16777216;
                    array1[i-2]=-16777216;
                    array1[i-3]=-16777216;
                }
            }
        }
        System.out.println(array1.length);

        int[] array2=new int[dest1.getWidth()*dest1.getHeight()];
        dest1.getRGB(0, 0, dest1.getWidth(), dest1.getHeight(), array2, 0, dest1.getWidth());

//        for(int i=0;i<100;i++){
//            System.out.print(array2[i]+"  ");
//            System.out.println((array2[i]&0x00ff00)>>8);
//        }

//        changeColor(array2);

        System.out.println(array2.length);

        int[] array3 = combine(dest1, array1, array2);

        System.out.println(array3.length);

        finalImg=new BufferedImage(dest.getWidth(),dest.getHeight(),dest.getType());
        finalImg.setRGB(0,0,dest.getWidth(),dest.getHeight(),array3,0,dest.getWidth());

        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("E://222.png"));
        writer.setOutput(ios);
        writer.write(finalImg);
        finalImg.flush();
        ios.flush();

    }

    private static int[] combine(BufferedImage dest1, int[] array1, int[] array2) {
        int[] array3=new int[dest1.getWidth()*dest1.getHeight()];

//        for(int i=0;i<array3.length;i++){
//            int array3_1;
//            int array3_2;
//            int array3_3;
//            array3_1=((array1[i]&0xff0000)>>16+(array2[i]&0xff0000)>>16)>255?255:((array1[i]&0xff0000)>>16+(array2[i]&0xff0000)>>16);
//            array3_2=((array1[i]&0x00ff00)>>8+(array2[i]&0x00ff00)>>8)>255?255:((array1[i]&0x00ff00)>>8+(array2[i]&0x00ff00)>>8);
//            array3_3=((array1[i]&0x0000ff)+(array2[i]&0x0000ff))>255?255:((array1[i]&0x0000ff)+(array2[i]&0x0000ff));
//            System.out.println(""+(array1[i]&0x0000ff)+","+(array2[i]&0x0000ff)+" = "+array3_3);
//            array3[i]=(array3_1<<16|array3_2<<8|array3_3)&0xffffff;
//        }
//        return array3;

        for(int i=0;i<array3.length;i++){
            if((array1[i]&0x0000ff)!=255){
                array3[i]=0;
            }else {
                array3[i]=array2[i]+array1[i];
            }
        }



        return array3;
    }


    private static void changeColor(int[] array2) {
        for (int i=0;i<array2.length;i++){
            int array2_1=(array2[i]&0xff0000)>>16;
            int array2_2=(array2[i]&0x00ff00)>>8;
            int array2_3=(array2[i]&0x0000ff);

            if (array2_1>=127){
                array2_1=255;
            }else if(array2_1<127){
                array2_1=0;
            }


            if (array2_2>=127){
                array2_2=255;
            }else if(array2_2<127){
                array2_2=0;
            }


            if (array2_3>=127){
                array2_3=255;
            }else if(array2_3<127){
                array2_3=0;
            }

            array2[i]=(array2_1<<16|array2_2<<8|array2_3)&0xffffff;
        }
    }


    private static BufferedImage bigger(BufferedImage src,int p) {

        BufferedImage result=new BufferedImage(src.getWidth()*p,src.getHeight()*p,src.getType());

        int srcArray[]=new int[src.getWidth()*src.getHeight()];
        System.out.println("srclen:"+srcArray.length);
        src.getRGB(0,0,src.getWidth(),src.getHeight(),srcArray,0,src.getWidth());

        int bigArray[]=new int[srcArray.length*(p*p)];
        for(int i=0;i<srcArray.length;i++){
            for(int j=0;j<(p*p);j++){
                bigArray[(p*p)*i+j]=srcArray[i];
            }
        }

        result.setRGB(0,0,src.getWidth()*p,src.getHeight()*p,bigArray,0,src.getWidth()*p);

        return result;

    }

    public static BufferedImage scaleBmp(BufferedImage src,int p){

        int[] srcArray=new int[src.getWidth()*src.getHeight()];
        src.getRGB(0,0,src.getWidth(),src.getHeight(),srcArray,0,src.getWidth());


        int scaleArray[]=new int[srcArray.length/(p*p)];
        System.out.println("scalelen:"+scaleArray.length);
        for(int i=0;i<srcArray.length/(p*p);i++){
            scaleArray[i]=srcArray[(p*p)*i];
        }

        BufferedImage result=new BufferedImage(src.getWidth()/p,src.getHeight()/p,src.getType());
        result.setRGB(0,0,src.getWidth()/p,src.getHeight()/p,scaleArray,0,src.getWidth()/p);

        return result;

    }

    private static BufferedImage colorFilter(BufferedImage src) {

        int[] srcArray=new int[src.getHeight()*src.getWidth()];
        src.getRGB(0,0,src.getWidth(),src.getHeight(),srcArray,0,src.getWidth());
        double LABArray[]=new double[srcArray.length];
        for(int i=0;i<srcArray.length/3;i++){
            int[] RGBtemp=new int[3];
            double[] XYZtemp;
            double[] LABtemp;
            RGBtemp[0]=srcArray[i*3];
            RGBtemp[1]=srcArray[i*3+1];
            RGBtemp[2]=srcArray[i*3+2];
            XYZtemp=sRGB2XYZ(RGBtemp);
            LABtemp=XYZ2Lab(XYZtemp);
            LABArray[i*3]=LABtemp[0];
            LABArray[i*3+1]=LABtemp[1];
            LABArray[i*3+2]=LABtemp[2];
        }


        int[] destArray=new int[srcArray.length];
        for(int i=0;i<LABArray.length/3;i++){
            double[] LABtemp=new double[3];
            LABtemp[0]=LABArray[i*3];
            LABtemp[1]=LABArray[i*3+1];
            LABtemp[2]=LABArray[i*3+2];
            double[] XYZtemp;
            int[] RGBtemp;
            XYZtemp=Lab2XYZ(LABtemp);
            RGBtemp=XYZ2sRGB(XYZtemp);
            destArray[i*3]=RGBtemp[0];
            destArray[i*3+1]=RGBtemp[1];
            destArray[i*3+2]=RGBtemp[2];
        }


        BufferedImage result=new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
        result.setRGB(0,0,src.getWidth(),src.getHeight(),destArray,0,src.getWidth());

        return result;

    }

    public static double[] sRGB2XYZ(int[] sRGB) {
        double[] XYZ = new double[3];
        double sR, sG, sB;
        sR = (double)sRGB[0];
        sG = (double)sRGB[1];
        sB = (double)sRGB[2];
        sR /= 255;
        sG /= 255;
        sB /= 255;

        if (sR <= 0.04045) {
            sR = sR / 12.92;
        } else {
            sR = Math.pow(((sR + 0.055) / 1.055), 2.4);
        }

        if (sG <= 0.04045) {
            sG = sG / 12.92;
        } else {
            sG = Math.pow(((sG + 0.055) / 1.055), 2.4);
        }

        if (sB <= 0.04045) {
            sB = sB / 12.92;
        } else {
            sB = Math.pow(((sB + 0.055) / 1.055), 2.4);
        }

        XYZ[0] = 41.24 * sR + 35.76 * sG + 18.05 * sB;
        XYZ[1] = 21.26 * sR + 71.52 * sG + 7.2 * sB;
        XYZ[2] = 1.93 * sR + 11.92 * sG + 95.05 * sB;

        return XYZ;
    }


    public static double[] XYZ2Lab(double[] XYZ) {
        double[] Lab = new double[3];
        double X, Y, Z;
        X = XYZ[0];
        Y = XYZ[1];
        Z = XYZ[2];
        double Xn, Yn, Zn;
        Xn = 95.04;
        Yn = 100;
        Zn = 108.89;
        double XXn, YYn, ZZn;
        XXn = X / Xn;
        YYn = Y / Yn;
        ZZn = Z / Zn;

        double fx, fy, fz;

        if (XXn > 0.008856) {
            fx = Math.pow(XXn, 0.333333);
        } else {
            fx = 7.787 * XXn + 0.137931;
        }

        if (YYn > 0.008856) {
            fy = Math.pow(YYn, 0.333333);
        } else {
            fy = 7.787 * YYn + 0.137931;
        }

        if (ZZn > 0.008856) {
            fz = Math.pow(ZZn, 0.333333);
        } else {
            fz = 7.787 * ZZn + 0.137931;
        }

        Lab[0] = 116 * fy - 16;
        Lab[1] = 500 * (fx - fy);
        Lab[2] = 200 * (fy - fz);

        return Lab;
    }


    public static double[] Lab2XYZ(double[] Lab) {
        double[] XYZ = new double[3];
        double L, a, b;
        double fx, fy, fz;
        double Xn, Yn, Zn;
        Xn = 95.04;
        Yn = 100;
        Zn = 108.89;

        L = Lab[0];
        a = Lab[1];
        b = Lab[2];

        fy = (L + 16) / 116;
        fx = a / 500 + fy;
        fz = fy - b / 200;

        if (fx > 0.2069) {
            XYZ[0] = Xn * Math.pow(fx, 3);
        } else {
            XYZ[0] = Xn * (fx - 0.1379) * 0.1284;
        }

        if ((fy > 0.2069) || (L > 8)) {
            XYZ[1] = Yn * Math.pow(fy, 3);
        } else {
            XYZ[1] = Yn * (fy - 0.1379) * 0.1284;
        }

        if (fz > 0.2069) {
            XYZ[2] = Zn * Math.pow(fz, 3);
        } else {
            XYZ[2] = Zn * (fz - 0.1379) * 0.1284;
        }

        return XYZ;
    }

    public static int[] XYZ2sRGB(double[] XYZ) {
        int[] sRGB = new int[3];
        double X, Y, Z;
        double dr, dg, db;
        X = XYZ[0];
        Y = XYZ[1];
        Z = XYZ[2];

        dr = 0.032406 * X - 0.015371 * Y - 0.0049895 * Z;
        dg = -0.0096891 * X + 0.018757 * Y + 0.00041914 * Z;
        db = 0.00055708 * X - 0.0020401 * Y + 0.01057 * Z;

        if (dr <= 0.00313) {
            dr = dr * 12.92;
        } else {
            dr = Math.exp(Math.log(dr) / 2.4) * 1.055 - 0.055;
        }

        if (dg <= 0.00313) {
            dg = dg * 12.92;
        } else {
            dg = Math.exp(Math.log(dg) / 2.4) * 1.055 - 0.055;
        }

        if (db <= 0.00313) {
            db = db * 12.92;
        } else {
            db = Math.exp(Math.log(db) / 2.4) * 1.055 - 0.055;
        }

        dr = dr * 255;
        dg = dg * 255;
        db = db * 255;

        dr = Math.min(255, dr);
        dg = Math.min(255, dg);
        db = Math.min(255, db);

        sRGB[0] = (int) (dr + 0.5);
        sRGB[1] = (int) (dg + 0.5);
        sRGB[2] = (int) (db + 0.5);

        return sRGB;
    }


}
