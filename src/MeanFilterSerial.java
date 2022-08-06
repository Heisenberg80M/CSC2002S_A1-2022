// A1_Parallel Programming
// Sequential mean filter
// Tafadzwa Nyazenga
// 6 August 2022

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;

public class MeanFilterSerial{

    static BufferedImage imageIN; // For storing INPUT image in RAM
    static BufferedImage imageOUT; // For storing OUTPUT image in RAM
    String inputImg;
    String outputImg;
    int wSize;


    public MeanFilterSerial(String inputImg, String outputImg, int wSize){
        this.inputImg = inputImg;
        this.outputImg = outputImg;
        this.wSize = wSize;
    }


    public static void main(String args[]) throws IOException{

        
        BufferedImage imageIN = null; // For storing INPUT image in RAM

        System.out.println(inputImg);

        int width = 1600;
        int height = 900;
        // READ IMAGE
        try{
            File inImage = new File(inputImg);
            imageIN = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            imageIN = ImageIO.read(inImage);
            System.out.println("Reading complete.");
        }

        catch (IOException e) {
            System.out.println("Error: " + e);
        }

        // WRITE IMAGE
        try {
            // Output file path
            File outImage = new File(outputImg);

            // Writing to file taking type and path as
            ImageIO.write(imageIN, "jpeg", outImage);

            System.out.println("Writing complete.");
        }
        catch (IOException e) {
            System.out.println("Error: " + e);
        }



    }





}
