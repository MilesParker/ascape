/**
 * <copyright>
 *
 * Copyright (c) 2009 Metascape, LLC.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Metascape - Initial API and Implementation
 *
 * </copyright>
 *
 */
package org.ascape.ide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Creates a plugin project, copying manifest nad build properties from templates in the overriding wizard's plugin.
 * 
 * @author milesparker
 * 
 */
@SuppressWarnings("deprecation")
public class PluginTemplateProjectWizard extends PluginProjectWizard {

    protected class StringPair {
        public String a;
        public String b;

        public StringPair(String a, String b) {
            super();
            this.a = a;
            this.b = b;
        }
    }

    @Override
    /**
     * Copies templated PDE porject files into target project and replaces template values with those of project.
     */
    public void createFiles() throws CoreException, IOException {
        IFolder mFolder = getProject().getFolder("META-INF");
        mFolder.create(true, true, null);
        copyFiles(new Path("res/MANIFEST_TEMPLATE.MF"), new Path("META-INF/MANIFEST.MF"), new StringPair[] {
            new StringPair("%project_name%", getProject().getName()),
            new StringPair("%project_symbol%", getProject().getName()) });
        copyFiles(new Path("res/build_template.properties"), new Path("build.properties"), new StringPair[0]);
    }

    /**
     * Copies a file from the source (Eclipse runtime) plugin into the destination (user workspace) project.
     * 
     * @param srcFileLoc plugin project relative path for source file
     * @param destFileLoc user project relative path for destination file
     * @param pairs an array of pairs specifying a template string replacements
     * @throws CoreException
     * @throws IOException
     */
    public void copyFiles(IPath srcFileLoc, IPath destFileLoc, StringPair[] pairs) throws CoreException, IOException {
        InputStream srcStream = streamFromPlugin(getPluginID(), srcFileLoc.toString());
        BufferedReader srcReader = new BufferedReader(new InputStreamReader(srcStream));
        StringBuffer buffer = new StringBuffer();
        String next = srcReader.readLine();
        while (next != null) {
            buffer.append(next + "\r\n");
            next = srcReader.readLine();
        }
        String parsedText = buffer.toString();
        for (StringPair p : pairs) {
            parsedText = StringUtils.replace(parsedText, p.a, p.b);
        }
        InputStream is = new StringBufferInputStream(parsedText);
        IFile destFile = getProject().getFile(destFileLoc);
        destFile.create(is, true, null);
    }

    /**
     * Finds and opens a stream for reading from the plugin path.
     * 
     * @param pluginID
     * @param path
     * @return
     */
    public InputStream streamFromPlugin(String pluginID, String path) {
        try {
            Bundle bundle = Platform.getBundle(pluginID);
            URL entry = bundle.getEntry(path);
            if (entry == null) {
                throw new RuntimeException("No resource:" + path + " found in plugin: " + pluginID);
            }
            return entry.openStream();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load project resource from plugin.", e);
        }
    }
}
