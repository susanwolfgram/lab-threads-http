import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		//attemps everything in the try block, in the case of any errors or exceptions, control is passed to the catch block 
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		//object makes a single request to a specified URL
		HttpURLConnection urlConnection = null;
		
		//reads text from a character-input stream
		BufferedReader reader = null;

		String[] movies = null;

		//attemps everything in the try block, in the case of any errors or exceptions, control is passed to the catch block 
		try {

			URL url = new URL(urlString);
			//opens connection to the URL 
			//sends a GET request
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			//InputStream used to read from some input 
			//input is the response from the GET request
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			if (buffer.length() == 0) {
				return null;
			}
			//formats the output to be returned from this method
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n");
		} 
		catch (IOException e) {
			return null;
		} 
		finally {
			//severs the url connection
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}

		return movies;
	}


	public static void main(String[] args) 
	{
		//text scanner which can parse primitive types and strings using regular expressions 
		Scanner sc = new Scanner(System.in);

		boolean searching = true;
		//basic console app which takes in user input and searches the movie database 
		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				//retrieves data
				String[] movies = downloadMovieData(searchTerm);
				//prints retrived data
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
