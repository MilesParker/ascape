/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * A class containing an object and a property descriptor.
 * Used to dynamically reflect accessor and invoke objects.
 *
 * @author Miles Parker
 * @version 1.9.2
 * @history 1.9.2 9/29/2000 small change to getAsText to test for null pointer condition and write a warning, instead of breaking
 * @since 1.0
 */
public class PropertyAccessor implements Comparable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Object object;

    protected PropertyDescriptor descriptor;

    protected Class propertyClass;

    public PropertyAccessor(Object object, PropertyDescriptor descriptor) {
        this.object = object;
        this.descriptor = descriptor;
        propertyClass = descriptor.getPropertyType();
    }

    public PropertyAccessor(Object object, String accessorName) {
        this.object = object;
        try {
            this.descriptor = new PropertyDescriptor(accessorName, object.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException("IntrospectionException; Likely cause: Property accessor name doesn't match any properties: " + object.getClass() + "." + accessorName);
        }
        propertyClass = descriptor.getPropertyType();
    }

    public PropertyDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public String getName() {
        return descriptor.getName();
    }

    public String getLongName() {
        return getLongName(descriptor);
    }

    public final static String getLongName(PropertyDescriptor descriptor) {
        return removeUnderscores(addSpacesToVariableName(capitalizeFirstCharacter(descriptor.getName())));
    }

    public String getAsText() {
        return getAsText(object, descriptor);
    }

    public final static String getAsText(Object object, PropertyDescriptor descriptor) {
        try {
            return getValue(object, descriptor).toString();
        } catch (NullPointerException e) {
            //This is no longer an issue as we want to be able to report null objects for methods w/o write access
            //            System.out.println("Warning, object " + object + " returns null for " + getLongName() + "; value=" + getValue());
            return "(null)";
        }
    }

    public static Object stringAsClass(Class wrapper, String string) throws NumberFormatException, IllegalArgumentException {
        //Tried doing this through introspection, but haven't found a way to determine
        //the wrapper class for a primitive type at run-time. Let me know if you have
        //found a way to do this.
        try {
            final Object[] enumValues = wrapper.getEnumConstants();
            if (enumValues != null) {
                for (Object object : enumValues) {
                    final Enum<?> en = (Enum<?>) object;
                    if (en.toString().equals(string)) {
                        return en;
                    }
                }
                throw new IllegalArgumentException("No enumerator matching: " + string + " in " + wrapper.getName());
            } else if (wrapper == Integer.TYPE) {
                return new Integer(string);
            } else if (wrapper == Double.TYPE) {
                return new Double(string);
            } else if (wrapper == Float.TYPE) {
                return new Float(string);
            } else if (wrapper == Long.TYPE) {
                return new Long(string);
            } else if (wrapper == Short.TYPE) {
                return new Short(string);
            } else if (wrapper == Byte.TYPE) {
                return new Byte(string);
            } else if (wrapper == BigDecimal.class) {
                return new BigDecimal(string);
            } else if (wrapper == BigInteger.class) {
                return new BigInteger(string);
            } else if (wrapper == Boolean.TYPE) {
                //added to support other common string representations of booleans
                if (string.length() > 0) {
                    String firstLetter = string.substring(0, 1);
                    if (string.equals("0")) {
                        return new Boolean(false);
                    } else if (string.equals("1")) {
                        return new Boolean(true);
                    } else if (firstLetter.equalsIgnoreCase("n")) {
                        return new Boolean(false);
                    } else if (firstLetter.equalsIgnoreCase("y")) {
                        return new Boolean(true);
                    } else if (firstLetter.equalsIgnoreCase("f")) {
                        return new Boolean(false);
                    } else if (firstLetter.equalsIgnoreCase("t")) {
                        return new Boolean(true);
                    } else {
                        return new Boolean(string);
                    }
                } else {
                    return null;
                }
            }
            //Don't forget this one...
            else if (wrapper == String.class) {
                return string;
            } else {
                throw new IllegalArgumentException("A conversion method hasn't been defined for " + wrapper.toString());
            }
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public void setAsText(String string) throws InvocationTargetException, IllegalArgumentException {
        setAsText(object, string, descriptor);
    }

    public static void setAsText(Object object, String string, PropertyDescriptor descriptor) throws InvocationTargetException, IllegalArgumentException {
        Object[] args = new Object[1];
        try {
            args[0] = stringAsClass(descriptor.getPropertyType(), string);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(string + " can not be converted to (" + descriptor.getPropertyType() + ") " + descriptor.getPropertyType().getName());
        }
        try {
            descriptor.getWriteMethod().invoke(object, args);
        }
        //mtp bug quick fix
        catch (NullPointerException e) {
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Set as text won't work for property " + descriptor.getName() + ", access too restricted: " + e);
        }
    }

    public boolean isWriteable() {
        return isWriteable(descriptor);
    }

    public static boolean isWriteable(PropertyDescriptor descriptor) {
        boolean writeable = descriptor.getWriteMethod() != null;
        try {
            stringAsClass(descriptor.getPropertyType(), "");
        } catch (IllegalArgumentException e) {
            //only allow writing to those object we know how to write to!
            writeable = false;
        }
        return writeable;
    }

    public Object getValue() {
        return getValue(object, descriptor);
    }

    public final static Object getValue(Object object, PropertyDescriptor descriptor) {
        try {
            return descriptor.getReadMethod().invoke(object, (Object[]) null);
        } catch (IllegalAccessException e) {
            System.out.println("Error in dynamic method read: " + e);
        } catch (IllegalArgumentException e) {
            System.out.println("Error in dynamic method read: " + e);
        } catch (InvocationTargetException e) {
            System.out.println("Error in dynamic method read: " + e);
        } catch (NullPointerException e) {
            System.out.println("No read method for property: " + descriptor.getName());
        }
        return null;
    }

    public void setValue(Object value) {
        try {
            Object[] args = new Object[1];
            args[0] = value;
            descriptor.getWriteMethod().invoke(object, args);
        } catch (IllegalAccessException e) {
            System.out.println("Error in dynamic method read: " + e);
        } catch (InvocationTargetException e) {
            System.out.println("Error in dynamic method read: " + e);
        }
    }

    /**
     * Returns an array of all read/write accessors for the supplied object.
     */
    public static List determineReadWriteAccessors(Object object, Class stopClass, boolean includeGetters)
    throws IntrospectionException {
        ArrayList tempPropertyAccessors = new ArrayList();
        PropertyDescriptor[] infoProperties;
        if (stopClass == null || object.getClass() == stopClass) {
            infoProperties = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
        } else {
            infoProperties = Introspector.getBeanInfo(object.getClass(), stopClass).getPropertyDescriptors();
        }
        for (int i = 0; i < infoProperties.length; i++) {
            //if (infoProperties[i].getWriteMethod() != null && infoProperties[i].getReadMethod() != null && ((infoProperties[i].getReadMethod().getReturnType() == Integer.TYPE) || (infoProperties[i].getReadMethod().getReturnType() == Double.TYPE) || (infoProperties[i].getReadMethod().getReturnType() == Float.TYPE))) {
            if ((includeGetters || infoProperties[i].getWriteMethod() != null) && infoProperties[i].getReadMethod() != null) {
                Class pt = infoProperties[i].getReadMethod().getReturnType();
                if (pt == Integer.TYPE || pt == Double.TYPE || pt == String.class || pt == Float.TYPE || pt == Boolean.TYPE) {
                    //System.out.println(infoProperties[j].getName());
                    tempPropertyAccessors.add(new PropertyAccessor(object, infoProperties[i]));
                }
            }
        }
        return tempPropertyAccessors;
    }

    public final static String capitalizeFirstCharacter(String string) {
        if (string.length() > 1) {
            return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
        } else {
            return string.toUpperCase();
        }
    }

    public final static String addSpacesToVariableName(String string) {
        if (string.length() > 0) {
            String newString = "" + string.charAt(0);
            for (int i = 1; i < string.length(); i++) {
                if (!Character.isUpperCase(string.charAt(i)) || Character.isUpperCase(string.charAt(i - 1))) {
                    newString += string.charAt(i);
                } else {
                    newString += " " + string.charAt(i);
                }
            }
            return newString;
        } else {
            return "";
        }
    }

    public static final String removeUnderscores(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '_') {
                if (i < string.length() - 1) {
                    string = string.substring(0, i) + string.substring(i + 1, string.length());
                } else {
                    string = string.substring(0, i);
                }
            }
        }
        return string;
    }

    public int compareTo(Object o) {
        return getName().compareTo(((PropertyAccessor) o).getName());
    }

    public static String paramName(String arg) {
        int equalAt = arg.lastIndexOf("=");
        if (equalAt > 0) {
            return arg.substring(0, equalAt);
        }
        return null;
    }

    public static String paramValue(String arg) {
        return arg.substring(arg.lastIndexOf("=") + 1);
    }

    public static Integer paramValueInt(String arg) {
        return (Integer) stringAsClass(Integer.TYPE, paramValue(arg));
    }

    public static Long paramValueLong(String arg) {
        return Long.parseLong(paramValue(arg));
    }

    public static Boolean paramValueBoolean(String arg) {
        return (Boolean) stringAsClass(Boolean.TYPE, paramValue(arg));
    }
}
