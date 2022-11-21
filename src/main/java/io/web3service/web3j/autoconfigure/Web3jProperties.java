package io.web3service.web3j.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * web3j property container.
 */
@ConfigurationProperties(prefix = "web3service.web3j")
@Data
public class Web3jProperties {

    /**
     * Http timeout
     */
    private Long httpTimeoutSeconds;

    /**
     * Blockchain network, example=>  ethereum: https://rpc.ankr.com/eth, https://rpc.flashbots.net
     */
    private Map<String, List<String>> network;
}
