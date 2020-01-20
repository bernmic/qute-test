package de.b4;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/")
public class HomeResource {

    @Inject
    Template index;

    @Inject
    Template months;

    @Inject
    FileService fileService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return index.data("items", fileService.getYears());
    }

    @GET
    @Path("months")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance months(@QueryParam("year") int year) {
        return months.data("items", fileService.getMonths(year)).data("year", year);
    }
}
