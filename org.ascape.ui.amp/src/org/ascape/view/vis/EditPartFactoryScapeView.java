package org.ascape.view.vis;

import org.eclipse.amp.agf.gef.ModelEditPartInput;
import org.eclipse.amp.axf.view.ModelInput;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;

public abstract class EditPartFactoryScapeView extends ModelScapeView {
    private static final long serialVersionUID = 1L;
    private RootEditPart rootPart;

    public EditPartFactoryScapeView(String primaryID, String secondaryID, String name) {
        super(primaryID, secondaryID, name);
    }

    public ModelInput createInput() {
        ModelInput editorInput = new ModelEditPartInput(getModel(), null);
        return editorInput;
    }

    public Object getAdapter(Class key) {
        if (key == EditPart.class) {
            return rootPart.getChildren().get(0);
        }
        return super.getAdapter(key);
    }
}
