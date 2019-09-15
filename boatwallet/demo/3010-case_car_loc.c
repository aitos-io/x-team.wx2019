#include "wallet/boatwallet.h"
#include <string.h>
#include <signal.h>
#include <time.h>
#include "curl/curl.h"

extern UINT8 g_use_real_gps;

// Uncomment following line if it's on target board
#define ON_TARGET

#ifdef ON_TARGET

// APIs defined on target board
extern int DemoEnableGPS(void);
extern int DemoDisableGPS(void);
extern char * DemoGetGPSLocation(void);

#else

// If not on target, simulate +CGPSINFO
int DemoEnableGPS(void) { return 0; }
int DemoDisableGPS(void) { return 0; }
char * DemoGetGPSLocation(void)
{
    static CHAR *g_locaton_string = "+CGPSINFO: 3109.991971,N,12122.945494,E,240519,025335.0,-10.3,8.0,337.5";
    static CHAR *g_no_locaton_string = "+CGPSINFO: ,,,,,,,,";
    static UINT8 n = 0;
    
    n++;
    
    if( n % 2 == 1 )
        return g_locaton_string;
    else
        return g_no_locaton_string;
}
#endif



// Before test this case, deploy following smart contract and replace
// contract_address with the actual deployed contract address.
// See Truffle Suite's documents for how to deploy a smart contract.
CHAR *contract_address = "0xcfeb869f69431e42cdb54a4f4f105c19c080a601";


//!@brief A struct to maintain a dynamic length string.
typedef struct TCurlPortStringWithLen
{
    CHAR *string_ptr;   //!< address of the string storage
    UINT32 string_len;  //!< string length in byte excluding NULL terminator, equal to strlen(string_ptr)
    UINT32 string_space;//!< size of the space <string_ptr> pointing to, including null terminator
}CurlPortStringWithLen;

CurlPortStringWithLen g_car_loc_curl_response = {NULL, 0, 0}; 
#define CAR_LOC_CURL_RECV_BUF_SIZE_STEP 4096

BOAT_RESULT CarLocCurlInit(void)
{
    CURLcode curl_result;
    BOAT_RESULT result;
    
    curl_result = curl_global_init(CURL_GLOBAL_DEFAULT);
    
    if( curl_result != CURLE_OK)
    {
        BoatLog(BOAT_LOG_CRITICAL, "Unable to initialize curl.");
        result = BOAT_ERROR_EXT_MODULE_OPERATION_FAIL;
    }
    else
    {
        g_car_loc_curl_response.string_space = CAR_LOC_CURL_RECV_BUF_SIZE_STEP;
        g_car_loc_curl_response.string_len = 0;


        g_car_loc_curl_response.string_ptr = BoatMalloc(CAR_LOC_CURL_RECV_BUF_SIZE_STEP);
        
        if( g_car_loc_curl_response.string_ptr == NULL )
        {
            BoatLog(BOAT_LOG_CRITICAL, "Fail to allocate Curl RESPONSE buffer.");
            curl_global_cleanup();
            result = BOAT_ERROR_NULL_POINTER;
        }
        else
        {
            result = BOAT_SUCCESS;
        }

    }
    
    return result;

}


void CarLocCurlDeinit(void)
{
    curl_global_cleanup();

    g_car_loc_curl_response.string_space = 0;
    g_car_loc_curl_response.string_len = 0;


    if( g_car_loc_curl_response.string_ptr != NULL )
    {
        BoatFree(g_car_loc_curl_response.string_ptr);
    }


    g_car_loc_curl_response.string_ptr = NULL;

    return;
}


