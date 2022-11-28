# web3j-spring-boot-starter

>Web3j-core: 4.9.5
>
>SpringBoot: 2.7.5



[SimpleDemo](https://github.com/x-saofen/web3j-spring-boot-starter/tree/master/src/test/java/io/web3service/web3j/demo/Web3jNetworkServiceSimpleDemo.java)



## V1.0.0

### ERC20
1. 合约名称 
2. 符号
3. 精度
4. 发行数量
5. 地址余额
6. 简单转账 适用BSC,  以太坊请用 EIP-1559 

## V1.0.1
  ```
  修改EIP-1559模版转账最终费用为 BaseFee + MaxPriorityFeePerGas
  ```

## V1.0.2
 ```
 新增根据合约地址读取ERC20合约模版
 ```


## Getting started

pom dependency

```xml
<dependency>
    <groupId>io.web3service</groupId>
    <artifactId>web3j-spring-boot-starter</artifactId>
    <version>${latestVersion}</version>
</dependency>
<dependency>
   <groupId>com.squareup.okhttp3</groupId>
   <artifactId>okhttp</artifactId>
   <version>4.3.1</version>
</dependency>
```
application.yml

```yaml
web3service:
  web3j:
    http-timeout-seconds: 30
    network:
      # 链网络: [url,url]
      polygon-testnet: https://matic-mumbai.chainstacklabs.com
      bsc-testnet: https://data-seed-prebsc-1-s1.binance.org:8545/
```

java

```java
@Autowired
private Web3jServiceTemplate web3jServiceTemplate;

public Long getChainId(){
  Web3jNetworkService service = web3jServiceTemplate.getNextTemplate(network);
  return service.getChainId();
}

```

