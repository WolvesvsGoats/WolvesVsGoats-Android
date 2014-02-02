/**
 * Wolves Vs Goats by André Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * 
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * 
 * Creative Commons is a non-profit organization.
 * 
 * @author André Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.localization.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osmdroid.tileprovider.IMapTileProviderCallback;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class MapTileProviderAssets extends MapTileProviderArray implements
		IMapTileProviderCallback {

	private static final String LOG_TAG = "MapTileProviderAssets";

	private static final String ASSETS_MAP_DIRECTORY = "map";
	private static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath();
	private static final String OSMDROID_MAP_FILE_SOURCE_DIRECTORY = "osmdroid";
	private static final String OSMDROID_MAP_FILE_SOURCE_DIRECTORY_PATH = SDCARD_PATH
			+ "/" + OSMDROID_MAP_FILE_SOURCE_DIRECTORY;

	public MapTileProviderAssets(final Context pContext) {
		this(pContext, TileSourceFactory.DEFAULT_TILE_SOURCE);
	}

	public MapTileProviderAssets(final Context pContext,
			final ITileSource pTileSource) {
		this(pContext, new SimpleRegisterReceiver(pContext),
				new NetworkAvailabliltyCheck(pContext), pTileSource);

	}

	public MapTileProviderAssets(final Context pContext,
			final IRegisterReceiver pRegisterReceiver,
			final INetworkAvailablityCheck aNetworkAvailablityCheck,
			final ITileSource pTileSource) {
		super(pTileSource, pRegisterReceiver);

		final TileWriter tileWriter = new TileWriter();

		// copy assets delivered in apk into osmdroid map source dir
		// load zip archive first, then cache, then online
		final List<String> zipArchivesRelativePathInAssets = listArchives(
				pContext.getAssets(), ASSETS_MAP_DIRECTORY);
		for (final String zipFileRelativePathInAssets : zipArchivesRelativePathInAssets) {
			final String copiedFilePath = copyAssetFile(pContext.getAssets(),
					zipFileRelativePathInAssets,
					OSMDROID_MAP_FILE_SOURCE_DIRECTORY);
			Log.d(LOG_TAG, String.format(
					"Archive zip file copied into map source directory %s",
					copiedFilePath));
		}
		final Set<String> setZipFileArchivesPath = new HashSet<String>();
//		FileTools.listFiles(setZipFileArchivesPath, new File(
//				OSMDROID_MAP_FILE_SOURCE_DIRECTORY_PATH), ".zip", true);
		final Set<IArchiveFile> setZipFileArchives = new HashSet<IArchiveFile>();
		for (final String zipFileArchivesPath : setZipFileArchivesPath) {
			final File zipfile = new File(zipFileArchivesPath);
			final IArchiveFile archiveFile = ArchiveFileFactory
					.getArchiveFile(zipfile);
			if (archiveFile != null) {
				setZipFileArchives.add(archiveFile);
			}
			setZipFileArchives.add(archiveFile);
			Log.d(LOG_TAG, String.format(
					"Archive zip file %s added to map source ",
					zipFileArchivesPath));
		}

		final MapTileFileArchiveProvider archiveProvider;
		Log.d(LOG_TAG, String.format(
				"%s archive zip files will be used as source",
				setZipFileArchives.size()));
		if (setZipFileArchives.size() > 0) {
			final IArchiveFile[] pArchives = setZipFileArchives
					.toArray(new IArchiveFile[setZipFileArchives.size()]);
			archiveProvider = new MapTileFileArchiveProvider(pRegisterReceiver,
					pTileSource, pArchives);
		} else {
			archiveProvider = new MapTileFileArchiveProvider(pRegisterReceiver,
					pTileSource);
		}
		mTileProviderList.add(archiveProvider);

		// cache
		final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
				pRegisterReceiver, pTileSource);
		mTileProviderList.add(fileSystemProvider);

		// online tiles
		final MapTileDownloader downloaderProvider = new MapTileDownloader(
				pTileSource, tileWriter, aNetworkAvailablityCheck);
		mTileProviderList.add(downloaderProvider);
	}

	public static List<String> listArchives(final AssetManager assetManager,
			final String subDirectory) {
		final List<String> listArchives = new ArrayList<String>();
		try {
			final String[] lstFiles = assetManager.list(subDirectory);
			if (lstFiles != null && lstFiles.length > 0) {
				for (final String file : lstFiles) {
					if (isZip(file)) {
						listArchives.add(subDirectory + "/" + file);
					}
					// filter files (xxxxx.xxx format) and parse only
					// directories, with out this all files are parsed and
					// the process is VERY slow
					// WARNNING: we could have directories with dot for
					// versioning
					else if (isDirectory(file)) {// (file.lastIndexOf(".") !=
													// (file.length() - 4)) {
						listArchives(assetManager, subDirectory + "/" + file);
					}
				}
			}
		} catch (final IOException e) {
			Log.w(LOG_TAG, String.format(
					"List error: can't list %s, exception %s", subDirectory,
					Log.getStackTraceString(e)));
		} catch (final Exception e) {
			Log.w(LOG_TAG, String.format(
					"List error: can't list %s, exception %s", subDirectory,
					Log.getStackTraceString(e)));
		}
		return listArchives;
	}

	private static boolean isZip(final String file) {
		return file.endsWith(".zip");
	}

	private static boolean isDirectory(final String file) {
		return file.lastIndexOf(".") != (file.length() - 4);
	}

	private static String copyAssetFile(final AssetManager assetManager,
			final String assetRelativePath,
			final String destinationDirectoryOnSdcard) {
		InputStream in = null;
		OutputStream out = null;
		final String newfilePath = SDCARD_PATH + "/"
				+ destinationDirectoryOnSdcard + "/" + assetRelativePath;
		final File newFile = new File(newfilePath);
		// copy file only if it doesn't exist yet
		if (!newFile.exists()) {
			Log.d(LOG_TAG, String.format(
					"Copy %s map archive in assets into %s", assetRelativePath,
					newfilePath));
			try {
				final File directory = newFile.getParentFile();
				if (!directory.exists()) {
					if (directory.mkdirs()) {
						// Log.d(LOG_TAG, "Directory created: " +
						// directory.getAbsolutePath());
					}
				}
				in = assetManager.open(assetRelativePath);
				out = new FileOutputStream(newfilePath);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (final Exception e) {
				Log.e(LOG_TAG,
						"Exception during copyAssetFile: "
								+ Log.getStackTraceString(e));
			}
		}
		return newfilePath;
	}

	private static void copyFile(final InputStream in, final OutputStream out)
			throws IOException {
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}