/**
 * Program #4
 * Hashtable
 * cs310
 * 05/04/18
 * @jesuslopez cssc0736
 */
package data_structures;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Hashtable<K extends Comparable<K>, V> implements DictionaryADT<K, V> {
	private LinkedList<DictionaryNode<K, V>>[] list;
	private int maxSize;
	private int tableSize;
	private int currentSize;
	private long modCounter;

	public Hashtable(int max) {
		modCounter = 0;
		maxSize = max;
		currentSize = 0;
		tableSize = (int) (max * 1.3f); // #of elements in array
		list = new LinkedList[tableSize]; // create an array of LL
		for (int i = 0; i < tableSize; i++)
			list[i] = new LinkedList<DictionaryNode<K, V>>();
	}

	private int getHashCode(K key) {
		return (key.hashCode() & 0x7FFFFFFF) % tableSize;
	}

	// Returns true if the dictionary has an object identified by 
	// key in it, otherwise false.
	public boolean contains(K key) {
		return list[getHashCode(key)].contains(new DictionaryNode<K, V>(key, null));
	}

	// Adds the given key/value pair to the dictionary. Returns
	// false if the dictionary is full, or if the key is a duplicate. 
	// Returns true if addition succeeded.
	public boolean add(K key, V value) {
		if (isFull() || contains(key))
			return false;
		list[getHashCode(key)].addFirst(new DictionaryNode<K, V>(key, value));
		currentSize++;
		modCounter++;
		return true;
	}

	// Deletes the key/value pair identified by the key parameter. 
	// Returns true if the key/value pair was found and removed, 
	// otherwise false.
	public boolean delete(K key) {
		if (isEmpty() || !contains(key))
			return false;
		list[getHashCode(key)].remove(new DictionaryNode<K, V>(key, null));
		currentSize--;
		modCounter++;
		return true;
	}

	// Returns the value associated with the parameter key. Returns 
	// null if the key is not found or the dictionary is empty.
	public V getValue(K key) {
		if (isEmpty() || !contains(key))
			return null;
		DictionaryNode<K, V> tmp = list[getHashCode(key)]
				.get(list[getHashCode(key)].locate(new DictionaryNode<K, V>(key, null)));
		if (tmp == null)
			return null;
		return tmp.value;
	}

	// Returns the key associated with the parameter value. Returns 
	// null if the value is not found in the dictionary. If more
	// than one key exists that matches the given value, returns the 
	// first one found.
	public K getKey(V value) {
		if (isEmpty())
			return null;
		for (int i = 0; i < list.length; i++) {
			Iterator<DictionaryNode<K, V>> items = list[i].iterator();
			while (items.hasNext()) {
				DictionaryNode<K, V> tmp = items.next();
				if (((Comparable<V>) tmp.value).compareTo((V) value) == 0)
					return (K) tmp.key;
			}
		}
		return null;
	}

	// Returns the number of key/value pairs currently stored 
	// in the dictionary
	public int size() {
		return currentSize;
	}

	// Returns true if the dictionary is at max capacity
	public boolean isFull() {
		return currentSize == maxSize;
	}

	// Returns true if the dictionary is empty
	public boolean isEmpty() {
		return currentSize == 0;
	}

	// Returns the Dictionary object to an empty state.
	public void clear() {
		for (LinkedList x : list) // ?
			x.clear();
		currentSize = 0;
		modCounter++;
	}

	// Returns an Iterator of the keys in the dictionary, in ascending
	// sorted order. The iterator is fail-fast.
	public Iterator<K> keys() {
		return new KeyIteratorHelper();
	}

	// Returns an Iterator of the values in the dictionary. The 
	// order of the values must match the order of the keys.
	// The iterator must be fail-fast.
	public Iterator<V> values() {
		return new ValueIteratorHelper();
	}

	//////////////////////////////////////////////////////////////////
	abstract class IteratorHelper<E> implements Iterator<E> {
		protected DictionaryNode<K, V>[] nodes;
		protected int index;
		protected long modificationCounter;

		public IteratorHelper() {
			nodes = new DictionaryNode[currentSize];
			index = 0;
			int j = 0;
			modificationCounter = modCounter;

			for (int i = 0; i < tableSize; i++)
				for (DictionaryNode n : list[i])
					nodes[j++] = n;
			nodes = (DictionaryNode<K, V>[]) shellSort(nodes);
		}

		public boolean hasNext() {
			if (modificationCounter != modCounter)
				throw new ConcurrentModificationException();
			return index < currentSize;
		}

		public abstract E next();

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	class KeyIteratorHelper<K> extends IteratorHelper<K> {
		public KeyIteratorHelper() {
			super();
		}

		public K next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return (K) nodes[index++].key;
		}
	}

	class ValueIteratorHelper<V> extends IteratorHelper<V> {
		public ValueIteratorHelper() {
			super();
		}

		public V next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return (V) nodes[index++].value;
		}
	}

	class DictionaryNode<K, V> implements Comparable<DictionaryNode<K, V>> {
		K key;
		V value;

		public DictionaryNode(K k, V v) {
			key = k;
			value = v;
		}

		public int compareTo(DictionaryNode<K, V> node) {
			return ((Comparable<K>) key).compareTo((K) node.key);
		}
	}

	private DictionaryNode<K, V>[] shellSort(DictionaryNode[] array) {
		DictionaryNode<K, V>[] n = array;
		int in, out, h = 1;
		DictionaryNode<K, V> tmp = null;
		int size = n.length;
		while (h <= size / 3) // calculate gaps
			h = h * 3 + 1;
		while (h > 0) {
			for (out = h; out < size; out++) {
				tmp = n[out];
				in = out;
				while (in > h - 1 && n[in - h].compareTo(tmp) >= 0) {
					n[in] = n[in - h];
					in -= h;
				}
				n[in] = tmp;
			} // end for
			h = (h - 1) / 3;
		} // end while
		return n;
	}

	////////////////////////////////////////////////////////////////

	private class LinkedList<E> implements Iterable<E> {
		private int currentSize;
		private Node<E> head, tail;

		public LinkedList() {
			currentSize = 0;
			head = tail = null;
		}

		public void addLast(E obj) {
			Node<E> newNode = new Node(obj);
			if (tail == null)
				head = tail = newNode;
			else {
				tail.next = newNode;
				tail = newNode;
			}
			currentSize++;
		}

		public void addFirst(E obj) {
			Node<E> newNode = new Node(obj);
			if (head == null)
				head = tail = newNode;
			else {
				newNode.next = head;
				head = newNode;
			}
			currentSize++;
		}

		public void insert(E obj, int location) {
			if (location > currentSize + 1 || location < 1)
				throw new RuntimeException("invalid input");
			Node<E> newNode = new Node(obj);
			Node<E> current = head, previous = null;
			int where = 1;
			while (current != null && location > where) {
				previous = current;
				current = current.next;
				where++;
			}

			if (previous == null) {
				newNode.next = head;
				head = tail = newNode;
			} else if (location == currentSize + 1) {
				tail.next = newNode;
				tail = newNode;
			} else {
				previous.next = newNode;
				newNode.next = current;
			}
			currentSize++;
		}

		public E remove(int location) {
			if (location > currentSize || location < 1)
				throw new RuntimeException("invalid input");
			Node<E> current = head, previous = null;
			int where = 1;
			while (current != null && location > where) {
				previous = current;
				current = current.next;
				where++;
			}
			E tmp = current.data;
			if (current == head)
				head = head.next;
			else if (current == tail) {
				previous.next = null;
				tail = previous;
			} else
				previous.next = current.next;
			if (head == null)
				tail = null;
			currentSize--;
			return tmp;
		}

		public E remove(E obj) {
			Node<E> current = head;
			Node<E> previous = null;
			while (current != null && ((Comparable<E>) obj).compareTo(current.data) != 0) {
				previous = current;
				current = current.next;
			}
			if (current == null)
				return null;
			else if (current == head)
				head = head.next;
			else if (current == tail) {
				previous.next = null;
				tail = previous;
			} else
				previous.next = current.next;
			E tmp = current.data;
			if (head == null)
				tail = null;
			currentSize--;
			return tmp;
		}

		public E removeFirst() {
			if (head == null)
				return null;
			E tmp = head.data;
			head = head.next;
			currentSize--;
			if (isEmpty())
				head = tail = null;
			return tmp;
		}

		public E removeLast() {
			Node<E> current = head, previous = null;
			if (current == null)
				return null;
			while (current.next != null) {
				previous = current;
				current = current.next;
			}

			E tmp = current.data;
			if (current == head)
				head = tail = null;
			else {
				previous.next = null;
				tail = previous;
			}
			currentSize--;
			return tmp;
		}

		public E get(int location) {
			if (location > currentSize || location < 1)
				throw new RuntimeException("invalid input");
			Node<E> current = head, previous = null;
			int where = 1;
			while (current.next != null && location > where) {
				previous = current;
				current = current.next;
				where++;
			}
			E tmp = current.data;
			return tmp;
		}

		public boolean contains(E obj) {
			Node<E> tmp = head;
			while (tmp != null) {
				if (((Comparable<E>) obj).compareTo(tmp.data) == 0)
					return true;
				tmp = tmp.next;
			}
			return false;
		}

		public int locate(E obj) {
			Node<E> current = head, previous = null;
			int where = 1;
			while (current != null && ((Comparable<E>) obj).compareTo(current.data) != 0) {
				previous = current;
				current = current.next;
				where++;
			}
			if (current == null)
				return -1;
			return where;
		}

		public void clear() {
			head = tail = null;
			currentSize = 0;
		}

		public boolean isEmpty() {
			return currentSize == 0;
		}

		public int size() {
			return currentSize;
		}

		public Iterator<E> iterator() {
			return new IteratorHelper();
		}

		class IteratorHelper implements Iterator<E> {
			Node<E> iterIndex;

			public IteratorHelper() {
				iterIndex = head;
			}

			public boolean hasNext() {
				return iterIndex != null;
			}

			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				E tmp = iterIndex.data;
				iterIndex = iterIndex.next;
				return tmp;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		}

		class Node<T> {
			T data;
			Node<T> next;

			public Node(T obj) {
				data = obj;
				next = null;
			}
		}

	}

}
