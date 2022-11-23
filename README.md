# web3j-spring-boot-starter

>Web3j-core: 5.0.0
>
>SpringBoot: 2.7.5



## V1.0.0

### ERC20
1. 合约名称 
2. 符号
3. 精度
4. 发行数量
5. 地址余额
6. 简单转账 适用BSC,  以太坊请用 EIP-1559 

## Getting started



```xml
<dependency>
    <groupId>io.web3service</groupId>
    <artifactId>web3j-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
   <groupId>com.squareup.okhttp3</groupId>
   <artifactId>okhttp</artifactId>
   <version>4.3.1</version>
</dependency>
```

```yaml
# application.yml
web3service:
  web3j:
    http-timeout-seconds: 30
    network:
    	# 链网络: [url,url]
      polygon-testnet: https://matic-mumbai.chainstacklabs.com
```
