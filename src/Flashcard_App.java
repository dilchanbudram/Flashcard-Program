import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Random;

public class Flashcard_App {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        //File fileOfSets = new File("Data/sets.txt");
        ArrayList<FlashcardSet> sets = loadSets();
        boolean running = true;
        while (running) {
            clearConsole();
            System.out.println();
            System.out.println("1. Add Set");
            System.out.println("2. Choose Set");
            System.out.println("3. Delete Set");
            System.out.println("4. Exit\n");

            System.out.print("Choice: ");
            String choice = scan.nextLine();
            System.out.println();
            if (choice.equals("1"))
                addSet(sets, scan);
            else if (choice.equals("2"))
                chooseSet(sets, scan);
            else if (choice.equals("3")) {
                deleteSets(sets, scan);
            }
            else if (choice.equals("4")) {
                running = false;
                scan.close();
            }
            else {}
                //System.out.println("INVALID ENTRY.\n");
        }
        //you could call saveCards at the very end to only do it once but
        //saving after every addition/deletion allows progress to save if the program has an error
    }

    public static void clearConsole() {
        for (int i = 0; i < 30; i++)
            System.out.println();
    }

    public static File getDataDirectory() {
        File dataDir = new File(
                System.getProperty("user.home"),
                "Flashcard_App/Data"
        );

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        return dataDir;
    }

    public static ArrayList <FlashcardSet> loadSets() {
        ArrayList <FlashcardSet> sets = new ArrayList<FlashcardSet>();
        try {
            File file = new File(getDataDirectory(), "sets.txt");
            Scanner input = new Scanner(file);
            while (input.hasNextLine())
                sets.add(new FlashcardSet(input.nextLine()));
            input.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Unable to load set(s).\n");
        }
        return sets;
    }

    public static void addSet(ArrayList <FlashcardSet> sets, Scanner scan) {
        while (true) { //I want to continuously add flashcard sets until "<" is entered
            System.out.print("Name of the set: ");
            String setName = scan.nextLine();
            System.out.println();

            if (setName.equals("<"))
                return;
            else if (setName.length() == 0) {
                System.out.println("NOTHING ENTERED.\n");
                continue;
            }

            boolean duplicate = false;
            for (FlashcardSet fs : sets)
                if (fs.name.equals(setName)) {
                    duplicate = true;
                    break;
                }
            if (duplicate) {
                System.out.println("ALREADY EXISTS.\n");
                continue;
            }

            sets.add(new FlashcardSet(setName));
            //for-each for debugging
            for (FlashcardSet fs: sets)
                System.out.println(fs.name);
            System.out.println();
            saveSets(sets);
        }
    }

    public static void saveSets(ArrayList <FlashcardSet> sets) {
        try {
            File file = new File(getDataDirectory(), "sets.txt");
            PrintWriter output = new PrintWriter(file);
            for (FlashcardSet fs : sets)
                output.println(fs.name);
            output.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Unable to save set(s).\n");
        }
    }

    public static void chooseSet(ArrayList<FlashcardSet> sets, Scanner scan) {
        while (true) { //since the choose set menu should be continuously running until it gets the desired inputs
            for (int i = 0; i < sets.size(); i++) {
                System.out.println((i + 1) + ". " + sets.get(i).name);
            }
            System.out.println();
            try {
                System.out.print("Enter the set number: ");
                String setNum = scan.nextLine();
                System.out.println();
                //the < entered condition must go first because if the program attempts to run the following else if when < is entered, it will cause an error
                if (setNum.equals("<"))
                    return;
                else if (Integer.parseInt(setNum) < 1 || Integer.parseInt(setNum) > sets.size()) {
                    //System.out.println("INVALID ENTRY.\n");
                    continue;
                }
                else //(Integer.parseInt(setNum) >= 1 && Integer.parseInt(setNum) <= sets.size())
                    workingSet(sets.get(Integer.parseInt(setNum) - 1), scan);
            }
            catch (NumberFormatException e) {
                continue;
            }
        }
    }

    public static void workingSet(FlashcardSet fs, Scanner scan) {
        loadCards(fs);
        while (true) {
            System.out.println("1. New card");
            System.out.println("2. Learn");
            System.out.println("3. Test");
            System.out.println("4. Delete card\n");

            System.out.print("Enter the number of your choice (or < to return): ");
            String choice = scan.nextLine();
            if (choice.equals("<"))
                return;
            else if (Integer.parseInt(choice) < 1 || Integer.parseInt(choice) > 4) {
                continue;
            }
            else {
                if (choice.equals("1"))
                    newCards(fs, scan);
                if (choice.equals("2"))
                    learn(fs, scan);
                if (choice.equals("3"))
                    test(fs, scan);
                if (choice.equals("4"))
                    deleteCards(fs, scan);
            }
            //break? probably not
        }
    }

    public static void loadCards(FlashcardSet fs) {
        try {
            File file = new File(getDataDirectory(), fs.name + "_cards.txt");
            Scanner fileInput = new Scanner(file);
            while (fileInput.hasNextLine()) {
                String line = fileInput.nextLine();
                int separator = line.indexOf("|");
                if (separator != -1) {
                    String word = line.substring(0, separator);
                    String definition = line.substring(separator + 1);

                    Flashcard card = new Flashcard(word, definition);
                    fs.cards.add(card);
                }
            }
            fileInput.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Unable to load card(s).\n");
        }
    }

    public static void saveCards(FlashcardSet fs) {
        try {
            File file = new File(getDataDirectory(), fs.name + "_cards.txt");
            PrintWriter output = new PrintWriter(file);
            for (Flashcard card : fs.cards)
                output.println(card.getWord() + "|" + card.getDefinition());
            output.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Unable to save card(s).\n");
        }
    }

    public static void newCards(FlashcardSet fs, Scanner scan) {
        while (true) {
            System.out.print("\nEnter a word (or < to return to menu): ");
            String word = scan.nextLine();
            if (word.equals("<"))
                return;
            if (word.length() == 0) {
                System.out.println("\nNOTHING ENTERED.");
                continue;
            }
            if (word.contains("|")) {
                System.out.println("\nINVALID CHARACTER.");
                continue;
            }

            boolean duplicate = false;
            for (Flashcard card : fs.cards)
                if (card.getWord().equalsIgnoreCase(word)) {
                    duplicate = true;
                    break;
                }
            if (duplicate) {
                System.out.println("\nALREADY EXISTS.");
                continue;
            }

            System.out.print("Enter the definition (or < to return to menu): ");
            String definition = scan.nextLine();
            if (definition.equals("<"))
                return;
            if (definition.length() == 0) {
                System.out.println("\nNOTHING ENTERED.");
                continue;
            }
            if (definition.contains("|")) {
                System.out.println("\nINVALID CHARACTER.");
                continue;
            }

            Flashcard card = new Flashcard(word, definition);
            fs.cards.add(card);
            //saveCards(fs) will repeatedly overwrite everything in the respective file
            saveCards(fs);
        }
    }

    public static void learn(FlashcardSet fs, Scanner scan) {
        if (fs.cards.size() == 0) {
            System.out.println("\nNO FLASHCARDS IN THE SET.");
            return;
        }

        ArrayList<Integer> seen = new ArrayList<Integer>();
        Random random = new Random();

        while (true) {
            if (seen.size() == fs.cards.size())
                seen.clear();
            int cardIndex;
            do
                cardIndex = random.nextInt(fs.cards.size());
            while (seen.contains(cardIndex));
            Flashcard card = fs.cards.get(cardIndex);

            System.out.println();

            System.out.println("Definition: " + card.getDefinition());
            System.out.print("Word (or > to skip, < to return to menu): ");
            String answer = scan.nextLine();
            if (answer.equals("<"))
                return;
            seen.add(cardIndex);
            if (answer.equals(">"))
                System.out.println("\nSKIPPED.");
            else if (answer.equalsIgnoreCase(card.getWord()))
                System.out.println("\nCORRECT !");
            else
                System.out.println("\nINCORRECT.\nThe correct word was: "
                        + card.getWord());
        }
    }

    public static void test(FlashcardSet fs, Scanner scan) {
        if (fs.cards.size() == 0) {
            System.out.println("\nNO FLASHCARDS IN THE SET.");
            System.out.println();
            return;
        }

        int numberOfQuestions;

        while (true) {
            System.out.print("How many words would you like to test?");
            String amount = scan.nextLine();
            if (amount.equals("<"))
                return;
            //maybe I have a similar try-catch statement for some other input...
            try {
                numberOfQuestions = Integer.parseInt(amount);
                if (numberOfQuestions <= 0)
                    System.out.println("Please enter a number greater than 0:");
                else if (numberOfQuestions > fs.cards.size())
                    System.out.println("Not enough words in the set.");
                else
                    break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number:");
            }
        }

        ArrayList<Integer> tested = new ArrayList<>();
        Random random = new Random();
        int correct = 0;
        for (int question = 0; question < numberOfQuestions; question++) {
            int cardIndex;
            do
                cardIndex = random.nextInt(fs.cards.size());
            while (tested.contains(cardIndex));
            tested.add(cardIndex);
            Flashcard card = fs.cards.get(cardIndex);

            System.out.println();

            System.out.println("Question " + (question + 1) + " of " + numberOfQuestions);
            System.out.println("Definition: " + card.getDefinition());
            System.out.print("Word (or < to quit): ");
            String answer = scan.nextLine();
            if (answer.equals("<")) {
                System.out.println("Score: " + correct + "/" + numberOfQuestions);
                System.out.printf("\nScore: %.2f", ((double)correct/numberOfQuestions) * 100);
                System.out.print("%\n");
                System.out.println();
                return;
            }
            else if (answer.equalsIgnoreCase(card.getWord())) {
                System.out.println("\nCORRECT !");
                correct++;
            }
            else
                System.out.println("\nINCORRECT.\nThe correct word was: " + card.getWord());
        }

        double percentage = (double) correct / numberOfQuestions * 100;

        System.out.println();
        System.out.println("Test complete!");
        System.out.println("Score: " + correct + "/" + numberOfQuestions);
        System.out.printf("\nScore: %.2f", percentage);
        System.out.print("%\n");
        System.out.println();
    }

    public static void deleteCards(FlashcardSet fs, Scanner scan) {
        while (true) {
            for (int i = 0; i < fs.cards.size(); i++)
                System.out.println((i + 1) + ". " + fs.cards.get(i).getWord());
            System.out.println();
            System.out.print("Delete which word: ");
            String word = scan.nextLine();
            if (word.equals("<"))
                return;
            else if (word.length() == 0) {
                System.out.println("\nNOTHING ENTERED.");
                continue;
            }
            else {
                for (int i = 0; i < fs.cards.size(); i++)
                    if (fs.cards.get(i).getWord().equals(word))
                        fs.cards.remove(i);
                saveCards(fs);
            }
        }
    }

    public static void deleteSets(ArrayList<FlashcardSet> sets, Scanner scan) {
        while (true) {
            try {
                for (int i = 0; i < sets.size(); i++)
                    System.out.println((i + 1) + ". " + sets.get(i).name);
                System.out.print("\nEnter the number of the set you want deleted: ");
                String deleteNum = scan.nextLine();
                if (deleteNum.equals("<"))
                    return;
                else if (Integer.parseInt(deleteNum) < 1 || Integer.parseInt(deleteNum) > sets.size()) {
                    System.out.println("\nINVALID ENTRY.");
                    System.out.println();
                    continue;
                }
                else {
                    sets.remove(Integer.parseInt(deleteNum) - 1);
                    saveSets(sets);
                }
            }
            catch (NumberFormatException e) {
                System.out.println("\nINVALID ENTRY.\n");
            }
        }
    }

}

    class Flashcard { //package-private; top-level classes cannot be private; put it in a different .java file to make it public
        private String word;
        private String definition;

        public Flashcard(String word, String definition) {
            this.word = word;
            this.definition = definition;
        }

        public String getWord() {
            return word;
        }

        public String getDefinition() {
            return definition;
        }
    }

    class FlashcardSet {
        String name;
        ArrayList <Flashcard> cards;

        FlashcardSet (String name) {
            this.name = name;
            cards = new ArrayList <Flashcard>();
        }

    }