size_t CarLocCurlWriteMemoryCallback(void *data_ptr, size_t size, size_t nmemb, void *userdata)
{
    size_t data_size;
    CurlPortStringWithLen *mem;
    UINT32 expand_size;
    UINT32 expand_steps;
    CHAR *expanded_str;
    UINT32 expanded_to_space;
    
    mem = (CurlPortStringWithLen*)userdata;

    // NOTE: For historic reasons, argument size is always 1 and nmemb is the
    // size of the data chunk to write. And size * nmemb doesn't include null
    // terminator even if the data were string.
    data_size = size * nmemb;
    
    // If response buffer has enough space:
    if( mem->string_space - mem->string_len > data_size ) // 1 more byte reserved for null terminator
    {
        memcpy(mem->string_ptr + mem->string_len, data_ptr, data_size);
        mem->string_len += data_size;
        mem->string_ptr[mem->string_len] = '\0';
    }
    else  // If response buffer has no enough space
    {

        // If malloc is supported, expand the response buffer in steps of
        // CAR_LOC_CURL_RECV_BUF_SIZE_STEP.
        
        expand_size = data_size - (mem->string_space - mem->string_len) + 1; // plus 1 for null terminator
        expand_steps = (expand_size - 1) / CAR_LOC_CURL_RECV_BUF_SIZE_STEP + 1;
        expanded_to_space = expand_steps * CAR_LOC_CURL_RECV_BUF_SIZE_STEP + mem->string_space;
    
        expanded_str = BoatMalloc(expanded_to_space);

        if( expanded_str != NULL )
        {
            memcpy(expanded_str, mem->string_ptr, mem->string_len);
            memcpy(expanded_str + mem->string_len, data_ptr, data_size);
            BoatFree(mem->string_ptr);
            mem->string_ptr = expanded_str;
            mem->string_space = expanded_to_space;
            mem->string_len += data_size;
            mem->string_ptr[mem->string_len] = '\0';
        }

    }

    return data_size;

}


