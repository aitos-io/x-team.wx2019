package io.aitos.hackathon.blockchain;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes8;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class CarService extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060008054600160a060020a031916331790556109f3806100326000396000f30060806040526004361061008d5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166317548a6f811461009257806322f7b5e0146100f8578063611fcbe9146101605780636c5dc748146101af57806383197ef0146101ca5780639d8f4a38146101e1578063b7e677c514610242578063fafdd6531461026f575b600080fd5b34801561009e57600080fd5b506100aa600435610287565b604051808660028111156100ba57fe5b60ff168152600160c060020a031995861660208201529385166040808601919091529290941660608401526080830152519081900360a00192509050f35b34801561010457600080fd5b5061011060043561030c565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561014c578181015183820152602001610134565b505050509050019250505060405180910390f35b34801561016c57600080fd5b5061018b60043560ff60243516600160c060020a031960443516610396565b6040518082600281111561019b57fe5b60ff16815260200191505060405180910390f35b3480156101bb57600080fd5b5061018b6004356024356107b2565b3480156101d657600080fd5b506101df610802565b005b3480156101ed57600080fd5b506101f960043561083f565b6040518085600281111561020957fe5b60ff168152600160c060020a03199485166020820152928416604080850191909152919093166060830152519081900360800192509050f35b34801561024e57600080fd5b5061025d6004356024356108ba565b60408051918252519081900360200190f35b34801561027b57600080fd5b5061025d600435610927565b6000818152600160208190526040822060020154829182918291829160ff1615151415610303575050506000838152600160208190526040909120805491015460ff8216935060c060020a6101008304810293506901000000000000000000830481029271010000000000000000000000000000000000900402905b91939590929450565b60008181526001602081905260409091206002015460609160ff90911615151415610391576000828152600160208181526040928390209091018054835181840281018401909452808452909183018282801561038957602002820191906000526020600020905b81548152600190910190602001808311610374575b505050505090505b919050565b60006060818460028111156103a757fe5b141561056c5760008581526001602081905260409091206002015460ff16151514156104295760008581526001602081905260409091208054869260ff19909116908360028111156103f557fe5b02179055506000858152600160205260409020805468ffffffffffffffff00191661010060c060020a860402179055610567565b60c06040519081016040528085600281111561044157fe5b8152600160c060020a03198516602080830191909152600060408084018290526060840182905260808401869052600160a090940184905289825291839052208251815491929091839160ff199091169083600281111561049e57fe5b021790555060208281015182546040850151606086015168ffffffffffffffff001990921661010060c060020a94859004021770ffffffffffffffff0000000000000000001916690100000000000000000091849004919091021778ffffffffffffffff000000000000000000000000000000000019167101000000000000000000000000000000000092909104919091021782556080830151805161054a926001850192019061095d565b5060a091909101516002909101805460ff19169115159190911790555b6107aa565b600184600281111561057a57fe5b14156106825760008581526001602081905260409091206002015460ff161515141561060c5760008581526001602081905260409091208054869260ff19909116908360028111156105c857fe5b02179055506000858152600160205260409020805470ffffffffffffffff0000000000000000001916690100000000000000000060c060020a860402179055610567565b60c06040519081016040528085600281111561062457fe5b815260006020808301829052600160c060020a031987166040808501919091526060840183905260808401869052600160a09094018490528983529083905290208251815491929091839160ff199091169083600281111561049e57fe5b600284600281111561069057fe5b14156107a55760008581526001602081905260409091206002015460ff16151514156107325760008581526001602081905260409091208054869260ff19909116908360028111156106de57fe5b02179055506000858152600160205260409020805478ffffffffffffffff000000000000000000000000000000000019167101000000000000000000000000000000000060c060020a860402179055610567565b60c06040519081016040528085600281111561074a57fe5b8152600060208083018290526040808401839052600160c060020a03198816606085015260808401869052600160a09094018490528983529083905290208251815491929091839160ff199091169083600281111561049e57fe5b600191505b509392505050565b600082815260016020819052604082206002015460ff16151514156107f857506000828152600160208181526040832082018054928301815583528220018290556107fc565b5060025b92915050565b60005473ffffffffffffffffffffffffffffffffffffffff1633141561083d5760005473ffffffffffffffffffffffffffffffffffffffff16ff5b565b600081815260016020819052604082206002015482918291829160ff909116151514156108b35750505060008281526001602052604090205460ff8116915060c060020a61010082048102916901000000000000000000810482029171010000000000000000000000000000000000909104025b9193509193565b600082815260016020819052604082200154821080156108f1575060008381526001602081905260409091206002015460ff161515145b156107fc5760008381526001602081905260409091200180548390811061091457fe5b9060005260206000200154905092915050565b600081815260016020819052604082206002015460ff161515141561039157506000908152600160208190526040909120015490565b82805482825590600052602060002090810192821561099a579160200282015b8281111561099a578251825560209092019160019091019061097d565b506109a69291506109aa565b5090565b6109c491905b808211156109a657600081556001016109b0565b905600a165627a7a72305820bafc252b662fc37b93ca955bcf7da38daaf0a2dd75387ce7f1147d6a35485b270029";

    public static final String FUNC_READSTATUSTIMESLENGTH = "readStatusTimesLength";

    public static final String FUNC_READLOCLISTS = "readLocLists";

    public static final String FUNC_UPDATESTATUS = "updateStatus";

    public static final String FUNC_UPDATELOC = "updateLoc";

    public static final String FUNC_DESTROY = "destroy";

    public static final String FUNC_READSTATUSANDTIMES = "readStatusAndTimes";

    public static final String FUNC_READLOCLISTBYINDEX = "readLocListByIndex";

    public static final String FUNC_READLOCLISTLENGTH = "readLocListLength";

    @Deprecated
    protected CarService(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CarService(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CarService(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CarService(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Tuple5<BigInteger, byte[], byte[], byte[], BigInteger>> readStatusTimesLength(byte[] xID) {
        final Function function = new Function(FUNC_READSTATUSTIMESLENGTH,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Bytes8>() {}, new TypeReference<Bytes8>() {}, new TypeReference<Bytes8>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple5<BigInteger, byte[], byte[], byte[], BigInteger>>(
                new Callable<Tuple5<BigInteger, byte[], byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple5<BigInteger, byte[], byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<BigInteger, byte[], byte[], byte[], BigInteger>(
                                (BigInteger) results.get(0).getValue(),
                                (byte[]) results.get(1).getValue(),
                                (byte[]) results.get(2).getValue(),
                                (byte[]) results.get(3).getValue(),
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteCall<List> readLocLists(byte[] xID) {
        final Function function = new Function(FUNC_READLOCLISTS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<TransactionReceipt> updateStatus(byte[] xID, BigInteger status, byte[] time) {
        final Function function = new Function(
                FUNC_UPDATESTATUS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID),
                        new org.web3j.abi.datatypes.generated.Uint8(status),
                        new org.web3j.abi.datatypes.generated.Bytes8(time)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateLoc(byte[] xID, byte[] newLoc) {
        final Function function = new Function(
                FUNC_UPDATELOC,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID),
                        new org.web3j.abi.datatypes.generated.Bytes32(newLoc)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> destroy() {
        final Function function = new Function(
                FUNC_DESTROY,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<BigInteger, byte[], byte[], byte[]>> readStatusAndTimes(byte[] xID) {
        final Function function = new Function(FUNC_READSTATUSANDTIMES,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Bytes8>() {}, new TypeReference<Bytes8>() {}, new TypeReference<Bytes8>() {}));
        return new RemoteCall<Tuple4<BigInteger, byte[], byte[], byte[]>>(
                new Callable<Tuple4<BigInteger, byte[], byte[], byte[]>>() {
                    @Override
                    public Tuple4<BigInteger, byte[], byte[], byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, byte[], byte[], byte[]>(
                                (BigInteger) results.get(0).getValue(),
                                (byte[]) results.get(1).getValue(),
                                (byte[]) results.get(2).getValue(),
                                (byte[]) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<byte[]> readLocListByIndex(byte[] xID, BigInteger index) {
        final Function function = new Function(FUNC_READLOCLISTBYINDEX,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID),
                        new org.web3j.abi.datatypes.generated.Uint256(index)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> readLocListLength(byte[] xID) {
        final Function function = new Function(FUNC_READLOCLISTLENGTH,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(xID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static CarService load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CarService(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CarService load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CarService(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CarService load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CarService(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CarService load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CarService(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CarService> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CarService.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<CarService> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CarService.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CarService> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CarService.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CarService> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CarService.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
