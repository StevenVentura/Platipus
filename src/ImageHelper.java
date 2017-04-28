import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageHelper
{
	public static final int IMAGE_PLAYER_DEFAULT = 0, IMAGE_BLOOP = 1, IMAGE_BUNNY = 2, IMAGE_ZOMBOO = 3;
	
	public ImageHelper()
	{
		
	}
	public static void saveImageToFile(String name, BufferedImage bi)
	{
		try{
		File f = new File(name);
		ImageIO.write(bi, "png", f);
		}catch(Exception e){e.printStackTrace();};
	}
	public static BufferedImage resize(BufferedImage originalImage, int newWidth, int newHeight)
	{
    BufferedImage resized = new BufferedImage(newWidth, newHeight, originalImage.getType());
    Graphics2D g2 = resized.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
    return resized;
	}
	
	public static Rectangle getScaledRect(Rectangle r, double rw, double rh, int height, int hShift, int vShift)
	{
		double x = r.getX();
		double y = r.getY();
		double h = r.getHeight();
		double w = r.getWidth();
		
		return new Rectangle((int)((x-hShift)*rw), height-(int)((y-vShift)*rh),(int)(rw*w),(int)(rh*h));
	}

	
	public static void draw(BufferedImage bi, int x, int y, Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		g.drawImage(bi, null, (int)((x-(bi.getWidth()/2)/rw-hShift)*rw), (int)(h-(y+bi.getHeight()/rh-vShift)*rh));
	}
	public static BufferedImage flipHorizontal(BufferedImage bufferedImage)
	{
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	    tx.translate(-bufferedImage.getWidth(null), 0);
	    AffineTransformOp op = new AffineTransformOp(tx,
	        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	     return op.filter(bufferedImage, null);
	}
    
	// http://code.google.com/p/game-engine-for-java/source/browse/src/com/gej/util/ImageTool.java#31
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

	//http://www.rgagnon.com/javadetails/java-0265.html
	private static Image makeColorTransparent
	(Image im, final Color color) {
	ImageFilter filter = new RGBImageFilter() {
	  // the color we are looking for... Alpha bits are set to opaque
	  public int markerRGB = color.getRGB() | 0xFF000000;

	  public final int filterRGB(int x, int y, int rgb) {
	    if ( ( rgb | 0xFF000000 ) == markerRGB ) {
	      // Mark the alpha bits as zero - transparent
	      return 0x00FFFFFF & rgb;
	      }
	    else {
	      // nothing to do
	      return rgb;
	      }
	    }
	  }; 

	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	return Toolkit.getDefaultToolkit().createImage(ip);
	}
	
	public static BufferedImage makeTransparent(Image im, Color color)
	{
		return toBufferedImage(makeColorTransparent(im, color));
	}
	
	public static void main(String[]args)
	{
		
	}
	
	
}