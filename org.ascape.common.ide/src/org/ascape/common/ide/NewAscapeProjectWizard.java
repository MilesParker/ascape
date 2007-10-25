package org.ascape.common.ide;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class NewAscapeProjectWizard extends Wizard implements INewWizard {

    private JavaCapabilityConfigurationPage javaCapability;

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        System.out.println((new StringBuffer(" : "+selection).insert(0, "[NewAscapeProjectWizard.init] selection")));
        
    }

    public void addPages() {
        addPage(new WizardNewProjectCreationPage("Create Ascape Project"));
        JavaCapabilityConfigurationPage javaCapability = new JavaCapabilityConfigurationPage();
        addPage(javaCapability);
    }

    public boolean performFinish() {
        IJavaProject project = javaCapability.getJavaProject();
        try {
            List<IClasspathEntry> newClasspath = Arrays.asList(project.getRawClasspath());
            newClasspath.add(JavaCore.newProjectEntry(new Path(Activator.getDefault().getBundle().getResource("src").getPath()), true));
// URL srcURL = Activator.getDefault().getBundle().getResource("").getPath();
// IClasspathEntry libEntry = JavaCore.newLibraryEntry(
// new Path("d:/lib/foo.jar"), // library location
// new Path("d:/lib/foo_src.zip"), // source archive location
// new Path("src"), // source archive root path
// true); // exported
            project.setRawClasspath((IClasspathEntry[]) newClasspath.toArray(), new NullProgressMonitor());

        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
