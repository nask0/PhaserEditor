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
package phasereditor.canvas.core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import phasereditor.canvas.core.WorldModel.ZOperation;

/**
 * @author arian
 *
 */
public class GroupModel extends BaseObjectModel {

	private static final String TYPE_NAME = "group";
	private List<BaseObjectModel> _children;

	public GroupModel(GroupModel parent, JSONObject data) {
		super(parent, TYPE_NAME, data);
	}

	public GroupModel(GroupModel parent) {
		super(parent, "group");
		_children = new ArrayList<>();
	}
	
	@Override
	public String getLabel() {
		return "[grp] " + getEditorName();
	}

	public Iterable<BaseObjectModel> getChildren() {
		return _children;
	}

	public void addChild(BaseObjectModel model) {
		model.getParent()._children.remove(model);
		_children.add(model);
		getWorld().firePropertyChange(WorldModel.PROP_STRUCTURE);
	}

	public BaseObjectModel findByName(String name) {
		if (getEditorName().equals(name)) {
			return this;
		}

		for (BaseObjectModel model : _children) {
			if (model instanceof GroupModel) {
				BaseObjectModel found = ((GroupModel) model).findByName(name);
				if (found != null) {
					return found;
				}
			} else if (model.getEditorName().equals(name)) {
				return model;
			}
		}
		return null;
	}

	public void sendTo(BaseObjectModel model, ZOperation op) {
		if (op.perform(_children, model)) {
			getWorld().firePropertyChange(WorldModel.PROP_STRUCTURE);
		}
	}

	public void riseToBottom(BaseObjectModel model) {
		_children.remove(model);
		_children.add(0, model);
		getWorld().firePropertyChange(WorldModel.PROP_STRUCTURE);
	}

	public void removeChild(BaseObjectModel model) {
		_children.remove(model);
		getWorld().firePropertyChange(WorldModel.PROP_STRUCTURE);
	}

	@Override
	protected void readInfo(JSONObject jsonInfo) {
		super.readInfo(jsonInfo);

		_children = new ArrayList<>();

		try {
			JSONArray modelList = jsonInfo.getJSONArray("children");
			for (int i = 0; i < modelList.length(); i++) {
				BaseObjectModel model = null;
				JSONObject jsonModel = modelList.getJSONObject(i);
				String type = jsonModel.getString("type");
				switch (type) {
				case ImageSpriteShapeModel.TYPE_NAME:
					model = new ImageSpriteShapeModel(this, jsonModel);
					break;
				case SpritesheetShapeModel.TYPE_NAME:
					model = new SpritesheetShapeModel(this, jsonModel);
					break;
				case AtlasSpriteShapeModel.TYPE_NAME:
					model = new AtlasSpriteShapeModel(this, jsonModel);
					break;
				case GroupModel.TYPE_NAME:
					model = new GroupModel(this, jsonModel);
					break;
				default:
					break;
				}

				if (model != null) {
					_children.add(model);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void writeInfo(JSONObject jsonInfo) {
		super.writeInfo(jsonInfo);

		JSONArray shapesArray = new JSONArray();

		for (BaseObjectModel model : _children) {
			JSONObject jsonShape = new JSONObject();
			shapesArray.put(jsonShape);
			model.write(jsonShape);
		}

		jsonInfo.put("children", shapesArray);
	}

}