package image_enhancement;


// demonstrate how to display an image
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.*;

import javax.swing.*;

public class HistogramEqualizerDemo extends JFrame {

	private static final long serialVersionUID = 1L;
    BufferedImage sourceImage = null;
    File sourceFile, savedFile;
	static ImageUtilities util= new ImageUtilities();
	public static final String imageName = util.getImageName();
    static BufferedImage heColorImage; //grey image of input image
    int M, N;                /* power of 2 for each dimension */
    int iWidth, iHeight, fWidth, fHeight;
    String sWidth, sHeight;

	public HistogramEqualizerDemo() {
		
        sourceImage = ImageUtilities.getBufferedImage(imageName, this);
        iWidth = sourceImage.getWidth();
        iHeight = sourceImage.getHeight();

        fWidth = (int) (iWidth * 2.4);
        fHeight = (int) (iHeight * 1.4);

        sWidth = Integer.toString(iWidth);
        sHeight = Integer.toString(iHeight);

        heColorImage = makeNewBufferedImage(histGS(sourceImage), sourceImage.getWidth(), sourceImage.getHeight());
        
        savedFile = new File("test.jpg");
        try {
            ImageIO.write(heColorImage, "jpeg", savedFile);
        } catch (Exception e) {
        }
	}

    /**
     * Get RGB inputImageData from image and change them into
     * three color arrays, red, green, and blue.
     */
	public int[][] RGBToGrey(BufferedImage source){
		int greyScale[][] = new int[source.getWidth()][source.getHeight()];
		for(int x=0; x<source.getWidth(); x++){
			for(int y=0; y<source.getHeight(); y++){
				int c = source.getRGB(x, y);
				float r = (c&0x00ff0000)>>16;
				float g = (c&0x0000ff00)>>8;
				float b = c&0x000000ff;
				greyScale[x][y] = (int)(0.3*r + 0.59*g + 0.11*b);
			}
		}
		return greyScale;
	}

    /** Mean filter original image. */
	public int[][] histGS(BufferedImage source){
		int[][] input = RGBToGrey(source); //GS of source
		int[][] output = new int[source.getWidth()][source.getHeight()];
		int[] levelOccurence = new int[256];
		//count level occurnces
		for(int x=0; x<source.getWidth(); x++){
			for(int y=0; y<source.getHeight(); y++){
				levelOccurence[input[x][y]]++;
			}
		}
		for(int x=0; x<source.getWidth(); x++){
			for(int y=0; y<source.getHeight(); y++){
				float cdfp = cumulativeDistFactor(source, input[x][y], levelOccurence);
				output[x][y] = (int)(cdfp*(float)255);
			}
		}
		return output;
	}	
    private float probabilityOfPixel(BufferedImage source, int level, int[] occurences){
    	float output = 0;
    	output = ((float)occurences[level])/(float)((source.getHeight()*source.getWidth()));
    	return output;
    }
    private float cumulativeDistFactor(BufferedImage source, int level, int[] occurences){
    	float cdf = 0;
    	for(int j=0; j<level; j++){
    		cdf+=probabilityOfPixel(source, j, occurences);
    	}
    	return cdf;
    }
    public BufferedImage makeNewBufferedImage(int[][] gs, int width, int height){
 	   BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
 	   int[] iArray = {0,0,0,255};
 	   WritableRaster r = image.getRaster();
 	   for(int x=0; x<width; x++){
 		   for(int y=0; y<height; y++){
 			   int v = gs[x][y];
 			   iArray[0] = v;
 			   iArray[1] = v;
 			   iArray[2] = v;
 			   r.setPixel(x, y, iArray);
 		   }
 	   }
 	   image.setData(r);
 	   return image;
    }

    public static BufferedImage getHeColorImage() {
		return heColorImage;
	}
    
}
