/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.sweep;

import java.lang.reflect.InvocationTargetException;

import org.ascape.util.PropertyAccessor;

/**
 * A class faciliating iteration across a paramater dimension.
 * This class has the neat feature that it will set the object to the next value itself
 * on a call to next.
 *
 * @author Miles Parker
 * @version 1.9
 * @history 1.9 8/1/2000 first in
 * @since 1.9 8/1/2000
 */
public class SweepDimension extends PropertyAccessor implements Sweepable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    abstract class TypedSweepDimension implements Sweepable {

        public abstract String getMinAsText();

        public abstract void setMinAsText(String s);

        public abstract String getMaxAsText();

        public abstract void setMaxAsText(String s);

        public abstract String getIncrementAsText();

        public abstract void setIncrementAsText(String s);
    }

    public class TypedDoubleSweepDimension extends TypedSweepDimension {

        double start;
        double end;
        double increment;
        double currentValue;

        public TypedDoubleSweepDimension() {
        }

        public TypedDoubleSweepDimension(double start, double end, double increment) {
            this.start = start;
            this.end = end;
            this.increment = increment;
        }

        public void reset() {
            currentValue = start;
        }

        public boolean hasNext() {
            if (increment > 0) {
                return currentValue <= end;
            } else {
                return currentValue >= end;
            }
        }

        public Object next() {
            Double nextValue = new Double(currentValue);
            currentValue += increment;
            return nextValue;
        }

        public String getMinAsText() {
            return Double.toString(start);
        }

        public void setMinAsText(String s) {
            start = Double.parseDouble(s);
            //start = (new Double(s)).doubleValue();
            currentValue = start;
        }

        public String getMaxAsText() {
            return Double.toString(increment);
        }

        public void setMaxAsText(String s) {
            end = Double.parseDouble(s);
        }

        public String getIncrementAsText() {
            return Double.toString(increment);
        }

        public void setIncrementAsText(String s) {
            increment = Double.parseDouble(s);
        }
    }

    public class TypedFloatSweepDimension extends TypedSweepDimension {

        float start;
        float end;
        float increment;
        float currentValue;

        public TypedFloatSweepDimension() {
        }

        public TypedFloatSweepDimension(float start, float end, float increment) {
            this.start = start;
            this.end = end;
            this.increment = increment;
        }

        public void reset() {
            currentValue = start;
        }

        public boolean hasNext() {
            if (increment > 0) {
                return currentValue <= end;
            } else {
                return currentValue >= end;
            }
        }

        public Object next() {
            Float nextValue = new Float(currentValue);
            currentValue += increment;
            return nextValue;
        }

        public String getMinAsText() {
            return Float.toString(start);
        }

        public void setMinAsText(String s) {
            start = Float.parseFloat(s);
            //start = (new Float(s)).floatValue();
            currentValue = start;
        }

        public String getMaxAsText() {
            return Float.toString(increment);
        }

        public void setMaxAsText(String s) {
            end = Float.parseFloat(s);
        }

        public String getIncrementAsText() {
            return Float.toString(increment);
        }

        public void setIncrementAsText(String s) {
            increment = Float.parseFloat(s);
        }
    }

    public class TypedIntegerSweepDimension extends TypedSweepDimension {

        int start;
        int end;
        int increment;
        int currentValue;

        public TypedIntegerSweepDimension() {
        }

        public TypedIntegerSweepDimension(int start, int end, int increment) {
            this.start = start;
            this.end = end;
            this.increment = increment;
        }

        public void reset() {
            currentValue = start;
        }

        public boolean hasNext() {
            if (increment > 0) {
                return currentValue <= end;
            } else {
                return currentValue >= end;
            }
        }

        public Object next() {
            Integer nextValue = new Integer(currentValue);
            currentValue += increment;
            return nextValue;
        }

        public String getMinAsText() {
            return Integer.toString(start);
        }

        public void setMinAsText(String s) {
            start = Integer.parseInt(s);
            //start = (new Integer(s)).intValue();
            currentValue = start;
        }

        public String getMaxAsText() {
            return Integer.toString(increment);
        }

        public void setMaxAsText(String s) {
            end = Integer.parseInt(s);
        }

        public String getIncrementAsText() {
            return Integer.toString(increment);
        }

        public void setIncrementAsText(String s) {
            increment = Integer.parseInt(s);
        }
    }

    public class TypedLongSweepDimension extends TypedSweepDimension {

        long start;
        long end;
        long increment;
        long currentValue;

        public TypedLongSweepDimension() {
        }

        public TypedLongSweepDimension(long start, long end, long increment) {
            this.start = start;
            this.end = end;
            this.increment = increment;
        }

        public void reset() {
            currentValue = start;
        }

        public boolean hasNext() {
            if (increment > 0) {
                return currentValue <= end;
            } else {
                return currentValue >= end;
            }
        }

        public Object next() {
            Long nextValue = new Long(currentValue);
            currentValue += increment;
            return nextValue;
        }

        public String getMinAsText() {
            return Long.toString(start);
        }

        public void setMinAsText(String s) {
            start = Long.parseLong(s);
            //start = (new Long(s)).longValue();
            currentValue = start;
        }

        public String getMaxAsText() {
            return Long.toString(increment);
        }

        public void setMaxAsText(String s) {
            end = Long.parseLong(s);
        }

        public String getIncrementAsText() {
            return Long.toString(increment);
        }

        public void setIncrementAsText(String s) {
            increment = Long.parseLong(s);
        }
    }

    private TypedSweepDimension typedDimension;

    /**
     * Constructs a dimension composed of the object and an accessor for that object.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     */
    private SweepDimension(Object object, String accessorName) {
        super(object, accessorName);
        propertyClass = descriptor.getPropertyType();
        this.object = object;
    }

    /**
     * Constructs a dimension composed of the object and an accessor (property) for that object, providing start, final , and increment values.
     * This version takes string arguments and automatically coerces them, sutable for use with UIs and xml bindings.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     * @param start the start value for the dimension (String)
     * @param start the final (termination) value for the dimension (String)
     * @param start the iteration amount for the dimension (String)
     */
    public SweepDimension(Object object, String accessorName, final String start, final String end, final String increment) {
        this(object, accessorName);
        if (propertyClass == Integer.TYPE) {
            typedDimension = new TypedIntegerSweepDimension();
        } else if (propertyClass == Double.TYPE) {
            typedDimension = new TypedDoubleSweepDimension();
        } else if (propertyClass == Float.TYPE) {
            typedDimension = new TypedFloatSweepDimension();
        } else if (propertyClass == Long.TYPE) {
            typedDimension = new TypedLongSweepDimension();
        } else {
            throw new IllegalArgumentException("Type mismatch in creating " + accessorName + " sweep iterator.");
        }
        setMinAsText(start);
        setMaxAsText(end);
        setIncrementAsText(increment);
        typedDimension.reset();
    }

    /**
     * Constructs a dimension composed of the object and an integer accessor (property) for that object, providing start, final , and increment values.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     * @param start the start value for the dimension (int)
     * @param start the final (termination) value for the dimension (int)
     * @param start the iteration amount for the dimension (int)
     */
    public SweepDimension(Object object, String accessorName, final int start, final int end, final int increment) {
        this(object, accessorName);
        if (propertyClass == Integer.TYPE) {
            typedDimension = new TypedIntegerSweepDimension(start, end, increment);
        } else {
            throw new IllegalArgumentException("Type mismatch in creating " + accessorName + " sweep iterator.");
        }
        typedDimension.reset();
    }

    /**
     * Constructs a dimension composed of the object and an integer accessor (property) for that object, providing start, final , and increment values.
     * Assumed to iterate by one (as in i++) each time.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     * @param start the start value for the dimension (int)
     * @param start the final (termination) value for the dimension (int)
     */
    public SweepDimension(Object object, String accessorName, final int start, final int end) {
        this(object, accessorName, start, end, 1);
    }

    /**
     * Constructs a dimension composed of the object and an double accessor (property) for that object, providing start, final , and increment values.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     * @param start the start value for the dimension (int)
     * @param start the final (termination) value for the dimension (int)
     * @param start the iteration amount for the dimension (int)
     */
    public SweepDimension(Object object, String accessorName, final double start, final double end, final double increment) {
        this(object, accessorName);
        if (propertyClass == Double.TYPE) {
            typedDimension = new TypedDoubleSweepDimension(start, end, increment);
        } else {
            throw new IllegalArgumentException("Type mismatch in creating " + accessorName + " sweep iterator.");
        }
        typedDimension.reset();
    }

    /**
     * Constructs a dimension composed of the object and an float accessor (property) for that object, providing start, final , and increment values.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     * @param start the start value for the dimension (int)
     * @param start the final (termination) value for the dimension (int)
     * @param start the iteration amount for the dimension (int)
     */
    public SweepDimension(Object object, String accessorName, final float start, final float end, final float increment) {
        this(object, accessorName);
        if (propertyClass == Float.TYPE) {
            typedDimension = new TypedFloatSweepDimension(start, end, increment);
        } else {
            throw new IllegalArgumentException("Type mismatch in creating " + accessorName + " sweep iterator.");
        }
        typedDimension.reset();
    }

    /**
     * Constructs a dimension composed of the object and an long accessor (property) for that object, providing start, final , and increment values.
     * @param object the object to have its state changed.
     * @param accessorName the name of the accessor, following the standard Java propert pattern.
     * @param start the start value for the dimension (int)
     * @param start the final (termination) value for the dimension (int)
     * @param start the iteration amount for the dimension (int)
     */
    public SweepDimension(Object object, String accessorName, final long start, final long end, final long increment) {
        this(object, accessorName);
        if (propertyClass == Long.TYPE) {
            typedDimension = new TypedLongSweepDimension(start, end, increment);
        } else {
            throw new IllegalArgumentException("Type mismatch in creating " + accessorName + " sweep iterator.");
        }
        typedDimension.reset();
    }

    /*public SweepDimension(Object object, String accessorName, long start, float end, int increment) {
    	this(object, accessorName);
        if (propertyClass == Integer.TYPE) {
        	typedDimension = new IntDimension();
        	((IntDimension) typedDimension).start = start;
        	((IntDimension) typedDimension).end = end;
        	((IntDimension) typedDimension).increment = increment;
	    }
	    else {
	        throw new IllegalArgumentException("Type mismatch in creating " + accessorName + " sweep iterator.");
	    }
	    typedDimension.reset();
    }*/

    private Object[] assignValueArgs = new Object[1];

    private void assignValue(Object assignValue) {
        assignValueArgs[0] = assignValue;
        try {
            descriptor.getWriteMethod().invoke(object, assignValueArgs);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Exception in assign value write: " + e + ". Attempting to write " + assignValue + " to " + getName());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Exception in assign value write: " + e + ". Attempting to write " + assignValue + " to " + getName());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception (in invocation target) in assign value write: " + e.getTargetException() + ". Attempting to write " + assignValue + " to " + getName());
        }
    }

    /**
     * Returns the minimum value as a text string.
     */
    public String getMinAsText() {
        return typedDimension.getMinAsText();
    }

    /**
     * Sets the minimum value as a text string.
     */
    public void setMinAsText(String string) {
        typedDimension.setMinAsText(string);
    }

    /**
     * Returns the maximum value as a text string.
     */
    public String getMaxAsText() {
        return typedDimension.getMaxAsText();
    }

    /**
     * Sets the maximum value as a text string.
     */
    public void setMaxAsText(String string) {
        typedDimension.setMaxAsText(string);
    }

    /**
     * Returns the increment value as a text string.
     */
    public String getIncrementAsText() {
        return typedDimension.getIncrementAsText();
    }

    /**
     * Sets the increment value as a text string.
     */
    public void setIncrementAsText(String string) {
        typedDimension.setIncrementAsText(string);
    }

    /**
     * Resets the dimension to its original value.
     */
    public void reset() {
        typedDimension.reset();
    }

    /**
     * Returns true if the dimension hasn't yet reached its final (terminal) value.
     */
    public boolean hasNext() {
        return typedDimension.hasNext();
    }

    /**
     * Iterates to the next dimension value, setting the property of the object with the new value.
     */
    public Object next() {
        Object value = typedDimension.next();
        assignValue(value);
        return value;
    }
}
