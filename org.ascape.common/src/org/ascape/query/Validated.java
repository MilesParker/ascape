/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.query;

import org.ascape.query.parser.ParseException;

/*
 * User: Miles Parker
 * Date: Apr 6, 2005
 * Time: 5:17:20 PM
 */

public interface Validated {
    public void validate(Object object) throws ParseException;
}
