import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DLeftHashTable {
    private int buckets;
    private Map<Integer, List<Entry>> leftTable;
    private Map<Integer, List<Entry>> rightTable;

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
        leftTable = new HashMap<>();
        rightTable = new HashMap<>();
    }

    public void insert(String key, int value) {

        int leftHash = hash(key, leftTable.size());
        int rightHash = hash(key, rightTable.size());

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

        int leftHash = hash(key, leftTable.size());
        int rightHash = hash(key, rightTable.size());

        List<Entry> leftBucket = leftTable.get(leftHash);
        if (leftBucket != null) {
            for (Entry entry : leftBucket) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }

        List<Entry> rightBucket = rightTable.get(rightHash);
        if (rightBucket != null) {
            for (Entry entry : rightBucket) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }

        return null;
    }

    private int hash(String key, int size) {

        int hash = 0;
        for (char c : key.toCharArray()) {
            hash += (int) c;
        }
        return hash % size;
    }
}