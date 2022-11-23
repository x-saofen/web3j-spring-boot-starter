package io.web3service.web3j.contract;

import org.web3j.crypto.Credentials;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
  * @author   github.com/x-saofen
  */
public class BaseErc20Contract extends Contract {

    public BaseErc20Contract(String contractBinary, String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, web3j, credentials, gasProvider);
    }

    public BaseErc20Contract(String contractBinary, String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(new EnsResolver(web3j), contractBinary, contractAddress, web3j, transactionManager, gasProvider);
    }



}
