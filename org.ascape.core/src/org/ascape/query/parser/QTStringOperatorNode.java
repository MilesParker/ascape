package org.ascape.query.parser;

import org.ascape.query.Validated;

/*
 * User: Miles Parker
 * Date: Apr 7, 2005
 * Time: 4:50:40 PM
 */

public abstract class QTStringOperatorNode extends QTOperator implements Validated {

    public QTStringOperatorNode(int i) {
        super(i);
    }

    public QTStringOperatorNode(BoolExprTree p, int i) {
        super(p, i);
    }

    public void validate(Object object) throws ParseException {
        Class type = getCriteria().getProperty().getType();
        if ((type == Boolean.class) || (type == Boolean.TYPE) || Number.class.isAssignableFrom(type) || (type == Integer.TYPE) || (type == Double.TYPE) || (type == Float.TYPE) || (type == Long.TYPE)) {
            throw new ParseException("Can't use " + this + " for number or boolean classes. (Type of " + getCriteria().getProperty().getName() + " is " + getCriteria().getProperty().getType() + ")");
        }
    }
}
