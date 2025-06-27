import java.util.concurrent.ThreadLocalRandom;
import java.util.*;
public class BarbeariaRecrutaZero {
    public static void main(String[] args) {
        int tempoCochiloSargento = args.length > 0 ? Integer.parseInt(args[0]) : 2;
        Scanner sc = new Scanner(System.in);
        Barbearia barbearia = new Barbearia(20);
        System.out.println("Quantos barbeiros estão trabalhando ?");
        int  qntBarbeiros = sc.nextInt();

        Thread barbeiro1 = new Thread(new Barbeiro(barbearia, "Recruta Zero"));
        Thread tenenteEscovinha = new Thread(new TenenteEscovinha(barbearia));
        Thread sargentoTainha = new Thread(new SargentoTainha(barbearia, tempoCochiloSargento));
        if (qntBarbeiros == 1) {
            // Caso A: Um barbeiro
            System.out.println("=== Caso A: Um Barbeiro ===");
            simular(barbearia, barbeiro1, sargentoTainha, tenenteEscovinha);
        }
        else if (qntBarbeiros == 2) {
            // Caso B: Dois barbeiros (descomentar para testar)
            Thread barbeiro2 = new Thread(new Barbeiro(barbearia, "Dentinho"));
            System.out.println("=== Caso B: Dois Barbeiros ===");
            simular(barbearia, barbeiro1, barbeiro2, sargentoTainha, tenenteEscovinha);

        }

        else if (qntBarbeiros == 3) {
            // Caso C: Três barbeiros (descomentar para testar)
            Thread barbeiro2 = new Thread(new Barbeiro(barbearia, "Dentinho"));
            Thread barbeiro3 = new Thread(new Barbeiro(barbearia, "Otto"));
            System.out.println("=== Caso C: Três Barbeiros ===");
            simular(barbearia, barbeiro1, barbeiro2, barbeiro3, sargentoTainha, tenenteEscovinha);
        }


    }

    private static void simular(Barbearia barbearia, Thread barbeiro1, Thread sargentoTainha, Thread tenenteEscovinha) {
        barbeiro1.start();
        sargentoTainha.start();
        tenenteEscovinha.start();

        try {
            sargentoTainha.join();
            System.out.println("Sargento Tainha foi para casa. Aguardando clientes na barbearia...");
            while (barbearia.temClientes()) {
                Thread.sleep(1000); // Aguarda 1 segundo e verifica novamente
            }
            barbeiro1.interrupt();
            tenenteEscovinha.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=== Simulação Finalizada ===");
        barbearia.imprimirEstatisticasFinais();
    }
    private static void simular(Barbearia barbearia, Thread barbeiro1, Thread barbeiro2, Thread sargentoTainha, Thread tenenteEscovinha) {
        barbeiro1.start();
        barbeiro2.start();
        sargentoTainha.start();
        tenenteEscovinha.start();

        try {
            sargentoTainha.join();
            System.out.println("Sargento Tainha foi para casa. Aguardando clientes na barbearia...");
            while (barbearia.temClientes()) {
                Thread.sleep(1000); // Aguarda 1 segundo e verifica novamente
            }
            barbeiro1.interrupt();
            barbeiro2.interrupt();
            tenenteEscovinha.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=== Simulação Finalizada ===");
        barbearia.imprimirEstatisticasFinais();
    }
    private static void simular(Barbearia barbearia, Thread barbeiro1, Thread barbeiro2, Thread barbeiro3, Thread sargentoTainha, Thread tenenteEscovinha) {
        barbeiro1.start();
        barbeiro2.start();
        barbeiro3.start();
        sargentoTainha.start();
        tenenteEscovinha.start();

        try {
            sargentoTainha.join();
            System.out.println("Sargento Tainha foi para casa. Aguardando clientes na barbearia...");
            while (barbearia.temClientes()) {
                Thread.sleep(1000); // Aguarda 1 segundo e verifica novamente
            }
            barbeiro1.interrupt();
            barbeiro2.interrupt();
            barbeiro3.interrupt();
            tenenteEscovinha.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=== Simulação Finalizada ===");
        barbearia.imprimirEstatisticasFinais();
    }

}