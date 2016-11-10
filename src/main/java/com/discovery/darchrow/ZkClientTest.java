/**
 * Copyright (c) 2016 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.discovery.darchrow;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.DataUpdater;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ZkClientTest {
	public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183");
        String node = "/myapp";

        // 订阅监听事件
        childChangesListener(zkClient, node);
        dataChangesListener(zkClient, node);
        stateChangesListener(zkClient);

        if (!zkClient.exists(node)) {
            zkClient.createPersistent(node, "hello zookeeper");
        }
        System.out.println(zkClient.readData(node).toString());

        zkClient.updateDataSerialized(node, new DataUpdater<String>() {

            public String update(String currentData) {
                return currentData + "-123";
            }
        });
        System.out.println(zkClient.readData(node).toString());

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅children变化
     *
     * @param zkClient
     * @param path
     * @author SHANHY
     * @create  2016年3月11日
     */
    public static void childChangesListener(ZkClient zkClient, final String path) {
        zkClient.subscribeChildChanges(path, new IZkChildListener() {

            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("clildren of path " + parentPath + ":" + currentChilds);
            }

        });
    }

    /**
     * 订阅节点数据变化
     *
     * @param zkClient
     * @param path
     * @author SHANHY
     * @create  2016年3月11日
     */
    public static void dataChangesListener(ZkClient zkClient, final String path){
        zkClient.subscribeDataChanges(path, new IZkDataListener(){

            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Data of " + dataPath + " has changed.");
            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("Data of " + dataPath + " has deleted.");
            }

        });
    }

    /**
     * 订阅状态变化
     *
     * @param zkClient
     * @author SHANHY
     * @create  2016年3月11日
     */
    public static void stateChangesListener(ZkClient zkClient){
        zkClient.subscribeStateChanges(new IZkStateListener() {

            public void handleStateChanged(KeeperState state) throws Exception {
                System.out.println("handleStateChanged");
            }

            public void handleSessionEstablishmentError(Throwable error) throws Exception {
                System.out.println("handleSessionEstablishmentError");
            }

            public void handleNewSession() throws Exception {
                System.out.println("handleNewSession");
            }
        });
    }
}
