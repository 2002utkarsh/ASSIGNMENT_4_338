
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DLeftHashTable {
    private int buckets; // Number of buckets per table
    private Map<Integer, List<Entry>> leftTable;
    private Map<Integer, List<Entry>> rightTable;

    // Entry class to store <key, value> pairs
    private static class Entry {
        String key;
        int value;

        public Entry(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    
    public DLeftHashTable(int buckets) {
        this.buckets = buckets;
        leftTable = new HashMap<>(buckets);
        rightTable = new HashMap<>(buckets);
    }

   
    public void insert(String key, int value) {

        int leftHash = hashLeft(key);
        int rightHash = hashRight(key);

       
        List<Entry> leftBucket = leftTable.computeIfAbsent(leftHash, k -> new ArrayList<>());
        List<Entry> rightBucket = rightTable.computeIfAbsent(rightHash, k -> new ArrayList<>());

        Entry entry = new Entry(key, value);

        
        if (leftBucket.size() <= rightBucket.size()) {
            leftBucket.add(entry);
        } else {
            rightBucket.add(entry);
        }
    }

    public Integer lookup(String key) {
      
        int leftHash = hashLeft(key);
        int rightHash = hashRight(key);

        List<Entry> leftBucket = leftTable.get(leftHash);
        List<Entry> rightBucket = rightTable.get(rightHash);

        if (leftBucket == null && rightBucket == null) {

            return null;
        }

        if (leftBucket != null) {
            for (Entry entry : leftBucket) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }

        if (rightBucket != null) {
            for (Entry entry : rightBucket) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }
        return null;
    }


    private int hashLeft(String key) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash += (int) c;
        }
        return hash % buckets;
    }

    private int hashRight(String key) {
        int hash = 5381;
        for (int i = 0; i < key.length(); i++) {
            hash = ((hash << 5) + hash) + key.charAt(i);
        }
        return Math.abs(hash % buckets);
    }
}