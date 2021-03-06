// The MIT License (MIT)
//
// Copyright (c) 2015 Arian Fornaris
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions: The above copyright notice and this permission
// notice shall be included in all copies or substantial portions of the
// Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.
package phasereditor.assetpack.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.json.JSONException;
import org.json.JSONObject;

import phasereditor.atlas.core.AtlasCore;
import phasereditor.project.core.ProjectCore;
import phasereditor.ui.PhaserEditorUI;

public abstract class AssetFactory {

	private static AssetFactory[] _cache;

	static {
		AssetType[] types = AssetType.values();
		_cache = new AssetFactory[types.length];

		cache(new AssetFactory(AssetType.image) {
			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AssetPackModel pack = section.getPack();
				ImageAssetModel asset = new ImageAssetModel(key, section);
				IFile file = pack.pickImageFile();
				if (file != null) {
					asset.setKey(pack.createKey(file));
					asset.setUrl(ProjectCore.getAssetUrl(file));
				}
				return asset;
			}

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new ImageAssetModel(jsonData, section);
			}
		});

		cache(new AssetFactory(AssetType.svg) {
			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AssetPackModel pack = section.getPack();
				var asset = new SvgAssetModel(key, section);
				IFile file = pack.pickSvgFile();
				if (file != null) {
					asset.setKey(pack.createKey(file));
					asset.setUrl(ProjectCore.getAssetUrl(file));
				}
				return asset;
			}

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new SvgAssetModel(jsonData, section);
			}
		});

		cache(new AssetFactory(AssetType.animation) {
			@Override
			public AnimationsAssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AssetPackModel pack = section.getPack();
				var asset = new AnimationsAssetModel(key, section);
				IFile file = pack.pickFile(pack.discoverAnimationsFiles());
				if (file != null) {
					asset.setKey(pack.createKey(file));
					asset.setUrl(ProjectCore.getAssetUrl(file));
				}
				return asset;
			}

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new AnimationsAssetModel(jsonData, section);
			}
		});

		cache(new AssetFactory(AssetType.spritesheet) {
			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new SpritesheetAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AssetPackModel pack = section.getPack();
				SpritesheetAssetModel asset = new SpritesheetAssetModel(key, section);
				IFile file = pack.pickImageFile();
				if (file != null) {
					asset.setKey(pack.createKey(file));
					asset.setUrl(ProjectCore.getAssetUrl(file));
				}
				return asset;
			}
		});

		cache(new AssetFactory(AssetType.audio) {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new AudioAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AudioAssetModel asset = new AudioAssetModel(key, section);
				AssetPackModel pack = section.getPack();
				initAudioFiles(asset, pack);
				return asset;
			}
		});

		cache(new AssetFactory(AssetType.video) {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new VideoAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				VideoAssetModel asset = new VideoAssetModel(key, section);
				AssetPackModel pack = section.getPack();
				initVideoFiles(asset, pack);
				return asset;
			}
		});

		cache(new AssetFactory(AssetType.audioSprite) {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new AudioSpriteAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AudioSpriteAssetModel asset = new AudioSpriteAssetModel(key, section);
				AssetPackModel pack = section.getPack();
				// pick an audiosprite json file
				IFile file = pack.pickAudioSpriteFile();
				if (file == null) {
					// there is not any audiosprite file, then try with an
					// audio file
					initAudioFiles(asset, pack);
				} else {
					// ok, there is an audiosprite json file, use it.
					asset.setKey(pack.createKey(file));
					asset.setJsonURLFile(file);
					asset.setUrlsFromJsonResources();
				}
				return asset;
			}
		});

		cache(new TilemapAssetFactory(AssetType.tilemapCSV));

		cache(new TilemapAssetFactory(AssetType.tilemapTiledJSON));

		cache(new TilemapAssetFactory(AssetType.tilemapImpact));

		cache(new AssetFactory(AssetType.bitmapFont) {
			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new BitmapFontAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				BitmapFontAssetModel asset = new BitmapFontAssetModel(key, section);
				AssetPackModel pack = section.getPack();
				IFile file = pack.pickBitmapFontFile();
				if (file != null) {
					asset.setKey(pack.createKey(file));
					asset.setFontDataURL(ProjectCore.getAssetUrl(file));

					String name = PhaserEditorUI.getNameFromFilename(file.getName());
					IFile imgFile = file.getParent().getFile(new Path(name + ".png"));
					if (imgFile.exists()) {
						asset.setTextureURL(asset.getUrlFromFile(imgFile));
					}
				}
				pack.pickFile(null);

				return asset;
			}
		});

		cache(new AssetFactory(AssetType.physics) {
			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new PhysicsAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				// TODO: discover physics files
				return new PhysicsAssetModel(key, section);
			}
		});

		cache(new AtlasAssetFactory(AssetType.atlas));

		cache(new AtlasAssetFactory(AssetType.atlasXML));

		cache(new AtlasAssetFactory(AssetType.unityAtlas));

		cache(new MultiAtlasAssetFactory());

		cache(new TextAssetFactory());

		cache(new TextAssetFactory(AssetType.json, "json") {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new JsonAssetModel(jsonData, section);
			}

			@Override
			protected SimpleFileAssetModel makeAsset(String key, AssetSectionModel section) {
				return new JsonAssetModel(key, section);
			}
		});

		cache(new TextAssetFactory(AssetType.xml, "xml") {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new XmlAssetModel(jsonData, section);
			}

			@Override
			protected SimpleFileAssetModel makeAsset(String key, AssetSectionModel section) {
				return new XmlAssetModel(key, section);
			}

		});

		cache(new ShaderAssetFactory());

		cache(new AssetFactory(AssetType.binary) {
			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new BinaryAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				return new BinaryAssetModel(key, section);
			}
		});

		cache(new TextAssetFactory(AssetType.script, "js") {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new ScriptAssetModel(jsonData, section);
			}

			@Override
			protected SimpleFileAssetModel makeAsset(String key, AssetSectionModel section) {
				return new ScriptAssetModel(key, section);
			}
		});

		cache(new AssetFactory(AssetType.plugin) {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new PluginAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AssetPackModel pack = section.getPack();
				var asset = new PluginAssetModel(key, section);
				List<IFile> files = pack.discoverTextFiles(new String[] { "js" });
				IFile file = pack.pickFile(files);
				if (file != null) {
					asset.setKey(pack.createKey(file));
					asset.setUrl(ProjectCore.getAssetUrl(file));
				}
				return asset;
			}
		});

		cache(new AssetFactory(AssetType.scenePlugin) {

			@Override
			public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
				return new ScenePluginAssetModel(jsonData, section);
			}

			@Override
			public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
				AssetPackModel pack = section.getPack();
				var asset = new ScenePluginAssetModel(key, section);
				List<IFile> files = pack.discoverTextFiles(new String[] { "js" });
				IFile file = pack.pickFile(files);
				if (file != null) {
					String newKey = pack.createKey(file);
					asset.setKey(newKey);
					asset.setUrl(ProjectCore.getAssetUrl(file));
					asset.setSystemKey("plugin" + newKey.substring(0, 1).toUpperCase() + newKey.substring(1));
					asset.setSceneKey(newKey);
				}
				return asset;
			}
		});

		cache(new HtmlAssetFactory());
	}

	private static void cache(AssetFactory factory) {
		_cache[factory.getType().ordinal()] = factory;
	}

	public static AssetFactory[] getFactories() {
		return _cache;
	}

	public static AssetFactory getFactory(AssetType type) {
		return _cache[type.ordinal()];
	}

	static void initAudioFiles(AudioAssetModel asset, AssetPackModel pack) throws CoreException {
		List<IFile> files = pack.pickAudioFiles();
		if (!files.isEmpty()) {
			asset.setKey(pack.createKey(files.get(0)));
			List<String> urls = asset.getUrlsFromFiles(files);
			asset.setUrls(urls);
		}
	}

	static void initVideoFiles(VideoAssetModel asset, AssetPackModel pack) throws CoreException {
		List<IFile> files = pack.pickVideoFiles();
		if (!files.isEmpty()) {
			asset.setKey(pack.createKey(files.get(0)));
			List<String> urls = asset.getUrlsFromFiles(files);
			asset.setUrls(urls);
		}
	}

	private AssetType _type;

	public AssetFactory(AssetType type) {
		super();
		_type = type;
	}

	public AssetType getType() {
		return _type;
	}

	public String getLabel() {
		return _type.name();
	}

	public String getHelp() {
		try {
			return AssetModel.getHelp(_type);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Create a new asset.
	 * 
	 * @param model
	 *            The pack model.
	 * @param key
	 *            The key of the new asset.
	 * @return The new asset.
	 * @throws Exception
	 */
	public abstract AssetModel createAsset(String key, AssetSectionModel section) throws Exception;

	public abstract AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception;
}

class MultiAtlasAssetFactory extends AssetFactory {

	public MultiAtlasAssetFactory() {
		super(AssetType.multiatlas);
	}

	@Override
	public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
		AssetPackModel pack = section.getPack();
		var asset = new MultiAtlasAssetModel(key, section);

		IFile file = pack.pickFile(pack.discoverAtlasFiles(getType()));

		if (file != null) {
			asset.setKey(pack.createKey(file));
			asset.setUrl(ProjectCore.getAssetUrl(file));
			asset.setPath(ProjectCore.getAssetUrl(file.getProject(), file.getParent().getFullPath()));
			asset.build(new ArrayList<>());
		}

		return asset;
	}

	@Override
	public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
		return new MultiAtlasAssetModel(jsonData, section);
	}
}

