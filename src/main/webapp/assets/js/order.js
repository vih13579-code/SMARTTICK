/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

function showTab(tabId, element) {
    document.querySelectorAll('.tab-link').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.order-list').forEach(order => order.classList.remove('active'));

    element.classList.add('active');
    document.getElementById(tabId).classList.add('active');
}
