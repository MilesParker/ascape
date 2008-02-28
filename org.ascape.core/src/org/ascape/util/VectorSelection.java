/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * A selection imposed on a Vector. Methods are provided for selecting elements, and notifying
 * observers of changes in selction status, but not changes in the underlying vector class.
 * (This class will be generalized for all collections, and collection observer functionality
 * will probably be provided when collections are integrated into the main engine, which should
 * happen after Java 2 (JDK 1.2) becomes commonplace.)
 * <i>Important:</i> because Vectors are not observable, underlying vectors must call the update method
 * on any enclosing vector selection if any changes are made to them.
 * If you want to be able to select Vector elements based on name, implement the HasName interface.
 * Should be thread safe, but not tested. Performance emphasis is on accessing selected elements,
 * not setting selection. Accessing selected elements should be quite fast since an array of
 * selected elements is stored locally.
 *
 * @author Miles Parker
 * @version 1.9.2 2/5/01
 * @history 1.0 based on other previous classes
 * @history 1.9.2 2/5/01 fixed bug in setSelected(String...), fixed error reporting
 * @history 1.0.1 3/9/99 documentated
 * @since 1.0
 * @see HasName
 * @see org.ascape.util.data.DataSelection
 */
public class VectorSelection extends Observable implements Observer, Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The underlying vector that we are imposing a seleciton upon.
     */
    protected Vector vector;

    /**
     * An array of selected elements of the vector.
     */
//    private Object[] selectedElements = new Object[0];

    private List selectedElementsList = new LinkedList();

    /**
     * An array for selection status for all elements in the vector.
     */
