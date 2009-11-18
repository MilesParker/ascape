/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.swing;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A JTextField that only accepts digits.
 *
 * @author Miles Parker
 * @version 1.9
 * @since 1.9
 * @history first in 8/3/2000
 */
public class NumberOnlyField extends JTextField {

    /**
     * 
     */
    private static final long serialVersionUID = -5563810786594381089L;

    public NumberOnlyField(String text, int cols) {
        super(text, cols);
    }

    protected Document createDefaultModel() {
        return new NumberOnlyDocument();
    }

    private static class NumberOnlyDocument extends PlainDocument {

        /**
         * 
         */
        private static final long serialVersionUID = 8013609598759177913L;

        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                if (Character.isDigit(str.charAt(i))) {
                    s.append(str.charAt(i));
                }
            }
            super.insertString(offs, s.toString(), a);
        }
    }
}

