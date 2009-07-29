package org.webosinternals.service.accel;

import com.palm.luna.LSException;
import com.palm.luna.service.LunaServiceThread;
import com.palm.luna.service.ServiceMessage;
import com.palm.luna.message.ErrorMessage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class AccelService extends LunaServiceThread {
	private final String POLL_INTERVAL_PATH = "/sys/class/input/input5/poll_interval";

    @LunaServiceThread.PublicMethod
    public void setUpdateInterval(ServiceMessage msg)
    throws JSONException, LSException {
        if (msg.getJSONPayload().has("value")) {
            String value = msg.getJSONPayload().getString("value");
            if (Double.valueOf(value) <= 0.01) {
				value = "10";
			}
			else if (Double.valueOf(value) >= 1) {
				value = "1000";
			}
			else {
				value = Integer.toString((int)(Double.valueOf(value) * 1000));
			}
			
            FileWriter fw;
			BufferedWriter output;
			try {
				fw = new FileWriter(POLL_INTERVAL_PATH, false);
				output = new BufferedWriter(fw);
				output.write(value);
				output.newLine();
				output.close();
				fw.close();
			} catch (IOException e) {
				msg.respondError(ErrorMessage.ERROR_CODE_METHOD_EXCEPTION, "Failed to set the update interval.");
				return;
			}
			
    		JSONObject reply = new JSONObject();
    		reply.put("success", true);
    		msg.respond(reply.toString());
    		return;
        }
    }
    
    @LunaServiceThread.PublicMethod
    public void getUpdateInterval(ServiceMessage msg)
    throws JSONException, LSException {
    	BufferedReader input;
		FileReader fr;
		String value = null;
		try {
			fr = new FileReader(POLL_INTERVAL_PATH);
			input = new BufferedReader(fr);
			value = input.readLine();
			input.close();
			fr.close();
		} catch (IOException e) {
			msg.respondError(ErrorMessage.ERROR_CODE_METHOD_EXCEPTION, "Failed to get the update interval.");
			return;
		}	
    	
		JSONObject reply = new JSONObject();
		reply.put("value", value);
		msg.respond(reply.toString());
		return;
    }
}