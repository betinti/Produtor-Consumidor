package Delivery;

import MeM.Main;

import java.io.File;
import java.util.concurrent.Semaphore;

public class Transportadora extends Thread{

    //TODO: Global
    private final char Id;
    private final int minSleep;
    private final int maxSleep;
    private Semaphore mutex;

    //TODO: Consumir
    private String[] bufferTransporte;
    private Semaphore itemTransporte;
    private Semaphore espacosTransporte;

    //TODO: Save data
    private final File file;

    //TODO: Simultaneidade
    private final int simultaneoMax;
    private int contSimultaneo;

    //TODO: Interno/Produzir
    private String[] bufferInterno;
    private Semaphore itemInterno;
    private Semaphore espacoInterno;
    private int[] primeiro = {0};
    private int[] ultimo = {0};
    private final Semaphore mutexInterno;

    public Transportadora(char Id, int maxSleep, int minSleep, Semaphore mutex, int simultaneoMax,
                   String[] bufferTransporte, Semaphore itemTransporte, Semaphore espacosTransporte,
                          File file) {
        //TODO: Global
        this.Id = Id;
        this.maxSleep = maxSleep;
        this.minSleep = minSleep;
        this.mutex = mutex;
        //TODO: Consumir
        this.bufferTransporte = bufferTransporte;
        this.itemTransporte = itemTransporte;
        this.espacosTransporte = espacosTransporte;
        //TODO: Save data
        this.file = file;
        //TODO: Simultaneidade
        this.simultaneoMax = simultaneoMax; this.contSimultaneo = 0;
        //TODO: Interno/Produzir
        this.bufferInterno = new String[simultaneoMax];
        this.itemInterno = new Semaphore(0); this.espacoInterno = new Semaphore(simultaneoMax);
        this.mutexInterno = new Semaphore(1);
    }

    @Override
    public void run() {
        try {
            while (contSimultaneo < simultaneoMax){
                Transportar Fe = new Transportar(this.Id, this.contSimultaneo, this.minSleep, this.maxSleep, this.mutexInterno,
                        this.bufferTransporte, this.itemTransporte, this.espacosTransporte,
                        this.simultaneoMax, this.itemInterno, this.espacoInterno, this.bufferInterno, this.primeiro, this.file);
                Fe.start();
                contSimultaneo++;
            }
            while (true){
                //consumo da fabrica -> produz interno
                espacoInterno.acquire();
                    itemTransporte.acquire();
                        mutex.acquire();
                        //consumo -> Fabrica
                        String encomenda = bufferTransporte[Main.primeiroTransporte];       //consome
                        bufferTransporte[Main.primeiroTransporte] = null;
                        if (encomenda != null){
                            Main.primeiroTransporte = (Main.primeiroTransporte+1) % Main.size;  //consome
                            //produz -> Interno
                            bufferInterno[ultimo[0]] = encomenda;                               //produz
                            ultimo[0] = ultimo[0] + 1;                                            //produz
                            if (ultimo[0] >= simultaneoMax) ultimo[0] = 0;                      //produz
                        }
                        mutex.release();
                    espacosTransporte.release();
                itemInterno.release();
            }
        } catch (Exception e){
            System.out.println("Transportadora");
            e.printStackTrace();
        }
    }
}
