
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class CreateJsonRead {

	public JSONObject createJsonObect(Review review) {
		JSONObject obj = new JSONObject();
		obj.put("name", review.getName());
		obj.put("date", review.getDate());
		obj.put("comment", review.getReviewComment());
		obj.put("stars", review.getRating());
		
		return obj;
	}

	public void writeToFile(JSONObject results, String location) {
		try {

			FileWriter file = new FileWriter(location);
				file.write(results.toJSONString());
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