BOAT_RESULT CarLocPOST(const CHAR *url_str,
                       const CHAR *request_str,
                       UINT32 request_len,
                       BOAT_OUT CHAR **response_str_ptr,
                       BOAT_OUT UINT32 *response_len_ptr)
{
    CURL *curl_ctx_ptr = NULL;
    struct curl_slist *curl_opt_list_ptr = NULL;
    CURLcode curl_result;
    
    long info;
    BOAT_RESULT result = BOAT_ERROR;
    boat_try_declare;


    if(  ( request_str == NULL && request_len != 0 )
       || response_str_ptr == NULL
       || response_len_ptr == NULL )
    {
        BoatLog(BOAT_LOG_CRITICAL, "Argument cannot be NULL.");
        result = BOAT_ERROR;
        boat_throw(BOAT_ERROR_NULL_POINTER, CarLocPOST_cleanup);
    }

   
    curl_ctx_ptr = curl_easy_init();
    
    if(curl_ctx_ptr == NULL)
    {
        BoatLog(BOAT_LOG_CRITICAL, "curl_easy_init() fails.");
        result = BOAT_ERROR_EXT_MODULE_OPERATION_FAIL;
        boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);
    }
    
    // Set RPC URL in format "<protocol>://<target name or IP>:<port>". e.g. "http://192.168.56.1:7545"
    curl_result = curl_easy_setopt(curl_ctx_ptr, CURLOPT_URL, url_str);
    if( curl_result != CURLE_OK )
    {
        BoatLog(BOAT_LOG_NORMAL, "Fail to set URL: %s", url_str);
        boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);
    }

    // Configure all protocols to be supported
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_PROTOCOLS, CURLPROTO_ALL);
                   
    // Configure SSL Certification Verification
    // If certification file is not available, set them to 0.
    // See: https://curl.haxx.se/libcurl/c/CURLOPT_SSL_VERIFYPEER.html
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_SSL_VERIFYPEER, 0);
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_SSL_VERIFYHOST, 0);

    // To specify a certificate file or specify a path containing certification files
    // Only make sense when CURLOPT_SSL_VERIFYPEER is set to non-zero.
    // curl_easy_setopt(curl_ctx_ptr, CURLOPT_CAINFO, "/etc/certs/cabundle.pem");
    // curl_easy_setopt(curl_ctx_ptr, CURLOPT_CAPATH, "/etc/cert-dir");

    // Allow Re-direction
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_FOLLOWLOCATION,1); 

    // Verbose Debug Info.
    // curl_easy_setopt(curl_ctx_ptr, CURLOPT_VERBOSE, 1);


    // Set HTTP Type: POST
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_POST, 1L);

    // Set redirection: No
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_FOLLOWLOCATION, 0);

    // Set entire curl timeout in millisecond. This time includes DNS resloving.
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_TIMEOUT_MS, 30000L);

    // Set Connection timeout in millisecond
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_CONNECTTIMEOUT_MS, 10000L);

    // Set HTTP HEADER Options
    curl_opt_list_ptr = curl_slist_append(curl_opt_list_ptr,"Content-Type:application/json;charset=UTF-8");
    if( curl_opt_list_ptr == NULL ) boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);
    
    curl_opt_list_ptr = curl_slist_append(curl_opt_list_ptr,"Accept:application/json, text/javascript, */*;q=0.01");
    if( curl_opt_list_ptr == NULL ) boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);

    curl_opt_list_ptr = curl_slist_append(curl_opt_list_ptr,"Accept-Language:zh-CN,zh;q=0.8");
    if( curl_opt_list_ptr == NULL ) boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);

    curl_easy_setopt(curl_ctx_ptr, CURLOPT_HTTPHEADER, curl_opt_list_ptr);


    // Set callback and receive buffer for RESPONSE
    // Clean up response buffer
    g_car_loc_curl_response.string_ptr[0] = '\0';
    g_car_loc_curl_response.string_len = 0;
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_WRITEDATA, &g_car_loc_curl_response);
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_WRITEFUNCTION, CarLocCurlWriteMemoryCallback);

    // Set content to POST    
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_POSTFIELDS, request_str);
    curl_easy_setopt(curl_ctx_ptr, CURLOPT_POSTFIELDSIZE, request_len);


    // Perform the RPC request
    curl_result = curl_easy_perform(curl_ctx_ptr);

    if( curl_result != CURLE_OK )
    {
        BoatLog(BOAT_LOG_NORMAL, "curl_easy_perform fails with CURLcode: %d.", curl_result);
        boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);
    }
    

    curl_result = curl_easy_getinfo(curl_ctx_ptr, CURLINFO_RESPONSE_CODE, &info);

    if(( curl_result == CURLE_OK ) && (info == 200 || info == 201))
    {
        *response_str_ptr = g_car_loc_curl_response.string_ptr;
        *response_len_ptr = g_car_loc_curl_response.string_len;
        
        BoatLog(BOAT_LOG_VERBOSE, "Post: %s", request_str);
        BoatLog(BOAT_LOG_VERBOSE, "Result Code: %ld", info);
        BoatLog(BOAT_LOG_VERBOSE, "Response: %s", *response_str_ptr);
    }
    else
    {
        BoatLog(BOAT_LOG_NORMAL, "curl_easy_getinfo fails with CURLcode: %d, HTTP response code %ld.", curl_result, info);
        boat_throw(BOAT_ERROR_EXT_MODULE_OPERATION_FAIL, CarLocPOST_cleanup);
    }    

    // Clean Up
    curl_slist_free_all(curl_opt_list_ptr);
    curl_easy_cleanup(curl_ctx_ptr);
    
    result = BOAT_SUCCESS;


    // Exceptional Clean Up
    boat_catch(CarLocPOST_cleanup)
    {
        BoatLog(BOAT_LOG_NORMAL, "Exception: %d", boat_exception);
        
        if( curl_opt_list_ptr != NULL )
        {
            curl_slist_free_all(curl_opt_list_ptr);
        }

        if( curl_ctx_ptr != NULL )
        {
            curl_easy_cleanup(curl_ctx_ptr);
        }
        result = boat_exception;
    }
    
    
    
    return result;
    
}


	
BOAT_RESULT CallupdateLoc(CHAR * contract_addr_str, UINT8 xid[32], UINT8 newLoc[32])
{
    BoatAddress recipient;
    CHAR *function_prototye_str;
    UINT8 function_selector[32];
    TxFieldVariable data;
    UINT8 data_array[4+32+32];
    BOAT_RESULT result;
    
    if( contract_addr_str == NULL )
    {
        return BOAT_ERROR;
    }
   
    // Set nonce
    result = BoatTxSetNonce();
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;

    // Set recipient

    UtilityHex2Bin(
                    recipient,
                    20,
                    contract_addr_str,
                    TRIMBIN_TRIM_NO,
                    BOAT_TRUE
                  );

    result = BoatTxSetRecipient(recipient);

    if( result != BOAT_SUCCESS ) return BOAT_ERROR;
    

    // Set value

    result =BoatTxSetValue(NULL);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;


    // Set data (Function Argument)
    
    function_prototye_str = "updateLoc(bytes32,bytes32)";
    keccak_256((UINT8 *)function_prototye_str, strlen(function_prototye_str), function_selector);

    data.field_ptr = data_array;
    
    memcpy(data.field_ptr, function_selector, 4);
    
    // Truncate input string up to 31 characters plus '\0'
    memcpy(data.field_ptr+4, xid, 32);
    memcpy(data.field_ptr+36, newLoc, 32);
   
    data.field_len = 68;
     
    result = BoatTxSetData(&data);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;

    
    // Perform the transaction
    // NOTE: Field v,r,s are calculated automatically
    result = BoatTxSend();
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;

    return BOAT_SUCCESS;
}





