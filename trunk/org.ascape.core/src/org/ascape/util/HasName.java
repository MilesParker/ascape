/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

/**
 * An interface declaring that an instance of a class has a name.
 * Note that a name is not the same as the string typically returned by
 * <code>toString()</code>. Instead, it is an in context name with
 * significance to the user. For example, an instance of Foo might
 * return "Foo 23A" for <code>toString</code>, but "Fred" for <code>getName()</code>.
 *
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public interface HasName {

    /**
     * Returns the of the object.
     */
    public String getName();
}
