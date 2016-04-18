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
package phasereditor.canvas.ui.shapes;

import phasereditor.canvas.core.BaseObjectModel;
import phasereditor.canvas.core.GroupModel;
import phasereditor.canvas.ui.editors.ObjectCanvas;

/**
 * @author arian
 *
 */
public class GroupControl extends BaseObjectControl<GroupModel> {

	public GroupControl(ObjectCanvas canvas, GroupModel model) {
		super(canvas, model);
	}

	@Override
	protected final IObjectNode createShapeNode() {
		GroupNode group = createGroupNode();
		for (BaseObjectModel child : getModel().getChildren()) {
			BaseObjectControl<?> control = ShapeFactory.createShapeControl(getCanvas(), child);
			group.getChildren().add(control.getNode());
		}
		return group;
	}

	protected GroupNode createGroupNode() {
		return new GroupNode(this);
	}

	@Override
	public GroupNode getNode() {
		return (GroupNode) super.getNode();
	}
	
	@Override
	public double getWidth() {
		// 0 for now
		return 0;
	}

	@Override
	public double getHeight() {
		// 0 for now
		return 0;
	}

}