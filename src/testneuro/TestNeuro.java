/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testneuro;

/**
 *
 * @author Administrator
 */
import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class TestNeuro {

 public static void main(String[] args) {
    // load trained neural network saved with Neuroph Studio (specify some existing neural network file here)
    NeuralNetwork nnet = NeuralNetwork.load("C:\\Users\\Administrator\\Documents\\NetBeansProjects\\TestNeuro\\src\\testneuro\\net11-5.nnet"); // load trained neural network saved with Neuroph Studio
    // get the image recognition plugin from neural network
    ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin)nnet.getPlugin(ImageRecognitionPlugin.class); // get the image recognition plugin from neural network

try {
       // image recognition is done here (specify some existing image file)
        HashMap<String, Double> output = imageRecognition.recognizeImage(new File("C:\\Users\\Administrator\\Documents\\NetBeansProjects\\TestNeuro\\src\\testneuro\\qnIU6zE8zKw.jpg"));
        System.out.println("Picture 1" + output.toString());
    } catch(IOException ioe) {
        ioe.printStackTrace();
    }
 }
}
