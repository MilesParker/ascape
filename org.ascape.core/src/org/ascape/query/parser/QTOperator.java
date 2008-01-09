package org.ascape.query.parser;


/*
 * User: Miles Parker
 * Date: Apr 7, 2005
 * Time: 8:24:00 PM
 */

public abstract class QTOperator extends QTCriteriaSubNode {
    public QTOperator(int i) {
        super(i);
    }

    public QTOperator(BoolExprTree p, int i) {
        super(p, i);
    }

    public abstract boolean evaluate(QTValue compared, Object value);
}
