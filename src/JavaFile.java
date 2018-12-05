import java.io.*;
import java.util.ArrayList;

public class JavaFile {

    public ArrayList<String> javaFile = new ArrayList<>();
    private ArrayList<String> ERROR_LOG = new ArrayList<>();

    String separator = java.io.File.separator;
    String fileName;

    public JavaFile(String filepath, String fileName) {
        this.fileName = fileName;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            String st;
            while ((st = br.readLine()) != null) {
                javaFile.add(st + " "); //to account for loss of info during the split by space
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateJavaFile(ArrayList<String> args) {
        try {
            File correctedArgs = new File("src" + separator + "TextSources" + separator + fileName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(correctedArgs.getAbsolutePath()));

            for (String line : args) {
                bw.write(line + "\n");
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> fixClassSyntax() {
        ArrayList<String> fixedCode = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();

        int changeInBraces, changeInSemicolons, changeInParentheses, changeInSquareBrackets, changeInLines = 0;
        for (int i = 0; i < javaFile.size(); i++) {
            changeInBraces = getCountOfChar(javaFile.get(i), "{");
            changeInSemicolons = getCountOfChar(javaFile.get(i), ";");
            changeInSquareBrackets = getCountOfChar(javaFile.get(i), "]");
            changeInParentheses = getCountOfChar(javaFile.get(i), ")");
            changeInLines = javaFile.size();

            javaFile.set(i, updateSquareBrackets(javaFile.get(i)));
            javaFile.set(i, updateClosedParenthesis(javaFile.get(i)));

            int indexOfSpace = 0;

            for (int j = 0; j < javaFile.get(i).length(); j++) { //split each among the spaces
                if (javaFile.get(i).substring(j, j + 1).equals(" ")) {
                    words.add(javaFile.get(i).substring(indexOfSpace, j));
                    indexOfSpace = j + 1;
                }
            }
            words.add(javaFile.get(i).substring(indexOfSpace));

            //words has the individual words from each line of code
            if (words.get(0).equals("public") || words.get(0).equals("private") || words.get(0).equals("protected") ||
                    words.get(0).equals("for") || words.get(0).equals("while") || words.get(0).equals("if")) {
                if (!words.get(words.size() - 1).equals("{")) {
                    words.add("{");
                }
            } else if (words.get(words.size() - 1).charAt(words.get(words.size() - 1).length() - 1) != ';') {
                words.add(";");
            }

            changeInBraces = getCountOfChar(concatenateList(words), "{") - changeInBraces;
            changeInSemicolons = getCountOfChar(concatenateList(words), ";") - changeInSemicolons;
            if (words.contains("}") && words.contains(";")) {
                changeInSemicolons = 0;
            }

            changeInSquareBrackets = getCountOfChar(concatenateList(words), "]") - changeInSquareBrackets;
            changeInParentheses = getCountOfChar(concatenateList(words), ")") - changeInParentheses;

            addToErrorLog(changeInBraces, "{", i);
            addToErrorLog(changeInSemicolons, ";", i);
            addToErrorLog(changeInSquareBrackets, "]", i);
            addToErrorLog(changeInParentheses, ")", i);

            fixedCode.add(concatenateList(words));
            words.clear();
        }

        addClosedBraces(fixedCode);
        int newNumLines = fixedCode.size();
        changeInLines = fixedCode.size() - changeInLines;
        addToErrorLog(changeInLines, "\n", newNumLines + 1);

        return fixedCode;
    }

    public void addToErrorLog(int change, String character, int lineNum) {
        if (character.equals("\n")) {
            for (int i = lineNum - change; i < lineNum; i++) {
                ERROR_LOG.add("missing } at line: " + i);
            }
        } else {
            for (int j = 0; j < change; j++) {
                ERROR_LOG.add("missing \"" + character + "\" at line: " + (lineNum + 1));
            }
            for (int j = 0; j > change; j--) {
                ERROR_LOG.add("extra \"" + character + "\" at line: " + (lineNum + 1));
            }
        }
    }

    private void addClosedBraces(ArrayList<String> fixedCode) {
        int closedBracesNeeded = 0;
        for (String line : fixedCode) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '{') {
                    closedBracesNeeded++;
                } else if (line.charAt(i) == '}') {
                    closedBracesNeeded--;
                }
            }
        }
        for (int i = 0; i < closedBracesNeeded; i++) {
            fixedCode.add("}");
        }
    }

    private boolean hasOnlySpaces(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    private int getCountOfChar(String line, String character) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.substring(i, i + 1).equals(character)) {
                count++;
            }
        }
        return count;
    }

    private String updateClosedParenthesis(String line) {
        String lineCopy = line;
        line = removeCharacter(line, "{");
        if (line.length() > 3) {
            if (!line.substring(0, 3).equals("for")) {
                line = removeCharacter(line, ";");
            }
        } else {
            line = removeCharacter(line, ";");
        }

        line = line.trim();

        if (hasOnlySpaces(line)) {
            return lineCopy;
        }

        int openParenthesisCount = 0;
        int closedParenthesisCount = 0;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '(') {
                openParenthesisCount++;
            }
        }
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ')') {
                closedParenthesisCount++;
            }
        }

        if (openParenthesisCount > closedParenthesisCount) {
            for (int i = 0; i < openParenthesisCount - closedParenthesisCount; i++) {
                line += ')';
            }

        } else {
            for (int i = 0; i < closedParenthesisCount - openParenthesisCount; i++) {
                line = removeOneInstanceOfCharacterFromEnd(line, ')');
            }
        }
        return line;
    }

    private String updateSquareBrackets(String line) {
        String lineCopy = line;
        line = removeCharacter(line, "{");
        if (line.length() > 3) {
            if (!line.substring(0, 3).equals("for")) {
                line = removeCharacter(line, ";");
            }
        } else {
            line = removeCharacter(line, ";");
        }

        line = line.trim();

        if (hasOnlySpaces(line)) {
            return lineCopy;
        }

        int openParenthesisCount = 0;
        int closedParenthesisCount = 0;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '[') {
                openParenthesisCount++;
            }
        }
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ']') {
                closedParenthesisCount++;
            }
        }

        if (openParenthesisCount > closedParenthesisCount) {
            for (int i = 0; i < openParenthesisCount - closedParenthesisCount; i++) {
                line += ']';
            }

        } else {
            for (int i = 0; i < closedParenthesisCount - openParenthesisCount; i++) {
                line = removeOneInstanceOfCharacterFromEnd(line, ']');
            }
        }
        return line;
    }

    private String removeOneInstanceOfCharacterFromEnd(String line, Character character) {
        String output = "";
        int count = 0;
        for (int i = line.length() - 1; i >= 0; i--) {
            if (line.charAt(i) == character && count < 1) {
                count++;
            } else {
                output += line.charAt(i);
            }
        }
        output = reverse(output);
        return output;
    }

    private String reverse(String str) {
        String newString = "";
        for (int i = str.length() - 1; i >= 0; i--) {
            newString += str.substring(i, i + 1);
        }
        return newString;
    }

    private String removeCharacter(String line, String character) {
        String output = "";
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != character.charAt(0)) {
                output += line.charAt(i);
            }
        }
        return output;
    }

    private String concatenateList(ArrayList<String> list) {
        String output = "";
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            output += str;
            if (i < list.size() - 1) {
                output += " ";
            }
        }
        return output;
    }

    public ArrayList<String> getERROR_LOG() {
        return this.ERROR_LOG;
    }
}


