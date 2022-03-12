package de.notion.messaging.redis.config;

import de.notion.messaging.MessagingService;
import de.notion.messaging.config.Connection;
import de.notion.messaging.redis.RedisMessageEndPoint;
import de.notion.messaging.sender.MessageEndPoint;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.concurrent.TimeUnit;

public class RedisConnection implements Connection {

    private final boolean clusterMode;
    private final String[] addresses;
    private final String password;

    private RedissonClient redissonClient;
    private boolean connected;

    public RedisConnection(boolean useCluster, String password, String... addresses) {
        this.clusterMode = useCluster;
        this.addresses = addresses;
        this.password = password;
        this.connected = false;
    }

    @Override
    public void load() {
        if (isLoaded()) return;
        if (addresses.length == 0)
            throw new IllegalArgumentException("Address Array empty");
        Config config = new Config();
        if (clusterMode) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.addNodeAddress(addresses);

            if (!password.isEmpty())
                clusterServersConfig.addNodeAddress(addresses).setPassword(password);
            else
                clusterServersConfig.addNodeAddress(addresses);
        } else {
            String address = addresses[0];
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setSubscriptionsPerConnection(30);

            if (!password.isEmpty())
                singleServerConfig.setAddress(address).setPassword(password);
            else
                singleServerConfig.setAddress(address);
        }
        config.setNettyThreads(4);
        config.setThreads(4);
        this.redissonClient = Redisson.create(config);
        connected = true;
    }

    @Override
    public void shutdown() {
        redissonClient.shutdown(0, 2, TimeUnit.SECONDS);
        connected = false;
    }

    @Override
    public boolean isLoaded() {
        return connected;
    }

    @Override
    public MessageEndPoint construct(MessagingService messagingService) {
        return new RedisMessageEndPoint(messagingService, redissonClient);
    }
}
