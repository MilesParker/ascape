package org.ascape.view.vis;

import org.eclipse.amp.agf.gef.ModelEditPartInput;
import org.eclipse.amp.axf.view.ModelInput;
import org.eclipse.gef.EditPartFactory;

public abstract class EditPartScapeView extends EditPartFactoryScapeView implements EditPartFactory {

    private static final long serialVersionUID = 1L;

    public EditPartScapeView(String primaryID, String secondaryID, String name) {
        super(primaryID, secondaryID, name);
    }

    public ModelInput createInput() {
        ModelInput editorInput = new ModelEditPartInput(getModel(), this);
        return editorInput;
    }
}