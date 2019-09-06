
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class JFaceDetection {
    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.out.println("Done");
        String iin = "uploads/001228601169.JPG";
        String imageUrl = "http://distance.atu.kz/turniket/" + iin;

        saveImage(imageUrl, iin);
        Mat src = Imgcodecs.imread(iin);

        String xmlFile = "xmls/lbpcascade_frontalface.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile);

        MatOfRect faceDetection = new MatOfRect();
        cc.detectMultiScale(src, faceDetection);
        System.out.println(String.format("Кол-во лиц: %d", faceDetection.toArray().length));
        for (Rect rect:faceDetection.toArray()){
            System.out.println(rect.width + ", " + rect.height);
            Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 3);
            BufferedImage inImage = null;
            try {
                inImage = ImageIO.read(new File(iin));
                BufferedImage outImage = cropImage(inImage, rect);
                File outputfile = new File(iin);
                ImageIO.write(outImage, "jpg", outputfile);
            } catch (IOException e) {
            }
        }

        //Imgcodecs.imwrite(iin, src);
        System.out.println("Image Detection Finished");

    }
    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
    private static BufferedImage cropImage(BufferedImage src, Rect rect) {
        BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
        return dest;
    }
}
