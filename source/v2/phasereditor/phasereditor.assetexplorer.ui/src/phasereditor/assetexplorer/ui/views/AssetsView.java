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
package phasereditor.assetexplorer.ui.views;

import static java.lang.System.out;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONObject;

import phasereditor.assetexplorer.ui.views.newactions.NewWizardLancher;
import phasereditor.assetpack.core.AssetGroupModel;
import phasereditor.assetpack.core.AssetPackCore;
import phasereditor.assetpack.core.AssetPackModel;
import phasereditor.assetpack.core.AssetSectionModel;
import phasereditor.assetpack.core.IAssetKey;
import phasereditor.assetpack.core.animations.AnimationsModel;
import phasereditor.assetpack.ui.AssetPackUI;
import phasereditor.atlas.core.AtlasData;
import phasereditor.canvas.core.CanvasFile;
import phasereditor.canvas.core.CanvasType;
import phasereditor.canvas.ui.CanvasUI;
import phasereditor.ui.FilteredTreeCanvas;
import phasereditor.ui.TreeCanvas;

@SuppressWarnings({ "synthetic-access", "boxing" })
public class AssetsView extends ViewPart {
	/**
	 * 
	 */
	private static final String CANVAS_STATE_KEY = "canvasState";
	/**
	 * 
	 */
	private static final String FILTER_TEXT_KEY = "filterText";
	/**
	 * 
	 */
	private static final String EXPANDED_INDEXES_KEY = "expandedIndexes";
	public static final String ID = "phasereditor.assetpack.views.assetExplorer";
	private AssetExplorerContentProvider _contentProvider;
	private TreeCanvas _treeCanvas;
	private IPartListener _partListener;
	private FilteredTreeCanvas _filteredTreeCanvas;
	// private AssetExplorerLabelProvider _treeLabelProvider;
	// private AssetExplorerContentProvider _treeContentProvider;
	// private AssetExplorerListLabelProvider _listLabelProvider;
	// private AssetExplorerListContentProvider _listContentProvider;
	public static String ROOT = "root";
	public static String ANIMATIONS_NODE = "Animations Files";
	public static String ATLAS_NODE = "Texture Packer Files";
	public static String CANVAS_NODE = "Canvas Files";
	public static String PACK_NODE = "Pack Files";
	public static String PROJECTS_NODE = "Other Projects";

	public AssetsView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		_contentProvider = new AssetExplorerContentProvider();

