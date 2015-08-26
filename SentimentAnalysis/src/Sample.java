import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.parser.ParseException;


public class Sample {

	public static void main(String[] args) throws IOException, ParseException {
//		String url_str="https://graph.facebook.com/search?q=bitsian&type=event&access_token=CAAGHFrXhns4BAMdpnlQiBx1zZCIcKMCFKkOfKaNa7lbmRNiTzvgjiQxbYvxocRbE6fOaXFb4zV48yH6ixYIgl8ZCiLlQ1E2FcNZCopktVClBaT2tnjmZCQaGf7rR7GaZBzjZCSsjSbJkdcDEX96yINzKeUnisPyjd3wtQbsa0UZBObqU6oCZCvtAxWKSnwLW5zZCW0ZAWilgT7ZAsygbR9P3qh6";
		String url_str="https://graph.facebook.com/1454542068184230";
		URL url = new URL(url_str);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		System.out.println(br.readLine());
		
	}
	
}
