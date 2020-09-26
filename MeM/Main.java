package MeM;

import Delivery.Transportadora;
import Factory.Fabrica;
import Store.Loja;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Main {

    //TODO: Globais
    public static int size = 50;
    public static char[] catalogo = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    //                                                A         B           C           D
    public static int[][][] productionTime =   {{{600,1000},{400,600},{1000,1200},{800,1000}},  //A
                                                {{200,400},{800,1000},{1200,1400},{600,800}},   //B
                                                {{1000,1200},{1200,1400},{400,600},{400,600}},  //C
                                                {{400,600},{800,1000},{600,800},{1000,1200}},   //D
                                                {{800,1000},{200,400},{400,600},{1200,1400}},   //E
                                                {{1400,1600},{1000,1200},{400,600},{800,1000}}, //F
                                                {{400,600},{1000,1200},{1000,1200},{600,800}},  //G
                                                {{800,1000},{600,800},{400,600},{1200,1400}}};  //H

    public static int contadorGlobal = 1;
    public static File file = new File("C:\\Users\\User\\OneDrive\\IDE\\InteliJ\\programacaoDistribuidaParalelaEConcorrente\\produtorConsumidor\\src\\MeM\\data.txt");

    //TODO: Lista de Pedidos
    public static int primeiroPedidos = 0;
    public static int ultimoPedidos = 0;

    //TODO: Lista de Transportes
    public static int primeiroTransporte = 0;
    public static int ultimoTransporte = 0;



    public static void main(String[] args) throws IOException {

        //TODO: Data
        BufferedWriter data = new BufferedWriter(new FileWriter(file, false));
        data.close();   //Limpar o txt

        //TODO: Mutex
        Semaphore mutexLoja = new Semaphore(1);
        Semaphore mutexFabrica = new Semaphore(1);
        Semaphore mutexTransportadora = new Semaphore(1);


        //TODO: Globais
        int minSleepLoja = 10;
        int maxSleepLoja = 150;
        int minSleepTransportadoraA = 100;
        int maxSleepTransportadoraA = 200;
        int minSleepTransportadoraB = 400;
        int maxSleepTransportadoraB = 600;

        //TODO: Lista de Pedidos
        String[] bufferPedido = new String[size];
        Semaphore itemPedidos = new Semaphore(0);
        Semaphore espacosPedidos = new Semaphore(size);


        //TODO: Lista de Transportes
        String[] bufferTransporte = new String[size];
        Semaphore itemTransporte = new Semaphore(0);
        Semaphore espacosTransporte = new Semaphore(size);

        //TODO: Lojas
        Loja lojaA = new Loja('A', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaB = new Loja('B', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaC = new Loja('C', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaD = new Loja('D', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaE = new Loja('E', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaF = new Loja('F', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaG = new Loja('G', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);
        Loja lojaH = new Loja('H', maxSleepLoja, minSleepLoja, mutexLoja, bufferPedido, itemPedidos, espacosPedidos);

        //TODO: Fabricas
        Fabrica fabricaA = new Fabrica('A', 4, mutexFabrica, bufferPedido, itemPedidos, espacosPedidos, bufferTransporte, itemTransporte, espacosTransporte);
        Fabrica fabricaB = new Fabrica('B', 1, mutexFabrica, bufferPedido, itemPedidos, espacosPedidos, bufferTransporte, itemTransporte, espacosTransporte);
        Fabrica fabricaC = new Fabrica('C', 4, mutexFabrica, bufferPedido, itemPedidos, espacosPedidos, bufferTransporte, itemTransporte, espacosTransporte);
        Fabrica fabricaD = new Fabrica('D', 4, mutexFabrica, bufferPedido, itemPedidos, espacosPedidos, bufferTransporte, itemTransporte, espacosTransporte);


        //TODO: Transportadoras
        Transportadora transportadoraA = new Transportadora('A', maxSleepTransportadoraA,minSleepTransportadoraA, mutexTransportadora, 10, bufferTransporte, itemTransporte, espacosTransporte, file);
        Transportadora transportadoraB = new Transportadora('B', maxSleepTransportadoraB,minSleepTransportadoraB, mutexTransportadora, 20, bufferTransporte, itemTransporte, espacosTransporte, file);

        try {
            //TODO: Lojas
            lojaA.start();
            lojaB.start();
            lojaC.start();
            lojaD.start();
            lojaE.start();
            lojaF.start();
            lojaG.start();
            lojaH.start();

            //TODO: Fabricas
            fabricaA.start();
            fabricaB.start();
            fabricaC.start();
            fabricaD.start();

            //TODO: Transportadoras
            transportadoraA.start();
            transportadoraB.start();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
