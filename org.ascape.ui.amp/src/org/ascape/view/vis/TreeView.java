package org.ascape.view.vis;

import org.eclipse.amp.axf.core.IModel;
import org.eclipse.amp.axf.core.IObservationProvider;
import org.eclipse.amp.axf.ide.AXFWorkbenchPlugin;
import org.eclipse.ui.IViewPart;


public class TreeView extends EditPartFactoryScapeView {
    private static final long serialVersionUID = 1L;

    public TreeView() {
        super("org.eclipse.amp.view.TreeView", null, "Tree View");
    }

    public Object getAdapter(Class key) {
        if (key == IModel.class) {
            IObservationProvider model = (IObservationProvider) AXFWorkbenchPlugin.getDefault().getManager().getAdapter(getScape().getRoot(), IModel.class);
            return model;
        }
        if (key == IViewPart.class) {
            return getViewPart();
        }
        return null;
    }
}
