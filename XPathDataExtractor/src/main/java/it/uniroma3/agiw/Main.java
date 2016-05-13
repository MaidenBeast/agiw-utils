package it.uniroma3.agiw;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {
		Options options = new Options();
		
		options.addOption("t", "task", true, "Il task da far eseguire al programma (Executor o Tester)");
		
		options.addOption("p", "page", true, "Pagina su cui testare l'XPath");
		options.addOption("x", "xpath", true, "XPath da testare");
		
		options.addOption("s", "sources-json", true, "File json di input con dentro gli URL della pagine HTML");
		options.addOption("i", "in-json", true, "File json di input con dentro gli XPath da eseguire");
		options.addOption("o", "out-json", true, "File json di output con i risultati degli XPath");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		
		XPathProgram program = null;
		
		final String PARSE_ERROR = "Usage: [-t|--task] [tester] "
				+ "[-p|--page] <html-page> "
				+ "[-x|--xpath] <xpath> |"
				+ "[-t|--task] [executor] "
				+ "[-s|--source-json] <source-json> "
				+ "[-i|--in-json] <in-json> "
				+ "[-o|--out-json] <out-json>";
		
		try {
			cmd = parser.parse(options, args);
			
			if (cmd.hasOption("t")) {
				String task = cmd.getOptionValue("task");
				
				if (task.equals("tester") && cmd.hasOption("p") && cmd.hasOption("x")) {
					program = new XPathTester(cmd.getOptionValue("page"), cmd.getOptionValue("xpath"));
				} else if (task.equals("executor") && cmd.hasOption("i") && cmd.hasOption("o") && cmd.hasOption("s")) {
					String sourceJson = cmd.getOptionValue("source-json");
					String inJson = cmd.getOptionValue("in-json");
					String outJson = cmd.getOptionValue("out-json");
					program = new XPathDataExtractor(sourceJson, inJson, outJson);
				} else {
					System.err.println(PARSE_ERROR);
					System.exit(1);
				}
					program.execute();
			} else {
				System.err.println(PARSE_ERROR);
				System.exit(1);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
