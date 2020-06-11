package ERC;
import java.util.ArrayList;

public class ERCHelper {
	
	public static void handleExitCode(ArrayList<Finding> findings, Settings settings) {
		if(settings.isExitCode()) {
			int amountFindings = findings.size()>127?127:findings.size();
			System.exit(amountFindings);
		}
	}
	
	static void processResults(StringBuilder output, ArrayList<Finding> findings, ArrayList<String> errors, Settings settings) {
		if(settings.isJsonOutput()) {
			output.append("\"findings\": [");
			for (Finding touple : findings) {
				output.append("{");
				StringHelper.builderAppendKeyValue(output,touple.getSource(), touple.getFinding());
				output.append("},");
			}
			output.append("],");
			output.append("\"errors\": [");
			for (String error : errors) {
				output.append("{");
				output.append("\""+error+"\"");
				output.append("},");
			}
			output.append("],");
			output.append("}");
			System.out.println(output.toString());
		}else{
			for (String error : errors) {
				System.out.println("!- "+error);
			}
			System.out.println("\r\nDone.");
		}
	}
	
	static void runInfo(String url, StringBuilder jsonOutputB, Settings settings, String res) {
		if(settings.isJsonOutput()) {
			StringHelper.builderAppendKeyValue(jsonOutputB,"taskRunning",url);
		}else{
			String header ="ERC running running for - "+url+" ("+res.length()+") "+(settings.getProxyObj()==null?"":" -> "+settings.getProxyString());
			System.out.print(header+"\r\n");
			System.out.print(StringHelper.repeat("*", header.length())+"\r\n");			
			if(settings.isDebug()) {
				int maxLength = res.length()<1000?res.length():1000;
				System.out.println("Debug Output:"+res.substring(0,maxLength));
			}
		}
	}
}