		_filteredTreeCanvas = new FilteredTreeCanvas(parent, SWT.NONE);
		_filteredTreeCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		_treeCanvas = _filteredTreeCanvas.getCanvas();
		_treeCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				var elem = getTreeCanvas().getUtils().getOverObject();
				if (elem != null) {
					handleDoubleClick(elem);
				}
			}
		});

		afterCreateWidgets();
	}

	public TreeCanvas getTreeCanvas() {
		return _treeCanvas;
	}

	private void handleDoubleClick(Object elem) {
		IFile file = null;

		var provider = _contentProvider;

		if (elem instanceof IProject) {
			provider.forceToFocuseOnProject((IProject) elem);
			_lastToken = (IProject) elem;
			_viewer.refresh();
			// _treeCanvasAdapter.expandToLevel(3);
		} else if (elem instanceof IFile) {
			file = (IFile) elem;
		} else if (elem instanceof CanvasFile) {
			file = ((CanvasFile) elem).getFile();
		} else if (elem instanceof AtlasData) {
			file = ((AtlasData) elem).getFile();
		} else if (elem instanceof AnimationsModel) {
			file = ((AnimationsModel) elem).getFile();
		} else if (elem instanceof NewWizardLancher) {
			((NewWizardLancher) elem).openWizard(provider.getProjectInContent());
		}

		if (file != null) {
			try {
				IDE.openEditor(getSite().getPage(), file);
			} catch (PartInitException e) {
				throw new RuntimeException(e); // NOSONAR
			}
		}

		AssetPackUI.openElementInEditor(elem);
	}

	private void afterCreateWidgets() {

		// selection provider

		getViewSite().setSelectionProvider(_treeCanvas.getUtils());

		{

			// menu
			MenuManager manager = new MenuManager();
			Menu menu = manager.createContextMenu(_treeCanvas);
			_treeCanvas.setMenu(menu);

			getViewSite().registerContextMenu(manager, _treeCanvas.getUtils());
		}

		// tooltips

		AssetPackUI.installAssetTooltips(_treeCanvas, _treeCanvas.getUtils());
		CanvasUI.installCanvasTooltips(_treeCanvas, _treeCanvas.getUtils());

		// undo context

		IUndoContext undoContext = WorkspaceUndoUtil.getWorkspaceUndoContext();

		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), new UndoActionHandler(getSite(), undoContext));
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), new RedoActionHandler(getSite(), undoContext));

		// parts listener

		initPartListener();

		// tree canvas

		_viewer = new AssetExplorerTreeCanvasViewer(_treeCanvas, _contentProvider, new AssetExplorerLabelProvider());
		_viewer.setInput(ROOT);
	}

	private void initPartListener() {
		_partListener = new IPartListener() {

			@Override
			public void partOpened(IWorkbenchPart part) {
				if (part instanceof IEditorPart) {
					refreshViewer();
				}
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				if (part instanceof IEditorPart) {
					refreshViewer();
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part instanceof IEditorPart) {
					refreshViewer();
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof IEditorPart) {
					refreshViewer();
				}
			}

			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part instanceof IEditorPart) {
					refreshViewer();
				}
			}
		};
		getViewSite().getPage().addPartListener(_partListener);
	}

	IProject _lastToken = null;
	private AssetExplorerTreeCanvasViewer _viewer;
	private List<Integer> _initialExpandedIndexes;
	private String _initialFilterText;
	private JSONObject _initialTreeState;
	private Map<IProject, List<Integer>> _projectExpansionMap = new HashMap<>();
	private Map<IProject, String> _projectFilterMap = new HashMap<>();

	void refreshViewer() {

		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		}

		_treeCanvas.setRedraw(false);

		try {
			out.println("AssetsView.refreshViewer()");

			IProject project = getActiveProject();

			if (project != _lastToken) {

				if (_lastToken != null) {
					_projectExpansionMap.put(_lastToken, _treeCanvas.getExpandedIndexes());
					_projectFilterMap.put(_lastToken, _filteredTreeCanvas.getFilterText());
				}

				refreshContent(project);

				{
					var indexes = _projectExpansionMap.getOrDefault(project, List.of());
					_treeCanvas.setExpandedIndexes(indexes);

					var filter = _projectFilterMap.get(project);
					_filteredTreeCanvas.setFilterText(filter);
				}

				_lastToken = project;
			}

		} finally {
			_treeCanvas.setRedraw(true);
		}

	}

	public boolean isInitialStateRecovered() {
		return _initialExpandedIndexes == null;
	}

	public void restoreInitialState() {
		if (_initialExpandedIndexes != null) {
			_treeCanvas.setExpandedIndexes(_initialExpandedIndexes);

			if (_initialFilterText != null) {
				_filteredTreeCanvas.setFilterText(_initialFilterText);
			}

			if (_initialTreeState != null) {
				_treeCanvas.restoreState(_initialTreeState);
			}

			_initialExpandedIndexes = null;
		}

		_lastToken = getActiveProject();

		_treeCanvas.redraw();
	}

	public static IProject getActiveProject() {
		IProject activeProjet = null;
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				activeProjet = file.getProject();
			} else {
				activeProjet = input.getAdapter(IProject.class);
			}
		}
		return activeProjet;
	}

	@Override
	public void dispose() {

		getViewSite().getPage().removePartListener(_partListener);

		super.dispose();
	}

	@Override
	public void setFocus() {
		_treeCanvas.setFocus();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IContextProvider.class)) {
			return new IContextProvider() {

				@Override
				public String getSearchExpression(Object target) {
					return null;
				}

				@Override
				public int getContextChangeMask() {
					return NONE;
				}

				@Override
				public IContext getContext(Object target) {
					IContext context = HelpSystem.getContext("phasereditor.help.assetexplorer");
					return context;
				}
			};
		}
		return super.getAdapter(adapter);
	}

	public void refreshContent(IProject project) {

		out.println("Assets.refreshContent(" + (project == null ? "null" : project.getName()) + ")");

		IProject currentProject = _contentProvider.getProjectInContent();

		if (currentProject != null && project != currentProject) {
			out.println("  Skip refresh.");
			return;
		}
		out.println("  Perfom refresh");

		if (_treeCanvas.isDisposed()) {
			return;
		}

		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		}

		var expanded = _treeCanvas.getExpandedObjects();

		try {
			_treeCanvas.setRedraw(false);

			_viewer.refresh();

			List<Object> toExpand = new ArrayList<>();

			for (Object obj : expanded) {
				if (obj instanceof IAssetKey) {
					IAssetKey key = ((IAssetKey) obj).getSharedVersion();
					toExpand.add(key);
				} else if (obj instanceof AssetGroupModel) {
					AssetPackModel oldPack = ((AssetGroupModel) obj).getSection().getPack();
					JSONObject ref = oldPack.getAssetJSONRefrence(obj);
					IFile file = oldPack.getFile();
					AssetPackModel newPack = AssetPackCore.getAssetPackModel(file, false);
					if (newPack != null) {
						Object obj2 = newPack.getElementFromJSONReference(ref);
						if (obj2 != null) {
							toExpand.add(obj2);
						}
					}
				} else if (obj instanceof AssetSectionModel) {
					AssetPackModel oldPack = ((AssetSectionModel) obj).getPack();
					JSONObject ref = oldPack.getAssetJSONRefrence(obj);
					IFile file = oldPack.getFile();
					AssetPackModel newPack = AssetPackCore.getAssetPackModel(file, false);
					if (newPack != null) {
						Object obj2 = newPack.getElementFromJSONReference(ref);
						toExpand.add(obj2);
					}
				} else if (obj instanceof AssetPackModel) {
					AssetPackModel newPack = AssetPackCore.getAssetPackModel(((AssetPackModel) obj).getFile(), false);
					toExpand.add(newPack);
				} else if (obj instanceof CanvasType) {
					toExpand.add(obj);
				} else {
					toExpand.add(obj);
				}
			}
			toExpand.remove(null);
			_treeCanvas.setExpandedObjects(toExpand);

		} finally {
			_treeCanvas.setRedraw(true);
		}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		if (memento != null) {
			{
				var str = memento.getString(EXPANDED_INDEXES_KEY);

				if (str != null) {
					try {
						_initialExpandedIndexes = Arrays.stream(str.split(",")).map(s -> Integer.parseInt(s))
								.collect(toList());
					} catch (Exception e) {
						// do nothing
					}
				}
			}

			{
				_initialFilterText = memento.getString(FILTER_TEXT_KEY);
			}

			{
				var str = memento.getString(CANVAS_STATE_KEY);
				if (str != null) {
					_initialTreeState = new JSONObject(str);
				}
			}
		}

		super.init(site, memento);
	}

	@Override
	public void saveState(IMemento memento) {
		List<Integer> expandedIndexes = _treeCanvas.getExpandedIndexes();

		memento.putString(EXPANDED_INDEXES_KEY, expandedIndexes.stream().map(i -> i.toString()).collect(joining(",")));
		memento.putString(FILTER_TEXT_KEY, _filteredTreeCanvas.getFilterText());
		{
			JSONObject jsonState = new JSONObject();
			_treeCanvas.saveState(jsonState);
			memento.putString(CANVAS_STATE_KEY, jsonState.toString());
		}

		// TODO: save all the mapping information of all the projects.
	}

}