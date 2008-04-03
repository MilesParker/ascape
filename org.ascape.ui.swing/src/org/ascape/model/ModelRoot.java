package org.ascape.model;

import org.ascape.runtime.swing.SwingRunner;

/**
 * Implementation of ModelRunner for swing based applications.
 * retained for backward compatability, but also ensures that models developed for one type of UI
 * -- non-swing for example -- can be executed on other UIs without implementation changes.
 * @author milesparker
 *
 */
public class ModelRoot extends SwingRunner {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs, creates and runs the supplied model.
     * 
     * @param modelName
     *            the fully qualified name of the Java class for the model's
     *            root scape
     */
    public static Scape open(String modelName) {
        return (new SwingRunner()).openInstance(modelName);
    }
}
