/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 * Model class representing an employee reply to a customer rating.
 * Stores reply information and read status.
 *
 * @author TrucBQCE181355
 */
public class RatingReplies {

    /** Reply ID. */
    private int replyID;

    /** Employee ID who submitted the reply. */
    private int employeeID;

    /** Related rating ID. */
    private int rateID;

    /** Reply content. */
    private String answer;

    /** Indicates whether the reply has been read. */
    private boolean isRead;

    /**
     * Default constructor.
     */
    public RatingReplies() {
    }

    /**
     * Constructs a RatingReplies object with all attributes.
     *
     * @param replyID reply ID
     * @param employeeID employee ID
     * @param rateID rating ID
     * @param answer reply content
     * @param isRead read status
     */
    public RatingReplies(int replyID, int employeeID, int rateID, String answer, boolean isRead) {
        this.replyID = replyID;
        this.employeeID = employeeID;
        this.rateID = rateID;
        this.answer = answer;
        this.isRead = isRead;
    }

    /**
     * Gets the reply ID.
     *
     * @return reply ID
     */
    public int getReplyID() {
        return replyID;
    }

    /**
     * Sets the reply ID.
     *
     * @param replyID reply ID
     */
    public void setReplyID(int replyID) {
        this.replyID = replyID;
    }

    /**
     * Gets the employee ID.
     *
     * @return employee ID
     */
    public int getEmployeeID() {
        return employeeID;
    }

    /**
     * Sets the employee ID.
     *
     * @param employeeID employee ID
     */
    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    /**
     * Gets the related rating ID.
     *
     * @return rating ID
     */
    public int getRateID() {
        return rateID;
    }

    /**
     * Sets the related rating ID.
     *
     * @param rateID rating ID
     */
    public void setRateID(int rateID) {
        this.rateID = rateID;
    }

    /**
     * Gets the reply content.
     *
     * @return reply content
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the reply content.
     *
     * @param answer reply content
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Checks whether the reply has been read.
     *
     * @return true if read; otherwise false
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