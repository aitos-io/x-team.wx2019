package io.aitos.hackathon.blockchain;


import io.aitos.hackathon.utils.ByteUtils;
import io.aitos.hackathon.utils.HexUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Data
public class CarServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(CarServiceUtils.class);

    @Value("${W3J_NODE_URL}")
    private String W3J_NODE_URL;

    @Value("${PRIVATE_KEY}")
    private  String PRIVATE_KEY;

    @Value("${CONTRACT_ADDRESS}")
    private  String CONTRACT_ADDRESS;

    @Value("${GAS_PRICE}")
    private  String GAS_PRICE;

    @Value("${GAS_LIMIT}")
    private  String GAS_LIMIT;



    //    private static CarServiceUtils instance;
//    private CarService mCarServiceContract;

    public CarServiceUtils() {
    }

//    public static CarServiceUtils getInstance() {
//        if (instance == null) {
//            synchronized (CarServiceUtils.class) {
//                if (instance == null) {
//                    instance = new CarServiceUtils();
//                }
//            }
//        }
//        return instance;
//    }

    /**
     * 得到自定义合约
     *
     * @return
     */
    public CarService getCarService() {
        String w3j_node_url = W3J_NODE_URL;
        String private_key = PRIVATE_KEY;
        String contract_address = CONTRACT_ADDRESS;
        String gas_price = GAS_PRICE;
        String gas_limit = GAS_LIMIT;
        CarService carService = loadContract(w3j_node_url, private_key, contract_address, gas_price, gas_limit);
        return carService;
    }

    public void updateStatus(CarService contract, String xid, CarStatus status, long time) {
        byte[] hashedXidByte = Hash.sha3(xid.getBytes());
        BigInteger carStatus = BigInteger.valueOf(status.getStatus());
        byte[] updateTime = ByteUtils.longToBytes(time);

        RemoteCall<TransactionReceipt> remoteCall = contract.updateStatus(hashedXidByte, carStatus, updateTime);
        handleReceipt(remoteCall);
    }


    public void updateCarLocation(CarService contract, String xid, long time, String loc) {
        byte[] hashedXidBytes = Hash.sha3(xid.getBytes());
        String curTime = String.valueOf(time);
        String locInfo = curTime + ";" + loc;

        System.out.println("=========locInfo:" + locInfo);
        byte[] hashedLocBytes = Hash.sha3(locInfo.getBytes());

        RemoteCall<TransactionReceipt> remoteCall = contract.updateLoc(hashedXidBytes, hashedLocBytes);
        handleReceipt(remoteCall);
    }

    public List<String> readLocLists(CarService contract, String xid) {
        List<String> carLocInfoList = new ArrayList<>();
        byte[] hashedXidBytes = Hash.sha3(xid.getBytes());
        RemoteCall<List> remoteCall = contract.readLocLists(hashedXidBytes);
        try {
            List<byte[]> remoteReceipt = remoteCall.sendAsync().get();
            for (int i = 0; i < remoteReceipt.size(); i++){
                byte[] byte32 = remoteReceipt.get(i);
                String hashedLocInfo = HexUtils.bytesToHexString(byte32);
                System.out.println("locHashedInfo:" + hashedLocInfo);
                carLocInfoList.add(hashedLocInfo);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return carLocInfoList;
    }

    public void readLocListByIndex(CarService contract, String xid, int index) {
        byte[] hashedXidBytes = Hash.sha3(xid.getBytes());
        BigInteger bigIndex = BigInteger.valueOf(index);

        RemoteCall<byte[]> remoteCall = contract.readLocListByIndex(hashedXidBytes, bigIndex);
        try {
            byte[] remoteReceipt = remoteCall.sendAsync().get();
            String hashedLocInfo = HexUtils.bytesToHexString(remoteReceipt);
            System.out.println("locHashedInfo:" + hashedLocInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void handleReceipt(RemoteCall<TransactionReceipt> remoteCall) {
        try {
            TransactionReceipt remoteCallReceipt = remoteCall.sendAsync().get();
            System.out.println("TransactionHash:" + remoteCallReceipt.getTransactionHash());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据上链失败");
        }
    }

    public CarService loadContract(String w3j_node_url, String private_key, String contract_address, String gas_price, String gas_limit) {

        Web3j web3j = Web3j.build(new HttpService(w3j_node_url));

        Credentials credentials = Credentials.create(private_key);

        ContractGasProvider contractGasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                return new BigInteger(gas_price);
            }

            @Override
            public BigInteger getGasPrice() {
                return new BigInteger(gas_price);
            }

            @Override
            public BigInteger getGasLimit(String contractFunc) {
                return new BigInteger(gas_limit);
            }

            @Override
            public BigInteger getGasLimit() {
                return new BigInteger(gas_limit);
            }
        };

        CarService mCarServiceContract = CarService.load(contract_address, web3j, credentials, contractGasProvider);
        logger.info("加载合约时的地址================>" + contract_address);
        return mCarServiceContract;
    }

}
