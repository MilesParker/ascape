package org.ascape.util.vis;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorIndex {

    private Display display;

    // We want to use a consistent seed so we get predictable random colors
    private Random colorStream;

    public ColorIndex(Display display, long seed) {
        super();
        this.display = display;
        colorStream = new Random(seed);
    }

    public ColorIndex() {
        this(Display.getCurrent(), 123);
    }

    private Map<Object, Color> colorForObject = new HashMap<Object, Color>();

    private Map<RGB, Color> colorForRGB = new HashMap<RGB, Color>();

    public final Color getIndexedColor(RGB rgb) {
        Color color = colorForRGB.get(rgb);
        if (color == null) {
            color = new Color(display, rgb);
            colorForRGB.put(rgb, color);
        }
        return color;
    }

    public final Color getColorFor(Object model, RGB proposed) {
        Color color = colorForObject.get(model);
        if (color == null) {
            color = new Color(display, proposed);
            colorForObject.put(model, color);
        }
        return color;
    }

    public final Color getGeneratedColorFor(Object model, float sat, float brightness) {
        Color color = colorForObject.get(model);
        if (color == null) {
            RGB hsb = new RGB(colorStream.nextFloat() * 360, sat, brightness);
            color = new Color(display, hsb);
            colorForObject.put(model, color);
        }
        return color;
    }
}
