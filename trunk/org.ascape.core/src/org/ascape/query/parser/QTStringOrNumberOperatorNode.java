package org.ascape.query.parser;

import org.ascape.query.Validated;

/*
 * User: Miles Parker
 * Date: Apr 7, 2005
 * Time: 5:23:02 PM
 */

public abstract class QTStringOrNumberOperatorNode extends QTOperator implements Validated {
    public QTStringOrNumberOperatorNode(int i) {
        super(i);
    }

    public QTStringOrNumberOperatorNode(BoolExprTree p, int i) {
        super(p, i);
    }

    public void validate(Object object) throws ParseException {
        Class type = getCriteria().getProperty().getType();
        if (!(type == String.class || Number.class.isAssignableFrom(type) || (type == Integer.TYPE) || (type == Double.TYPE) || (type == Float.TYPE) || (type == Long.TYPE))) {
            throw new ParseException("Can't use " + this + " for non-string or non-number classes. (Type of " + getCriteria().getProperty().getName() + " is " + getCriteria().getProperty().getType() + ")");
        }
    }
}
