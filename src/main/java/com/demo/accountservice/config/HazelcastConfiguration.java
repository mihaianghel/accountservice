package com.demo.accountservice.config;

import com.demo.accountservice.domain.Account;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {

        Config config = new Config();
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        IMap<String, Account> accounts = hazelcastInstance.getMap("accounts");
        accounts.put("01001", new Account("01001", new BigDecimal("2738.59")));
        accounts.put("01002", new Account("01002", new BigDecimal("23.00")));
        accounts.put("01003", new Account("01003", new BigDecimal("0.00")));

        return hazelcastInstance;

    }
}
