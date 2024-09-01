import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.image.*;

public class SCRIPT {
	
	public static final String IMG_PATH = "C:\\Users\\oliveira.sampaio\\Desktop\\TRABALHO NEVE\\BRUTOS";
	
	public static void main (String[] args) throws IOException{
		File f = new File("C:\\Users\\oliveira.sampaio\\Desktop\\TRABALHO NEVE\\BRUTOS\\LANDSAT 4-5\\LC08_L1TP_219076_20231218_20240103_02_T1_B10.TIF");
		BufferedImage img = ImageIO.read(f);
		int h = img.getHeight(), w = img.getWidth();
		BufferedImage res = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				res.setRGB(i,j,img.getRGB(i,j));
			}
		}
		
		File outputfile = new File(IMG_PATH+"\\the.png");
		ImageIO.write(res, "png", outputfile);
	}
}