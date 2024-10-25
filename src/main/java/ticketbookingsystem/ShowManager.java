

/**
 *
 * @author Yzhang & cloud
 */
package ticketbookingsystem;

import java.util.ArrayList;
import java.util.*;
import java.util.List;


public class ShowManager {
    private List<MovieShow> movieShows = new ArrayList<>();

    public void addMovieShow(MovieShow movieShow) {
        movieShows.add(movieShow);
    }

    public List<MovieShow> getAvailableMovieShows() {
        return movieShows;
    }
    
    public Set<String> getUniqueMovies() {
        Set<String> uniqueMovies = new HashSet<>();
        for (MovieShow show : movieShows) {
            uniqueMovies.add(show.getMovieName());
        }
        return uniqueMovies;
    }

    // get all shows from movie id
    public List<MovieShow> getShowtimesForMovie(String movieName) {
        List<MovieShow> showtimes = new ArrayList<>();
        for (MovieShow show : movieShows) {
            if (show.getMovieName().equalsIgnoreCase(movieName)) {
                showtimes.add(show);
            }
        }
        return showtimes;
    }

    // Select a movie show by its unique ID
    public MovieShow selectMovieShowById(int showId) {
        for (MovieShow movieShow : movieShows) {
            if (movieShow.getShowId() == showId) {
                return movieShow;
            }
        }
        return null;  // Return null if no show is found with the given ID
    }
    
    
}

