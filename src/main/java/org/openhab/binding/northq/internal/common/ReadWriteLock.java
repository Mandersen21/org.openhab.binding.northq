/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.common;

/**
 * The {@link ReadWriteLock} its a lock.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
public class ReadWriteLock {

    private static ReadWriteLock instance;

    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    private ReadWriteLock() {

    }

    public static ReadWriteLock getInstance() {
        if (instance == null) {
            instance = new ReadWriteLock();
        }

        return instance;
    }

    public synchronized void lockRead() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }

    public synchronized void unlockRead() {
        readers--;
        notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;

        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }

    public synchronized void unlockWrite() throws InterruptedException {
        writers--;
        notifyAll();
    }
}