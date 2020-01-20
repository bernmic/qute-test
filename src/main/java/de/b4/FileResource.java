package de.b4;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.util.Set;

@Path("/file")
public class FileResource {

    @Inject
    FileService fileService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getFiles() {
        return fileService.getAllFiles();
    }

    @GET
    @Path("/year")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Integer> getYears() {
        return fileService.getYears();
    }

    @GET
    @Path("/year/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Integer> getMonths(@PathParam("year") int year) {
        return fileService.getMonths(year);
    }

    @GET
    @Path("/year/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Integer> getDays(@PathParam("year") int year, @PathParam("month") int month) {
        return fileService.getDays(year, month);
    }

    @GET
    @Path("/year/{year}/{month}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getDaysAsZip(@PathParam("year") int year, @PathParam("month") int month) {
        byte[] b = fileService.getDaysAsZip(year, month);
        System.out.println(b.length);
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.header("Content-Disposition", "attachment;filename=" + String.format("billing-dev-%s-%s.zip", year, month));
        responseBuilder.entity(new ByteArrayInputStream(b));
        return responseBuilder.build();
    }

    @GET
    @Path("/year/{year}/{month}/{day}/download")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDay(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {
        String result = fileService.getDayFile(year, month, day);
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.entity(result);
        return responseBuilder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> create() {
        fileService.createFiles();
        return fileService.getAllFiles();
    }
}
