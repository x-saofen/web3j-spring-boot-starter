package io.web3service.web3j.contract;

import io.web3service.web3j.core.Web3jNetworkService;
import lombok.SneakyThrows;
import org.springframework.util.Assert;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
  * @author   github.com/x-saofen
  */
public class ContractClient {

    @SneakyThrows({NoSuchMethodException.class, InvocationTargetException.class , InstantiationException.class, IllegalAccessException.class})
    public static <T> T loadContract(Web3jNetworkService web3j, String contractAddress, Class<? extends Contract> type)  {
        Optional<String> contractBinary = web3j.getContractBinary(contractAddress);
        Assert.isTrue(contractBinary.isPresent(), "Failed to get "+ contractAddress +" contract bytecode.");
        Constructor<? extends Contract> constructor = type.getConstructor(String.class, String.class, Web3j.class, TransactionManager.class, ContractGasProvider.class);
        return (T) constructor.newInstance(contractBinary.get(), contractAddress, web3j, new ReadonlyTransactionManager(web3j, contractAddress), new DefaultGasProvider());
    }



}
