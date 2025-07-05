import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Classe que representa a barbearia, controlando acesso às cadeiras, filas por categoria e estatísticas
public class Barbearia {
    private final int NUM_CADEIRAS;
    private final Semaphore cadeiras; // Semáforo que representa as cadeiras disponíveis para clientes
    private final BlockingQueue<Cliente> filaOficial;
    private final BlockingQueue<Cliente> filaSargento;
    private final BlockingQueue<Cliente> filaCabo;
    private final AtomicInteger contadorPausa;
    private final AtomicInteger tempoTotalPausa;

    private final AtomicInteger totalClientesAtendidos;
    final AtomicInteger contadorOficial;
    final AtomicInteger contadorSargento;
    final AtomicInteger contadorCabo;
    private final AtomicInteger tempoTotalServicoOficial;
    private final AtomicInteger tempoTotalServicoSargento;
    private final AtomicInteger tempoTotalServicoCabo;

    // Lock e condição para coordenação entre threads
    private final Lock lock;
    private final Condition condicaoBarbeiro;

    // Construtor da barbearia, inicializando estruturas e contadores
    public Barbearia(int numCadeiras) {
        this.NUM_CADEIRAS = numCadeiras;
        this.cadeiras = new Semaphore(numCadeiras); // semáforo com número de cadeiras
        this.filaOficial = new LinkedBlockingQueue<>();
        this.filaSargento = new LinkedBlockingQueue<>();
        this.filaCabo = new LinkedBlockingQueue<>();
        this.totalClientesAtendidos = new AtomicInteger(0);
        this.contadorOficial = new AtomicInteger(0);
        this.contadorSargento = new AtomicInteger(0);
        this.contadorCabo = new AtomicInteger(0);
        this.tempoTotalServicoOficial = new AtomicInteger(0);
        this.tempoTotalServicoSargento = new AtomicInteger(0);
        this.tempoTotalServicoCabo = new AtomicInteger(0);
        this.lock = new ReentrantLock();
        this.condicaoBarbeiro = lock.newCondition();
        this.contadorPausa = new AtomicInteger(0);
        this.tempoTotalPausa = new AtomicInteger(0);
    }

    // Método para adicionar cliente à fila correspondente ou registrar uma pausa
    public boolean adicionarCliente(Cliente cliente) {
        if (cliente.categoria == 0) { // Categoria 0 é uma pausa
            pausa(cliente.tempoServico);
            return true;
        }

        // Tenta adquirir uma cadeira (semáforo)
        if (cadeiras.tryAcquire()) {
            lock.lock();  // garante exclusão mútua ao acessar as filas
            try {
                // Direciona cliente para a fila de acordo com a categoria
                switch (cliente.categoria) {
                    case 1 -> filaOficial.put(cliente);
                    case 2 -> filaSargento.put(cliente);
                    case 3 -> filaCabo.put(cliente);
                    default -> {
                        // Categoria inválida
                        System.err.println("Categoria de cliente inválida: " + cliente.categoria);
                        cadeiras.release(); // libera a cadeira adquirida
                        return false;
                    }
                }
                condicaoBarbeiro.signal(); // Notifica o barbeiro que há cliente esperando
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                cadeiras.release();
                return false;
            } finally {
                lock.unlock();
            }
        }
        return false; // Nenhuma cadeira disponível
    }

    // Retorna o próximo cliente da fila com prioridade: Oficial > Sargento > Cabo
    public Cliente atenderProximoCliente() throws InterruptedException {
        lock.lock();
        try {
            // Espera enquanto todas as filas estão vazias
            while (filaOficial.isEmpty() && filaSargento.isEmpty() && filaCabo.isEmpty()) {
                condicaoBarbeiro.await();
            }

            // Atende da fila de maior prioridade disponível
            if (!filaOficial.isEmpty()) {
                return filaOficial.take();
            } else if (!filaSargento.isEmpty()) {
                return filaSargento.take();
            } else {
                return filaCabo.take();
            }
        } finally {
            lock.unlock();
        }
    }

    // Registra uma pausa com a duração especificada
    public void pausa(int tempoPausa) {
        contadorPausa.incrementAndGet();
        tempoTotalPausa.addAndGet(tempoPausa);
    }

    // Retorna o número de pausas realizadas
    public int getContadorPausa() {
        return contadorPausa.get();
    }

