package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.lax1dude.eaglercraft.AssetRepository;
import net.lax1dude.eaglercraft.EPK2Compiler;
import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.adapter.SimpleStorage;
import net.minecraft.client.Minecraft;

public class TexturePackList {
	/**
	 * An instance of TexturePackDefault for the always available builtin texture
	 * pack.
	 */
	public static final ITexturePack defaultTexturePack = new TexturePackDefault();

	/** The Minecraft instance. */
	private final Minecraft mc;

	/** The directory the texture packs will be loaded from. */
	//private final File texturePackDir;

	/** Folder for the multi-player texturepacks. Returns File. */
	//private final File mpTexturePackFolder;

	/** The list of the available texture packs. */
	private List availableTexturePacks = new ArrayList();

	/**
	 * A mapping of texture IDs to TexturePackBase objects used by
	 * updateAvaliableTexturePacks() to avoid reloading texture packs that haven't
	 * changed on disk.
	 */
	private Map texturePackCache = new HashMap();

	/** The TexturePack that will be used. */
	public ITexturePack selectedTexturePack;

	/** True if a texture pack is downloading in the background. */
	private boolean isDownloading;

	public TexturePackList(Minecraft par2Minecraft) {
		this.mc = par2Minecraft;
		//this.texturePackDir = new File(par1File, "texturepacks");
		//this.mpTexturePackFolder = new File(par1File, "texturepacks-mp-cache");
		this.updateAvaliableTexturePacks();
	}

	/**
	 * Sets the new TexturePack to be used, returning true if it has actually
	 * changed, false if nothing changed.
	 */
	public boolean setTexturePack(ITexturePack par1ITexturePack) {
		if (par1ITexturePack == this.selectedTexturePack) {
			return false;
		} else {
			this.isDownloading = false;
			this.selectedTexturePack = par1ITexturePack;
			try {
				AssetRepository.reset();
				AssetRepository.install(SimpleStorage.get(this.selectedTexturePack.getTexturePackFileName()));
			} catch (IOException ignored) {}
			this.mc.gameSettings.skin = par1ITexturePack.getTexturePackFileName();
			this.mc.gameSettings.saveOptions();
			return true;
		}
	}

	/**
	 * filename must end in .zip or .epk
	 */
	public void requestDownloadOfTexture(String par1Str) {
		String var2 = par1Str.substring(par1Str.lastIndexOf("/") + 1);

		if (var2.contains("?")) {
			var2 = var2.substring(0, var2.indexOf("?"));
		}

		if (var2.toLowerCase().endsWith(".zip") || var2.toLowerCase().endsWith(".epk")) {
			this.downloadTexture(par1Str, var2);
		}
	}

	private void downloadTexture(String par1Str, String par2File) {
		this.isDownloading = true;
		SimpleStorage.set(par2File.replaceAll("[^A-Za-z0-9_]", "_"), par2File.toLowerCase().endsWith(".zip") ? zipToEpk(EaglerAdapter.downloadURL(par1Str)) : EaglerAdapter.downloadURL(par1Str));
		this.onDownloadFinished();
	}

	/**
	 * Return true if a texture pack is downloading in the background.
	 */
	public boolean getIsDownloading() {
		return this.isDownloading;
	}

	/**
	 * Called from Minecraft.loadWorld() if getIsDownloading() returned true to
	 * prepare the downloaded texture for usage.
	 */
	public void onDownloadFinished() {
		this.isDownloading = false;
		this.updateAvaliableTexturePacks();
		this.mc.scheduleTexturePackRefresh();
	}

