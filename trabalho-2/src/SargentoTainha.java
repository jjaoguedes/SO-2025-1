import java.util.concurrent.ThreadLocalRandom;

public class SargentoTainha implements Runnable {
    private final Barbearia barbearia;
    private final int tempoCochilo;
    // Conta quantas vezes ele tentou e não conseguiu adicionar cliente
    private int tentativasFalhas;
    // Se atingir esse valor, ele vai embora
    private final int MAX_TENTATIVAS_FALHAS = 3;
    // Número máximo de clientes que ele tentará adicionar.
    private final int MAX_CLIENTES = 100;

    public SargentoTainha(Barbearia barbearia, int tempoCochilo) {
        this.barbearia = barbearia;
        this.tempoCochilo = tempoCochilo;
    }

    @Override
    public void run() {
        for (int i = 0; i < MAX_CLIENTES; i++) {
            try {
                Thread.sleep(tempoCochilo * 1000L);
                // Sorteia a categoria do cliente
                int categoria = ThreadLocalRandom.current().nextInt(0, 4);
                // Simula uma pausa e não adiciona cliente
                if (categoria == 0) {
                    barbearia.pausa(1);
                    System.out.println("Sargento Tainha tirando um cochilo...");
                    continue;
                }

                // Gera tempo de atendimento conforme categoria
                int tempoServico = switch (categoria) {
                    case 1 -> ThreadLocalRandom.current().nextInt(4, 7);
                    case 2 -> ThreadLocalRandom.current().nextInt(2, 5);
                    case 3 -> ThreadLocalRandom.current().nextInt(1, 4);
                    default -> 0;
                };

                // Cria cliente e tenta adicionar
                Cliente cliente = new Cliente(categoria, tempoServico);
                if (!barbearia.adicionarCliente(cliente)) {
                    System.out.println("Barbearia cheia! Sargento Tainha não conseguiu adicionar: " + cliente);
                    tentativasFalhas++;
                    if (tentativasFalhas >= MAX_TENTATIVAS_FALHAS) {
                        System.out.println("Sargento Tainha atingiu o limite de tentativas. Indo para casa.");
                        return;
                    }
                } else {
                    System.out.println("Sargento Tainha adicionou: " + cliente);
                    tentativasFalhas = 0;
                }
            } catch (InterruptedException e) {
                // Se for interrompido por outra thread, encerra sua execução
                Thread.currentThread().interrupt();
                System.out.println("Sargento Tainha foi interrompido.");
                return;
            }
        }
        System.out.println("Sargento Tainha adicionou todos os clientes. Indo para casa.");
    }
}
