/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */





function previewImage(event) {
    var reader = new FileReader();
    reader.onload = function () {
        var output = document.getElementById('avatarPreview');
        output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
}

function openModal() {
    document.getElementById("phoneModal").style.display = "block";
}

function closeModal() {
    document.getElementById("phoneModal").style.display = "none";
}


function updatePhone() {
    let newPhone = document.getElementById("newPhoneNumber").value.trim();
    
    // Double-check before updating
    if (newPhone.length !== 10 || newPhone[0] !== '0' || newPhone[1] < '2' || newPhone[1] > '9' || isNaN(Number(newPhone))) {
        alert("Invalid phone number. Please enter a valid 10-digit number starting with 0.");
        return;
    }

    // Update phone number in the UI
    document.getElementById("phoneDisplay").innerText = newPhone.slice(-2);
    document.getElementById("phoneInput").value = newPhone;

    // Close modal
    closeModal();
}



