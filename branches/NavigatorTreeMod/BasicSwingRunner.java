package org.ascape.runtime.swing;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.ascape.model.Scape;
import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.runtime.Runner;
import org.ascape.runtime.RuntimeEnvironment;

public class BasicSwingRunner extends Runner {

    private static final long serialVersionUID = 6552410869134181196L;

    public BasicSwingRunner(RuntimeEnvironment environment) {
        super(environment);
    }

    public void notify(final ScapeEvent event, final ScapeListener listener) {
        if (listener instanceof JComponent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    listener.scapeNotification(event);
                }
            });
        } else {
            listener.scapeNotification(event);
        }
    }

    public void respondControl(final ControlEvent control) {
        // We might be in Swing thread and don't want to risk a deadlock.
        new Thread(new Runnable() {
            public void run() {
                BasicSwingRunner.super.respondControl(control);
            }
        }).start();
    }

    public void closeAndOpenSavedFinally(Scape oldScape) {
    }

    public void saveChoose() {
    }


}
