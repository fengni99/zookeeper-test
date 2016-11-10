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
package com.discovery.darchrow.lock;

import com.discovery.darchrow.lock.ConcurrentTest.ConcurrentTask;

public class ZkTest {
	public static void main(String[] args) {
		/*Runnable task1 = new Runnable(){
			public void run() {
				DistributedLock lock = null;
				try {
					lock = new DistributedLock("127.0.0.1:2182","test1");
					//lock = new DistributedLock("127.0.0.1:2182","test2");
					lock.lock();
					Thread.sleep(3000);
					System.out.println("===Thread " + Thread.currentThread().getId() + " running");
				} catch (Exception e) {
					e.printStackTrace();
				} 
				finally {
					if(lock != null)
						lock.unlock();
				}
				
			}
			
		};
		new Thread(task1).start();*/
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/
		ConcurrentTask[] tasks = new ConcurrentTask[10];
		for(int i=0;i<tasks.length;i++){
			ConcurrentTask task3 = new ConcurrentTask(){
				public void run() {
					DistributedLock lock = null;
					try {
						lock = new DistributedLock("127.0.0.1:2183","test2");
						lock.lock();
						System.out.println("Thread " + Thread.currentThread().getId() + " running");
					} catch (Exception e) {
						e.printStackTrace();
					} 
					finally {
						lock.unlock();
					}
					
				}
			};
			tasks[i] = task3;
		}
		new ConcurrentTest(tasks);
	}
}