package org.ascape.runtime.swing;

import java.awt.Image;
import java.awt.Toolkit;

import org.ascape.util.vis.ImageProvider;

public class SwingImageProvider implements ImageProvider{

	public Image getImage(String string) {
        return Toolkit.getDefaultToolkit().getImage(DesktopEnvironment.class.getResource("images/" + string));
	}
	
}
