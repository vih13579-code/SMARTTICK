package Models;

import java.sql.Timestamp;

public class CustomerNotification {

    private final int replyId;
    private final int productId;
    private final String productName;
    private final String reviewComment;
    private final String answer;
    private final Timestamp reviewDate;
    private final boolean read;

    public CustomerNotification(int replyId, int productId, String productName,
            String reviewComment, String answer, Timestamp reviewDate, boolean read) {
        this.replyId = replyId;
        this.productId = productId;
        this.productName = productName;
        this.reviewComment = reviewComment;
        this.answer = answer;
        this.reviewDate = reviewDate;
        this.read = read;
    }

    public int getReplyId() {
        return replyId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public String getAnswer() {
        return answer;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public boolean isRead() {
        return read;
    }
}
