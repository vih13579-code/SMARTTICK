/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.Date;

<<<<<<< HEAD
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
=======
public class ProductRating {
    private int rateID;
    private int customerID;
    private int productID;
    private int orderID;
    private Date createdDate;
    private int star;
    private String comment;
    private boolean isDeleted;
    private boolean isRead;
    private String fullName;

>>>>>>> Smarttick_skeleton
    public ProductRating(int star) {
        this.star = star;
    }

<<<<<<< HEAD
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

=======
    public ProductRating(int rateID, int customerID, int productID, int orderID, Date createdDate, int star, String comment, boolean isDeleted, boolean isRead, String fullName) {
>>>>>>> Smarttick_skeleton
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

<<<<<<< HEAD
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

=======
    public ProductRating(int rateID, int customerID, int productID, int orderID, Date createdDate, int star, String comment, boolean isDeleted, boolean isRead) {
>>>>>>> Smarttick_skeleton
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
<<<<<<< HEAD

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
=======
   

    
    public ProductRating() {
    }

>>>>>>> Smarttick_skeleton
    public String getFullName() {
        return fullName;
    }

<<<<<<< HEAD
    /**
     * Sets the customer's full name.
     *
     * @param fullName customer full name
     */
=======
>>>>>>> Smarttick_skeleton
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

<<<<<<< HEAD
    /**
     * Gets the rating ID.
     *
     * @return rating ID
     */
=======
>>>>>>> Smarttick_skeleton
    public int getRateID() {
        return rateID;
    }

<<<<<<< HEAD
    /**
     * Sets the rating ID.
     *
     * @param rateID rating ID
     */
=======
>>>>>>> Smarttick_skeleton
    public void setRateID(int rateID) {
        this.rateID = rateID;
    }

<<<<<<< HEAD
    /**
     * Gets the customer ID.
     *
     * @return customer ID
     */
=======
>>>>>>> Smarttick_skeleton
    public int getCustomerID() {
        return customerID;
    }

<<<<<<< HEAD
    /**
     * Sets the customer ID.
     *
     * @param customerID customer ID
     */
=======
>>>>>>> Smarttick_skeleton
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

<<<<<<< HEAD
    /**
     * Gets the product ID.
     *
     * @return product ID
     */
=======
>>>>>>> Smarttick_skeleton
    public int getProductID() {
        return productID;
    }

<<<<<<< HEAD
    /**
     * Sets the product ID.
     *
     * @param productID product ID
     */
=======
>>>>>>> Smarttick_skeleton
    public void setProductID(int productID) {
        this.productID = productID;
    }

<<<<<<< HEAD
    /**
     * Gets the order ID.
     *
     * @return order ID
     */
=======
>>>>>>> Smarttick_skeleton
    public int getOrderID() {
        return orderID;
    }

<<<<<<< HEAD
    /**
     * Sets the order ID.
     *
     * @param orderID order ID
     */
=======
>>>>>>> Smarttick_skeleton
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

<<<<<<< HEAD
    /**
     * Gets the created date.
     *
     * @return created date
     */
=======
>>>>>>> Smarttick_skeleton
    public Date getCreatedDate() {
        return createdDate;
    }

<<<<<<< HEAD
    /**
     * Sets the created date.
     *
     * @param createdDate created date
     */
=======
>>>>>>> Smarttick_skeleton
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

<<<<<<< HEAD
    /**
     * Gets the star rating.
     *
     * @return star rating
     */
=======
>>>>>>> Smarttick_skeleton
    public int getStar() {
        return star;
    }

<<<<<<< HEAD
    /**
     * Sets the star rating.
     *
     * @param star star rating
     */
=======
>>>>>>> Smarttick_skeleton
    public void setStar(int star) {
        this.star = star;
    }

<<<<<<< HEAD
    /**
     * Gets the customer comment.
     *
     * @return comment
     */
=======
>>>>>>> Smarttick_skeleton
    public String getComment() {
        return comment;
    }

<<<<<<< HEAD
    /**
     * Sets the customer comment.
     *
     * @param comment customer comment
     */
=======
>>>>>>> Smarttick_skeleton
    public void setComment(String comment) {
        this.comment = comment;
    }

<<<<<<< HEAD
    /**
     * Checks whether the rating is deleted.
     *
     * @return true if deleted, otherwise false
     */
=======
>>>>>>> Smarttick_skeleton
    public boolean isIsDeleted() {
        return isDeleted;
    }

<<<<<<< HEAD
    /**
     * Sets the deletion status.
     *
     * @param isDeleted deletion status
     */
=======
>>>>>>> Smarttick_skeleton
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

<<<<<<< HEAD
    /**
     * Checks whether the rating has been read.
     *
     * @return true if read, otherwise false
     */
=======
>>>>>>> Smarttick_skeleton
    public boolean isIsRead() {
        return isRead;
    }

<<<<<<< HEAD
    /**
     * Sets the read status.
     *
     * @param isRead read status
     */
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
=======
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
    
    
}
>>>>>>> Smarttick_skeleton
