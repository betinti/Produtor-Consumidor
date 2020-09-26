package Factory;

import MeM.Main;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Fabricar extends Thread{

    //TODO: Global
    private final char Id;
    private final int key;
    private int maxSleep;
    private int minSleep;
    private final Semaphore mutex;

    //TODO: Interno/Consumir
    private final int simultaneoMax;
    private final Semaphore itemInterno;
    private final Semaphore espacoInterno;
    private final String[] bufferInterno;
    private int[] primeiro;

    //TODO: Transportadora/Produzir
    private final Semaphore itemTransporte;
    private final Semaphore espacosTransporte;
    private String[] bufferTransporte;

    Fabricar(char Id, int key, int simultaneoMax, Semaphore mutex,
             Semaphore itemInterno, Semaphore espacoInterno, String[] bufferInterno, int[] primeiro,
             Semaphore itemTransporte, Semaphore espacosTransporte, String[] bufferTransporte){
        //TODO: Global
        this.Id = Id;
        this.key = key;
        this.mutex = mutex;
        //TODO: Interno
        this.itemInterno = itemInterno;
        this.espacoInterno = espacoInterno;
        this.bufferInterno = bufferInterno;
        this.primeiro = primeiro;   this.simultaneoMax = simultaneoMax;
        //TODO: Transportadora
        this.itemTransporte = itemTransporte;
        this.espacosTransporte = espacosTransporte;
        this.bufferTransporte = bufferTransporte;
    }

    private void getSleep(String data){
        this.minSleep = Integer.parseInt(data.split(";")[4]);
        this.maxSleep = Integer.parseInt(data.split(";")[5]);
    }

    private String tratar(String fab, long time){
        String result = "";
        String[] mem = fab.split(";");
        int cont = 0;
        while (cont < 4){
            result = result + mem[cont] + ";";
            cont++;
        } result = result + time + ";";
        return result;
    }

    @Override
    public void run() {
        while (true)
        try {
            itemInterno.acquire();
                espacosTransporte.acquire();
                    mutex.acquire();
                        //consumir -> Interno
                        String fabricacao = bufferInterno[primeiro[0]];
                        bufferInterno[primeiro[0]] = null;
                        if (fabricacao != null){
                            long time = System.currentTimeMillis();
                            primeiro[0] = primeiro[0] + 1;
                            if (primeiro[0] >= simultaneoMax) primeiro[0] = 0;
                            getSleep(fabricacao);
                            //produzir -> Transportadora
                            String mem = tratar(fabricacao, time);
                            bufferTransporte[Main.ultimoTransporte] = mem;
                            Main.ultimoTransporte = Main.ultimoTransporte + 1;
                            if (Main.ultimoTransporte >= Main.size) Main.ultimoTransporte = 0;
                            System.out.println("Fabrica " + Id  + " (" + key + ") " + " produziu " + fabricacao.split(";")[1]);
                        }
                    mutex.release();
                itemTransporte.release();
            espacoInterno.release();
            if (fabricacao != null){
                Thread.sleep(new Random().nextInt(maxSleep-minSleep) + minSleep);
            }
        } catch (Exception e){
            System.out.println("FabricarNew" );
            e.printStackTrace();
        }
    }
}
