// Classe que representa um cliente com categoria e tempo de serviço
public class Cliente {
    public final int categoria; // Oficial - Sargento - Cabo
    public final int tempoServico;

    // Construtor da classe Cliente que inicializa os atributos categoria e tempo de serviço
    public Cliente(int categoria, int tempoServico) {
        this.categoria = categoria;
        this.tempoServico = tempoServico;
    }

    // Sobrescrita do método toString para exibir as informações do cliente estruturadas
    @Override
    public String toString() {
        return "Cliente{" +
                "categoria=" + categoria +
                ", tempoServico=" + tempoServico +
                '}';
    }
}
