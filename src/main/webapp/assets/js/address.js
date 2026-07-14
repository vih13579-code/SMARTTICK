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

function renderCity(data) {
    for (const x of data) {
        let translatedName = translateLocation(x.Name);
        citis.options[citis.options.length] = new Option(translatedName, x.Id);
    }
}

function loadDistricts(cityId) {
    districts.length = 1;
    wards.length = 1;
    if (cityId !== "") {
        let result = allData.find(n => n.Id === cityId);
        for (const k of result.Districts) {
            let translatedName = translateLocation(k.Name);
            districts.options[districts.options.length] = new Option(translatedName, k.Id);
        }
    }
}

function loadWards(districtId) {
    wards.length = 1;
    let cityData = allData.find(n => n.Id === citis.value);
    if (cityData && districtId !== "") {
        let districtData = cityData.Districts.find(n => n.Id === districtId);
        for (const w of districtData.Wards) {
            let translatedName = translateLocation(w.Name);
            wards.options[wards.options.length] = new Option(translatedName, w.Id);
        }
    }
}
function translateLocation(name) {
    let temp = name;

    if (temp.includes("Thành phố "))
        temp = temp.replace("Thành phố ", "") + " City";
    if (temp.includes("Tỉnh "))
        temp = temp.replace("Tỉnh ", "") + " Province";
    if (temp.includes("Huyện "))
        temp = temp.replace("Huyện ", "") + " District";
    if (temp.includes("Quận "))
        temp = temp.replace("Quận ", "") + " District";
    if (temp.includes("Thị xã "))
        temp = temp.replace("Thị xã ", "") + " Town";
    if (temp.includes("Thị trấn "))
        temp = temp.replace("Thị trấn ", "") + " Town";
    if (temp.includes("Phường "))
        temp = temp.replace("Phường ", "") + " Ward";
    if (temp.includes("Xã "))
        temp = temp.replace("Xã ", "") + " Commune";

    return removeDiacritics(temp); // Delete dấu tiếng Việt
}

function removeDiacritics(str) {
    return str.normalize("NFD").replace(/[\u0300-\u036f]/g, ""); // Loại bỏ dấu tiếng Việt
}
function openPopup(isUpdate, data = null) {
    document.getElementById("add").style.display = "block";
    document.getElementById("addoverlay").style.display = "block";

    if (isUpdate && data) {
        document.getElementById("popupLabel").innerHTML = "Update Address";
        document.getElementById("addressInput").value = data.address || "";

        // Chọn tỉnh/thành phố trước, rồi kích hoạt sự kiện change
        setSelectValue("city", data.province, function () {
            document.getElementById("city").dispatchEvent(new Event("change"));

            // Chờ quận/huyện load xong rồi chọn
            setTimeout(() => {
                setSelectValue("district", data.district, function () {
                    document.getElementById("district").dispatchEvent(new Event("change"));

                    // Chờ xã/phường load xong rồi chọn
                    setTimeout(() => {
                        setSelectValue("ward", data.commune);
                    }, 200);
                });
            }, 200);
        });
    } else {
        document.getElementById("popupLabel").innerHTML = "Add Address";
        document.getElementById("addressInput").value = "";
        document.getElementById("city").selectedIndex = 0;
        document.getElementById("district").length = 1;
        document.getElementById("ward").length = 1;
}
}

function setSelectValue(selectId, value, callback = null) {
    let select = document.getElementById(selectId);
    let found = false;

    for (let i = 0; i < select.options.length; i++) {
        if (select.options[i].text === value) {
            select.selectedIndex = i;
            found = true;
            break;
        }
    }

    if (!found) {
        select.selectedIndex = 0;
    }

    if (callback) {
        callback();
}
}


function closeAddPopup() {
    document.getElementById("add").style.display = "none";
    document.getElementById("addoverlay").style.display = "none";
    let selects = document.querySelectorAll("#add select");
    selects.forEach(select => {
        select.selectedIndex = 0;
    });
}
