package com.tssap.dtr.client.lib.protocol.impl;


public class HashMapIntObject {

	/**
	 * Default load factor for the hashtable.<p>
	 */
	public static final float LOAD_FACTOR = 0.75f;
	/**
	 * Default initial capacity for the hashtable.<p>
	 */
	public static final int INITIAL_CAPACITY = 13;
	/**
	 * Default grow step (<tt>newSize = oldSize * growStep</tt>) for the hashtable.<p>
	 */
	public static final int GROW_STEP = 2;
	/**
	 * Last element in the list.<p>
	 */
	protected static final int LAST = -1;

	/**
	 * Current loaf factor (0.0, 1.0].<p>
	 */
	protected float loadFactor = LOAD_FACTOR;

	/**
	 * Index in the simple number database.<p>
	 */
	protected int simplIndex = 0;

	/** Limit for the hash table (<tt>limit = capacity * loadFactor</tt>).<p> */
	protected int limit;
	/**
	 * Capacity of the hash table.<p>
	 */
	protected int capacity;
	/**
	 * Pointer to list of free slots.<p>
	 */
	protected int nextFree;
	/**
	 * Keys.<p>
	 */
	protected int keys[];
	/**
	 * Values.<p>
	 */
	protected Object elements[];
	/**
	 * Pointer to next slot in case of collision.<p>
	 */
	protected int nextPtr[];

	protected int count;

	static final protected int[] primes = 
	  {13, 17, 19, 23, 29, 31, 37, 43, 53, 61, 73, 89, 107, 127, 149, 179,
	   223, 257, 307, 367, 439, 523, 631, 757, 907, 1087, 1301, 1559, 1871,
	   2243, 2689, 3229, 3877, 4649, 5581, 6689, 8039, 9631, 11579, 13873,
	   16649, 19973, 23971, 28753, 34511, 41411, 49697, 59621, 71549, 85853,
	   103043, 123631, 148361, 178021, 213623, 256349, 307627, 369137, 442961,
	   531569, 637873, 765437, 918529, 1102237, 1322669, 1587221, 1904647,
	   2285581, 2742689, 3291221, 3949469, 4739363, 5687237, 6824669, 8189603,
	   9827537, 11793031, 14151629, 16981957, 20378357, 24454013, 29344823,
	   35213777, 42256531, 50707837, 60849407, 73019327, 87623147, 105147773,
	   126177323, 151412791, 181695341, 218034407, 261641287, 313969543,
	   376763459, 452116163, 542539391, 651047261, 781256711, 937508041,
	   1125009637, 1350011569, 1620013909, 1944016661, 2147483647};

	/**
	 * Retrieves the count of the elements in the structure.<p>
	 *
	 * @return   the count of the elements in the structure.
	 */
	public int size() {
		return count;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public HashMapIntObject() {
		init(INITIAL_CAPACITY);
	}

	/**
	 * Returns an array of the keys in this hashtable.<p>
	 *
	 * @return  array of the keys in this hashtable.
	 */
	public int[] keys() {
		int index = 0;
		int[] result = new int[count];

		for (int i = 0; i < capacity; i++) {
			for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
				result[index++] = keys[pos - capacity];
			}
		}

		return result;
	}


	/**
	 * Tests if the specified key is a key in this hashtable.<p>
	 *
	 * @param   <tt>key</tt> a possible element.
	 * @return  <tt>true</tt> if and only if the specified element is in this
	 *          hashtable, <tt>false</tt> otherwise.
	 */
	public boolean containsKey(int key) {
		int pos = nextPtr[hash(key) % capacity];

		while (pos != LAST) {
			if (keys[pos - capacity] == key) {
				return true;
			}

			pos = nextPtr[pos];
		}

		return false;
	}

	/**
	 * Clears this hashtable so that it contains no keys.<p>
	 */
	public void clear() {
		for (int i = 0; i < capacity; i++) {
			nextPtr[i] = LAST;
		}

		for (int i = capacity; i < nextPtr.length;) { //$JL-ASSIGN$
			nextPtr[i] = ++i;
		}

		nextFree = capacity;
		count = 0;
	}

	public Object get(int key) {
		int index;
		int pos = nextPtr[hash(key) % capacity];

		while (pos != LAST) {
			index = pos - capacity;

			if (keys[index] == key) {
				return elements[index];
			}

			pos = nextPtr[pos];
		}

		return null;
	}

