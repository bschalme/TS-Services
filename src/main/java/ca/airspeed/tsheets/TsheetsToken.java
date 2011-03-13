/*
 * Copyright 2011 Airspeed Consulting

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ca.airspeed.tsheets;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

public class TsheetsToken {

	private static Logger logger = LoggerFactory.getLogger(TsheetsToken.class);

	public void go() {
		Client c = Client.create();
		c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		WebResource r = c
				.resource("");
		String response = r.accept(MediaType.APPLICATION_JSON_TYPE).get(
				String.class);
		logger.info(response);
		JSONObject jsonObject = JSONObject.fromObject(response);
		Object bean = JSONObject.toBean(jsonObject);
		String token = null;
		try {
			token = ((String) PropertyUtils.getProperty(bean, "token"));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(token);
		String resource = "https://airspeed.tsheets.com/api.php?action=logout&token={0}&output_format=json";
		r = c.resource(MessageFormat.format(resource, token));
		response = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		logger.info(response);
	}

	public static void main(String[] args) {
		TsheetsToken tsheets = new TsheetsToken();
		tsheets.go();
	}

}