class TilemapAssetFactory extends AssetFactory {

	public TilemapAssetFactory(AssetType type) {
		super(type);
	}

	@Override
	public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
		return new TilemapAssetModel(jsonData, section);
	}

	@Override
	public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
		AssetPackModel pack = section.getPack();
		TilemapAssetModel asset = new TilemapAssetModel(key, getType(), section);

		IFile file = pack.pickTilemapFile(getType());

		if (file != null) {
			asset.setKey(pack.createKey(file));
			asset.setUrl(ProjectCore.getAssetUrl(file));
		}

		return asset;
	}
}

class AtlasAssetFactory extends AssetFactory {
	AtlasAssetFactory(AssetType type) {
		super(type);
	}

	@Override
	public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
		AtlasAssetModel asset = new AtlasAssetModel(jsonData, section);
		return asset;
	}

	@Override
	public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
		AssetPackModel pack = section.getPack();
		AtlasAssetModel asset = new AtlasAssetModel(getType(), key, section);

		IFile file = pack.pickFile(pack.discoverAtlasFiles(getType()));

		if (file == null) {
			file = pack.pickImageFile();
			if (file != null) {
				asset.setKey(pack.createKey(file));
				asset.setTextureURL(ProjectCore.getAssetUrl(file));
			}
		} else {
			asset.setKey(pack.createKey(file));
			String format = AtlasCore.getAtlasFormat(file);
			if (format != null) {
				asset.setFormat(format);
			}
			asset.setAtlasURL(ProjectCore.getAssetUrl(file));
			String name = PhaserEditorUI.getNameFromFilename(file.getName());
			IFile imgFile = file.getParent().getFile(new Path(name + ".png"));
			if (imgFile.exists()) {
				asset.setTextureURL(asset.getUrlFromFile(imgFile));
			}
		}

		return asset;
	}
}

