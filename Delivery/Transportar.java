package Delivery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Transportar extends Thread{

    //TODO: Global
    private final char Id;
    private final int key;
    private final int minSleep;
    private final int maxSleep;
    private final Semaphore mutex;

    //TODO: Consumir
    private final String[] bufferTransporte;
    private final Semaphore itemTransporte;
    private final Semaphore espacosTransporte;

    //TODO: Interno/Consumir
    private final int simultaneoMax;
    private final Semaphore itemInterno;
    private final Semaphore espacoInterno;
    private final String[] bufferInterno;
    private int[] primeiro;

    //TODO: save data
    private final BufferedWriter data;

    Transportar(char Id, int key, int minSleep, int maxSleep, Semaphore mutex,
                String[] bufferTransporte, Semaphore itemTransporte, Semaphore espacosTransporte,
                int simultaneoMax, Semaphore itemInterno, Semaphore espacoInterno, String[] bufferInterno,
                int[] primeiro, File file) throws IOException {
        //TODO: Global
        this.Id = Id;   this.key = key;
        this.minSleep = minSleep;   this.maxSleep = maxSleep;
        this.mutex = mutex;
        //TODO: Consumir
        this.bufferTransporte = bufferTransporte;
        this.itemTransporte = itemTransporte;   this.espacosTransporte = espacosTransporte;
        //TODO: Interno/Consumir
        this.simultaneoMax = simultaneoMax; this.itemInterno = itemInterno;
        this.espacoInterno = espacoInterno; this.bufferInterno = bufferInterno;
        this.primeiro = primeiro;
        //TODO: save data
        this.data = new BufferedWriter(new FileWriter(file, true));
    }

    @Override
    public void run() {
        while (true)
        try {
            itemInterno.acquire();
                itemTransporte.acquire();
                    mutex.acquire();
                        //consumo -> Interno
                        String mem = bufferInterno[primeiro[0]];
                        String entrega = mem + System.currentTimeMillis();
                        bufferInterno[primeiro[0]] = null;
                        if (mem != null){
                            primeiro[0] = primeiro[0] + 1;
                            if (primeiro[0] >= simultaneoMax) primeiro[0] = 0;
                            data.append(entrega);
                            data.newLine();
                            data.flush();
                        mutex.release();
                            System.out.println("Transportadora " + Id + " (" + key + ") " + " consumiu " + entrega.split(";")[1]);
                        }
                espacosTransporte.release();
            espacoInterno.release();
            if (mem != null){
                Thread.sleep(new Random().nextInt(maxSleep-minSleep) + minSleep);
            }
        } catch (Exception e){
            System.out.println("TransportarNew");
            e.printStackTrace();
        }
    }
}
