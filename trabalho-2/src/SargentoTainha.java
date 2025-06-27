import java.util.concurrent.ThreadLocalRandom;

public class SargentoTainha implements Runnable {
    private final Barbearia barbearia;
    private final int tempoCochilo;
    private int tentativasFalhas;
    private final int MAX_TENTATIVAS_FALHAS = 3;
    private final int MAX_CLIENTES = 100; // Número máximo de clientes a adicionar

    public SargentoTainha(Barbearia barbearia, int tempoCochilo) {
        this.barbearia = barbearia;
        this.tempoCochilo = tempoCochilo;
    }

    @Override
    public void run() {
        for (int i = 0; i < MAX_CLIENTES; i++) {
            try {
                Thread.sleep(tempoCochilo * 1000L);

                int categoria = ThreadLocalRandom.current().nextInt(0, 4);
                if (categoria == 0) {
                    barbearia.pausa(1);
                    System.out.println("Sargento Tainha tirando um cochilo...");
                    continue;
                }

                int tempoServico = switch (categoria) {
                    case 1 -> ThreadLocalRandom.current().nextInt(4, 7);
                    case 2 -> ThreadLocalRandom.current().nextInt(2, 5);
                    case 3 -> ThreadLocalRandom.current().nextInt(1, 4);
                    default -> 0;
                };

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
                Thread.currentThread().interrupt();
                System.out.println("Sargento Tainha foi interrompido.");
                return;
            }
        }
        System.out.println("Sargento Tainha adicionou todos os clientes. Indo para casa.");
    }
}