    // Retorna o tempo médio das pausas
    public double getTempoMedioPausa() {
        return contadorPausa.get() == 0 ? 0 : (double) tempoTotalPausa.get() / contadorPausa.get();
    }

    // Registra estatísticas ao atender um cliente
    public void clienteAtendido(Cliente cliente) {
        cadeiras.release(); // Libera cadeira após atendimento
        totalClientesAtendidos.incrementAndGet();

        // Atualiza contadores e somatórios conforme categoria
        switch (cliente.categoria) {
            case 1 -> {
                contadorOficial.incrementAndGet();
                tempoTotalServicoOficial.addAndGet(cliente.tempoServico);
            }
            case 2 -> {
                contadorSargento.incrementAndGet();
                tempoTotalServicoSargento.addAndGet(cliente.tempoServico);
            }
            case 3 -> {
                contadorCabo.incrementAndGet();
                tempoTotalServicoCabo.addAndGet(cliente.tempoServico);
            }
        }
    }

    // Verifica se ainda há clientes aguardando atendimento
    public boolean temClientes() {
        lock.lock();
        try {
            return !filaOficial.isEmpty() || !filaSargento.isEmpty() || !filaCabo.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    // Retorna o total de clientes atendidos
    public int getTotalClientes() {
        return totalClientesAtendidos.get();
    }

    // Retorna a porcentagem de ocupação das cadeiras
    public double getPorcentagemOcupacao() {
        return (double) (NUM_CADEIRAS - cadeiras.availablePermits()) / NUM_CADEIRAS * 100;
    }

    // Métodos para obter o tamanho das filas por categoria
    public int getComprimentoFilaOficial() {
        return filaOficial.size();
    }

    public int getComprimentoFilaSargento() {
        return filaSargento.size();
    }

    public int getComprimentoFilaCabo() {
        return filaCabo.size();
    }

    // Métodos para calcular tempo médio de atendimento por categoria
    public double getTempoMedioAtendimentoOficial() {
        return contadorOficial.get() == 0 ? 0 : (double) tempoTotalServicoOficial.get() / contadorOficial.get();
    }

    public double getTempoMedioAtendimentoSargento() {
        return contadorSargento.get() == 0 ? 0 : (double) tempoTotalServicoSargento.get() / contadorSargento.get();
    }

    public double getTempoMedioAtendimentoCabo() {
        return contadorCabo.get() == 0 ? 0 : (double) tempoTotalServicoCabo.get() / contadorCabo.get();
    }

    // Retorna uma string com o status atual da ocupação das cadeiras
    public String getStatusOcupacao() {
        double porcentagemOcupacao = getPorcentagemOcupacao();
        double porcentagemLivre = 100 - porcentagemOcupacao;
        return String.format("%.1f%% ocupados, %.1f%% livre.", porcentagemOcupacao, porcentagemLivre);
    }

    // Exibe relatório com todas as estatísticas da simulação
    public void imprimirEstatisticasFinais() {

            System.out.println("\n=== Relatório Final da Barbearia ===");
            System.out.println("Total de clientes atendidos: " + getTotalClientes());
            System.out.println("Estado de ocupação da(s) cadeira(s): " + getStatusOcupacao());
            System.out.println("Comprimento médio das filas:");
            System.out.println("Oficiais: " + getComprimentoFilaOficial());
            System.out.println("Sargentos: " + getComprimentoFilaSargento());
            System.out.println("Cabos: " + getComprimentoFilaCabo());
            System.out.println("Tempo médio de atendimento por categoria:");
            System.out.println("Oficiais: " + getTempoMedioAtendimentoOficial() + " segundos");
            System.out.println("Sargentos: " + getTempoMedioAtendimentoSargento() + " segundos");
            System.out.println("Cabos: " + getTempoMedioAtendimentoCabo() + " segundos");
            System.out.println("Número de atendimentos por categoria:");
            System.out.println("Oficiais: " + contadorOficial.get());
            System.out.println("Sargentos: " + contadorSargento.get());
            System.out.println("Cabos: " + contadorCabo.get());
            System.out.println("Número total de clientes por categoria:");
            System.out.println("Oficiais: " + contadorOficial.get());
            System.out.println("Sargentos: " + contadorSargento.get());
            System.out.println("Cabos: " + contadorCabo.get());
            System.out.println("Número total de pausas: " + getContadorPausa());

    }
}
