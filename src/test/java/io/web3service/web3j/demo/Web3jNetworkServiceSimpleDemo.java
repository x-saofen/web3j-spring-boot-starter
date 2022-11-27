package io.web3service.web3j.demo;


import io.web3service.web3j.contract.BaseErc20Contract;
import io.web3service.web3j.contract.ContractClient;
import io.web3service.web3j.core.Web3jNetworkService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;


/**
 * Test
 *
 * @author github.com/x-saofen
 */
@Slf4j
public class Web3jNetworkServiceSimpleDemo {

    static final Web3jNetworkService BSC_NETWORK_SERVICE = new Web3jNetworkService(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"), 5L);
    static String BSC_CONTRACT = "0x384157Ad1CD1D0a79b3F555b3365de62707540ea";
    static final Web3jNetworkService POLYGON_NETWORK_SERVICE = new Web3jNetworkService(new HttpService("https://matic-mumbai.chainstacklabs.com"), 5L);
    static String POLYGON_CONTRACT = "0x6A041A5bBbF5C0B230270B982B17faBa29713773";
    static String TO_ADDRESS = "0xC86F53caDe21C36933D83Bf94ce4E0A55BB5D47C";
    static Credentials CREDENTIALS = Credentials.create("you private key");


    public static void main(String[] args) throws Exception{
    }

    private static void bscReadFunctions() {
        log.info("#### Chain Id: {}", BSC_NETWORK_SERVICE.getChainId());
        log.info("#### ERC20 Contract name: {}", BSC_NETWORK_SERVICE.getContractName(BSC_CONTRACT));
        log.info("#### ERC20 Contract symbol: {}", BSC_NETWORK_SERVICE.getErc20ContractSymbol(BSC_CONTRACT));
        log.info("#### ERC20 Contract decimals: {}", BSC_NETWORK_SERVICE.getErc20ContractDecimals(BSC_CONTRACT));
        log.info("#### ERC20 Contract totalSupply: {}", BSC_NETWORK_SERVICE.getErc20ContractTotalSupply(BSC_CONTRACT));
        log.info("#### ERC20 Contract balances: {}", BSC_NETWORK_SERVICE.getErc20ContractBalancesOf(BSC_CONTRACT, TO_ADDRESS));
    }

    @SneakyThrows
    private static void bscLoadContract() {
        BaseErc20Contract erc20Contract = ContractClient.loadContract(BSC_NETWORK_SERVICE, BSC_CONTRACT, BaseErc20Contract.class);
        log.info("#### ERC20 Contract name: {}", erc20Contract.name().send());
        log.info("#### ERC20 Contract symbol: {}", erc20Contract.symbol().send());
        log.info("#### ERC20 Contract decimals: {}", erc20Contract.decimals().send());
        log.info("#### ERC20 Contract totalSupply: {}", erc20Contract.totalSupply().send());
        log.info("#### ERC20 Contract balances: {}", erc20Contract.balanceOf(TO_ADDRESS).send());
    }


    private static void polygonReadFunctions() {
        log.info("#### Chain Id: {}", POLYGON_NETWORK_SERVICE.getChainId());
        log.info("#### ERC20 Contract name: {}", POLYGON_NETWORK_SERVICE.getContractName(POLYGON_CONTRACT));
        log.info("#### ERC20 Contract symbol: {}", POLYGON_NETWORK_SERVICE.getErc20ContractSymbol(POLYGON_CONTRACT));
        log.info("#### ERC20 Contract decimals: {}", POLYGON_NETWORK_SERVICE.getErc20ContractDecimals(POLYGON_CONTRACT));
        log.info("#### ERC20 Contract totalSupply: {}", POLYGON_NETWORK_SERVICE.getErc20ContractTotalSupply(POLYGON_CONTRACT));
        log.info("#### ERC20 Contract balances: {}", POLYGON_NETWORK_SERVICE.getErc20ContractBalancesOf(POLYGON_CONTRACT, TO_ADDRESS));
    }


    @SneakyThrows
    private static void checkTxHash(String hash, Web3jNetworkService web3j) {
        EthGetTransactionReceipt transactionReceipt = null;
        while (Objects.isNull(transactionReceipt) || Objects.isNull(transactionReceipt.getResult())) {
            transactionReceipt = web3j.ethGetTransactionReceipt(hash).sendAsync().get();
            Thread.sleep(1000L);
        }
        log.info("Transfer {}", transactionReceipt.getResult().isStatusOK() ? "succeed" : "failed");
    }

    @SneakyThrows
    private static void bscSimpleTransfer() {
        BigInteger gasPrice = BSC_NETWORK_SERVICE.ethGasPrice().sendAsync().get().getGasPrice();
        BigInteger nonce = BSC_NETWORK_SERVICE.ethGetTransactionCount(CREDENTIALS.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getTransactionCount();
        BigInteger value = Convert.toWei("0.1", Convert.Unit.ETHER).toBigInteger();
        EthSendTransaction ethSendTransaction = BSC_NETWORK_SERVICE.simpleTransfer(CREDENTIALS, TO_ADDRESS, gasPrice, BigInteger.valueOf(21000), nonce, value, null);
        log.info("Tx hash: {}", ethSendTransaction.getTransactionHash());
        if (Objects.isNull(ethSendTransaction.getTransactionHash())) {
            throw new RuntimeException("Transfer failed: " + ethSendTransaction.getError().getMessage());
        }
        checkTxHash(ethSendTransaction.getTransactionHash(), BSC_NETWORK_SERVICE);
    }

    @SneakyThrows
    private static void bscSimpleERC20Transfer() {
        BigInteger gasPrice = BSC_NETWORK_SERVICE.ethGasPrice().sendAsync().get().getGasPrice();
        BigInteger nonce = BSC_NETWORK_SERVICE.ethGetTransactionCount(CREDENTIALS.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getTransactionCount();
        BigInteger value = Convert.toWei("10000", Convert.Unit.ETHER).toBigInteger();
        EthSendTransaction ethSendTransaction = BSC_NETWORK_SERVICE.simpleTransfer(CREDENTIALS, TO_ADDRESS, gasPrice, BigInteger.valueOf(64000), nonce, value, BSC_CONTRACT, null);
        log.info("Tx hash: {}", ethSendTransaction.getTransactionHash());
        if (Objects.isNull(ethSendTransaction.getTransactionHash())) {
            throw new RuntimeException("Transfer failed: " + ethSendTransaction.getError().getMessage());
        }
        checkTxHash(ethSendTransaction.getTransactionHash(), BSC_NETWORK_SERVICE);
    }


    @SneakyThrows
    private static void polygonSimpleTransfer() {
        BigInteger gasPrice = POLYGON_NETWORK_SERVICE.ethGasPrice().sendAsync().get().getGasPrice();
        BigInteger nonce = POLYGON_NETWORK_SERVICE.ethGetTransactionCount(CREDENTIALS.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getTransactionCount();
        BigInteger value = Convert.toWei("0.1", Convert.Unit.ETHER).toBigInteger();
        EthSendTransaction ethSendTransaction = POLYGON_NETWORK_SERVICE.simpleTransfer(CREDENTIALS, TO_ADDRESS, gasPrice, BigInteger.valueOf(21000), nonce, value, POLYGON_NETWORK_SERVICE.getChainId());
        log.info("Tx hash: {}", ethSendTransaction.getTransactionHash());
        if (Objects.isNull(ethSendTransaction.getTransactionHash())) {
            throw new RuntimeException("Transfer failed: " + ethSendTransaction.getError().getMessage());
        }
        checkTxHash(ethSendTransaction.getTransactionHash(), POLYGON_NETWORK_SERVICE);
    }

    @SneakyThrows
    private static void polygonSimpleERC20Transfer() {
        BigInteger gasPrice = POLYGON_NETWORK_SERVICE.ethGasPrice().sendAsync().get().getGasPrice();
        BigInteger nonce = POLYGON_NETWORK_SERVICE.ethGetTransactionCount(CREDENTIALS.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getTransactionCount();
        BigInteger value = Convert.toWei("10000", Convert.Unit.ETHER).toBigInteger();
        EthSendTransaction ethSendTransaction = POLYGON_NETWORK_SERVICE.simpleTransfer(CREDENTIALS, TO_ADDRESS, gasPrice, BigInteger.valueOf(64000), nonce, value, POLYGON_CONTRACT, POLYGON_NETWORK_SERVICE.getChainId());
        log.info("Tx hash: {}", ethSendTransaction.getTransactionHash());
        if (Objects.isNull(ethSendTransaction.getTransactionHash())) {
            throw new RuntimeException("Transfer failed: " + ethSendTransaction.getError().getMessage());
        }
        checkTxHash(ethSendTransaction.getTransactionHash(), POLYGON_NETWORK_SERVICE);
    }

    @SneakyThrows
    private static void simpleEIP1559Transfer() {
        TransactionReceipt receipt = POLYGON_NETWORK_SERVICE.simpleEIP1559Transfer(CREDENTIALS, TO_ADDRESS, new BigDecimal("1"), Convert.Unit.ETHER);
        log.info(receipt.getTransactionHash());
        log.info("Tx : {}", receipt.isStatusOK() ? "succeed" : "failed ");
    }

    @SneakyThrows
    private static void polygonSimpleErc20EIP1559Transfer() {
        BigInteger nonce = POLYGON_NETWORK_SERVICE.ethGetTransactionCount(CREDENTIALS.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getTransactionCount();
        EthSendTransaction ethSendTransaction = POLYGON_NETWORK_SERVICE.simpleEIP1559Transfer(POLYGON_CONTRACT, CREDENTIALS, TO_ADDRESS, BigDecimal.valueOf(10000), Convert.Unit.ETHER, BigInteger.valueOf(64000), nonce);
        log.info("Tx hash: {}", ethSendTransaction.getTransactionHash());
        if (Objects.isNull(ethSendTransaction.getTransactionHash())) {
            throw new RuntimeException("Transfer failed: " + ethSendTransaction.getError().getMessage());
        }
        checkTxHash(ethSendTransaction.getTransactionHash(), POLYGON_NETWORK_SERVICE);
    }


}
