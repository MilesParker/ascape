/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

/**
 * An object that is capable of having some its values diffused.
 * 
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public interface Diffusable {

    /**
     * Get the diffusion temp value.
     * 
     * @return the diffusion temp
     */
    public double getDiffusionTemp();

    /**
     * Set the diffusion temp value.
     * 
     * @param diffusionTemp
     *            the diffusion temp
     */
    public void setDiffusionTemp(double diffusionTemp);
}

