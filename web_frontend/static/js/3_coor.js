$(function () {
    initTable();
});

function initTable() {
    $("#dg").datagrid({
        url: '/informationController/queryContractInfo',
        columns: [[{
            field: 'hashId',
            title: 'xID',
            width: 400,
            align: 'center',
            sortable: false
        }, {
            field: 'bookTime',
            title: 'Booking Timestamp',
            width: 300,
            align: 'center',
            sortable: false,
            formatter: function (value, rowData) {
                return format(value);
            }
        }, {
            field: 'startTime',
            title: 'Begin Timestamp',
            width: 300,
            align: 'center',
            sortable: false,
            formatter: function (value, rowData) {
                return format(value);
            }
        }, {
            field: 'stopTime',
            title: 'End Timestamp',
            width: 300,
            align: 'center',
            sortable: false,
            formatter: function (value, rowData) {
                return format(value);
            }
        }, {
            field: 'status',
            title: 'Status',
            width: 200,
            align: 'center',
            sortable: false,
            formatter: function (value, rowData) {
                if (value==1){
                    return 'Booked';
                }else if (value==2){
                    return 'Inuse';
                } else{
                    return 'Completed';
                }
            }
        }, {
                field: 'xId',
                title: 'XID',
                width: 400,
                align: 'center',
                sortable: false,
                hidden:'true'
            },
            {
            field: '操作',
            title: 'Operation',
            align: 'center',
            width: 300,
            formatter: function (value, rowData) {
                return "<a target=\"_self\" href='" + "http://" + window.location.host + "/4_detail.html?xId=" + rowData.xId + "'>" + 'More' + "</a>"
            }
        }]]
    });
};
var format=function (value) {
    if (value == undefined) {
        return null;
    }else{
        var time1=value.substring(0,value.indexOf('.'));
        var time2=time1.substring(0,value.indexOf('T'));
        var time3=time1.substring(value.indexOf('T')+1,value.length);
        var time4=time2+' '+time3;
        return time4;
    }

};
