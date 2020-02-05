package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    private boolean pause,active;
    public static final Object tieLock = new Object();
    public static Object pantalla = ControlFrame.pantalla;
    public static Object desempate = new Object();

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.pause=false;
        this.active=true;
    }

    public void run() {

        while (active) {
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            try {
                if(pause){
                    synchronized (pantalla){
                        pantalla.wait();
                        //Cuando se salga del wait, significa que desde el ControlFrame se ejecutó notifyAll y se despertaron los inmortales
                        renaudarInmortal();

                    }
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
    public void fight(Immortal i2){
        int thisHash = System.identityHashCode(this);
        int i2Hash = System.identityHashCode(i2);

        if (thisHash < i2Hash) {
            synchronized (this) {
                synchronized (i2) {
                    this.fight(i2,true);
                }
            }
        } else if (thisHash > i2Hash) {
            synchronized (i2) {
                synchronized (this) {
                    this.fight(i2,true);
                }
            }
        } else {
            synchronized (desempate) {
                synchronized (this) {
                    synchronized (i2) {
                        this.fight(i2,true);
                    }
                }
            }
        }
    }
    public void fight(Immortal i2,boolean flag) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
            i2.morir();
            immortalsPopulation.remove(i2);
        }

    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    public void morir(){
        this.active=false;
    }
    public void pausarInmortal(){
        this.pause=true;
    }
    public void renaudarInmortal(){
        this.pause=false;
    }
}
