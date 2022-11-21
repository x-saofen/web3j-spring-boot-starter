package io.web3service.web3j.core;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;

/**
 *
 * @author github.com/x-saofen
 */
public class Web3jNetworkService extends JsonRpc2_0Web3j {

    public Web3jNetworkService(Web3jService web3jService) {
        super(web3jService);
    }


}
