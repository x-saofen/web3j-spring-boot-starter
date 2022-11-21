package io.web3service.web3j.autoconfigure;

import io.web3service.web3j.core.Web3jNetworkService;
import io.web3service.web3j.core.Web3jServiceFactory;
import io.web3service.web3j.core.Web3jServiceTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.Web3j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author github.com/x-saofen
 */
@Slf4j
@AllArgsConstructor
@Configuration
@ConditionalOnClass(Web3j.class)
@EnableConfigurationProperties(Web3jProperties.class)
public class Web3jAutoConfiguration {

    private Web3jProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public Web3jServiceTemplate web3j() {
        Map<String, List<String>> propertiesNetwork = properties.getNetwork();
        Assert.isTrue(!CollectionUtils.isEmpty(propertiesNetwork), "Web3j client address must not be null");
        Set<String> networks = propertiesNetwork.keySet();
        Web3jServiceTemplate.Build build = Web3jServiceTemplate.buildEmpty();
        networks.forEach( network -> {
            Exception ex = null;
            try{
                List<String> networkAddress = propertiesNetwork.get(network);
                List<Web3jNetworkService> web3jService = Web3jServiceFactory.buildServiceList(networkAddress, properties.getHttpTimeoutSeconds());
                log.info("Building service {} for endpoint: {}", network,  networkAddress);
                build.buildServiceList(web3jService, network);
            }catch (Exception e){
                ex = e;
            }
            if(Objects.nonNull(ex)){
                ex.printStackTrace();
                System.exit(-1);
            }
        });
        return build.getWeb3jServiceTemplate();
    }


}
