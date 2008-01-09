package org.ascape.util.vis;

import java.awt.Image;

public class ImageRegistry implements ImageProvider {
	
	public final static ImageRegistry INSTANCE = new ImageRegistry();
	
	private static ImageProvider provider;
	
	public static void register(ImageProvider _provider) {
		provider = _provider;
	};

	public Image getImage(String string) {
		// TODO Auto-generated method stub
		return provider.getImage(string);
	}
}
