/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */



var allData = [];
var citis = document.getElementById("city");
var districts = document.getElementById("district");
var wards = document.getElementById("ward");

var Parameter = {
    url: "https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json",
    method: "GET",
    responseType: "json"
};

// Đợi API tải xong rồi mới render
axios(Parameter).then(function (result) {
    allData = result.data;
    if (allData.length > 0) {
        renderCity(allData);
    }
}).catch(function (error) {
    console.error("Lỗi tải dữ liệu: ", error);
});

citis.onchange = function () {
    if (allData.length > 0) {
        loadDistricts(this.value);
    }
};

districts.onchange = function () {
    if (allData.length > 0) {
        loadWards(this.value);
    }
};
