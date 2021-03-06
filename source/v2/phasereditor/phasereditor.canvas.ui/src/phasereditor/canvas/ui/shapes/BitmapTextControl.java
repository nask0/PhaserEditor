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

import java.util.Arrays;
import java.util.List;

import phasereditor.assetpack.core.BitmapFontAssetModel;
import phasereditor.bmpfont.core.BitmapFontModel.Align;
import phasereditor.canvas.core.BitmapTextModel;
import phasereditor.canvas.ui.editors.ObjectCanvas;
import phasereditor.canvas.ui.editors.grid.PGridBitmapTextFontProperty;
import phasereditor.ui.properties.PGridEnumProperty;
import phasereditor.ui.properties.PGridModel;
import phasereditor.ui.properties.PGridNumberProperty;
import phasereditor.ui.properties.PGridSection;
import phasereditor.ui.properties.PGridStringProperty;

/**
 * @author arian
 *
 */
public class BitmapTextControl extends BaseSpriteControl<BitmapTextModel> {

	private PGridStringProperty _text_property;
	private PGridBitmapTextFontProperty _font_property;
	private PGridNumberProperty _size_property;
	private PGridEnumProperty<Align> _align_property;

	public BitmapTextControl(ObjectCanvas canvas, BitmapTextModel model) {
		super(canvas, model);
	}

	@Override
	public double getTextureWidth() {
		return getNode().getBoundsInLocal().getWidth();
	}

	@Override
	public double getTextureHeight() {
		return getNode().getBoundsInLocal().getHeight();
	}

	@Override
	protected IObjectNode createNode() {
		return new BitmapTextNode(this);
	}

	@Override
	public void updateFromModel() {
		super.updateFromModel();

		getNode().updateFromModel();
	}

	@Override
	public BitmapTextNode getNode() {
		return (BitmapTextNode) super.getNode();
	}

	@Override
	protected void initPrefabPGridModel(List<String> validProperties) {
		super.initPrefabPGridModel(validProperties);

		validProperties.addAll(Arrays.asList(

				BitmapTextModel.PROPSET_TEXT,

				BitmapTextModel.PROPSET_SIZE,

				BitmapTextModel.PROPSET_MAX_WIDTH,

				BitmapTextModel.PROPSET_ALIGN

		));
	}

	@Override
	protected void initPGridModel(PGridModel propModel) {
		super.initPGridModel(propModel);

		PGridSection section = new PGridSection("BitmapText");

		_font_property = new PGridBitmapTextFontProperty(getId(), "font", help("Phaser.BitmapText.font")) {

			@Override
			public void setValue(BitmapFontAssetModel value, boolean notify) {
				getModel().setAssetKey(value);
				if (notify) {
					updateFromPropertyChange();
					getCanvas().getSelectionBehavior().updateSelectedNodes_async();
				}
			}

			@Override
			public BitmapFontAssetModel getValue() {
				return getModel().getAssetKey();
			}

			@Override
			public boolean isModified() {
				return true;
			}

			@Override
			public BitmapTextModel getModel() {
				return BitmapTextControl.this.getModel();
			}

		};

		_text_property = new PGridStringProperty(getId(), "text", help("Phaser.BitmapText.text"), "Write the text.") {

			@Override
			public boolean isModified() {
				return getModel().getText().length() > 0;
			}

			@Override
			public void setValue(String value, boolean notify) {
				getModel().setText(value);
				if (notify) {
					updateFromPropertyChange();
					getCanvas().getSelectionBehavior().updateSelectedNodes_async();
				}
			}

			@Override
			public String getValue() {
				return getModel().getText();
			}

			@Override
			public boolean isReadOnly() {
				return getModel().isPrefabReadOnly(BitmapTextModel.PROPSET_TEXT);
			}
		};

		_size_property = new PGridNumberProperty(getId(), "fontSize", help("Phaser.BitmapText.fontSize")) {

			@Override
			public boolean isModified() {
				return getModel().getFontSize() != BitmapTextModel.DEF_FONT_SIZE;
			}

			@Override
			public void setValue(Double value, boolean notify) {
				getModel().setFontSize(value.intValue());
				if (notify) {
					updateFromPropertyChange();
					getCanvas().getSelectionBehavior().updateSelectedNodes_async();
				}
			}

			@Override
			public Double getValue() {
				return Double.valueOf(getModel().getFontSize());
			}

			@Override
			public boolean isReadOnly() {
				return getModel().isPrefabReadOnly(BitmapTextModel.PROPSET_SIZE);
			}
		};

		PGridNumberProperty _maxWidth_property = new PGridNumberProperty(getId(), "maxWidth",
				help("Phaser.BitmapText.maxWidth")) {

			@Override
			public boolean isModified() {
				return getModel().getMaxWidth() != BitmapTextModel.DEF_MAX_WIDTH;
			}

			@Override
			public void setValue(Double value, boolean notify) {
				getModel().setMaxWidth(value.intValue());
				if (notify) {
					updateFromPropertyChange();
					getCanvas().getSelectionBehavior().updateSelectedNodes_async();
				}
			}

			@Override
			public Double getValue() {
				return Double.valueOf(getModel().getMaxWidth());
			}

			@Override
			public boolean isReadOnly() {
				return getModel().isPrefabReadOnly(BitmapTextModel.PROPSET_MAX_WIDTH);
			}
		};

		_align_property = new PGridEnumProperty<>(getId(), "align", help("Phaser.BitmapText.align"),
				Align.values()) {

			@Override
			public boolean isModified() {
				return getModel().getAlign() != BitmapTextModel.DEF_ALIGN;
			}

			@Override
			public void setValue(Align value, boolean notify) {
				getModel().setAlign(value);
				if (notify) {
					updateFromPropertyChange();
					getCanvas().getSelectionBehavior().updateSelectedNodes_async();
				}
			}

			@Override
			public Align getValue() {
				return getModel().getAlign();
			}

			@Override
			public boolean isReadOnly() {
				return getModel().isPrefabReadOnly(BitmapTextModel.PROPSET_ALIGN);
			}
		};

		section.add(_font_property);
		section.add(_text_property);
		section.add(_size_property);
		section.add(_maxWidth_property);
		section.add(_align_property);

		propModel.getSections().add(section);

		// will never be supported by BitmapText, this should be moved to asset
		// sprites.
		getAnimationsProperty().getSection().remove(getAnimationsProperty());
	}

	public PGridStringProperty getTextProperty() {
		return _text_property;
	}

	public PGridEnumProperty<Align> getAlignProperty() {
		return _align_property;
	}

	public PGridBitmapTextFontProperty getFontProperty() {
		return _font_property;
	}

	public PGridNumberProperty getSizeProperty() {
		return _size_property;
	}
}
