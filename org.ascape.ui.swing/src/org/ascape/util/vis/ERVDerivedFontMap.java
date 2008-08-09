/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

/**
 * Class DerivedFontMap is used to maintain instances of derived fonts. This
 * minimizes calls to Font.deriveFont, which makes up a new Font each time it is
 * called. This is particularly useful in highly dynamic environments (such as
 * animation, or a place where objects and fonts are resized often). Any of the
 * single-argument calls to Font.deriveFont are supported for storage and
 * retrieval via getFont. Multi-argument calls to deriveFont are passed through
 * the getFont call but are NOT stored. The user can make up some arbitrary key
 * and store the key-Font pair in the map via the <code>setFont()</code> method.
 * It is useful to note that the font size is roughly proportional to the pixel
 * size, so if one needs a font to fit into an approximately 24 pixel high
 * space, a good place to start experimenting is with with a Font returned by a
 * call to a DerivedFontMap.getFont(24f). The results will of course vary quite
 * a bit depending on the baseFont.
 *
 * @author    Roger Critchlow, Carl Tollander, Miles Parker, Matthew Hendrey, and others
 * @version   2.9
 * @history   2.9 Moved into main Ascape.
 * @history   1.0 (Class version) 06/05/01 initial definition
 * @since     1.0
 */
public class ERVDerivedFontMap implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private HashMap fontMap = new HashMap();
    private Font baseFont;

    /**
     * Usually the baseFont has a size of 1, e.g. <code>new Font("Serif",
     * Font.PLAIN, 1));</code>
     *
     * @param baseFont  parameter
     */
    public ERVDerivedFontMap(Font baseFont) {
        this.baseFont = baseFont;
    }

    /**
     * Obtain a derived font, creating one if need be. The argument is rounded
     * to the nearest integer, then converted to a java.lang.Float which is used
     * as the storage and retrieval key.
     *
     * @param fontSize  parameter
     * @return          the font
     */
    public Font getFont(float fontSize) {
        Float f = new Float((float) java.lang.Math.round(fontSize));
        Font font = (Font) fontMap.get(f);
        if (font == null) {
            font = baseFont.deriveFont(f.floatValue());
            fontMap.put(f, font);
        }
        return font;
    }

    /**
     * Return a new Font object by replicating the current Font object and
     * applying a new transform to it.
     *
     * @param transform  parameter
     * @return           the font
     */
    public Font getFont(AffineTransform transform) {
        if (transform == null) {
            return null;
        }
        Font font = (Font) fontMap.get(transform);
        if (font == null) {
            font = baseFont.deriveFont(transform);
            fontMap.put(transform, font);
        }
        return font;
    }

    /**
     * Return a new Font object by replicating the current Font object and
     * applying a new style to it.
     *
     * @param style  parameter
     * @return       the font
     */
    public Font getFont(int style) {
        Integer i = new Integer(style);
        Font font = (Font) fontMap.get(i);
        if (font == null) {
            font = baseFont.deriveFont(i.intValue());
            fontMap.put(i, font);
        }
        return font;
    }

    /**
     * Return a new Font object by replicating the current Font object and
     * applying a new set of font attributes to it.
     *
     * @param attributes  parameter
     * @return            the font
     */
    public Font getFont(Map attributes) {
        if (attributes == null) {
            return null;
        }
        Font font = (Font) fontMap.get(attributes);
        if (font == null) {
            font = baseFont.deriveFont(attributes);
            fontMap.put(attributes, font);
        }
        return font;
    }


    /**
     * Obtain a font stored by key. This is used in situations where a special
     * key is needed, for example, when the programmer would otherwise be
     * calling a multiple-argument call to <code>Font.deriveFont()</code> .
     *
     * @param key  parameter
     * @return     the font
     */
    public Font getFont(Object key) {
        return (Font) fontMap.get(key);
    }

    /**
     * Store a font (this should be a font derived from the base font) under a
     * special key.
     *
     * @param key   the font
     * @param font  the font
     */
    public void setFont(Object key, Font font) {
        fontMap.put(key, font);
    }

}
