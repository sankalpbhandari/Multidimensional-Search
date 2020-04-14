/*
  Starter code for MDS

  @authors: Angad Vittal(AXV180010)
 * Divyesh Patel(DGP170030)
 * Sankalp Bhandari(SSB170006)
 * Shraddha Bang(SXB180041)
 */

package ssb170006;

// If you want to create additional classes, place them in this file as subclasses of MDS

import java.util.*;


public class MDS {
    TreeMap<Long, Product> productMap; // Map of product id and product
    HashMap<Long, HashSet<Long>> nameMap; // Map of name and product ids

    // Constructors
    public MDS() {
        productMap = new TreeMap<>();
        nameMap = new HashMap<>();
    }

    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
    */

    /**
     * Insert a new product whose name is given in the list.
     * If an entry with the same id already exists, then its name and price are replaced
     * by the new values, unless list is null or empty, in which case, just the price is updated.
     *
     * @param id    id of product
     * @param price price of product
     * @param list  the list containing the name of the product
     * @return 1 if the product is new, and 0 otherwise.
     **/
    public int insert(long id, Money price, List<Long> list) {
        if (!productMap.containsKey(id)) {
            Product product = new Product(id, price, list);
            productMap.put(id, product);
            updateNameMap(id);
            return 1;
        }
        Product oldProduct = productMap.get(id);
        if (list != null && list.size() > 0) {
            removeNames(id, new ArrayList<>(oldProduct.name));
            oldProduct.name.clear();
            oldProduct.name.addAll(list);
            updateNameMap(id);
        }
        oldProduct.price = price;
        return 0;

    }

    /**
     * Updates the Name Map that is the reverse mapping of the product to names.
     *
     * @param id id of product.
     */
    private void updateNameMap(long id) {
        Product product = findProduct(id);
        for (long desc : product.name) {
            if (nameMap.containsKey(desc)) {
                nameMap.get(desc).add(id);
            } else {
                HashSet<Long> prodIds = new HashSet<>();
                prodIds.add(id);
                nameMap.put(desc, prodIds);
            }
        }
    }

    /**
     * Finds product of given id
     *
     * @param id id of product
     * @return the Product if it is found otherwise null
     **/
    private Product findProduct(long id) {
        return productMap.getOrDefault(id, null);
    }

    // b. Find(id): return price of item with given id (or 0, if not found).

    /**
     * Finds the price of product given it's id
     *
     * @param id id of product
     * @return price of product with given id or 0, if not found
     **/
    public Money find(long id) {
        Product product = findProduct(id);
        if (product != null)
            return product.price;
        return new Money();
    }

    /* 
       c. Delete(id): delete item from storage.  Returns the sum of the
       long ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
    */

    /**
     * Delete product from storage
     *
     * @param id id of product to be deleted
     * @return the sum of the names that are in the name of the
     * product deleted, or 0, if such an id did not exist.
     **/
    public long delete(long id) {
        Product product = findProduct(id);
        if (null == product)
            return 0;
        long sum = 0;
        for (long desc : product.name) {
            sum += desc;
            nameMap.get(desc).remove(id);
            if (nameMap.get(desc).isEmpty())
                nameMap.remove(desc);
        }
        productMap.remove(id);
        return sum;
    }

    /* 
       d. FindMinPrice(n): given a long int, find items whose description
       contains that number (exact match with one of the long ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    */

    /**
     * Given a long int, find products whose name
     * contains that number, and return lowest price of those products.
     *
     * @param n the long int to search for in the product names
     * @return lowest price of the product. Return 0 if there is no such product.
     **/
    public Money findMinPrice(long n) {
        if (!nameMap.containsKey(n))
            return new Money();
        HashSet<Long> prodIds = nameMap.get(n);
        Money minPrice = convertToMoney(Long.MAX_VALUE);
        for (long id : prodIds) {
            Product tempProduct = findProduct(id);
            if (tempProduct.price.compareTo(minPrice) < 0) {
                minPrice = tempProduct.price;
            }
        }
        return minPrice;
    }

    /* 
       e. FindMaxPrice(n): given a long int, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */

    /**
     * Given a long int, find products whose name
     * contains that number, and return highest price of those products.
     *
     * @param n the long int to search for in the product name
     * @return highest price of the products. Return 0 if there is no such product.
     **/
    public Money findMaxPrice(long n) {
        if (!nameMap.containsKey(n))
            return new Money();
        HashSet<Long> prodIds = nameMap.get(n);
        Money maxPrice = new Money();
        for (long id : prodIds) {
            Product tempProduct = findProduct(id);
            if (tempProduct.price.compareTo(maxPrice) > 0) {
                maxPrice = tempProduct.price;
            }
        }
        return maxPrice;
    }

