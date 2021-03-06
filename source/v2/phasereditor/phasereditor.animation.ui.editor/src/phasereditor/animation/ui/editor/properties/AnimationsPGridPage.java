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
package phasereditor.animation.ui.editor.properties;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import phasereditor.animation.ui.editor.AnimationsEditor;
import phasereditor.ui.properties.MultiPGridModel;
import phasereditor.ui.properties.PGridModel;
import phasereditor.ui.properties.PGridPage;

/**
 * @author arian
 *
 */
public class AnimationsPGridPage extends PGridPage {

	private AnimationsEditor _editor;

	public AnimationsPGridPage(AnimationsEditor editor) {
		super(true);
		_editor = editor;
	}

	public AnimationsEditor getEditor() {
		return _editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getGrid().setOnChanged(_editor::gridPropertyChanged);
	}

	@Override
	protected PGridModel createModelWithSelection(IStructuredSelection selection) {
		return createModelWithSelection_public(selection);
	}

	public static PGridModel createModelWithSelection_public(IStructuredSelection selection) {
		var elems = selection.toArray();

		if (elems.length > 1) {
			List<PGridModel> models = Arrays.stream(elems)
					.map(e -> createModelWithSelection_public(new StructuredSelection(e))).collect(toList());

			MultiPGridModel multiModel = new MultiPGridModel(models);

			return multiModel;
		}

		var elem = selection.getFirstElement();
		PGridModel model = Adapters.adapt(elem, PGridModel.class);

		return model;
	}

}
