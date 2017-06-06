package net.infstudio.modmeta;

import com.jsoniter.JsonIterator;
import net.infstudio.modmeta.forge.ForgeModASMData;
import net.infstudio.modmeta.forge.ForgeModMetaData;
import net.infstudio.modmeta.liteloader.LiteModMetaData;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author ci010
 */
public class DefaultParserTest
{
	@Test
	public void testParseMeta() throws URISyntaxException, IOException
	{
		URL resource = DefaultParserTest.class.getResource("/journeymap-1.11-5.4.6.jar");
		Path path = Paths.get(resource.toURI());
		ForgeModMetaData[] data = DefaultParser.parseFMLInfoJson(path);
		System.out.println(Arrays.toString(data));
		path = Paths.get(DefaultParserTest.class.getResource("/FTBLib-1.10.2-3.5.3.jar").toURI());
		data = DefaultParser.parseFMLInfoJson(path);
		System.out.println(Arrays.toString(data));
		path = Paths.get(DefaultParserTest.class.getResource("/appliedenergistics2-rv4-alpha-2.jar").toURI());
		data = DefaultParser.parseFMLInfoJson(path);
		System.out.println(Arrays.toString(data));
	}

	@Test
	public void testParseASM() throws URISyntaxException, IOException
	{
		URL resource = DefaultParserTest.class.getResource("/journeymap-1.11-5.4.6.jar");
		Path path = Paths.get(resource.toURI());
		ForgeModASMData[] data = DefaultParser.parseFMLASM(path);
		System.out.println(Arrays.toString(data));
		path = Paths.get(DefaultParserTest.class.getResource("/FTBLib-1.10.2-3.5.3.jar").toURI());
		data = DefaultParser.parseFMLASM(path);
		System.out.println(Arrays.toString(data));
		path = Paths.get(DefaultParserTest.class.getResource("/appliedenergistics2-rv4-alpha-2.jar").toURI());
		data = DefaultParser.parseFMLASM(path);
		System.out.println(Arrays.toString(data));
	}

	@Test
	public void testParseLite() throws Exception
	{
		URL resource = DefaultParserTest.class.getResource("/betteranimationscollectionrevived2.litemod");
		Path path = Paths.get(resource.toURI());
		LiteModMetaData[] json = DefaultParser.parseLiteJson(path);
		System.out.println(Arrays.toString(json));
	}

}
