pragma solidity >=0.4.16 <0.6.0;

contract carService {
//    address payable organizer;
    address organizer;

    enum OperationStatus {Sucess, FailWrongParameter, FailNotExist}
    enum ContractStatus {Booked, Inuse, Completed}

    struct carSession {
        ContractStatus status;
        bytes8     bookTime;
        bytes8     lockTime;
        bytes8     unlockTime;
        bytes32[] locList;

        bool      isValid; // internal use
    }

    mapping(bytes32 => carSession) sessionRecords;


	/*function initSession(bytes32 xID, uint8 status, bytes8 bookTime) public returns (OperationStatus status_) {
	    status_ = OperationStatus.Sucess;
	    if (sessionRecords[xID].isValid == true) {
	        status_ = OperationStatus.FailExist;
	    } else if (bookTime <= 0) {
	        status_ = OperationStatus.FailWrongParameter;
	    } else {
	        sessionRecords[xID] = carSession(status,
                                                 bookTime,
                                                 0x0000000000000000,
                                                 0x0000000000000000,
                                                 0,
                                                 true);
	        status_ = OperationStatus.Sucess;
	    }
	}*/

	function updateStatus(bytes32 xID, ContractStatus status, bytes8 time) public returns (OperationStatus status_) {
            bytes32[] memory dummy;
	    status_ = OperationStatus.Sucess;

	    if(status == ContractStatus.Booked) {
	        if (sessionRecords[xID].isValid == true) {
	            sessionRecords[xID].status = status;
	            sessionRecords[xID].bookTime = time;
	            //carSession newSession = sessionRecords[xID];
	            //newSession.status = status;
	            //newSession.bookTime = time;
	            //sessionRecords[xID] = newSession;
	        } else {
	            sessionRecords[xID] = carSession(status,
                                                     time,
                                                     0x0000000000000000,
                                                     0x0000000000000000,
                                                     dummy,
                                                     true);
	        }
	        
	    } else if (status == ContractStatus.Completed) {
	        if (sessionRecords[xID].isValid == true) {
	            sessionRecords[xID].status = status;
	            sessionRecords[xID].lockTime = time;
	        } else {
	            sessionRecords[xID] = carSession(status,
                                                     0x0000000000000000,
                                                     time,
                                                     0x0000000000000000,
                                                     dummy,
                                                     true);
	        }
	        
	    } else if (status == ContractStatus.Inuse) {
	        if (sessionRecords[xID].isValid == true) {
	            sessionRecords[xID].status = status;
	            sessionRecords[xID].unlockTime = time;
	        } else {
	            sessionRecords[xID] = carSession(status,
                                                     0x0000000000000000,
                                                     0x0000000000000000,
                                                     time,
                                                     dummy,
                                                     true);
	        }
	        
	    } else {
	        status_ = OperationStatus.FailWrongParameter;
	    }
    }

    function updateLoc(bytes32 xID, bytes32 newLoc) public returns (OperationStatus status_){
        
        if (sessionRecords[xID].isValid == true) {
            sessionRecords[xID].locList.push(newLoc);
            status_ = OperationStatus.Sucess;
        } else {
            status_ = OperationStatus.FailNotExist;
        }
    }


    /////////////////////////////////////////////////////////////////////////////
    ////////////////////////constant view functions//////////////////////////////
    function readStatusAndTimes(bytes32 xID) public view returns (ContractStatus, bytes8, bytes8, bytes8) {
        if (sessionRecords[xID].isValid == true) {
            return (sessionRecords[xID].status, sessionRecords[xID].bookTime, sessionRecords[xID].lockTime, sessionRecords[xID].unlockTime);
        }
    }

    function readStatusTimesLength(bytes32 xID) public view returns (ContractStatus, bytes8, bytes8, bytes8, uint) {
        if (sessionRecords[xID].isValid == true) {
            return (sessionRecords[xID].status, sessionRecords[xID].bookTime, sessionRecords[xID].lockTime, sessionRecords[xID].unlockTime, sessionRecords[xID].locList.length);
        }
    }

    
    function readLocListLength(bytes32 xID) public view returns (uint length_) {
        if (sessionRecords[xID].isValid == true) {
            length_ = sessionRecords[xID].locList.length;
        }
    }

    function readLocListByIndex(bytes32 xID, uint index) public view returns (bytes32 event_) { 
        if((sessionRecords[xID].locList.length > index) && (sessionRecords[xID].isValid == true)) {
			event_ = sessionRecords[xID].locList[index];
        }
    }


    function readLocLists(bytes32 xID) public view returns (bytes32[] memory event_) { 
        if(sessionRecords[xID].isValid == true) {
            event_ = sessionRecords[xID].locList;
        }
    }





    constructor () public {
	    organizer = msg.sender;
	}

    function destroy() public {
        if (msg.sender == organizer) { 
            selfdestruct(organizer);
        }
    }
}
