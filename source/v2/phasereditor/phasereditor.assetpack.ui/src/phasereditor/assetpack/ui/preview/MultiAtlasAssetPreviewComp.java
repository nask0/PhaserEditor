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
package phasereditor.assetpack.ui.preview;

import static phasereditor.ui.PhaserEditorUI.swtRun;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import phasereditor.assetpack.core.MultiAtlasAssetModel;
import phasereditor.assetpack.ui.AssetPackUI;
import phasereditor.ui.EditorSharedImages;
import phasereditor.ui.FilteredFrameGrid;
import phasereditor.ui.IEditorSharedImages;
import phasereditor.ui.ImageCanvas_Zoom_FitWindow_Action;

@SuppressWarnings("synthetic-access")
public class MultiAtlasAssetPreviewComp extends Composite {
	static final Object NO_SELECTION = "none";

	private MultiAtlasAssetModel _model;
	private FilteredFrameGrid _filteredGrid;

	private Action _tilesAction;

	private Action _listAction;

	private ImageCanvas_Zoom_FitWindow_Action _zoom_fitWindow_action;

	public MultiAtlasAssetPreviewComp(Composite parent, int style) {
		super(parent, style);

		setLayout(new StackLayout());

		_filteredGrid = new FilteredFrameGrid(this, SWT.NONE, true);

		AssetPackUI.installAssetTooltips(_filteredGrid);

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		moveTop(_filteredGrid);
	}

	private void moveTop(Control control) {
		StackLayout layout = (StackLayout) getLayout();
		layout.topControl = control;
		layout();

		swtRun(this::updateActionsState);

		control.setFocus();
	}

	private void updateActionsState() {
		var canvas = _filteredGrid.getCanvas();
		_zoom_fitWindow_action.setEnabled(!canvas.isListLayout());
		_tilesAction.setChecked(!canvas.isListLayout());
		_listAction.setChecked(canvas.isListLayout());
	}

	public void setModel(MultiAtlasAssetModel model) {
		_model = model;
		_filteredGrid.loadFrameProvider(new MultiAtlasFrameProvider(model));
	}

	public MultiAtlasAssetModel getModel() {
		return _model;
	}

	public void createToolBar(IToolBarManager toolbar) {

		_tilesAction = new Action("Tiles", IAction.AS_CHECK_BOX) {
			{
				setImageDescriptor(EditorSharedImages.getImageDescriptor(IEditorSharedImages.IMG_APPLICATION_TILE));
			}

			@Override
			public void run() {
				moveTop(_filteredGrid);
				_filteredGrid.getCanvas().setListLayout(false);
			}
		};

		_listAction = new Action("List", IAction.AS_CHECK_BOX) {
			{
				setImageDescriptor(EditorSharedImages.getImageDescriptor(IEditorSharedImages.IMG_APPLICATION_LIST));
			}

			@Override
			public void run() {
				moveTop(_filteredGrid);
				var _canvas = _filteredGrid.getCanvas();
				if (_canvas.getFrameSize() < 32) {
					_canvas.setFrameSize(32);
				}
				_canvas.setListLayout(true);
			}
		};

		toolbar.add(_tilesAction);
		toolbar.add(_listAction);

		toolbar.add(new Separator());

		_zoom_fitWindow_action = new ImageCanvas_Zoom_FitWindow_Action(_filteredGrid.getCanvas());
		toolbar.add(_zoom_fitWindow_action);

		updateActionsState();
	}

}
