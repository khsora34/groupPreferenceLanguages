import java.util.Scanner;

public class StandardInput {

    private static Scanner keyboard;

    public StandardInput() {}

    public static void enter() {
        keyboard = new Scanner(System.in);
        System.out.print(">>\n");
        keyboard.nextLine();
    }

    public int readNumber() {
        keyboard = new Scanner(System.in);
        String entrada;
        boolean already = false;
        do {
            if (already) {
                System.out.print("\nTRY AGAIN:");
            } else {
                System.out.print("\nENTER A NUMBER:");
                already = true;
            }
            entrada = keyboard.next();
        } while (!entrada.matches("-?\\d+") || Integer.valueOf(entrada) == 0);
        return Integer.valueOf(entrada);
    }

    public char readChar() {
        keyboard = new Scanner(System.in);
        String entrada;
        boolean already = false;
        do {
            if (already) {
                System.out.print("\nTRY AGAIN:");
            } else {
                System.out.print("\nENTER A CHARACTER:");
                already = true;
            }
            entrada = keyboard.next();
        } while (entrada.length() > 1);
        return entrada.charAt(0);
    }

    public String readString() {
        System.out.print("ENTER ANY TEXT:");
        keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }

    public String nextLine() {
        keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }

}
