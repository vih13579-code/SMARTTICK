(function () {
    "use strict";

    var form = document.getElementById("formAddress");
    var provinceSelect = document.getElementById("city");
    var communeSelect = document.getElementById("commune");
    var locationError = document.getElementById("location-error");
    var communePlaceholder = "Select Ward / Commune / Special Zone";

    if (!form || !provinceSelect || !communeSelect) {
        return;
    }

    var provinces = [];
    var locationUrl = form.getAttribute("data-location-url");

    function setFirstOption(select, label) {
        select.length = 0;
        select.add(new Option(label, ""));
    }

    function setLoadingState() {
        setFirstOption(provinceSelect, "Loading provinces...");
        setFirstOption(communeSelect, communePlaceholder);
        provinceSelect.disabled = true;
        communeSelect.disabled = true;
    }

    function showLocationError(message) {
        if (locationError) {
            locationError.textContent = message;
        }
    }

    function normalizeLocation(value) {
        return (value || "")
                .trim()
                .toLocaleLowerCase("en")
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .replace(/đ/g, "d")
                .replace(/\s+/g, " ");
    }

    function findProvince(provinceName) {
        var target = normalizeLocation(provinceName);
        return provinces.find(function (province) {
            return normalizeLocation(province.name) === target;
        });
    }

    function findCommune(province, communeName) {
        var target = normalizeLocation(communeName);
        return province && province.communes.find(function (commune) {
            return normalizeLocation(commune.name) === target;
        });
    }

    function renderProvinces() {
        setFirstOption(provinceSelect, "Select Province / City");
        provinces.forEach(function (province) {
            provinceSelect.add(new Option(province.name, province.name));
        });
        provinceSelect.disabled = false;
        communeSelect.disabled = true;
        showLocationError("");
    }

    function loadCommunes(provinceName) {
        setFirstOption(communeSelect, communePlaceholder);

        var province = findProvince(provinceName);
        if (!province) {
            communeSelect.disabled = true;
            return;
        }

        province.communes.forEach(function (commune) {
            communeSelect.add(new Option(commune.name, commune.name));
        });
        communeSelect.disabled = false;
    }

    function setSelectedLocation(select, savedName) {
        var target = normalizeLocation(savedName);

        for (var index = 1; index < select.options.length; index++) {
            if (normalizeLocation(select.options[index].value) === target) {
                select.selectedIndex = index;
                return true;
            }
        }

        select.selectedIndex = 0;
        return false;
    }

    function parseSavedAddress(fullAddress) {
        var parts = (fullAddress || "").split(",").map(function (part) {
            return part.trim();
        }).filter(Boolean);

        if (parts.length < 2) {
            return {province: "", commune: "", address: fullAddress || "", legacy: true};
        }

        var provinceName = parts[parts.length - 1];
        var province = findProvince(provinceName);
        if (!province) {
            return {province: "", commune: "", address: parts.join(", "), legacy: true};
        }

        for (var index = parts.length - 2; index >= 0; index--) {
            var commune = findCommune(province, parts[index]);
            if (commune) {
                return {
                    province: province.name,
                    commune: commune.name,
                    address: parts.slice(0, index).join(", "),
                    legacy: index !== parts.length - 2
                };
            }
        }

        return {
            province: province.name,
            commune: "",
            address: parts.slice(0, -1).join(", "),
            legacy: true
        };
    }

    function restoreAddressSelections(fullAddress) {
        locationReady.then(function (isReady) {
            if (!isReady) {
                return;
            }

            var saved = parseSavedAddress(fullAddress);
            document.getElementById("addressInput").value = saved.address;

            if (!setSelectedLocation(provinceSelect, saved.province)) {
                showLocationError(
                        "This saved address uses the former administrative structure. "
                        + "Please select its current province/city and commune-level unit.");
                return;
            }

            loadCommunes(provinceSelect.value);
            if (!setSelectedLocation(communeSelect, saved.commune)) {
                showLocationError(
                        "This saved address uses the former administrative structure. "
                        + "Please select its current ward, commune, or special zone.");
                return;
            }

            if (saved.legacy) {
                showLocationError(
                        "Please verify this address because it was saved before the two-tier reform.");
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
                if (!data || data.administrativeModel !== "province-to-commune"
                        || !Array.isArray(data.provinces) || data.provinces.length !== 34) {
                    throw new Error("The post-merger location dataset is invalid");
                }
                provinces = data.provinces;
                renderProvinces();
                return true;
            })
            .catch(function (error) {
                setFirstOption(provinceSelect, "Unable to load provinces");
                provinceSelect.disabled = true;
                communeSelect.disabled = true;
                showLocationError("Unable to load Vietnam location data. Please reload the page.");
                console.error("Cannot load Vietnam location data:", error);
                return false;
            });

    provinceSelect.addEventListener("change", function () {
        loadCommunes(this.value);
        showLocationError("");
    });

    form.addEventListener("submit", function (event) {
        var addressInput = document.getElementById("addressInput");
        var addressError = document.getElementById("error-message");
        var isValid = true;

        addressError.textContent = "";
        showLocationError("");

        if (!provinceSelect.value || !communeSelect.value) {
            showLocationError(
                    "Please select a province/city and a ward, commune, or special zone.");
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
            fullAddress: button.getAttribute("data-full-address") || ""
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
            addressInput.value = "";
            form.action = "UpdateAddress?id=" + encodeURIComponent(data.id);

            if (defaultSwitch) {
                defaultSwitch.checked = data.isDefault === "1";
                defaultSwitch.disabled = data.isDefault === "1";
            }
            restoreAddressSelections(data.fullAddress);
            return;
        }

        document.getElementById("popupLabel").textContent = "Add Address";
        addressInput.value = "";
        form.action = "AddAddress";
        provinceSelect.selectedIndex = 0;
        loadCommunes("");

        if (defaultSwitch && !form.querySelector('input[type="hidden"][name="isDefault"]')) {
            defaultSwitch.checked = false;
            defaultSwitch.disabled = false;
        }
    };

    window.closeAddPopup = function () {
        document.getElementById("add").style.display = "none";
        document.getElementById("addoverlay").style.display = "none";
        provinceSelect.selectedIndex = 0;
        loadCommunes("");
        showLocationError("");

        var defaultSwitch = document.getElementById("flexSwitchCheckDefault");
        if (defaultSwitch && !form.querySelector('input[type="hidden"][name="isDefault"]')) {
            defaultSwitch.checked = false;
            defaultSwitch.disabled = false;
        }
    };
}());
