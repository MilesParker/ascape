package org.ascape.runtime.applet;

import org.ascape.runtime.swing.BasicSwingRunner;

public class AppletRunner extends BasicSwingRunner {
    private static final long serialVersionUID = 5465L;

    public AppletRunner() {
        super(new AppletEnvironment());
    }
}
