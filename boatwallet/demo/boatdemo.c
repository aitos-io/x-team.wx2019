#include "wallet/boatwallet.h"

// Case declaration

BOAT_RESULT CaseCarLocMain(void);


BOAT_RESULT SetCommonParam(CHAR *node_url_ptr)
{
    UINT8 priv_key_array[32];
    TxFieldMax32B gas_price;
    TxFieldMax32B gas_limit;
    BOAT_RESULT result;

    // Step 1: Set Wallet Parameters
    
    // Set EIP-155 Compatibility
    result = BoatWalletSetEIP155Comp(BOAT_FALSE);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;


    // Set Node URL
    result = BoatWalletSetNodeUrl(node_url_ptr);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;


    // Set Chain ID (If EIP-155 compatibility is FALSE, Chain ID is ignored)
    result = BoatWalletSetChainId(5777);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;


    // Set Private Key
    // PRIVATE KEY MUST BE SET BEFORE SETTING TRANSACTION PARAMETERS

    UtilityHex2Bin(
                    priv_key_array,
                    32,
                    "0x829e924fdf021ba3dbbc4225edfece9aca04b929d6e75613329ca6f1d31c0bb4",
                    TRIMBIN_TRIM_NO,
                    BOAT_FALSE
                  );

    result = BoatWalletSetPrivkey(priv_key_array);

    // Destroy private key in local variable
    memset(priv_key_array, 0x00, 32);

    if( result != BOAT_SUCCESS ) return BOAT_ERROR;


    // Step 2: Set Transaction Common Parameters

    // Set gasprice
    // Either manually set the gas price or get the price from network
    #if 0
    // Manually
    gas_price.field_len =
    UtilityHex2Bin(
                    gas_price.field,
                    32,
                    "0x8250de00",
                    TRIMBIN_LEFTTRIM,
                    BOAT_TRUE
                  );
    #else
    // To use the price obtained from network, simply call BoatTxSetGasPrice(NULL)
    result = BoatTxSetGasPrice(NULL/*&gas_price*/);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;
    #endif

    // Set gaslimit

    gas_limit.field_len =
    UtilityHex2Bin(
                    gas_limit.field,
                    32,
                    "0x1fffff",
                    TRIMBIN_LEFTTRIM,
                    BOAT_TRUE
                  );

    result = BoatTxSetGasLimit(&gas_limit);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;
    
    return BOAT_SUCCESS;
}

UINT8 g_use_real_gps = 1;

int main(int argc, char *argv[])
{
    BOAT_RESULT result;
   

    // Usage Example: boatdemo http://127.0.0.1:7545
     
    if( argc < 2 )
    {
        BoatLog(BOAT_LOG_CRITICAL, "Usage: %s http://<IP Address or URL for node>:<port> [<Simulate GPS=0|Real GPS=1|Activate GPS Only=2>, default=1]\n", argv[0]);
        return BOAT_ERROR;
    }
    
    if( argc >= 3 )
    {
        if( argv[2][0] == '0' )
        {
            // Use Simulated GPS position
            g_use_real_gps = 0;
        }
        else if( argv[2][0] == '2' )
        {
            // Activate GPS only
            g_use_real_gps = 2;
        }
        else
        {
            // Use Real GPS
            g_use_real_gps = 1;
        }
    }

    // Wait 60s for PDP context being activated
    // sleep(60);

    BoatWalletInit();
    

    
    // Set common parameters such as Node URL, Chain ID, Private key, gasprice, gaslimit
    result = SetCommonParam(argv[1]);
    // result = SetCommonParam("http://47.104.142.169:7545");
    if( result != BOAT_SUCCESS ) goto main_destruct;
    

    // Case 3010: CaseCarLocMain
    BoatLog(BOAT_LOG_NORMAL, "====== Testing CaseCarLocMain ======");
    result = CaseCarLocMain();
    if( result != BOAT_SUCCESS ) goto main_destruct;


main_destruct:


    BoatWalletDeInit();
    
    return 0;
}
