package org.ascape.view.vis;



public class GEFView extends EditPartFactoryScapeView {

    private static final long serialVersionUID = 1L;

    public GEFView(String primaryID, String secondaryID, String name) {
        super(primaryID, secondaryID, name);
    }

    public GEFView() {
        super("org.eclipse.amp.view.GEFView", null, "Graphic View");
    }

    public GEFView(String name) {
        super("org.eclipse.amp.view.GEFView", null, name);
    }
}