//+CGPSINFO:[<lat>],[<N/S>],[<log>],[<E/W>],[<date>],[<UTCtime>],[<alt>],[<speed>],[<course>]
typedef struct TStringWithLen
{
    CHAR *string_ptr;
    UINT32 string_len;
}StringWithLen;

typedef struct TCgpsinfo
{
    StringWithLen lat;
    StringWithLen ns;
    StringWithLen log;
    StringWithLen ew;
    StringWithLen date;
    StringWithLen utctime;
    StringWithLen alt;
    StringWithLen speed;
    StringWithLen course;
}Cgpsinfo;

BOAT_RESULT ParseCGPSINFO(const CHAR *cgpsinfo_str, BOAT_OUT Cgpsinfo *parsed_gpsinfo_ptr)
{
    CHAR *cgpsinfo_pos_ptr;
    CHAR *field_start_pos_ptr;
    CHAR c;
    UINT32 field_len;
    
    if( cgpsinfo_str == NULL || parsed_gpsinfo_ptr == NULL )
    {
        BoatLog(BOAT_LOG_NORMAL, "Arguments cannot be NULL.");
        return BOAT_ERROR;
    }

    // Check if the string contains "+CGPSINFO:"
    cgpsinfo_pos_ptr = strstr(cgpsinfo_str, "+CGPSINFO:");
    if( cgpsinfo_pos_ptr == NULL )
    {
        BoatLog(BOAT_LOG_NORMAL, "Unable to find \"+CGPSINFO:\" in string.");
        return BOAT_ERROR;
    }
    
    cgpsinfo_pos_ptr = cgpsinfo_pos_ptr + strlen("+CGPSINFO:");
    
    // Skip space
    while( (c = *cgpsinfo_pos_ptr) == ' ' )
    {
        cgpsinfo_pos_ptr++;
    }

   
    // Extract every field

    // Extract <lat>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->lat.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->lat.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->lat.string_ptr = NULL;
        parsed_gpsinfo_ptr->lat.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <N/S>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->ns.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->ns.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->ns.string_ptr = NULL;
        parsed_gpsinfo_ptr->ns.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }

    
    // Extract <log>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->log.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->log.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->log.string_ptr = NULL;
        parsed_gpsinfo_ptr->log.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <E/W>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->ew.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->ew.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->ew.string_ptr = NULL;
        parsed_gpsinfo_ptr->ew.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <date>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->date.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->date.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->date.string_ptr = NULL;
        parsed_gpsinfo_ptr->date.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <UTCtime>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->utctime.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->utctime.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->utctime.string_ptr = NULL;
        parsed_gpsinfo_ptr->utctime.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <alt>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->alt.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->alt.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->alt.string_ptr = NULL;
        parsed_gpsinfo_ptr->alt.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <speed>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->speed.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->speed.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->speed.string_ptr = NULL;
        parsed_gpsinfo_ptr->speed.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }


    // Extract <course>
    field_start_pos_ptr = cgpsinfo_pos_ptr;
    
    while( (c = *cgpsinfo_pos_ptr) != '\0' )
    {
        if( c == ',' )     break;  // Note: cgpsinfo_pos_ptr points to ',' when loop exits
        cgpsinfo_pos_ptr++;
    }
    
    field_len = (UINT32)(cgpsinfo_pos_ptr - field_start_pos_ptr);
    if( field_len != 0 )
    {
        parsed_gpsinfo_ptr->course.string_ptr = field_start_pos_ptr;
        parsed_gpsinfo_ptr->course.string_len = field_len;
        cgpsinfo_pos_ptr++;
    }
    else
    {
        parsed_gpsinfo_ptr->course.string_ptr = NULL;
        parsed_gpsinfo_ptr->course.string_len = 0;
        
        if( c != '\0' )     cgpsinfo_pos_ptr++;
    }
    
    return BOAT_SUCCESS;
}





