$(function () {
    initTable();
});

function initTable() {
    var loc = location.href;
    console.log("位置信息:"+loc);
    var totalLength = loc.length;//地址的总长度
    var interceptLength = loc.indexOf("=");//取得=号的位置
    var id = loc.substr(interceptLength + 1, totalLength - interceptLength);//从=号后面的内容
    console.log("id:"+id);
    $("#dg").datagrid({
        method:'get',
        url: '/informationController/queryFormalRecord?xId='+id,
        columns: [[{
            field: 'xId',
            title: 'xID',
            width: 400,
            align: 'center',
            sortable: false
        }, {
            field: 'gps',
            title: 'Location',
            width: 400,
            align: 'center',
            sortable: false
        }, {
            field: 'createTime',
            title: 'Timestamp',
            width: 300,
            align: 'center'
        }, {
            field: 'judgment',
            title: 'Hash Verification',
            align: 'center',
            width: 300,
            formatter: function (value, rowData) {
                if (value==true){
                    return "<img src=\"images/yes.png\"/>"
                } else{
                    return "<img src=\"images/no.png\"/>"
                }

            }
        }]]
    });
}
