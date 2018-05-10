package dao;

import common.DBConnection;
import entity.BookDetails;
import entity.BookIssueDetails;
import entity.LocationDetails;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.LibraryServerApp;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class BooksDao {
    private static Log logger = LogFactory.getLog(BooksDao.class);
    private Connection connect= null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;

    public void init() {
        /*call at the start of the application, inorder to store the book details*/
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(LibraryServerApp.getConfiguration().getInputFolderPath()))
        {
            Object obj = parser.parse(reader);
            JSONArray bookDetailsList = (JSONArray) obj;
            bookDetailsList.forEach( bookDetail -> parseBookDetails((JSONObject) bookDetail));

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }

    }

    public void parseBookDetails(JSONObject bookDetail) {
        String bookName = (String) bookDetail.get("BookName");
        String  author = (String) bookDetail.get("Author");
        String isbn = (String) bookDetail.get("ISBN");
        String query = "insert into book_details (book_name, author, isbn, status)"
                + " values (?, ?, ?, ?)";
        try{
            connect = DBConnection.getDBConnection();
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, bookName);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, isbn);
            preparedStatement.setString(4, "Maintenance");
            preparedStatement.execute();

        }catch (SQLException e) {
            logger.error(e.getMessage());
        }finally {
            close();
        }
    }

    /*fetch book details*/
    public List<BookDetails> getBooks() {
        List<BookDetails> bookDetails = null;
        try {
            connect = DBConnection.getDBConnection();
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("select id, book_name, author, isbn, status from book_details");

             bookDetails = writeResultSet(resultSet);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }finally {
            close();
        }

        return bookDetails;
    }

    private List<BookDetails> writeResultSet(ResultSet resultSet) throws SQLException {
        List bookList = new ArrayList<BookDetails>();
        while(resultSet.next()) {
            BookDetails bookDetails = new BookDetails();
            bookDetails.setBookId(resultSet.getInt("id"));
            bookDetails.setAuthor(resultSet.getString("author"));
            bookDetails.setBookName(resultSet.getString("book_name"));
            bookDetails.setIsbn(resultSet.getString("isbn"));
            bookDetails.setStatus(resultSet.getString("status"));
            bookList.add(bookDetails);
        }
        return bookList;
    }

    /*fetch the location of the book*/
    public LocationDetails getLocation(int bookId) {
        LocationDetails locationDetails = null;
        try {
            connect = DBConnection.getDBConnection();
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("select shelf_number, row_number, column_number from location_details where book_id=" + bookId);

            while(resultSet.next()) {
                locationDetails = new LocationDetails();
                locationDetails.setShelfNumber(resultSet.getInt("shelf_number"));
                locationDetails.setRowNumber(resultSet.getInt("row_number"));
                locationDetails.setColumnNumber(resultSet.getInt("column_number"));
            }
        }catch (SQLException e) {
            logger.error("Error ::" + e.getErrorCode() + e.getMessage());
        }finally {
            close();
        }

        return locationDetails;
    }

    /*Updating the status of the selected book to available and its location details*/
    public void updateLocation(int bookId, LocationDetails locationDetails){
        if(locationDetails == null)
            throw new WebApplicationException("Bad data", Response.Status.BAD_REQUEST);

        connect = DBConnection.getDBConnection();

        String query = "insert into location_details (shelf_number, row_number, column_number, book_id)"
                + " values (?, ?, ?, ?)";

        String updateStatusQuery = "update book_details set status='Available' where id=" + bookId;
        try {
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setInt(1, locationDetails.getShelfNumber());
            preparedStatement.setInt(2, locationDetails.getRowNumber());
            preparedStatement.setInt(3, locationDetails.getColumnNumber());
            preparedStatement.setInt(4, bookId);

            preparedStatement.execute();

            preparedStatement = connect.prepareStatement(updateStatusQuery);
            preparedStatement.execute();
        }catch (SQLException e){
            logger.error(e.getMessage());
            throw new WebApplicationException("Duplicate Entry", Response.Status.CONFLICT);
        }finally {
            close();
        }
    }

    /*update the issue details for the selected book, change the status of the book and removed its location*/
    public void updateBookIssueDetails(int bookId, String personName) {

        if(!(bookId > 0 && StringUtils.isNotEmpty(personName)))
            throw new WebApplicationException("Improper Data", Response.Status.BAD_REQUEST);

        String query = " insert into bookIssue_details (name, issue_date, to_date, book_id)"
                + " values (?, ?, ?, ?)";

        String locationDetailsQuery = "delete from location_details where book_id=" + bookId;

        String bookDetailsUpdateQuery = "update book_details set status='Issued' where id=" + bookId;
        try {
            connect = DBConnection.getDBConnection();
            statement = connect.createStatement();
            resultSet = statement
                        .executeQuery("select id from book_details where id="
                            + bookId + "&status='Available'");
            if(!resultSet.next()){
                throw new WebApplicationException("Not Found", Response.Status.NOT_FOUND);
            }
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, personName);
            preparedStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
            preparedStatement.setInt(4, bookId);

            preparedStatement.execute();

            preparedStatement = connect.prepareStatement(locationDetailsQuery);
            preparedStatement.execute();

            preparedStatement = connect.prepareStatement(bookDetailsUpdateQuery);
            preparedStatement.execute();
        }catch (SQLException e){
            logger.error("Error::" + e.getErrorCode() + e.getMessage());
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }finally {
            close();
        }
    }

    /*updating the status of the book to returned*/
    public void updateBookData(int bookId) {
        String bookDetailsUpdateQuery = "update book_details set status='Maintenance' where id=" + bookId;
        try {
            connect = DBConnection.getDBConnection();
            preparedStatement = connect.prepareStatement(bookDetailsUpdateQuery);
            preparedStatement.execute();
        }catch (SQLException e){
            logger.error(e.getMessage());
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }finally {
            close();
        }
    }


    /*fetch issue history of the selected book and sort the result in reverse order of issuedDate*/
    public List<BookIssueDetails> getBookIssueDetails(int bookId) {
        List<BookIssueDetails> bookIssueDetailsList = new ArrayList<>();
        List<BookIssueDetails> sortedList = null;
        BookIssueDetails bookIssueDetails;

        String query ="select book_name, name, issue_date, to_date from bookIssue_details  \n" +
                "inner join book_details on bookIssue_details.book_id = book_details.id where bookIssue_details.book_id=" + bookId;
        try {
            connect = DBConnection.getDBConnection();
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                bookIssueDetails = new BookIssueDetails();
                bookIssueDetails.setBookName(resultSet.getString("book_name"));
                bookIssueDetails.setPersonName(resultSet.getString("name"));
                bookIssueDetails.setIssueDate(resultSet.getDate("issue_date"));
                bookIssueDetails.setToDate(resultSet.getDate("to_date"));
                bookIssueDetailsList.add(bookIssueDetails);
            }

            sortedList = bookIssueDetailsList.stream()
                            .sorted(Comparator.comparing(BookIssueDetails::getIssueDate).reversed())
                            .collect(Collectors.toList());

        }catch (SQLException e) {
            logger.error("error ::" + e.getErrorCode() + e.getMessage());
        }finally {
            close();
        }

        return sortedList;
    }


   /*close connections*/
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}