BOAT_RESULT GpsToLocRecord(BOAT_OUT CHAR *record_str)
{

    BOAT_RESULT result;
    CHAR *gps_location_ptr;

    Cgpsinfo parsed_gpsinfo;
    UINT32 location_string_len;

    
    // DemoGetGPSLocation() returns a string current GPS information, either
    // something like "+CGPSINFO: 3109.991971,N,12122.945494,E,240519,025335.0,-10.3,8.0,337.5"
    // or "+CGPSINFO: ,,,,,,,," if GPS is out of coverage
    gps_location_ptr = DemoGetGPSLocation();
    if( gps_location_ptr == NULL ) return BOAT_ERROR;

    result = ParseCGPSINFO(gps_location_ptr, &parsed_gpsinfo);
    if( result != BOAT_SUCCESS ) return BOAT_ERROR;

    // Check for "+CGPSINFO: ,,,,,,,,", i.e. unable to obtain location due
    // to loss of GPS coverage, ignore it
    if(   parsed_gpsinfo.lat.string_len
        + parsed_gpsinfo.ns.string_len
        + parsed_gpsinfo.log.string_len
        + parsed_gpsinfo.ew.string_len
          == 0 )
    {
        BoatLog(BOAT_LOG_NORMAL, "Out of GPS coverage, ignore.");
        return BOAT_ERROR;
    }


    // GPSINFO Date/Time: 140919,165134.0
    UINT32 day, month, year, hour, minute, second;
    
    sscanf(parsed_gpsinfo.date.string_ptr, "%02u%02u%02u", &day, &month, &year);
    /*
    day = (UINT32)(parsed_gpsinfo.date.string_ptr[0]-'0') * 10 + (UINT32)(parsed_gpsinfo.date.string_ptr[1]-'0');
    month = (UINT32)(parsed_gpsinfo.date.string_ptr[2]-'0') * 10 + (UINT32)(parsed_gpsinfo.date.string_ptr[3]-'0');
    year = (UINT32)(parsed_gpsinfo.date.string_ptr[4]-'0') * 10 + (UINT32)(parsed_gpsinfo.date.string_ptr[5]-'0');
    */
    if( year > 40 )
    {
        year = year + 1900;
    }
    else
    {
        year = year + 2000;
    }
    
    sscanf(parsed_gpsinfo.utctime.string_ptr, "%02u%02u%02u", &hour, &minute, &second);
    /*
    hour = (UINT32)(parsed_gpsinfo.utctime.string_ptr[0]-'0') * 10 + (UINT32)(parsed_gpsinfo.utctime.string_ptr[1]-'0');
    minute = (UINT32)(parsed_gpsinfo.utctime.string_ptr[2]-'0') * 10 + (UINT32)(parsed_gpsinfo.utctime.string_ptr[3]-'0');
    second = (UINT32)(parsed_gpsinfo.utctime.string_ptr[4]-'0') * 10 + (UINT32)(parsed_gpsinfo.utctime.string_ptr[5]-'0');
    */

    
    struct tm structed_time;
    UINT64 time_since_1970_ms;
    
    structed_time.tm_year = year - 1900;
    structed_time.tm_mon = month - 1;
    structed_time.tm_mday = day;
    structed_time.tm_hour = hour;
    structed_time.tm_min = minute;
    structed_time.tm_sec = second;
    structed_time.tm_isdst = 0;
    
    
    time_since_1970_ms = (UINT64)mktime(&structed_time) * 1000;

    printf("%llu, %u %u %u, %02u:%02u:%02u\n", time_since_1970_ms, structed_time.tm_year, structed_time.tm_mon, structed_time.tm_mday, structed_time.tm_hour, structed_time.tm_min, structed_time.tm_sec);
    
    printf("time_since_1970_ms: 0x");
    for( int k = 0; k < 8; k++ )
    {
        printf("%02x", *((UINT8*)&time_since_1970_ms + k));
    }
    putchar('\n');
    
    sprintf(record_str, "%llu;", time_since_1970_ms);
    
    location_string_len = strlen(record_str);

    // Save the location (first 4 fields in GPS information) to contract
    if(   parsed_gpsinfo.lat.string_len
        + parsed_gpsinfo.ns.string_len
        + parsed_gpsinfo.log.string_len
        + parsed_gpsinfo.ew.string_len
        + 3 < 32 )  // +3 for three ','s
    {

        
        // <lat>
        memcpy( record_str + location_string_len,
                parsed_gpsinfo.lat.string_ptr,
                parsed_gpsinfo.lat.string_len);

        location_string_len += parsed_gpsinfo.lat.string_len;
        record_str[location_string_len++] = ',';
        
        // <N/S>
        memcpy( record_str + location_string_len,
                parsed_gpsinfo.ns.string_ptr,
                parsed_gpsinfo.ns.string_len);

        location_string_len += parsed_gpsinfo.ns.string_len;
        record_str[location_string_len++] = ',';

        // <log>
        memcpy( record_str + location_string_len,
                parsed_gpsinfo.log.string_ptr,
                parsed_gpsinfo.log.string_len);

        location_string_len += parsed_gpsinfo.log.string_len;
        record_str[location_string_len++] = ',';

        // <E/W>
        memcpy( record_str + location_string_len,
                parsed_gpsinfo.ew.string_ptr,
                parsed_gpsinfo.ew.string_len);

        location_string_len += parsed_gpsinfo.ew.string_len;
        record_str[location_string_len++] = '\0';
                    
    }
 
  
    return BOAT_SUCCESS;
}





