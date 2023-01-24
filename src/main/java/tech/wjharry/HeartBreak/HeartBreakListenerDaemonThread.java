package tech.wjharry.HeartBreak;

import tech.wjharry.Common.MemberRole;
import tech.wjharry.Common.Singleton;

import java.util.Random;

/**
 * 心跳健康监控线程，一旦心跳超时，就认为Leader已经失联，需要发起选举
 */
public class HeartBreakListenerDaemonThread extends Thread {
    @Override
    public void run() {
        while (true) {
            if (Singleton.memberContext.getRole() == MemberRole.LEADER) {
                continue;
            }

            // 在5~10秒内随机选取作为超时时间，避免所有成员同时超时，出现选票反复被瓜分的情况
            Random random = new Random();
            long time = (int) (random.nextDouble() * 5000) + 5000;
            if (System.currentTimeMillis() - Singleton.memberContext.getLastHeartBreak() > time) {
                System.out.println(System.currentTimeMillis() + " " + Singleton.memberContext.getLastHeartBreak());
                Singleton.electionService.start();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
