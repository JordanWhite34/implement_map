package maps;

//import java.sql.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Objects;
/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    // define reasonable default values for each of the following three fields
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = .75;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 5;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 11;


    /*
     Warning:
     You may not rename this field or change its type.
     We will be inspecting it in our secret tests.
    */

    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private final double threshold;
    private int size;
    private final int chainCapacity;
    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        //throw new UnsupportedOperationException("Not implemented yet.");
        this.threshold = resizingLoadFactorThreshold;
        this.size = 0;
        this.chainCapacity = chainInitialCapacity;

        if (initialChainCount > 0) {
            this.chains = createArrayOfChains(initialChainCount);
        } else {
            throw new IllegalArgumentException();
        }


    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {

        if (Objects.equals(key, null) || !Objects.equals(chains[Math.abs(key.hashCode()) % chains.length], null)) {
            if (Objects.equals(key, null)) {
                return chains[0].get(null);
            }
            return chains[Math.abs(key.hashCode()) % chains.length].get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (threshold <= size/chains.length) {
            AbstractIterableMap<K, V>[] newChains = createArrayOfChains(2 * size + 1);
            for (int i = 0; i < chains.length; i++) {
                AbstractIterableMap<K, V> temp = chains[i];
                if (!Objects.equals(temp, null)) {
                    Set<K> keys = temp.keySet();
                    for (K k : keys) {
                        if (Objects.equals(k, null)) {
                            if (Objects.equals(newChains[0], null)) {
                                newChains[0] = createChain(chainCapacity);
                            }
                            newChains[0].put(null, temp.get(null));
                        }
                        else {
                            int index = Math.abs(k.hashCode()) % newChains.length;
                            if (Objects.equals(newChains[index], null)) {
                                newChains[index] = createChain(chainCapacity);
                            }
                            newChains[index].put(k, temp.get(k));
                        }
                    }
                }
            }
            chains = newChains;
        }
        // index out of bounds, -2 ?
        if (Objects.equals(key, null) || Objects.equals(chains[Math.abs(key.hashCode()) % chains.length], null)) {
            AbstractIterableMap<K, V> newEntry = createChain(chainCapacity);
            if (Objects.equals(key, null)) {
                chains[0] = newEntry;
                size++;
                return chains[0].put(null, value);
            } else {
                chains[Math.abs(key.hashCode()) % chains.length] = newEntry;
            }

            //!chains[key.hashCode() % chains.length].containsKey(key)
        }
        if (!chains[Math.abs(key.hashCode()) % chains.length].containsKey(key)) {
            size++;
        }
        return chains[Math.abs(key.hashCode()) % chains.length].put(key, value);
    }

    @Override
    public V remove(Object key) {
        if (key == null && (chains[0].containsKey(null))) {
            size--;
            V temp = chains[0].remove(null);
            if (chains[0].size() == 0) {
                chains[0] = null;
            }
            return temp;
        }
        if (!Objects.equals(chains[Math.abs(key.hashCode()) % chains.length], null)) {
            size--;
            V temp = chains[Math.abs(key.hashCode()) % chains.length].remove(key);
            if (chains[Math.abs(key.hashCode()) % chains.length].size() == 0) {
                chains[Math.abs(key.hashCode()) % chains.length] = null;
            }
            return temp;
        }
        return null;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (Objects.equals(key, null)) {
            return chains[0].containsKey(null);
        }
        if (Objects.equals(chains[Math.abs(key.hashCode()) % chains.length], null)) {
            return false;
        }
        return chains[Math.abs(key.hashCode()) % chains.length].containsKey(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final AbstractIterableMap<K, V>[] chains;
        private int indexChain;
        private Iterator<Map.Entry<K, V>> iterator;
        // You may add more fields and constructor parameters

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.indexChain = 0;
            this.chains = chains;
            while (Objects.equals(this.chains[this.indexChain], null) && this.indexChain < chains.length - 1) {
                this.indexChain++;
            }
            if (!Objects.equals(this.chains[this.indexChain], null)) {
                this.iterator = this.chains[this.indexChain].iterator();
            }
        }


        @Override
        public boolean hasNext() {
            if (this.indexChain == chains.length - 1 && (Objects.equals(this.iterator, null) || !iterator.hasNext())) {
                return false;
            }
            else if (!Objects.equals(this.iterator, null) && iterator.hasNext()) {
                return true;
            }
            else {
                for (int i = this.indexChain + 1; i < chains.length; i++) {
                    if (!Objects.equals(this.chains[i], null)) {
                        this.iterator = this.chains[i].iterator();
                        indexChain = i;
                        return true;
                    }
                }
            }
            return false;
        }


        @Override
        public Map.Entry<K, V> next() {
           if (hasNext()) {
               return this.iterator.next();
           }
           else {
               throw new NoSuchElementException();
           }
        }
    }
}
