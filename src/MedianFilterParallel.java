// A1_Parallel Programming
// Parallel median filter
// Tafadzwa Nyazenga
// 17 August 2022

import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.Arrays;

public class MedianFilterParallel extends RecursiveAction{

    //global variables
  	private int lo;
  	private int hi;
  	private int windowSize;
    private int[] src;
    private int[] dst;
  	protected static int SEQUENTIAL_CUTOFF= 10000;

    static long startTime = 0;

  	private static void tick(){
  		  startTime = System.nanoTime();
  	}

  	private static float tock(){
  		  return (System.nanoTime()-startTime)/1000000.0f;
  	}


    public MedianFilterParallel(int lo, int hi, int windowSize, int[] src, int[] dst){
    		this.lo = lo;
    		this.windowSize = windowSize;
        this.src = src;
        this.hi = hi;
        this.dst= dst;
    }

    @Override
    protected void compute(){
        if((hi)<SEQUENTIAL_CUTOFF){
            computeDirectly();
            return;
        }
        invokeAll(new MedianFilterParallel(lo, ((hi)/2), windowSize, src, dst), new MedianFilterParallel((((hi)/2)+lo), (hi-((hi)/2)), windowSize, src, dst));
    }

    protected void computeDirectly(){
        int fil = (windowSize-1)/2;
        float resRed[]= new float[windowSize*windowSize];
        float resGreen[]= new float[windowSize*windowSize];
        float resBlue[]= new float[windowSize*windowSize];
        int count = 0;
        for(int x = lo; x < hi+lo; x++){
            for(int y = -fil; y <= fil; y++){
                long sum = Math.min(Math.max(y+x, 0), src.length-1);
                int pixel = src[(int) sum];
                resRed[count] = (float)((pixel&0x00FF0000)>>16);
                resGreen[count] = (float)((pixel&0x0000FF00)>>8);
                resBlue[count] = (float)((pixel&0x000000FF)>>0);

                count++;
                }
            Arrays.parallelSort(resRed);
            Arrays.parallelSort(resGreen);
            Arrays.parallelSort(resBlue);
            int color = (((int)resRed[(resRed.length-1)/2])<<16)|(((int)resGreen[(resGreen.length-1)/2])<<8)|(((int) resBlue[(resBlue.length-1)/2])<<0);
            dst[x]= color;

            if(count>(windowSize*windowSize)-1){
                count = 0;
            }
        }
    }

    public static void main(String []args) throws IOException{

        String input;
        String arr[] = new String[3];
        //code to get input from user
        Scanner into = new Scanner(System.in);
        System.out.println("Please enter input image name, output image name and a window size greater or equal to 3: ");
        input = into.nextLine();
        arr = input.split(" ");

        //code to check correctness of parameter inputs
        try{
            if(arr[0].equals(null)||arr[1].equals(null)||arr[2].equals(null)){
                  System.out.println("Error");
            }
        }
        catch(Exception e){
            System.out.println("Input parameters entered incorrectly. Please remember to separate the parameters with spaces!");
            System.exit(0);
        }

        int temp = Integer.parseInt(arr[2]);
        if((temp < 3 )||(temp%2==0)){
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

        //getting values from bufferedImage into an array

        int [] src = inputImg.getRGB(0, 0, inputImg.getWidth(), inputImg.getHeight(), null, 0, inputImg.getWidth());
        int [] dst = new int[src.length];

        //displays system info
        int proc  = Runtime.getRuntime().availableProcessors();
        System.out.println(proc + " processor(s) avaiable");


        //invokes the actual parallel part of the program
        MeanFilterParallel par = new MeanFilterParallel(0, src.length, window, src, dst);
        ForkJoinPool pool = new ForkJoinPool();

        //starts the timer
        tick();
        //does the work
        pool.invoke(par);
        //stops the timer
        float timed = tock();

        //displays the time the main work took to run
        System.out.println("System took: " + timed + " milliseconds to run.");

        //writing the processed data into a new buffered image
        BufferedImage outputImg = new BufferedImage(inputImg.getWidth(), inputImg.getHeight(), BufferedImage.TYPE_INT_RGB);
        outputImg.setRGB(0, 0, outputImg.getWidth(), outputImg.getHeight(), dst, 0, outputImg.getWidth());
        File fn = new File(arr[1]);

        //writing data from the new buffered image into a new file
        ImageIO.write(outputImg, "jpg", fn);
        System.out.println("success");
    }
}
