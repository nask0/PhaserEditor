// The MIT License (MIT)
//
// Copyright (c) 2015, 2016 Arian Fornaris
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
package phasereditor.ui.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import phasereditor.ui.ColorButtonSupport;
import phasereditor.ui.PhaserEditorUI;

/**
 * @author arian
 *
 */
public class PGridValueLabelProvider extends PGridLabelProvider {
	public PGridValueLabelProvider(PGrid grid) {
		super(grid);
	}

	@SuppressWarnings("rawtypes")
	protected Object getPropertyValue(PGridProperty prop) {
		PGrid grid = getGrid();
		PGridModel model = grid.getModel();
		return model.getPropertyValue(prop);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public final String getText(Object element) {
		if (element instanceof PGridSection) {
			return "(properties)";
		}

		if (element instanceof PGridProperty) {
			var prop = (PGridProperty) element;

			var model = getGrid().getModel();
			if (model instanceof MultiPGridModel) {

				var values = ((MultiPGridModel) model).getPropertyValues(prop);

				if (values.size() != 1) {
					return "";
				}
			}

			return getPropertyValueLabel(prop);
		}

		return super.getText(element);
	}

	@SuppressWarnings("rawtypes")
	protected String getPropertyValueLabel(PGridProperty element) {
		if (element instanceof PGridColorProperty) {
			PGridColorProperty prop = (PGridColorProperty) element;
			RGB rgb = (RGB) getPropertyValue(prop);

			if (rgb == null) {
				return "";
			}

			if (prop.getDefaultRGB() != null && prop.getDefaultRGB().equals(rgb)) {
				return "(default)";
			}

			return ColorButtonSupport.getHexString(rgb);
		}

		Object value = getPropertyValue((element));
		return value == null ? "" : value.toString().replace("\n\r", "").replace("\n", "");
	}

	private Map<Object, Image> _images = new HashMap<>();

	@Override
	public Image getImage(Object element) {
		if (element instanceof PGridColorProperty) {
			PGridColorProperty prop = (PGridColorProperty) element;

			if (!prop.isModified()) {
				return null;
			}

			RGB value = (RGB) getPropertyValue(prop);
			if (value == null) {
				return null;
			}
			if (_images.containsKey(value)) {
				return _images.get(value);
			}
			Image image = PhaserEditorUI.makeColorIcon(value);
			_images.put(value, image);
			return image;
		}

		return super.getImage(element);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Image img : _images.values()) {
			img.dispose();
		}
		_images.clear();
	}
}