BOAT_RESULT UploadLocRecord(CHAR *loc_record_ptr)
{
    BOAT_RESULT result;
    UINT32 n;

    CHAR rawxid_str[256];
    //UINT8 rawxid[128];
    UINT32 rawxid_len;
    UINT8 xid[32] = {0xAA};
    UINT8 loc_hash[32];
    
    CHAR *car_request_xid_url_ptr;
    CHAR *car_post_loc_url_ptr;
    CHAR *curl_response_str;
    UINT32 curl_response_len;
    boat_try_declare;
    
    if( loc_record_ptr == NULL )
    {
        return BOAT_ERROR;
    }


    car_request_xid_url_ptr = "http://58.215.142.223:6789/externalInterface/queryVehicleXId";
    //car_request_xid_url_ptr = "http://172.20.10.2:6789/externalInterface/queryVehicleXId";
    
    result = CarLocPOST(car_request_xid_url_ptr,
                        NULL,
                        0,
                        &curl_response_str,
                        &curl_response_len);
    
    if( result != BOAT_SUCCESS || curl_response_len == 0 )
    {
        BoatLog(BOAT_LOG_NORMAL, "Request xID failed.");
        boat_throw(BOAT_ERROR, UploadLocRecord_cleanup);
    }
    
    BoatLog(BOAT_LOG_NORMAL, "xID: %s", curl_response_str);
    
    if( strcmp(curl_response_str, "error") == 0 )
    {
        BoatLog(BOAT_LOG_NORMAL, "Request xID error.");
        boat_throw(BOAT_ERROR, UploadLocRecord_cleanup);
    }
    
    strcpy(rawxid_str, curl_response_str);

    rawxid_len = strlen(rawxid_str);
    
    keccak_256((UINT8*)rawxid_str, rawxid_len, xid);
    
    printf("hashed xid:\n0x");
    for( n = 0; n < 32; n++ )   printf("%02x", xid[n]);
    putchar('\n');
    
    
    car_post_loc_url_ptr = "http://58.215.142.223:6789/orderCar/updateBoxLoc";
    //car_post_loc_url_ptr = "http://172.20.10.2:6789/orderCar/updateBoxLoc";
    
    CHAR json_buf[256];
    snprintf(
         json_buf,
         sizeof(json_buf),
         "{\"xId\":\"%s\",\"locationInfo\":\"%s\"}",
         rawxid_str,
         loc_record_ptr
        );
    
    result = CarLocPOST(car_post_loc_url_ptr,
                        json_buf,
                        strlen(json_buf),
                        &curl_response_str,
                        &curl_response_len);
    
    if( result != BOAT_SUCCESS )
    {
        BoatLog(BOAT_LOG_NORMAL, "Post position failed.");
        boat_throw(BOAT_ERROR, UploadLocRecord_cleanup);;
    }
    
    BoatLog(BOAT_LOG_NORMAL, "Post Loc: %s", json_buf);
    BoatLog(BOAT_LOG_NORMAL, "Response: %s", curl_response_str);

    keccak_256((UINT8*)(loc_record_ptr), strlen(loc_record_ptr), loc_hash);
    result = CallupdateLoc(contract_address, xid, loc_hash);


    
    boat_catch(UploadLocRecord_cleanup)
    {
        BoatLog(BOAT_LOG_NORMAL, "Exception: %d", boat_exception);
    }
    

    return BOAT_SUCCESS;
}


