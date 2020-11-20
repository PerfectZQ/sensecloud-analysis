package sensecloud.connector.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;


public class TextRenderer {
	
	private static final String FILE_ENGINE = "file.engine";
	private static final String CLASSPATH_ENGINE = "classpath.engine";
	private static final String STRING_ENGINE = "string.engine";
	
	private Map<String, PebbleEngine> engines = new HashMap();
	
	public TextRenderer() {
		engines.put(FILE_ENGINE, new PebbleEngine.Builder().build());
		engines.put(CLASSPATH_ENGINE, new PebbleEngine.Builder().loader(new ClasspathLoader(TextRenderer.class.getClassLoader())).build());
		engines.put(STRING_ENGINE, new PebbleEngine.Builder().loader(new StringLoader()).build());
	}
	
	private PebbleEngine getEngine(String engineType) {
		return engines.get(engineType);
	}

	public String render(String tpl, Map<String, Object> context) {
		Writer writer = new StringWriter();
		try {
			PebbleTemplate compiledTemplate = getEngine(FILE_ENGINE).getTemplate(tpl);
			compiledTemplate.evaluate(writer, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public void render(String tpl, Map<String, Object> context, String outputFile) {
		try {
			PebbleTemplate compiledTemplate = getEngine(FILE_ENGINE).getTemplate(tpl);
			Writer writer = new FileWriter(new File(outputFile));
			compiledTemplate.evaluate(writer, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String renderFromClasspath(String tpl, Map<String, Object> context) {
		Writer writer = new StringWriter();
		try {
			PebbleTemplate compiledTemplate = getEngine(CLASSPATH_ENGINE).getTemplate(tpl);
			compiledTemplate.evaluate(writer, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
	
	public String renderStringTemplate(String tpl, Map<String, Object> context) {
		Writer writer = new StringWriter();
		try {
			PebbleTemplate compiledTemplate = getEngine(STRING_ENGINE).getTemplate(tpl);
			compiledTemplate.evaluate(writer, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
}
