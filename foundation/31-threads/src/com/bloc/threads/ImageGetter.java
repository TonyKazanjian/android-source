package com.bloc.threads;

import java.net.URL;
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
public class ImageGetter extends Thread {

	private URL mURL;
	private boolean mOpenWhenCompleted;

	/*
	 * ImageGetter
	 *
	 * Initialize the ImageGetter class.
	 *
	 * @param url The URL of the image this ImageGetter should
	 *		  download (String)
	 * @param openWhenCompleted if `true`, the image will be opened
	 *		  as soon it is downloaded, `false` otherwise
	 */
	/************************************************
 	 *	ASSIGNMENT:
 	 *	Implement this constructor for ImageGetter.
 	 *	The variables passed into it must impact the `run()` method's
 	 *	behavior as described above.
	/************************************************/
	public ImageGetter(String url, boolean openWhenCompleted){
	    try{
	        mURL = new URL(url);
	    mOpenWhenCompleted = openWhenCompleted;
	  }
	   catch (MalformedURLException e)
	        {  }
	       }
	

	@Override
	public void run() {
		/************************************************
 		 *	ASSIGNMENT:
 		 *	Perform the work found in `Main.java` in this
 		 *	Thread instead.
		/************************************************/
		try {
			File existingImage = new File("google_logo.png");
			if (existingImage.exists()) {
				existingImage.delete();
			}
			URL url = new URL("https://www.google.com/images/srpr/logo11w.png");
			BufferedImage bufferedImage = ImageIO.read(url);
			File outputfile = new File("google_logo.png");
			System.out.println();
			ImageIO.write(bufferedImage, "png", outputfile);
			if ("/".equals(System.getProperties().getProperty("file.separator"))) {
				Runtime.getRuntime().exec("open google_logo.png");
			} else {
				
			    System.out.println("would have opened Googlel logo");
			    //Runtime.getRuntime().exec("google_logo.png");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}