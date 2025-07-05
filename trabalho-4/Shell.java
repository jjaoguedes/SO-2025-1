import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Classe principal do shell
 * Responsável por iniciar o loop de comandos e encerrar a sessão.
 */
public class Shell {
    // Nome do arquivo de log com timestamp da sessão
    private static final String LOG_FILE = "shell-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".log";

    public static void main(String[] args) {
        ShellExecutor executor = new ShellExecutor(LOG_FILE);
        Scanner scanner = new Scanner(System.in);

        // Loop principal de leitura de comandos do usuário
        while (true) {
            System.out.print("shell> ");
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;

            executor.logCommand(line); // registra no log

            if (line.trim().equals("exit")) {
                executor.logMessage("Saindo do shell.");
                break; // encerra loop
            }

            executor.executeCommand(line); // executa o comando
        }
        scanner.close();
    }
}

