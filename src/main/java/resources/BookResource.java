package resources;

import dao.BooksDao;
import entity.BookDetails;
import entity.BookIssueDetails;
import entity.LocationDetails;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Singleton
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

    private BooksDao booksDao = new BooksDao();

    @GET
    public Response getBooks() {
        List<BookDetails> books = booksDao.getBooks();
        if(books != null)
            return Response.status(Response.Status.OK).entity(books).build();
        else
            return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{id}/location")
    public Response getBookLocation(@PathParam("id") int bookID) {
        LocationDetails locationDetails = booksDao.getLocation(bookID);
        if(locationDetails != null)
            return Response.status(Response.Status.OK).entity(locationDetails).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}/location")
    public Response updateBookLocation(@PathParam("id") int bookID, LocationDetails location) {
        booksDao.updateLocation(bookID, location);
        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("/{id}/issue")
    public Response updateBooks(@PathParam("id") int bookId){
        booksDao.updateBookData(bookId);

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/{id}/issue")
    public Response updateBookIssueDetails(@PathParam("id") int bookId, @QueryParam("name") String personName) {
        booksDao.updateBookIssueDetails(bookId, personName);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/{id}/issue-history")
    public Response getBookIssueHistory(@PathParam("id") int bookId) {
        List<BookIssueDetails> bookIssueDetails = booksDao.getBookIssueDetails(bookId);
        if(bookIssueDetails!=null)
            return Response.status(Response.Status.OK).entity(bookIssueDetails).build();
        else
            return Response.status(Response.Status.NO_CONTENT).build();
    }
}
