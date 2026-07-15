/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author TranVTH
 */
public class Address {

    private int addressID;
    private int customerID;
    private int isDefault;
    private String addressDetails;

    public Address() {
    }

    public Address(int customerID, int isDefault, String addressDetails) {
        this.customerID = customerID;
        this.isDefault = isDefault;
        this.addressDetails = addressDetails;
    }

    public Address(int addressID, int customerID, int isDefault, String addressDetails) {
        this.addressID = addressID;
        this.customerID = customerID;
        this.isDefault = isDefault;
        this.addressDetails = addressDetails;
    }

    public Address(int addressID, String addressDetails) {
        this.addressID = addressID;
        this.addressDetails = addressDetails;
    }

    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getAddressDetails() {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        this.addressDetails = addressDetails;
    }

}
