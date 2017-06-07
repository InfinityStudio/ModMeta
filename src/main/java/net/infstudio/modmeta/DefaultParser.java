package net.infstudio.modmeta;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import net.infstudio.modmeta.forge.ForgeModASMData;
import net.infstudio.modmeta.forge.ForgeModData;
import net.infstudio.modmeta.forge.ForgeModMetaData;
import net.infstudio.modmeta.forge.ModAnnotationVisitor;
import net.infstudio.modmeta.liteloader.LiteModMetaData;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
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

	public static LiteModMetaData parseLiteJson(InputStream liteModStream) throws IOException
	{
		Objects.requireNonNull(liteModStream);

		JarInputStream jarInputStream = liteModStream instanceof JarInputStream ? (JarInputStream) liteModStream : new JarInputStream(liteModStream);

		boolean found = false;
		for (JarEntry entry = jarInputStream.getNextJarEntry(); entry != null; entry = jarInputStream.getNextJarEntry())
			if (entry.getName().endsWith("litemod.json"))
			{
				found = true;
				break;
			}
		if (!found) return null;
		try (ReadableByteChannel channel = Channels.newChannel(jarInputStream))
		{
			ByteBuffer buffer = ByteBuffer.allocate(5120);
			channel.read(buffer);
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			return JsonIterator.deserialize(bytes, LiteModMetaData.class);
		}
	}

	public static LiteModMetaData parseLiteJson(Path liteModPath) throws IOException
	{
		Objects.requireNonNull(liteModPath);

		Path path = validateLiteMod(liteModPath);
		Path json = path.resolve("/litemod.json");
		if (!Files.exists(json)) return null;
		try (FileChannel open = FileChannel.open(json, StandardOpenOption.READ))
		{
			byte[] bytes = new byte[(int) Files.size(json)];
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			open.read(buffer);
			return JsonIterator.deserialize(bytes, LiteModMetaData.class);
		}
	}

	public static ForgeModData[] parseFMLAll(InputStream jarFileStream) throws IOException
	{
		Objects.requireNonNull(jarFileStream);

		List<ForgeModData> asmData = new ArrayList<>();
		Set<Map<String, Object>> set = new HashSet<>();
		ModAnnotationVisitor visitor = new ModAnnotationVisitor(set);

		JarInputStream jarInputStream = jarFileStream instanceof JarInputStream ? (JarInputStream) jarFileStream : new JarInputStream(jarFileStream);
		for (JarEntry entry = jarInputStream.getNextJarEntry(); entry != null; entry = jarInputStream.getNextJarEntry())
			if (CLASS_FILE.matcher(entry.getName()).matches())
				visitClass(set, asmData, visitor, jarInputStream);
			else if (entry.getName().endsWith("mcmod.info"))
			{
				ByteBuffer buffer = ByteBuffer.allocate(5120);
				ReadableByteChannel channel = Channels.newChannel(jarInputStream);
				channel.read(buffer);
				buffer.flip();
				byte[] bytes = new byte[buffer.limit()];
				buffer.get(bytes);
				Any any = JsonIterator.deserialize(bytes);
				if (any.get("modListVersion").valueType() != ValueType.INVALID)
					any.get("modList").asList().stream().map(a -> a.as(ForgeModMetaData.class)).forEach
							(asmData::add);
				if (any.valueType() == ValueType.ARRAY)
					any.asList().stream().map(a -> a.as(ForgeModMetaData.class)).forEach(asmData::add);
				if (any.valueType() == ValueType.OBJECT)
					asmData.add(any.as(ForgeModMetaData.class));
			}
		return asmData.toArray(new ForgeModData[asmData.size()]);
	}

	public static ForgeModASMData[] parseFMLASM(InputStream jarFileStream) throws IOException
	{
		Objects.requireNonNull(jarFileStream);

		List<ForgeModData> asmData = new ArrayList<>();
		Set<Map<String, Object>> set = new HashSet<>();
		ModAnnotationVisitor visitor = new ModAnnotationVisitor(set);

		JarInputStream jarInputStream = jarFileStream instanceof JarInputStream ? (JarInputStream) jarFileStream : new JarInputStream(jarFileStream);
		for (JarEntry entry = jarInputStream.getNextJarEntry(); entry != null; entry = jarInputStream.getNextJarEntry())
			if (CLASS_FILE.matcher(entry.getName()).matches())
				visitClass(set, asmData, visitor, jarInputStream);
		return asmData.toArray(new ForgeModASMData[asmData.size()]);
	}

	public static ForgeModASMData[] parseFMLASM(Path jarFilePath) throws IOException
	{
		Objects.requireNonNull(jarFilePath);

		List<ForgeModData> asmData = new ArrayList<>();
		Set<Map<String, Object>> set = new HashSet<>();
		ModAnnotationVisitor visitor = new ModAnnotationVisitor(set);

		Path path = validateFMLMod(jarFilePath);
		List<Path> paths = Files.walk(path).filter(pa -> pa.getFileName() != null &&
				CLASS_FILE.matcher(pa.getFileName().toString()).matches()).collect(Collectors.toList());
		for (Path classPath : paths)
			visitClass(set, asmData, visitor, Files.newInputStream(classPath));
		return asmData.toArray(new ForgeModASMData[asmData.size()]);
	}

	public static ForgeModMetaData[] parseFMLInfoJson(InputStream stream) throws IOException
	{
		Objects.requireNonNull(stream);

		boolean found = false;
		JarInputStream jarInputStream = stream instanceof JarInputStream ? (JarInputStream) stream : new JarInputStream(stream);
		for (JarEntry entry = jarInputStream.getNextJarEntry(); entry != null; entry = jarInputStream.getNextJarEntry())
			if (entry.getName().endsWith("mcmod.info"))
			{
				found = true;
				break;
			}
		if (!found) return new ForgeModMetaData[0];
		ByteBuffer buffer = ByteBuffer.allocate(5120);
		try (ReadableByteChannel channel = Channels.newChannel(jarInputStream))
		{
			channel.read(buffer);
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
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

	private static Path validateLiteMod(Path path) throws IOException
	{
		if (LITE_MOD.matcher(path.getFileName().toString()).matches())
		{
			FileSystem fileSystem = FileSystems.newFileSystem(path, DefaultParser.class.getClassLoader());
			path = fileSystem.getPath("/");
		}
		return path;
	}

	private static void visitClass(Set<Map<String, Object>> set,
								   List<ForgeModData> asmData,
								   ModAnnotationVisitor visitor,
								   InputStream stream) throws IOException
	{
		set.clear();
		ClassReader reader = new ClassReader(stream);
		reader.accept(visitor, 0);
		if (set.isEmpty()) return;
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
}
