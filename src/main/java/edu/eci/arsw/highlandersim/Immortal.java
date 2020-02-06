package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.CopyOnWriteArrayList;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private AtomicInteger health;
    
    private int defaultDamageValue;

    private final CopyOnWriteArrayList<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    private boolean pause,active,alive;
    public static final Object tieLock = new Object();
    public static Object pantalla = ControlFrame.pantalla;
    private static Object desempate = new Object();

    private static AtomicInteger poblacion;

    private static AtomicInteger pausedThreads = new AtomicInteger(0);

    public Immortal(String name, CopyOnWriteArrayList<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
        this.pause=false;
        this.active=true;
        this.alive=true;
        this.poblacion = new AtomicInteger(immortalsPopulation.size());
    }

    public void run() {

        while (true) {

            synchronized(this){
                while(pause) {
                    pausedThreads.getAndIncrement();
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);
            if(getHealth() <= 0){
                poblacion.getAndDecrement();
                immortalsPopulation.remove(this);
                break;
            }
            this.fight(im);
            try {

                Thread.sleep(15);

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }

    }
    public void fight(Immortal i2){
        long thisId = this.getId();
        long i2Id = i2.getId();

        if (thisId < i2Id) {
            synchronized (this) {
                synchronized (i2) {
                    this.fight(i2,true);
                }
            }
        }else {
            synchronized (i2) {
                synchronized (this) {
                    this.fight(i2,true);
                }
            }
        }

    }
    public void fight(Immortal i2,boolean flag) {

        if (i2.getHealth() > 0 && this.getHealth()>0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            if (i2.getHealth()>=0) {
                this.health = new AtomicInteger(defaultDamageValue+this.health.get());
            }

            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");

        }

    }

    public void changeHealth(int v) {
        health = new AtomicInteger(v);
    }

    public int getHealth() {
        return health.get();
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    public void morir(){
        this.active=false;
        this.alive=false;
    }

    public static int getPausedThreads(){
        return pausedThreads.get();
    }

    public static int getNumInmortales(){
        return poblacion.get();
    }
    public void pausarInmortal(){
        this.pause=true;
    }
    public void renaudarInmortal(){
        this.pause=false;
        pausedThreads.set(0);
        synchronized (this) {
            notify();
        }
    }
}















