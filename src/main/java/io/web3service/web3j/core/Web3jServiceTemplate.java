package io.web3service.web3j.core;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Web3jServiceTemplate {

    private Web3jServiceTemplate(){}

    public static Web3jServiceTemplate.Build buildEmpty() {
        return new Web3jServiceTemplate.Build();
    }

    @Getter
    public static class Build{
        private Web3jServiceTemplate web3jServiceTemplate = new Web3jServiceTemplate();

        public Build buildServiceList(List<Web3jNetworkService> web3jService, String network){
            web3jServiceTemplate.appendWeb3jServiceList(web3jService, network);
            return this;
        }
    }

    private Map<String, List<Web3jNetworkService>> networkConnectors = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> atomicLongMap = new HashMap<>();
    private Map<String, Integer> serviceSizeMap = new HashMap<>();

    private synchronized void appendWeb3jServiceList(List<Web3jNetworkService> web3jService, String network) {
        List<Web3jNetworkService> networkServices = networkConnectors.get(network);
        if(Objects.isNull(networkServices)) {
            networkServices = new ArrayList<>(web3jService.size());
        }
        networkServices.addAll(web3jService);
        networkConnectors.put(network, networkServices);
        atomicLongMap.put(network, new AtomicInteger(0));
        serviceSizeMap.put(network, web3jService.size());
    }

    public List<Web3jNetworkService> getNetworkTemplate(String network){
        return networkConnectors.get(network);
    }

    public Web3jNetworkService getRandomTemplate(String network){
        int defaultIndex = 0;
        if(!networkInstanceIsOne(network)) {
            defaultIndex = new Random().nextInt(serviceSizeMap.get(network));
        }
        return networkConnectors.get(network).get(defaultIndex);
    }

    public Web3jNetworkService getNextTemplate(String network){
        return networkConnectors.get(network).get(getServiceIndex(network));
    }

    private Integer getServiceIndex(String network){
        if(networkInstanceIsOne(network)){
            return 0;
        }
        AtomicInteger atomicInteger = atomicLongMap.get(network);
        int current;
        int next;
        do {
            current = atomicInteger.get();
            next = current >= 2147483647?0:current + 1;
        } while(!atomicInteger.compareAndSet(current, next));
        return serviceSizeMap.get(network) % next;
    }

    private boolean networkInstanceIsOne(String network){
        return serviceSizeMap.get(network) == 0;
    }

}
