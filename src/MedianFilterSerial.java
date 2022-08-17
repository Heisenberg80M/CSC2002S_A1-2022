// A1_Parallel Programming
// Sequential median filter
// Tafadzwa Nyazenga
// 6 August 2022

import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;

public class MedianFilterSerial{

    private static int[] src, dst, block;
    static long startTime = 0;

  	private static void tick(){
  		  startTime = System.nanoTime();
  	}

  	private static float tock(){
  		  return (System.nanoTime()-startTime)/1000000.0f;
  	}

    public static long MedianCalc(int[] blk, int fil){
        int length = blk.length;
        float []resRed = new float[length];
        float []resGreen = new float[length];
        float []resBlue = new float[length];
        int resPxl = 0;

        for(int i = 0; i< length; i++){

            resRed[i] = (float)((blk[i]&0x00FF0000)>>16);
            resGreen[i] = (float)((blk[i]&0x0000FF00)>>8);
            resBlue[i] = (float)((blk[i]&0x000000FF)>>0);
        }
        Arrays.sort(resRed);
        Arrays.sort(resGreen);
        Arrays.sort(resBlue);


        resPxl = (((int)resRed[(length-1)/2]<<16)|((int)resGreen[(length-1)/2]<<8)|((int)resBlue[(length-1)/2]<<0));


        return resPxl;
    }

    public static void main(String []args) throws IOException{

        String input;
        String arr[] = new String[3];
        float totalTime = 0;

        //code to get input from user
        Scanner into = new Scanner(System.in);
        System.out.println("Please enter input image name, output image name and a window size greater or equal to 3: ");
        input = into.nextLine();
        arr = input.split(" ");

        //check if input parameters have been entered correctly
        try{
            if(arr[0].equals(null)||arr[1].equals(null)||arr[2].equals(null)){}
        }
        catch(Exception e){
            System.out.println("Input parameters entered incorrectly. Please remember to separate the parameters with spaces!");
            System.exit(0);;
        }

        int temp = Integer.parseInt(arr[2]);
        if(temp < 3 || (temp%2==0)){
            System.out.println("Inaccurate input for parameter 3. Please make sure value is greater or equal to 3 and an odd number.");
            System.exit(0);
        }

        //declaring the variables to be used
        int window;
        window = Integer.parseInt(arr[2]);
        File f;
        BufferedImage inputImg = null;

        try{
            f = new File(arr[0]);
            inputImg = ImageIO.read(f);

        }
        catch(Exception e){
            System.out.println("Input image not found!");
        }

        int count1= 0;
        int w = inputImg.getWidth();
        int h = inputImg.getHeight();
        block = new int[window*window];

        //getting the  pixel data from the input image
        src = inputImg.getRGB(0, 0, w, h, null, 0, w);
        dst = new int[src.length];
        int range = (window-1)/2;
        for(int x = 0; x<src.length; x++){
            if((x-range)>=0&&(x+range)<src.length){
                for(int y = x-(w*range); y<x+(w*range); y++){
                    if((y-range>=0)&&(y+range<src.length)){
                        for(int j = x-range;j<x+range;j++){
                            block[count1] = src[j];
                            count1++;
                            // to ensure no index is out of bounds errors
                            if(count1>(window*window)-1){
                                count1= 0;
                              }
                          }
                      }
                      tick();
                      dst[x] = (int)MedianFilterSerial.MedianCalc(block, window);
                      totalTime += tock();
                  }
              }
          }

        //displays the time the main work took to run
        System.out.println("System took: " + totalTime + " milliseconds to run.");

        //writing the processed data into a new buffered image
        BufferedImage outputImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        File nf = new File(arr[1]);

        outputImg.setRGB(0, 0, w, h, dst, 0, w);

        //writing data from the new buffered image into a new file
        ImageIO.write(outputImg, "jpg", nf);
        System.out.println("Success!");
    }
}
