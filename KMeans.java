import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Random;

public class KMeans 
{
	public static void main( String [] args ) 
	{
		if ( args.length < 3 ) 
		{
			System.out.println( "Usage: Kmeans <input-image> <k> <output-image>" );
			return;
		}
  
		try 
		{
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			int k=Integer.parseInt(args[1]);
			BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
			ImageIO.write(kmeansJpg, "jpg", new File(args[2]));
		}

		catch ( IOException e ) 
		{
			System.out.println( e.getMessage() );
		}
	}
	 
	private static BufferedImage kmeans_helper( BufferedImage originalImage, int k ) 
	{
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage( w, h, originalImage.getType() );
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage( originalImage, 0, 0, w, h, null );

		// Read rgb values from the image.
		int[] rgb = new int[(w*h)];
		int count = 0;
		for ( int i = 0; i < w; i++ ) 
		{
			for( int j = 0; j < h; j++ ) 
			{
				rgb[count++] = kmeansImage.getRGB(i,j);
			}
		}

		// Call kmeans algorithm: update the rgb values to compress image.
		kmeans( rgb,k );

		// Write the new rgb values to the image.
		count = 0;
		for( int i = 0; i < w; i++ ) 
		{
			for( int j = 0; j < h; j++ ) 
			{
				kmeansImage.setRGB( i, j, rgb[count++] );
			}
		}

		// Return the compressed image
		return kmeansImage;
	}

	// Update the array rgb by assigning each entry in the rgb array to its cluster center
	private static void kmeans(int[] rgb, int k) 
	{
		int lenRGB = rgb.length;
		if (k > lenRGB) 
		{
			System.out.println("The value of k is greater than Pixel Length.");
			return;
		}
		int[] sum = new int[k];
		int[] allClusters = new int[lenRGB];
		int[] average = new int[k];   
		int[] average_N = new int[k];
		int[] nRed = new int[k];   
		int[] nGreen = new int[k]; 
		int[] nBlue = new int[k]; 
		int c = 0;
		double distance = 0;                   
		double thresh = 0.0;   
		
		for(int i=0;i<k;i++) 
		{
			Random r = new Random();
			average_N[i] = rgb[r.nextInt(lenRGB)];
		}

		do 
		{
			int avgNLen = average_N.length;
			for(int i=0;i<avgNLen;i++) 
			{
				nRed[i] = 0;
				nGreen[i] = 0;
				nBlue[i] = 0;
				average[i] = average_N[i];
				sum[i] = 0;
			}

			for(int i=0;i<lenRGB;i++) 
			{
				thresh = Double.MAX_VALUE;
				for(int j=0;j<avgNLen;j++) 
				{
					Color color1 = new Color(rgb[i]);
					Color color2 = new Color(average_N[j]);
					int dRed = color1.getRed() - color2.getRed();
					int dGreen = color1.getGreen() - color2.getGreen();
					int dBlue = color1.getBlue() - color2.getBlue();
					distance = Math.sqrt(dRed * dRed + dGreen * dGreen + dBlue * dBlue);
					if(distance < thresh) 
					{
						c = j;
						thresh = distance;
					}
				}
				Color color = new Color(rgb[i]);
				nRed[c] = nRed[c] + color.getRed();
				nGreen[c] = nGreen[c] + color.getGreen();
				nBlue[c] = nBlue[c] + color.getBlue();
				sum[c]++;
				allClusters[i] = c;
			}	
			
			for(int i=0;i<average_N.length;i++) 
			{
				int aRed = (int)(nRed[i] / sum[i]);
				int aGreen = (int)(nGreen[i] / sum[i]);
				int aBlue = (int)(nBlue[i] / sum[i]);
				average_N[i] = ((aGreen & 0x000000FF) << 8) | ((aBlue & 0x000000FF)) | ((aRed & 0x000000FF) << 16) ;
			}
		} 
		while(bothSame(average, average_N)==0);
	
		for(int i=0;i<lenRGB;i++) 
		{
			rgb[i] = average_N[allClusters[i]];
		}
	}
	
	private static int bothSame(int[] average, int[] average_N) 
	{
		for (int i=0;i<average_N.length;i++)
		{
			if(average_N[i]!=average[i])
			{
				return 0;
			}
		}
		return 1;
	}
}
