import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Duke {
    private List<String> text = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Duke().run();
    }

    public void run() {
        printLine();
        printName();
        printGreeting();

        String input = scanner.nextLine();
        
        while(!input.equalsIgnoreCase("bye")) {
            handleInput(input);
            input = scanner.nextLine();
        } 
        printLine();
        printGoodbye();
        printLine();
    }

    private void printLine() {
        System.out.println("--------------------------------");
    }

    private void printName() {
        System.out.println("Hello! I am sealriously");
    }

    private void printGreeting() {
        System.out.println("What can I do for you?");
    }

    private void printGoodbye() {
        System.out.println("Bye. See you soon!");
    }

    public void handleInput(String input) {
        printLine();
        System.out.println("You said: " + input);
        printLine();

        if (!input.equalsIgnoreCase("list")) {
            text.add(input);
        } else {
            for(int i=0; i < text.size(); i++ ) {
                System.out.println((i+1) + ")" + text.get(i));
            }
        }
    } 
}
