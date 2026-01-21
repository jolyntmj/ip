import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Duke {
    public static void main(String[] args) {
        new Duke().run();
    }

    public void run() {
        printLine();
        printName();
        printGreeting();
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
}
