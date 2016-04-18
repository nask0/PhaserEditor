// The MIT License (MIT)
//
// Copyright (c) 2016 Arian Fornaris
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

import org.json.JSONObject;

import phasereditor.assetpack.core.SpritesheetAssetModel;

/**
 * @author arian
 *
 */
public class SpritesheetShapeModel extends AssetShapeModel<SpritesheetAssetModel> {

	public static final String TYPE_NAME = "spritesheet";

	private int _frameIndex;

	public SpritesheetShapeModel(GroupModel parent, SpritesheetAssetModel asset) {
		super(parent, asset, TYPE_NAME);
		_frameIndex = 0;
	}

	public SpritesheetShapeModel(GroupModel parent, JSONObject obj) {
		super(parent, TYPE_NAME, obj);
	}
	
	public int getFrameIndex() {
		return _frameIndex;
	}

	public void setFrameIndex(int frameIndex) {
		_frameIndex = frameIndex;
	}

	@Override
	protected void writeInfo(JSONObject jsonInfo) {
		super.writeInfo(jsonInfo);
		jsonInfo.put("frameIndex", _frameIndex);
	}

	@Override
	protected void readInfo(JSONObject jsonInfo) {
		super.readInfo(jsonInfo);
		_frameIndex = jsonInfo.getInt("frameIndex");
	}
}