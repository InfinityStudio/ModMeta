package net.infstudio.modmeta.liteloader;

import java.util.Arrays;

/**
 * @author ci010
 */
public class LiteModMetaData
{
	private String mcversion = "";
	private String name = "", author = "", version = "", description = "", url = "";
	private String revision = "-1";
	private String tweakClass = "";
	private String[] dependsOn = new String[0], requiredAPIs = dependsOn, classTransformerClasses = dependsOn;

	public String getMcversion() {return mcversion;}

	public String getName() {return name;}

	public String getAuthor() {return author;}

	public String getVersion() {return version;}

	public String getDescription() {return description;}

	public String getUrl() {return url;}

	public String getRevision() {return revision;}

	public String getTweakClass() {return tweakClass;}

	public String[] getDependsOn() {return dependsOn;}

	public String[] getRequiredAPIs() {return requiredAPIs;}

	public String[] getClassTransformerClasses() {return classTransformerClasses;}

	@Override
	public String toString()
	{
		return "LiteModMetaData{" +
				"mcversion='" + mcversion + '\'' +
				", name='" + name + '\'' +
				", author='" + author + '\'' +
				", version='" + version + '\'' +
				", description='" + description + '\'' +
				", url='" + url + '\'' +
				", revision='" + revision + '\'' +
				", tweakClass='" + tweakClass + '\'' +
				", dependsOn=" + Arrays.toString(dependsOn) +
				", requiredAPIs=" + Arrays.toString(requiredAPIs) +
				", classTransformerClasses=" + Arrays.toString(classTransformerClasses) +
				'}';
	}
}
