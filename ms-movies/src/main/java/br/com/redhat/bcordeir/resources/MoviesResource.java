package br.com.redhat.bcordeir.resources;

import br.com.redhat.bcordeir.services.MoviesService;
import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.com.redhat.bcordeir.dto.MovieDTO;

@Path("/movies")
public class MoviesResource {

    @Inject
    MoviesService moviesService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response getMovies() {
        Log.info("Getting movies");
        return Response.ok(moviesService.getMovies()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response createMovie(MovieDTO movie) {
        Log.info("Creating movie: " + movie);
        return Response.status(Response.Status.CREATED).entity(moviesService.addMovie(movie)).build();
    }
}
