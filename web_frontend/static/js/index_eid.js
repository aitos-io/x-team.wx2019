var clickLogo=function () {
    $.ajax({
        url: "/orderCar/blackBoxOperation",
        method: 'post',
        data: '',
        headers: {},
        success: function (data) {
        }
    });
};