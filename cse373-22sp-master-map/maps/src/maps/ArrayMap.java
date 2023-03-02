package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    // TO: define a reasonable default value for the following field
    private static final int DEFAULT_INITIAL_CAPACITY = 51;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;
    private int size;


    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Size is not valid");
        } else {
            this.entries = new SimpleEntry[initialCapacity];
            size = 0;
        }
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                return entries[i].getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        SimpleEntry<K, V> newEntry = new SimpleEntry<>(key, value);

        if (size == entries.length - 1) {
            SimpleEntry<K, V>[] newArray = createArrayOfEntries(entries.length * 2);
            for (int i = 0; i < size; i++) {
                newArray[i] = entries[i];
            }
            entries = newArray;
        }

        if (size == 0) {
            entries[0] = newEntry;
            size++;
        } else if (get(key) != null) {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(entries[i].getKey(), key)) {
                    V prevVal = entries[i].getValue();
                    entries[i].setValue(value);
                    return prevVal;
                }
            }
        } else {
            entries[size] = newEntry;
            size++;
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                V getValue = entries[i].getValue();
                entries[i] = entries[size-1];
                entries[size-1] = null;
                size--;
                return getValue;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
       for (int i = 0; i < size; i++) {
           if (Objects.equals(entries[i].getKey(), key)) {
               return true;
           }
       }
       return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries);
    }

    // //  after you implement the iterator, remove this toString implementation
    // // Doing so will give you a better string representation for assertion errors the debugger.
    // @Override
    // public String toString() {
    //     return super.toString();
    // }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        // You may add more fields and constructor parameters
        private int currentIndex;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            currentIndex = 0;
            this.entries = entries;
        }

        @Override
        public boolean hasNext() {
            if (Objects.equals(entries[currentIndex], null)) {
                return false;
            }
            return true;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (hasNext()) {
                Map.Entry<K, V> element = entries[currentIndex];
                currentIndex++;
                return element;
            }
            throw new NoSuchElementException("No Entry");
        }
    }
}
