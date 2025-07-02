// Classe que representa um barbeiro, implementando Runnable para ser executado em uma thread
public class Barbeiro implements Runnable {
    
    private final Barbearia barbearia; // Referência à barbearia onde o barbeiro irá atuar
    private final String nome;

    // Construtor: recebe a barbearia onde o barbeiro trabalha e seu nome
    public Barbeiro(Barbearia barbearia, String nome) {
        this.barbearia = barbearia;
        this.nome = nome;
    }

    // Método que define o comportamento da thread do barbeiro
    @Override
    public void run() {
        // Loop contínuo enquanto a thread não for interrompida
        while (!Thread.interrupted()) {
            try {
                // Aguarda e obtém o próximo cliente a ser atendid
                Cliente cliente = barbearia.atenderProximoCliente();

                // Exibe no console qual cliente está sendo atendido e por quanto tempo
                System.out.println(nome + " está atendendo " + getNomeCategoria(cliente.categoria) +
                        " por " + cliente.tempoServico + " segundos.");

                // Simula o tempo de atendimento com sleep (multiplica por 1000 para converter segundos para milissegundos)
                Thread.sleep(cliente.tempoServico * 1000L);

                 // Registra o atendimento na barbearia (libera cadeira, atualiza contadores etc.)
                barbearia.clienteAtendido(cliente);
            } catch (InterruptedException e) {
                // Caso a thread seja interrompida, sai do loop de forma limpa
                System.out.println(nome + " foi interrompido.");
                // Thread.currentThread().interrupt();
                return; // Encerra a execução da thread
            }
        }
    }

    // Método auxiliar para traduzir o número da categoria para texto
    private String getNomeCategoria(int categoria) {
        return switch (categoria) {
            case 1 -> "Oficial";
            case 2 -> "Sargento";
            case 3 -> "Cabo";
            default -> "Desconhecido";
        };
    }
}
