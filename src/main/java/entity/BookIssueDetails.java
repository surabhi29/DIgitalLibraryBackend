package entity;

import java.util.Date;

public class BookIssueDetails implements Comparable<BookIssueDetails>{
    private String personName;
    private Date issueDate;
    private Date toDate;
    private String bookName;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public int compareTo(BookIssueDetails bookIssueDetails) {
        return issueDate.compareTo(bookIssueDetails.getIssueDate());
    }

    @Override
    public String toString() {
        return "BookIssueDetails{" +
                "personName='" + personName + '\'' +
                ", issueDate=" + issueDate +
                ", toDate=" + toDate +
                ", bookName='" + bookName + '\'' +
                '}';
    }
}
