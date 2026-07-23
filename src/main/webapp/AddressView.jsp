<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="Models.Address" %>
<!DOCTYPE html>
<html lang="en">


    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="./assets/css/popup.css"/>
        <title>Shipping Address</title>
        <style>

            .address-page-panel {
                width: 100%;
                border-radius: 18px;
                background: #fff;
                color: #1f2937;
                padding: 24px;
                box-shadow: 0 12px 32px rgba(17, 24, 39, 0.12);
            }

            .address-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 16px;
                border-bottom: 1px solid rgba(17, 24, 39, 0.12);
                padding-bottom: 18px;
            }

            .address-header h2,
            .address-section-title {
                margin: 0;
                color: inherit;
                font-weight: 800;
            }

            .address-page-panel .btn-add {
                background: #d8ad5a;
                color: #111;
                border: 0;
                border-radius: 999px;
                padding: 9px 16px;
                font-weight: 800;
            }

            .address-page-panel .btn-add:hover {
                background: #f0c873;
                color: #111;
            }

            .address-section-title {
                padding: 20px 0 10px;
                font-size: 24px;
            }

            #addressList {
                display: grid;
                gap: 14px;
            }

            .address-card {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 18px;
                padding: 16px 18px;
                border: 1px solid rgba(17, 24, 39, 0.1);
                border-radius: 14px;
                background: rgba(17, 24, 39, 0.03);
            }

            .address-text {
                display: grid;
                gap: 8px;
                min-width: 0;
            }

            .address-line {
                margin: 0;
                line-height: 1.55;
                overflow-wrap: anywhere;
                font-weight: 650;
            }

            .default-badge {
                width: max-content;
                display: inline-flex;
                align-items: center;
                border-radius: 999px;
                padding: 5px 11px;
                background: rgba(216, 173, 90, 0.16);
                color: #8a5d10 !important;
                border: 1px solid rgba(216, 173, 90, 0.35);
                font-size: 12px;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: .04em;
            }

            .address-actions {
                display: flex;
                flex-direction: column;
                align-items: flex-end;
                gap: 8px;
                flex: 0 0 auto;
            }

            .address-actions-row {
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .address-action-link,
            .address-default-form {
                margin: 0;
                padding: 0;
            }

            .address-action-link {
                border: 0;
                background: transparent;
                color: #2563eb !important;
                font-weight: 800;
                padding: 5px 0;
                cursor: pointer;
            }

            .address-action-link.danger {
                color: #dc2626 !important;
            }

            .btn-default-address {
                background: rgba(17, 24, 39, 0.06);
                color: #1f2937;
                border: 1px solid rgba(17, 24, 39, 0.12);
                border-radius: 999px;
                padding: 6px 12px;
                font-size: 13px;
                font-weight: 800;
                cursor: pointer;
            }

            .empty-address {
                padding: 26px;
                border: 1px dashed rgba(17, 24, 39, 0.18);
                border-radius: 14px;
                color: #6b7280;
                text-align: center;
                font-weight: 700;
            }

            .add {
                display: none;
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                background: #fff;
                color: #1f2937;
                padding: 24px;
                border-radius: 18px;
                box-shadow: 0 30px 90px rgba(0, 0, 0, 0.35);
                z-index: 1060;
                width: min(760px, calc(100vw - 32px));
                max-height: calc(100vh - 48px);
                overflow-y: auto;
                border: 1px solid rgba(17, 24, 39, 0.12);
            }

            .addoverlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.68);
                z-index: 1050;
            }

            .add .title {
                margin-bottom: 16px;
                font-weight: 800;
            }

            .add .modal-footer {
                border-top: 0;
                padding: 10px 0 0;
            }

            .customer-profile-page .address-page-panel {
                background: rgba(255, 255, 255, .07);
                color: #f7f3ea;
                border: 1px solid rgba(255, 255, 255, .11);
                box-shadow: 0 24px 70px rgba(0, 0, 0, .26);
            }

            .customer-profile-page .address-header {
                border-bottom-color: rgba(255, 255, 255, .16);
            }

            .customer-profile-page .address-card {
                background: rgba(255, 255, 255, .06);
                border-color: rgba(255, 255, 255, .12);
            }

            .customer-profile-page .address-line,
            .customer-profile-page .address-page-panel h2,
            .customer-profile-page .address-section-title {
                color: #fff !important;
            }

            .customer-profile-page .default-badge {
                background: rgba(216, 173, 90, .18);
                border-color: rgba(216, 173, 90, .38);
                color: #ffd782 !important;
            }

            .customer-profile-page .btn-default-address {
                background: rgba(255, 255, 255, .08);
                border-color: rgba(255, 255, 255, .16);
                color: #f7f3ea;
            }

            .customer-profile-page .empty-address {
                border-color: rgba(255, 255, 255, .18);
                color: #d7cbb7;
            }

            .customer-profile-page .add {
                background: #2f2b24 !important;
                color: #f7f3ea !important;
                border-color: rgba(216, 173, 90, .28);
            }

            .customer-profile-page .add label,
            .customer-profile-page .add .title,
            .customer-profile-page .add .form-check-label {
                color: #fff !important;
            }

            .customer-profile-page .add .form-control,
            .customer-profile-page .add .form-select {
                background: rgba(255, 255, 255, .08) !important;
                border-color: rgba(255, 255, 255, .18) !important;
                color: #fff !important;
            }

            .customer-profile-page .add .form-select option {
                background: #24211b;
                color: #fff;
            }

            @media (max-width: 700px) {
                .address-header,
                .address-card {
                    align-items: stretch;
                    flex-direction: column;
                }

                .address-actions {
                    align-items: flex-start;
                    width: 100%;
                }

                .address-actions-row {
                    flex-wrap: wrap;
                }
            }
        </style>
    </head>

    <body style="font-family: Arial, sans-serif; ">
        <%
            String action = "";
            if (request.getParameter("action") != null) {
                action = request.getParameter("action");
                if (action.equalsIgnoreCase("forOrder")) {
        %>
        <jsp:include page="header.jsp"></jsp:include>
            <br>
            <div class="container">
                <div style=" justify-content: space-between; align-items: center;"> 
                    <a class="btn btn-primary" href="order?action=changeAddress">Back to checkout</a>
                    <p style="margin-top: 20px">Please choose other address by setting as default.</p>
                </div>

            <%
                    }
                }
            %>
            <div class="address-page-panel">
                <div class="address-header">
                    <h2>My addresses</h2>
                    <button class="btn btn-add"
                            onclick="openPopup(false)">+ Add address</button>
                </div>
                <c:if test="${not empty sessionScope.addressError}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${sessionScope.addressError}"/>
                    </div>
                    <c:remove var="addressError" scope="session"/>
                </c:if>

                <h4 class="address-section-title">Address</h4>

                <div id="addressList">
                    <c:if test="${sessionScope.addressList.isEmpty()}">
                        <div class="empty-address">There is no shipping address</div>
                    </c:if>
                    <c:if test="${!sessionScope.addressList.isEmpty()}">
                        <c:forEach items="${sessionScope.addressList}" var="ad">
                            <c:set var="isDefault" value="0" ></c:set>
                                <div class="address address-card">
                                    <div class="address-text">
                                        <p class="address-line">${ad.getAddressDetails()}</p>
                                    <c:if test="${ad.getIsDefault() == 1}">
                                        <c:set var="isDefault" value="1" ></c:set>
                                            <span class="default-badge">Default</span>
                                    </c:if>
                                </div>
                                <div class="address-actions actions">        
                                    <c:set var="arr" value="${fn: split(ad.getAddressDetails(), ',')}" ></c:set>
                                    <%
                                        Object arrObj = pageContext.getAttribute("arr");

                                        // Kiểm tra nếu không null và chuyển thành mảng String[]
                                         if (arrObj instanceof String[]) {
                                             String[] arr = (String[]) arrObj;
                                             if (arr.length >= 4) {

                                            // Xử lý mảng theo thuật toán của bạn
                                            String province = arr[arr.length - 1];
                                            String district = arr[arr.length - 2];
                                            String commune = arr[arr.length - 3];

                                            // Ghép các phần còn lại thành address
                                            String address = "";
                                            for (int i = 0; i < arr.length - 3; i++) {

                                                address += arr[i] + ",";

                                            }
                                            address = address.substring(0, address.length() - 1);

                                            // Trả kết quả lại JSTL
                                            request.setAttribute("province", province);
                                             request.setAttribute("district", district);
                                             request.setAttribute("commune", commune);
                                             request.setAttribute("address", address);
                                             } else {
                                                 request.setAttribute("province", "");
                                                 request.setAttribute("district", "");
                                                 request.setAttribute("commune", "");
                                                 request.setAttribute("address", String.join(", ", arr));
                                             }
                                         }
                                    %>


                                    <div class="address-actions-row btn1">
                                        <button class="address-action-link btn-update"
                                                data-bs-toggle="modal"
                                                data-bs-target="#updateModal"
                                                data-isDefault = "${isDefault}"
                                                data-id = "${ad.getAddressID()}"
                                                data-province="${province}"
                                                data-district="${district}"
                                                data-commune="${commune}"
                                                data-address="${address}"
                                                onclick="openPopupFromButton(this)">Update</button>
                                        <c:if test="${ad.getIsDefault() == 0}">
                                            <%
                                                if (!action.equalsIgnoreCase("") && action.equalsIgnoreCase("forOrder")) {
                                            %>
                                            <a href="DeleteAddress?id=${ad.getAddressID()}&currentAddressPage=forOrder" class="address-action-link danger btn-delete">Delete</a>
                                            <input type="type" name="currentAddressPage" value="forOrder" hidden>
                                            <%
                                            } else {

                                            %>
                                            <a href="DeleteAddress?id=${ad.getAddressID()}&currentAddressPage=addressPage" class="address-action-link danger btn-delete">Delete</a>
                                            <%                                                }
                                            %>

                                        </c:if>

                                    </div>
                                    <c:if test="${ad.getIsDefault() == 0}">
                                        <form class="address-default-form" method="POST" action="UpdateAddress?action=setAsDefault&id=${ad.getAddressID()}">
                                            <%
                                                if (!action.equalsIgnoreCase("") && action.equalsIgnoreCase("forOrder")) {
                                            %>
                                            <input type="type" name="currentAddressPage" value="forOrder" hidden>
                                            <%
                                            } else {

                                            %>
                                            <input type="type" name="currentAddressPage" value="addressPage" hidden>
                                            <%                                                }
                                            %>

                                            <button class="btn-default-address btn-default" type="submit">Set as default</button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>

                </div>
            </div>
            <%
                if (request.getParameter("action") != null) {
                    action = request.getParameter("action");
                    if (action.equalsIgnoreCase("forOrder")) {
            %>
        </div>
        <br>
        <!-- Popup Notification (if needed) -->
        <%
            String message = (String) session.getAttribute("message");
            if (message != null) {
        %>
        <div id="cookiesPopup" style="position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 350px; display: flex; flex-direction: column; align-items: center; background-color: #fff; color: #000; text-align: center; border-radius: 20px; padding: 30px 30px 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); z-index: 1000;">
            <button class="close" onclick="closePopup()" style="width: 30px; font-size: 20px; color: #c0c5cb; align-self: flex-end; background-color: transparent; border: none; margin-bottom: 10px; cursor: pointer;">✖</button>
            <img src="https://cdn-icons-png.flaticon.com/512/845/845646.png" alt="success-tick" style="width: 82px; margin-bottom: 15px;" />
            <p style="margin-bottom: 40px; font-size: 18px;">${sessionScope.message}</p>
            <button class="accept" onclick="closePopup()" style="background-color: #28a745; border: none; border-radius: 5px; width: 200px; padding: 14px; font-size: 16px; color: white; box-shadow: 0px 6px 18px -5px rgba(40, 167, 69, 1); cursor: pointer;">OK</button>
        </div>


        <div id="overlay" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 999;"></div>

        <%
                session.removeAttribute("message");
            }
        %>

        <jsp:include page="footer.jsp"></jsp:include>
        <%
                }
            }
        %>
        <div class="addoverlay" id="addoverlay" onclick=""></div>

        <div class="add" id="add">

            <div class="" style="display: flex; ">
                <h4 class="title" id="popupLabel">Add Address</h4>
            </div>
            <form method="POST" action="AddAddress" id="formAddress">
                <%
                    if (!action.equalsIgnoreCase("") && action.equalsIgnoreCase("forOrder")) {
                %>
                <input type="type" name="currentAddressPage" value="forOrder" hidden>
                <%
                } else {
                %>
                <input type="type" name="currentAddressPage" value="addressPage" hidden>
                <%                                                }
                %>
                <div class="mb-3">
                    <label for="city" class="form-label">Province</label>
                    <input class="form-control" type="text" name="province" id="city"
                           placeholder="Enter province or city" maxlength="100" required>
                </div>
                <div class="mb-3">
                    <label for="district" class="form-label">District</label>
                    <input class="form-control" type="text" name="district" id="district"
                           placeholder="Enter district" maxlength="100" required>
                </div>
                <div class="mb-3">
                    <label for="ward" class="form-label">Ward</label>
                    <input class="form-control" type="text" name="ward" id="ward"
                           placeholder="Enter ward or commune" maxlength="100" required>
                </div>
                <div class="mb-3">
                    <label for="address" class="form-label">Detailed Address:</label>
                    <input type="text" id="addressInput" name="address" class="form-control" required
                           value="">
                    <small id="error-message" class="form-text text-danger"></small>

                </div>
                <% boolean first = false; %>
                <c:if test="${sessionScope.addressList.isEmpty()}">
                    <%  first = true; %>
                    <div class="mb-3 form-check form-switch">
                        <input type="hidden" name="isDefault" value="1">
                        <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault" checked disabled>
                        <label class="form-check-label" for="flexSwitchCheckDefault">Set as default</label>
                    </div>
                </c:if>
                <c:if test="${!sessionScope.addressList.isEmpty()}">
                    <div class="mb-3 form-check form-switch">

                        <input class="form-check-input" name="isDefault" type="checkbox" role="switch" id="flexSwitchCheckDefault">
                        <label class="form-check-label" for="flexSwitchCheckDefault" id="defaultSwitch">Set as default</label>
                    </div>
                </c:if>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeAddPopup()"
                            data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-danger">Save</button>
                </div>
            </form>
        </div>
    </div>
    <script>
                        document.getElementById('formAddress').addEventListener('submit', function (event) {
                            const addressInput = document.getElementById('addressInput');
                            const errorMessage = document.getElementById('error-message');

                            errorMessage.textContent = '';

                            if (addressInput.value.trim().length < 5) {
                                errorMessage.textContent = 'Detailed Address must be at least 5 characters.';
                                errorMessage.style.color = 'red';
                                event.preventDefault();

                                // Hidden thông báo sau 5 giây
                                setTimeout(() => {
                                    errorMessage.textContent = '';
                                }, 5000);
                            }
                        });


                        function closePopup() {
                            document.getElementById("cookiesPopup").style.display = "none";
                            document.getElementById("overlay").style.display = "none";
                        }
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
                        if (typeof axios !== "undefined") {
                        axios(Parameter)
                                .then(function (result) {
                                    console.log("Dữ liệu API:", result.data); // Debug dữ liệu
                                    allData = result.data;
                                    if (allData.length > 0) {
                                        renderCity(allData);
                                    }
                                })
                                .catch(function (error) {
                                    console.error("Lỗi tải dữ liệu: ", error);
                                });
                        }


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
                            console.log("Render City Data: ", data); // Kiểm tra dữ liệu có vào không
                            for (const x of data) {
                                let translatedName = translateLocation(x.Name);
                                console.log("Add tỉnh/thành phố:", translatedName); // Kiểm tra dữ liệu sau khi dịch
                                citis.options[citis.options.length] = new Option(translatedName, translatedName);
                            }
                        }


                        function loadDistricts(cityName) {
                            districts.length = 1;
                            wards.length = 1;
                            if (cityName !== "") {
                                let result = allData.find(n => translateLocation(n.Name) === cityName);
                                for (const k of result.Districts) {
                                    let translatedName = translateLocation(k.Name);
                                    districts.options[districts.options.length] = new Option(translatedName, translatedName);
                                }
                            }
                        }

                        function loadWards(districtName) {
                            wards.length = 1;
                            let cityData = allData.find(n => translateLocation(n.Name) === citis.value);
                            if (cityData && districtName !== "") {
                                let districtData = cityData.Districts.find(n => translateLocation(n.Name) === districtName);
                                for (const w of districtData.Wards) {
                                    let translatedName = translateLocation(w.Name);
                                    wards.options[wards.options.length] = new Option(translatedName, translatedName);
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
                        function openPopupFromButton(button) {
                            const isDefault = button.getAttribute("data-isDefault").trim();
                            const id = button.getAttribute("data-id").trim();
                            const province = button.getAttribute("data-province").trim();
                            const district = button.getAttribute("data-district").trim();
                            const commune = button.getAttribute("data-commune").trim();
                            const address = button.getAttribute("data-address").trim();

                            openPopup(true, {isDefault, id, province, district, commune, address});
                        }

                        function openPopup(isUpdate, data = null) {
                            document.getElementById("add").style.display = "block";
                            document.getElementById("addoverlay").style.display = "block";

                            if (isUpdate && data) {
                                document.getElementById("popupLabel").innerHTML = "Update Address";
                                document.getElementById("addressInput").value = data.address || "";
                                const form = document.getElementById("formAddress");
                                if (data.isDefault === "1") {
                                    document.getElementById("flexSwitchCheckDefault").checked = true;
                                    document.getElementById("flexSwitchCheckDefault").disabled = true;
                                } else {
                                    document.getElementById("flexSwitchCheckDefault").checked = false;
                                    document.getElementById("flexSwitchCheckDefault").disabled = false;

                                }

                                if (form && data.id) {
                                    form.action = "UpdateAddress?id=" + data.id.trim();
                                } else {
                                    console.error("Error: formAddress not found or data.id is invalid");
                                }


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
                                document.getElementById("city").value = "";
                                document.getElementById("district").value = "";
                                document.getElementById("ward").value = "";
                        }
                        }

                        function setSelectValue(selectId, value, callback = null) {
                            let select = document.getElementById(selectId);
                            if (select && select.tagName !== "SELECT") {
                                select.value = value || "";
                                if (callback) {
                                    callback();
                                }
                                return;
                            }
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
                            let addressFields = document.querySelectorAll("#add input[name='province'], #add input[name='district'], #add input[name='ward'], #add input[name='address']");
                            addressFields.forEach(field => {
                                field.value = "";
                            });
        <% if (!first) {
        %>
                            document.getElementById("flexSwitchCheckDefault").disabled = false;
                            document.getElementById("flexSwitchCheckDefault").checked = false;
        <%
            }%>



                        }

                        console.log(document.getElementById("city"));
                        console.log(document.getElementById("district"));
                        console.log(document.getElementById("ward"));

    </script>

</body>

</html>
