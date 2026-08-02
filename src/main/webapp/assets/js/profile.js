/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */





function previewImage(event) {
    if (!event.target.files || !event.target.files[0]) {
        return;
    }
    var reader = new FileReader();
    reader.onload = function () {
        var output = document.getElementById('avatarPreview');
        output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
}



