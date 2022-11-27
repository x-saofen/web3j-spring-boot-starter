package io.web3service.web3j.core;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author github.com/x-saofen
 */
@Slf4j
public class Web3jServiceFactory {


    private static class Constant{
        public static final String EMPTY = "";
        public static final String HTTP = "http";
        public static final String OS_NAME = "os.name";

        public static final String WIN = "win";
    }

    /**
     *  build Web3jService
     * @param list      clientAddress
     * @param timeout   http timeout
     * @return  Web3jNetworkService list
     */
    public static List<Web3jNetworkService> buildServiceList(List<String> list, Long timeout){
        Assert.isTrue(!CollectionUtils.isEmpty(list), "Web3j client address must not be null");
        List<Web3jNetworkService> result = new ArrayList<>(list.size());
        list.forEach( clientAddress -> {
            try {
                result.add(new Web3jNetworkService(buildService(clientAddress, timeout), timeout));
            } catch (Exception e) {
                log.error("Web3j chain ID query err.", e);
            }
        });
        return result;
    }

    /**
     * copy www.web3labs.com
     * @param clientAddress network address
     * @param timeout   timeout
     * @see <a href="https://github.com/web3j/web3j-spring-boot-starter/blob/master/src/main/java/org/web3j/spring/autoconfigure/Web3jAutoConfiguration.java">Source code address</a>
     * @return  Web3jService
     */
    public static Web3jService buildService(String clientAddress, Long timeout){
        Web3jService web3jService;
        if (clientAddress == null || clientAddress.equals(Constant.EMPTY)) {
            web3jService = new HttpService(createOkHttpClient(timeout));
        } else if (clientAddress.startsWith(Constant.HTTP)) {
            web3jService = new HttpService(clientAddress, createOkHttpClient(timeout), false);
        } else if (System.getProperty(Constant.OS_NAME).toLowerCase().startsWith(Constant.WIN)) {
            web3jService = new WindowsIpcService(clientAddress);
        } else {
            web3jService = new UnixIpcService(clientAddress);
        }
        return web3jService;
    }

    /**
     * copy www.web3labs.com
     * @see <a href="https://github.com/web3j/web3j-spring-boot-starter/blob/master/src/main/java/org/web3j/spring/autoconfigure/Web3jAutoConfiguration.java">Source code address</a>
     * @return  OkHttpClient
     */
    private static OkHttpClient createOkHttpClient(Long timeout) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configureLogging(builder);
        configureTimeouts(builder, timeout);
        return builder.build();
    }

    /**
     * copy www.web3labs.com
     * @see <a href="https://github.com/web3j/web3j-spring-boot-starter/blob/master/src/main/java/org/web3j/spring/autoconfigure/Web3jAutoConfiguration.java">Source code address</a>
     */
    private static void configureTimeouts(OkHttpClient.Builder builder, Long timeout) {
        if (timeout != null) {
            builder.connectTimeout(timeout, TimeUnit.SECONDS);
            // Sets the socket timeout too
            builder.readTimeout(timeout, TimeUnit.SECONDS);
            builder.writeTimeout(timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * copy www.web3labs.com
     * @see <a href="https://github.com/web3j/web3j-spring-boot-starter/blob/master/src/main/java/org/web3j/spring/autoconfigure/Web3jAutoConfiguration.java">Source code address</a>
     */
    private static void configureLogging(OkHttpClient.Builder builder) {
        if (log.isDebugEnabled()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(log::debug);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }

}
