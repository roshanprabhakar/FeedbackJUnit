import java.io.File;
import java.util.ArrayList;

public class Collections {

    private final String separator = File.separator;
    private StatsObj universalERR_LOG = new StatsObj(5);

    private File TextSources = new File("src" + separator + "TextSources");
    private ArrayList<File> TextFiles = new ArrayList<>();

    public Collections() {
        File[] temp = TextSources.listFiles();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].getAbsolutePath().substring(temp[i].getAbsolutePath().indexOf(".")).equals(".txt")) {
                TextFiles.add(temp[i]);
            }
        }
    }

    public void analyzeCollections() {
        String name;
        for (int i = 0; i < TextFiles.size(); i++) {

            name = TextFiles.get(i).getName();

            System.out.println();
            System.out.println();
            System.out.println("►►►►►►►►►► File " + (i + 1) + ": " + name + " ◄◄◄◄◄◄◄◄◄◄");
            try {
                JavaFile jf = new JavaFile("src/TextSources/" + name.substring(0, name.indexOf(".")) + ".txt", name.substring(0, name.indexOf(".")) + ".txt");
                Compiler compiler = new Compiler(name.substring(0, name.indexOf(".")));

                System.out.println("► Compile-time Errors: ");
                compiler.compile();

                jf.updateJavaFile(jf.fixClassSyntax());
                for (int j = 0; j < jf.getERROR_LOG().size(); j++) {
                    universalERR_LOG.add(jf.getERROR_LOG().get(j).substring(0, jf.getERROR_LOG().get(j).indexOf(" at")));
                }
                System.out.println("\n► ERROR_LOG");
                System.out.println(jf.getERROR_LOG());

                System.out.println("\n► Fixing errors if applicable ...");
                compiler.compile();

                System.out.println("► Running synthetic JVM...");
                System.out.println("\n----- File Output -----");
                SyntheticJVM syntheticJVM = new SyntheticJVM(name.substring(0, name.indexOf(".")));
                syntheticJVM.run();
                for (int j = 0; j < syntheticJVM.getERROR_LOG().size(); j++) {
                    universalERR_LOG.add(syntheticJVM.getERROR_LOG().get(j));
                }
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("-------------------- CLASS SUMMARY --------------------");
        System.out.println("\n►►► Top 5 Most frequent errors: ");
        for (int i = 0; i < universalERR_LOG.getTopMostFreq().size(); i++) {
            System.out.println("\t" + (i + 1) + ")\t" + universalERR_LOG.getTopMostFreq().get(i));
        }
        System.out.print("\n►►► Average Number of Errors Per Entry: ");
        System.out.printf("%.2f", (double) (universalERR_LOG.size()) / TextFiles.size());
        System.out.println("\n\n-------------------------------------------------------");
    }
}
