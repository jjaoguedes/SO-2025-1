public class Barbeiro implements Runnable {
    private final Barbearia barbearia;
    private final String nome;

    public Barbeiro(Barbearia barbearia, String nome) {
        this.barbearia = barbearia;
        this.nome = nome;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Cliente cliente = barbearia.atenderProximoCliente();
                System.out.println(nome + " está atendendo " + getNomeCategoria(cliente.categoria) +
                        " por " + cliente.tempoServico + " segundos.");
                Thread.sleep(cliente.tempoServico * 1000L);
                barbearia.clienteAtendido(cliente);
            } catch (InterruptedException e) {
                System.out.println(nome + " foi interrompido.");
                // Thread.currentThread().interrupt();
                return; // Sai do loop quando interrompido
            }
        }
    }

    private String getNomeCategoria(int categoria) {
        return switch (categoria) {
            case 1 -> "Oficial";
            case 2 -> "Sargento";
            case 3 -> "Cabo";
            default -> "Desconhecido";
        };
    }
}