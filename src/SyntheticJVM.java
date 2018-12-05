import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SyntheticJVM {

    private final String separator = File.separator;
    private final String FILE_NAME;
    private ArrayList<String> ERROR_LOG = new ArrayList<>();

    public SyntheticJVM(String fileName) {
        this.FILE_NAME = fileName;
    }

    public void run() {
        try {
            runProcess(("javac -cp src src" + separator + "TextSources" + separator + FILE_NAME + ".java"));
            runProcess(("java -cp src" + separator + "TextSources" + separator + " " + FILE_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printLines(InputStream ins) throws Exception {
        String line;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            if (line.contains("Exception")) {
                ERROR_LOG.add(line.substring(line.indexOf("java"), line.indexOf(":")));
            }
        }
    }

    private void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(pro.getInputStream());
        printLines(pro.getErrorStream());
        pro.waitFor();
    }

    public ArrayList<String> getERROR_LOG() {
        return this.ERROR_LOG;
    }

}