/**
 * Copyright (C) 2008 Happy Fish / YuQing
 * <p>
 * FastDFS Java Client may be copied only under the terms of the GNU Lesser
 * General Public License (LGPL).
 * Please visit the FastDFS Home Page https://github.com/happyfish100/fastdfs for more detail.
 */

package com.xpay.csource.fastdfs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracker server group
 *
 * @author Happy Fish / YuQing
 * @version Version 1.17
 * <p>
 * this java file is modified by cmf
 */
public class TrackerGroup {
    public final InetSocketAddress[] tracker_servers;   //多线程不会修改

    private AtomicLong tracker_server_index;             //多线程会进行修改
    private AtomicBoolean[] tracker_valid;              //多线程会进行修改

    /**
     * Constructor
     *
     * @param tracker_servers tracker servers
     */
    public TrackerGroup(InetSocketAddress[] tracker_servers) {
        this.tracker_servers = tracker_servers;
        this.tracker_server_index = new AtomicLong(0);
        this.tracker_valid = new AtomicBoolean[tracker_servers.length];
        for (int i = 0; i < this.tracker_valid.length; i++) {
            this.tracker_valid[i] = new AtomicBoolean(true);
        }
    }

    /**
     * return connected tracker server
     *
     * @return connected tracker server, null for fail
     */
    public TrackerServer getConnection(int serverIndex) throws IOException {
        Socket sock = new Socket();
        sock.setReuseAddress(true);
        sock.setSoTimeout(ClientGlobal.g_network_timeout);
        sock.connect(this.tracker_servers[serverIndex], ClientGlobal.g_connect_timeout);
        return new TrackerServer(sock, this.tracker_servers[serverIndex]);
    }

    /**
     * 如果所有tracker都无效，则直接切换至下一个，否则，切换至下一个有效的
     *
     * @return
     */
    private int switchNextIndex() {
        int next_index = (int) (tracker_server_index.incrementAndGet() % tracker_servers.length);
        if (tracker_valid[next_index].get()) {
            //如果下一个是有效的，则返回下一个
            return next_index;
        }
        //否则进行遍历，寻找
        for (int i = next_index + 1; i < next_index + tracker_servers.length; i++) {
            if (this.tracker_valid[i % tracker_servers.length].get()) {
                tracker_server_index.set(i);
                return i % tracker_servers.length;
            }
        }
        //如果找不到有效的，则返回next_index，进行尝试
        return next_index;
    }

    /**
     * return connected tracker server
     *
     * @return connected tracker server, null for fail
     */
    public TrackerServer getConnection() throws IOException {
        for (int i = 0; i < this.tracker_servers.length; i++) {
            int next_index = switchNextIndex();
            try {
                TrackerServer server = this.getConnection(next_index);
                tracker_valid[next_index].set(true);
                return server;
            } catch (IOException ex) {
                tracker_valid[next_index].set(false);
                System.err.println("connect to server " + this.tracker_servers[next_index].getAddress().getHostAddress() + ":" + this.tracker_servers[next_index].getPort() + " fail");
            }
        }
        return null;
    }


    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public void startMonitorThread() {
        executorService.scheduleAtFixedRate(() -> {
            for (int i = 0; i < tracker_valid.length; i++) {
                if (!tracker_valid[i].get() && testTrackerAvailable(tracker_servers[i])) {
                    tracker_valid[i].set(true);
                }
            }
        }, 1, 10, TimeUnit.SECONDS);
    }


    public void stopMonitorThread() {
        executorService.shutdown();
    }


    private static boolean testTrackerAvailable(InetSocketAddress inetSocketAddress) {
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(300);
            socket.connect(inetSocketAddress);
            System.out.println("tracker is available " + inetSocketAddress);
        } catch (IOException e) {
            System.out.println("tracker is unavailable " + inetSocketAddress);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        return true;
    }
}
