import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Classe responsável por interpretar e executar os comandos do shell.
 * Também realiza o registro em log e trata execução em background, pipes e redirecionamentos.
 */
class ShellExecutor {
    private final String logFile;

    public ShellExecutor(String logFile) {
        this.logFile = logFile;
    }

    // Registra o comando digitado
    public void logCommand(String cmd) {
        writeToLog("[CMD] " + cmd);
    }

    // Registra mensagens gerais no log
    public void logMessage(String msg) {
        writeToLog("[LOG] " + msg);
    }

    /**
     * Executa a linha de comando recebida
     * Trata comandos internos, externos, pipes, redirecionamento e background
     */
    public void executeCommand(String line) {
        line = expandPath(line); // expande ~ e ./

        boolean background = false;
        if (line.endsWith("&")) {
            background = true;
            line = line.substring(0, line.length() - 1).trim();
        }

        // Redirecionamento ou pipes
        if (line.contains("|") || line.contains("<") || line.contains(">")) {
            try {
                handlePipesAndRedirection(line, background);
            } catch (IOException e) {
                System.err.println("Erro: " + e.getMessage());
            }
            return;
        }

        // Comandos internos ou externos
        List<String> tokens = new ArrayList<>(Arrays.asList(line.split(" ")));
        if (tokens.isEmpty()) return;
        String cmd = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        try {
            switch (cmd) {
                case "cd": changeDirectory(args); break;
                case "pwd": printWorkingDirectory(); break;
                case "echo": echo(args); break;
                case "cp": copyFile(args); break;
                case "mv": moveFile(args); break;
                case "rm": removeFile(args); break;
                case "mkdir": makeDirectory(args); break;
                case "rmdir": removeDirectory(args); break;
                case "ls": listFiles(); break;
                default: runExternalCommand(tokens, background); break;
            }
        } catch (Exception e) {
            logMessage("Erro ao executar comando: " + e.getMessage());
            System.err.println("Erro: " + e.getMessage());
        }
    }

    // Expande ~ para home e ./ para diretório atual
    private String expandPath(String line) {
        return line.replace("~", System.getProperty("user.home"))
                   .replace("./", System.getProperty("user.dir") + "/");
    }

    // Implementações dos comandos internos
    private void changeDirectory(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Uso: cd <diretorio>");
            return;
        }
        File dir = new File(args.get(0));
        if (dir.exists() && dir.isDirectory()) {
            System.setProperty("user.dir", dir.getAbsolutePath());
        } else {
            System.out.println("Diretório não encontrado.");
        }
    }

    private void printWorkingDirectory() {
        System.out.println(System.getProperty("user.dir"));
    }

    private void echo(List<String> args) {
        System.out.println(String.join(" ", args));
    }

    private void copyFile(List<String> args) throws IOException {
        if (args.size() < 2) {
            System.out.println("Uso: cp <origem> <destino>");
            return;
        }
        File origem = new File(args.get(0));
        File destino = new File(args.get(1));
        try (FileInputStream fis = new FileInputStream(origem);
             FileOutputStream fos = new FileOutputStream(destino)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    private void moveFile(List<String> args) throws IOException {
        copyFile(args);
        removeFile(Collections.singletonList(args.get(0)));
    }

    private void removeFile(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Uso: rm <arquivo>");
            return;
        }
        File f = new File(args.get(0));
        if (!f.delete()) {
            System.out.println("Falha ao remover arquivo.");
        }
    }

    private void makeDirectory(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Uso: mkdir <nome>");
            return;
        }
        File dir = new File(args.get(0));
        if (!dir.mkdir()) {
            System.out.println("Falha ao criar diretório.");
        }
    }

    private void removeDirectory(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Uso: rmdir <nome>");
            return;
        }
        File dir = new File(args.get(0));
        if (!dir.delete()) {
            System.out.println("Falha ao remover diretório (verifique se está vazio).");
        }
    }

    private void listFiles() {
        File dir = new File(System.getProperty("user.dir"));
        String[] files = dir.list();
        if (files != null) {
            for (String f : files) {
                System.out.println(f);
            }
        }
    }

   /**
     * Executa comandos externos, com suporte a execução em background
     */
    private void runExternalCommand(List<String> tokens, boolean background) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(tokens);
        builder.directory(new File(System.getProperty("user.dir")));

        if (background) {
            builder.inheritIO();
            builder.start();
            logMessage("Processo em background iniciado.");
        } else {
            Process proc = builder.start();
            printStream(proc.getInputStream(), System.out);
            printStream(proc.getErrorStream(), System.err);
            try {
                int status = proc.waitFor();
                logMessage("Processo finalizado com código: " + status);
            } catch (InterruptedException e) {
                System.err.println("Interrompido: " + e.getMessage());
            }
        }
    }

   /**
     * Manipula comandos com pipes e redirecionamentos
     */
    private void handlePipesAndRedirection(String line, boolean background) throws IOException {
        String[] pipeParts = line.split("\\|");
        List<ProcessBuilder> builders = new ArrayList<>();

        for (String part : pipeParts) {
            part = expandPath(part.trim());
            File outputFile = null;
            File inputFile = null;
            boolean append = false;

            if (part.contains(">>")) {
                String[] split = part.split(">>");
                part = split[0].trim();
                outputFile = new File(split[1].trim());
                append = true;
            } else if (part.contains(">")) {
                String[] split = part.split(">");
                part = split[0].trim();
                outputFile = new File(split[1].trim());
            } else if (part.contains("<")) {
                String[] split = part.split("<");
                part = split[0].trim();
                inputFile = new File(split[1].trim());
            }

            List<String> command = new ArrayList<>(Arrays.asList(part.split(" ")));
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(System.getProperty("user.dir")));

            if (outputFile != null) {
                builder.redirectOutput(append ? ProcessBuilder.Redirect.appendTo(outputFile)
                                              : ProcessBuilder.Redirect.to(outputFile));
            }
            if (inputFile != null) {
                builder.redirectInput(ProcessBuilder.Redirect.from(inputFile));
            }

            builders.add(builder);
        }

        Process last = null;
        Process prev = null;
        for (int i = 0; i < builders.size(); i++) {
            ProcessBuilder builder = builders.get(i);
            if (i == 0) {
                prev = builder.start();
            } else {
                Process current = builder.start();
                try (OutputStream out = current.getOutputStream(); InputStream in = prev.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
                prev = current;
            }
            last = prev;
        }

        if (last != null && !background) {
            printStream(last.getInputStream(), System.out);
        }
    }

    // copia o conteúdo de um InputStream para um PrintStream, caractere por caractere
    private void printStream(InputStream input, PrintStream out) {
        try {
            int c;
            while ((c = input.read()) != -1) {
                out.print((char) c);
            }
        } catch (IOException e) {
            out.println("Erro ao ler saída: " + e.getMessage());
        }
    }

    // registra uma mensagem no arquivo logFile, precedida pela data e hora atual
    private void writeToLog(String content) {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + content + "\n");
        } catch (IOException e) {
            System.out.println("Erro ao escrever no log: " + e.getMessage());
        }
    }
}
