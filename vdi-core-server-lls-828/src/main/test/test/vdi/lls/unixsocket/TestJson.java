package test.vdi.lls.unixsocket;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.vdi.support.desktop.lls.domain.resource.Host;



public class TestJson {
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		ConfigUtil.loadConfigFileByPath("/test.properties");
		
//		System.out.println(ConfigUtil.getKey("test"));
//		JsonObject json =ParseJSON.getGson().fromJson(ConfigUtil.getKey("test"), JsonObject.class);
//		System.out.println(json);
//		for (Entry<String, JsonElement> string : json.entrySet()) {
//			System.out.println(string.getKey());
//		}
//		JsonParser parser = new JsonParser();
//		JsonElement je=parser.parse(ConfigUtil.getKey("array"));
////		System.out.println(je.toString().charAt(35));
////		je.getAsJsonArray();
//		List<Host> l = new Gson().fromJson(je.toString(),Host.getHostListType());
//		for (Host host : l) {
//		System.out.println(host.getCpuInfo().get(0).getCpuMHz());	
//		}
		ObjectMapper op =new ObjectMapper();
		
		System.out.println(op.readTree(ConfigUtil.getKey("host")).findValue("content"));
		System.out.println(op.readTree(ConfigUtil.getKey("hosts")).findValue("content"));
//		System.out.println(op.re);
//General
		Host h=	op.readValue(ConfigUtil.getKey("test"),Host.class );
		System.out.println(h.getCpuInfo().get(0).getCpuMHz());
		System.out.println(h.getHostIdentity());
	}
	public void parseJson(){
//		org.codehaus.jackson.JsonParser parser= org.codehaus.jackson.JsonParser();
		
	
	}
}
