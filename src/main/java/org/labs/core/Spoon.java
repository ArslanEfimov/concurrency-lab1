package org.labs.core;

import java.util.concurrent.locks.ReentrantLock;

public class Spoon {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final long id;

    public Spoon(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void take(){
        lock.lock();
    }

    public void put(){
        lock.unlock();
    }
}
