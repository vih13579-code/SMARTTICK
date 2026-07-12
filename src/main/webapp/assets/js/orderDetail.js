/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

/* global bootstrap */

function showWarning(show) {
    if (show) {
        var warningModal = new bootstrap.Modal(document.getElementById('updatePopup'));
        warningModal.show();
    }
}
function closePopup() {
    var warningModal = bootstrap.Modal.getInstance(document.getElementById('updatePopup'));
    if (warningModal) {
        warningModal.hide();
    }
}
function confirmDelete() {
    var warningModal = new bootstrap.Modal(document.getElementById('updatePopup'));
    warningModal.show();
    return false;
}
document.getElementById("confirmBtn").onclick = function () {
    document.getElementById("deleteForm").submit();
};
document.getElementById("cancelBtn").onclick = function () {
    document.getElementById("confirmationModal").style.display = "none";
};