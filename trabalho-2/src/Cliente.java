public class Cliente {
    public final int categoria;
    public final int tempoServico;

    public Cliente(int categoria, int tempoServico) {
        this.categoria = categoria;
        this.tempoServico = tempoServico;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "categoria=" + categoria +
                ", tempoServico=" + tempoServico +
                '}';
    }
}
