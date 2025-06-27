public class TenenteEscovinha implements Runnable {
    private final Barbearia barbearia;
    private final long TEMPO_RELATORIO = 3000; // Tempo em milissegundos

    public TenenteEscovinha(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(TEMPO_RELATORIO);
                gerarRelatorioParcial();
            } catch (InterruptedException e) {
                System.out.println("Tenente Escovinha foi interrompido.");
                Thread.currentThread().interrupt();
                // Gere o relatório final aqui, se necessário
            }
        }
    }

    private synchronized void gerarRelatorioParcial() {
        System.out.println("\n=== Relatório Parcial da Barbearia ===");
        System.out.println("Estado de ocupação da(s) cadeira(s): " + barbearia.getStatusOcupacao());
        System.out.println("Comprimento médio das filas:");
        System.out.println("Oficiais: " + barbearia.getComprimentoFilaOficial());
        System.out.println("Sargentos: " + barbearia.getComprimentoFilaSargento());
        System.out.println("Cabos: " + barbearia.getComprimentoFilaCabo());
        System.out.println("Tempo médio de atendimento por categoria:");
        System.out.println("Oficiais: " + barbearia.getTempoMedioAtendimentoOficial() + " segundos");
        System.out.println("Sargentos: " + barbearia.getTempoMedioAtendimentoSargento() + " segundos");
        System.out.println("Cabos: " + barbearia.getTempoMedioAtendimentoCabo() + " segundos");
        System.out.println("Número de atendimentos por categoria:");
        System.out.println("Oficiais: " + barbearia.contadorOficial.get());
        System.out.println("Sargentos: " + barbearia.contadorSargento.get());
        System.out.println("Cabos: " + barbearia.contadorCabo.get());
        System.out.println("Número total de clientes por categoria:");
        System.out.println("Oficiais: " + barbearia.contadorOficial.get());
        System.out.println("Sargentos: " + barbearia.contadorSargento.get());
        System.out.println("Cabos: " + barbearia.contadorCabo.get());
        System.out.println("Número total de pausas: " + barbearia.getContadorPausa());
    }
}