class HtmlAssetFactory extends AssetFactory {

	protected HtmlAssetFactory() {
		super(AssetType.html);
	}

	@Override
	public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
		return new HtmlAssetModel(jsonData, section);
	}

	@Override
	public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
		AssetPackModel pack = section.getPack();
		HtmlAssetModel asset = new HtmlAssetModel(key, section);
		List<IFile> files = pack.discoverTextFiles(new String[] { "html" });
		IFile file = pack.pickFile(files);
		if (file != null) {
			asset.setKey(pack.createKey(file));
			asset.setUrl(ProjectCore.getAssetUrl(file));
		}
		return asset;
	}

}

class TextAssetFactory extends AssetFactory {

	private String[] _exts;

	protected TextAssetFactory(AssetType type, String... exts) {
		super(type);
		_exts = exts;
	}

	public TextAssetFactory() {
		this(AssetType.text, "txt", "text");
	}

	@Override
	public AssetModel createAsset(JSONObject jsonData, AssetSectionModel section) throws Exception {
		return new SimpleFileAssetModel(jsonData, section);
	}

	@Override
	public AssetModel createAsset(String key, AssetSectionModel section) throws Exception {
		AssetPackModel pack = section.getPack();
		SimpleFileAssetModel asset = makeAsset(key, section);
		List<IFile> files = pack.discoverTextFiles(_exts);
		IFile file = pack.pickFile(files);
		if (file != null) {
			asset.setKey(pack.createKey(file));
			asset.setUrl(ProjectCore.getAssetUrl(file));
		}
		return asset;
	}

	@SuppressWarnings("static-method")
	protected SimpleFileAssetModel makeAsset(String key, AssetSectionModel section) {
		return new SimpleFileAssetModel(key, section);
	}
}

class ShaderAssetFactory extends TextAssetFactory {
	public ShaderAssetFactory() {
		super(AssetType.glsl, "vert", "frag", "tesc", "tese", "geom", "comp");
	}

	@Override
	public AssetModel createAsset(JSONObject jsonDef, AssetSectionModel section) throws Exception {
		return new ShaderAssetModel(jsonDef, section);
	}

	@Override
	protected ShaderAssetModel makeAsset(String key, AssetSectionModel section) {
		return new ShaderAssetModel(key, section);
	}
}
