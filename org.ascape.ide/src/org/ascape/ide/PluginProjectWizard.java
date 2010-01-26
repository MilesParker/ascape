/**
 * <copyright>
 *
 * Copyright (c) 2007-2009 Metascape, LLC
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Metascape - Development and maintenance
 *
 * </copyright> $Id: PluginProjectWizard.java,v 1.4 2009/09/23 18:14:57 mparker Exp $
 */
package org.ascape.ide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.statushandlers.StatusManager;


/**
 * Creates a generic AMF project; intended for specialization.
 * @author milesparker
 */
public abstract class PluginProjectWizard extends Wizard implements INewWizard {

    private WizardNewProjectCreationPage projectPage;

    public PluginProjectWizard() {
        super();
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    public void addPages() {
        projectPage = new WizardNewProjectCreationPage("Specify model name and location.");
        addPage(projectPage);
        setWindowTitle("Create a new " + getProjectTypeName() + " project.");
        projectPage.setDescription(getWindowTitle());
    }

    public IProject getProject() {
        IProject project;
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        project = workspace.getRoot().getProject(projectPage.getProjectName());
        return project;
    }

    public boolean performFinish() {
        try {
            IJavaProject javaProject = JavaCore.create(getProject());
            final IProjectDescription projectDescription = ResourcesPlugin.getWorkspace()
            .newProjectDescription(projectPage.getProjectName());
            projectDescription.setLocation(null);

            getProject().create(projectDescription, null);
            List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();

            projectDescription.setNatureIds(getNatures());

            List<String> builderIDs = new ArrayList<String>();
            addBuilders(builderIDs);
            ICommand[] buildCMDS = new ICommand[builderIDs.size()];
            int i = 0;
            for (String builderID : builderIDs) {
                ICommand build = projectDescription.newCommand();
                build.setBuilderName(builderID);
                buildCMDS[i++] = build;
            }
            projectDescription.setBuildSpec(buildCMDS);
            getProject().open(null);
            getProject().setDescription(projectDescription, null);

            addClasspaths(classpathEntries, getProject());

            javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]), null);
            javaProject.setOutputLocation(new Path("/" + projectPage.getProjectName() + "/bin"), null);

            createFiles();
            return true;
        } catch (Exception exception) {
            StatusManager.getManager().handle(
                                              new Status(IStatus.ERROR, getPluginID(), "Problem creating "
                                                         + getProjectTypeName() + " project. Ignoring.", exception));
            try {
                getProject().delete(true, null);
            } catch (Exception e) {
                // TODO: handle exception
            }
            return false;
        }
    }

    public String[] getNatures() {
        return new String[] { JavaCore.NATURE_ID };
    }

    public void createFiles() throws CoreException, IOException {
    }

    public String getProjectTypeName() {
        return "Plugin";
    }

    public String getPluginID() {
        return AscapeIDEPlugin.PLUGIN_ID;
    }

    public void addClasspaths(List<IClasspathEntry> classpathEntries, IProject project) throws CoreException {
        classpathEntries.add(JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER")));
        classpathEntries.add(JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins")));
        IFolder srcFolder = project.getFolder(getSourceDirName());
        classpathEntries.add(JavaCore.newSourceEntry(srcFolder.getFullPath()));
        srcFolder.create(true, true, null);
    }

    public String getSourceDirName() {
        return "src";
    }

    public void addBuilders(List<String> builderIDs) {
        builderIDs.add(JavaCore.BUILDER_ID);
    }
}