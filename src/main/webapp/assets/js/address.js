(function () {
    "use strict";

    var form = document.getElementById("formAddress");
    var provinceSelect = document.getElementById("city");
    var districtSelect = document.getElementById("district");
    var wardSelect = document.getElementById("ward");
    var locationError = document.getElementById("location-error");

    if (!form || !provinceSelect || !districtSelect || !wardSelect) {
        return;
    }

    var locations = [];
    var locationUrl = form.getAttribute("data-location-url");

    function setFirstOption(select, label) {
        select.length = 0;
        select.add(new Option(label, ""));
    }

    function setLoadingState() {
        setFirstOption(provinceSelect, "Loading provinces...");
        setFirstOption(districtSelect, "Select District");
        setFirstOption(wardSelect, "Select Ward");
        provinceSelect.disabled = true;
        districtSelect.disabled = true;
        wardSelect.disabled = true;
    }

    function showLocationError(message) {
        if (locationError) {
            locationError.textContent = message;
        }
    }

    function renderProvinces() {
        setFirstOption(provinceSelect, "Select Province");
        locations.forEach(function (province) {
            var provinceName = toEnglishLocationName(province.Name);
            provinceSelect.add(new Option(provinceName, provinceName));
        });
        provinceSelect.disabled = false;
        districtSelect.disabled = true;
        wardSelect.disabled = true;
        showLocationError("");
    }

    function loadDistricts(provinceName) {
        setFirstOption(districtSelect, "Select District");
        setFirstOption(wardSelect, "Select Ward");
        wardSelect.disabled = true;

        var province = locations.find(function (item) {
            return normalizeLocation(toEnglishLocationName(item.Name)) === normalizeLocation(provinceName);
        });

        if (!province) {
            districtSelect.disabled = true;
            return;
        }

        province.Districts.forEach(function (district) {
            var districtName = toEnglishLocationName(district.Name);
            districtSelect.add(new Option(districtName, districtName));
        });
        districtSelect.disabled = false;
    }

    function loadWards(districtName) {
        setFirstOption(wardSelect, "Select Ward");

        var province = locations.find(function (item) {
            return normalizeLocation(toEnglishLocationName(item.Name))
                    === normalizeLocation(provinceSelect.value);
        });
        var district = province && province.Districts.find(function (item) {
            return normalizeLocation(toEnglishLocationName(item.Name))
                    === normalizeLocation(districtName);
        });

        if (!district) {
            wardSelect.disabled = true;
            return;
        }

        district.Wards.forEach(function (ward) {
            var wardName = toEnglishLocationName(ward.Name);
            wardSelect.add(new Option(wardName, wardName));
        });
        wardSelect.disabled = false;
    }

    function normalizeLocation(value) {
        return (value || "")
                .trim()
                .toLocaleLowerCase("vi")
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .replace(/đ/g, "d")
                .replace(/\s+/g, " ");
    }

    function toEnglishLocationName(name) {
        var replacements = [
            [/^Thành phố\s+/i, "", " City"],
            [/^Tỉnh\s+/i, "", " Province"],
            [/^Huyện\s+/i, "", " District"],
            [/^Quận\s+/i, "", " District"],
            [/^Thị xã\s+/i, "", " Town"],
            [/^Thị trấn\s+/i, "", " Town"],
            [/^Phường\s+/i, "", " Ward"],
            [/^Xã\s+/i, "", " Commune"]
        ];
        var result = name || "";

        replacements.some(function (replacement) {
            if (replacement[0].test(result)) {
                result = result.replace(replacement[0], replacement[1]) + replacement[2];
                return true;
            }
            return false;
        });

        return removeDiacritics(result);
    }

    function removeDiacritics(value) {
        return (value || "")
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .replace(/Đ/g, "D")
                .replace(/đ/g, "d");
    }

    function setSelectedLocation(select, savedName) {
        var target = normalizeLocation(savedName);
        var englishTarget = normalizeLocation(toEnglishLocationName(savedName));

        for (var index = 1; index < select.options.length; index++) {
            var option = select.options[index];
            if (normalizeLocation(option.text) === target
                    || normalizeLocation(option.text) === englishTarget) {
                select.selectedIndex = index;
                return true;
            }
        }

        select.selectedIndex = 0;
        return false;
    }

    function restoreAddressSelections(data) {
        locationReady.then(function (isReady) {
            if (!isReady) {
                return;
            }
            if (!setSelectedLocation(provinceSelect, data.province)) {
                showLocationError("The province from the saved address could not be found.");
                return;
            }

            loadDistricts(provinceSelect.value);
            if (!setSelectedLocation(districtSelect, data.district)) {
                showLocationError("The district from the saved address could not be found.");
                return;
            }

            loadWards(districtSelect.value);
            if (!setSelectedLocation(wardSelect, data.commune)) {
                showLocationError("The ward from the saved address could not be found.");
            }
        });
    }

    setLoadingState();

    var locationReady = fetch(locationUrl, {cache: "force-cache"})
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("HTTP " + response.status);
                }
                return response.json();
            })
            .then(function (data) {
                if (!Array.isArray(data) || data.length === 0) {
                    throw new Error("Location data is empty");
                }
                locations = data;
                renderProvinces();
                return true;
            })
            .catch(function (error) {
                setFirstOption(provinceSelect, "Unable to load provinces");
                provinceSelect.disabled = true;
                showLocationError("Unable to load Vietnam location data. Please reload the page.");
                console.error("Cannot load Vietnam location data:", error);
                return false;
            });

    provinceSelect.addEventListener("change", function () {
        loadDistricts(this.value);
    });

    districtSelect.addEventListener("change", function () {
        loadWards(this.value);
    });

    form.addEventListener("submit", function (event) {
        var addressInput = document.getElementById("addressInput");
        var addressError = document.getElementById("error-message");
        var isValid = true;

        addressError.textContent = "";
        showLocationError("");

        if (!provinceSelect.value || !districtSelect.value || !wardSelect.value) {
            showLocationError("Please select a province, district, and ward.");
            isValid = false;
        }

        if (addressInput.value.trim().length < 5) {
            addressError.textContent = "Detailed address must be at least 5 characters.";
            isValid = false;
        }

        if (!isValid) {
            event.preventDefault();
        }
    });

    window.openPopupFromButton = function (button) {
        window.openPopup(true, {
            isDefault: button.getAttribute("data-isDefault").trim(),
            id: button.getAttribute("data-id").trim(),
            province: button.getAttribute("data-province").trim(),
            district: button.getAttribute("data-district").trim(),
            commune: button.getAttribute("data-commune").trim(),
            address: button.getAttribute("data-address").trim()
        });
    };

    window.openPopup = function (isUpdate, data) {
        document.getElementById("add").style.display = "block";
        document.getElementById("addoverlay").style.display = "block";
        var addressInput = document.getElementById("addressInput");
        var defaultSwitch = document.getElementById("flexSwitchCheckDefault");

        showLocationError("");

        if (isUpdate && data) {
            document.getElementById("popupLabel").textContent = "Update Address";
            addressInput.value = data.address || "";
            form.action = "UpdateAddress?id=" + encodeURIComponent(data.id);

            if (defaultSwitch) {
                defaultSwitch.checked = data.isDefault === "1";
                defaultSwitch.disabled = data.isDefault === "1";
            }
            restoreAddressSelections(data);
            return;
        }

        document.getElementById("popupLabel").textContent = "Add Address";
        addressInput.value = "";
        form.action = "AddAddress";
        provinceSelect.selectedIndex = 0;
        loadDistricts("");

        if (defaultSwitch && !form.querySelector('input[type="hidden"][name="isDefault"]')) {
            defaultSwitch.checked = false;
            defaultSwitch.disabled = false;
        }
    };

    window.closeAddPopup = function () {
        document.getElementById("add").style.display = "none";
        document.getElementById("addoverlay").style.display = "none";
        provinceSelect.selectedIndex = 0;
        loadDistricts("");
        showLocationError("");

        var defaultSwitch = document.getElementById("flexSwitchCheckDefault");
        if (defaultSwitch && !form.querySelector('input[type="hidden"][name="isDefault"]')) {
            defaultSwitch.checked = false;
            defaultSwitch.disabled = false;
        }
    };
}());