    /* 
       f. FindPriceRange(n,low,high): given a long int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */

    /**
     * Given a long int, find the number of products whose name contains n,
     * and their prices fall within a given range
     *
     * @param n    the long int to search for in the product names
     * @param low  the low end of the price range, inclusive
     * @param high the high end of the price range, inclusive
     * @return the number of products in this price range
     **/
    public int findPriceRange(long n, Money low, Money high) {
        if (!nameMap.containsKey(n))
            return 0;
        if (low.compareTo(high) > 0)
            return 0;

        int count = 0;
        HashSet<Long> prodIds = nameMap.get(n);
        for (long id : prodIds) {
            Product tempProduct = findProduct(id);
            if (tempProduct.price.compareTo(low) >= 0 && tempProduct.price.compareTo(high) <= 0)
                count += 1;
        }
        return count;
    }

    /* 
       g. PriceHike(l,h,r): increase the price of every product, whose id is
       in the range [l,h] by r%.  Discard any fractional pennies in the new
       prices of items.  Returns the sum of the net increases of the prices.
    */

    /**
     * Increase the price of every product, whose id is in a range by specific percentage.
     * Discard any fractional pennies in the new prices of products.
     *
     * @param l    the low end of the ID range, inclusive
     * @param h    the high end of the ID range, inclusive
     * @param rate the percentage rate to increase prices
     * @return the sum of the net increases of the prices
     **/
    public Money priceHike(long l, long h, double rate) {
        if (productMap.firstKey().compareTo(h) > 0 || l > h || productMap.lastKey().compareTo(l) < 0)
            return new Money();
        long hike = 0;
        for (long id : productMap.keySet()) {
            if (id < l)
                continue;
            if (id > h)
                break;
            Product product = findProduct(id);
            long oldPrice = convertToLong(product.price);
            long rateInc = (long) (oldPrice * rate / 100);
            Money newPrice = convertToMoney(oldPrice + rateInc);
            hike += rateInc;
            insert(id, newPrice, null);
        }
        return convertToMoney(hike);
    }

    /**
     * Converts Money to Long
     *
     * @param money the Money to be converted
     * @return the Long value, in cents
     **/
    private Long convertToLong(Money money) {
        return money.dollars() * 100 + money.cents();
    }

    /**
     * Converts Long to Money
     *
     * @param money the Long value, in cents, that is to be converted
     * @return the Money
     **/
    private Money convertToMoney(long money) {
        int cents = (int) (money % 100);
        long dollars = money / 100;
        return new Money(dollars, cents);
    }

    /*
      h. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    */

    /**
     * Remove elements of list from the name of id.
     * It is possible that some of the products in the list are not in the id's name.
     *
     * @param id   id of product
     * @param list list of names to be removed
     * @return the sum of the numbers that are actually deleted from the name of id.
     * Return 0 if there is no such id.
     **/
    public long removeNames(long id, List<Long> list) {
        if (null == list || list.isEmpty())
            return 0;

        long sum = 0;
        Product product = findProduct(id);
        for (long desc : list) {
            if (product.name.contains(desc)) {
                sum += desc;
                nameMap.get(desc).remove(id);
                if (nameMap.get(desc).isEmpty())
                    nameMap.remove(desc);
            }
            product.name.remove(desc);
        }
        return sum;
    }

    static class Product {
        private long id;
        private Money price;
        private List<Long> name;

        public Product(long id, Money price, List<Long> name) {
            this.id = id;
            this.price = price;
            this.name = new ArrayList<>();
            this.name.addAll(name);
        }
    }

    // Do not modify the Money class in a way that breaks LP3Driver.java
    public static class Money implements Comparable<Money> {
        long d;
        int c;

        public Money() {
            d = 0;
            c = 0;
        }

        public Money(long d, int c) {
            this.d = d;
            this.c = c;
        }

        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if (len < 1) {
                d = 0;
                c = 0;
            } else if (part.length == 1) {
                d = Long.parseLong(s);
                c = 0;
            } else {
                d = Long.parseLong(part[0]);
                c = Integer.parseInt(part[1]);
            }
        }

        public long dollars() {
            return d;
        }

        public int cents() {
            return c;
        }

        public int compareTo(Money other) {
            if (this.dollars() < other.dollars())
                return -1;
            if (this.dollars() > other.dollars())
                return 1;
            return Integer.compare(this.cents(), other.cents());
        }

        public String toString() {
            return d + "." + c;
        }
    }

}
