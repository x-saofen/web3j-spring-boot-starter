package io.web3service.web3j.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author github.com/x-saofen
 */
@SuppressWarnings("all")
@Slf4j
public class Web3jNetworkService extends JsonRpc2_0Web3j {

    private Long defaultTimeOut = 5L;

    public Web3jNetworkService(Web3jService web3jService, Long httpTimeOut) {
        super(web3jService);
        if (Objects.nonNull(httpTimeOut) && httpTimeOut > 0L) {
            this.defaultTimeOut = httpTimeOut;
        }
    }

    private EthChainId chainId;

    @SneakyThrows({ExecutionException.class, InterruptedException.class })
    public Long getChainId() {
        if(Objects.nonNull(chainId)){
            return chainId.getChainId().longValue();
        }
        synchronized(this){
            if(Objects.nonNull(chainId)){
                return chainId.getChainId().longValue();
            }
            chainId = super.ethChainId().sendAsync().get();
            log.info("Init chain ID: {}", chainId);
        }
        return chainId.getChainId().longValue();
    }

    private static class Constant {
        private static final String EMPTY = "";
        private static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";
        private static final String NAME = "name";
        private static final String TOTAL_SUPPLY = "totalSupply";
        private static final String BALANCES_OF = "balanceOf";
        private static final String DECIMALS = "decimals";
        private static final String SYMBOL = "symbol";
        private static final String APPROVAL = "approval";
        private static final String DECREASE_APPROVAL = "decreaseApproval";
        private static final String ALLOWANCE = "allowance";
        private static final String TRANSFER = "transfer";


    }

    /**
     * contract call function
     *
     * @param function        contract function
     * @param contractAddress contract address
     * @return type
     */
    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    private List<Type> callReadFunction(Function function, String contractAddress) {
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(Constant.EMPTY_ADDRESS, contractAddress, data);
        EthCall ethCall = ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
    }


    /**
     * contract simple read function
     *
     * @param contractAddress  contract address
     * @param name             contract function name
     * @param outputParameters Parameters
     * @return type
     */
    public List<Type> simpleReadFunction(String contractAddress, String name, TypeReference<?> outputParameters) {
        return simpleReadFunction(contractAddress, name, new ArrayList<>(), outputParameters);
    }

    /**
     * contract simple read function
     *
     * @param contractAddress  contract address
     * @param name             contract function name
     * @param inputParameters  Parameters
     * @param outputParameters Parameters
     * @return type
     */
    public List<Type> simpleReadFunction(String contractAddress, String name, List<Type> inputParameters, TypeReference<?>... outputParameters) {
        Function function = new Function(
                name, Objects.isNull(inputParameters) ? new ArrayList<>() : inputParameters, Arrays.asList(outputParameters));
        return callReadFunction(function, contractAddress);
    }

    /**
     * Get contract name
     *
     * @param contractAddress contract address
     * @return String name
     */
    public String getContractName(String contractAddress) {
        final List<Type> types = simpleReadFunction(contractAddress, Constant.NAME, new TypeReference<Utf8String>() {
        });
        return CollectionUtils.isEmpty(types) ? Constant.EMPTY : types.get(0).getValue().toString();
    }

    /**
     * Get contract symbol
     *
     * @param contractAddress contract address
     * @return String symbol
     */
    public String getErc20ContractSymbol(String contractAddress) {
        final List<Type> types = simpleReadFunction(contractAddress, Constant.SYMBOL, new TypeReference<Utf8String>() {
        });
        return CollectionUtils.isEmpty(types) ? Constant.EMPTY : types.get(0).getValue().toString();
    }

    /**
     * Get contract decimals
     *
     * @param contractAddress contract address
     * @return BigInteger decimals
     */
    public BigInteger getErc20ContractDecimals(String contractAddress) {
        final List<Type> types = simpleReadFunction(contractAddress, Constant.DECIMALS, new TypeReference<Uint8>() {
        });
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }

    /**
     * Get contract totalSupply
     *
     * @param contractAddress contract address
     * @return BigInteger totalSupply
     */
    public BigInteger getErc20ContractTotalSupply(String contractAddress) {
        final List<Type> types = simpleReadFunction(contractAddress, Constant.TOTAL_SUPPLY, new TypeReference<Uint256>() {
        });
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }


    /**
     * get address balances
     *
     * @param contractAddress contract address
     * @param address         address
     * @return BigInteger balances
     */
    public BigInteger getErc20ContractBalancesOf(String contractAddress, String address) {
        final List<Type> types = simpleReadFunction(contractAddress, Constant.BALANCES_OF, Arrays.asList(new Address(address)), new TypeReference<Uint256>() {
        });
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }

    /**
     * allowance
     *
     * @param owner           owner
     * @param spender         approve Address
     * @param contractAddress contract Address
     * @return amount
     */
    public BigInteger allowance(String owner, String spender, String contractAddress) {
        Function function = new Function(Constant.ALLOWANCE,
                Arrays.<Type>asList(new Address(owner),
                        new Address(spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        List<Type> types = callReadFunction(function, contractAddress);
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }

    /**
     * send transaction
     *
     * @param transaction transaction
     * @param credentials wallet
     * @return EthSendTransaction
     */
    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    public EthSendTransaction sendTransaction(RawTransaction transaction, Credentials credentials, Long chainId) {
        byte[] signMessage = Objects.isNull(chainId) ? TransactionEncoder.signMessage(transaction, credentials) : TransactionEncoder.signMessage(transaction, chainId, credentials);
        String hexValue = Numeric.toHexString(signMessage);
        return super.ethSendRawTransaction(hexValue).sendAsync().get();
    }

    /**
     * send transaction
     *
     * @param credentials wallet
     * @param toAddress   to address
     * @param gasPrice    gas
     * @param gasLimit    gas limit
     * @param nonce       nonce
     * @param value       amount
     * @return EthSendTransaction
     */
    public EthSendTransaction simpleTransfer(Credentials credentials, String toAddress, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce, BigInteger value, Long chainId) {
        return simpleTransfer(credentials, toAddress, gasPrice, gasLimit, nonce, value, null, chainId);
    }

    /**
     * send transaction
     *
     * @param credentials wallet
     * @param toAddress   to address
     * @param gasPrice    gas
     * @param gasLimit    gas limit
     * @param nonce       nonce
     * @param value       amount
     * @param contractAddress    contract address
     * @return EthSendTransaction
     */
    @SneakyThrows(Exception.class)
    public EthSendTransaction simpleTransfer(Credentials credentials, String toAddress, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce, BigInteger value, String contractAddress, Long chainId) {
        RawTransaction rawTransaction;
        if (Objects.nonNull(contractAddress)) {
            Function function = new Function(
                    Constant.TRANSFER,
                    Arrays.asList(new Address(toAddress), new Uint256(value)),
                    Collections.singletonList(new TypeReference<Type>() {
                    }));
            String encodedFunction = FunctionEncoder.encode(function);
            rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);
        } else {
            rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, value, "");
        }
        return sendTransaction(rawTransaction, credentials, chainId);
    }



}
