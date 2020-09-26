package Store;

import MeM.Main;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Loja extends Thread{

    //TODO: Global
    private final char Id;
    private int contVendas;
    private final int minSleep;
    private final int maxSleep;
    private Semaphore mutex;
    private Random rand = new Random();

    //TODO: Produzir
    private String[] bufferPedido;
    private Semaphore itemPedidos;
    private Semaphore espacosPedidos;


    public Loja(char Id, int maxSleep, int minSleep, Semaphore mutex,
                String[] bufferPedido, Semaphore itemPedidos, Semaphore espacosPedidos){
        this.Id = Id;
        this.bufferPedido = bufferPedido;
        this.mutex = mutex;
        this.itemPedidos = itemPedidos;
        this.espacosPedidos = espacosPedidos;
        this.minSleep = minSleep;
        this.maxSleep = maxSleep;
        this.contVendas = 0;
    }

    @Override
    public void run(){
        while (true)
        try {
            contVendas++;
            String some = contVendas + " " + Main.catalogo[rand.nextInt(Main.catalogo.length)];
            espacosPedidos.acquire();
                mutex.acquire();
                    String produto = Main.contadorGlobal + ";" + String.valueOf(Id)  + some + ";" + System.currentTimeMillis() + ";";
                    bufferPedido[Main.ultimoPedidos] = produto; //produzir
                    Main.contadorGlobal = Main.contadorGlobal + 1;
                    Main.ultimoPedidos = Main.ultimoPedidos + 1;
                    if (Main.ultimoPedidos >= Main.size) Main.ultimoPedidos = 0;
                mutex.release();
            itemPedidos.release();
            Thread.sleep(new Random().nextInt(maxSleep-minSleep) + minSleep);
            System.out.println("Loja: " + Id + " vendeu seu " + contVendas + "° produto " + some);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
