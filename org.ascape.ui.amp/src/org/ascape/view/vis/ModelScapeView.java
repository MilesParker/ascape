package org.ascape.view.vis;

import java.util.TooManyListenersException;

import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.eclipse.amp.axf.core.IModel;
import org.eclipse.amp.axf.core.IObservationProvider;
import org.eclipse.amp.axf.ide.AXFWorkbenchPlugin;
import org.eclipse.amp.axf.view.ModelInput;
import org.eclipse.amp.axf.view.ModelViewPart;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IViewPart;

public abstract class ModelScapeView extends DefaultScapeListener implements IAdaptable {

    private static final long serialVersionUID = -2831280332433751678L;

    private String name;

    private String primaryID;
    private String secondaryID;
    private ModelViewPart modelViewPart;

    private IModel model;

    public ModelScapeView(String primaryID, String secondaryID, String name) {
        super();
        this.primaryID = primaryID;
        this.secondaryID = secondaryID;
        this.name = name;
        if (name == null) {
            this.name = primaryID + (secondaryID != null ? ":" + secondaryID : "");
        }
    }

    public ModelScapeView(String primaryID) {
        this(primaryID, null, null);
    }

    public Object getAdapter(Class key) {
        if (key == IModel.class) {
            IObservationProvider model = (IObservationProvider) AXFWorkbenchPlugin.getDefault().getManager()
                    .getAdapter(getScape().getRoot(), IModel.class);
            return model;
        }
        if (key == IViewPart.class) {
            return modelViewPart;
        }
        return null;
    }

    public ModelInput createInput() {
        return new ModelInput(getModel());
    }

    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        model = (IModel) AXFWorkbenchPlugin.getDefault().getManager().getAdapter(getScape().getRoot(), IModel.class);
        AXFWorkbenchPlugin.getDefault().getManager().createViewPart(primaryID, createInput(), name);
    }

    public IModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName() + " View";
    }

    public IViewPart getViewPart() {
        return modelViewPart;
    }
}
