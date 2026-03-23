package br.com.redhat.bcordeir.services;

import java.util.ArrayList;
import java.util.List;

import br.com.redhat.bcordeir.dto.MovieDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MoviesService {

    private final List<MovieDTO> movies = new ArrayList<>();

    public MoviesService() {
        movies.add(new MovieDTO("The Dark Knight", "Christopher Nolan", 2008));
        movies.add(new MovieDTO("The Dark Knight Rises", "Christopher Nolan", 2012));
        movies.add(new MovieDTO("Inglorious Basterds", "Quentin Tarantino", 2009));
        movies.add(new MovieDTO("Once Upon a Time in Hollywood", "Quentin Tarantino", 2019));
    }

    public List<MovieDTO> getMovies() {
        return movies;
    }

    public MovieDTO addMovie(MovieDTO movie) {
        movies.add(movie);
        return movie;
    }
}
