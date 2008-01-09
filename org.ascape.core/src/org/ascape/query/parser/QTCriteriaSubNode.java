package org.ascape.query.parser;


/*
 * User: Miles Parker
 * Date: Apr 7, 2005
 * Time: 4:52:51 PM
 */

public class QTCriteriaSubNode extends SimpleNode {
    public QTCriteriaSubNode(int i) {
        super(i);
    }

    public QTCriteriaSubNode(BoolExprTree p, int i) {
        super(p, i);
    }

    public QTCriteria getCriteria() {
        return (QTCriteria) jjtGetParent();
    }
}
