// The MIT License (MIT)
//
// Copyright (c) 2015, 2018 Arian Fornaris
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
package phasereditor.canvas.ui.shapes;

import java.util.List;
import java.util.stream.Collectors;

import phasereditor.assetpack.core.AssetPackCore;
import phasereditor.assetpack.core.IAssetFrameModel;
import phasereditor.assetpack.core.ImageAssetModel;
import phasereditor.assetpack.core.ImageAssetModel.Frame;
import phasereditor.assetpack.core.TilemapAssetModel;
import phasereditor.canvas.core.TilemapSpriteModel;
import phasereditor.canvas.ui.editors.ObjectCanvas;
import phasereditor.canvas.ui.editors.grid.PGridBooleanProperty;
import phasereditor.canvas.ui.editors.grid.PGridFrameProperty;
import phasereditor.canvas.ui.editors.grid.PGridModel;
import phasereditor.canvas.ui.editors.grid.PGridNumberProperty;
import phasereditor.canvas.ui.editors.grid.PGridSection;
import phasereditor.canvas.ui.editors.grid.PGridTilemapIndexesProperty;

/**
 * @author arian
 */
public class TilemapSpriteControl extends BaseSpriteControl<TilemapSpriteModel> {

	private PGridTilemapIndexesProperty _collisionIndexes_prop;
	private PGridFrameProperty _tilesetImage_prop;

	public TilemapSpriteControl(ObjectCanvas canvas, TilemapSpriteModel model) {
		super(canvas, model);
	}

	@Override
	public double getTextureWidth() {
		TilemapAssetModel asset = getModel().getAssetKey();
		if (asset.isCSVFormat()) {
			int[][] map = asset.getCsvData();
			if (map.length > 0) {
				return map[0].length * getModel().getTileWidth();
			}
		}
		return 100;
	}

	@Override
	public double getTextureHeight() {
		TilemapAssetModel asset = getModel().getAssetKey();
		if (asset.isCSVFormat()) {
			int[][] map = asset.getCsvData();
			if (map.length > 0) {
				return map.length * getModel().getTileHeight();
			}
		}
		return 100;
	}

	@Override
	protected TilemapSpriteNode createNode() {
		return new TilemapSpriteNode(this);
	}

	@Override
	public TilemapSpriteNode getNode() {
		return (TilemapSpriteNode) super.getNode();
	}

	public PGridTilemapIndexesProperty getCollisionIndexesProperty() {
		return _collisionIndexes_prop;
	}

	public PGridFrameProperty getTilesetImageProperty() {
		return _tilesetImage_prop;
	}

	@Override
	public void updateFromModel() {
		super.updateFromModel();

		getNode().updateContent();
	}

	@SuppressWarnings("boxing")
	@Override
	protected void initPGridModel(PGridModel propModel) {
		super.initPGridModel(propModel);

		List<PGridSection> sections = propModel.getSections();
		sections.subList(1, sections.size()).stream().forEach(s -> {
			s.setName("TilemapLayer " + s.getName());
		});

		{
			// tilemap

			PGridNumberProperty tileWidth_prop = new PGridNumberProperty(getId(), "tileWidth",
					help("Phaser.Tilemap", "tileWidth"), true) {

				@Override
				public void setValue(Double value, boolean notify) {
					getModel().setTileWidth(value.intValue());
					if (notify) {
						updateFromPropertyChange();
						getCanvas().getSelectionBehavior().updateSelectedNodes_async();
					}
				}

				@Override
				public Double getValue() {
					return Double.valueOf(getModel().getTileWidth());
				}

				@Override
				public boolean isModified() {
					return getModel().getTileWidth() != 32;
				}
			};

			PGridNumberProperty tileHeight_prop = new PGridNumberProperty(getId(), "tileHeight",
					help("Phaser.Tilemap", "tileWidth"), true) {

				@Override
				public void setValue(Double value, boolean notify) {
					getModel().setTileHeight(value.intValue());
					if (notify) {
						updateFromPropertyChange();
						getCanvas().getSelectionBehavior().updateSelectedNodes_async();
					}
				}

				@Override
				public Double getValue() {
					return Double.valueOf(getModel().getTileHeight());
				}

				@Override
				public boolean isModified() {
					return getModel().getTileHeight() != 32;
				}
			};

			_tilesetImage_prop = new PGridFrameProperty(getId(), "tilesetImage",
					"The single tileset image of the CSV map.") {

				@Override
				public void setValue(IAssetFrameModel value, boolean notify) {
					getModel().setTilesetImage(((ImageAssetModel.Frame) value).getAsset());
					if (notify) {
						updateFromPropertyChange();
					}
				}

				@Override
				public boolean isModified() {
					return getModel().getTilesetImage() != null;
				}

				@Override
				public IAssetFrameModel getValue() {
					ImageAssetModel asset = getModel().getTilesetImage();
					return asset == null ? null : asset.getFrame();
				}

				@Override
				public List<?> getFrames() {
					List<Frame> list = AssetPackCore.getAssetPackModels(getModel().getWorld().getProject()).stream()
							.flatMap(pack -> pack.getAssets().stream())
							.filter(asset -> asset instanceof ImageAssetModel)
							.map(image -> ((ImageAssetModel) image).getFrame()).collect(Collectors.toList());
					return list;
				}
			};

			_collisionIndexes_prop = new PGridTilemapIndexesProperty(getId(), "collision.indexes",
					help("Phaser.Tilemap.setCollision", "indexes")) {

				@Override
				public void setValue(List<Integer> value, boolean notify) {
					getModel().setCollisionIndexes(value);
					if (notify) {
						updateFromPropertyChange();
					}
				}

				@Override
				public boolean isModified() {
					return !getModel().getCollisionIndexes().isEmpty();
				}

				@Override
				public List<Integer> getValue() {
					return getModel().getCollisionIndexes();
				}

				@Override
				public TilemapSpriteModel getSpriteModel() {
					return getModel();
				}
			};

			PGridSection section = new PGridSection("Tilemap");

			section.add(tileWidth_prop);
			section.add(tileHeight_prop);
			section.add(_tilesetImage_prop);
			section.add(_collisionIndexes_prop);

			sections.add(section);
		}

		{
			// tilemapLayer

			PGridSection section = new PGridSection("TilemapLayer");

			PGridBooleanProperty resizeWorld_prop = new PGridBooleanProperty(getId(), "resizeWorld",
					help("Phaser.TilemapLayer.resizeWorld")) {

				@Override
				public void setValue(Boolean value, boolean notify) {
					getModel().setResizeWorld(value);
					if (notify) {
						updateFromPropertyChange();
					}
				}

				@Override
				public Boolean getValue() {
					return getModel().isResizeWorld();
				}

				@Override
				public boolean isModified() {
					return !getModel().isResizeWorld();
				}
			};
			section.add(resizeWorld_prop);
		}

	}

}
