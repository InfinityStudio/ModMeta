package net.infstudio.modmeta;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import net.infstudio.modmeta.forge.ForgeModASMData;
import net.infstudio.modmeta.forge.ForgeModMetaData;
import net.infstudio.modmeta.forge.ModAnnotationVisitor;
import net.infstudio.modmeta.liteloader.LiteModMetaData;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class DefaultParser
{
	public static final Pattern ZIP_JAR = Pattern.compile("(.+).(zip|jar)$");
	public static final Pattern CLASS_FILE = Pattern.compile("[^\\s$]+(\\$[^\\s]+)?\\.class$");
	public static final Pattern LITE_MOD = Pattern.compile("(.+).(litemod)$");

	public static LiteModMetaData[] parseLiteJson(Path liteModPath) throws IOException
	{
		Path path = validateLiteMod(liteModPath);
		Path json = path.resolve("/litemod.json");
		if (!Files.exists(json)) return new LiteModMetaData[0];
		try (FileChannel open = FileChannel.open(json, StandardOpenOption.READ))
		{
			byte[] bytes = new byte[(int) Files.size(json)];
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			open.read(buffer);
			return new LiteModMetaData[]{JsonIterator.deserialize(bytes,LiteModMetaData.class)};
		}
	}

	private static Path validateLiteMod(Path path) throws IOException
	{
		if (LITE_MOD.matcher(path.getFileName().toString()).matches())
		{
			FileSystem fileSystem = FileSystems.newFileSystem(path, DefaultParser.class.getClassLoader());
			path = fileSystem.getPath("/");
		}
		return path;
	}

	public static ForgeModASMData[] parseFMLASM(Path jarFilePath) throws IOException
	{
		Path path = validateFMLMod(jarFilePath);
		List<ForgeModASMData> asmData = new ArrayList<>();
		Set<Map<String, Object>> set = new HashSet<>();
		List<Path> paths = Files.walk(path).filter(pa -> pa.getFileName() != null &&
				CLASS_FILE.matcher(pa.getFileName().toString()).matches())
				.collect(Collectors.toList());
		ModAnnotationVisitor visitor = new ModAnnotationVisitor(set);
		for (Path classPath : paths)
		{
			set.clear();
			ClassReader reader = new ClassReader(Files.newInputStream(classPath));
			reader.accept(visitor, 0);
			if (set.isEmpty()) continue;
			for (Map<String, Object> map : set)
			{
				String modid = map.get("modid").toString();
				if (Objects.nonNull(modid) && !modid.isEmpty())
					asmData.add(new ForgeModASMData(
							(String) map.getOrDefault("modid", ""),
							(String) map.getOrDefault("name", ""),
							(String) map.getOrDefault("version", ""),
							(String) map.getOrDefault("mcversion", ""),
							(String) map.getOrDefault("certificateFingerprint", ""),
							(String) map.getOrDefault("dependencies", ""),
							(String) map.getOrDefault("acceptableRemoteVersions", ""),
							(String) map.getOrDefault("acceptedMinecraftVersions", ""),
							(String) map.getOrDefault("modLanguage", ""),
							(String) map.getOrDefault("modLanguageAdapter", ""),
							(String) map.getOrDefault("updateJSON", ""),
							(String) map.getOrDefault("guiFactory", ""),
							(String) map.getOrDefault("acceptableSaveVersions", ""),
							(boolean) map.getOrDefault("useMetadata", true),
							(boolean) map.getOrDefault("clientSideOnly", false),
							(boolean) map.getOrDefault("serverSideOnly", false),
							(boolean) map.getOrDefault("canBeDeactivated", false)));
			}
		}
		return asmData.toArray(new ForgeModASMData[asmData.size()]);
	}

	public static ForgeModMetaData[] parseFMLInfoJson(Path jarFilePath) throws IOException
	{
		Path path = validateFMLMod(jarFilePath);
		Path modInf = path.resolve("/mcmod.info");
		if (!Files.exists(modInf)) return new ForgeModMetaData[0];
		try (FileChannel open = FileChannel.open(modInf, StandardOpenOption.READ))
		{
			byte[] bytes = new byte[(int) Files.size(modInf)];
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			open.read(buffer);
			Any any = JsonIterator.deserialize(bytes);
			if (any.get("modListVersion").valueType() != ValueType.INVALID)
				return any.get("modList").asList().stream().map(a -> a.as(ForgeModMetaData.class)).toArray
						(ForgeModMetaData[]::new);
			if (any.valueType() == ValueType.ARRAY)
				return any.asList().stream().map(a -> a.as(ForgeModMetaData.class)).toArray(ForgeModMetaData[]::new);
			if (any.valueType() == ValueType.OBJECT)
				return new ForgeModMetaData[]{any.as(ForgeModMetaData.class)};
			buffer.flip();
			throw new IllegalArgumentException("Unsupported mod info!\n" + Charset.defaultCharset().decode(buffer));
		}
	}

	private static Path validateFMLMod(Path path) throws IOException
	{
		if (ZIP_JAR.matcher(path.getFileName().toString()).matches())
		{
			FileSystem fileSystem = FileSystems.newFileSystem(path, DefaultParser.class.getClassLoader());
			path = fileSystem.getPath("/");
		}
		return path;
	}
}
