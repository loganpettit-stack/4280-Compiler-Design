import java.util.ArrayList;

/*Filters comments out of code and code into
* cleaned character array list*/
public class Filter {

    private ArrayList<Character> characterArrayList = new ArrayList<>();

    public ArrayList<Character> getCharacterArrayList() {
        return characterArrayList;
    }

    /* Remove comments from line */
    private String removeComments(String data) {
        String filteredString = "";

        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) != '#') {
                filteredString = filteredString + data.charAt(i);
            } else if (data.charAt(i) == '#') {
                break;
            }
        }

        return filteredString;
    }

    public void putFileContentsInList(String line){

        if(line == null){
            /* If last line in file add EOF token */
            char e = (char) 0x1b;
            characterArrayList.add(e);
        }
        /*Otherwise add characters to array list*/
        else {
            line = removeComments(line);

            for (int i = 0; i < line.length(); i++) {
                characterArrayList.add(line.charAt(i));
            }

            characterArrayList.add('\n');
        }

    }
}