	/**
	 * check the texture packs the client has installed
	 */
	public void updateAvaliableTexturePacks() {
		ArrayList var1 = new ArrayList();
		var1.add(defaultTexturePack);
		Iterator var2 = this.getTexturePackDirContents().iterator();

		while (var2.hasNext()) {
			String var3 = (String) var2.next();

			Object var5 = (ITexturePack) this.texturePackCache.get(var3);

			if (var5 == null) {
				try {
					var5 = new TexturePackCustom(var3, defaultTexturePack);
					this.texturePackCache.put(var3, var5);
				} catch (RuntimeException e) {
					e.printStackTrace(); // bad texture pack
				}
			}

			if (((ITexturePack) var5).getTexturePackFileName().equals(this.mc.gameSettings.skin)) {
				this.selectedTexturePack = (ITexturePack) var5;
				try {
					AssetRepository.reset();
					AssetRepository.install(SimpleStorage.get(this.selectedTexturePack.getTexturePackFileName()));
				} catch (IOException ignored) {}
			}

			var1.add(var5);
		}

		this.availableTexturePacks.removeAll(var1);
		var2 = this.availableTexturePacks.iterator();

		while (var2.hasNext()) {
			ITexturePack var6 = (ITexturePack) var2.next();
			var6.deleteTexturePack(this.mc.renderEngine);
			this.texturePackCache.remove(var6.getTexturePackID());
		}

		this.availableTexturePacks = var1;
	}

	/**
	 * Generate an internal texture pack ID from the file/directory name, last
	 * modification time, and file size. Returns null if the file/directory is not a
	 * texture pack.
	 */
//	private String generateTexturePackID(File par1File) {
//		return par1File.isFile() && par1File.getName().toLowerCase().endsWith(".zip") ? par1File.getName() + ":" + par1File.length() + ":" + par1File.lastModified()
//				: (par1File.isDirectory() && (new File(par1File, "pack.txt")).exists() ? par1File.getName() + ":folder:" + par1File.lastModified() : null);
//	}

	/**
	 * Return a List<String> of file/directories in the texture pack directory.
	 */
	private List getTexturePackDirContents() {
		return SimpleStorage.isAvailable() ? Arrays.asList(SimpleStorage.list()) : Collections.emptyList();
	}

	/**
	 * Returns a list of the available texture packs.
	 */
	public List availableTexturePacks() {
		return Collections.unmodifiableList(this.availableTexturePacks);
	}

	public ITexturePack getSelectedTexturePack() {
		if (this.selectedTexturePack == null) {
			this.selectedTexturePack = defaultTexturePack;
		}
		return this.selectedTexturePack;
	}

	public boolean func_77300_f() {
		if (!this.mc.gameSettings.serverTextures) {
			return false;
		} else {
			ServerData var1 = this.mc.getServerData();
			return var1 == null ? true : var1.func_78840_c();
		}
	}

	public boolean getAcceptsTextures() {
		if (!this.mc.gameSettings.serverTextures) {
			return false;
		} else {
			ServerData var1 = this.mc.getServerData();
			return var1 == null ? false : var1.getAcceptsTextures();
		}
	}

	static boolean isDownloading(TexturePackList par0TexturePackList) {
		return par0TexturePackList.isDownloading;
	}

	/**
	 * Set the selectedTexturePack field (Inner class static accessor method).
	 */
	static ITexturePack setSelectedTexturePack(TexturePackList par0TexturePackList, ITexturePack par1ITexturePack) {
		return par0TexturePackList.selectedTexturePack = par1ITexturePack;
	}
/*
	/**
	 * Generate an internal texture pack ID from the file/directory name, last
	 * modification time, and file size. Returns null if the file/directory is not a
	 * texture pack. (Inner class static accessor method).
	 
	static String generateTexturePackID(TexturePackList par0TexturePackList, File par1File) {
		return par0TexturePackList.generateTexturePackID(par1File);
	}
*/
	static ITexturePack func_98143_h() {
		return defaultTexturePack;
	}

	static Minecraft getMinecraft(TexturePackList par0TexturePackList) {
		return par0TexturePackList.mc;
	}

	public static final byte[] zipToEpk(byte[] in) {
		try {
			EPK2Compiler epk2Compiler = new EPK2Compiler();
			try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(in))) {
				ZipEntry zipEntry;
				byte[] bb = new byte[16000];
				while ((zipEntry = zis.getNextEntry()) != null) {
					if (zipEntry.isDirectory()) continue;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int len;
					while ((len = zis.read(bb)) != -1) {
						baos.write(bb, 0, len);
					}
					baos.close();
					epk2Compiler.append(zipEntry.getName(), baos.toByteArray());
				}
			}
			return epk2Compiler.complete();
		} catch (IOException e) {
			e.printStackTrace();
			return in;
		}
	}
}
