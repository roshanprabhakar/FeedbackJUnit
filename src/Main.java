public class Main {
    public static void main(String[] args) {
        runCollectionsAnalysis();
    }

    public static void runCollectionsAnalysis() {
        try {
            Collections collections = new Collections();
            collections.analyzeCollections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
