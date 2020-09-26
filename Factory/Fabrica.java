package Factory;

import MeM.Main;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Fabrica extends Thread{

    //TODO: Global
    private final char Id;
    private int minSleep;
    private int maxSleep;
    private Semaphore mutex;
    private Random rand = new Random();

    //TODO: Consumidor -> Loja
    private String[] bufferPedido;
    private Semaphore itemPedidos;
    private Semaphore espacosPedidos;

    //TODO: Produtor -> Transportadora
    private String[] bufferTransporte;
    private Semaphore itemTransporte;
    private Semaphore espacosTransporte;

    //TODO: Simultaneidade
    private final int simultaneoMax;
    private int contSimultaneo;

    //TODO: Interno
    private String[] bufferInterno;
    private Semaphore itemInterno;
    private Semaphore espacoInterno;
    private int[] primeiro = {0};
    private int[] ultimo = {0};
    private final Semaphore mutexInterno;

    public Fabrica(char Id, int simultaneoMax, Semaphore mutex,
                   String[] bufferPedido, Semaphore itemPedidos, Semaphore espacosPedidos,
                   String[] bufferTransporte, Semaphore itemTransporte, Semaphore espacosTransporte){
        //TODO: Global:
        this.Id = Id;   this.mutex = mutex;
        //TODO: Simultaneidade
        this.simultaneoMax = simultaneoMax; this.contSimultaneo = 0;
        //TODO: Consumidor -> Loja
        this.bufferPedido = bufferPedido;
        this.espacosPedidos = espacosPedidos;   this.itemPedidos = itemPedidos;
        //TODO: Produtor -> Transportadora
        this.bufferTransporte = bufferTransporte;
        this.itemTransporte = itemTransporte;   this.espacosTransporte = espacosTransporte;
        //TODO: Interno
        this.bufferInterno = new String[simultaneoMax];
        this.itemInterno = new Semaphore(0);   this.espacoInterno = new Semaphore(simultaneoMax);
        this.mutexInterno = new Semaphore(1);
    }

    private void getSleep(String c){
        int i ,j;
        String[] bruto = c.split(";");
        String[] splited = bruto[1].split(" ");
        switch (this.Id){
            case 'A':
                i = 0;
                break;
            case 'B':
                i = 1;
                break;
            case 'C':
                i = 2;
                break;
            case 'D':
                i = 3;
                break;
            default:
                i = rand.nextInt(4);
                break;
        }
        switch (splited[1]){
            case "A":
                j = 0;
                break;
            case "B":
                j = 1;
                break;
            case "C":
                j = 2;
                break;
            case "D":
                j = 3;
                break;
            case "E":
                j = 4;
                break;
            case "F":
                j = 5;
                break;
            case "G":
                j = 6;
                break;
            case "H":
                j = 7;
                break;
            default:
                j = rand.nextInt(8);
                break;
        }
        this.minSleep = Main.productionTime[j][i][0];
        this.maxSleep = Main.productionTime[j][i][1];
    }

    @Override
    public void run() {
        try {
            while (contSimultaneo < simultaneoMax){
                Fabricar Fe = new Fabricar(this.Id, this.contSimultaneo, this.simultaneoMax,this.mutexInterno,
                        this.itemInterno, this.espacoInterno, this.bufferInterno, this.primeiro,
                        this.itemTransporte, this.espacosTransporte, this.bufferTransporte);
                Fe.start();
                contSimultaneo++;
            }
            while (true){
                itemPedidos.acquire();
                    espacoInterno.acquire();
                        mutex.acquire();
                            //consumo - Loja
                            String consumo = bufferPedido[Main.primeiroPedidos];
                            bufferPedido[Main.primeiroPedidos] = null;
                            if (consumo != null){
                                Main.primeiroPedidos = Main.primeiroPedidos + 1;
                                if (Main.primeiroPedidos >= Main.size) Main.primeiroPedidos = 0;
                                consumo = consumo + System.currentTimeMillis() + ";";
                                getSleep(consumo);
                                //produz - Interno
                                bufferInterno[ultimo[0]] = consumo + minSleep + ";" + maxSleep + ";";
                                ultimo[0] = ultimo[0] + 1;
                                if (ultimo[0] >= simultaneoMax) ultimo[0] = 0;
                                System.out.println("Fabrica " + Id + " consumiu " + consumo.split(";")[1]);
                            }
                        mutex.release();
                    itemInterno.release();
                espacosPedidos.release();
            }
        } catch (Exception e){
            System.out.println("Fabrica");
            e.printStackTrace();
        }
    }
}
