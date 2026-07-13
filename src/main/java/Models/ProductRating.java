/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.Date;

/**
 * Model class representing a product rating and customer feedback.
 * Stores rating information, customer details, and feedback status.
 *
 * @author TrucBQCE181355
 */
public class ProductRating {

    /** Rating ID. */
    private int rateID;

    /** Customer ID who submitted the rating. */
    private int customerID;

    /** Rated product ID. */
    private int productID;

    /** Related order ID. */
    private int orderID;

    /** Date when the rating was created. */
    private Date createdDate;

    /** Star rating value. */
    private int star;

    /** Customer comment. */
    private String comment;

    /** Indicates whether the rating has been deleted. */
    private boolean isDeleted;

    /** Indicates whether the rating has been read by the administrator. */
    private boolean isRead;

    /** Customer full name. */
    private String fullName;

    /**
     * Constructs a ProductRating object with only the star rating.
     *
     * @param star rating value
     */
    public ProductRating(int star) {
        this.star = star;
    }

    /**
     * Constructs a ProductRating object with all information.
     *
     * @param rateID rating ID
     * @param customerID customer ID
     * @param productID product ID
     * @param orderID order ID
     * @param createdDate rating creation date
     * @param star star rating
     * @param comment customer comment
     * @param isDeleted deletion status
     * @param isRead read status
     * @param fullName customer full name
     */
    public ProductRating(int rateID, int customerID, int productID, int orderID,
            Date createdDate, int star, String comment,
            boolean isDeleted, boolean isRead, String fullName) {

        this.rateID = rateID;
        this.customerID = customerID;
        this.productID = productID;
        this.orderID = orderID;
        this.createdDate = createdDate;
        this.star = star;
        this.comment = comment;
        this.isDeleted = isDeleted;
        this.isRead = isRead;
        this.fullName = fullName;
    }

    /**
     * Constructs a ProductRating object without customer name.
     *
     * @param rateID rating ID
     * @param customerID customer ID
     * @param productID product ID
     * @param orderID order ID
     * @param createdDate rating creation date
     * @param star star rating
     * @param comment customer comment
     * @param isDeleted deletion status
     * @param isRead read status
     */
    public ProductRating(int rateID, int customerID, int productID, int orderID,
            Date createdDate, int star, String comment,
            boolean isDeleted, boolean isRead) {

        this.rateID = rateID;
        this.customerID = customerID;
        this.productID = productID;
        this.orderID = orderID;
        this.createdDate = createdDate;
        this.star = star;
        this.comment = comment;
        this.isDeleted = isDeleted;
        this.isRead = isRead;
    }

    /**
     * Default constructor.
     */
    public ProductRating() {
    }

    /**
     * Gets the customer's full name.
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the customer's full name.
     *
     * @param fullName customer full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the rating ID.
     *
     * @return rating ID
     */
    public int getRateID() {
        return rateID;
    }

    /**
     * Sets the rating ID.
     *
     * @param rateID rating ID
     */
    public void setRateID(int rateID) {
        this.rateID = rateID;
    }

    /**
     * Gets the customer ID.
     *
     * @return customer ID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Sets the customer ID.
     *
     * @param customerID customer ID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * Gets the product ID.
     *
     * @return product ID
     */
    public int getProductID() {
        return productID;
    }

    /**
     * Sets the product ID.
     *
     * @param productID product ID
     */
    public void setProductID(int productID) {
        this.productID = productID;
    }

    /**
     * Gets the order ID.
     *
     * @return order ID
     */
    public int getOrderID() {
        return orderID;
    }

    /**
     * Sets the order ID.
     *
     * @param orderID order ID
     */
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    /**
     * Gets the created date.
     *
     * @return created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the created date.
     *
     * @param createdDate created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets the star rating.
     *
     * @return star rating
     */
    public int getStar() {
        return star;
    }

    /**
     * Sets the star rating.
     *
     * @param star star rating
     */
    public void setStar(int star) {
        this.star = star;
    }

    /**
     * Gets the customer comment.
     *
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the customer comment.
     *
     * @param comment customer comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Checks whether the rating is deleted.
     *
     * @return true if deleted, otherwise false
     */
    public boolean isIsDeleted() {
        return isDeleted;
    }

    /**
     * Sets the deletion status.
     *
     * @param isDeleted deletion status
     */
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * Checks whether the rating has been read.
     *
     * @return true if read, otherwise false
     */
    public boolean isIsRead() {
        return isRead;
    }

    /**
     * Sets the read status.
     *
     * @param isRead read status
     */
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}