CHAR *simulated_loc_list[] = 
{
    "1568453920464;3109.931916,N,12122.954956,E",
    "1568453925464;3109.988272,N,12123.247893,E",
    "1568453930464;3109.871322,N,12123.330559,E",
    "1568453935464;3109.355152,N,12123.507448,E",
    "1568453940464;3109.041920,N,12123.613329,E",
    "1568453945464;3108.726729,N,12123.671712,E",
    "1568453950464;3108.573263,N,12123.349001,E",
    "1568453955464;3109.112141,N,12123.068706,E",
    "1568453960464;3109.337177,N,12123.013024,E",
    "1568453965464;3109.856216,N,12122.887402,E"
};

CHAR g_loc_from_gps_str[256];


BOAT_RESULT CaseCarLocMain(void)
{
    int n;
    
    CarLocCurlInit();

    if( g_use_real_gps == 1 )
    {
        // Use Real GPS
        
        DemoEnableGPS();
        
        for( n = 0; n < 20; n++ )
        {
            if( BOAT_SUCCESS == GpsToLocRecord(g_loc_from_gps_str) )
            {
                BoatLog(BOAT_LOG_NORMAL, "GPS Location: %s", g_loc_from_gps_str);
                UploadLocRecord(g_loc_from_gps_str);
            }
            
            sleep(10);
        }
        
        DemoDisableGPS();
    }
    else if( g_use_real_gps == 2 )
    {
        // Activate GPS only, exit without deactivating it
        DemoEnableGPS();;
    }
    else
    {
        // Use simulated GPS position
        for( n = 0; n < sizeof(simulated_loc_list)/sizeof(CHAR *); n++ )
        {
            UploadLocRecord(simulated_loc_list[n]);
        }
    }


    CarLocCurlDeinit();
    return BOAT_SUCCESS;
}