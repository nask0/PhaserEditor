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
package phasereditor.animation.ui.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.json.JSONObject;

import phasereditor.animation.ui.editor.properties.AnimationFrameModel_in_Editor_PGridModel;
import phasereditor.assetpack.core.animations.AnimationFrameModel;
import phasereditor.ui.properties.PGridModel;

/**
 * @author arian
 *
 */
public class AnimationFrameModel_in_Editor extends AnimationFrameModel implements IAdaptable {


	public AnimationFrameModel_in_Editor(AnimationModel_in_Editor anim) {
		super(anim);
	}

	public AnimationFrameModel_in_Editor(AnimationModel_in_Editor anim, JSONObject jsonData) {
		super(anim, jsonData);
	}
	
	@Override
	public AnimationModel_in_Editor getAnimation() {
		return (AnimationModel_in_Editor) super.getAnimation();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == PGridModel.class) {
			return new AnimationFrameModel_in_Editor_PGridModel(getAnimation(), this);
		}
		return null;
	}

}
