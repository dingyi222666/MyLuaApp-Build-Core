/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dingyi.terminal.virtualprocess;


import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple alternative to JDK {@link PipedInputStream};
 * @see ByteQueueOutputStream
 * @since 2.9.0
 */
public class ByteQueueInputStream extends InputStream {

     final ByteQueue blockingQueue;

    final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a new instance with no limit to its internal buffer size.
     */
    public ByteQueueInputStream() {
        this(new ByteQueue(1024));
    }

    /**
     * Constructs a new instance with given buffer
     *
     * @param blockingQueue backing queue for the stream
     */
    public ByteQueueInputStream(final ByteQueue blockingQueue) {
        this.blockingQueue = Objects.requireNonNull(blockingQueue, "blockingQueue");
    }

    /**
     * Creates a new QueueOutputStream instance connected to this. Writes to the output stream will be visible to this
     * input stream.
     *
     * @return QueueOutputStream connected to this stream
     */
    public ByteQueueOutputStream newQueueOutputStream() {
        return new ByteQueueOutputStream(blockingQueue);
    }

    private static final int EOF = -1;

    private final byte[] readBuffer = new byte[1];

    /**
     * Reads and returns a single byte.
     *
     * @return either the byte read or {@code -1} if the end of the stream has been reached
     */
    @Override
    public int read() throws IOException {
        return read(readBuffer);
    }


    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        lock.lock();
        if (isClose) {
            throw new IOException("Stream is closed");
        }
        int read =  blockingQueue.read(b,off,true);
       /* System.out.write(b,off,read);*/
        lock.unlock();
        return read;
    }

    private boolean isClose = false;

    @Override
    public void close() throws IOException {
        isClose = true;
    }
}