	/**
	 * Maps the specified key to the specified value in this hashtable.
	 *
	 * The value can be retrieved by calling the <code>get()</code> method with a key that
	 * is equal to the original key.
	 *
	 * @param   <tt>key</tt> hashtable key.
	 * @param   <tt>value</tt> value that key is to be mapped to.
	 * @return  <tt>true</tt> if key has been mapped in this hashtable (old value is replaced), 
	 *          <tt>false</tt> otherwise (new entry has created).
	 */
	public void put(int key, Object value) {
		if (count == limit) {
			rehash();
		}

		int pos = hash(key) % capacity;
		int index;

		while (nextPtr[pos] != LAST) {
			pos = nextPtr[pos];
			index = pos - capacity;
			if (keys[index] == key) {
				elements[index] = value;
				return;
			}
		}

		index = nextFree - capacity;
		nextPtr[pos] = nextFree;
		keys[index] = key;
		elements[index] = value;
		nextFree = nextPtr[nextFree];
		nextPtr[nextPtr[pos]] = LAST;
		count++;
	}

	public void remove(int key) {
		int prevPos = hash(key) % capacity;
		int pos = nextPtr[prevPos];

		while (pos != LAST) {
			if (keys[pos - capacity] == key) {
				nextPtr[prevPos] = nextPtr[pos];
				nextPtr[pos] = nextFree;
				nextFree = pos;
				count--;
				return;
			}

			prevPos = pos;
			pos = nextPtr[pos];
		}
	}

	/**
	 * Put method for internal use. Not  and does not perform check for
	 * overflow.
	 *
	 * @param   <tt>key</tt> hashtable key.
	 * @param   <tt>value</tt> value that key is to be mapped to.
	 */
	protected void _put(int key, Object value) {
		int pos = hash(key) % capacity;

		while (nextPtr[pos] != LAST) {
			pos = nextPtr[pos];
		}

		int index = nextFree - capacity;
		nextPtr[pos] = nextFree;
		keys[index] = key;
		elements[index] = value;
		nextFree = nextPtr[nextFree];
		nextPtr[nextPtr[pos]] = LAST;
		count++;
	}

	protected void init(int initialCapacity) {
		int n = (simplIndex < primes.length) ? simplIndex : primes.length;
		for (; primes[n] < initialCapacity; n++); 
		long l = ((long)n << 32) | primes[n];

		simplIndex = (int) (l >> 32) + 4;
		this.capacity = (int)l;

		limit = (int) (capacity * loadFactor);
		nextPtr = new int[capacity + limit];

		for (int i = 0; i < capacity; i++) {
			nextPtr[i] = LAST;
		}

		for (int i = capacity; i < nextPtr.length;) { //$JL-ASSIGN$
			nextPtr[i] = ++i;
		}

		keys = new int[limit];
		elements = new Object[limit];
		nextFree = capacity;
		count = 0;
	}

	protected long getClosestPrime(int key, int startPos) {
		int i = (startPos < primes.length) ? startPos : primes.length;
		for (; primes[i] < key; i++);
		return ((long)i << 32) | primes[i];
	}

	/**
	 * Increases the capacity of this hashtable and internally reorganizes
	 * it to accommodate and access its entries more efficiently.
	 * This method is called automatically when the
	 * number of keys in the hashtable exceeds this hashtable capacity
	 * and load factor.<p>
	 */
	protected void rehash() {
		int[] oldKeys = keys;
		Object[] oldElements = elements;
		init(capacity * GROW_STEP);

		for (int i = 0; i < oldKeys.length; i++) {
			_put(oldKeys[i], oldElements[i]);
		}
	}

	protected int hash(int key) {
		return key & 0x7fffffff;
	}

	//  /**
	//   * Returns a string representation of this hashtable object
	//   * in the form of a set of entries, enclosed in braces and separated
	//   * by the ASCII characters <tt>, </tt> (comma and space). Each
	//   * entry is rendered as the key, an equality sign <tt>=</tt>, and the
	//   * associated element, where the <code>toString</code> method is used to
	//   * convert the key and element to strings. Overrides the
	//   * <code>toString</code> method of <code>java.lang.Object</code>.<p>
	//   *
	//   * @return  a string representation of this hashtable.
	//   */
	//  public String toString() {
	//    int c = 0;
	//    int index;
	//    StringBuffer buf = new StringBuffer();
	//    buf.append("{");
	//
	//    for (int i = 0; i < capacity; i++) {
	//      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
	//        index = pos - capacity;
	//        buf.append(keys[index] + "=" + elements[index]);
	//
	//        if (++c < count) {
	//          buf.append(", ");
	//        }
	//      } 
	//    } 
	//
	//    buf.append("}");
	//    return buf.toString();
	//  }

}
