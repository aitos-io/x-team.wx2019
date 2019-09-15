$(function () {
    initTable();
});

function initTable() {
    $("#dg").datagrid({
        url: '/informationController/queryCarInfo',
        columns: [[{
            field: 'vinNumber',
            title: 'VIN',
            width: 100,
            align: 'center',
            sortable: false
        }, {
            field: 'carName',
            title: 'Brand',
            width: 100,
            align: 'center',
            sortable: false
        }, {
            field: 'carModel',
            title: 'Model',
            width: 100,
            align: 'center',
            sortable: false
        }, {
            field: 'numberPlate',
            title: 'Plate No.',
            width: 100,
            align: 'center',
            sortable: false
        }]]
    });
}
