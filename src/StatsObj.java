import java.util.*;

public class StatsObj {

    private HashMap<String, Integer> data;
    private int N;
    private int size;
    private ArrayList<String> topN;

    public StatsObj(int N) {
        this.N = N;
        data = new HashMap<String, Integer>();
        size = 0;
        topN = new ArrayList<>();
    }

    public void add(String item) {
        if (data.get(item) == null) {
            data.put(item, 1);
            if (topN.size() < N)
                topN.add(item);
        } else {
            data.put(item, data.get(item) + 1);
            if (!topN.contains(item))
                topN.add(item);
            sort(item);
        }
        size++;
    }

    public void sort(String item) {
        for (int i = topN.size() - 1; i > 0; i--) {
            if (data.get(topN.get(i)) > data.get(topN.get(i - 1)))
                swap(i, i - 1);
        }
        while (topN.size() > N)
            topN.remove(topN.size() - 1);
    }

    public void swap(int a, int b) {
        String temp = topN.get(a);
        topN.set(a, topN.get(b));
        topN.set(b, temp);
    }

    public void addAllLetters(String str) {
        for (int i = 0; i < str.length(); i++) {
            add(str.substring(i, i + 1));
        }
    }

    public int getCountOf(String testLetter) {
        if (data.containsKey(testLetter))
            return data.get(testLetter);
        return 0;
    }

    public int size() {
        return size;
    }

    public int getNumUnique() {
        return data.size();
    }

    public String getMostFreq() {
        if (topN.size() < 1)
            return "Obj is empty";
        return topN.get(0);
    }

    public ArrayList<String> getTopMostFreq() {
        return topN;
    }

    public String getRandom() {
        int p = (int) (Math.random() * size);
        int numSoFar = 0;
        for (String key : data.keySet()) {
            numSoFar += data.get(key);
            if (p < numSoFar)
                return key;
        }
        return topN.get(0);
    }
}



