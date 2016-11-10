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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

public class Test {
	// 会话超时时间，设置为与系统默认时间一致
    private static final int SESSION_TIMEOUT = 30 * 1000;

    // 创建 ZooKeeper 实例
    private ZooKeeper zk;

    // 创建 Watcher 实例
    private Watcher wh = new Watcher() {
        /**
         * Watched事件
         */
        public void process(WatchedEvent event) {
            System.out.println("WatchedEvent >>> " + event.toString());
        }
    };

    // 初始化 ZooKeeper 实例
    private void createZKInstance() throws IOException {
        // 连接到ZK服务，多个可以用逗号分割写
        zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", Test.SESSION_TIMEOUT, this.wh);

        /*
         *  有server1、server2、server3这三个服务，在client去连接zk的时候，指向server1初始化的过程中是没有问题的，然而刚刚初始化完成，准备去连接server1的时候，server1因为网络等原因挂掉了。 
		 *	然而对client来说，它会拿server1的配置去请求连接，这时肯定会报连接被拒绝的异常以致启动退出。 
		 *	所以优雅的解决这个问题的方法思路就是“在连接的时候判断连接状态，如果未连接成功，程序自动使用其他连接去请求连接”，这样来避开这种罕见的异常问题。
         */
        
        if(!zk.getState().equals(States.CONNECTED)){
            while(true){
                if(zk.getState().equals(States.CONNECTED)){
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 此方法测试结论：
     * zk的watch机制，一次性触发器，用于统一配置管理
     * 数据发生改变，会将这个改变发送给设置了watch的客户端
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private void testWatch() throws IOException, InterruptedException, KeeperException {
        System.out.println("\n1. 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限： OPEN_ACL_UNSAFE ，节点类型： Persistent");
        zk.create("/zoo2", "myData2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        System.out.println("\n2. 查看是否创建成功： ");
        System.out.println(new String(zk.getData("/zoo2", this.wh, null)));// 添加Watch

        // 前面一行我们添加了对/zoo2节点的监视，所以这里对/zoo2进行修改的时候，会触发Watch事件。
        System.out.println("\n3. 修改节点数据 ");
        zk.setData("/zoo2", "shanhy20160310".getBytes(), -1);

        // 这里再次进行修改，则不会触发Watch事件，这就是我们验证ZK的一个特性“一次性触发”，也就是说设置一次监视，只会对下次操作起一次作用。
        System.out.println("\n3-1. 再次修改节点数据 ");
        zk.setData("/zoo2", "shanhy20160310-ABCD".getBytes(), -1);

        System.out.println("\n4. 查看是否修改成功： ");
        System.out.println(new String(zk.getData("/zoo2", false, null)));

        System.out.println("\n5. 删除节点 ");
        zk.delete("/zoo2", -1);

        System.out.println("\n6. 查看节点是否被删除： ");
        System.out.println(" 节点状态： [" + zk.exists("/zoo2", false) + "]");
        
        
    }
    
    private void simpleOperations() throws KeeperException, InterruptedException{
    	 zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
    			   CreateMode.PERSISTENT); 
		 // 创建一个子目录节点
		 zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),
		   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		 System.out.println(new String(zk.getData("/testRootPath",false,null))); 
		 // 取出子目录节点列表
		 System.out.println(zk.getChildren("/testRootPath",true)); 
		 // 修改子目录节点数据
		 zk.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
		 System.out.println("目录节点状态：["+zk.exists("/testRootPath",true)+"]"); 
		 // 创建另外一个子目录节点
		 zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), 
		   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		 System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
		 // 删除子目录节点
		 zk.delete("/testRootPath/testChildPathTwo",-1); 
		 zk.delete("/testRootPath/testChildPathOne",-1); 
		 // 删除父目录节点
		 zk.delete("/testRootPath",-1); 
    }
    
    
    
    
    

    private void ZKClose() throws InterruptedException {
        zk.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        Test dm = new Test();
        //创建zookeeper实例
        dm.createZKInstance();
        
        //dm.testWatch();
        dm.simpleOperations();
        
        //关闭
        dm.ZKClose();
    }
}
