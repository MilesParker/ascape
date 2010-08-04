package org.ascape.runtime;

import java.io.IOException;

import org.ascape.model.Scape;

public class NonGraphicRunner extends Runner {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void closeAndOpenSavedFinally(Scape oldScape) {
        throw new InternalError("Unexpected call to close and open for headless runtime.");
    }

    @Override
    public void saveChoose() {
        throw new InternalError("Unexpected call to save and choose for headless runtime.");
    }

    /**
     * Creates, initializes and runs the model specified in the argument. To allow the running of a model directly from
     * the command line, you should subclass this method as shown below:
     * 
     * <pre><code><BR>
     *     public MyModel extends Model {
     *         public static void main(String[] args) {
     *             (open(&quot;mypath.MyModel&quot;)).start();
     *         }
     *     }
     * <BR>
     * </pre>
     * 
     * </code> Otherwise, assuming your classpath is set up correctly, to invoke a model from the command line type:
     * 
     * <pre><code><BR>
     *     java org.ascape.model.Scape mypath.myModel
     * </pre>
     * 
     * </code>
     * 
     * @param args
     *        at index 0; the name of the subclass of this class to run
     */
    public static void main(String[] args) {
        // Register environment
        Runner model = new NonGraphicRunner();
        try {
            model.launch(args);
        } catch (IOException e) {
            throw new RuntimeException("Exception attempting to load model.", e);
        }
    }
}
