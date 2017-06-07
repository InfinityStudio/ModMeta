package net.infstudio.modmeta.forge;

/**
 * @author ci010
 */
public class ForgeModASMData implements ForgeModData
{
	private String
			modid = "",
			name = "",
			version = "",
			mcversion = "",
			certificateFingerprint = "",
			dependencies = "",
			acceptableRemoteVersions = "",
			acceptedMinecraftVersions = "",
			modLanguage = "",
			modLanguageAdapter = "",
			updateJSON = "",
			guiFactory = "",
			acceptableSaveVersions = "";
	private boolean useMetadata, clientSideOnly, serverSideOnly, canBeDeactivated;

	public ForgeModASMData(String modid, String name, String version,
						   String mcversion, String certificateFingerprint, String dependencies,
						   String acceptableRemoteVersions, String acceptedMinecraftVersions, String modLanguage,
						   String modLanguageAdapter, String updateJSON, String guiFactory, String acceptableSaveVersions,
						   boolean useMetadata, boolean clientSideOnly, boolean serverSideOnly,
						   boolean canBeDeactivated)
	{
		this.modid = modid;
		this.name = name;
		this.version = version;
		this.mcversion = mcversion;
		this.certificateFingerprint = certificateFingerprint;
		this.dependencies = dependencies;
		this.acceptableRemoteVersions = acceptableRemoteVersions;
		this.acceptedMinecraftVersions = acceptedMinecraftVersions;
		this.modLanguage = modLanguage;
		this.modLanguageAdapter = modLanguageAdapter;
		this.updateJSON = updateJSON;
		this.guiFactory = guiFactory;
		this.acceptableSaveVersions = acceptableSaveVersions;
		this.useMetadata = useMetadata;
		this.clientSideOnly = clientSideOnly;
		this.serverSideOnly = serverSideOnly;
		this.canBeDeactivated = canBeDeactivated;
	}

	public String getModid()
	{
		return modid;
	}

	public String getName()
	{
		return name;
	}

	public String getVersion()
	{
		return version;
	}

	public String getMcversion()
	{
		return mcversion;
	}

	public String getCertificateFingerprint()
	{
		return certificateFingerprint;
	}

	public String getDependencies()
	{
		return dependencies;
	}

	public String getAcceptableRemoteVersions()
	{
		return acceptableRemoteVersions;
	}

	public String getAcceptedMinecraftVersions()
	{
		return acceptedMinecraftVersions;
	}

	public String getModLanguage()
	{
		return modLanguage;
	}

	public String getModLanguageAdapter()
	{
		return modLanguageAdapter;
	}

	public String getUpdateJSON()
	{
		return updateJSON;
	}

	public String getGuiFactory()
	{
		return guiFactory;
	}

	public String getAcceptableSaveVersions()
	{
		return acceptableSaveVersions;
	}

	public boolean isUseMetadata()
	{
		return useMetadata;
	}

	public boolean isClientSideOnly()
	{
		return clientSideOnly;
	}

	public boolean isServerSideOnly()
	{
		return serverSideOnly;
	}

	public boolean isCanBeDeactivated()
	{
		return canBeDeactivated;
	}

	@Override
	public String toString()
	{
		return "ForgeModASMData{" +
				"modid='" + modid + '\'' +
				", name='" + name + '\'' +
				", version='" + version + '\'' +
				", mcversion='" + mcversion + '\'' +
				", certificateFingerprint='" + certificateFingerprint + '\'' +
				", dependencies='" + dependencies + '\'' +
				", acceptableRemoteVersions='" + acceptableRemoteVersions + '\'' +
				", acceptedMinecraftVersions='" + acceptedMinecraftVersions + '\'' +
				", modLanguage='" + modLanguage + '\'' +
				", modLanguageAdapter='" + modLanguageAdapter + '\'' +
				", updateJSON='" + updateJSON + '\'' +
				", guiFactory='" + guiFactory + '\'' +
				", acceptableSaveVersions='" + acceptableSaveVersions + '\'' +
				", useMetadata=" + useMetadata +
				", clientSideOnly=" + clientSideOnly +
				", serverSideOnly=" + serverSideOnly +
				", canBeDeactivated=" + canBeDeactivated +
				'}';
	}
}
