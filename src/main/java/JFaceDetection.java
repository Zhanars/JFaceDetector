
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
    int needWidth = 248, needHeight = 366;
    static double coefficient = 0.5;
    public static void main(String[] args) throws IOException {

        run("uploads/000126000086.JPG");
        run("uploads/000616501476.JPG");
        run("uploads/000621601320.JPG");
        run("uploads/000703500609.jpeg");
        run("uploads/010213550237.JPG");
        run("uploads/010315601001.JPG");
        run("uploads/010609600725.JPG");
        run("uploads/010816500813.JPG");
        run("uploads/010817000064.jpeg");
        run("uploads/011003500658.JPG");
        run("uploads/011031501371.JPG");

    }
    public static void run(String iin) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String imageUrl = "http://distance.atu.kz/turniket/" + iin;

        saveImage(imageUrl, iin);
        Mat src = Imgcodecs.imread(iin);

        String xmlFile = "xmls/lbpcascade_frontalface.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile);

        MatOfRect faceDetection = new MatOfRect();
        cc.detectMultiScale(src, faceDetection);
        while(faceDetection.toArray().length == 0) {
            System.out.println(String.format("Кол-во лиц: %d", faceDetection.toArray().length));
            Mat src1 = src;
            Core.rotate(src1, src, Core.ROTATE_90_COUNTERCLOCKWISE);
            Imgcodecs.imwrite(iin, src);
            cc.detectMultiScale(src, faceDetection);
            System.out.println("Поворот изображения: " + iin);
        }
        int max = 0;
        for (Rect rect:faceDetection.toArray()){
            if (max < rect.width) {
                System.out.println(rect.width + ", " + rect.height);
                Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 3);
                BufferedImage inImage = null;
                try {
                    inImage = ImageIO.read(new File(iin));
                    BufferedImage outImage = cropImage(inImage, rect);
                    String fileName = iin.substring(iin.lastIndexOf('/') + 1);
                    File outputfile = new File("uploads/new" + fileName);
                    ImageIO.write(outImage, "jpg", outputfile);
                } catch (IOException e) {
                }
                max = rect.width;
            }
        }
        System.out.println("Обнаружение лиц закончено");
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
        int x = (rect.x < (int)(0.6 * coefficient * rect.width)) ? 0 : (rect.x - (int)(0.6 * coefficient * rect.width));
        int y = (rect.y < (int)(0.7 * coefficient * rect.height)) ? 0 : (rect.y - (int)(0.7 * coefficient * rect.height));
        int width = ((int)(3 * coefficient * rect.width + x) >= src.getWidth()) ? (src.getWidth() - x) : (int)(3 * coefficient * rect.width);
        int height = ((int)(4 * coefficient * rect.height + y) >= src.getHeight()) ? (src.getHeight() - y) : (int)(4 * coefficient * rect.height);
        //System.out.println(height);
        BufferedImage dest = src.getSubimage(x, y, width, height);
        return dest;
    }
}
