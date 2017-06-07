package net.infstudio.modmeta.forge;


import java.util.Arrays;

/**
 * @author ci010
 */
public class ForgeModMetaData implements ForgeModData
{
	private String modid = "",
			name = "",
			description = "",
			version = "";

	private String url = "",
			updateUrl = "",
			updateJSON = "";

	private String logoFile = "",
			credits = "",
			parent = "";

	private String[] screenshots = new String[0],
			authorList = new String[0];

	@Override
	public String getModid()
	{
		return modid;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getUrl()
	{
		return url;
	}

	public String getUpdateUrl()
	{
		return updateUrl;
	}

	public String getUpdateJSON()
	{
		return updateJSON;
	}

	public String getLogoFile()
	{
		return logoFile;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	public String getCredits()
	{
		return credits;
	}

	public String getParent()
	{
		return parent;
	}

	public String[] getScreenshots()
	{
		return screenshots;
	}

	public String[] getAuthorList()
	{
		return authorList;
	}

	@Override
	public String toString()
	{
		return "ForgeModMetaData{" +
				"modid='" + modid + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", url='" + url + '\'' +
				", updateUrl='" + updateUrl + '\'' +
				", updateJSON='" + updateJSON + '\'' +
				", logoFile='" + logoFile + '\'' +
				", version='" + version + '\'' +
				", credits='" + credits + '\'' +
				", parent='" + parent + '\'' +
				", screenshots=" + Arrays.toString(screenshots) +
				", authorList=" + Arrays.toString(authorList) +
				'}';
	}
}
