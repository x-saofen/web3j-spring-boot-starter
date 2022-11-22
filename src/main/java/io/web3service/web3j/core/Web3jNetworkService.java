package io.web3service.web3j.core;

import lombok.SneakyThrows;
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
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author github.com/x-saofen
 */
@SuppressWarnings("all")
public class Web3jNetworkService extends JsonRpc2_0Web3j {

    private Long defaultTimeOut = 5L;

    public Web3jNetworkService(Web3jService web3jService, Long httpTimeOut) {
        super(web3jService);
        if(Objects.nonNull(httpTimeOut) && httpTimeOut > 0L){
            this.defaultTimeOut = httpTimeOut;
        }
    }


    private static class Constant{
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


    }

    /**
     * contract call function
     * @param function  contract function
     * @param contract  contract address
     * @return  type
     */
    @SneakyThrows({ExecutionException.class, InterruptedException.class })
    private List<Type> callReadFunction(Function function, String contract) {
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(Constant.EMPTY_ADDRESS, contract, data);
        EthCall ethCall = ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        return FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
    }


    /**
     * contract simple read function
     * @param contract     contract address
     * @param name          contract function name
     * @param outputParameters Parameters
     * @return type
     */
    public List<Type> simpleReadFunction(String contract, String name, TypeReference<?> outputParameters) {
        return simpleReadFunction(contract, name, new ArrayList<>(), outputParameters);
    }

    /**
     * contract simple read function
     * @param contract     contract address
     * @param name          contract function name
     * @param inputParameters   Parameters
     * @param outputParameters Parameters
     * @return type
     */
    public List<Type> simpleReadFunction(String contract, String name, List<Type> inputParameters, TypeReference<?>... outputParameters) {
        Function function = new Function(
                name, Objects.isNull(inputParameters) ? new ArrayList<>() : inputParameters, Arrays.asList(outputParameters));
        return callReadFunction(function, contract);
    }

    /**
     * Get contract name
     * @param contract     contract address
     * @return  String name
     */
    public String getContractName(String contract){
        final List<Type> types = simpleReadFunction(contract, Constant.NAME, new TypeReference<Utf8String>() {
        });
        return CollectionUtils.isEmpty(types) ? Constant.EMPTY : types.get(0).getValue().toString();
    }

    /**
     * Get contract symbol
     * @param contract     contract address
     * @return  String symbol
     */
    public String getErc20ContractSymbol(String contract){
        final List<Type> types = simpleReadFunction(contract, Constant.SYMBOL, new TypeReference<Utf8String>() {
        });
        return CollectionUtils.isEmpty(types) ? Constant.EMPTY : types.get(0).getValue().toString();
    }

    /**
     * Get contract decimals
     * @param contract     contract address
     * @return  BigInteger decimals
     */
    public BigInteger getErc20ContractDecimals(String contract){
        final List<Type> types = simpleReadFunction(contract, Constant.DECIMALS, new TypeReference<Uint8>() {
        });
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }

    /**
     * Get contract totalSupply
     * @param contract     contract address
     * @return  BigInteger totalSupply
     */
    public BigInteger getErc20ContractTotalSupply(String contract){
        final List<Type> types = simpleReadFunction(contract, Constant.TOTAL_SUPPLY, new TypeReference<Uint256>() {
        });
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }


    /**
     * get address balances
     * @param contract      contract address
     * @param address       address
     * @return  BigInteger balances
     */
    public BigInteger getErc20ContractBalancesOf(String contract, String address){
        final List<Type> types = simpleReadFunction(contract, Constant.BALANCES_OF, Arrays.asList(new Address(address)), new TypeReference<Uint256>() {
        });
        return CollectionUtils.isEmpty(types) ? BigInteger.ZERO : new BigInteger(types.get(0).getValue().toString());
    }



}