//    private boolean[] elementIsSelected = new boolean[0];

    /**
     * The number of elements currently selected.
     */
    private int selectedCount = 0;

    /**
     * Construct a new VectorSelection.
     */
    public VectorSelection() {
    }

    /**
     * Construct a new VectorSelection.
     * @param vector the underlying vector
     */
    public VectorSelection(Vector vector) {
        setVector(vector);
    }

    /**
     * Set the underlying vector for this selection.
     * If any underlying elements are the same as in a previous vector,
     * their selection status should be retained.
     */
    public synchronized void setVector(Vector vector) {
        this.vector = vector;
        //vector.addObserver(this);
        update();
    }

    /**
     * Returns the underlying vector.
     */
    public Vector getVector() {
        return vector;
    }

    /**
     * Returns the current complete seleciton of vector elements.
     */
    public Object[] getSelection() {
//        return selectedElements;
        return selectedElementsList.toArray();
    }

    /**
     * Add an elelemnt to the underlying vector. Automatically calls update.
     * @param object the object to add
     * @param select should the object be intitially selected?
     */
    public void addElement(Object object, boolean select) {
        if (!vector.contains(object)) {
            vector.addElement(object);
            update();
        }
        setSelected(object, select);
    }

    /**
     * Removes an elemnt from the underlying vector. Automatically calls update.
     * @param object the object to add
     */
    public void removeElement(Object object) {
        if (vector.contains(object)) {
            vector.removeElement(object);
            update();
        }
    }

    /**
     * Add an elelemnt to the underlying vector. Automatically calls update.
     * Object is initially selected.
     */
    public void addElement(Object object) {
        addElement(object, true);
    }

    /**
     * Is the supplied object selected in this selection?
     * @param elem the object to determine selection status for
     * @return true if the item is selected, false if the item is not selected or is not in the list
     */
    public boolean isSelected(Object elem) {
//        for (int i = 0; i < selectedElements.length; i++) {
//            if (selectedElements[i] == elem) {
//                return true;
//            }
//        }
        return selectedElementsList.contains(elem);
    }

    /**
     * Is the object at the supplied index selected in this selection?
     * @param index the vector index of the object to determine selection status for
     * @return true if the item is selected
     */
    public boolean isSelected(int index) {
//        return isSelected(vec)
        return selectedElementsList.contains(vector.get(index));
    }

    /**
     * Selects every element in the vector.
     */
    public synchronized void selectAll() {
        selectedCount = vector.size();
//        for (int i = 0; i < elementIsSelected.length; i++) {
//            elementIsSelected[i] = true;
//        }
        selectedElementsList.clear();
        selectedElementsList.addAll(vector);
        setChanged();
        notifyObservers();
    }

    /**
     * Sets selection to none, unselecting every element in the vector.
     */
    public synchronized void clearSelection() {
//        selectedElements = new Object[0];
//        for (int i = 0; i < elementIsSelected.length; i++) {
//            elementIsSelected[i] = false;
//        }
        selectedElementsList.clear();
        selectedCount = 0;
    }

    /**
     * Sets selection to none, unselecting every element in the vector.
     */
    public synchronized void clear() {
        vector.removeAllElements();
        clearSelection();
    }

    /**
     * Selects the object with the supplied name.
     * @param name the name of the series to set select status for
     */
    public synchronized void select(String name) {
        setSelected(name, true);
    }

    /**
     * Unselects the object with the supplied name.
     * @param name the name of the series to set select status for
     */
    public synchronized void unselect(String name) {
        setSelected(name, false);
    }

    /**
     * Sets the object at the supplied index to the selection status indicated.
     * @param index the vector index of the object to set select status for
     * @param select true to select the item, false to unselect it
     */
    public synchronized void setSelected(int index, boolean select) {
        Object selectedElem = vector.get(index);

        if (select && !selectedElementsList.contains(selectedElem)) {
            //Find previous selected element..
            int previousIndex = vector.indexOf(selectedElem) - 1;
            for (; previousIndex >= 0; previousIndex--) {
                Object previousElem = vector.get(previousIndex);
                if (selectedElementsList.contains(previousElem)) {
                    previousIndex = selectedElementsList.indexOf(previousElem);
                    break;
                }
            }

            selectedCount++;
            selectedElementsList.add(Math.min(selectedElementsList.size(), previousIndex + 1), selectedElem);
        } else if (!select && selectedElementsList.contains(selectedElem)) {
            selectedCount--;
            selectedElementsList.remove(selectedElem);
        }
//        elementIsSelected[index] = select;
//        selectedElements = new Object[selectedCount];
//        int selectedIndex = 0;
//        for (int i = 0; i < elementIsSelected.length; i++) {
//            if (elementIsSelected[i]) {
//                selectedElements[selectedIndex] = vector.elementAt(i);
//                selectedIndex++;
//            }
//        }
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the object with the supplied name to the selection status indicated.
     * <i>Warning:</i> Expects Vector members to implement of <code>HaveName</code>.
     * @param name the name of the series to set select status for
     * @param select true to select the item, false to unselect it
     * @exception ClassCastException if vector elements are not instances of HaveName
     */
    public synchronized void setSelected(String name, boolean select) {
        Enumeration e = vector.elements();
        int index = 0;
        while (e.hasMoreElements()) {
            String candidateName = ((HasName) e.nextElement()).getName();
            if ((candidateName.equalsIgnoreCase(name)) || (candidateName.replaceAll(" ", "").equalsIgnoreCase(name))) {
                setSelected(index, select);
                setChanged();
                notifyObservers();
                return;
            }
            index++;
        }
        throw new RuntimeException("Tried to select an item in VectorSelection that does not exit: " + name);
    }

    /**
     * Sets the supplied object to the selection status indicated.
     * @param elem the object to set selection status for
     * @param select true to select the item, false to unselect it
     * @exception RuntimeException if the element does not exist in the vector
     */
    public synchronized void setSelected(Object elem, boolean select) {
        int index = vector.indexOf(elem);
        if (index != -1) {
            setSelected(index, select);
        } else {
            throw new RuntimeException("Tried to select an item in VectorSelection that does not exit: " + elem);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the number of selected elements.
     */
    public int getSelectionSize() {
        return selectedCount;
    }

    /**
     * Returns the index in the vector of the object at the provided selection index.
     * @param index the selection index to get the vector index for
     */
    public int getIndexInVector(int index) {
        try {
            Object elem = selectedElementsList.get(index);
            return vector.indexOf(elem);
        } catch (NullPointerException e) {
            throw new RuntimeException("Object does not exist in vector");
        }
//        int count = 0;
//        for (int i = 0; i < elementIsSelected.length; i++) {
//            if (elementIsSelected[i]) {
//                if (count == index) {
//                    return i;
//                }
//                count++;
//            }
//        }
//        throw new RuntimeException("Object does not exist in vector");
    }

    public int getSelectedIndex(Object object) {
        return selectedElementsList.indexOf(object);
    }

    /**
     * Returns the object at the provided selection index.
     * @param index the index in this selection of the series
     */
    public Object getSelectedElement(int index) {
        return vector.elementAt(getIndexInVector(index));
    }

    /**
     * Normally, notifies the selection that a change in the backing vector has occured.
     * Not used at the moment, because Vector isn't observable. Will probably be
     * implmented when java collections are fully implemeneted in ascape.
     */
    public void update(Observable observed, Object arg) {
        update();
        setChanged();
        notifyObservers();
    }

    /**
     * Updates the entire vector, retaining current element selection status
     * and relative selection order. May be costly, especially for large collections,
     * as this class is optimized for the selection access case.
     * Calls to this method may be batched, that is, you can make a number of changes
     * to the underlying vector and call update one time.
     */
    public synchronized void update() {
//        boolean[] newElementIsSelected = new boolean[vector.size()];
//        Vector newSelectedElementsVector = new Vector();
        List newSelectedElementsList = new LinkedList();
        Enumeration e = vector.elements();
//        int index = 0;
        //We need to retain the original order of the vector,
        //so we'll iterate through it to avoid any complications.
        while (e.hasMoreElements()) {
            Object elem = e.nextElement();
            if (selectedElementsList.contains(elem)) {
                newSelectedElementsList.add(elem);
            }
//            for (int i = 0; i < selectedElements.length; i++) {
//                if (selectedElements[i] == elem) {
//                    newElementIsSelected[index] = true;
//                    newSelectedElementsVector.addElement(selectedElements[i]);
//                    break;
//                }
//            }
//            index++;
        }
//        elementIsSelected = newElementIsSelected;
//        selectedElements = new Object[newSelectedElementsVector.size()];
//        newSelectedElementsVector.copyInto(selectedElements);
        selectedElementsList.clear();
        selectedElementsList.addAll(newSelectedElementsList);
    }

    /**
     * Performs shallow copy, cloning this vector selection, with a shared backing vector.
     */
    public Object clone() {
        try {
            VectorSelection clone = (VectorSelection) super.clone();
            clone.vector = (Vector) vector.clone();
            clone.selectedElementsList = (List) ((LinkedList) selectedElementsList).clone();
//            clone.selectedElements = new Object[selectedElements.length];
//            System.arraycopy(selectedElements, 0, clone.selectedElements, 0, selectedElements.length);
//            clone.elementIsSelected = new boolean[elementIsSelected.length];
//            System.arraycopy(elementIsSelected, 0, clone.elementIsSelected, 0, elementIsSelected.length);
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public synchronized void moveToFront(Object elem) {
        if (vector.contains(elem)) {
            vector.remove(elem);
            vector.add(0, elem);
        } else {
            throw new RuntimeException("Tried to move an element that's not in the vector: " + elem);
        }
        update();
        setChanged();
        notifyObservers();
    }

    public synchronized void moveToBack(Object elem) {
        if (vector.contains(elem)) {
            vector.remove(elem);
            vector.add(vector.size(), elem);
        } else {
            throw new RuntimeException("Tried to move an element that's not in the vector: " + elem);
        }
        update();
        setChanged();
        notifyObservers();
    }



    /**
     * Returns a string represention of the vector selection.
     */
    public String toString() {
        String desc = "";
        Enumeration e = vector.elements();
//        int index = 0;
        //We need to retain the original order of the vector,
        //so we'll iterate through it to avoid any complications.
        while (e.hasMoreElements()) {
            Object elem = e.nextElement();
            desc += elem.toString();
//            if (elementIsSelected[index]) {
            if (selectedElementsList.contains(elem)) {
                desc += " [Selected]";
            } else {
                desc += " [Not Selected]";
            }
            desc += "\n";
//            index++;
        }
        if (desc.equals("")) {
            desc = "[Empty]";
        }
        return desc;
    }
}
