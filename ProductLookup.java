/**
 * Program #4
 * ProductLookup
 * cs310
 * 05/04/18
 * @jesuslopez cssc0736
 */
import data_structures.*;
import java.util.Iterator;

public class ProductLookup {

	private DictionaryADT<String, StockItem> dictionary;

	// Constructor. There is no argument-less constructor, or default size
	public ProductLookup(int maxSize) {
		dictionary = new Hashtable<String, StockItem>(maxSize);
	}

	// Adds a new StockItem to the dictionary
	public void addItem(String SKU, StockItem item) {
		dictionary.add(SKU, item);
	}

	// Returns the StockItem associated with the given SKU, if it is
	// in the ProductLookup, null if it is not.
	public StockItem getItem(String SKU) {
		return dictionary.getValue(SKU);

	}

	// Returns the retail price associated with the given SKU value.
	// -.01 if the item is not in the dictionary
	public float getRetail(String SKU) {
		StockItem retail =  dictionary.getValue(SKU);
		if(retail== null)
			return -.01f;
		return retail.getRetail();

	}

	// Returns the cost price associated with the given SKU value.
	// -.01 if the item is not in the dictionary
	public float getCost(String SKU) {
		StockItem cost = dictionary.getValue(SKU);
		if(cost == null)
			return -.01f;
		return cost.getCost();
	}

	// Returns the description of the item, null if not in the dictionary.
	public String getDescription(String SKU) {
		StockItem description = dictionary.getValue(SKU);
		if(description == null)
			return null;
		return description.getDescription();
	}

	// Deletes the StockItem associated with the SKU if it is
	// in the ProductLookup. Returns true if it was found and
	// deleted, otherwise false.
	public boolean deleteItem(String SKU) {
		return dictionary.delete(SKU);
	}

	// Prints a directory of all StockItems with their associated
	// price, in sorted order (ordered by SKU).
	public void printAll() {
		Iterator<StockItem> iter = dictionary.values();
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	}

	// Prints a directory of all StockItems from the given vendor,
	// in sorted order (ordered by SKU).
	public void print(String vendor) {
		Iterator<StockItem> iter = dictionary.values();
		StockItem temp = null;
		while(iter.hasNext()) {
			temp = iter.next();
			if(((Comparable<String>)temp.vendor).compareTo(vendor) == 0)
				System.out.println(temp);
		}
	}

	// An iterator of the SKU keys.
	public Iterator<String> keys() {
		return dictionary.keys();
	}

	// An iterator of the StockItem values.
	public Iterator<StockItem> values() {
		return dictionary.values();
	}
}