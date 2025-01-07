package com.sap.sdo.impl.util;

public final class ArrayListBehavior {

    private ArrayListBehavior() {
        // should only be used by static access
    }
    
    public static <E> E get(ArrayListContainer<E> container, int index) {
        rangeCheck(container, index);
        return container.getArray()[index];
    }

    public static <E> E set(ArrayListContainer<E> container, int index, E element) {
        rangeCheck(container, index);
        E[] elementData = container.getArray();
        E oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    public static <E> void add(ArrayListContainer<E> container, int index, E element) {
        int size = container.size();
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(container, size + 1); // Increments modCount!!
        E[] elementData = container.getArray();
        System.arraycopy(elementData, index, elementData, index + 1, size
            - index);
        elementData[index] = element;
        size++;
        container.setSize(size);
    }

    public static <E> E remove(ArrayListContainer<E> container, int index) {
        rangeCheck(container, index);

        container.increaseModCount();
        E[] elementData = container.getArray();
        E oldValue = elementData[index];
        int size = container.size();

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null; // Let gc do its work
        container.setSize(size);
        return oldValue;
    }
    
    public static <E> void clear(ArrayListContainer<E> container) {
        container.increaseModCount();
        container.setSize(0);
        container.setArray(null);
    }

    public static <E> void ensureCapacity(ArrayListContainer<E> container, int minCapacity) {
        container.increaseModCount();
        E[] elementData = container.getArray();
        if (elementData == null) {
            int newCapacity = minCapacity<=10?10:minCapacity;
            elementData = container.createArray(newCapacity);
            container.setArray(elementData);
        } else {
            int oldCapacity = elementData.length;
            if (minCapacity > oldCapacity) {
                Object oldData[] = elementData;
                int newCapacity = (oldCapacity * 3) / 2 + 1;
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                }
                elementData = container.createArray(newCapacity);
                container.setArray(elementData);
                System.arraycopy(oldData, 0, elementData, 0, container.size());
            }
        }
    }

    public static <E> void trimToSize(ArrayListContainer<E> container) {
        int size = container.size();
        if (size == 0) {
            container.setArray(null);
        } else {
            E[] elementData = container.getArray();
            int oldCapacity = elementData.length;
            if (size < oldCapacity) {
                Object oldData[] = elementData;
                elementData = container.createArray(size);
                container.setArray(elementData);
                System.arraycopy(oldData, 0, elementData, 0, size);
            }
        }
    }

    private static <E> void rangeCheck(ArrayListContainer<E> container, int index) {
        int size = container.size();
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

}
