/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

public class RatingReplies {
    private int replyID;
    private int employeeID;
    private int rateID;
    private String answer;
    private boolean isRead;

    public RatingReplies() {
    }

    public RatingReplies(int replyID, int employeeID, int rateID, String answer, boolean isRead) {
        this.replyID = replyID;
        this.employeeID = employeeID;
        this.rateID = rateID;
        this.answer = answer;
        this.isRead = isRead;
    }
    
    public int getReplyID() {
        return replyID;
    }

    public void setReplyID(int replyID) {
        this.replyID = replyID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getRateID() {
        return rateID;
    }

    public void setRateID(int rateID) {
        this.rateID = rateID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
    
}
